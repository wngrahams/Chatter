package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import javafx.scene.layout.Border;

public class ClientFrame extends JFrame {
	
	private JTabbedPane display;
	private JTextField textEntry;
	private JPanel usersPanel;
	
//	private ArrayList<User> userList = new ArrayList<User>();

	public ClientFrame() {
		super();
		initializePanels();
		this.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
	}
	
	public void addNewUser(User newUser) {
//		JLabel newUser = new JLabel(nickname);
//	    newUser.setBorder(new LineBorder(Color.DARK_GRAY));
//	    
	    System.out.println("new user: " + newUser.getNickname());
	    
	    JLabel userLabel = new JLabel(newUser.getNickname());
	    usersPanel.add(userLabel);

//		String currentUsers = usersPanel.getText();
//		String newUsers = currentUsers + "\n" + nickname;
//		System.out.println("new list: " + newUsers);
//		usersPanel.setText(newUsers);
	    pack();
		repaint();
	}
	
	private void initializePanels() {
		setTitle("Chatter Client");
		setSize(700, 550);
		
		// TODO: Change this so that client is disconnected from server when this is clicked
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	    setLayout(new BorderLayout());
	    
	    //Tabbed chat display
	    display = new JTabbedPane();
	    display.setPreferredSize(new Dimension(550, 400));
	    display.addTab("Global Chat", null, null, "Global chat with all connected usersPanel");
	    
	    add(display, BorderLayout.CENTER);
	    
	    
	    //Text entry panel
	    textEntry = new JTextField();
//	    textEntry.setPreferredSize(new Dimension(700, 50));
	    
	    add(textEntry, BorderLayout.SOUTH);
	    
	    //usersPanel panel
	    usersPanel = new JPanel();
	    initializeUsersPanel();
	    
	    add(usersPanel, BorderLayout.WEST);
	}
	
	private void initializeUsersPanel() {
		usersPanel.setPreferredSize(new Dimension(120, 400));
		
		TitledBorder title = BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
				"<html><h3>Connected users:</h3></html>");
		title.setTitleJustification(TitledBorder.LEFT);
		usersPanel.setBorder(title);

	}

}
