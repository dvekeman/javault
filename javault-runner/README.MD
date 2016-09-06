# Javault Runner

Execute the following commands from the root directory (javault):

    ./gradlew build
    java -Djava.security.manager=java.lang.SecurityManager -Djava.security.policy=all.policy -jar ./javault-runner/build/libs/javault-runner-0.0.1.jar "int sum = 0;int product = 1;for(int i = 1; i <= 10; i++){    System.out.println(\"i = \" + i);    sum += i;    product *= i;}System.out.println(\"sum = \" + sum);System.out.println(\"product = \" + product);"

Note
- Quotes need to be escaped