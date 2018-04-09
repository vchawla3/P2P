import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
	Socket csocket;
	Server(Socket csocket) {
		this.csocket = csocket;
	}


	public static void main(String args[]) {
		ServerSocket ssock = new ServerSocket(7734);
		System.out.println("Listening on port 7731");

		while (true) {
			Socket sock = ssock.accept();
			System.out.println("Client Connected");
			new Thread(new Server(sock)).start();
		}		
	}

	ArrayList<Peer> peers;
	ArrayList<RFC> rfcs;
	public void run() {

	}
	
}