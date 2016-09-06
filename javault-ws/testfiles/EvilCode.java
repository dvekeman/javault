import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EvilCode implements Runnable {

	public EvilCode(){
		System.out.println("I am initializing... hehe");
	}
	
	@Override
	public void run() {
		System.out.println("I am running! Whoohoo!");
		try(BufferedReader fr = new BufferedReader(new FileReader("evil.txt"))){
			System.out.println(fr.readLine());
		} catch(IOException e){
			System.err.println("Oops, couldn't cast my magic spell");
			e.printStackTrace();
		}
	}
}
