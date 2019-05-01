package com.forkexec.pts.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;
import javax.xml.ws.BindingProvider;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault;
import com.forkexec.pts.ws.PointsPortType;
import com.forkexec.pts.ws.PointsService;
import com.forkexec.pts.ws.Tag;
import com.forkexec.pts.ws.Value;
import com.forkexec.pts.ws.ReadResponse;
import com.forkexec.pts.ws.WriteResponse;

import com.forkexec.pts.ws.cli.exception.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class PointsClient {

  /** WS port (port type is the interface, port is the implementation) */
  PointsPortType port = null;

  ArrayList<PointsPortType> ports = new ArrayList<>();

  private int Q = 0;

  /** WS end point address */
  private Collection<String> wsURLs;

  public Collection<String> getWsURLs() {
    return wsURLs;
  }

  /** output option **/
  private boolean verbose = false;

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /** constructor with provided web service URL */
  public PointsClient(Collection<String> wsURLs) {
    this.wsURLs = wsURLs;
    this.Q = wsURLs.size() / 2 + 1;
    System.out.println("URL SIZE: " + wsURLs.size() + " QOUROM: " + this.Q);
    createStubs();
  }

  /** Stub creation and configuration */
  private void createStubs() {
    for (String wsURL : getWsURLs()) {
      if (verbose)
        System.out.println("Creating stub ...");
      PointsService service = new PointsService();
      PointsPortType port = service.getPointsPort();

      if (wsURL != null) {
        if (verbose)
          System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
        ports.add(port);
      }
    }
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
    ArrayList<Response<ReadResponse>> responses = new ArrayList<>(ports.size());
    ArrayList<Value> quorum = new ArrayList<>(this.Q);
    int n = 0;

    try {
      checkValidEmail(userEmail);

      for (int i = 0; i < ports.size(); i++) {
        port = ports.get(i);
        responses.add(readAsync(userEmail));
      }

      while (n < this.Q) {
        for (int i = 0; i < responses.size(); i++) {
          if (responses.get(i).isDone()) {
            quorum.add(responses.get(i).get().getReturn());
            responses.remove(i);
            n++;
            break;
          }
        }
      }

      Value maxValue = quorum.get(0);
      for (int i = 1; i < this.Q; i++) {
        Value value = quorum.get(i);

        if (value.getTag().getSeq() > maxValue.getTag().getSeq())
          maxValue = value;
      }

      return maxValue;

    } catch (InvalidEmailFault_Exception e) {
      throw new InvalidEmailFault_Exception(e.getMessage(), e.getFaultInfo());
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException("Exception: " + e.getCause());
    }
  }

  private void setMaxValue(String userEmail, Value value) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    try {
      checkValidEmail(userEmail);

      for (int i = 0; i < ports.size(); i++) {
        port = ports.get(i);
        write(userEmail, value.getVal(), value.getTag());
      }
    } catch (InvalidPointsFault_Exception e) {
      throw new InvalidPointsFault_Exception( e.getMessage(), e.getFaultInfo());
    } catch (InvalidEmailFault_Exception e) {
      throw new InvalidEmailFault_Exception(e.getMessage(), e.getFaultInfo());
    }
  }

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

      if (pointsToAdd <= 0)
        throwInvalidPointsFault("Points cannot be negative!");

      Value maxValue = getMaxValue(userEmail);

      int points = maxValue.getVal() + pointsToAdd;

      setMaxValue(userEmail, newValue(points, maxValue.getTag()));

      return points;
  }

  public int spendPoints(String userEmail, int pointsToSpend)
      throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {

      if (pointsToSpend <= 0)
        throwInvalidPointsFault("Points cannot be negative!");

      Value maxValue = getMaxValue(userEmail);

      int points = maxValue.getVal() - pointsToSpend;
      if (points < 0) {
        throw new NotEnoughBalanceFault_Exception("Not Enough Points to spend");
      }

      setMaxValue(userEmail, newValue(points, maxValue.getTag()));

      return points;
  }

  private Value newValue(int val, Tag t) {
    Tag tag = new Tag();
    tag.setSeq(t.getSeq() + 1);
    tag.setCid(t.getCid());

    Value value = new Value();
    value.setVal(val);
    value.setTag(tag);
    return value;
  }

  // remote invocation methods ----------------------------------------------

  public Value read(String userEmail) throws InvalidEmailFault_Exception {
    return port.read(userEmail);
  }

  public Response<ReadResponse> readAsync(String userEmail) {
    return port.readAsync(userEmail);
  }

  public String write(String userEmail, int points, Tag t) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    return port.write(userEmail, points, t);
  }

  public Response<WriteResponse> writeAsync(String userEmail, int points, Tag t) {
    return port.writeAsync(userEmail, points, t);
  }

  // control operations -----------------------------------------------------
  public String ctrlPing(String inputMessage) {
    StringBuilder builder = new StringBuilder();

    for(PointsPortType p : ports) {
      String aux = p.ctrlPing(inputMessage);
      if ( p.ctrlPing(inputMessage) != null )
        builder.append( p.ctrlPing(inputMessage) );
    }
    return builder.toString();
  }

  public void ctrlClear() {
    for(PointsPortType p : ports) {
      p.ctrlClear();
    }
  }

  public void ctrlInit(int startPoints) throws BadInitFault_Exception {
    for (PointsPortType p : ports) {
      p.ctrlInit(startPoints);
    }
  }

  /** Helper to throw a new InvalidPointsFault exception. */
  private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
    InvalidPointsFault faultInfo = new InvalidPointsFault();
    faultInfo.setMessage(message);
    throw new InvalidPointsFault_Exception(message, faultInfo);
  }
}
