package org.javault.ws;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("secure")
@Component
public class SecureProfile {
	@PostConstruct
	public void secureApplication(){
		System.setProperty("java.security.policy", "all.policy");
		System.setProperty("java.security.manager", "java.lang.SecurityManager");
	}
}
