package groupmechat;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

import drivers.GroupMeIds;
import drivers.GroupMePortListener;

public class GroupMeChat extends GroupMePortListener{
	String bot_id, group_id;
	
	public CurseFilter curse_filter; // allocate space for a curse filter
	public ExperimentalWordCapture word_capture = new ExperimentalWordCapture(); // create an instance of the word capture object
	public CommitteeCommands committee_commands; // allocate space for a committee command object
	
	// control variables accessed through the setters
	private boolean use_ewc = false;
	private boolean use_curse_filter = false;
	private boolean use_mc = false;
	private boolean use_committee_commands = false;
	
	// constructor that takes a port number, the ID of the bot and group, and a name for the chat.
	public GroupMeChat(int port,String bot_id, String group_id, String name) {
		super(port, name); // build a GroupMePortListener on the given port
		this.bot_id = bot_id; // set the bot_id string
		this.group_id = group_id; // set the group_id string
		curse_filter = new CurseFilter(bot_id, group_id); // build a new curse filter
		committee_commands = new CommitteeCommands(bot_id, name); // build a new committee command parser
	}

	/* This function is called from the GroupMePortListener when a new message is posted in a chat
	 * The String message is a string representation of a json object sent to a port listener by GroupMe
	 */
	@Override
	public void readMessage(String message) {
		try {
			JSONObject json = new JSONObject(message); // Parse a json object from the message string
			String user_id = json.getString("user_id"); // get the user_id string from the message
			String name = json.getString("name"); // get the name of the person who posted the message
			message = json.getString("text"); // get the text from this message
			message = message.toLowerCase(); // set the text of this message to lower case for consistant parsing
			
			GroupMeIds.addUserId(name, user_id); // Add the user_id to the list of known user_id's based on rules defined in the GroupMeIds object
			
			if(use_mc) messageCounter(user_id); // Count the messages and their timestamps if counter is turned on
			if(use_curse_filter) curse_filter.filterMessage(name, user_id, message); // Use a curse filter if turned on
			if(use_ewc) word_capture.appendWordsToFile(user_id, message); // Use the word_capture if turned on
			if(use_committee_commands) committee_commands.readMessage(message, name); // Activate Iris commands if turned on
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void messageCounter(String user_id) throws IOException, JSONException{
		long epoch_time = System.currentTimeMillis() / 1000; // get epoch time in seconds
		//Open .json
		String json_text = new String(Files.readAllBytes(Paths.get(chat_name + "_messages.json")), StandardCharsets.UTF_8); // open file 
		JSONObject json = new JSONObject(json_text); // Parse json object from string read from file
		//Append time : id pair
		json.put(epoch_time + "", user_id); // add a new value based on epoch time with the user id
		PrintWriter file_writer = new PrintWriter(chat_name + "_messages.json"); // reopen file for writing
		file_writer.println(json.toString(1)); // write new json object to file
		file_writer.close(); // close file
	}
	
	public void wordCaptureOn(){
		use_ewc = true;
	}
	
	public void wordCaptureOff(){
		use_ewc = false;
	}
	
	public void curseFilterOn(){
		use_curse_filter = true;
	}
	
	public void curseFilterOff(){
		use_curse_filter = false;
	}
	
	public void messageCounterOn(){
		use_mc = true;
	}
	
	public void messageCounterOff(){
		use_mc = false;
	}
	
	public void committeeCommandsOn(){
		use_committee_commands = true;
		committee_commands.start();
	}
	
	public void committeeCommandsOff(){
		use_committee_commands = false;
	}
}
