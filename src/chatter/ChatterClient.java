package chatter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

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
	
	public ChatterClient(int port) {
		this("localhost", port);
	}
	
	public ChatterClient(String ip, int port) {
		clientFrame = new ClientFrame(this);
		clientUser = new User();
		clientFrame.addNewUser(getUser());
	   
		serverIP = ip;
		serverPort = port;
		
		if(!connectToServer())
			disconnectFromServer();
	}

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
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error connecting to server input/output streams."));
			return false;
		}
		
		// start listen thread to constantly listen to server:
		new ServerListener().start();
		
		try {
			toServer.writeObject(clientUser);
		} catch (IOException e) {
			clientFrame.displayMessage(new Message("Error sending user information to server."));
			return false;
		} 
		
		//no errors:
		return true;
	}
	
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
	
//	private class messageReciever implements Runnable {
//		
//		@Override
//		public void run() {
//			try {
//				Message serverMessage = (Message)(fromServer.readObject());
//				clientFrame.displayMessage(serverMessage);
//			} catch (IOException e) {
//				System.err.println(e);
//			} catch (ClassNotFoundException e) {
//				System.err.print(e);
//			}
//		}
//	}
	
	private class messageSender implements Runnable {
		
		private Message messageToSend;
		
		public messageSender(Message message) {
			messageToSend = message;
		}
		
		@Override
		public void run() {
			try {	
				toServer.writeObject(messageToSend);
			} catch (IOException e) {
				Message errorMessage = new Message("Error sending message to " + messageToSend.getRecipient());
				clientFrame.displayMessage(errorMessage);
			} 
		}
	}
	
	public void recieveMessage() {
		try {
			fromServer = new ObjectInputStream(socket.getInputStream());
			if (fromServer != null) {
				Message serverMessage = (Message)(fromServer.readObject());
				if (null != serverMessage) {
					clientFrame.displayMessage(serverMessage);
					System.out.println("Recieved message: " + serverMessage);
				}
				else
					System.err.println("No Message recieved");
			}
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.print(e);
		}
	}
	
	public void sendMessage(String text, User recipient) {
		Message messageToSend = new Message(recipient, clientUser, text);
		Thread messageThread = new Thread(new messageSender(messageToSend));
		messageThread.start();
	}
	
	/**
	 * Thread class to constantly listen to the server
	 */
	public class ServerListener extends Thread {
		
		@Override
		public void run() {
			while (true) {
				try {
					Message messageRecieved = (Message)(fromServer.readObject());
					clientFrame.displayMessage(messageRecieved);
				} catch (ClassNotFoundException | IOException e) {
					clientFrame.displayMessage(new Message("Disconnected from server"));
					disconnectFromServer();
					break;
				} catch (ClassCastException e) {
					clientFrame.displayMessage(new Message("Error: unknown object recieved from server."));
				}
			}
		}
	}
}
