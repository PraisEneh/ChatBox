/**
 * @author Praise Chinedu-Eneh
 * Purpose: This is the client side of the Chat program. Any number of clients may tune into the sever and talk to one another.
 * 
 */

package praise.eneh.chat;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import praise.eneh.networking.LogInDialog;

public class Chat extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int PORT_NUMBER = 63458;
	
	private String name = "Praise";
	private String host = "localhost";
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	//created a text area to display chat messages and and input field
	//for entering text
	private JTextArea chatArea = new JTextArea(20,20);
	private JTextArea inputArea = new JTextArea(3,20);
	
	//Change the Build path for the Chat Project, by adding the Networking Project
	//add a private LogInDialog instance variable named logInDialog, with "Chat"
	//as the app name
	private LogInDialog logInDialog = new LogInDialog("Chat"); 
	
	
	//Methods
	//
	//
	public void run() {
		try {
			//set socket to a new socket object using host and PORT_NUMBER
			socket = new Socket(host,PORT_NUMBER);
			//set in to a new BufferedReader by creating a new InputStreamReader and getting the input stream from socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//set out to a new PrintWriter created by getting the output stream from socket
			//automatically flush the output stream
			out = new PrintWriter(socket.getOutputStream(), true);
			
			//in will keep listening for a socket until keepRunning is set to false
			boolean keepRunning = true;
			while(keepRunning == true) {
				String input = in.readLine();
				if(input==null) {
					keepRunning = false;
				}
				//Split the input into its action code and parameters: the first character and the rest of the string.
				//Then send the client's name to the server if the action code is SUBMIT
				else if(input.length()>0) {
					//Created a string named actionCode and set it to the first character of input
					String actionCode = input.substring(0,1);
					//Created a string named parameters and set all but the first character of input
					String parameters = input.substring(1);
					//created a switch blocked based on actionCode
					switch(actionCode) {
					//if it's value is action code SUBMIT
					case ActionCode.SUBMIT:
						//print action code NAME joined with name as line to out
						out.println(ActionCode.NAME+name);
						break;
					case ActionCode.ACCEPTED:
						setTitle(name);
						//sets the typing line to input
						inputArea.requestFocus();
						break;
					case ActionCode.REJECTED:
						JOptionPane.showMessageDialog(this, "Username: "+name+" is not available");
						logIn();
						out.println(ActionCode.NAME+name);
						break;
					case ActionCode.CHAT:
						//creates a "beep" sound in the window
						Toolkit.getDefaultToolkit().beep();
						//append parameters and two newline characters to chatArea
						chatArea.append(parameters+" \n\n");
						//Scroll the chat area
						//getting the whole text of chat area
						String text = chatArea.getText();
						//setting an integer value to the length of that whole text
						int endOfText = text.length();
						//moving the position of chat area to reflect that length
						chatArea.setCaretPosition(endOfText);
						break;
						
						
					}
				}
			}
			
		}//if a connection exception is caught
		catch(ConnectException e) {
			//show JOptionPane dialog stating that the server is not running
			//JOptionPane is a window pane 
			JOptionPane.showMessageDialog(this, "Server is not running");
		}catch(Exception e) {
			//show JOptionPane dialog stating that it lost connection to the server
			JOptionPane.showMessageDialog(this, "Lost connection to the server");
		}finally {
			close();
		}
	}
	public void send() {
		//create a new string named message by getting text from inputArea
		//and trimming it. Trim will remove any extra space on the 
		//beginning and end of the stringS
		String message = inputArea.getText().trim();
		//if the length of message is greater than 0
		//1. print message to console
		//2.set the text in inputArea to an empty string
		if(message.length()>0) {
			
			inputArea.setText("");
			String s = ActionCode.BROADCAST+name+": "+message;
			//we println because of the printwriter. 
			out.println(s);
		}
	}
	//This is the method that the LogInDialog uses to set the host and user name
	private void logIn() {
		logInDialog.setVisible(true);
		if(!logInDialog.isCanceled()) {
			host =  logInDialog.getIpAddress();
			name = logInDialog.getUserName();
			System.out.println("Username: "+name+ " IP address: "+ host);
		}else {
			close();
		}
		
	}
	/*
	 * create a method that will check to see if the socket is null
	 *if so, close the socket
	 */
	public void close() {
		try {
			if(out != null) {
				out.println(ActionCode.QUIT);
			}
			if(socket != null) {
				socket.close();
			}
		}catch(Exception e) {
			
		}
		//exit the program
		System.exit(0);
	}
	/**
	 * This is the method that initializes the GUI contents
	 */
	public void initGUI() {
		TitleLabel titleLabel = new TitleLabel("Chat");
		add(titleLabel, BorderLayout.PAGE_START);
		
		//listeners
		
		//added a window listener created from a new WindowAdapter
		//the WindowAdapter has a method called windowClosing which was overridden
		//to call the close method in our program
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		
		//Main Panel
		
		//created a new JPanel object named mainPanel
		JPanel mainPanel = new JPanel();
		//set the layout for mainPanel to a BoxLayout object for the container
		//mainPanel and aligned it on the y-axis
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		//add mainPanel to the center of the window
		add(mainPanel, BorderLayout.CENTER);
		
		//Chat Area
		
		//set chatArea to be non-editable
		chatArea.setEditable(false);
		//turn on line wrap
		chatArea.setLineWrap(true);
		//turn on wrap style by word for chatArea
		chatArea.setWrapStyleWord(true);
		//create a new Insets object named marginInsets
		//with margins of 3 on each side
		Insets marginInsets = new Insets(3,3,3,3);
		//set the margins for chatArea to marginInsets
		chatArea.setMargin(marginInsets);
		//create a new JScrollPane named chatScrollPane that contains chatArea
		//and uses vertical scroll bars as needed, but never uses horizontal ones
		JScrollPane chatScrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//add chatScrollPane to mainPanel
		mainPanel.add(chatScrollPane);
		
		//Input Area
		
		//Created a new JLabel object named messageLabel, with text
		//"Type your message here"
		JLabel messageLabel = new JLabel("Type your message here:");
		mainPanel.add(messageLabel);
		//turn on line wrap for inputArea
		inputArea.setLineWrap(true);
		//turn on wrap style by word for inputArea
		inputArea.setWrapStyleWord(true);
		//set the margins for inputArea to marginInsets
		inputArea.setMargin(marginInsets);
		/*
		 * We will need to write a key listener to listen for the Enter key and then call
		 * send() when the Enter key is pressed. This is because there is no actionPerformed()
		 * method for JTextArea objects.
		 */
		//added a key listener to inputArea, created from a new key adapter; the overridden
		//keyReleased() method takes one parameter -a KeyEvent object named e- and return nothing
		//When key is released, create an integer named key by getting key code from e.
		//if key is the same as KeyEvent's virtual enter key, call send().
		inputArea.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e){
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER) {
					send();
				}
			}
		});
		//create a new JScrollPane named inputScrollPane that contains chatArea
		//and uses vertical scroll bars as needed, but never uses horizontal ones
		JScrollPane inputScrollPane = new JScrollPane(inputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//add inputScrollPane to mainPanel
		mainPanel.add(inputScrollPane);
		
		//Button Panel
		
		//Create a new JPanel object named buttonPanel
		JPanel buttonPanel = new JPanel();
		//add buttonPanel to the bottom of the window
		add(buttonPanel, BorderLayout.PAGE_END);
		//create a new JButton object named sendButton, with "Send as its label
		JButton sendButton = new JButton("Send");
		//add a new action listener to sendButton, and override the listener's actionPerformed() method
		//actionPerformed should take one parameter-an ActionEvent- and return nothing
		//when button is clicked call send(), passing no arguments
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		//add sendButton to buttonPanel
		buttonPanel.add(sendButton);
		
	}
	
	//Constructors
	//
	//
	
	public Chat() {
		initGUI();
		setTitle("Chat");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		logIn();
		
		//created a new thread object based on THIS class and started the thread
		new Thread(this).start();
	}
	
	public static void main(String[] args) {

		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
			
		}catch(Exception e) {
			
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Chat();
			}
		});

	}

}
