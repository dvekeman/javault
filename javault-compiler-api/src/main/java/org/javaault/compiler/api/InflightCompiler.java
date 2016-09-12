package org.javaault.compiler.api;

/**
 * Bridge interface between multiple java compiler implementation.
 * <br>
 * INPUT: String (for now only String is supported but this could be extended later on)<br>
 * OUTPUT: Class <br> 
 */
public interface InflightCompiler {
	<T> Class<T> compileSource(String className, String source) throws CompilerException;
}
