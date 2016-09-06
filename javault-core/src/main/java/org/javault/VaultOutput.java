package org.javault;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class VaultOutput<T> {
	private ByteArrayOutputStream sysout;
	private ByteArrayOutputStream syserr;
	private T result;
	
	public VaultOutput(T result, ByteArrayOutputStream sysout, ByteArrayOutputStream syserr){
		this.result = result;
		this.sysout = sysout;
		this.syserr = syserr;
	}
	
	public String getSysout() {
		try {
			return sysout.toString("UTF-8");
		} catch(UnsupportedEncodingException e){
			return sysout.toString();
		}
	}

	public String getSyserr() {
		try {
			return syserr.toString("UTF-8");
		} catch(UnsupportedEncodingException e){
			return syserr.toString();
		}
	}
	
	public T getResult(){
		return result;
	}
}
