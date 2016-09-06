# 1. Start the application

Don't forget to run with the Security Manager enabled (if not then everything will fail in the VaultRunner)

    -Djava.security.policy=all.policy -Djava.security.manager=java.lang.SecurityManager

There are several ways to run the Application:

- IDE: create a run configuration and make sure to set the security manager
- jar:

    ./gradlew :javault-ws:build
    # Run in UNSECURE mode (everything will fail)
    ./gradlew :javault-ws:build && java -jar -Dspring.profiles.active=unsecure ./javault-ws/build/libs/javault-ws-0.1.0.jar
    # Run in SECURE mode
    ./gradlew :javault-ws:build && java -jar -Dspring.profiles.active=secure ./javault-ws/build/libs/javault-ws-0.1.0.jar

# 2. Run some tests

From the root directory (javault) run the following curls command as test:

## Provide a JAVA file as input

    curl -X POST -H"Content-Type:text/plain" http://localhost:9000/runInVault0?name=HelloWorld -d @javault-ws/testfiles/HelloWorld.java

Some notes:
- We post the data as plain text (so we have to pass in the header)
- Class name is passed a request variable (name=HelloWorld)
- Source is passed from a sample file: javault-ws/testfiles/HelloWorld.java

Here is a second example: 

    curl -X POST -H"Content-Type:text/plain" http://localhost:9000/runInVault0?name=Counter -d @javault-ws/testfiles/Counter.java
    
Here is an example which fails because we try to read file: 

    curl -X POST -H"Content-Type:text/plain" http://localhost:9000/runInVault0?name=Pwd -d @javault-ws/testfiles/Pwd.java

## Provide a PLAIN TEXT (script) as input

    curl -X POST -H"Content-Type:text/plain" http://localhost:9000/runScriptInVault0 -d @javault-ws/testfiles/CounterScript.txt

Same as above but as oneliner

    curl -X POST -H"Content-Type:text/plain" http://localhost:9000/runScriptInVault0 -d 'int sum = 0;int product = 1;for(int i = 1; i <= 10; i++){    System.out.println("i = " + i);    sum += i;    product *= i;}System.out.println("sum = " + sum);System.out.println("product = " + product);'
