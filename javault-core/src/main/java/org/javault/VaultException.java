package org.javault;

/**
 * Generic exception for feedback to the client code
 */
public class VaultException extends Exception {
	public VaultException(String message){
		super(message);
	}
	
	public VaultException(String message, Throwable t){
		super(message, t);
	}
}
