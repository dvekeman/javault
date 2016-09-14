package org.javault.ws;

public class WsVaultOutput {
	private int statusCode;
	private String output;

	public WsVaultOutput(int statusCode, String output) {
		this.statusCode = statusCode;
		this.output = output;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
