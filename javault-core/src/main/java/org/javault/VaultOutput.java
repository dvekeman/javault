package org.javault;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class VaultOutput<T> {

	private final ByteArrayOutputStream sysout;
	private final ByteArrayOutputStream syserr;
	private final T result;
	private final VaultOutputStatus status;
	private final List<Throwable> exceptions = Lists.newArrayList();

	public VaultOutput(T result, ByteArrayOutputStream sysout, ByteArrayOutputStream syserr, List<Throwable> exceptions) {
		this.result = result;
		this.sysout = sysout;
		this.syserr = syserr;
		this.status = exceptions.isEmpty() ? VaultOutputStatus.SUCCESS : VaultOutputStatus.FATAL;
		this.exceptions.addAll(exceptions);
	}

	public String getSysout() {
		try {
			return sysout.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return sysout.toString();
		}
	}

	public String getSyserr() {
		try {
			return syserr.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return syserr.toString();
		}
	}

	public String getOutput() {
		switch (status) {
			case SUCCESS:
				return getSysout();
			case FATAL:
				StringBuilder output = new StringBuilder();
				output.append(
						exceptions.stream()
								.filter(throwable -> throwable instanceof VaultRunException)
								.map(Throwable::getCause)
								.map(Throwable::getMessage)
								.collect(Collectors.joining(System.lineSeparator())));
				if (getSyserr() != null && !"".equals(getSyserr())) {
					output.append(System.lineSeparator() +
							System.lineSeparator() +
							getSyserr());
				}
				return output.toString();
			default:
				return getSysout() + System.lineSeparator() + getSyserr();
		}
	}

	public int getStatusCode() {
		return status.getStatusCode();
	}

	public List<Throwable> getExceptions() {
		return exceptions;
	}

	//	public T getResult(){
//		return result;
//	}
}
