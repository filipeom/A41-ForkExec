package com.forkexec.pts.ws.frontend;

import java.util.regex.Pattern;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.pts.ws.Value;
import com.forkexec.pts.ws.Tag;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/**
 * FrontEnd port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class PointsFrontEnd {

  /** UDDI server URL */
  private String uddiURL = null;

  /** Number of backup servers */
  private int N = 0;

  /** Constant, service prefix */
  private final String POINTS = "A41_Points";

  /** Constant, acknowledge */
  private final String SUCCESS = "ACK";

  /** output option **/
  private boolean verbose = false;

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /** constructor with provided UDDI location and name */
  public PointsFrontEnd(String uddiURL, int N) throws PointsFrontEndException {
    this.uddiURL = uddiURL;
    this.N = N;
  }

  /** UDDI lookup */
  private String uddiLookup(String wsName) throws PointsFrontEndException {
    String wsURL;
    try {
      if (verbose)
        System.out.printf("Contacting UDDI at %s%n", uddiURL);
      UDDINaming uddiNaming = new UDDINaming(uddiURL);

      if (verbose)
        System.out.printf("Looking for '%s'%n", wsName);
      wsURL = uddiNaming.lookup(wsName);

    } catch (Exception e) {
      String msg = String.format("FrontEnd failed lookup on UDDI at %s!", uddiURL);
      throw new PointsFrontEndException(msg, e);
    }

    if (wsURL == null) {
      String msg = String.format("Service with name %s not found on UDDI at %s", wsName, uddiURL);
      throw new PointsFrontEndException(msg);
    }

    return wsURL;
  }

  /** Email address validation. */
	private void checkValidEmail(final String emailAddress) throws InvalidEmailFault_Exception {
		final String message;
		if (emailAddress == null) {
			message = "Null email is not valid";
		} else if (!Pattern.matches("(\\w\\.?)*\\w+@\\w+(\\.?\\w)*", emailAddress)) {
			message = String.format("Email: %s is not valid", emailAddress);
		} else {
			return;
		}
		throw new InvalidEmailFault_Exception(message, null);
	}
  
  private Value getMaxValue(String userEmail) throws InvalidEmailFault_Exception {
    PointsClient client = null;
    try {
      checkValidEmail(userEmail);

      client = new PointsClient(uddiLookup(POINTS + Integer.toString(1)));
      Value maxValue = client.read(userEmail);
      for (int i = 2; i <= N; i++) {
        client = new PointsClient(uddiLookup(POINTS + Integer.toString(i)));
        Value value = client.read(userEmail);

        if (value.getTag().getSeq() > maxValue.getTag().getSeq())
          maxValue = value;
      }
      return maxValue;
    } catch (PointsClientException | PointsFrontEndException e) {
      throw new RuntimeException("Failed to lookup Points Service.");
    } catch (InvalidEmailFault_Exception e) {
      throw new InvalidEmailFault_Exception(e.getMessage(), e.getFaultInfo());
    }
  }

  private void setMaxValue(String userEmail, Value value) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    try {
      checkValidEmail(userEmail);

      for (int i = 1; i <= N; i++) {
        PointsClient client = new PointsClient(uddiLookup(POINTS + Integer.toString(i)));
        client.write(userEmail, value.getVal(), value.getTag());
      }
    } catch (PointsClientException | PointsFrontEndException e) {
      throw new RuntimeException("Failed to lookup Points Service.");
    } catch (InvalidPointsFault_Exception e) {
      throw new InvalidPointsFault_Exception( e.getMessage(), e.getFaultInfo());
    } catch (InvalidEmailFault_Exception e) {
      throw new InvalidEmailFault_Exception(e.getMessage(), e.getFaultInfo());
    }
  }

  // remote invocation methods ----------------------------------------------

  public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
    Value maxValue = getMaxValue(userEmail);

    if (maxValue.getTag().getSeq() > 0)
      throw new EmailAlreadyExistsFault_Exception("Email already in use.");

    try {
      setMaxValue(userEmail, newValue(maxValue.getVal(), maxValue.getTag()));
    } catch (InvalidPointsFault_Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public int pointsBalance(String userEmail) throws InvalidEmailFault_Exception {
    return getMaxValue(userEmail).getVal();
  }

  public int addPoints(String userEmail, int pointsToAdd)
      throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {

      if (pointsToAdd < 0)
        throwInvalidPointsFault("Points cannot be negative!");

      Value maxValue = getMaxValue(userEmail);

      int points = maxValue.getVal() + pointsToAdd;

      setMaxValue(userEmail, newValue(points, maxValue.getTag()));

      return points;
  }

  public int spendPoints(String userEmail, int pointsToSpend)
      throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {

      if (pointsToSpend < 0)
        throwInvalidPointsFault("Points cannot be negative!");

      Value maxValue = getMaxValue(userEmail);

      int points = maxValue.getVal() - pointsToSpend;
      if (points < 0) {
        throw new NotEnoughBalanceFault_Exception("Not Enough Points to spend");
      }

      setMaxValue(userEmail, newValue(points, maxValue.getTag()));

      return points;
  }

  public Value newValue(int val, Tag t) {
    Tag tag = new Tag();
    tag.setSeq(t.getSeq() + 1);
    tag.setCid(t.getCid());

    Value value = new Value();
    value.setVal(val);
    value.setTag(tag);
    return value;
  }

  // control operations -----------------------------------------------------

  public String ctrlPing(String inputMessage) {
    PointsClient cli = null;
    StringBuilder builder = new StringBuilder();

    try {
      for(int i = 0; i < N; i++) {
        cli = new PointsClient( uddiLookup(POINTS + Integer.toString(i+1) ) );
        if ( cli.ctrlPing(inputMessage) != null )
          builder.append( cli.ctrlPing(inputMessage) );
      }
    } catch (PointsClientException | PointsFrontEndException e) {
      throw new RuntimeException("Failed to lookup Points Service.");
    }
    return builder.toString();
  }

  public void ctrlClear() {
    PointsClient cli = null;
    try {
      for(int i = 0; i < N; i++) {
        cli = new PointsClient( uddiLookup(POINTS + Integer.toString(i+1) ) );
        cli.ctrlClear();
      }
    } catch (PointsClientException | PointsFrontEndException e) {
      throw new RuntimeException("Failed to lookup Points Service.");
    }
  }

  public void ctrlInit(int startPoints) throws BadInitFault_Exception {
    PointsClient cli = null;

    try {
      for (int i = 0; i < N; i++) {
        cli = new PointsClient(uddiLookup(POINTS + Integer.toString(i + 1)));
        cli.ctrlInit(startPoints);
      }
    } catch (PointsClientException | PointsFrontEndException e) {
      throw new RuntimeException("Failed to lookup Points Service.");
    }
  }


  /** Helper to throw a new InvalidPointsFault exception. */
  private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
    InvalidPointsFault faultInfo = new InvalidPointsFault();
    faultInfo.setMessage(message);
    throw new InvalidPointsFault_Exception(message, faultInfo);
  }
}
