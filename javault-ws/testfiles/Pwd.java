import java.io.File;

public class Pwd implements Runnable {

	@Override
	public void run() {
		File pwd = new File(".");
		for(File f : pwd.listFiles()){
			System.out.println(String.format("%s - %d (bytes)", f.getName(), f.getTotalSpace()));
		}
	}

	public static void main(String[] args){
		new Pwd().run();
	}
}
