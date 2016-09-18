# Javault

Interpret and run custom java code (source, class, jar) in a secure _vault_. The core vault asserts that
the Security Manager is running and runs without any additional permissions.

[![Build Status](https://travis-ci.org/dvekeman/javault.svg?branch=master)](https://travis-ci.org/dvekeman/javault)
[![codecov](https://codecov.io/gh/dvekeman/javault/branch/master/graph/badge.svg)](https://codecov.io/gh/dvekeman/javault)
[![Documentation Status](https://readthedocs.org/projects/javault/badge/?version=latest)](http://javault.readthedocs.io/en/latest/?badge=latest)

## Documentation

This codebase consists of a number of modules

- ```javault-compiler-api```: small api module to allow switching backend compilers
- ```javault-simple-compiler```: simple, naive implementation of the javault-compiler-api
- ```javault-core```: core logic to compile (if needed) and run classes, jars, snippets, ...
- ```javault-runner```: command line execution (see examples below)
- ```javault-ws```: web service (REST) wrapper to call the javault as a service

Main features

Javault Core...
- ... does not accept *anything* if code is not running with the SecurityManager
- ... by default does not grant any permissions to executing code
- ... can compile and run java source files if needed (input is correct .java file)
- ... can compile and run snippets (input is any plaintext with correct java statements)
- ... can run class files (from a provided directory)
- ... can - not yet - run jar files

See README.MD inside
[```javault-runner```](https://github.com/dvekeman/javault/blob/master/javault-runner/README.MD) and
[```javault-ws```](https://github.com/dvekeman/javault/blob/master/javault-ws/README.MD)

### Examples

#### Javault Runner

Command line wrapper to _demonstrate_ the javault-core.

##### Example 1: Run a snippet using the command line runner

![Command line usage](https://github.com/dvekeman/javault/blob/master/doc/img/javault-runner-example1.png "Command line usage (javault-runner)")

#### Javault Web Service

REST Web Service to _demonstrate_ the javault-core. Note this outputs JSON. For now the content of the json string includes line breaks as '\n' (depending on the system you ran the code on).

##### Example 1: Just a hello world

![Web Service usage](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example1.png "Web Service usage (javault-ws)")

##### Example 2: Counter snippet

![Web Service usage](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example2.png "Web Service usage (javault-ws)")

##### Example 3: Run code that breaches the security (e.g. listing the files in the directory).

![Web Service usage](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example3.png "Web Service usage (javault-ws)")

##### Example 4: Plain text as input

![Web Service compilation exception](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example4.png "Web Service usage with compilation exception (javault-ws)")

##### Example 5: Same but as oneliner

![Web Service compilation exception](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example5.png "Web Service usage with compilation exception (javault-ws)")

##### Example 6: Code that doesn't compile

![Web Service compilation exception](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example6.png "Web Service usage with compilation exception (javault-ws)")
