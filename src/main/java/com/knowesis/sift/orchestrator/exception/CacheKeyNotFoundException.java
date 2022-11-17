package com.knowesis.sift.orchestrator.exception;

/**
 * This class is used to throw error if the cache key is not found
 */
public class CacheKeyNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public CacheKeyNotFoundException() {
		super();
	}
	
	/**
	 * Used to throw error if the cache key is not found in memory
	 * @param message custom message to display in exception stack trace
	 */
	public CacheKeyNotFoundException(String message) {
		super(message);
	}

}
