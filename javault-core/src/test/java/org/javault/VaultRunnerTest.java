package org.javault;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class VaultRunnerTest {

	private static final Logger LOG = LoggerFactory.getLogger(VaultRunner.class);

	private VaultRunner vaultRunner;
	
	@Before
	public void setup(){
		vaultRunner = new VaultRunner();
	}
	
	@Test(expected = VaultException.class)
	public void testLoadEvilCode() throws VaultException, MalformedURLException {
		LOG.info("Running evil code. Update the VaultRunner#allowedPermissionsAcc");
		
		// Trailing slash is important to mark it as a directory to the classloader
		URL evilCodeDirectory = this.getClass().getClassLoader().getResource("org/javault/");
		vaultRunner.runInVault(Lists.newArrayList(evilCodeDirectory), "org.javault.EvilCode");
		LOG.info("Done");
	}
	
	@Test
	public void testHolyCode()  throws VaultException, MalformedURLException {
		// Trailing slash is important to mark it as a directory to the classloader
		URL evilCodeDirectory = this.getClass().getClassLoader().getResource("org/javault/");
		vaultRunner.runInVault(Lists.newArrayList(evilCodeDirectory), "org.javault.HolyCode");
	}
}
