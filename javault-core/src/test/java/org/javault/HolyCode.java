package org.javault;

import java.util.concurrent.atomic.AtomicInteger;

public class HolyCode implements Runnable {

	private AtomicInteger atomicInteger = new AtomicInteger(0);
	
	public HolyCode(){
		System.out.println("I am initializing.");
	}
	
	@Override
	public void run() {
		System.out.println("I am running! Whoohoo!");
		atomicInteger.incrementAndGet();
		System.out.println("I did it!");
	}
	
}
