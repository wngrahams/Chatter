package chatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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

public class ChatterServer {

	ServerSocket sock;
	Socket client;
	int port = 0xFFFF;
	boolean keepGoing = true;
	
	Map<User,ChatterClient> map = new HashMap<User,ChatterClient>();
	
	private ServerFrame serverFrame;
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatterServer cs = new ChatterServer();
	}
	
	public ChatterServer() {
		
		System.out.println("inside server constructor");
		acceptClient();
	}
	
	
	
	public void acceptClient() {
		
		try
		{
			sock = new ServerSocket(port);
			
			ObjectInputStream clientChatterObj;
			
			while(keepGoing)
			{
				System.out.println("looking for clients");
				client = sock.accept();
				System.out.println("found a client");
				clientChatterObj = new ObjectInputStream(client.getInputStream());
				System.out.println("recieved input stream");
				Object clientObj = clientChatterObj.readObject();
				ChatterClient testClient = (ChatterClient)clientObj;
				System.out.println("recieved clientobj");
				User userObj =  testClient.getUser();
				
				map.put(userObj, testClient);
				
				
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
	
	public void readClient() {
		try
		{
			
			ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
			
			while(keepGoing)
			{
		        //String[] parts = in.split()
		        //map.put(key, value)
		        Object objReceived = inputStream.readObject();
		        Message messageObj = (Message)objReceived;
		        String message = messageObj.getMessage();	
		        
		        User sender = messageObj.getSender();
		        User recipient = messageObj.getRecipient();
		        
		        
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
	
	
	public void splitStream(InputStream in)
	{
		
	}
	
	//receive Message object
	public void sendMessage(User recipient, User sender, String message)
	{
		//connect to recipient, send Message object
		
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
	
	
}
