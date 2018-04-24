import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;

public class Client {
	// Command Line arguments will include the server's host and port number
	public static void main(String args[]) {
		BufferedReader in;
		PrintStream pstream;
		Scanner scan;
		try {
			//First establish connection with Server and setup streams
			String serverhost = args[0];
			int serverport = Integer.parseInt(args[1]);
			Socket serversocket = new Socket(serverhost, serverport);
			in = new BufferedReader(new InputStreamReader(serversocket.getInputStream()));
			pstream = new PrintStream(serversocket.getOutputStream());
			System.out.println("Your Client Connected to Server");
			// Setup scanner for user input
			scan = new Scanner(System.in);

			//Now setup with Server, first pass host and port of yourself
			System.out.print("Enter your hostname: ");
			String host = scan.nextLine();
			System.out.print("Enter your port num (> 1024): ");
			String port = scan.nextLine();

			//Pass it to server
			pstream.println(host);
			pstream.println(port);

			//After successful connection, spawn thread to handle P2P incoming connections
			ClientThread r = new ClientThread(Integer.parseInt(port));
			new Thread(r).start();

			
			boolean loop = true;
			while (loop) {
				boolean wasNotGet = true;
				//remake string for most of the requests (add, lookup, and list)
				String request = " P2P-CI/1.0\n" + "Host: " + host + "\n" + "Port: " + port;

				//Manage requests from the user to the server, and user to other peers
				System.out.println("Choose one of the following commands (enter 1-5): ");
				System.out.println("1: ADD");
				System.out.println("2: LOOKUP ");
				System.out.println("3: LIST ");
				System.out.println("4: GET ");
				System.out.println("5: QUIT ");

				int cmd = Integer.parseInt(scan.nextLine());
				String RFCnum = "";
				String RFCtitle = "";
				switch(cmd) {
					case 1:
						//Add command
						System.out.print("Enter RFC Number to Add to Server: ");
						RFCnum = scan.nextLine();

						System.out.print("Enter RFC Title to Add to Server: ");
						RFCtitle = scan.nextLine();

						request = "ADD RFC " + RFCnum + request + "\n" + "Title: " + RFCtitle;
						pstream.println(request);
						break;
					case 2:
						// Lookup command
						System.out.print("Enter RFC Number to Lookup on Server: ");
						RFCnum = scan.nextLine();

						System.out.print("Enter RFC Title to Lookup on Server: ");
						RFCtitle = scan.nextLine();

						request = "LOOKUP RFC " + RFCnum + request + "\n" + "Title: " + RFCtitle;
						pstream.println(request);
						break;
					case 3:
						// List command
						request = "LIST ALL" + request;
						pstream.println(request);
						break;
					case 4:
						// Get command
						System.out.print("Enter RFC Number to download from peer: ");
						RFCnum = scan.nextLine();

						System.out.print("Enter Host of the peer: ");
						String peerHost = scan.nextLine();

						System.out.print("Enter Port of the peer: ");
						String peerPort = scan.nextLine();

						//Get OS
						String os = System.getProperty("os.name");

						request = "GET RFC " + RFCnum + " P2P-CI/1.0\nHost: " + peerHost + "\nOS: " + os;
						handleRequestToPeer(request, peerHost, peerPort, RFCnum);

						//set to false so input buffer does keep reading in below
						wasNotGet = false;
						break;
					case 5:
						// Leave!!!!
						loop = false;
						break;
				}

				if (loop && wasNotGet) {
					//Print the entire response!!
					String response;
					while (!(response = in.readLine()).equals("EOR")) {
					   System.out.println(response);
					}					
					System.out.println();
				}
			}
			System.out.println("Goodbye");	
			scan.close();
			in.close();
			pstream.close();
			//serversocket.close();
			System.exit(0);
		} catch (IOException ex) {
				System.out.println("Could not create server connection");
				System.out.println(ex.getMessage());
		}
	}


	public static void handleRequestToPeer(String request, String peerHost, String peerPort, String RFCnum) {
		try {
			//Open socket to this peer
			Socket peersocket = new Socket(peerHost, Integer.parseInt(peerPort));
			BufferedReader peerin = new BufferedReader(new InputStreamReader(peersocket.getInputStream()));
			PrintStream peerpstream = new PrintStream(peersocket.getOutputStream());
			
			//Send request
			peerpstream.println(request);

			//Get Response that looks like...
			// P2P-CI/1.0 200 OK
			// Date: Wed, 12 Feb 2009 15:12:05 GMT
			// OS: Mac OS 10.2.1
			// Last-Modified: Thu, 21 Jan 2001 9:23:46 GMT
			// Content-Length: 12345
			// Content-Type: text/text
			// (data data data ...) 
			String data = peerin.readLine();
			if (data.split(" ")[1].equals("200")) {
				System.out.println(peerin.readLine());
				System.out.println(peerin.readLine());
				System.out.println(peerin.readLine());
				System.out.println(peerin.readLine());
				System.out.println(peerin.readLine());

				//write the file!!
				String newTitle = "rfc" + RFCnum + ".txt";
				File rfcFile = new File(newTitle);
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rfcFile)));
				String i = peerin.readLine();
				//special indicator which RFC file would not have
				while(!i.equals("EOR")) {
					out.println(i);
					i = peerin.readLine();
				}
				out.close();
				//System.out.println("RFC File Downloaded!");
				System.out.println();
			} else {
				//bad response, print the error
				System.out.print(data);
			}

			//Close the peersocket stuff
			peerin.close();
			peerpstream.close();
			peersocket.close();
		} catch (IOException ex) {
			System.out.println("Could not create peer2peer connection to get RFC");
			System.out.println(ex.getMessage());
		}
		return;
	}
}