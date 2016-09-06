package org.javault.simplecompiler;

import java.io.IOException;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import com.google.common.collect.Maps;

public class SimpleJavaFileManager extends ForwardingJavaFileManager {
	private final Map<String, ClassJavaFileObject> classFilesByClassNames;

	/*default*/ SimpleJavaFileManager(JavaFileManager fileManager) {
		super(fileManager);
		classFilesByClassNames = Maps.newHashMap();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
		classFilesByClassNames.put(className, file);
		return file;
	}

	public ClassJavaFileObject getClassForClassName(String className){
		return classFilesByClassNames.get(className);
	}
}
