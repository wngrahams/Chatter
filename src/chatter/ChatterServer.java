package chatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.sun.java_cup.internal.runtime.Scanner;

import server.ServerFrame;

import chatter.ChatterClient;
import client.User;
import chatter.Message;

public class ChatterServer implements Runnable{

	ServerSocket sock;
	Socket client;
	int port = 0xFFFF;
	boolean keepGoing = true;
	
	private PrintWriter os;
	//private final Socket socket;
	
	Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	private ServerFrame serverFrame;
	private ObjectInputStream clientChatterObj;
	private ObjectOutputStream serverObj;
	
	private User sender;
	private User recipient;
	
	private ChatterClient testClient;
	private Message messageObj;
	private User userObj;
	private String message;
	
	
	   
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
				
				map.put(userObj, testClient);
	
				//this.os = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				
	            System.out.println("Client connected to server");
	            System.out.println("client obj - " + testClient);
	            System.out.println("client user: " + userObj);
	            
	            


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
			System.out.println("inside run");
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
	
	/*
	public void readClient() {
		try
		{
			//System.out.println("inside readClient");
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
	*/
	
	public void splitStream(InputStream in)
	{
		
	}
	
	//receive Message object
	public void sendMessage(User recipient, User sender, String message)
	{
		//connect to recipient, send Message object
		System.out.println("inside server SendMessage");
	      try {
	          synchronized (os) {
	            os.println(sender + ":" + message);
	            os.flush();
	          }
	        } catch (Exception e) {
	          // We shutdown this client on any exception.
	        	e.printStackTrace();
	        }
		
		if(recipient == null)
		{
			//sending a group message, so it has to be send to the "sender" as well
		}
		
		else
		{
			//sending private message
			//establish connection, 
		}
		
	}
	
	public void sendNickname(String nick)
	{
		//instead of sending message, send string "nickname" to everybody
	}
	
	
	/*
	class  extends Thread
	{
		Socket sock;
		ObjectInputStream messageObj;
		ObjectOutputStream serverObj;
		
		
	}
	*/
	
}
