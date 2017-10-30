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
	
	private int messageType;
	
	public static final int TEXT_MESSAGE = 0b0;
	public static final int USER_MESSAGE = 0b1;
	
	public Message() {
		this("");
	}
	
	public Message(String text) {
		this(null, null, text);
	}
	
	public Message(User from, int messageType) {
		this(User.SERVER, from, from + " logged on", messageType);
	}

	public Message(User to, User from, String text) {
		this(to, from, text, TEXT_MESSAGE);
	}
	
	public Message(User to, User from, String text, int messageType){
		setRecipient(to);
		setSender(from);
		setMessage(text);
		setType(messageType);
	}

	public String getMessage() {
		return message;
	}

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
	
	public void setType(int type) {
		if (type != TEXT_MESSAGE && type != USER_MESSAGE) 
			throw new RuntimeException("Unknown error type");
		
		messageType = type;
	}

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
