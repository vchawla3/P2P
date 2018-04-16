import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
	static ArrayList<Peer> peers;
	static ArrayList<RFC> rfcs;

	static Socket csocket;
	Server(Socket csocket) {
		this.csocket = csocket;
	}

	public static void main(String args[]) {
		try {
			ServerSocket ssock = new ServerSocket(7734);
			System.out.println("Listening on port 7734");
			peers = new ArrayList<Peer>();
			rfcs = new ArrayList<RFC>();

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

	//Each client is now given this thread on the server
	public void run() {
		BufferedReader in;
		PrintStream pstream;
		int port = 0;
		String host = "";
		try {
			//DataInputStream dis= new DataInputStream(csocket.getInputStream());
			in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			pstream = new PrintStream(csocket.getOutputStream());

			host = in.readLine();
			port = Integer.parseInt(in.readLine());
			Peer p = new Peer(port, host);
			peers.add(p);

			while(true) {
				//switch on the command from the client
				String cmdLine = in.readLine();
				
				String cmd = cmdLine.split(" ")[0];
				// error checking here if cmd exists
				

				//each cmd uses host and port...
				//add host
				String hostLine = in.readLine();
				//add port
				String portLine = in.readLine();
				String titleLine, response;
				switch(cmd) 
				{
					case "ADD":
						//but only add/lookup use title
						//add title
						titleLine = in.readLine();
						response = handleAddRFC(cmdLine, hostLine, portLine, titleLine);
						
						//send the response to the peer
						pstream.println(response);
						break;
					
					case "LOOKUP":
						//but only add/lookup use title
						//add title
						titleLine = in.readLine();
						response = handleLookup(cmdLine, hostLine, portLine, titleLine);

						//send the response to the peer
						pstream.println(response);
						break;
					
					case "LIST":
						response = handleList(cmdLine, hostLine, portLine);
						
						//send the response to the peer
						pstream.println(response);
						break;

					default:
						//If it is not any of these commands then it is a bad request
						pstream.println(badRequest());
						break;
				}
			}
			//pstream.close();
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		catch (NullPointerException ex) {
			clientLeft(port, host);
		}
		finally {
			try {
				csocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
         			
		}
	}

	//Remove peer from peer list and also remove any rfcs stored for this peer
	public void clientLeft(int port, String host){
		for(int i = 0; i < peers.size(); i++){
			Peer peer = peers.get(i);
			if(peer.host.equals(host) && peer.port == port) {
				peers.remove(i);
			}
		}
		for(int i = 0; i < rfcs.size(); i++){
			RFC rfc = rfcs.get(i);
			if(rfc.host.equals(host)) {
				rfcs.remove(i);
			}
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
		String[] cmdsplit = cmdLine.split(" ");
		if (cmdsplit[3].equals("P2P-CI/1.0")) {
			type = Integer.parseInt(cmdsplit[2]);
		} else {
			return badRequest();
		}

		//Check the host line for errors
		String[] hostsplit = hostLine.split(" ");
		if (hostsplit[0].equals("Host:")) {
			host = hostsplit[1];
		} else {
			return badRequest();
		}

		//Check the port line for errors
		String[] postsplit = portLine.split(" ");
		if (postsplit[0].equals("Port:")) {
			port = postsplit[1];
		} else {
			
			return badRequest();
		}

		//Check the title line for errors
		String[] titlesplit = titleLine.split(": ");
		if (titlesplit[0].equals("Title")) {
			title = titlesplit[1];
		} else {
			return badRequest();
		}

		//No errors up to here, so now add the rfc to master index and send back 200 OK response
		RFC r = new RFC(type, title, host);
		rfcs.add(r);

		//append End Of Request so client knows this is the end
		return "P2P-CI/1.0 200 OK\n" + "RFC" + type + " " + title + " " + host + " " + port + "\nEOR" ;
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
		String[] cmdsplit = cmdLine.split(" ");
		if (cmdsplit[3].equals("P2P-CI/1.0")) {
			type = Integer.parseInt(cmdsplit[2]);
		} else {
			System.out.println("bad type");
			return badRequest();
		}

		//Check the host line for errors
		String[] hostsplit = hostLine.split(" ");
		if (hostsplit[0].equals("Host:")) {
			host = hostsplit[1];
		} else {
			System.out.println("bad host");
			return badRequest();
		}

		//Check the port line for errors
		String[] postsplit = portLine.split(" ");
		if (postsplit[0].equals("Port:")) {
			port = postsplit[1];
		} else {
			System.out.println("bad Port");
			return badRequest();
		}

		//Check the title line for errors
		String[] titlesplit = titleLine.split(": ");
		if (titlesplit[0].equals("Title")) {
			title = titlesplit[1];
		} else {
			System.out.println("bad title");
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
						response += "RFC " + rfc.type + " " + rfc.title + " " + peer.host + " " + peer.port + "\n";
					}
				}
				
			}
		}

		//append End Of Request so client knows this is the end
		return response + "EOR";
	}

	public String handleList(String cmdLine, String hostLine, String portLine)  {
		//request looks like...
		// LIST ALL P2P-CI/1.0
		// Host: thishost.csc.ncsu.edu
		// Port: 5678 
		String host, port;
		
		//Check the cmd line for errors
		String[] cmdsplit = cmdLine.split(" ");
		if (!cmdsplit[2].equals("P2P-CI/1.0")) {
			return badRequest();
		}

		//Check the host line for errors
		String[] hostsplit = hostLine.split(" ");
		if (hostsplit[0].equals("Host:")) {
			host = hostsplit[1];
		} else {
			return badRequest();
		}

		//Check the port line for errors
		String[] postsplit = portLine.split(" ");
		if (postsplit[0].equals("Port:")) {
			port = postsplit[1];
		} else {
			return badRequest();
		}

		String response = "P2P-CI/1.0 200 OK\n";
		//No errors so list all the RFC's in our list
		for (RFC rfc: rfcs) {
			for(Peer peer: peers) {
				if(rfc.host.equals(peer.host)) {
					response += "RFC " + rfc.type + " " + rfc.title + " " + peer.host + " " + peer.port + "\n";
				}
			}
		}

		//append End Of Request so client knows this is the end
		return response + "EOR";

	}

	public String badRequest() {
		return "P2P-CI/1.0 400 Bad Request\nEOR";
	}

	public String notFound() {
		return "P2P-CI/1.0 404 Not Found\nEOR";
	}

	public String versionNotSupported() {
		return "P2P-CI/1.0 505 P2P-CI Version Not Supported\nEOR";
	}

	
}