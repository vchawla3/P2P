# CSC 573 IP Project 1 - P2P-CI Server
Vaibhav Chawla (vchawla3)  
Shikar Sharma (ssharm29)

### Compilation Instructions
`cd src/p2p`  
`javac *.java`

### Running the Program
* Server: `java Server`
  * No interaction, will just print the requests as they come into the server
* Client: `java Client [serverIP] [serverPort]`
  * Will ask for input on what the clients host and port is (to setup sockets for incoming peers)
  * Then a menu of 5 options will appear
    1. Add
    2. Lookup
    3. List
    4. Get
    5. Quit