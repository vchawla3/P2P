# CSC 573 IP Project 1 - P2P-CI Server
Vaibhav Chawla (vchawla3)  
Shikhar Sharma (ssharm29)

### Compilation Instructions
`cd src/p2p`  
`javac *.java`

### Running the Program
* Server: `java Server`
  * No interaction, will just print the requests as they come into the server
* Client: `java Client [serverIP] [serverPort]`
  * Will ask for input on what the clients host and port is (to setup sockets for incoming peers)
  * Then a menu of 5 options will appear, user will select one by entering a value (1-5) and clicking 'enter'
    1. Add
    2. Lookup
    3. List
    4. Get
    5. Quit

### Assumptions 
* System runs java8
* The 'title' of RFC's will also be their file name where they are stored
* RFC's may have the same RFC Number, but different titles/filenames on the various clients


### ToDO
* p2p
  * Send file data back as response to peer
* p2s
  * Remove all instances of RFC/Peers from list when client closes connection