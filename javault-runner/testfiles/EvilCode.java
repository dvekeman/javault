package org.javault;

public class EvilCode implements Runnable {

	public EvilCode(){
		System.out.println("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		System.out.println("I am running! Whoohoo!");
		try(java.io.BufferedReader fr = new java.io.BufferedReader(new java.io.FileReader("evil.txt"))){
			System.out.println(fr.readLine());
		} catch(java.io.IOException e){
			e.printStackTrace();
		}
	}
}
