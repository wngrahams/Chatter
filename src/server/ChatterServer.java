package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import chatter.Message;
import chatter.User;

/** 
 * The <code>ChatterServer</code> class creates allows for
 * the connection of multiple clients and manages their interactions,
 * including the sending of global and private messages, name changes,
 * logging on, and logging off.
 * 
 * @author Graham Stubbs (wgs11@georgetown.edu)
 * @author Cooper Logerfo (cml264@georgetown.edu)
 */
public class ChatterServer {

	private ServerFrame serverFrame;
	
	private int port = 0xFFFF;
	private boolean keepGoing;
	
	// An ArrayList to keep track of all connected clients
	private ArrayList<ChatterThread> threadList;
	
	
	/** 
	 * Starts a new <code>ChatterServer</code> at the port provided,
	 * or at the default port <code>0xFFFF</code> if no port is provided.
	 * 
	 * @param args Desired connection port
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ChatterServer cs = new ChatterServer();
	}
	
	/** 
	 * Creates a new <code>ChatterServer</code> at the default port <code>0xFFFF</code>
	 */
	public ChatterServer() {
		this(0xFFFF);
	}
	
	/** 
	 * Creates a new <code>ChatterServer</code> at the given port
	 * 
	 * @param port Desired connection port
	 */
	public ChatterServer(int port)
	{
		this.port = port;
		serverFrame = new ServerFrame();
		threadList = new ArrayList<ChatterThread>();
		
		startServer();
	}
	
	/** 
	 * Checks to see if a given username is currently in use by a connected
	 * client
	 * 
	 * @param name The name to check
	 * @return <code>true</code> if the name is available, <code>false</code> otherwise
	 */
	private boolean checkNameAvailable(String name) {
		synchronized (threadList) {
			for (int i=0; i<threadList.size(); i++) {
				User currentUser = threadList.get(i).getUser();
				String userName = currentUser.getNickname();
				if (name.equals(userName))
					return false;
			}
			
			return true;
		}
	}
	
	/** 
	 * Called when a client first connects to the server, sends a log-on
	 * <code>Message</code> to the new client for every other connected
	 * client, allowing for the new client to populate its list of users
	 * 
	 * @param reciever The newly connected <code>ChatterThread</code> to receive the <code>Message</code>s
	 */
	private void getAllUsers(ChatterThread reciever) {
		synchronized(threadList) {
			for (int i=0; i<threadList.size(); i++) {
				if (threadList.get(i) != reciever) {
					ChatterThread otherClient = threadList.get(i);
					reciever.sendMessage(new Message(otherClient.clientUser, Message.USER_LOGON_MESSAGE));
				}
			}
		}
	}
	
	/** 
	 * Removes a given client from the list of clients
	 * 
	 * @param client The client to remove
	 */
	private void removeClientFromList(ChatterThread client) {
		synchronized(threadList) {
			threadList.remove(this);
		}
	}
	
	/** 
	 * Sends a given <code>Message</code> to connected clients. The specific 
	 * clients the <code>Message</code> is sent to depends on the sender and
	 * the type of <code>Message</code>
	 * 
	 * @param m The <code>Message</code> to send
	 */
	private void sendMessageToClient(Message m) {
		synchronized(threadList) {
			if (m.getRecipient() == User.SERVER) {
				// The message is global, send to all clients
				for (int i=0; i<threadList.size(); i++) {
					
					ChatterThread clientRecipient = threadList.get(i);
					if (!clientRecipient.sendMessage(m)) {
						threadList.remove(i);
						i--;
					}
				}
			}
			else {
				// Otherwise, the message is a private message, only send it to the sender and receiver
				for (int i=0; i< threadList.size(); i++) {
					ChatterThread clientRecipient = threadList.get(i);
					User testUser = clientRecipient.getUser();
					
					if (testUser.equals(m.getRecipient()) || testUser.equals(m.getSender())) {
						if (!clientRecipient.sendMessage(m)) {
							threadList.remove(i);
							i--;
						}
					}
				}
			}
		}
	}
	
	/** 
	 * Creates a new <code>ServerSocket</code> at the port given on class
	 * creation. Then creates a new <code>Socket</code> to continually
	 * listen for new client connections. Terminates on <code>ServerFrame</code>
	 * close or if an error is encountered when creating the <code>ServerSocket</code>
	 */
	private void startServer() {
		keepGoing = true;
				
		try {
			// Create the ServerSocket 
			ServerSocket serverSocket = new ServerSocket(port);
			serverFrame.displayMessage(new Message("Successfully started server. IP: " + serverSocket.getInetAddress() + " Port: " + port));
			serverFrame.displayMessage(new Message("Ready to receive clients..."));
			
			// Do this until ServerFrame close, won't start if ServerSocket creation fails
			while (keepGoing) {
				
				try {
					// Create Socket to continually listen for new client connections
					Socket clientSocket = serverSocket.accept();
					
					if (!keepGoing)
						break;
					
					ChatterThread newClient = new ChatterThread(clientSocket);
					threadList.add(newClient);
					newClient.start();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error connecting to client."));
				} 
			}
			
			try {
				serverSocket.close();
				for (int i=0; i<threadList.size(); i++) {
					ChatterThread toClose = threadList.get(i);
					toClose.close();
				}
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error closing server socket."));
			}
		} catch (IOException e) {
			serverFrame.displayMessage(new Message("Error creating server socket at port: " + port));
			keepGoing = false;
		}
		
		
	}
	
