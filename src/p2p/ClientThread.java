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
		try {
			ServerSocket p2psocket = new ServerSocket(port);
			while (true) {
				//Manage requests from the peers
				Socket peersocket = p2psocket.accept();
				new Thread(new PeerThread(peersocket)).start();
			}
		} catch (IOException ex) {
				//System.out.println("Client Thread: Could not accept peers!!");
				//System.out.println(ex.getMessage());
		}
	}
}