package server;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.corba.se.spi.activation.Server;

import chatter.ChatterClient;
import chatter.ChatterServer;
import chatter.Message;
import client.User;

public class ServerFrame extends JFrame {

	private ChatterServer connectedServer;
	
	private JTabbedPane display;
	private JButton sendButton;
	private JTextField textEntry;
	private JScrollPane usersPanel;
	
	private ArrayList<JTextArea> textDisplays = new ArrayList<JTextArea>(1);
	
	private JList<User> userList;
	private DefaultListModel<User> listModel;
	
	private User recipient;
	
	public ServerFrame(ChatterServer cs) {
		super();
		connectedServer = cs;
		initializePanels();
		this.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
		User setDisplay = new User();
		addNewUser(setDisplay);
		removeUser(setDisplay);
	}
	
	public void addNewUser(User newUser) {
	    listModel.addElement(newUser);

	    pack();
		repaint();
	}
	
	public void displayMessage(Message serverMessage) {
		User messageSender = serverMessage.getSender();

		if (serverMessage.getType() == Message.TEXT_MESSAGE) 
			printToGlobal(serverMessage.getMessage());
		else if (serverMessage.getType() == Message.USER_LOGON_MESSAGE) {
			addNewUser(messageSender);
			printToGlobal(messageSender + " has connected.");
		}
		else if (serverMessage.getType() == Message.USER_LOGOFF_MESSAGE) {
			removeUser(messageSender);
			printToGlobal(messageSender + " has disconnected.");
		}
	}
	
	private void initializePanels() {
		setTitle("Chatter Server");
		setSize(700, 550);
		
		// TODO: Change this so that client is disconnected from server when this is clicked
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	    setLayout(new BorderLayout());
	    
	    JPanel chatPanel = new JPanel();
	    chatPanel.setLayout(new BorderLayout());
	    
	    //Tabbed chat display
	    JTextArea textDisplay = new JTextArea();
	    textDisplays.add(textDisplay);
	    textDisplay.setEditable(false);
	    
	    JScrollPane textScrollPane = new JScrollPane(textDisplay);
	    
	    display = new JTabbedPane();
	    display.setPreferredSize(new Dimension(550, 400));
	    display.addChangeListener(new ChangeListener() {
	    	// updates recipient of message based on currently selected tab
	        public void stateChanged(ChangeEvent e) {
	        	setRecipientFromSelectedTab();
	        }
	    });
	    display.addTab("Server", null, textScrollPane, "Server Messages");
	    
	    chatPanel.add(display, BorderLayout.CENTER);
	    
	    //Text entry panel
//	    JPanel textPanel = new JPanel();
//	    textPanel.setLayout(new BorderLayout());
//	    
//	    textEntry = new JTextField(30);
//	    textEntry.addActionListener(this);
//	    
//	    sendButton = new JButton("Send");
//	    sendButton.addActionListener(this);
//	    
//	    textPanel.add(textEntry, BorderLayout.CENTER);
//	    textPanel.add(sendButton, BorderLayout.EAST);
//	    
//	    chatPanel.add(textPanel, BorderLayout.SOUTH);
	    
	    add(chatPanel, BorderLayout.CENTER);
	    
	    //usersPanel panel
	    listModel = new DefaultListModel<User>();
		userList = new JList<User>(listModel);
//		userList.addListSelectionListener(this);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    usersPanel = new JScrollPane(userList);
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
	
	private void printToGlobal(Message message) {
		JTextArea currentTextDisplay = textDisplays.get(0);
		currentTextDisplay.append(message + "\n");
		
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	private void printToGlobal(String txt) {
		JTextArea currentTextDisplay = textDisplays.get(0);
		currentTextDisplay.append(txt + "\n");
		
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	private void removeUser(User u) {
		listModel.removeElement(u);
		
		pack();
		repaint();
	}
	
	private void setRecipientFromSelectedTab() {
		int currentTab = display.getSelectedIndex();
        if (currentTab == 0) {
        	recipient = null;
        }
        else {
        	// TODO: make this more efficient
        	String recipientName = display.getTitleAt(currentTab);
        	for (int i=0; i<listModel.getSize(); i++) {
        		if(listModel.getElementAt(i).getNickname() == recipientName) {
        			recipient = listModel.getElementAt(i);
        			break;
        		}
        	}
        }
	}
}
