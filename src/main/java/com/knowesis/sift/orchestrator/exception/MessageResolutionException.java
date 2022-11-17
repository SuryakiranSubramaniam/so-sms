package com.knowesis.sift.orchestrator.exception;

/**
 * Custom Exception class
 *
 */
public class MessageResolutionException extends Exception {

	/**
	 * Searial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This class is used to throw exceptions if Core message is in wrong format.
	 * @param message custom message to display in exception stack trace.
	 */
	public MessageResolutionException(String message) {
		super(message);
	}
}
