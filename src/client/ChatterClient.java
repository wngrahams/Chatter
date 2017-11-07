package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sun.corba.se.impl.oa.toa.TOA;

import chatter.Message;
import chatter.User;

public class ChatterClient implements Serializable {
	
	/**
	 * ChatterClient class consolidates the socket, ClientFrame, output stream, and input stream 
	 * for a new User that joins the server. 
	 */
	private static final long serialVersionUID = 836093546191030129L;
	private transient ClientFrame clientFrame;
	private User clientUser;
	
	private transient Socket socket; 
	private String serverIP;
	private int serverPort;
	
	private transient ObjectOutputStream toServer;
	private transient ObjectInputStream fromServer;

	
	public static void main(String[] args) {
		ChatterClient cc;
		if (args.length < 1)
			cc = new ChatterClient();
		else 
			cc = new ChatterClient(Integer.parseInt(args[1]));
	}
	
	public ChatterClient() {
		this("localhost", 0xFFFF);
	}
	
	/**
	 * ChatterClient constructor connecting to "localhost" server with port number "port".
	 */
	public ChatterClient(int port) {
		this("localhost", port);
	}
	
	/**
	 * ChatterClient constructor with perameters for receiving IP address and port number 
	 * of the host server. Creates a new <code>User</code> object and <code>ClientFrame</code> 
	 * object to be associated with this client. 
	 */
	public ChatterClient(String ip, int port) {
		clientUser = new User();
		clientFrame = new ClientFrame(this);
		clientFrame.addNewUser(getUser());
	   
		serverIP = ip;
		serverPort = port;
		
		if(!connectToServer())
			disconnectFromServer();
	}

	/**
	 * Runs in constructor, connects client to <code>ChatterServer</code>.
	 * Generates a connection message <code>Message</code> and displays it to <code>ClientFrame</code>.
	 */
	private boolean connectToServer() {
		try {
			clientFrame.displayMessage(new Message("Connecting to server: " + serverIP + " " + serverPort));
			socket = new Socket(serverIP, serverPort);
		} catch (UnknownHostException e) {
			clientFrame.displayMessage(new Message("IP " + serverIP + " could not be determined."));
			return false;
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error creating socket with host: " + serverIP + " " + serverPort));
			return false;
		} 
		
		clientFrame.displayMessage(new Message("Connection successful."));
		
		try {
			fromServer = new ObjectInputStream(socket.getInputStream());
			toServer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error connecting to server input/output streams."));
			return false;
		}
		
		// start listen thread to constantly listen to server:
		new ServerListener().start();
		
		try {
			Message logon = new Message(clientUser, Message.USER_LOGON_MESSAGE);
			toServer.writeUnshared(logon);
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error sending user information to server."));
			return false;
		} 
		
		//no errors:
		return true;
	}
	
	/**
	 * Method for disconnecting from <code>ChatterServer</code>. Called when <code>ClientFrame</code> is closed.
	 * Generates a disconnection message <code>Message</code> and displays it to <code>ClientFrame</code>.
	 */
	public void disconnectFromServer() {
		// disconnect from the server:
		try {
			if (fromServer != null) {
				fromServer.close();
				fromServer = null;
			}
			if (toServer != null) {
				toServer.close();
				toServer = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error disconnecting from server."));
		}
		
		clientFrame.displayMessage(new Message("Disonnected from server."));
	}
	
	public User getUser() {
		return clientUser;
	}
	
	/**
	 * Method for sending a <code>Message</code> from client to <code>ChatterServer</code>.
	 * Includes parameters for the text and recipient <code>User</code> the message is addressed to.
	 * Generates a new <code>Message</code> 
	 */
	public void sendMessage(String text, User recipient) {
		String[] splitString = text.split(" ", 2);
		Message messageToSend;
		
		if (splitString[0].equalsIgnoreCase("/nick")) {
			if (splitString.length > 1)
				messageToSend = new Message(User.SERVER, clientUser, splitString[1], Message.USER_NAME_MESSAGE);
			else {
				clientFrame.displayMessage(new Message("No nickname specified."));
				return;
			}
		}
		else 
			messageToSend = new Message(recipient, clientUser, text);
		
		Thread messageThread = new Thread(new messageSender(messageToSend));
		messageThread.start();
	}
	
	public void setUser(User u) {
		clientUser = u;
	}
	
	private class messageSender implements Runnable {
		
		private Message messageToSend;
		
		public messageSender(Message message) {
			messageToSend = message;
		}
		
		@Override
		public void run() {
			try {	
				toServer.writeUnshared(messageToSend);
			} catch (IOException e) {
				Message errorMessage = new Message("Error sending message to " + messageToSend.getRecipient());
				clientFrame.displayMessage(errorMessage);
			} catch (NullPointerException e) {
				clientFrame.displayMessage(new Message("Not connected to any server."));
			}
		}
	}
	
	/**
	 * Thread class to constantly listen to the server
	 */
	private class ServerListener extends Thread {
		
		@Override
		public void run() {
			while (true) {
				try {
					Message messageRecieved = (Message)(fromServer.readUnshared());
					clientFrame.displayMessage(messageRecieved);
				} catch (IOException e) {
					clientFrame.displayMessage(new Message("Server has shutdown."));
					disconnectFromServer();
					break;
				} catch (ClassCastException | ClassNotFoundException e) {
					clientFrame.displayMessage(new Message("Error: unknown object recieved from server."));
				}
			}
		}
	}
}
