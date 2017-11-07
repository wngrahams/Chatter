package chatter;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * User class used to consolidate a client's name/nickname and IP Address into one object.
	 *  A new User object is created when the ChatterClient class is run. 
	 */
	private static final long serialVersionUID = 8774797718179737042L;
	private String nickname;
	private String ipAddress; 
	
	public static final User SERVER = null;
	
	public User() {
		int rand = (int) (Math.random() * 100000 + 1);
		nickname = "user" + Integer.toString(rand);
		ipAddress = "localhost";
	}
	
	public User(String name) {
		this(name, "localhost");
	}
	
	public User(String name, String ip) {
		setNickname(name);
		ipAddress = ip;
	}
	
	/**
	 * Used to overwrite an existing User object. 
	 */
	public User(User otherUser) {
		setNickname(otherUser.getNickname());
		ipAddress = otherUser.getIP();
	}
	
	public String getIP() {
		return ipAddress;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String name) {
		nickname = name;
	}
	
	/**
	 * Returns User object as a string.
	 */
	@Override
	public String toString() {
		if (nickname == null)
			return "server";
		
		return nickname;
	}
	
	/**
	 * Method for comparing User objects. Used to identify if Users have the same name/nickname. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false;
			
		User userObj = (User)obj;
		if (userObj.getNickname().equals(this.getNickname()) && userObj.getIP().equals(this.getIP()))
			return true;
		
		else 
			return false;
	}
}
