---
layout: default
title: Lab 4 - Insecure Logging
---

## Lab

Many developers log information to the android log.  Sometimes sensitive data as well.   To inspect the android emulators log files you have to run the command:

`adb logcat`

![logcat](img/4_adblogcat.png)

The command will trail the log file allowing for you to see the output of any applications logging to the console.

In this lab, run ExploitMeMobile and execute a transfer of funds within the application or just run through the application in general.  

Observe what information is passed within the log file as you execute different screens in the application itself.

![view count](img/4_viewaccount.png)

Visit the account screen and observe the logcat output.

![view console](img/44_viewconsolelog.png)

You can see that the account information is being logged to the logcat file.

## Solution
