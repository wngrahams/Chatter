package chatter;

import java.io.Serializable;

import client.User;

public class Message implements Serializable {

	/**
	 *  
	 */
	private static final long serialVersionUID = 4419539405020821670L;
	private String message;
	private User recipient;
	private User sender;
	private static int NEWUSER = 1;
	private static int MESSAGE = 2;
	private int type;
	
	public Message(String text) {
		this(null, null, text, 0);
	}

	public Message(User to, User from, String text, int type) {
		setRecipient(to);
		setSender(from);
		setMessage(text);
		setType(type);
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getRecipient() {
		if (null == recipient)
			return new User("global chat");
		
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

	@Override
	public String toString() {
		String output;
		if (sender != null)
			output = sender + ": " + message;
		else 
			output = "Message from server: " + message;
		return output;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int messageType) {
		type = messageType;
	}
}
