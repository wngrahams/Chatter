package chatter;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import server.ServerFrame;

import chatter.ChatterClient;
import client.User;
import client.ClientFrame;
import chatter.Message;

public class ChatterServer implements Runnable{

	ServerSocket sock;
	Socket client;
	int port = 0xFFFF;
	boolean keepGoing = true;

	Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	Map<User,UniqueClient> newMap = new HashMap<User, UniqueClient>();
	
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
			sock = new ServerSocket(port);
			
			//ObjectInputStream clientChatterObj;

			while(keepGoing)
			{
				client = sock.accept();
				
				clientChatterObj = new ObjectInputStream(client.getInputStream());
				serverObj = new ObjectOutputStream(client.getOutputStream());
				
				Object clientObj = clientChatterObj.readObject();
				testClient = (ChatterClient)clientObj;
				userObj =  testClient.getUser();
				
				//this client that just logged on is added to teh hash
				map.put(userObj, testClient);

	            System.out.println("Client connected to server");
	            System.out.println("client obj - " + testClient);
	            System.out.println("client user: " + userObj);
	            
	            //This is the new class
	            UniqueClient clientThread = new UniqueClient(client); 
	            clientThread.start(); //opens the thread for this unique client
	            
	            newMap.put(userObj, clientThread);
	            
	            //clientFrameObj = new ClientFrame(testClient);
	            //clientFrameObj.addNewUser(userObj);


			}
			sock.close();
		}
		
		catch(Exception e)
		{
			System.err.println(e);
		}
		
	}
	
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("inside Server run");
			//ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
			
			while(keepGoing)
			{
		        //String[] parts = in.split()
		        //map.put(key, value)
		        Object objReceived = clientChatterObj.readObject();
		        messageObj = (Message)objReceived;
		        message = messageObj.getMessage();	
		        
		        sender = messageObj.getSender();
		        recipient = messageObj.getRecipient();
		        
		        System.out.println("message obj received : " + messageObj);
		        
		        
		        //Sender is attempting to update name
		        if(message.charAt(0) == '/')
		        {
		        		//String userNickname = message;
		        		sender.setNickname(message);
		        		//so, I also have to send this nickname to everybody		        		
		        		sendNickname(message);
		        	
		        }
		        //Sender is attempting to send a message
		        else
		        {
		        		//search has map for recipient
		        		if(map.containsKey(recipient))
		        		{
		        			//send message string to recipient
		        			//actually, just send the Message object
		        			sendMessage(recipient, sender, message);
		        		}
		        		
		        		else
		        		{
		        			//send a message back to sender saying that their recipient doesn't exist
		        		}
		        		        	
		        }
			}
		}
		catch(Exception e)
		{
			
		}
	}
	

	
	//receive Message object
	public void sendMessage(User recipient, User sender, String message)
	{
		System.out.println("inside sendMessage");
		UniqueClient recipientThread = newMap.get(recipient);
		
		/*
		if(newMap.containsKey(recipient))
		{
			System.out.println("inside sendMessage, our map contains recipient"
					+ "sending ms : " + message);
			recipientThread = newMap.get(recipient);
			recipientThread.writeMessage(message);
		}*/
		
		if(recipient == null)
		{
			//sending a group message, so it has to be send to the "sender" as well
			//System.out.println("inside recipient = null");
		}
		
		else
		{
			//sending private message
			//establish connection, 
			//System.out.println("inside recipient != null..");
			//System.out.println("recipient - "+ recipient.getNickname());
			//recipientThread.writeMessage(message);
			
			//null pointer exception bc Jim is not actually a user connected to the server
			//Jim has no socket.
			
		}
		
	}
	
	public void sendNickname(String nick)
	{
		//instead of sending message, send string "nickname" to everybody
	}
	
	
	
	class UniqueClient extends Thread
	{
		Socket sock;
		ObjectInputStream messageObject;
		ObjectOutputStream serverObj;
		Message receivedMsgObj;
		String messageText;
		
		User recipientThread;
		User senderThread;
		
		
		UniqueClient(Socket sock)//, ChatterClient clientObj, User userObj)	
		{
			this.sock = sock;
			try
			{
				messageObject = new ObjectInputStream(client.getInputStream());
				serverObj = new ObjectOutputStream(client.getOutputStream());

		        //Sender is attempting to update name
		        if(messageText.charAt(0) == '/')
		        {
		        		//String userNickname = message;
		        		sender.setNickname(messageText);
		        		//so, I also have to send this nickname to everybody		        		
		        		sendNickname(messageText);
		        	
		        }
		        
		        //Sender is attempting to send a message
		        
//		        else
//		        {
//		        		System.out.println("insdie unique client sock, else");
//		        		//sendMessage(recipientThread, senderThread, messageText);
//		        		//search has map for recipient
//		        		if(map.containsKey(recipient))
//		        		{
//		        			//send message string to recipient
//		        			//actually, just send the Message object
//		        			sendMessage(recipient, sender, message);
//		        		}
//		        		
//		        		else
//		        		{
//		        			//send a message back to sender saying that their recipient doesn't exist
//		        		}
//		        		        	
//		        }
		        
			}
			catch(Exception e)
			{
				System.err.println(e);
			}

		}
		
		public void run()
		{
			//here is where I'll have to call "sendMessage" method
			//sendMessage(recipientThread, senderThread, messageText);
			//so we are trying to send messageText from sender to recipient (both user obj)
			
			System.out.println("inside UniqueClient run");
			
			boolean kg = true;
			while(kg)
			{
				try
				{
					//System.out.println("message obj received : ");
			        Object objReceived = messageObject.readObject();
			        //System.out.println("message obj received : " + objReceived );
			        receivedMsgObj = (Message)objReceived;
			        messageText = receivedMsgObj.getMessage();	
			        
			        senderThread = receivedMsgObj.getSender();
			        recipientThread = receivedMsgObj.getRecipient();
			        
			        System.out.println("message obj received. Sender = " + senderThread.getNickname()
			        + "recipient = " + recipientThread.getNickname() + "msg" + messageText);
				}
				catch(Exception e)
				{
					
				}
			}
			
			if(map.containsKey(recipientThread))
			{
				//sendMessage(recipientThread, senderThread, messageText);
			}

		}
		
		
		public void writeMessage(String message)
		{
			System.out.println("inside writeMessage");
			try
			{
				serverObj.writeObject(message);
			}
			catch(Exception e)
			{
				
			}

		}
	}
		
}



//as clients join the server, the server must update the ClientFrame so that they see other users

//if I start up a client and send a msg to one user, teh "run" method runs and sendMessage is called
//but if I click on another user to send a message, the "run" method never gets called