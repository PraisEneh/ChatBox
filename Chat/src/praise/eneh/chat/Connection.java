/**
 * @author Praise Chinedu-Eneh
 * Purpose: The server must maintain connections to each client. In order for the connections to remain open and
 * run simultaneously, each connection must be managed on a separate thread. Each connection will need its own
 * socket, reader, and writer, as well as access back to the server.
 */
package praise.eneh.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Runnable {
	
	private final static String DEFAULT_NAME = "(New Client)";
	
	private ChatServer server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String name = DEFAULT_NAME;
	
	
	//Methods
	//
	//
	
	public void run() {
		try {
			//Set in to a new BufferedReader created from a new InputStreamReader, created by getting the input stream from socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//Set out to a new PrintWriter object, created by getting the output stream from socket and flushing it automatically
			out = new PrintWriter(socket.getOutputStream(), true);
			
			/*
			 * Ask the client to submit his name as soon as the connection is running. We should slo maintain a bloolean indicating
			 * whenever the client has yet submitted a valid name
			 */
			sendToClient(ActionCode.SUBMIT);
			boolean validName = false;
			
			boolean keepRunning = true;
			//while keepRunning is true
			while(keepRunning == true) {
				//get input by reading a line of in and set it to input
				String input = in.readLine();
				//log a message to server stating that input was received from name
				server.log(input + " was recieved from " + name);
				//if input is null, set keepRunning to false
				if(input == null) {
					keepRunning = false;
				}else if(input.length() > 0) {
					/*
					 * Now that the server can build a list of connections, we need to check whether a new connection with a submitted
					 * name can be added. If a connection can be added, ad it and let the client know it was added. If the connection
					 * can't be added, reject it and let the client know.
					 */
					//Create a string named actionCode, set to the first character of input
					String actionCode = input.substring(0,1);
					//Create a string named parameter, set to all but the first character of input
					String parameters = input.substring(1);
					//add a switch statement based on action code
					switch(actionCode) {
					//if it's value is action code NAME
					case ActionCode.NAME:
						//create a string named submittedName, set to parameters
						String submittedName = parameters;
						//create a boolean named added by adding a connection to server using this connection and submittedName
						boolean added = server.addConnection(this, submittedName);
						if(added==true) {
							validName = true;
							name = submittedName;
							//send action code ACCEPTED to client
							sendToClient(ActionCode.ACCEPTED);
							String message = ActionCode.CHAT+ name+ " joined the conversation";
									server.broadcast(message);
						}else {
							//send action code REJECTED to client
							sendToClient(ActionCode.REJECTED);
						}
						break;
					case ActionCode.QUIT:
						keepRunning = false;
						break;
					case ActionCode.BROADCAST:
						if(validName) {
							String message = ActionCode.CHAT+parameters;
							server.broadcast(message);
						}
					}
					
				}
			}
		}catch(IOException e) {
			//If an IOException is caught, log a message to server, stating that an error occurred when connecting to a new client
			//or communicating with that client
			server.log("An error occurred when conneting to a new client or when communicating with that client");
			//get the message from e and log that message to server
			server.log(e.getMessage());
				
		}finally {
			quit();
		}
	}
	
	public void sendToClient(String s) {
		out.println(s);
		server.log(s+ " was sent to "+name);
	}
	private void quit() {
		server.log(name + " ended the connection.");
		
		if(!name.equals(DEFAULT_NAME)) {
			server.removeConnection(name);
			if(out != null) {
				String s = ActionCode.CHAT+name+" left the conversation";
				server.broadcast(s);
				out = null;
			}
		}
		try {
			socket.close();
		}catch(IOException e) {
			
		}
	}
	
	//Made the user name for the connection available to the server
	public String getName() {
		return name;
	}
	
	
	//Constructors
	//
	//
	
	//Added a constructor that takes two parameters-- a ChatServer object named server and a Socket object named socket
	public Connection(ChatServer server, Socket socket) {
		//Set this class's server to the value of the server parameter. DO the same with socket
		this.server = server;
		this.socket = socket;
		
		//Start a new thread object with THIS class
		new Thread(this).start();
	}

}
