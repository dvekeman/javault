package org.javault.simplecompiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public class ClassJavaFileObject extends SimpleJavaFileObject {
	private final ByteArrayOutputStream outputStream;
	private final String className;

	protected ClassJavaFileObject(String className, Kind kind) {
		super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
		this.className = className;
		outputStream = new ByteArrayOutputStream();
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return outputStream;
	}

	public byte[] getBytes() {
		return outputStream.toByteArray();
	}

	public String getClassName() {
		return className;
	}
}
