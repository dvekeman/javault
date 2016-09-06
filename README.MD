# Javault

Interpret and run custom java code (source, class, jar) in a secure _vault_. The core vault asserts that 
the Security Manager is running and runs without any additional permissions.

[![Build Status](https://travis-ci.org/dvekeman/javault.svg?branch=master)](https://travis-ci.org/dvekeman/javault)
[![codecov](https://codecov.io/gh/dvekeman/javault/branch/master/graph/badge.svg)](https://codecov.io/gh/dvekeman/javault)

## Documentation

This codebase consists of a number of modules

- ```javault-compiler-api```: small api module to allow switching backend compilers
- ```javault-simple-compiler```: simple, naive implementation of the javault-compiler-api
- ```javault-core```: core logic to compile (if needed) and run classes, jars, snippets, ...
- ```javault-runner```: command line execution (see examples below)
- ```javault-ws```: web service (REST) wrapper to call the javault as a service

See README.MD inside 
[```javault-runner```](https://github.com/dvekeman/javault/blob/master/javault-runner/README.MD) and 
[```javault-ws```](https://github.com/dvekeman/javault/blob/master/javault-ws/README.MD)

### Examples

![Alt text](https://github.com/dvekeman/javault/blob/master/doc/img/javault-runner-example1.png "Command line usage (javault-runner)")

![Alt text](https://github.com/dvekeman/javault/blob/master/doc/img/javault-ws-example1.png "Web Service usage (javault-ws)")