	/** 
	 * The <code>ChatterThread</code> class extends <code>Thread</code>
	 * and allows for the <code>ChatterServer</code> to connect and 
	 * communicate with a <code>ChatterClient</code> object. Multiple
	 * <code>ChatterThread</code>s should be created to allow for 
	 * communication between multiple clients
	 * 
	 * @author Graham Stubbs (wgs11@georgetown.edu)
	 * @author Cooper Logerfo (cml264@georgetown.edu)
	 */
	private class ChatterThread extends Thread {
		
		private Socket clientSocket;
		private User clientUser;
		
		private ObjectOutputStream send;
		private ObjectInputStream recieve;
		
		private Message clientMessage;
		
		/** 
		 * Creates a new <code>ChatterThread</code> object connected to a given
		 * <code>Socket</code>. Sets up an <code>ObjectOutputStream</code> and an
		 * <code>ObjectInputStream</code> to allow <code>Message</code>s to be sent
		 * back and forth between the client and server
		 * 
		 * @param s The <code>Socket</code> the client is connected to
		 */
		public ChatterThread(Socket s) {
			clientSocket = s;
			
			Message userMessage = new Message();
			
			try {
				send = new ObjectOutputStream(clientSocket.getOutputStream());
				recieve = new ObjectInputStream(clientSocket.getInputStream());
								
				userMessage = (Message)(recieve.readUnshared());
				clientUser = userMessage.getSender();
				
				serverFrame.displayMessage(new Message(clientUser, Message.USER_LOGON_MESSAGE));
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error connecting client input/output stream"));
				return;
			} catch (ClassNotFoundException e) {
				serverFrame.displayMessage(new Message("Unknown object recieved from " + clientUser));
				return;
			} 
			
			
			sendMessageToClient(userMessage);
			getAllUsers(this);
		}
		
		/** 
		 * Disconnects the client from its connected <code>ObjectOutputStream</code>
		 * and <code>ObjectInputStream</code>. Closes the connected <code>Socket</code>.
		 */
		protected void close() {
			if (send != null) {
				try {
					send.close();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error disconnecting from client output stream."));
				}
			}
			if (recieve != null) {
				try {
					recieve.close();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error disconnecting from client input stream."));

				}
			}
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					serverFrame.displayMessage(new Message("Error disconnecting from client socket."));
				}
			}
		}
		
		/** 
		 * Overrides <code>Thread.run()</code>. Continually checks for 
		 * <code>Message</code>s sent by the connected client. If a message
		 * is received, <code>sendMessageToClient()</code> is called to
		 * handle the message.
		 */
		@Override
		public void run() {
			boolean clientRun = true;
			while (clientRun) {
				try {
					clientMessage = (Message)(recieve.readUnshared());
				} catch (ClassNotFoundException e) {
					serverFrame.displayMessage(new Message("Unknown object recieved from '" + clientUser + "'"));
					break;
				} catch (IOException e) {
					serverFrame.displayMessage(new Message(clientUser, Message.USER_LOGOFF_MESSAGE));
					break;
				}

				// first check if it's a name change, if the new name is available
				if (clientMessage.getType() == Message.USER_NAME_MESSAGE && !checkNameAvailable(clientMessage.getMessage()))
						sendMessageToClient(new Message(clientUser, User.SERVER, "Name '" + clientMessage.getMessage() + "' is not available."));
				else
					sendMessageToClient(clientMessage);
			}
			
			removeClientFromList(this);
			sendMessageToClient(new Message(User.SERVER, clientUser, "disconnect", Message.USER_LOGOFF_MESSAGE));
			close();
		}
		
		/** 
		 * Returns the <code>User</code> associated with this client
		 * 
		 * @return The <code>User</code> associated with this client
		 */
		public User getUser() {
			return clientUser;
		}
		
		/** 
		 * Uses the client's <code>ObjectOutputStream</code> to write the 
		 * given <code>Message</code> over the network to the client
		 * 
		 * @param m The <code>Message</code> to be written
		 * @return <code>true</code> if the message is successfully sent, <code>false</code> otherwise
		 */
		public boolean sendMessage(Message m) {
			if (!clientSocket.isConnected() || clientSocket.isClosed()) {
				close();
				return false;
			}
			
			try {
				if (m.getType() == Message.TEXT_MESSAGE) {
					send.writeUnshared(m);
				}
				else if (m.getType() == Message.USER_NAME_MESSAGE) {
					send.writeUnshared(m);
					
					// Change name only for user that sent this name change request
					if (clientUser.equals(m.getSender())) {
						User oldUser = new User(clientUser);
						clientUser = new User(m.getMessage(), clientUser.getIP());
						serverFrame.displayMessage(new Message(clientUser, oldUser, clientUser.getNickname(), Message.USER_NAME_MESSAGE));
						send.writeUnshared(new Message(clientUser, User.SERVER, "Successfully changed name from '" + oldUser + "' to '" + clientUser + "'"));
					}
				}	
				else if (m.getType() == Message.USER_LOGON_MESSAGE ||  m.getType() == Message.USER_LOGOFF_MESSAGE){
					// No need to send to the user that logged on/off
					if (m.getSender() != clientUser) 
						send.writeUnshared(m);
				}
				
			} catch (IOException e) {
				serverFrame.displayMessage(new Message("Error sending message to " + clientUser));
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
	}
}