package chatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import client.ClientFrame;
import client.User;

public class ChatterClient {
	
	private ClientFrame clientFrame;
	private User clientUser;
	
	private Socket socket;
	private String serverIP;
	private int serverPort;

	public static void main(String[] args) {
		if (args.length < 1) {
			ChatterClient cc = new ChatterClient();
		}
		else {
			ChatterClient cc = new ChatterClient(Integer.parseInt(args[1]));
		}
	}
	
	public ChatterClient() {
		this("localhost", 0xFFFF);
	}
	
	public ChatterClient(int port) {
		this("localhost", port);
	}
	
	public ChatterClient(String ip, int port) {
		clientFrame = new ClientFrame(this);
		clientUser = new User();
	   
		serverIP = ip;
		serverPort = port;
		
		connectToServer();
	}

	private void connectToServer() {
		try {
			socket = new Socket(serverIP, serverPort);
			System.out.println("Connecting to server...");
		} catch (IOException e) {
			System.err.println("Failed to connect to server.");
			System.err.println(e);
		}
	}
	
	public User getUser() {
		return clientUser;
	}
}
