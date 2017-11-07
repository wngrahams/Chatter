package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chatter.Message;
import chatter.User;

/** 
 * The <code>ClientFrame</code> class extends <code>JFrame</code>;
 * it allows the user to interact with the client by typing and sending 
 * messages, viewing messages already sent, and viewing a list of 
 * connected users.
 * 
 * @author Graham Stubbs (wgs11@georgetown.edu)
 * @author Cooper Logerfo (cml264@georgetown.edu)
 */
@SuppressWarnings("serial")
public class ClientFrame extends JFrame implements ActionListener, ListSelectionListener{
	
	private ChatterClient connectedClient;
	
	private JTabbedPane display;
	private JButton sendButton;
	private JTextField textEntry;
	private JScrollPane usersPanel;
	
	private ArrayList<JTextArea> textDisplays = new ArrayList<JTextArea>(1);
	
	private JList<User> userList;
	private DefaultListModel<User> listModel;
	
	private User recipient;
	
	private static final String TITLE = "Chatter Client - ";
	
	/** 
	 * Creates a new <code>ClientFrame</code> with a given
	 * associated <code>ChatterClient</code> that will send 
	 * and receive messages to other clients via the <code>ChatterServer</code>.
	 * Calls the parent super constructor and initializes the panels 
	 * used in the interface.
	 * 
	 * @param cc The associated <code>ChatterClient</code>
	 */
	public ClientFrame(ChatterClient cc) {
		super();
		connectedClient = cc;
		initializePanels();
		this.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
	}
	
	/** 
	 * Adds a new <code>User</code> to the <code>JList</code> of 
	 * connected users.
	 * 
	 * @param newUser The <code>User</code> to add
	 */
	public void addNewUser(User newUser) {
	    listModel.addElement(newUser);

	    pack();
		repaint();
	}
	
	/** 
	 * Displays a <code>Message</code> to the client text display. The 
	 * way the text is displayed and the resulting behavior of the frame
	 * depends on the type of the <code>Message</code>
	 * 
	 * @param message The <code>Message</code> to display
	 */
	public void displayMessage(Message message) {
		User messageSender = message.getSender();
		User messageRecipient = message.getRecipient();

		if (message.getType() == Message.TEXT_MESSAGE) {
			// navigate to correct tab or create new tab
			if (messageSender != User.SERVER) {
				if (messageRecipient == User.SERVER)
					printToGlobal(message);
				else if (connectedClient.getUser().equals(messageSender))
					printToTab(messageRecipient, message);
				else if (connectedClient.getUser().equals(messageRecipient)) 
					printToTab(messageSender, message);
			}
			else
				printToGlobal(message);
		}
		else if (message.getType() == Message.USER_LOGON_MESSAGE) {
			if (messageSender != connectedClient.getUser()) 
				addNewUser(message.getSender());
		}
		else if (message.getType() == Message.USER_LOGOFF_MESSAGE) {
			if (messageSender != connectedClient.getUser()) 
				removeUser(message.getSender());
		}
		else if (message.getType() == Message.USER_NAME_MESSAGE) { 
			User thisUser = connectedClient.getUser();
			User oldUser = message.getSender();
			if (oldUser.equals(thisUser)) {
				connectedClient.setUser(new User(message.getMessage(), thisUser.getIP()));
				this.setTitle(TITLE + connectedClient.getUser());
			} 
			
			removeUser(oldUser);
			addNewUser(new User(message.getMessage(), oldUser.getIP()));
			pack();
			repaint();
		}
	}

	/** 
	 * Called when a user in the <code>JList</code> is selected, navigates
	 * to the correct tab based on which user was selected. Creates a new
	 * tab if no tab is found for the selected user.
	 * 
	 * @param u The desired <code>User</code> to be found. 
	 */
	private void goToTab(User u) {
		int tabIndex;
		if (null == u) 
			tabIndex = 0;
		else 
			tabIndex = display.indexOfTab(u.getNickname());
		
		if (tabIndex != -1) {
			display.setSelectedIndex(tabIndex);
		}
		else {
			JTextArea textArea = new JTextArea();
		    textArea.setEditable(false);
		    
		    String tooltip = "Private message with " + u.getNickname();
		    
			display.addTab(u.getNickname(), null, new JScrollPane(textArea), tooltip);
			textDisplays.add(textArea);
			display.setSelectedIndex(textDisplays.size() - 1);
		}
	}
	
