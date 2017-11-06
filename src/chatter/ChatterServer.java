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

	private ServerFrame serverFrame;
	
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
		serverFrame = new ServerFrame(this);
		threadList = new ArrayList<ChatterThread>();
		
		startServer();
	}
	
	private void getAllUsers(ChatterThread reciever) {
		synchronized(threadList) {
			for (int i=0; i<threadList.size(); i++) {
				if (threadList.get(i) != reciever) {
					ChatterThread otherClient = threadList.get(i);
					reciever.sendMessage(new Message(otherClient.clientUser, Message.USER_LOGON_MESSAGE));
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
				System.out.println("Sending private message from " + m.getSender() + " to " + m.getRecipient());
				for (int i=0; i< threadList.size(); i++) {
					ChatterThread clientRecipient = threadList.get(i);
					System.out.println("User "+ i + ": " + clientRecipient.getUser());
					User testUser = clientRecipient.getUser();
					
					if (testUser.equals(m.getRecipient()) || testUser.equals(m.getSender())) {
						System.out.println("sending...");
						if (!clientRecipient.sendMessage(m)) {
							threadList.remove(i);
							i--;
						}
					}
				}
			}
		}
	}
	
	private void startServer() {
		keepGoing = true;
				
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			serverFrame.displayMessage(new Message("Successfully started server. IP: " + serverSocket.getInetAddress() + " Port: " + port));
			serverFrame.displayMessage(new Message("Ready to receive clients..."));
			
			while (keepGoing) {
				
				try {
					Socket clientSocket = serverSocket.accept();
					
					if (!keepGoing)
						break;
					
					ChatterThread newClient = new ChatterThread(clientSocket);
					threadList.add(newClient);
					newClient.start();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error connecting to client."));
				} 
			}
			
			try {
				serverSocket.close();
				for (int i=0; i<threadList.size(); i++) {
					ChatterThread toClose = threadList.get(i);
					toClose.close();
				}
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error closing server socket."));
			}
		} catch (IOException e) {
			serverFrame.displayMessage(new Message("Error creating server socket at port: " + port));
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
								
//				userMessage = (Message)(recieve.readObject());
				userMessage = (Message)(recieve.readUnshared());
				clientUser = userMessage.getSender();
				
				serverFrame.displayMessage(new Message(clientUser, Message.USER_LOGON_MESSAGE));
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error connecting client input/output stream"));
				return;
			} catch (ClassNotFoundException e) {
				serverFrame.displayMessage(new Message("Unknown object recieved from " + clientUser));
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
					serverFrame.displayMessage(new Message("Error disconnecting from client output stream."));
				}
			}
			if (recieve != null) {
				try {
					recieve.close();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error disconnecting from client input stream."));

				}
			}
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error disconnecting from client socket."));
				}
			}
		}
		
		public void run() {
			boolean clientRun = true;
			while (clientRun) {
				try {
//					clientMessage = (Message)(recieve.readObject());
					clientMessage = (Message)(recieve.readUnshared());
				} catch (ClassNotFoundException e) {
					serverFrame.displayMessage(new Message("Unknown object recieved from '" + clientUser + "'"));
					break;
				} catch (IOException e) {
					serverFrame.displayMessage(new Message(clientUser, Message.USER_LOGOFF_MESSAGE));
					break;
				}

				sendMessageToClient(clientMessage);
			}
			
			removeClientFromList(this);
			sendMessageToClient(new Message(User.SERVER, clientUser, "disconnect", Message.USER_LOGOFF_MESSAGE));
			close();
		}
		
		public User getUser() {
			return clientUser;
		}
		
		public boolean sendMessage(Message m) {
			if (!clientSocket.isConnected() || clientSocket.isClosed()) {
				close();
				return false;
			}
			
			try {
				if (m.getType() == Message.TEXT_MESSAGE) {
//					send.writeObject(m);
					send.writeUnshared(m);
				}
				else if (m.getType() == Message.USER_NAME_MESSAGE) {
//					send.writeObject(m);
					send.writeUnshared(m);
					
					// change name only for user that sent this name change request
					if (clientUser.equals(m.getSender())) {
						String oldUser = clientUser.getNickname();
						clientUser.setNickname(m.getMessage()); 
						serverFrame.displayMessage(new Message("User '" + oldUser + "' changed name to: '" + clientUser + "'"));
						send.writeObject(new Message(clientUser, User.SERVER, "Successfully changed name from " + oldUser + " to " + clientUser));
						send.writeUnshared(new Message(clientUser, User.SERVER, "Successfully changed name from " + oldUser + " to " + clientUser));
					}
				}	
				else if (m.getType() == Message.USER_LOGON_MESSAGE ||  m.getType() == Message.USER_LOGOFF_MESSAGE){
					if (m.getSender() != clientUser) {
//						send.writeObject(m);
						send.writeUnshared(m);
					}
				}
				
//				send.reset();
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error sending message to " + clientUser));
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
	}
}