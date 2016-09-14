package org.javault;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

public interface VaultRunner {
	/**
	 * Run code in a controlled environment. The input is a directory containing one or more class files (or 
	 * packages of class files) and a main method to be invoked.
	 * 
	 * Example: 
	 * <pre>
	 *   vaultRunner.runInVault0(Lists.newArrayList(evilCodeDirectory), "somepackage.SomeClass")
	 * </pre>
	 *
	 * @see #runInVault0(List, String)
	 * @see #runInVault0(String, String)
	 * @see #runInVault0(Runnable)
	 * @see #runInVault0(String)
	 * 
	 * @param paths Directories containing class files
	 * @param runnableClass Main class to run
	 * @return The output result, system out and system error    
	 * @throws VaultException
	 */
	Future<VaultOutput> runInVault0(List<URL> paths, String runnableClass) throws VaultException;

	/**
	 * Run code in a controlled environment. The input is a class name and the source code for the class
	 *
	 * Example: 
	 * <pre>
	 *   vaultRunner.runInVault0("SomeClass", "System.out.println(\"Hello world\");")
	 * </pre>
	 *
	 * @see #runInVault0(List, String)
	 * @see #runInVault0(String, String)
	 * @see #runInVault0(Runnable)
	 * @see #runInVault0(String)
	 * 
	 * @param className The name of the class
	 * @param source The source of the class
	 * @return The output result, system out and system error    
	 * @throws VaultException
	 */
	Future<VaultOutput> runInVault0(String className, String source) throws VaultException;

	/**
	 * Run code in a controlled environment. The input is a Runnable class.
	 *
	 * Example: 
	 * <pre>
	 *   vaultRunner.runInVault0(myRunnable)
	 * </pre>
	 *
	 * @see #runInVault0(List, String)
	 * @see #runInVault0(String, String)
	 * @see #runInVault0(Runnable)
	 * @see #runInVault0(String)
	 * 
	 * @param runnable A <code>Runnable</code> class
	 * @return The output result, system out and system error    
	 * @throws VaultException
	 */
	Future<VaultOutput> runInVault0(Runnable runnable) throws VaultException;

	/**
	 * Run code in a controlled environment. The input is a snippet (code without wrapping class)
	 *
	 * Example: 
	 * <pre>
	 *   vaultRunner.runInVault0("System.out.println(\"Hello world\");")
	 * </pre>
	 *
	 * @see #runInVault0(List, String) 
	 * @see #runInVault0(String, String)
	 * @see #runInVault0(Runnable) 
	 * @see #runInVault0(String) 
	 *
	 * @param snippet
	 * @return
	 * @throws VaultException
	 */
	Future<VaultOutput> runInVault0(String snippet) throws VaultException;
}
