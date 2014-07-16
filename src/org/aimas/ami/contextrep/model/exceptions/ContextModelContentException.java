package org.aimas.ami.contextrep.model.exceptions;

public class ContextModelContentException extends Exception {
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

	public ContextModelContentException(Throwable cause) {
		super(cause);
	}
	
	public ContextModelContentException(String message) {
		super(message);
	}
	
	public ContextModelContentException(String message, Throwable cause) {
		super(message, cause);
	}
}
