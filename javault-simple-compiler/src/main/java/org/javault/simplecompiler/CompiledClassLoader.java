package org.javault.simplecompiler;

import java.util.Iterator;
import java.util.List;

public class CompiledClassLoader extends ClassLoader {
	private final List<ClassJavaFileObject> files;

	/*default*/ CompiledClassLoader(List<ClassJavaFileObject> files) {
		this.files = files;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Iterator<ClassJavaFileObject> itr = files.iterator();
		while (itr.hasNext()) {
			ClassJavaFileObject file = itr.next();
			if (file.getClassName().equals(name)) {
				itr.remove();
				byte[] bytes = file.getBytes();
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}
		return super.findClass(name);
	}
}
