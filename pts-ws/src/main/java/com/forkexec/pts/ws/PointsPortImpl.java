package com.forkexec.pts.ws;

import javax.jws.WebService;
import com.forkexec.pts.domain.Points;
import org.komparator.supplier.domain.QuantityException;
import java.util.regex.Matcher;                               
import java.util.regex.Pattern;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.pts.ws.PointsPortType", 
            wsdlLocation = "PointsService.wsdl", 
            name = "PointsWebService", 
            portName = "PointsPort", 
            targetNamespace = "http://ws.pts.forkexec.com/", 
            serviceName = "PointsService")

public class PointsPortImpl implements PointsPortType {

    /**
     * The Endpoint manager controls the Web Service instance during its whole
     * lifecycle.
     */
    private final PointsEndpointManager endpointManager;

    /** Constructor receives a reference to the endpoint manager. */
    public PointsPortImpl(final PointsEndpointManager endpointManager) {
	this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    @Override
	public void activateUser(final String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {

         if (userEmail != null) {
            String regex = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9])*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(userEmail);

            if (!m.matches()) throwInvalidEmail("User email is not valid");

            Points points = Points.getInstance();

            if (!points.userExists(userEmail))
                points.addUser(userEmail);
            else
             throwEmailAlreadyExists("User email already exists.");

        } else 
            throwInvalidEmail("User email cannot be null.");
    }

    @Override
    public int pointsBalance(final String userEmail) throws InvalidEmailFault_Exception {

         if (userEmail != null) {
            Points points = Points.getInstance();

            if (points.userExists(userEmail))
                return points.getUserPoints(userEmail);
        }
        
        throwInvalidEmail("User email is invalid.");
        return -1;
    }

    @Override
    public int addPoints(final String userEmail, final int pointsToAdd)
	    throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        
        if (pointsToAdd < 0) throwInvalidPoints("Cant add negative points");

        if (userEmail != null) {
            Points points = Points.getInstance();

            if (points.userExists(userEmail)) {
                return points.addUserPoints(userEmail, pointsToAdd);
            }
        }
        
        throwInvalidEmail("User email is invalid.");
        return -1;
    }

    @Override
    public int spendPoints(final String userEmail, final int pointsToSpend)
	    throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {

        if (pointsToSpend < 0) throwInvalidPoints("Points cant be negative.");

         if (userEmail != null) {
            Points points = Points.getInstance();

            if (points.userExists(userEmail)) {
                try {
                    return points.spendUserPoints(userEmail, pointsToSpend);
                }   catch(QuantityException qe) { 
                        throwNotEnoughBalance("Points are insufficient."); 
                }
            }
        } 
        
        throwInvalidEmail("User email is invalid.");
        return -1;
    }

    // Control operations ----------------------------------------------------
    // TODO
    /* Diagnostic operation to check if service is running. */
    @Override
    public String ctrlPing(String inputMessage) {
	// If no input is received, return a default name.
	if (inputMessage == null || inputMessage.trim().length() == 0)
	    inputMessage = "friend";

	// If the park does not have a name, return a default.
	String wsName = endpointManager.getWsName();
	if (wsName == null || wsName.trim().length() == 0)
	    wsName = "Park";

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
        if (startPoints < 0) throwBadInit("User should not start with negative points");

        Points.getInstance().setInitialBalance(startPoints);
    }

    // Exception helpers -----------------------------------------------------

    /** Helper to throw a new BadInit exception. */
    private void throwBadInit(final String message) throws BadInitFault_Exception {
        final BadInitFault faultInfo = new BadInitFault();
        faultInfo.message = message;
        throw new BadInitFault_Exception(message, faultInfo);
    }

    private void throwEmailAlreadyExists(final String message) throws EmailAlreadyExistsFault_Exception {
        final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
        faultInfo.message = message;
        throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
    }

    private void throwInvalidEmail(final String message) throws InvalidEmailFault_Exception {
        final InvalidEmailFault faultInfo = new InvalidEmailFault();
        faultInfo.message = message;
        throw new InvalidEmailFault_Exception(message, faultInfo);
    }

    private void throwInvalidPoints(final String message) throws InvalidPointsFault_Exception {
        final InvalidPointsFault faultInfo = new InvalidPointsFault();
        faultInfo.message = message;
        throw new InvalidPointsFault_Exception(message, faultInfo);
    }

    private void throwNotEnoughBalance(final String message) throws NotEnoughBalanceFault_Exception {
        final NotEnoughBalanceFault faultInfo = new NotEnoughBalanceFault();
        faultInfo.message = message;
        throw new NotEnoughBalanceFault_Exception(message, faultInfo);
    }
}
