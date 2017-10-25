package server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerFrame extends JFrame {

	   JPanel labelPanel1;
	   JPanel labelPanel2;
	   
	   JPanel updatePanel;
	   JPanel userPanel;
	   
	   JPanel container;

	   JLabel clientLabel;
	   JLabel updateLabel;
	   //JLabel label2;

	   String clientInfo;
	   
	   //JTextField text;
	   private JTextArea clientUpdate;
	   private JTextArea clients;
	   
	   
	   /*
	    * constructor, calls initialize 
	    */ 
	   public ServerFrame()
	   {
		   InitializePanel();
	   }
	   
	   
	   
	   /*
	    * initialize method that creates the GUI
	    */
	   public void InitializePanel()
	   {
		   setTitle("Server GUI");
		   setDefaultCloseOperation(EXIT_ON_CLOSE);
		   setLayout( new BorderLayout() );
		   
		   clientLabel = new JLabel();
		   clientLabel.setText("Clients");
		   clientLabel.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		   clientLabel.setHorizontalAlignment(JLabel.CENTER);
		   
		   container = new JPanel();
		   container.setLayout(new GridLayout(1,2));
		   /*
		   label2 = new JLabel();
		   label2.setText("UserID(Nickname) | <User IP address>");
		   label2.setHorizontalAlignment(JLabel.CENTER);
			*/
		   
		   labelPanel1 = new JPanel();
		   labelPanel1.add(clientLabel, BorderLayout.NORTH);
		   
		   updateLabel = new JLabel();
		   updateLabel.setText("Client Updates");
		   updateLabel.setHorizontalAlignment(JLabel.CENTER);
		   updateLabel.setFont(new Font("TimesRoman", Font.PLAIN, 25));
	       
	       labelPanel2 = new JPanel();
	       labelPanel2.add(updateLabel, BorderLayout.NORTH);
		   
		   updatePanel = new JPanel(new BorderLayout());
	       clientUpdate = new JTextArea(100,100);
	       clientUpdate.setEditable(false);
	       
	       userPanel = new JPanel(new BorderLayout());
	       clients = new JTextArea(100,100);
	       clients.setEditable(false);
	       
	       userPanel.add(new JScrollPane(clients));
	       userPanel.add(labelPanel1, BorderLayout.NORTH);
	       
	       updatePanel.add(new JScrollPane(clientUpdate));
	       updatePanel.add(labelPanel2, BorderLayout.NORTH);
	       
	       addClientUpdate("User x changed nickname to y\n");
	       addClientUpdate("User x logged on at Time:...");
	       addClient("ID, nickanme... etc");

		   container.add(userPanel); 
		   container.add(updatePanel);
		   add(container);
		   
		   setSize( new Dimension( 500,500 ) );
		   setVisible(true);
	   }
	   
	   
	   
	   /*
	    * Main fn, to instantiate the gui
	    */
	   public static void main( String[] args )
	   {
	      new ServerFrame();
	   }
	   
	   
	   
	   /*
	    * So I'm thining we can use a function similar to this to pull the user's ID and IP address
	    * then add them to a string and store them (and output them on the "Clients" panel
	    */  
	   public String getUserInfo()
	   {
		   //actually, void and just update member var "clientInfo"
		   String x = "y";
		   
		   //String clientPort = sock.getLocalPort().toString();
		   //String clientIP = sock.getLocalSocketAddress.toString();  ... or "remote"?
		   
		   //String clientInfo = clientPort + ClientIP
		   
		   return x;
	   }
	    
	   
	   
	   /*
	    *These two are for adding stuff to the text areas.
	    */
	    private void addClientUpdate(String str) {
	    		clientUpdate.append(str);
	    }
	     
	    private void addClient(String string) {
    		clients.append(string);
	    }
	     
	

}
