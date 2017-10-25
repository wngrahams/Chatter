package chatter;

import client.ClientFrame;
import client.User;

public class ChatterClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientFrame clientFrame = new ClientFrame();
		clientFrame.addNewUser(new User("Graham", "localhost"));
		clientFrame.addNewUser(new User());
	}

}
