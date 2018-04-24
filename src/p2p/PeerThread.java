import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			if (!cmdsplit[0].equals("GET") || !cmdsplit[1].equals("RFC") || !hostsplit[0].equals("Host:") || !ossplit[0].equals("OS")) {
				//send bad request the response to the peer
				pstream.println(badRequest());
			} else if (!cmdsplit[3].equals("P2P-CI/1.0")) {
				//send bad version the response to the peer
				pstream.println(versionNotSupported());
			}


			String RFCnum = cmdsplit[2];
			String filename = "rfc" + RFCnum + ".txt";
			

			//write file data while the file exists
			try {
				File rfcFile = new File(filename);
				BufferedReader br = new BufferedReader(new FileReader(rfcFile));
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date date = new Date();

				// Add appropriate headers
				pstream.println("P2P-CI/1.0 200 OK");
				pstream.println("Date: " + sdf.format(date));
				pstream.println("OS: " + System.getProperty("os.name"));
				pstream.println("Last-Modified: " + sdf.format(rfcFile.lastModified()));
				pstream.println("Content-Length: " + rfcFile.length());
				pstream.println("Content-Type: text/text");
				for(String line; (line = br.readLine()) != null; ) {
					System.out.println(line);
        			pstream.println(line);
    			}
				br.close();
				//special indicator which RFC file would not have
				pstream.println("EOR");
				pstream.close();
			} catch (FileNotFoundException ex) {
				//System.out.println("Could not Find the requested file!");
				//System.out.println(ex.getMessage());
				pstream.println(notFound());
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