	/** 
	 * Sets up the positioning and size of the various <code>JPanel</code>s
	 * and their contained <code>JComponent</code>s.
	 */
	private void initializePanels() {
		setTitle(TITLE + connectedClient.getUser());
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
	    display.addChangeListener(new ChangeListener() {
	    	// updates recipient of message based on currently selected tab
	        public void stateChanged(ChangeEvent e) {
	        	setRecipientFromSelectedTab();
	        }
	    });
	    display.addTab("Global Chat", null, textScrollPane, "Global chat with all connected users");
	    
	    chatPanel.add(display, BorderLayout.CENTER);
	    
	    //Text entry panel
	    JPanel textPanel = new JPanel();
	    textPanel.setLayout(new BorderLayout());
	    
	    textEntry = new JTextField(30);
	    textEntry.addActionListener(this);
	    
	    sendButton = new JButton("Send");
	    sendButton.addActionListener(this);
	    
	    textPanel.add(textEntry, BorderLayout.CENTER);
	    textPanel.add(sendButton, BorderLayout.EAST);
	    
	    chatPanel.add(textPanel, BorderLayout.SOUTH);
	    
	    add(chatPanel, BorderLayout.CENTER);
	    
	    //usersPanel panel
	    listModel = new DefaultListModel<User>();
		userList = new JList<User>(listModel);
		userList.addListSelectionListener(this);
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
	 * Given a desired<code>User</code>, this method prints a <code>Message</code>
	 * to that user's corresponding tab
	 * 
	 * @param u The associated <code>User</code>
	 * @param m The <code>Message</code> to display
	 */
	private void printToTab(User u, Message m) {
		int tabIndex;
		if (null == u) {
			printToGlobal(m);
			return;
		}
		else 
			tabIndex = display.indexOfTab(u.getNickname());
		
		JTextArea currentTextDisplay;
		
		if (tabIndex != -1)
			currentTextDisplay = textDisplays.get(tabIndex);
		else {
			JTextArea textArea = new JTextArea();
		    textArea.setEditable(false);
		    
		    String tooltip = "Private message with " + u.getNickname();
		    
			display.addTab(u.getNickname(), null, new JScrollPane(textArea), tooltip);
			textDisplays.add(textArea);
			
			currentTextDisplay = textDisplays.get(textDisplays.size() - 1);
		}
		
		currentTextDisplay.append(m + "\n");
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	/** 
	 * Prints the given message to the tab that corresponds with global
	 * messages.
	 * 
	 * @param message The <code>Message</code> to display
	 */
	private void printToGlobal(Message message) {
		JTextArea currentTextDisplay = textDisplays.get(0);
		currentTextDisplay.append(message + "\n");
		
		currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
	}
	
	/** 
	 * Removes a given user from the <code>JList</code> of <code>User</code>s
	 * 
	 * @param u The <code>User</code> to remove
	 */
	private void removeUser(User u) {
		listModel.removeElement(u);
		
		pack();
		repaint();
	}
	
	/** 
	 * Determines the recipient of the next message sent by the user
	 * based on the tab that is currently open
	 */
	private void setRecipientFromSelectedTab() {
		int currentTab = display.getSelectedIndex();
        if (currentTab == 0) {
        	recipient = null;
        }
        else {
        	String recipientName = display.getTitleAt(currentTab);
        	for (int i=0; i<listModel.getSize(); i++) {
        		if(listModel.getElementAt(i).getNickname() == recipientName) {
        			recipient = listModel.getElementAt(i);
        			break;
        		}
        	}
        	
        	recipient = new User(recipientName);
        }
	}
	
	/** 
	 * Invoked when the enter key or the 'send' button is pressed,
	 * sends a message to a recipient as determined by <code>setRecipientFromSelectedTab()</code> 
	 * 
	 * @param e <code>ActionEvent</code>
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendButton || e.getSource() == textEntry) {
			String text = textEntry.getText();
			if (text != null && !text.isEmpty()) {				
				connectedClient.sendMessage(text, recipient);
				textEntry.setText(null);
			}
		}
	}

	/** 
	 * Invoked when a user is selected by mouse from the <code>JList</code> of
	 * connected users, calls the <code>goToTab()</code> method
	 * 
	 * @param e <code>ListSelectionEvent</code>
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		goToTab(userList.getSelectedValue());
	}
	
}
