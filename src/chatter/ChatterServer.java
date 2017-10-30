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

//	ServerSocket serverSock;
//	Socket client;
	private int port = 0xFFFF;
	private boolean keepGoing;

	Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	Map<User,ChatterThread> threadMap = new HashMap<User, ChatterThread>();
	private ArrayList<ChatterThread> threadList;
	
//	private ServerFrame serverFrame;
//	public ObjectInputStream clientChatterObj;
//	private ObjectOutputStream serverObj;
	
//	private ObjectInputStream fromClient;
//	private ObjectOutputStream toClient;
	
//	private User sender; 
//	private User recipient;
	
//	private ChatterClient testClient;
//	public Message messageObj;
//	private User userObj;
//	private String message;
	
	//private ClientFrame clientFrameObj;
	
	
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatterServer cs = new ChatterServer();
	}
	
	public ChatterServer() {
		this(0xFFFF);
//		threadList = new ArrayList<ChatterThread>();
//		System.out.println("inside server constructor");
//		acceptClient();
	}
	
	public ChatterServer(int port)
	{
		this.port = port;
		threadList = new ArrayList<ChatterThread>();
		startServer();
	}
	
	
//	public void acceptClient() {
//		
//		try
//		{
//			serverSock = new ServerSocket(port);
//			
//			//ObjectInputStream clientChatterObj;
//
//			while(keepGoing)
//			{
//				System.out.println("looking for clients");
//				client = serverSock.accept();
//				System.out.println("found a client");
//				clientChatterObj = new ObjectInputStream(client.getInputStream());
//				serverObj = new ObjectOutputStream(client.getOutputStream());
//				
//				Object clientObj = clientChatterObj.readObject();
////				testClient = (ChatterClient)clientObj;
////				userObj =  testClient.getUser();
//				userObj = (User)clientObj;
//				
//				//this client that just logged on is added to teh hash
//				//map.put(userObj, testClient);
//
//	            System.out.println("Client connected to server");
//	            System.out.println("client obj - " + testClient);
//	            System.out.println("client user: " + userObj);
//	            
//	            //This is the new class
//	            ChatterThread clientThread = new ChatterThread(client); 
//	            clientThread.start(); //opens the thread for this unique client
//	            
//	            System.out.println("inside accept, after thread start, threadobj - " + clientThread);
//	            
//	            threadMap.put(userObj, clientThread);
//	            threadList.add(clientThread);
//
//
//			}
//			serverSock.close();
//		}
//		
//		catch(Exception e)
//		{
//			System.err.println(e);
//		}
//		
//	}
	
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
	
//	private synchronized ArrayList<ChatterThread> determineConnectedClients() {
//		for (int i=0; i<threadList.size(); i++) {
//			ChatterThread clientRecipient = threadList.get(i);
//			
//			if (!clientRecipient.sendMessage(m)) {
//				threadList.remove(i);
//				// TODO: also remove from hashmap
//				i--;
//			}
//		}
//	}

	
	//receive Message object
//	public void sendMessage(User recipient, User sender, String message)
//	{
//		System.out.println("inside sendMessage"); 
//
//		//sending a group message, so it has to be send to the "sender" as well
//		if(recipient == null)
//		{
//			for(int i = 0; i<threadList.size(); i++)
//			{
//				ChatterThread receiver = threadList.get(i);
//				receiver.writeMessage(message);
//			}
//		}
//		//private message
//		else
//		{
//			if(threadMap.containsKey(recipient))
//			{
//				ChatterThread recipientThread = threadMap.get(recipient);
//				System.out.println("inside sendMessage, our map contains recipient"
//						+ "sending ms : " + message);
//				recipientThread = threadMap.get(recipient);
//				recipientThread.writeMessage(message);
//			}
//			else
//			{
//				//"recipient" user does not exist in our list
//				//send something back to the sender telling them
//			}
//		}				
//	}
	
//	public void sendNickname(User user, String nick)
//	{
//		//instead of sending message, send string "nickname" to everybody
//		sender.setNickname(nick);
//		for(int i = 0; i<threadList.size(); i++)
//		{
//			ChatterThread receiver = threadList.get(i);
//			//go through each client's thread
//			//TODO: iterate through that client's JList until you get to "user" who is changing
//			//their name, then update the name 
//		}
//	}
	
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

	/*
//	class ChatterThread extends Thread
//	{
//		Socket sock;
//		ObjectInputStream clientInput;
//		ObjectOutputStream serverOutput;
//		Message receivedMsgObj;
//		String messageText;
//		
//		User recipientUser;
//		User senderUser;
//		
//		
//		ChatterThread(Socket client)//, ChatterClient clientObj, User userObj)	
//		{
//			this.sock = client;
//			try
//			{
//				clientInput = new ObjectInputStream(client.getInputStream());
//				serverOutput = new ObjectOutputStream(client.getOutputStream());
//		        
//			}
//			catch(Exception e)
//			{
//				System.err.println(e);
//			}
//
//		}
//		
//		public void run()
//		{
//
//			System.out.println("inside UniqueClient run");
//			
//			boolean kg = true;
//			while(kg)
//			{
//				try
//				{
//					System.out.println("message obj received : ");
//					
//
//					receivedMsgObj = (Message)clientInput.readObject();
//					//^this is where the nullpointer exception is happening^
//					
//					
//			        //System.out.println("message obj received : " + objReceived );
//
//			        messageText = receivedMsgObj.getMessage();	
//			        senderUser= receivedMsgObj.getSender();
//			        recipientUser = receivedMsgObj.getRecipient();
//			        
//			        System.out.println("receiver = " + recipientUser + "sender = " + senderUser);
//			        System.out.println("message obj received, msg = " + messageText);
//					
//			        //Sender is attempting to update name
//			        if(messageText.charAt(0) == '/')
//			        {
//			        		//String userNickname = message;
//			        		// TODO: read out '/' character and create a new string
//		        			senderUser.setNickname(messageText);  
//			        		sendNickname(senderUser, messageText);	
//			        }
//			        
//			        else
//			        {
//			        		//send message
//			        		sendMessage(recipientUser, senderUser, messageText);
//			        	
//			        }
//				}
//				catch(Exception e)
//				{
//					System.err.println("exception caught in run - "+e);
//					break;
//				}
//			}
//			
//			//close();
//		}
//
//		private void close() {
//			// try to close the connection
//			try {
//				if(serverObj != null) serverObj.close();
//			}
//			catch(Exception e) {}
//			try {
//				if(clientInput != null) clientInput.close();
//			}
//			catch(Exception e) {};
//			try {
//				if(sock != null) sock.close();
//			}
//			catch (Exception e) {}
//		}
//		
//		
//		public void writeMessage(String message)
//		{
//			System.out.println("inside writeMessage");
//			try
//			{
//				serverOutput.writeObject(message);
//			}
//			catch(Exception e)
//			{
//				
//			}
//
//		}
//	}
 */
}