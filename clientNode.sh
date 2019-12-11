#!/bin/sh

sudo apt update  

sudo apt install default-jdk  

sudo wget https://github.com/mmao95/CS655-GENIMINI/blob/master/Client.java

sudo javac Client.java

sudo java Client
