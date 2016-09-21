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
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.javaault.compiler.api.CompilerException;
import org.javaault.compiler.api.InflightCompiler;
import org.javault.simplecompiler.SimpleJavaCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class DefaultVaultRunner implements VaultRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultVaultRunner.class);

	private AccessControlContext accessControlContext;

	private final ExecutorService pool = Executors.newFixedThreadPool(10);

	private DefaultVaultRunner(AccessControlContext accessControlContext) {
		this.accessControlContext = accessControlContext;
	}
	
	public static class Builder {
		PermissionCollection allowedPermissions = new Permissions();
		public Builder() {
		}

		/**
		 * allowedPermissions.add(new java.io.FilePermission("myfile.txt", "read"));
		 * 
		 * @param permission
		 * @return
		 */
		public Builder addPermission(Permission permission){
			allowedPermissions.add(permission);
			return this;
		}
		
		public VaultRunner build(){
			AccessControlContext accessControlContext = new AccessControlContext(new ProtectionDomain[]{
					new ProtectionDomain(null, allowedPermissions)});
			return new DefaultVaultRunner(accessControlContext);
		}

	}

	@Override
	public Future<VaultOutput> runInVault0(List<URL> paths, String runnableClass) throws VaultException {
		Runnable r = loadUntrusted(paths, runnableClass);
		return internalRunInVault(r);
	}

	@Override
	public Future<VaultOutput> runInVault0(String className, String source) throws VaultException {
		InflightCompiler inflightCompiler = new SimpleJavaCompiler();
		try {
			Class<Runnable> clazz = inflightCompiler.compileSource(className, source);
			Runnable r = loadUntrusted(clazz);
			return internalRunInVault(r);
		} catch (CompilerException ce) {
			ByteArrayOutputStream syserr = new ByteArrayOutputStream();
			new PrintStream(syserr).append(ce.getMessage()).flush();
			return CompletableFuture.completedFuture(
					new VaultOutput(null, new ByteArrayOutputStream(), syserr,
							Lists.newArrayList(new VaultCompilerException(String.format("Unable to compile the source code for %s", className), ce))));
		}
	}

	@Override
	public Future<VaultOutput> runInVault0(Runnable runnable) throws VaultException {
		return internalRunInVault(runnable);
	}

	@Override
	public Future<VaultOutput> runInVault0(String snippet) throws VaultException {
		String tempClassName = "VaultSnippetExecution";
		String tempHost = "" +
				"public class %s implements Runnable {\n" +
				"  public void run() {\n" +
				"    %s" +
				"  }\n" +
				"}\n";
		return runInVault0(tempClassName, String.format(tempHost, tempClassName, snippet));
	}

	private Future<VaultOutput> internalRunInVault(Runnable runnable) throws VaultException {
		//Enforce that the security manager is enabled
		SecurityManager securityManager = System.getSecurityManager();
		LOG.debug("Security Manager: " + securityManager);
		if (securityManager == null) {
			throw new VaultException("Security manager is mandatory. Run with -Djava.security.manager=java.lang.SecurityManager -Djava.security.policy=all.policy ");
		}
		try {
			return doRun(runnable);
		} catch (PrivilegedActionException e) {
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
	private Future<VaultOutput> doRun(Runnable runnable) throws PrivilegedActionException {
		return pool.submit(() -> {
			try {
				// Redirect the System.out to a custom outputstream
				ByteArrayOutputStream sysout = new ByteArrayOutputStream();
				ByteArrayOutputStream syserr = new ByteArrayOutputStream();
				System.setOut(new PrintStream(sysout));
				System.setErr(new PrintStream(syserr));

				final List<VaultRunException> resultingException = Lists.newArrayList();
				AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
					public Void run() throws InterruptedException, VaultRunException {

						Thread t = new Thread(runnable);
						t.setUncaughtExceptionHandler((t1, e) ->
								resultingException.add(new VaultRunException("Uncaught exception on thread " + t1.getId(), e)));
						t.start();
						t.join();
						LOG.debug("Sandbox code: DONE!");
						return null;
					}
				}, accessControlContext);

				LOG.debug("VaultRunner: DONE!");
				return new VaultOutput(null, sysout, syserr, resultingException);
			} finally {
				// Reset the System.out
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
			}
		});
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

			Class[] interfaces = c.getInterfaces();
			boolean isRunnable = Arrays.asList(interfaces).stream()
					.anyMatch(aClass -> aClass.getCanonicalName().equals(Runnable.class.getCanonicalName()));
			if (isRunnable) {
				return (Runnable) c.newInstance();
			}
			throw new VaultException("Unable to determine how to run this code");
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
