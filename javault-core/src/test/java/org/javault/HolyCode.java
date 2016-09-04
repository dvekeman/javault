package org.javault;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HolyCode implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(HolyCode.class);
	
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	
	public HolyCode(){
		LOG.info("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		LOG.info("I am running! Whoohoo!");
		atomicInteger.incrementAndGet();
		LOG.info("I did it!");
	}
}
