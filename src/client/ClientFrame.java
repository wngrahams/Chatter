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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClientFrame extends JFrame implements ActionListener, ListSelectionListener{
	
	private JTabbedPane display;
	private JButton sendButton;
	private JTextField textEntry;
	private JScrollPane usersPanel;
	
	private ArrayList<JTextArea> textDisplays = new ArrayList<JTextArea>(1);
	
	private JList<User> userList;
	private DefaultListModel<User> listModel;
	
	public ClientFrame() {
		super();
		initializePanels();
		this.setBackground(Color.LIGHT_GRAY);
		
		setVisible(true);
	}
	
	public void addNewUser(User newUser) {
	    listModel.addElement(newUser);

	    pack();
		repaint();
	}
	
	private void initializePanels() {
		setTitle("Chatter Client");
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
	
	private void initializeUsersPanel() {
		usersPanel.setPreferredSize(new Dimension(120, 400));
		
		TitledBorder title = BorderFactory.createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
				"<html><h3>Connected users:</h3></html>");
		title.setTitleJustification(TitledBorder.LEFT);
		usersPanel.setBorder(title);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendButton || e.getSource() == textEntry) {
			String text = textEntry.getText();
			if (text != null && !text.isEmpty()) {				
				JTextArea currentTextDisplay = textDisplays.get(display.getSelectedIndex());
				currentTextDisplay.append(text + '\n');
				textEntry.setText(null);
				
				currentTextDisplay.setCaretPosition(currentTextDisplay.getDocument().getLength());
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		User selected = userList.getSelectedValue();
		int tabIndex = display.indexOfTab(selected.getNickname());
		
		if (tabIndex != -1) {
			display.setSelectedIndex(tabIndex);
		}
		else {
			JTextArea textArea = new JTextArea();
		    textArea.setEditable(false);
		    
		    String tooltip = "Private message with " + selected.getNickname();
		    
			display.addTab(selected.getNickname(), null, new JScrollPane(textArea), tooltip);
			textDisplays.add(textArea);
		}
	}
	
	// TODO: name deselection on click

}
