package org.aask.exception;

public class DuplicatePropertyConfigurationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DuplicatePropertyConfigurationException(String error) {
		super(error);
	}
}
