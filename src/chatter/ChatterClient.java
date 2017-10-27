package chatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;

import client.ClientFrame;
import client.User;

public class ChatterClient implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 836093546191030129L;
	private transient ClientFrame clientFrame;
	private User clientUser;
	
	private transient Socket socket;
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
		clientFrame.addNewUser(getUser());
		clientFrame.addNewUser(new User("graham", "localhost"));
		clientFrame.addNewUser(new User("Jim", "localhost"));
	   
		serverIP = ip;
		serverPort = port;
		
		connectToServer();
	}

	private void connectToServer() {
		try {
			socket = new Socket(serverIP, serverPort);
			System.out.println("Connecting to server...");
			ObjectOutputStream userToServer = new ObjectOutputStream(socket.getOutputStream());
			userToServer.writeObject(this);
			System.out.println("after write object");
		} catch (IOException e) {
			System.err.println("Failed to connect to server.");
			System.err.println(e);
		} catch (NullPointerException n) {
			System.out.println("???");
		}
	}
	
	public User getUser() {
		return clientUser;
	}
	
	public void sendToServer(String text, User recipient) {
		try {
			System.out.println("insdie client send to server");
			ObjectOutputStream userToServer = new ObjectOutputStream(socket.getOutputStream());
			Message message = new Message(recipient, clientUser, text);
			userToServer.writeObject(message);
		} catch (IOException e) {
			System.out.println("Error sending message to " + recipient);
			System.err.println(e);
		} catch (NullPointerException n) {
			System.out.println("???");
		}
	}
}
