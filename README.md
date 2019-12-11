# CS655-GENIMINI

This project is one of GENI projects in BU CS655: Computer Networks.

## Introduction  
The goal of this project is to create a distributed system where a user submits the md5 hash of a 5-character password (A-Z, a-z) to the system using a web interface. The web interface with the help of worker nodes cracks the password by a brute force approach. Our password cracker system has a server which is responsible for dividing the tasks to different parts and client which is able to brute force a specific task. We also used the serverless methodology to simplify the client-side work and make our program easier to use.  

## Procedure  
Our Password Cracker includes two source files: the server and the worker client.  
The server /*TO DO*/  
The clients would try to connect to the server once begin to work using the sockets API to send UDP packets. When they receive a range from the server, they start cracking the password within this range. Firstly, they convert each number within the decimal range to a 52-base number that consists of only lowercase and uppercase letters (A-Z, a-z). Then they traverse the range and check if the password is in it, once they find the correct password, a report will be sent to the server and the client will be interrupted. Otherwise, they should attempt to connect to the server again automatically and repeat above work until find the password.  
