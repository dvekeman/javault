package org.javault;

public class EvilCode implements Runnable {

	public EvilCode(){
		System.out.println("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		System.out.println("I am running! Whoohoo!");
		java.nio.file.Path evilFilePath = java.nio.file.Paths.get("", "build", "resources", "test", "evil.txt");
		try(java.io.BufferedReader fr = new java.io.BufferedReader(new java.io.FileReader(evilFilePath.toFile()))){
			System.out.println(fr.readLine());
		} catch(java.io.IOException e){
			System.err.println("Oops, couldn't cast my magic spell");
			e.printStackTrace();
		}
	}
}
