package org.javault;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.List;

import org.javaault.compiler.api.CompilerException;
import org.javaault.compiler.api.InflightCompiler;
import org.javault.simplecompiler.SimpleJavaCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class DefaultVaultRunner implements VaultRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultVaultRunner.class);

	private static final AccessControlContext allowedPermissionsAcc;
	static {
		PermissionCollection allowedPermissions = new Permissions();
		// <<< CHANGEME HERE
		//allowedPermissions.add(new java.io.FilePermission("evil.txt", "read"));
		// >>>

		allowedPermissionsAcc = new AccessControlContext(new ProtectionDomain[] {
				new ProtectionDomain(null, allowedPermissions)});
	}

	@Override
	public VaultOutput runInVault0(List<URL> paths, String runnableClass) throws VaultException {
		Runnable r = loadUntrusted(paths, runnableClass);
		return internalRunInVault(r);
	}

	@Override
	public VaultOutput runInVault0(String className, String source) throws VaultException {
		InflightCompiler inflightCompiler = new SimpleJavaCompiler();
		try {
			Class<Runnable> clazz = inflightCompiler.compileSource(className, source);
			Runnable r = loadUntrusted(clazz);
			return internalRunInVault(r);
		} catch(CompilerException ce){
			throw new VaultException(String.format("Unable to compile the source code for %s", className), ce);
		} 
	}

	@Override
	public VaultOutput runInVault0(Runnable runnable) throws VaultException {
		return internalRunInVault(runnable);
	}

	private VaultOutput internalRunInVault(Runnable runnable) throws VaultException {
		//Enforce that the security manager is enabled
		String securityManager = System.getProperty("java.security.manager");
		if(securityManager == null || "".equals(securityManager)){
			throw new VaultException("Security manager is mandatory. Run with -Djava.security.manager=java.lang.SecurityManager -Djava.security.policy=all.policy ");
		}
		try {
			return doRun(runnable);
		} catch(PrivilegedActionException e){
			String msg = "Security breach: untrusted code found. Thankfully we stopped it!";
			LOG.warn(msg);
			throw new VaultException(msg, e.getCause());
		}
	}

	/*
	 * TODO: allow code that doesn't implement runnable (wrap in custom Runnable)
	 * Run the code with limited priviliges (see allowedPermissions)
	 * @param r The <code>Runnable</code> to execute
	 */
	private VaultOutput doRun(Runnable runnable) throws PrivilegedActionException {
		// Redirect the System.out to a custom outputstream
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sysout));
		System.setErr(new PrintStream(syserr));
		
		AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
			public Void run() throws InterruptedException, InternalVaultException {
				final List<InternalVaultException> resultingException = Lists.newArrayList();
				Thread t = new Thread(runnable);
				t.setUncaughtExceptionHandler((t1, e) -> 
						resultingException.add(new InternalVaultException("Uncaught exception on thread " + t1.getId(), e)));
				t.start();
				t.join();
				if(resultingException.isEmpty()){
					return null;
				} else {
					throw resultingException.get(0);
				}
			}
		}, allowedPermissionsAcc);

		// Reset the System.out
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		return new VaultOutput(null, sysout, syserr);
	}

	/*
	 * Load untrusted using a classloader bootstrapped from the provided URLs 
	 * 
	 * @param paths Directories containing the class files
	 * @param mainClass Main runnable class
	 * @return A Runnable
	 * @throws VaultException
	 */
	private Runnable loadUntrusted(List<URL> paths, String runnableClass) throws VaultException {
		try {
			Class c = loadClass(paths, runnableClass);
			return (Runnable) c.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
			throw new VaultException("Oops, something went wrong. We could not instantiate the class", e);
		}

	}

	/*
	 * Load untrusted code directly from the class
	 * 
	 * @param paths Directories containing the class files
	 * @param mainClass Main runnable class
	 * @return A Runnable
	 * @throws VaultException
	 */
	private Runnable loadUntrusted(Class clazz) throws VaultException {
		try {
			return (Runnable) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new VaultException("Oops, something went wrong. We could not instantiate the class", e);
		}

	}

	/*
	 * Load the class using a custom classloader
	 * 
	 * @param paths
	 * @param mainClass
	 * @return
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	private Class loadClass(List<URL> paths, String mainClass) throws ClassNotFoundException, MalformedURLException {
		URLClassLoader classLoader = new URLClassLoader(paths.toArray(new URL[]{}));
		return classLoader.loadClass(mainClass);
	}

}
