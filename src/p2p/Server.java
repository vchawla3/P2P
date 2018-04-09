package p2p;

import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
	static Socket csocket;
	Server(Socket csocket) {
		this.csocket = csocket;
	}


	public static void main(String args[]) {
		try {
			ServerSocket ssock = new ServerSocket(7734);
			System.out.println("Listening on port 7731");

			while (true) {
				Socket sock = ssock.accept();
				System.out.println("Client Connected");
				new Thread(new Server(sock)).start();
			}	
		} catch (IOException ex) {
				System.out.println("Could not accept client");
				System.out.println(ex.getMessage());
		}	
	}

	static ArrayList<Peer> peers;
	static ArrayList<RFC> rfcs;

	//Each client is now given this thread on the server
	public void run() {
		try {
			//DataInputStream dis= new DataInputStream(csocket.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			PrintStream pstream = new PrintStream(csocket.getOutputStream())

			String host = in.readLine();
			int port = Integer.parseInt(in.readLine());
			Peer p = new Peer(port, host);
			peers.add(p);

			while(true) {
				//switch on the command from the client
				String cmdLine = in.readLine();

				System.out.println(cmdLine);
				
				String cmd = cmdLine.split(" ")[0];
				// error checking here if cmd exists
				

				//each cmd uses host and port...
				//add host
				String hostLine = in.readLine();
				//add port
				String portLine = in.readLine();

				switch(cmd) 
				{
					case "ADD":
						//but only add/lookup use title
						//add title
						String titleLine = in.readLine();
						String response = handleAddRFC(cmdLine, hostLine, portLine, titleLine);
						
						//send the response to the peer
						pstream.println(response);
						break;
					
					case "LOOKUP":
						//but only add/lookup use title
						//add title
						String titleLine = in.readLine();
						String response = handleLookup(cmdLine, hostLine, portLine, titleLine);;

						//send the response to the peer
						pstream.println(response);
						break;
					
					case "LIST":
						String response = handleList(fullreq)
						//send the response to the peer
						pstream.println(response);
						break;

					default:
						//If it is not any of these commands then it is a bad request
						pstream.println(badRequest());
						break;
				}
			}
			pstream.close();
         	csocket.close();
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public String handleAddRFC(String cmdLine, String hostLine, String portLine, String titleLine) {
		// request looks like...
		// ADD RFC 123 P2P-CI/1.0
		// Host: thishost.csc.ncsu.edu
		// Port: 5678
		// Title: A Proferred Official ICP
		String host, title, port;
		int type;

		//Check the cmd line for errors
		String cmdsplit = cmdLine.split(" ");
		if (cmdsplit[3].equals("P2P-CI/1.0")) {
			type = Integer.parseInt(cmdsplit[2]);
		} else {
			return badRequest();
		}

		//Check the host line for errors
		String hostsplit = hostLine.split(" ");
		if (hostsplit[0].equals("Host:")) {
			host = hostsplit[1];
		} else {
			return badRequest();
		}

		//Check the port line for errors
		String postsplit = portLine.split(" ");
		if (postsplit[0].equals("Port:")) {
			port = postsplit[1];
		} else {
			return badRequest();
		}

		//Check the title line for errors
		String titlesplit = titleLine.split(": ");
		if (titlesplit[0].equals("Title")) {
			title = titlesplit[1];
		} else {
			return badRequest();
		}

		//No errors up to here, so now add the rfc to master index and send back 200 OK response
		RFC r = new RFC(type, title, host);
		rfcs.add(r);
		return "P2P-CI/1.0 200 OK\n" + "RFC" + type + " " + title + " " + host + " " + port + "\nEOF"
	}

	public String handleLookup(String cmdLine, String hostLine, String portLine, String titleLine) {
		//request looks like...
		//LOOKUP RFC 3457 P2P-CI/1.0
		//Host: thishost.csc.ncsu.edu
		//Port: 5678
		//Title: Requirements for IPsec Remote Access Scenarios 
		String host, title, port;
		int type;

		//Check the cmd line for errors
		String cmdsplit = cmdLine.split(" ");
		if (cmdsplit[3].equals("P2P-CI/1.0")) {
			type = Integer.parseInt(cmdsplit[2]);
		} else {
			return badRequest();
		}

		//Check the host line for errors
		String hostsplit = hostLine.split(" ");
		if (hostsplit[0].equals("Host:")) {
			host = hostsplit[1];
		} else {
			return badRequest();
		}

		//Check the port line for errors
		String postsplit = portLine.split(" ");
		if (postsplit[0].equals("Port:")) {
			port = postsplit[1];
		} else {
			return badRequest();
		}

		//Check the title line for errors
		String titlesplit = titleLine.split(": ");
		if (titlesplit[0].equals("Title")) {
			title = titlesplit[1];
		} else {
			return badRequest();
		}

		String response = "P2P-CI/1.0 200 OK\n";
		//No errors so now look for Peers's with the same RFC num and Title
		for (RFC rfc: rfcs) {
		//for each RFC, is it the same one from the request
			if (rfc.type == type && rfc.title.equals(title)) {
				//for the correct, RFC, which peers have it!!
				for(Peer peer: peers) {
					if(rfc.host.equals(peer.host)) {
						response += "RFC " + rfc.num + " " + rfc.title + " " + peer.hostname + " " + peer.port + "\n";
					}
				}
				
			}
		}
		return response;
	}

	public String handleList(String req) {

	}

	public String badRequest() {
		return "P2P-CI/1.0 400 Bad Request";
	}

	public String notFound() {
		return "P2P-CI/1.0 404 Not Found";
	}

	public String versionNotSupported() {
		return "P2P-CI/1.0 505 P2P-CI Version Not Supported";
	}

	
}