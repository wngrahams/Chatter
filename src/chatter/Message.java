package chatter;

import java.io.Serializable;

/**
 *  Message class consolidates messages passed between clients and server into one class.
 *  Contains a String that stores the text message, two <code>User</code> objects for storing
 *   the sender and recipient <code>User</code>. ALso contains integers denoting the "type" of message. 
 *   Message objects are either log-on, log-off, text message, or update name message.
 *   
 * @author Graham Stubbs (wgs11@georgetown.edu)
 * @author Cooper Logerfo (cml264@georgetown.edu)
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 4419539405020821670L;
	private String message;
	private User recipient;
	private User sender;
	
	private int messageType;
	
	public static final int USER_NAME_MESSAGE = 0b00;
	public static final int TEXT_MESSAGE = 0b01;
	public static final int USER_LOGON_MESSAGE = 0b10;
	public static final int USER_LOGOFF_MESSAGE = 0b11;
	
	public Message() {
		this("");
	}
	
	public Message(String text) {
		this(null, null, text);
	}
	
	public Message(User from, int messageType) {
		this(User.SERVER, from, "", messageType);
	}
	

	/**
	 * Message constructor with parameters. Receives two <code>User</code> objects, the message sender 
	 * and message recipient, the type of message, and the text of the message. Recipient will be null
	 *  if it is a log on/off message or if it is a global message.
	 */
	public Message(User to, User from, String text) {
		this(to, from, text, TEXT_MESSAGE);
	}
	
	/**
	 * Message constructor with parameters. Receives two <code>User</code> objects, the message sender 
	 * and message recipient, the type of message, and the text of the message. Recipient will be null 
	 * if it is a log on/off message or if it is a global message. Also includes a parameter for 
	 * message "type," indicating whether the message sent to the server is a user logging on/off, a text
	 * message, or a name change message.
	 */
	public Message(User to, User from, String text, int messageType){
		if (USER_LOGON_MESSAGE == messageType) {
			setMessage("'" + from + "' logged on.");
		}
		else if (USER_LOGOFF_MESSAGE == messageType)
			setMessage("'" + from + "' disconnected.");
		else
			setMessage(text);
		
		setRecipient(to);
		setSender(from);
		setType(messageType);
	}

	public String getMessage() {
		return message;
	}
	
	
	/**
	 * Update the text message, if "type" of message is a text message. 
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getRecipient() {
		if (null == recipient)
			return User.SERVER;
		
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}
	
	public int getType() {
		return messageType;
	}
	
	/**
	 * Update message "type" to indicate whether the incoming message to the server
	 * is a log-on message, a text message, an updated user name, or a log-off message.
	 */
	public void setType(int type) {
		if (type != TEXT_MESSAGE && type != USER_LOGON_MESSAGE 
				&& type != USER_LOGOFF_MESSAGE && type != USER_NAME_MESSAGE) 
			throw new RuntimeException("Unknown error type");
		
		messageType = type;
	}

	/**
	 * Returns the "Message" object as a string
	 */
	@Override
	public String toString() {
		String output;
		if (sender != User.SERVER)
			output = sender + ": " + message;
		else 
			output = "Message from server: " + message;
		return output;
	}
}
