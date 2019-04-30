package com.forkexec.pts.ws.frontend;

/**
 * 
 * Exception to be thrown when something is wrong with the client.
 * 
 */
public class EmailAlreadyExistsFault_Exception extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsFault_Exception() {
		super();
	}

	public EmailAlreadyExistsFault_Exception(String message) {
		super(message);
	}

	public EmailAlreadyExistsFault_Exception(Throwable cause) {
		super(cause);
	}

	public EmailAlreadyExistsFault_Exception(String message, Throwable cause) {
		super(message, cause);
	}

}
