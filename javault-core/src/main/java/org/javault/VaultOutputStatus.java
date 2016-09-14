package org.javault;

public enum VaultOutputStatus { FATAL(-2), COMPILER_ERROR(-1), SUCCESS(1);
	final int statusCode;
	VaultOutputStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	int getStatusCode(){
		return statusCode;
	}
}
