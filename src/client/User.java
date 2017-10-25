package client;

public class User {
	private String nickname;
	private String ipAddress;
	
	public User() {
		nickname = "user";
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
	
	protected void setNickname(String name) {
		nickname = name;
	}
	
	@Override
	public String toString() {
		return nickname;
	}
}
