package chatter;

import client.ChatterClient;
import server.ChatterServer;


/**
 * Chatter creates a new ChatterServer based on command line arguments. A ChatterClient
 * must be directly created using 'java ChatterClient'. A ChatterServer can also be created
 * using 'java ChatterServer' 
 * 
 * @author Graham Stubbs (wgs11@georgetown.edu)
 * @author Cooper Logerfo (cml264@georgetown.edu)
 */
public class Chatter {
	
	ChatterServer server;
	ChatterClient cc1;
	ChatterClient cc2;
	

	public Chatter() {
		this(0xFFFF);
	}
	
	public Chatter(int port)
	{
		server = new ChatterServer(port);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Chatter chat;
		if (args.length < 1)
			chat = new Chatter();
		else if (args.length == 1) {
			try {
				int portInt = Integer.parseInt(args[0]);
				chat = new Chatter(portInt);
			} catch (NumberFormatException e) {
				System.out.println("Usage: java Chatter <port_number>");
				System.out.println("Port number should be an integer less than " + 0xFFFF);
			}
		}
		else {
			System.out.println("Usage: java Chatter <port_number>");
		}
	}

}
