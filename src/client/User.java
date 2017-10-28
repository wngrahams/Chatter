package client;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8774797718179737042L;
	private String nickname;
	private String ipAddress; 
	
	public User() {
		int rand = (int) (Math.random() * 100000 + 1);
		nickname = "user" + Integer.toString(rand);
		ipAddress = "localhost";
	}
	
	public User(String name, String ip) {
		nickname = name;
		ipAddress = ip;
	}
	
	public String getIP() {
		return ipAddress;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	//changed to public
	public void setNickname(String name) {
		nickname = name;
	}
	
	@Override
	public String toString() {
		return nickname;
	}
}
