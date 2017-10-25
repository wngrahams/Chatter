package chatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatterServer {

	ServerSocket sock;
	Socket client;
	int port = 5155;
	boolean keepGoing = true;
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public ChatterServer() {
		
		System.out.println("inside server constructor");

	}
	
	
	
	public void acceptClient() {
		
		try
		{
			sock = new ServerSocket(port);
			
			while(keepGoing)
			{
				client = sock.accept(); 
	            System.out.println("Client connected to server");
/*
	            PrintWriter pout = new PrintWriter( client.getOutputStream(), true);
	            String writeme = new java.util.Date().toString();
	            pout.println( writeme );
	            pout.flush();*/
			}
			
		}
		
		catch(Exception e)
		{
			
		}

	}
	
	public void readClient() {
		try
		{
	        InputStream in = client.getInputStream();
	        BufferedReader bin = new BufferedReader( new InputStreamReader(in) );
	        String msg = bin.readLine();
            
            System.out.println("Server recieved following message from client ="+msg);
		}
		catch(Exception e)
		{
			
		}

		
	}
	
	
}
