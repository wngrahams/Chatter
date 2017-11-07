package server;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import chatter.Message;
import chatter.User;

/** 
 * The <code>ServerFrame</code> class is a GUI that allows interaction
 * with the <code>ChatterServer</code> class and allows for informative messages
 * to be displayed. <code></code>
 * 
 * @author Graham Stubbs (wgs11@georgetown.edu)
 * @author Cooper Logerfo (cml264@georgetown.edu)
 */
@SuppressWarnings("serial")
public class ServerFrame extends JFrame {
	
	private JTabbedPane display;
	private JScrollPane usersPanel;
	
	private ArrayList<JTextArea> textDisplays = new ArrayList<JTextArea>(1);
	
	private JList<User> userList;
	private DefaultListModel<User> listModel;
	
	/** 
	 * Constructs a new frame calling the JFrame super constructor. 
	 * Panels specific to this class are then initialized.
	 */
	public ServerFrame() {
		super();
		initializePanels();
		this.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
		User setDisplay = new User();
		addNewUser(setDisplay);
		removeUser(setDisplay);
	}

	/** 
	 * Adds a <code>User</code> object to be displayed in the 
	 * <code>JList</code> on the left side of the frame.
	 * 
	 * @param newUser the <code>User</code> object to be added
	 */
	public void addNewUser(User newUser) {
	    listModel.addElement(newUser);

	    pack();
		repaint();
	}
	
	/** 
	 * Displays a <code>Message</code> to the server text display. The 
	 * way the text is displayed and the resulting behavior of the frame
	 * depends on the type of the <code>Message</code>
	 * 
	 * @param serverMessage The <code>Message</code> to display
	 */
	public void displayMessage(Message serverMessage) {
		User messageSender = serverMessage.getSender();
		User messageRecipient = serverMessage.getRecipient();

		// if the message is a general text message, just display it
		if (serverMessage.getType() == Message.TEXT_MESSAGE) 
			printToGlobal(serverMessage.getMessage());
		
		// if the message is a log-on message, add the user to the JList and display a log-on notification
		else if (serverMessage.getType() == Message.USER_LOGON_MESSAGE) {
			addNewUser(messageSender);
			printToGlobal(messageSender + " has connected.");
		}
		
		// if the message is a log-off message, remove the user from the JList and display a log-off notification
		else if (serverMessage.getType() == Message.USER_LOGOFF_MESSAGE) {
			removeUser(messageSender);
			printToGlobal(messageSender + " has disconnected.");
		}
		
		// if the message is a name change message, update the JList and display a name change notification
		else if (serverMessage.getType() == Message.USER_NAME_MESSAGE) {
			removeUser(messageSender);
			addNewUser(messageRecipient);
			printToGlobal("User '" + messageSender + "' changed name to: '" + messageRecipient + "'");
		}
	}
	
	/** 
	 * Sets up the positioning and size of the various <code>JPanel</code>s
	 * and their contained <code>JComponent</code>s.
	 */
	private void initializePanels() {
		setTitle("Chatter Server");
		setSize(700, 550);
		
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
	    display.addTab("Server", null, textScrollPane, "Server Messages");
	    
	    chatPanel.add(display, BorderLayout.CENTER);
	    add(chatPanel, BorderLayout.CENTER);
	    
	    //usersPanel panel
	    listModel = new DefaultListModel<User>();
		userList = new JList<User>(listModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    usersPanel = new JScrollPane(userList);
	    initializeUsersPanel();
	    
	    add(usersPanel, BorderLayout.WEST);
	}
	
	/** 
	 * Called by <code>initializePanels()</code>, this method sets up the 
	 * panel on the left side of the frame that will display the <code>JList</code>
	 * of connected users.
	 */
	private void initializeUsersPanel() {
		usersPanel.setPreferredSize(new Dimension(120, 400));
		
		TitledBorder title = BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
				"<html><h3>Connected users:</h3></html>");
		title.setTitleJustification(TitledBorder.LEFT);
		usersPanel.setBorder(title);
	}
	
	/** 
	 * Prints a <code>Message</code> to the text display, adding a 
	 * newline character and reseting the text insertion position
	 * to the end of the displayed text.
	 * 
	 * @param message The <code>Message</code> to display
	 */
	@SuppressWarnings("unused")
	private void printToGlobal(Message message) {
		JTextArea currentTextDisplay = textDisplays.get(0);
		currentTextDisplay.append(message + "\n");
		
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	/** 
	 * Prints a <code>String</code> to the text display, adding a 
	 * newline character and reseting the text insertion position
	 * to the end of the displayed text.
	 * 
	 * @param txt The <code>String</code> to display
	 */
	private void printToGlobal(String txt) {
		JTextArea currentTextDisplay = textDisplays.get(0);
		currentTextDisplay.append(txt + "\n");
		
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	/** 
	 * Removes a given <code>User</code> from the JList display of 
	 * connected users. Called when a user disconnects or changes
	 * names.
	 * 
	 * @param u The <code>User</code> to remove
	 */
	private void removeUser(User u) {
		listModel.removeElement(u);
		
		pack();
		repaint();
	}
}
