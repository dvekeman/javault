package org.javault;

import java.net.URL;
import java.util.List;

public interface VaultRunner {
	/**
	 * Main entry point to run code in a controller environment.
	 * 
	 * Example: 
	 * <pre>
	 *   vaultRunner.runInVault0(Lists.newArrayList(evilCodeDirectory), "somepackage.SomeClass")
	 * </pre>
	 * 
	 * @param paths Directories containing class files
	 * @param runnableClass Main class to run
	 *                         
	 * @throws VaultException
	 */
	VaultOutput runInVault0(List<URL> paths, String runnableClass) throws VaultException;
	
	VaultOutput runInVault0(String className, String source) throws VaultException;
	
	VaultOutput runInVault0(Runnable runnable) throws VaultException;
	
	VaultOutput runInVault0(String source) throws VaultException;
}
