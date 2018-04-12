package p2p;

import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread implements Thread {
	int port;
	public MyRunnable(String port) {
       this.port = Integer.parseInt(port);
	}

	public void run() {
		BufferedReader in;
		PrintStream pstream;
		Scanner scan;
		try {
			Socket p2psocket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(csocserversocketket.getInputStream()));
			pstream = new PrintStream(serversocket.getOutputStream());
			System.out.println("Your Client Connected to Server");
			// Setup scanner for user input
			scan = new Scanner(System.in);

			//Now as setup with Server, first pass host and port of yourself
			System.out.print("Enter your hostname: ");
			String host = scan.nextLine();
			System.out.print("Enter your port num: ");
			String port = scan.nextLine();

			//Pass it to server
			pstream.println(host);
			pstream.println(port);

			//After successful connection, spawn thread for P2P incoming connections
			new Thread(new ClientThread(sock)).start();

			while (true) {
				//Manage requests from the user to the server

			}	
		} catch (IOException ex) {
				System.out.println("Could not accept server");
				System.out.println(ex.getMessage());
		}
	}
}