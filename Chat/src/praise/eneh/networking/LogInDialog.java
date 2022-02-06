/**
 * @author Praise Eneh
 * Purpose: The user of the client program will need to enter the IP address of the server, as well as his own user name.
 * He will do this through a log-in dialog. We will create a log-in dialog, after which the user may click OK to use the entered values
 * or click Cancel to ignore the new values. 
 */

package praise.eneh.networking;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LogInDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FILE_NAME = "LogIn.txt";
	private boolean canceled = false;
	//created a JTextField variable with two columns
	private JTextField ipAddressField = new JTextField(2);
	private JTextField userNameField = new JTextField(2);
	
	//Methods
	//
	//
	
	/**
	 * This is the method that initializes the contents of the GUI
	 */
	private void initGUI() {
		//Main Panel
		
		//Created a new JPanel object named mainPanel
		JPanel mainPanel = new JPanel();
		//set the layout for mainPanel to a new BoxLayout object for the container
		//mainPanel and set it to be aligned on the Y-axis
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		//add mainPanel to the center of the dialog
		add(mainPanel, BorderLayout.CENTER);
		
		JLabel ipAddressLabel = new JLabel("IP Address: ");
		mainPanel.add(ipAddressLabel);
		mainPanel.add(ipAddressField);
		JLabel userNameLabel = new JLabel("User Name: ");
		mainPanel.add(userNameLabel);
		mainPanel.add(userNameField);
		
		//Button Panel
		
		//Created a new JPanel object named buttonPanel
		JPanel buttonPanel = new JPanel();
		//add buttonPanel to the bottom of the dialog
		add(buttonPanel, BorderLayout.PAGE_END);
		//create a new JButton object named okButton, with "OK" as its label
		JButton okButton = new JButton("OK");
		//add a new action listener to okButton, and override the listener's actionPerformed()
		//method. actionPerformed() should take one parameter-an ActionEvent- and return nothing
		//when button is clicked, call ok() passing no arguments
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ok();
			}
		});
		//add okButton to buttonPanel
		buttonPanel.add(okButton);
		//Create a new JButton object named cancelButton, with "Cancel" as its label
		JButton cancelButton = new JButton("Cancel");
		//add a new action listener to cancelButton, and override the listener's actionPerformed()
		//method. actionPerformed() should take one parameter-an ActionEvent- and return nothing
		//when button is clicked, call cancel() passing no arguments
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				cancel();
			}
		});
		//add cancelButton to buttonPanel
		buttonPanel.add(cancelButton);
		//Get the dialog's root pane, and set its default button to okButton
		//the RootPane is associated with the JFrame itself. This is why we're not setting
		//the default button to a panel
		getRootPane().setDefaultButton(okButton);
		
		/*
		 * Cancel logging in if the user closes the log-in dialog
		 */

		//Listeners
		
		//Add a window listener, created from a new WindowAdpter; and override the adapter's
		//windowClosing() method. windowClosing() should take one parameter -a WindowEvent-
		//and return nothing. When the window is closed, call cancel()
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
	}
	
	public void ok() {
		String ipAddress = ipAddressField.getText().trim();
		String userName = userNameField.getText().trim();
		if(ipAddress.length()==0) {
			
			JOptionPane.showMessageDialog(this, "An IP address is required");
		}else if(userName.length()==0) {
			JOptionPane.showMessageDialog(this, "Username Required");
		}else {
			canceled = false;
			setVisible(false);
			//Save the log-in information to a file when the user clicks OK
			try {
				//Create a new BufferedWriter object named out, created from a new File Writer object
				//Created from a new File object, created from FILE_NAME
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(FILE_NAME)));
				//Write ipAddress to out
				out.write(ipAddress);
				//add a new line to out
				out.newLine();
				//Write userName to out
				out.write(userName);
				out.close();
			}catch(IOException e) {
				//if an error is caught, show a JOptionPane stating that there was an error
				//when writing to the file
				JOptionPane.showMessageDialog(this, "Error when writing to file " + FILE_NAME);
			}
			
		}
	}
	public void cancel() {
		canceled = true;
		setVisible(false);
	}
	//Get the IP address and user name from the input dialog
	public String getIpAddress() {
		return ipAddressField.getText().trim();
	}
	public String getUserName() {
		return userNameField.getText().trim();
	}
	public boolean isCanceled() {
		return canceled;
	}
	
	
	
	//End
	//of
	//Methods
	
	//Constructors
	//
	//
	
	public LogInDialog(String appName) {
		//set the dialog's title to indicate that this is the login dialog for appName
		setTitle("Log into "+ appName);
		initGUI();
		//set the dialog modal, so the user must respond to the dialog before doing
		//anything else with the program
		setModal(true);
		pack();
		//center the dialog on the screen by setting its location relative to nothing
		setLocationRelativeTo(null);
		//make the dialog not resizable
		setResizable(false);
		
		//Read the log-in file when the dialog is opened
		try {
			//create a new BufferedReader object named in, created from a new FileReader object,
			//Created from a new File object, created from FILE_NAME
			BufferedReader in = new BufferedReader(new FileReader(new File(FILE_NAME)));
			//Create a new string named ipAddress, by reading a line from in
			String ipAddress = in.readLine();
			//Set the text for ipAddressField to ipAddress
			ipAddressField.setText(ipAddress);
			//Create a new string named userName, by reading a line from in
			String userName = in.readLine();
			//Set the text for userNameField to userName
			userNameField.setText(userName);
			in.close();
		}catch(FileNotFoundException e) {
			
		}catch(IOException e) {
			JOptionPane.showMessageDialog(this, "Error encountered when reading from file "+ FILE_NAME);
		}
		
	}
}
