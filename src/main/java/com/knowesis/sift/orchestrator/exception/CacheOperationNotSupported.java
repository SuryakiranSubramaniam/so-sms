package com.knowesis.sift.orchestrator.exception;

public class CacheOperationNotSupported extends Exception {

	private static final long serialVersionUID = 1L;


	/**
	 * Used to throw exception if not able to perform cache operation.
	 */
	public CacheOperationNotSupported (String message){
		super(message);
	}
}
