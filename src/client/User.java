package client;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
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
	
	@Override
	public String toString() {
		if (nickname == null)
			return "server";
		
		return nickname;
	}
}
