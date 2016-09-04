package org.javault;

/**
 * Internal exception for feedback to the client code
 */
public class InternalVaultException extends Exception {
	public InternalVaultException(String message, Throwable t){
		super(message, t);
	}
}
