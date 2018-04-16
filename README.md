# CSC 573 IP Project 1 - P2P-CI System
Vaibhav Chawla (vchawla3)  
Shikhar Sharma (ssharm29)

### Compilation Instructions
`cd src/p2p`  
`javac *.java`

### Running the Program
* Server: `java Server`
  * No interaction, will just print the requests as they come into the server
  * Runs the server on localhost
* Client: `java Client [serverIP] [serverPort]`
  * Then the program will ask for input on what the clients host and port is (to setup sockets for incoming peers)
  * Then a menu of 5 options will appear, user will select one by entering a value (1-5) and clicking 'enter'
    1. Add
      * Will ask for rfc number and title to add to CI on the server
    2. Lookup
      * Will ask for rfc number and title to search the CI on the server and return corresponding entry if found
    3. List
      * Will simply return all rfcs stored on CI server
    4. Get
      * Will ask for rfc number to download, and the host/port of the peer who has it. Will download the txt file and output 'RFC File Downloaded!' once completed
    5. Quit
      * Will exit the program

### Assumptions 
* System runs java8
* The filename of RFC's will be in the format rfc[RFC Number].txt
* The client will have to manually 'ADD' their rfcs to the server when they first start the program
* RFC text files are in the same directory as the client
