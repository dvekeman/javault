package org.javault;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaultRunner implements CommandLineRunner {

	private static final String VERSION = "0.0.1";
	
	private VaultRunner vaultRunner = new DefaultVaultRunner();

	@Override
	public void run(String... args) {
		if (args.length == 0) {
			try {
				runFromInput();
				System.exit(0);
			} catch (VaultException ve) {
				ve.printStackTrace();
				System.exit(1);
			}
		}

		try {
			VaultOutput output = null; 
			if (args.length == 1) {
				output = vaultRunner.runInVault0(args[0]);
//			} else if(args.length == 2){
//				output = vaultRunner.runInVault0(args[0], args[1]);
			} else {
				printUsage();
				throw new VaultException("Too many arguments provided.");
			}
			System.out.println(output.getSysout());
		}catch(VaultException ve){
			ve.printStackTrace();
			printUsage();
			System.exit(1);
		}
	}

	private void runFromInput() throws VaultException {
		System.out.println("Enter your snippet code. When finished press Ctrl-D");
		System.out.println("For example:");
		System.out.println(
				"=================================================\n" +
				"int sum = 0;\n" +
				"for(int i = 0; i < 5; i++){\n" +
				"\tSystem.out.println(\"i: \" + i);\n" +
				"\tsum += i;\n" +
				"}\n" +
				"System.out.println(\"Sum: \" + sum);\n" +
				"<ENTER>\n" +
				"CTRL-D\n" +
				"=================================================\n"
		);

		Scanner scan = new Scanner(System.in);
		StringBuilder sb = new StringBuilder();
		try {
			while (scan.hasNextLine()){
				String line = scan.nextLine();
				sb.append(line);
			}
		} finally {
			scan.close();
		}

		String source = sb.toString();
		VaultOutput output = vaultRunner.runInVault0(source);

		System.out.println(output.getSysout());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JavaultRunner.class, args);
	}
	
	private void printUsage(){
		System.out.println("usage:");
		System.out.println("(OPTIONAL) Compile first: ./gradlew build");
		System.out.println("java " +
				"-Djava.security.manager=java.lang.SecurityManager -Djava.security.policy=all.policy " +
				"-jar javault-runner/build/libs/javault-runner-"+VERSION+".jar " +
				"\"int sum = 0;int product = 1;for(int i = 1; i <= 10; i++){" +
				"    System.out.println(\\\"i = \\\" + i);" +
				"    sum += i;" +
				"    product *= i;" +
				"}" +
				"System.out.println(\\\"sum = \\\" + sum);" +
				"System.out.println(\\\"product = \\\" + product);\"")
		;

	}

}