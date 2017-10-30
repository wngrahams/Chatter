package chatter;

import java.io.IOException;
import java.io.InputStream;
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

	ServerSocket serverSock;
	Socket client;
	int port = 0xFFFF;
	boolean keepGoing = true;

	Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	Map<User,ChatterThread> threadMap = new HashMap<User, ChatterThread>();
	private ArrayList<ChatterThread> threadList;
	
	private ServerFrame serverFrame;
	public ObjectInputStream clientChatterObj;
	private ObjectOutputStream serverObj;
	
	private User sender; 
	private User recipient;
	
	private ChatterClient testClient;
	public Message messageObj;
	private User userObj;
	private String message;
	
	//private ClientFrame clientFrameObj;
	
	
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatterServer cs = new ChatterServer();
	}
	
	public ChatterServer() {
		threadList = new ArrayList<ChatterThread>();
		System.out.println("inside server constructor");
		acceptClient();
	}
	
	public ChatterServer(int port)
	{
		this.port = port;
	}
	
	
	public void acceptClient() {
		
		try
		{
			serverSock = new ServerSocket(port);
			
			//ObjectInputStream clientChatterObj;

			while(keepGoing)
			{ 
				System.out.println("looking for clients");
				client = serverSock.accept();
				System.out.println("found a client");
				
				clientChatterObj = new ObjectInputStream(client.getInputStream());
				serverObj = new ObjectOutputStream(client.getOutputStream());

				Object clientObj = clientChatterObj.readObject();
				userObj = (User)clientObj;

	            System.out.println("Client connected to server");
	            System.out.println("client user: " + userObj);
	            
	            //This is the new class
	            ChatterThread clientThread = new ChatterThread(client, clientChatterObj, serverObj, userObj); 
	            clientThread.start(); //opens the thread for this unique client
	            
	            //System.out.println("inside accept, after thread start, threadobj - " + clientThread);
	            
	            threadMap.put(userObj, clientThread);
	            threadList.add(clientThread);


			}
			serverSock.close();
		}
		
		catch(Exception e)
		{
			System.err.println(e);
		}
		
	}
	
	
	

	
	//receive Message object
	public void sendMessage(Message message)//User recipient, User sender, String message)
	{
		User recipient = message.getRecipient();
		
		//sending a new user
//		
//		if(message.getType() == 1)
//		{
//			for(int i = 0; i<threadList.size(); i++)
//			{
//				ChatterThread receiver = threadList.get(i);
//				receiver.writeMessage(message);
//			}
//		}

		//sending a group message, so it has to be send to the "sender" as well
		if(recipient.toString() == "global chat")
		{
			System.out.println("inside sendMessage, recipient = global chat"); 
			for(int i = 0; i<threadList.size(); i++)
			{
				ChatterThread receiver = threadList.get(i);
				receiver.writeMessage(message);
			}
		}
		//private message
		else
		{
			if(threadMap.containsKey(recipient))
			{
				System.out.println("inside sendMessage.. contains recipient");
				ChatterThread recipientThread = threadMap.get(recipient);
				System.out.println("inside sendMessage, our map contains recipient"
						+ "sending ms : " + message);
				recipientThread = threadMap.get(recipient);
				recipientThread.writeMessage(message);
			}
			else
			{
				//"recipient" user does not exist in our list
				//send something back to the sender telling them
			}
		}				
	}
	
	public void sendNickname(User user, String nick)
	{
		//instead of sending message, send string "nickname" to everybody
		sender.setNickname(nick);
		for(int i = 0; i<threadList.size(); i++)
		{
			ChatterThread receiver = threadList.get(i);
			//go through each client's thread
			//TODO: iterate through that client's JList until you get to "user" who is changing
			//their name, then update the name 
		}
	}
	
	public void sendUser(User user)
	{
		
		for(int i = 0; i<threadList.size(); i++)
		{
			ChatterThread receiver = threadList.get(i);
			Message newUser = new Message(null, user, null, 1);
			receiver.writeMessage(newUser);
		}
	}
	

	
	class ChatterThread extends Thread
	{
		Socket sock;
		ObjectInputStream clientInput;
		ObjectOutputStream serverOutput;
		Message receivedMsgObj;
		String messageText;
		
		User recipientUser;
		User senderUser;
		
		User newUserToAdd;
		
		int messageType;
		
		
		ChatterThread(Socket client, ObjectInputStream inStream, ObjectOutputStream outStream, User userObj)//, ChatterClient clientObj, User userObj)	
		{
			this.sock = client;
			try 
			{  
				serverOutput = outStream;
				clientInput = inStream;
			}
			catch(Exception e)
			{
				System.err.println(e);
			}
			
			newUserToAdd = userObj;
		}
		
		public void run()
		{
			System.out.println("inside UniqueClient run");			
			boolean kg = true;
			while(kg)
			{
				try
				{
					System.out.println("message obj received");
					System.out.println("sender - " + newUserToAdd);
					
					sendUser(newUserToAdd);
					
					receivedMsgObj = (Message)clientInput.readObject();
					
					messageType = receivedMsgObj.getType();
					senderUser = receivedMsgObj.getSender();
					messageText = receivedMsgObj.getMessage();
					recipientUser = receivedMsgObj.getRecipient();
							
					System.out.println("sender + message = " + senderUser+" : " + messageText);
					System.out.println("addressed to = " + recipientUser);
					
					
					if(receivedMsgObj.getMessage() == null)
					{
						System.out.println("inside message == null");
						receivedMsgObj.setType(1);
						sendMessage(receivedMsgObj);
					}
					else
					{
						receivedMsgObj.setType(2);
						sendMessage(receivedMsgObj);
					}

				}
				catch(Exception e)
				{
					System.err.println("exception caught in run - "+e);
				}
					
			        //Sender is attempting to update name
			        if(messageText.charAt(0) == '/')
			        {
			        		System.out.println("inside messageText. /");
			        		//String userNickname = message;
			        		// TODO: read out '/' character and create a new string
		        			senderUser.setNickname(messageText);  
			        		//sendNickname(senderUser, messageText);	
			        }
			        
			        else
			        {
			        		//send message
						//sendMessage(receivedMsgObj);
			        	
			        }

			}
			
			//close();
		}

		private void close() {
			// try to close the connection
			try {
				if(serverObj != null) serverObj.close();
			}
			catch(Exception e) {}
			try {
				if(clientInput != null) clientInput.close();
			}
			catch(Exception e) {};
			try {
				if(sock != null) sock.close();
			}
			catch (Exception e) {}
		}
		
		
		public void writeMessage(Message message)//String message)
		{
			System.out.println("inside writeMessage");
			try
			{
				System.out.println("inside writing, writing following obj - " + message);
				serverOutput.writeObject(message);
				System.out.println("inside writing, wrote following obj - " + message);
			}
			catch(Exception e)
			{
				System.err.println(e);
			}

		}
		
		public void writeUser(User user)
		{
			try
			{
				serverOutput.writeObject(user);
			}
			catch(Exception e)
			{
				System.err.println(e);
			}

		}
	}
	
	/*
	 * alright so here is what as happening : the server wasn't able to connect any new 
	 * clients because control was never being passed back to the Server accept() method.
	 * This was because once a new client thread was created in accept() control was passed
	 * to the thread class where control was getting stuck in the constructor because
	 * it was looking for an input stream to read, but we already read out the input stream
	 * 
	 * 
	 * so I need to pass the input stream we take in the accept() method.
	 */
		
}



