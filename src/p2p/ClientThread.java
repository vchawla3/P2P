package p2p;

import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	int port;

	public ClientThread(int port) {
       this.port = port;
	}

	public void run() {
		BufferedReader in;
		PrintStream pstream;
		try {
			ServerSocket p2psocket = new ServerSocket(port);
			Socket peersocket = p2psocket.accept();

			while (true) {
				//Manage requests from the user to the server

			}	
		} catch (IOException ex) {
				System.out.println("Could not accept server");
				System.out.println(ex.getMessage());
		}
	}
}