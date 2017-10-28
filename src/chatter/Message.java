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

	public Message(User to, User from, String text) {
		setRecipient(to);
		setSender(from);
		setMessage(text);
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getRecipient() {
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
	
}
