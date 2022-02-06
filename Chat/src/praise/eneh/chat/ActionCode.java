/**
 * @author Praise Chinedu-Eneh
 * 
 * Purpose: The server must ask the client to submit a name. The client then must send a name. The server must make sure it is a valid name. 
 * If it's a valid name, the server must let the client know the name was accepted. If not, the server must let the client know it was rejected.
 * If the name was rejected, the client must try logging in with a different name or leave the chat. All this communication is handled with
 * action codes (codes that identify the purpose of the messages sent back and forth)
 * 
 */

package praise.eneh.chat;

public class ActionCode {
	
	//Action codes send from the server to the client
	public static final String SUBMIT = "S";
	public static final String ACCEPTED = "A";
	public static final String REJECTED = "R";
	public static final String CHAT = "C";
	
	//Action codes sent from the client to the server
	public static final String NAME = "N";
	public static final String BROADCAST = "B";
	public static final String QUIT = "Q";
	
	
	


}
