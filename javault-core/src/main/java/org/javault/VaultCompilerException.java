package org.javault;

/**
 * Exception indication java compilation issues
 */
public class VaultCompilerException extends VaultException {
	
	public final String compilationMessage;
	
	public VaultCompilerException(String message, Throwable t) {
		super(message, t);
		this.compilationMessage = t.getMessage();
	}

	public String getCompilationMessage() {
		return compilationMessage;
	}
}
