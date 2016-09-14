package org.javault;

/**
 * Internal exception for feedback to the client code
 */
public class VaultRunException extends Exception {
	public VaultRunException(String message, Throwable t){
		super(message, t);
	}
}
