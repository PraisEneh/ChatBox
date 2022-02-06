package praise.eneh.networking;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CheckPortAndIPAddress extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea infoArea = new JTextArea();
	
	private void initGUI() {
		add(infoArea, BorderLayout.CENTER);
	}

	public static void main(String[] args) {

		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception x) {
		}

		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
			new CheckPortAndIPAddress();
		}
		});
	}

	public CheckPortAndIPAddress() {
		initGUI();
		setTitle("Check Port and IP Address");
		// packs window
		pack();
		// centers window by setting its location relative to nothing
		setLocationRelativeTo(null);
		// makes window visible
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//port number
		//creating a server socket at port 0 that connects to a
		//random available port
		try {
			//setting server socket to 0 will automatically find an available port 
			ServerSocket serverSocket = new ServerSocket(0);
			int port = serverSocket.getLocalPort();
			serverSocket.close();
			infoArea.setText("Available Port: "+port);
		//private IP address
			InetAddress host = InetAddress.getLocalHost();
			String hostName = host.getHostName();
			String privateIPAddress = host.getHostAddress();
			infoArea.append("\nHost Name: "+hostName);
			infoArea.append("\nPrivate IP Address: "+privateIPAddress);
		//public IP address
			URL url = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String publicIPAddress = in.readLine();
			in.close();
			infoArea.append("\nPublic IP Address: "+ publicIPAddress);
			
		} catch (IOException e) {
			infoArea.append(e.getMessage());
		}
		

	}

	

}
