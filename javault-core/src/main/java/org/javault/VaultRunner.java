package org.javault;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class VaultRunner {

	private static final Logger LOG = LoggerFactory.getLogger(VaultRunner.class);

	private static final AccessControlContext allowedPermissionsAcc;
	static {
		PermissionCollection allowedPermissions = new Permissions();
		// <<< CHANGEME HERE
		//allowedPermissions.add(new java.io.FilePermission("evil.txt", "read"));
		// >>>

		allowedPermissionsAcc = new AccessControlContext(new ProtectionDomain[] {
				new ProtectionDomain(null, allowedPermissions)});
	}

	/**
	 * Main entry point to run code in a controller environment.
	 * @param paths Directories containing class files
	 * @param runnableClass Main class to run
	 *                         
	 * @throws VaultException
	 */
	public void runInVault(List<URL> paths, String runnableClass) throws VaultException {
		Runnable r = loadUntrusted(paths, runnableClass);
		try {
			runInVault(r);
		} catch(PrivilegedActionException e){
			String msg = "Security breach: untrusted code found. Thankfully we stopped it!";
			LOG.warn(msg);
			throw new VaultException(msg, e.getCause());
		}
	}

	/*
	 * Run the code with limited priviliges (see allowedPermissions)
	 * @param r The <code>Runnable</code> to execute
	 */
	private void runInVault(Runnable r) throws PrivilegedActionException {
		AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
			public Void run() throws InterruptedException, InternalVaultException {
				final List<InternalVaultException> resultingException = Lists.newArrayList();
				Thread t = new Thread(r);
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
	}

	/*
	 * Load a plugin 
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
