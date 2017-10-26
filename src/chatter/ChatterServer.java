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
				client = sock.accept();
				
				clientChatterObj = new ObjectInputStream(client.getInputStream());
				Object clientObj = clientChatterObj.readObject();
				ChatterClient testClient = (ChatterClient)clientObj;
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
			
			//Map<String, String> map = new HashMap<String,String>();
			
			ObjectInputStream inputStream; 
			
			while(keepGoing)
			{
		        //InputStream in = client.getInputStream();
		        //Scanner in2 = (Scanner) client.getInputStream();
		        
		        //String[] parts = in.split()
		        //map.put(key, value)
		        //Object o = inputStream.readObject();
		        
		        //BufferedReader bin = new BufferedReader( new InputStreamReader(in) );
		        //String msg = bin.readLine();
	            
	            //System.out.println("Server recieved following message from client ="+msg);
	            
	            /*
	            if(object.getRecipient == null)
	            {
	                  //group message
	            }
	            else
	            {
	            		//search hashmap for recipient user obj
	            }
	           
	            
	            */
			}
			

		}
		catch(Exception e)
		{
			
		}
		
	}
	
	public void splitStream(InputStream in)
	{
		
	}
	
	
}
