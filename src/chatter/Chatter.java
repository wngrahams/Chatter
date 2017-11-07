package chatter;

import client.ChatterClient;
import server.ChatterServer;

public class Chatter {
	
	ChatterServer server;
	ChatterClient cc1;
	ChatterClient cc2;
	

	public Chatter() {
		server = new ChatterServer(0xFFFF);
		cc1 = new ChatterClient("localhost", 0xFFFF);
		cc2 = new ChatterClient("localhost", 0xFFFF);
	}
	
	public Chatter(String host, int port)
	{
		server = new ChatterServer(port);
		cc1 = new ChatterClient(host, port);
		cc2 = new ChatterClient(host, port);
		
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Chatter chat;
		if (args.length < 1)
			chat = new Chatter();
		else if (args.length < 2 || args.length > 2)
			System.out.println("Usage: java Chatter <hostname> <port_number>");
		else {
			try {
				int portInt = Integer.parseInt(args[1]);
				chat = new Chatter(args[0], portInt);
			} catch (NumberFormatException e) {
				System.out.println("Usage: java Chatter <hostname> <port_number>");
				System.out.println("Port number should be an integer less than " + 0xFFFF);
			}
		}
	}

}
