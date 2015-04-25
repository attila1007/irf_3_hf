HungryElephant
==============

Description:
  Sample home work application for using code instrumentation, logging, etc.

Author:
  Lajos Cseppentő (using the work of Douglas Crockford (package org.json))
  Intelligent System Management course (VIMIA370)
  https://www.inf.mit.bme.hu/edu/bsc/irf

History
  Last updated: 2015.04.23.

Usage:
  0. Requirements: at least Java 1.8
  1. Sample config file is attached (config.xml)
  2. Running the server application:
        java -jar HungryElephant.jar <port> <configxml>
		or use run.sh / run.bat
  3. Connecting with a client:
        You can use either a telnet client, curl or a REST client
  
Development
  0. Please do not use Java 1.6 or Java 1.7!
  1. It is advised to use the Eclipse IDE with debugger (or an identical one, such as NetBeans or IntelliJ IDEA)
  2. Always read the comments, messages and exceptions carefully
  3. In order to avoid conflicts, the model should only used in a synchronized(model) block when the server is running
  4. The source code should be free of warnings
  5. REST Client for testing: google "rest client [put your browser name here]" or use custom Powershell/Python3 scripts

Sample usage: see http://localhost:8082/page/help