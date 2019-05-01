package com.forkexec.pts.ws;

import javax.jws.WebService;

import com.forkexec.pts.domain.Points;
import com.forkexec.pts.domain.*;
import com.forkexec.pts.domain.exception.InvalidEmailFaultException;
import com.forkexec.pts.domain.exception.InvalidPointsFaultException;
import com.forkexec.pts.domain.exception.NotEnoughBalanceFaultException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(
endpointInterface = "com.forkexec.pts.ws.PointsPortType",
wsdlLocation = "PointsService.wsdl",
name = "PointsWebService",
portName = "PointsPort",
targetNamespace = "http://ws.pts.forkexec.com/",
serviceName = "PointsService")
public class PointsPortImpl implements PointsPortType {

  private final String SUCCESS = "ACK";

  private final String FAILURE = "NACK";

  /**
   * The Endpoint manager controls the Web Service instance during its whole
   * lifecycle.
   */
  private final PointsEndpointManager endpointManager;



  /** Constructor receives a reference to the endpoint manager. */
  public PointsPortImpl(final PointsEndpointManager endpointManager) {
    this.endpointManager = endpointManager;
  }

  //========================================================================
  // MAIN OPERATATIONS
  //========================================================================

  @Override
  public Value read(String userEmail) throws InvalidEmailFault_Exception {
    final Points points = Points.getInstance();
    try {
      Tag tag = points.getAccountTag(userEmail);
      int value = points.getAccountPoints(userEmail);
      return createValue(value, tag);
    } catch (InvalidEmailFaultException e) {
      throwInvalidEmailFault(e.getMessage());
    }
    return null;
  }

  @Override
  public String write(String userEmail, int points, Tag t)
    throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
    final Points instance = Points.getInstance();

    try {
      Tag tag = instance.getAccountTag(userEmail);

      if (t.getSeq() > tag.getSeq()) {
        instance.setAccountTag(userEmail, t);
        instance.setAccountPoints(userEmail, points);
        return SUCCESS;
      }
    } catch (InvalidEmailFaultException e) {
      throwInvalidEmailFault(e.getMessage());
    } catch (InvalidPointsFaultException e) {
      throwInvalidPointsFault(e.getMessage());
    }
    return FAILURE;
  }
  
  //========================================================================
  // CONTROL OPERATIONS
  //========================================================================

  /** Diagnostic operation to check if service is running. */
  @Override
  public String ctrlPing(String inputMessage) {
    // If no input is received, return a default name.
    if (inputMessage == null || inputMessage.trim().length() == 0)
      inputMessage = "friend";

    // If the service does not have a name, return a default.
    String wsName = endpointManager.getWsName();
    if (wsName == null || wsName.trim().length() == 0)
      wsName = PointsApp.class.getSimpleName();

    // Build a string with a message to return.
    final StringBuilder builder = new StringBuilder();
    builder.append("Hello ").append(inputMessage);
    builder.append(" from ").append(wsName);
    return builder.toString();
  }

  /** Return all variables to default values. */
  @Override
  public void ctrlClear() {
    Points.getInstance().reset();
  }

  /** Set variables with specific values. */
  @Override
  public void ctrlInit(final int startPoints) throws BadInitFault_Exception {
    Points.getInstance().setInitialBalance(startPoints);
  }
  
  //========================================================================
  // VIEW HELPERS 
  //========================================================================

  public Value createValue(int val, Tag tag) {
    Value value = new Value();
    value.setVal(val);
    value.setTag(tag);
    return value;
  }
  
  //========================================================================
  // EXCEPTION HELPERS
  //========================================================================

  /** Helper to throw a new BadInit exception. */
  private void throwBadInit(final String message) throws BadInitFault_Exception {
    final BadInitFault faultInfo = new BadInitFault();
    faultInfo.message = message;
    throw new BadInitFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new InvalidEmailFault exception. */
  private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
    final InvalidEmailFault faultInfo = new InvalidEmailFault();
    faultInfo.message = message;
    throw new InvalidEmailFault_Exception(message, faultInfo);
  }

  /** Helper to throw a new InvalidPointsFault exception. */
  private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
    final InvalidPointsFault faultInfo = new InvalidPointsFault();
    faultInfo.message = message;
    throw new InvalidPointsFault_Exception(message, faultInfo);
  }

}
