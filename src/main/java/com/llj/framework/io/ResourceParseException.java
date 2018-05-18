package com.llj.framework.io;

public class ResourceParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceParseException() {
		super();
	}

	public ResourceParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceParseException(String message) {
		super(message);
	}

	public ResourceParseException(Throwable cause) {
		super(cause);
	}
}
