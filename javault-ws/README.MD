# 1. Start the application

Spring boot normally allows you to run the application from gradle (```./gradlew :javault-ws:bootRun``` or something
equivalent). However because we rely on the SecurityManager this does not work. The application will start up but the
VaultRunner will complain

    "message":"org.javault.VaultException: Security manager is mandatory.

## Using gradle

This uses the cargo plugin which requires a local tomcat installation.

Update the gradle build script to point TOMCAT8_HOME to your tomcat8 installation location

    def TOMCAT8_HOME = "/opt/tomcat8"

Once that is done building the war, deploying it and starting the container is just one command:

    ./gradlew -i --stacktrace :javault-ws:cargoStartLocal

There might be errors in the log (for /manager, /host-manager, ...) but these can be ignored our javault-ws should start
just fine.

Once you are done

    ./gradlew -i --stacktrace :javault-ws:cargoStopLocal

## Using standalone tomcat

First create a war file

    ./gradlew war

Then deploy the war file in tomcat.

Note: by default tomcat uses the war file as context path. The examples below
assume the context path is ```javault-ws``` whereas the war file will be called ```javault-ws-<version>.war``` so
either adjust the URLs from below or change the war file name.

# 2. Run some tests

From the root directory (javault) run the following curls command as test:

## Provide a JAVA file as input

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runInVault0?name=HelloWorld -d @javault-ws/testfiles/HelloWorld.java

Some notes:
- We post the data as plain text (so we have to pass in the header)
- Class name is passed a request variable (name=HelloWorld)
- Source is passed from a sample file: javault-ws/testfiles/HelloWorld.java

Here is a second example:

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runInVault0?name=Counter -d @javault-ws/testfiles/Counter.java | python -m json.tool

Here is an example which fails because we try to read file:

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runInVault0?name=Pwd -d @javault-ws/testfiles/Pwd.java | python -m json.tool

## Provide a PLAIN TEXT (snippet) as input

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runSnippetInVault0 -d @javault-ws/testfiles/CounterSnippet.txt | python -m json.tool

Same as above but as oneliner

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runSnippetInVault0 -d 'int sum = 0;int product = 1;for(int i = 1; i <= 10; i++){    System.out.println("i = " + i);    sum += i;    product *= i;}System.out.println("sum = " + sum);System.out.println("product = " + product);'  | python -m json.tool

Code that doesn't compile

    curl -X POST -H"Content-Type:text/plain" http://localhost:9090/javault-ws/runSnippetInVault0 -d 'I do not compile!'  | python -m json.tool
