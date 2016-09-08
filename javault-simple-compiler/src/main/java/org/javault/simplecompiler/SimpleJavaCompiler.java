//Credits to @chrisvest (github.com/chrisvest)
// https://gist.github.com/chrisvest/9873843
package org.javault.simplecompiler;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.javaault.compiler.api.CompilerException;
import org.javaault.compiler.api.InflightCompiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleJavaCompiler implements InflightCompiler {

	private final JavaCompiler compiler;
	private final SimpleJavaFileManager fileManager;

	public SimpleJavaCompiler() {
		compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager defaultFileManager = compiler.getStandardFileManager(
				/*DiagnosticListener*/null,
				Locale.getDefault(),
				Charset.forName("UTF-8"));
		fileManager = new SimpleJavaFileManager(defaultFileManager);
	}

	@Override
	public Class<?> compileSource(String className, String source) throws CompilerException {
		Map<String, String> sources = Maps.newHashMap();
		sources.put(className, source);
		try {
			Map<String, Class<?>> compiledClasses = compileClasses(sources);
			return compiledClasses.get(className);
		} catch (CompilerException e) {
			throw e;
		} catch (Exception e) {
			throw new CompilerException("Could not compile sources", e);
		}
	}

	public <T> Map<String, Class<?>> compileClasses(Map<String, T> sources) throws IOException, ClassNotFoundException, CompilerException {

		List<JavaFileObject> compilationUnits = Lists.newArrayList();
		sources.forEach((name, input) -> {
			JavaFileObject compilationUnit =
					new StringJavaFileObject(name, (String) input);
			compilationUnits.add(compilationUnit);
		});

		StringWriter out = new StringWriter();
		JavaCompiler.CompilationTask compilationTask = compiler.getTask(
				out,
				fileManager, 
				/*diagnoticListener=*/null, 
				/*options=*/null, 
				/*classes*/null,
				compilationUnits);

		boolean compilationSucceeded = compilationTask.call();

		if (!compilationSucceeded) {
			throw new CompilerException(out.toString());
		}

		Map<String, Class<?>> compiledClasses = Maps.newHashMap();

		for (String name : sources.keySet()) {
			ClassJavaFileObject javaFileObject = fileManager.getClassForClassName(name);
			Class<?> compiledClass = loadClass(name, javaFileObject.getBytes());
			compiledClasses.put(name, compiledClass);
		}

		return compiledClasses;
	}

	private Class<?> loadClass(String className, byte[] bytes) throws ClassNotFoundException {
		URLClassLoader classLoader = new URLClassLoader(new URL[]{}, SimpleJavaCompiler.class.getClassLoader()) {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		};

		Class<?> codeGenTest = classLoader.loadClass(className);
		return codeGenTest;
	}


}
