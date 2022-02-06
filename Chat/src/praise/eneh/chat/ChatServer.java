/**
 * @author Praise Chinedu-Eneh
 * Purpose: The Chat server is just as it's title. Serves as a server for communication between clients.
 * This server includes a GUI in order to more easily manage starting and stopping the server.
 * 
 * The server must also maintain a list of clients in the case of another user name existing, the server should reject the name.
 */
package praise.eneh.chat;

import java.util.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

public class ChatServer extends JFrame implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//text area created with 10 rows and 30 columns
	private JTextArea logArea = new JTextArea(10, 30);
	//start button created with the string title "start"
	private JButton startButton = new JButton("Start");
	private static final int PORT_NUMBER = 63458;
	private ServerSocket serverSocket;
	
	//Create a list for holding the Conections in server
	//created a private ArrayList instance variable of type Connection, named connections, initialized by calling it's constructor
	private ArrayList <Connection> connections = new ArrayList<Connection>();
	
	
	
	
	
	
	//start
	//of
	//methods
	
	/*
	 * starts server on a separate thread, so when it waits for the client to make contact
	 * it doesn't block window events handled by the event dispatch thread
	 */
	private void startServer() {
		startButton.setText("Stop");
		new Thread(this).start();
		
	}
	
	public void log(String message) {
		Date time = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
		String timeStamp = dateFormat.format(time);
		logArea.append(timeStamp+" "+message+"\n");
	}
	
	private void stop() {
		if(serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			}catch(Exception e) {
				log("Unable to close the server connection");
				log(e.getMessage());
			}
		}
	}
	
	/*
	 * Broadcast to all clients when a new client enters the conversation
	 * 
	 */
	public void broadcast(String s) {
		synchronized(connections) {
			for(int i =0; i<connections.size(); i++) {
				Connection connection = connections.get(i);
				connection.sendToClient(s);
			}
		}
	}
	
	/*
	 * Adding a method to add a connection to the list. Keep in mind that checking and modifying the contents
	 * of the list should be synchronized so that only one thread my manipulate the list at a time
	 * 
	 * It takes a connection object, newConnection and a string newName
	 * 
	 * The synchronized mechanism was Java's first mechanism for synchronizing access to objects shared by multiple threads.
	 * All synchronized blocks synchronized on the same object can only have one thread executing inside them at the same time. 
	 * All other threads attempting to enter the synchronized block are blocked until the thread inside the synchronized block exits the block.
	 */
	public boolean addConnection(Connection newConnection, String newName) {
		boolean added = false;
		boolean found = false;
		//synchronized block statment based on the arraylist of names
		synchronized(connections) {
			//loop through all elements in connections, while found is false, using an integer named i
			for(int i=0; i< connections.size() && !found; i++) {
				//Create a connection object by getting the element in connections at i
				Connection connection = connections.get(i);
				//create a string named name, by getting name from connection
				String name = connection.getName();
				if(newName.equals(name)) {
					found = true;
				}
			}
			if(!found) {
				connections.add(newConnection);
				added = true;
			}
		}
		return added;
		
	}
	
	//Add a method to remove a connection from the list
	public void removeConnection(String removeName) {
		boolean found = false;
		synchronized(connections) {
			for(int i = 0; i < connections.size() && !found; i++) {
				//we need to find the connection itself first
				Connection connection = connections.get(i);
				//then we can get the name from the connection
				String name = connection.getName();
				if(removeName.equals(name)) {
					found = true;
					connections.remove(i);
				}
				
			}
		}
	}
	
	
	public void run() {
		log("Server is running");
		
		try {
			serverSocket = new ServerSocket(PORT_NUMBER);
			while(true) {
				Socket socket = serverSocket.accept();
				log("New connetion started");
				/*
				 * Creating the connection when a client contacts the server
				 * using the connection class
				 */
				//Create a new Connection using this server and socket
				new Connection(this, socket);
			}
		}catch(IOException e) {
			log("Exception caught when listening on port "+PORT_NUMBER+".");
			log(e.getMessage());
		}
		finally {
			try {
				if(!serverSocket.isClosed()) {
					serverSocket.close();
				}
			}catch(Exception e) {
				
			}
		}
	}
	
	
	
	
	/**
	 * This is the method that initializes the GUI contents
	 */
	private void initGUI() {
		TitleLabel titleLabel = new TitleLabel("Chat Server");
		add(titleLabel,BorderLayout.PAGE_START);
		
		//main panel
		JPanel mainPanel = new JPanel();
		//set layout for mainPanel to a new BoxLayout object for the container "mainPanel"
		//and aligned on the y-axis
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		add(mainPanel,BorderLayout.CENTER);
		
		//log area
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		mainPanel.add(scrollPane);
		DefaultCaret caret = (DefaultCaret)logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//button panel
		JPanel buttonPanel = new JPanel();
		add(buttonPanel,BorderLayout.PAGE_END);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServer();
			}
		});
		buttonPanel.add(startButton);
		getRootPane().setDefaultButton(startButton);
		
		//listeners
		//
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
				System.exit(0);
			}
		});
		
	}
	//end
	//of
	//methods
	
	
	
	//start
	//of
	//constructors
	
	public ChatServer() {
		initGUI();
		setTitle("Chat Server");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
	}
	
	public static void main(String[] args) {
		
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		}catch(Exception e) {
			
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new ChatServer();
			}
		});

	}
	
	//end
	//of
	//constructors

	

	

}
