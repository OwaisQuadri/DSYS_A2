# DSYS_A2

Distributed Systems Assignment 2

This application is based on <a href="https://github.com/OwaisQuadri/DSYS_A1">Assignment 1</a> where the program was to be made using multithreading. In this assignment, Java RMI was used to connect the clients to the server’s functions to complete a certain task. My program is an academic testing system where a supervisor/teacher can hold multiple choice tests for a group of students. The same functions were implemented and some additional functionalities are also added for this assignment.

Instructions to Download:

1. Open the desired download location in the terminal.</li>
   Run command:

```
git clone https://github.com/OwaisQuadri/DSYS_A2
cd DSYS_A2
```

or

```
git clone https://github.com/UOITEngineering2/assignment2fall2020-OwaisQuadri/
cd assignment2fall2020-OwaisQuadri
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
- UN: "sanzir" , PW: "sanzir"
- UN: "taha" , PW: "taha"
