package org.javault.ws;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericException extends Exception{

	public GenericException(String msg){
		super(msg);
	}

	public GenericException(Exception e){
		super(e);
	}
}
