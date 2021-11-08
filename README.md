# DSYS_A1

Distributed Systems Assignment 2  

Continuation from Assignmnet 1 (which can be found <a href=https://github.com/OwaisQuadri/DSYS_A1>here</a>).  
  
Instructions to Download:  
  
1. Open the desired download location in the terminal.</li>
Run command:
```
git clone https://github.com/OwaisQuadri/DSYS_A2 
cd DSYS_A2
```
or  
```
git clone assignmentsubmissionURL
cd DSYS_A2
```
  
Instructions to Run:  
  
1. Open a new terminal in the "DSYS_A2" directory and run these commands to start a Supervisor session.
```
cd Server
start rmiregistry
java -Djava.security.policy=policy.txt Supervisor
```
for now, use the username "admin" and password "admin".  
  
2. Open a new terminal in "DSYS_A2" directory and run these commands to start a Student session.
```
cd Client
java Student
``` 
there are a few logins for students, please use one of the following pairs:  
- UN: "student" , PW: "student"
- UN: "owais" , PW: "owais"
- UN: "john" , PW: "john"
