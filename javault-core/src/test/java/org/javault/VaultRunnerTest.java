package org.javault;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * INFO: to run the test from within an IDE you have to enable java security by adding these system properties
 * <pre>
 *     -Djava.security.policy=all.policy -Djava.security.manager=java.lang.SecurityManager
 * </pre>
 */
public class VaultRunnerTest {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultVaultRunner.class);

	private VaultRunner vaultRunner;

	@Before
	public void setup() {
		vaultRunner = new DefaultVaultRunner();
	}

	@Test(expected = VaultException.class)
	public void testWithoutSecurity() throws VaultException {
		String securityManager = System.getProperty("java.security.manager");
		System.out.println("Current security manager: " + securityManager);

		System.setSecurityManager(null);
		securityManager = System.getProperty("java.security.manager");
		System.out.println("Current security manager: " + securityManager);

		vaultRunner.runInVault0(() -> System.out.println("Blah"));
	}

	@Test(expected = VaultException.class)
	public void testLoadEvilCode() throws VaultException, MalformedURLException, UnsupportedEncodingException {
		LOG.info("Running evil code. Update the VaultRunner#allowedPermissionsAcc");

		// Trailing slash is important to mark it as a directory to the classloader
		URL evilCodeDirectory = this.getClass().getClassLoader().getResource("org/javault/");

		// Run the code in the vault
		vaultRunner.runInVault0(Lists.newArrayList(evilCodeDirectory), "org.javault.EvilCode");
	}

	@Test
	public void testHolyCode() throws VaultException, MalformedURLException, UnsupportedEncodingException {
		// Trailing slash is important to mark it as a directory to the classloader
		URL evilCodeDirectory = this.getClass().getClassLoader().getResource("org/javault/");

		VaultOutput output = vaultRunner.runInVault0(Lists.newArrayList(evilCodeDirectory), "org.javault.HolyCode");
		//TODO: FIXME: Initialization output should also be part of the result!
//		assertEquals("I am initializing." + System.lineSeparator() + "I am running! Whoohoo!" + System.lineSeparator() + "I did it!" + System.lineSeparator() + "", output.getSysout());
		assertEquals("I am running! Whoohoo!" + System.lineSeparator() + "I did it!" + System.lineSeparator() + "", output.getSysout());
	}

	@Test
	public void testRunFromSource() throws VaultException, UnsupportedEncodingException {
		String helloWorld = "" +
				"public class HelloWorld implements Runnable {" + System.lineSeparator() + "" +
				"  public void run() {" + System.lineSeparator() + "" +
				"    System.out.println(\"Hello World, from a generated program!\");" + System.lineSeparator() + "" +
				"  }" + System.lineSeparator() + "" +
				"}" + System.lineSeparator() + "";
		VaultOutput output = vaultRunner.runInVault0("HelloWorld", helloWorld);
		assertEquals("Hello World, from a generated program!" + System.lineSeparator() + "", output.getSysout());
	}

	@Test
	public void testRunSnippet() throws VaultException, UnsupportedEncodingException {
		String helloWorldAsSnippet = "" +
				"    System.out.println(\"Hello World, a snippet, from a generated program!\");" + System.lineSeparator() + "";
		VaultOutput output = vaultRunner.runInVault0(helloWorldAsSnippet);
		assertEquals("Hello World, a snippet, from a generated program!" + System.lineSeparator() + "", output.getSysout());
	}

	@Test
	public void testRunRubbish() throws VaultException, UnsupportedEncodingException {
		String helloWorldAsSnippet = "" +
				"    I don't compile" + System.lineSeparator() + "" + System.lineSeparator();
		String expectedCompilationIssue = 
				"/VaultSnippetExecution.java:3: error: unclosed character literal" + System.lineSeparator() + "" +
				"        I don't compile" + System.lineSeparator() + "" +
				"             ^" + System.lineSeparator() + "" +
				"/VaultSnippetExecution.java:3: error: not a statement" + System.lineSeparator() + "" +
				"        I don't compile" + System.lineSeparator() + "" +
				"                ^" + System.lineSeparator() + "" +
				"/VaultSnippetExecution.java:3: error: ';' expected" + System.lineSeparator() + "" +
				"        I don't compile" + System.lineSeparator() + "" +
				"                       ^" + System.lineSeparator() + "" +
				"3 errors" + System.lineSeparator() + "";
		
		try {
			vaultRunner.runInVault0(helloWorldAsSnippet);
		} catch(VaultCompilerException vce){
			assertEquals(expectedCompilationIssue, vce.getCompilationMessage());
		}
	}
}
