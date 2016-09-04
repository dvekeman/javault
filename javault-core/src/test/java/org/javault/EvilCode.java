package org.javault;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvilCode implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(EvilCode.class);
	
	public EvilCode(){
		LOG.info("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		LOG.info("I am running! Whoohoo!");
		try(BufferedReader fr = new BufferedReader(new FileReader("evil.txt"))){
			LOG.info(fr.readLine());
		} catch(IOException e){
			LOG.error("Oops, couldn't cast my magic spell", e);
		}
	}
}
