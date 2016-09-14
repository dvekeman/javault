package org.javault.ws;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
//@RequestMapping(value = "/javault-ws-0.1.0")
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
	public WsVaultOutput getRunInVault0(
			@RequestParam(value = "name", defaultValue = "HelloWorld") String name,
			@RequestParam(value = "source", defaultValue = "public class HelloWorld implements Runnable {\n" +
					"  public void run() {\n" +
					"    System.out.println(\"Hello World, from a generated program!\");\n" +
					"  }\n" +
					"}\n") String source) throws HttpVaultException, GenericException {
		return doRunInVault0(name, source);
	}

	@RequestMapping(path = "/runInVault0", method = RequestMethod.POST)
	public WsVaultOutput postRunInVault0(
			@RequestParam(value = "name") String name,
			@RequestBody String source) throws HttpVaultException, GenericException {
		LOG.debug("name: " + name);
		LOG.debug("source: " + source);
		return doRunInVault0(name, source);
	}

	@RequestMapping(path = "/runSnippetInVault0", method = RequestMethod.POST)
	public WsVaultOutput postRunSnippetInVault0(
			@RequestBody String source) throws HttpVaultException, GenericException {
		LOG.debug("source: " + source);
		return doRunSnippetInVault0(source);
	}

	private WsVaultOutput doRunInVault0(String name, String source) throws HttpVaultException, GenericException {
		//TODO: wrap in service
		VaultRunner vaultRunner = new DefaultVaultRunner();
		try {
			VaultOutput output = vaultRunner.runInVault0(name, source).get(60, TimeUnit.SECONDS);
			return new WsVaultOutput(output.getStatusCode(), output.getOutput());
		} catch (VaultException ve) {
			throw new HttpVaultException(ve);
		} catch (InterruptedException ve) {
			throw new GenericException(ve);
		} catch (ExecutionException ve) {
			throw new GenericException(ve);
		} catch (TimeoutException ve) {
			throw new GenericException(ve);
		}
	}

	private WsVaultOutput doRunSnippetInVault0(String source) throws HttpVaultException, GenericException {
		//TODO: wrap in service
		VaultRunner vaultRunner = new DefaultVaultRunner();
		try {
			VaultOutput output = vaultRunner.runInVault0(source).get(60, TimeUnit.SECONDS);
			return new WsVaultOutput(output.getStatusCode(), output.getOutput());
		} catch (VaultException ve) {
			throw new HttpVaultException(ve);
		} catch (InterruptedException ve) {
			throw new GenericException(ve);
		} catch (ExecutionException ve) {
			throw new GenericException(ve);
		} catch (TimeoutException ve) {
			throw new GenericException(ve);
		}
	}

}