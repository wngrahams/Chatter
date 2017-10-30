package chatter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import server.ServerFrame;

import chatter.ChatterClient;
import client.User;
import chatter.Message;

public class ChatterServer {

	private int port = 0xFFFF;
	private boolean keepGoing;

	private Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	private Map<User,ChatterThread> threadMap = new HashMap<User, ChatterThread>();
	private ArrayList<ChatterThread> threadList;
	
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatterServer cs = new ChatterServer();
	}
	
	public ChatterServer() {
		this(0xFFFF);
	}
	
	public ChatterServer(int port)
	{
		this.port = port;
		threadList = new ArrayList<ChatterThread>();
		startServer();
	}
	
	private void getAllUsers(ChatterThread reciever) {
		synchronized(threadList) {
			for (int i=0; i<threadList.size(); i++) {
				if (threadList.get(i) != reciever) {
					ChatterThread otherClient = threadList.get(i);
					reciever.sendMessage(new Message(otherClient.clientUser, Message.USER_MESSAGE));
				}
			}
		}
	}
	
	private void removeClientFromList(ChatterThread client) {
		synchronized(threadList) {
			threadList.remove(this);
		}
	}
	
	private void sendMessageToClient(Message m) {
		synchronized(threadList) {
			if (m.getRecipient() == User.SERVER) {
				// Send message to all clients
				for (int i=0; i<threadList.size(); i++) {
					ChatterThread clientRecipient = threadList.get(i);
					
					if (!clientRecipient.sendMessage(m)) {
						threadList.remove(i);
						// TODO: also remove from hashmap
						i--;
					}
				}
			}
			else {
				// TODO: use hashmap to send private message
				System.out.println("Sending private message to " + m.getRecipient());
			}
		}
	}
	
	private void startServer() {
		keepGoing = true;
				
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while (keepGoing) {
				System.out.println("Waiting for clients...");
				
				try {
					Socket clientSocket = serverSocket.accept();
					
					if (!keepGoing)
						break;
					
					ChatterThread newClient = new ChatterThread(clientSocket);
					threadList.add(newClient);
					newClient.start();
				} catch (IOException e) {
					System.out.println("Error connecting to client.");
				} 
			}
			
			try {
				serverSocket.close();
				for (int i=0; i<threadList.size(); i++) {
					ChatterThread toClose = threadList.get(i);
					toClose.close();
				}
			} catch (IOException e) {
				System.out.println("Error closing server socket");
			}
		} catch (IOException e) {
			System.out.println("Error creating server socket at port: " + port);
			keepGoing = false;
		}
		
		
	}
	
	private class ChatterThread extends Thread {
		
		private Socket clientSocket;
		private User clientUser;
		
		private ObjectOutputStream send;
		private ObjectInputStream recieve;
		
		private Message clientMessage;
		
		public ChatterThread(Socket s) {
			clientSocket = s;
			
			Message userMessage = new Message();
			
			try {
				send = new ObjectOutputStream(clientSocket.getOutputStream());
				recieve = new ObjectInputStream(clientSocket.getInputStream());
								
				userMessage = (Message)(recieve.readObject());
				clientUser = userMessage.getSender();
				
				System.out.println(clientUser + " connected");
			} catch (IOException e) {
				System.out.println("Error connecting client Input/output stream");
				return;
			} catch (ClassNotFoundException e) {
				System.out.println("Unknown object recieved from " + clientUser);
				return;
			} 
			
			
			sendMessageToClient(userMessage);
			getAllUsers(this);
		}
		
		protected void close() {
			if (send != null) {
				try {
					send.close();
				} catch (IOException e) {
					System.out.println("Error disconnecting from client output stream.");
				}
			}
			if (recieve != null) {
				try {
					recieve.close();
				} catch (IOException e) {
					System.out.println("Error disconnecting from client input stream.");
				}
			}
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					System.out.println("Error disconnecting from client socket.");
				}
			}
		}
		
		public void run() {
			boolean clientRun = true;
			while (clientRun) {
				try {
					clientMessage = (Message)(recieve.readObject());
				} catch (ClassNotFoundException e) {
					System.out.println("Unknown object recieved from " + clientUser);
					break;
				} catch (IOException e) {
					System.out.println(clientUser + " has disconnected.");
					break;
				}
				
				sendMessageToClient(clientMessage);
			}
			
			removeClientFromList(this);
			close();
		}
		
		public boolean sendMessage(Message m) {
			if (!clientSocket.isConnected() || clientSocket.isClosed()) {
				close();
				return false;
			}
			
			try {
				if (m.getType() == Message.TEXT_MESSAGE)
					send.writeObject(m);
				else if (m.getType() == Message.USER_MESSAGE){
					if (m.getSender() != clientUser) {
						send.writeObject(m);
					}
				}
			} catch (IOException e) {
				System.err.println("Error sending message to " + clientUser);
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
	}
}