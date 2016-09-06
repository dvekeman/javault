package org.javault.ws;

import org.javault.VaultException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_ACCEPTABLE)
public class HttpVaultException extends Exception{
	public HttpVaultException(VaultException ve){
		super(ve);
	}
}
