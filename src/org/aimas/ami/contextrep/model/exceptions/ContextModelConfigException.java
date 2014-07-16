package org.aimas.ami.contextrep.model.exceptions;

public class ContextModelConfigException extends Exception {
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

	public ContextModelConfigException(Throwable cause) {
		super(cause);
	}
	
	public ContextModelConfigException(String message) {
		super(message);
	}
	
	public ContextModelConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
