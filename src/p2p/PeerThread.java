import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PeerThread extends Thread {
	Socket psocket;

	public PeerThread(Socket psocket) {
       this.psocket = psocket;
	}

	public void run() {
		BufferedReader in;
		PrintStream pstream;
		try {
			in = new BufferedReader(new InputStreamReader(psocket.getInputStream()));
			pstream = new PrintStream(psocket.getOutputStream());
			
			// Read in the request from peer
			// Only cmd is a GET
			String cmd = in.readLine();
			String[] cmdsplit = cmd.split(" ");
			String host = in.readLine();
			String[] hostsplit = host.split(" ");
			String os = in.readLine();
			String[] ossplit = os.split(":");

			// error checking
			if (!cmdsplit[0].equals("GET") || !cmdsplit[1].equals("RFC") || !hostsplit[0].equals("HOST") || !ossplit[0].equals("OS")) {
				//send bad request the response to the peer
				pstream.println(badRequest());
			} else if (!cmdsplit[3].equals("P2P-CI/1.0")) {
				//send bad version the response to the peer
				pstream.println(versionNotSupported());
			}

			

		} catch (IOException ex) {
			System.out.println("Could not accept peers");
			System.out.println(ex.getMessage());
		}
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