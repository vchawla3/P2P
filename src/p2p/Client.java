package p2p;

import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

	// Command Line arguments will include the server's host and port number
	public static void main(String args[]) {
		BufferedReader in;
		PrintStream pstream;
		Scanner scan;
		try {
			//First establish connection with Server and setup streams
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			Socket serversocket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(csocserversocketket.getInputStream()));
			pstream = new PrintStream(serversocket.getOutputStream());
			System.out.println("Your Client Connected to Server");
			// Setup scanner for user input
			scan = new Scanner(System.in);

			//Now setup with Server, first pass host and port of yourself
			System.out.print("Enter your hostname: ");
			String host = scan.nextLine();
			System.out.print("Enter your port num: ");
			String port = scan.nextLine();

			//Pass it to server
			pstream.println(host);
			pstream.println(port);

			//After successful connection, spawn thread to handle P2P incoming connections
			ClientThread r = new ClientThread(port);
			new Thread(r).start();

			
			boolean loop = true;
			while (loop) {
				//remake string for the request
				String request = " P2P-CI/1.0\n" + "Host: " + host + "\n" + "Port: " + port + "\n";

				//Manage requests from the user to the server, and user to other peers
				System.out.println("Choose one of the following commands: ");
				System.out.println("1: ADD");
				System.out.println("2: LOOKUP ");
				System.out.println("3: LIST ");
				System.out.println("4: GET ");
				System.out.println("5: QUIT ");

				String cmd = scan.nextInt();
				String RFCnum = "";
				String RFCtitle = "";
				switch(cmd) {
					case 1:
						//Add command
						System.out.print("Enter RFC Number to Add to Server: ");
						RFCnum = scan.nextLine();
						System.out.print("Enter RFC Title to Add to Server: ");
						RFCtitle = scan.nextLine();

						request = "ADD RFC " + RFCnum + request + "Title: " + RFCtitle;
						pstream.println(request)
						break;
					case 2:
						// Lookup command
						System.out.print("Enter RFC Number to Lookup on Server: ");
						RFCnum = scan.nextLine();
						System.out.print("Enter RFC Title to Lookup on Server: ");
						RFCtitle = scan.nextLine();

						request = "LOOKUP RFC " + RFCnum + request + "Title: " + RFCtitle;
						pstream.println(request)
						break;
					case 3:
						// List command
						request = "LIST ALL " + RFCnum + request;
						pstream.println(request)
						break;
					case 4:
						// Get command
						break;
					case 5:
						// Leave!!!!
						loop = false;
						break;
				}

				if (loop) {
					//Print the entire response!!
					String response;
					while ((response = in.readLine()) != null) {
					   System.out.println(response);
					}					
				}
			}
			System.out.println("Goodbye");	
			scan.close();
			in.close();
			pstream.close();
			serversocket.close();
		} catch (IOException ex) {
				System.out.println("Could not create server connection");
				System.out.println(ex.getMessage());
		}
	}

	public void run() {


	}
}