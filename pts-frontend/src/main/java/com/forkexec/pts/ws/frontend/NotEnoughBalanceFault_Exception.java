package com.forkexec.pts.ws.frontend;

/**
 * 
 * Exception to be thrown when something is wrong with the client.
 * 
 */
public class NotEnoughBalanceFault_Exception extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughBalanceFault_Exception() {
		super();
	}

	public NotEnoughBalanceFault_Exception(String message) {
		super(message);
	}

	public NotEnoughBalanceFault_Exception(Throwable cause) {
		super(cause);
	}

	public NotEnoughBalanceFault_Exception(String message, Throwable cause) {
		super(message, cause);
	}

}
