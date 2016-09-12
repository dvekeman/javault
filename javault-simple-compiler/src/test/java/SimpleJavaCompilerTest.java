import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.javaault.compiler.api.CompilerException;
import org.javault.simplecompiler.SimpleJavaCompiler;
import org.junit.Before;
import org.junit.Test;

public class SimpleJavaCompilerTest {

	private SimpleJavaCompiler simpleJavaCompiler;

	@Before
	public void setup() {
		simpleJavaCompiler = new SimpleJavaCompiler();
	}

	@Test
	public void testCompileHelloWorld() throws IOException, CompilerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String helloWorld = "" +
				"public class HelloWorld {" + System.lineSeparator() + "" +
				"  public static void main(String[] args) {" + System.lineSeparator() + "" +
				"    System.out.println(\"Hello World, from a generated program!\");" + System.lineSeparator() + "" +
				"  }" + System.lineSeparator() + "" +
				"}" + System.lineSeparator() + "";

		Class<?> codeGenTest = simpleJavaCompiler.compileSource("HelloWorld", helloWorld);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		
		Method main = codeGenTest.getMethod("main", String[].class);
		main.invoke(null, new Object[]{null});

		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		
		String generatedOutput = baos.toString("UTF-8");
		
		assertEquals("Hello World, from a generated program!" + System.lineSeparator() + "", generatedOutput);
	}
}
