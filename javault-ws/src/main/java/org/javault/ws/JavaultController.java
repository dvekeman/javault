package org.javault.ws;

import java.util.concurrent.atomic.AtomicLong;

import org.javault.DefaultVaultRunner;
import org.javault.VaultException;
import org.javault.VaultOutput;
import org.javault.VaultRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaultController {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultVaultRunner.class);
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(),
				String.format(template, name));
	}

	@RequestMapping(path = "/runInVault0")
	public String getRunInVault0(
			@RequestParam(value = "name", defaultValue = "HelloWorld") String name,
			@RequestParam(value = "source", defaultValue = "public class HelloWorld implements Runnable {\n" +
					"  public void run() {\n" +
					"    System.out.println(\"Hello World, from a generated program!\");\n" +
					"  }\n" +
					"}\n") String source) throws HttpVaultException {
		return doRunInVault0(name, source);
	}

	@RequestMapping(path = "/runInVault0", method = RequestMethod.POST)
	public String postRunInVault0(
			@RequestParam(value = "name") String name,
			@RequestBody String source) throws HttpVaultException {
		LOG.debug("name: " + name);
		LOG.debug("source: " + source);
		return doRunInVault0(name, source);
	}

	@RequestMapping(path = "/runScriptInVault0", method = RequestMethod.POST)
	public String postRunScriptInVault0(
			@RequestBody String source) throws HttpVaultException {
		LOG.debug("source: " + source);
		return doRunScriptInVault0(source);
	}

	private String doRunInVault0(String name, String source) throws HttpVaultException {
		//TODO: wrap in service
		VaultRunner vaultRunner = new DefaultVaultRunner();
		try {
			VaultOutput output = vaultRunner.runInVault0(name, source);
			return output.getSysout();
		} catch (VaultException ve) {
			throw new HttpVaultException(ve);
		}
	}

	private String doRunScriptInVault0(String source) throws HttpVaultException {
		//TODO: wrap in service
		VaultRunner vaultRunner = new DefaultVaultRunner();
		try {
			VaultOutput output = vaultRunner.runInVault0(source);
			return output.getSysout();
		} catch (VaultException ve) {
			throw new HttpVaultException(ve);
		}
	}

}