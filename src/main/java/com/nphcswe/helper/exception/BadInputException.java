package com.nphcswe.helper.exception;

public class BadInputException extends RuntimeException {

	private static final long serialVersionUID = -7109590054409078113L;

	public BadInputException(String message) {
		super(message);
	}

	public BadInputException(String message, Throwable t) {
		super(message, t);
	}

	public BadInputException(Throwable t) {
		super(t);
	}
}
