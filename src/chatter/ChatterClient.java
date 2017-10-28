package chatter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		clientFrame.addNewUser(new User("graham", "localhost"));
		clientFrame.addNewUser(new User("Jim", "localhost"));
	   
		serverIP = ip;
		serverPort = port;
		
		connectToServer();
		System.out.println("Starting to listen");
		while(null != clientFrame) {
			System.out.println("client listening");
			recieveMessage();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void connectToServer() {
		try {
			socket = new Socket(serverIP, serverPort);
			System.out.println("Connecting to server...");
			
			toServer = new ObjectOutputStream(socket.getOutputStream());
			
			toServer.writeObject(this);
			toServer.flush();
			System.out.println("after write object");
		} catch (IOException e) {
			System.err.println("Failed to connect to server.");
			System.err.println(e);
		} 
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
				System.out.println("Sending message: " + messageToSend);
				toServer.flush();
			} catch (IOException e) {
				Message errorMessage = new Message(clientUser, null, "Error sending message to " + messageToSend.getRecipient());
				clientFrame.displayMessage(errorMessage);
//				System.err.println(e);
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
}
