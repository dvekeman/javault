package org.javault;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EvilCode implements Runnable {

	public EvilCode(){
		System.out.println("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		System.out.println("I am running! Whoohoo!");
		Path evilFilePath = Paths.get("", "build", "resources", "test", "evil.txt");
		try(BufferedReader fr = new BufferedReader(new FileReader(evilFilePath.toFile()))){
			System.out.println(fr.readLine());
		} catch(IOException e){
			System.err.println("Oops, couldn't cast my magic spell");
			e.printStackTrace();
		}
	}
}
