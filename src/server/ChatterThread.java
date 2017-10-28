package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import chatter.Message;

public class ChatterThread extends Thread {
    private Socket socket = null;
    
    public ChatterThread(Socket socket) {
        super("ChatterThread");
        this.socket = socket;
    }
     
    public void run() {
    	
        try {
        	ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
        	ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
        	
            Message inMessage, outMessage;
//            KnockKnockProtocol kkp = new KnockKnockProtocol();
//            outputLine = kkp.processInput(null);
//            out.println(outputLine);
            
            inMessage = (Message)fromClient.readObject();
            if(inMessage != null)
            	toClient.writeObject(inMessage);
 
//            while ((inputLine = in.readLine()) != null) {
//                outputLine = kkp.processInput(inputLine);
//                out.println(outputLine);
//                if (outputLine.equals("Bye"))
//                    break;
//            }
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
}
