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
	
	public CurseFilter curse_filter;
	public ExperimentalWordCapture word_capture = new ExperimentalWordCapture();
	public CommitteeCommands committee_commands;
	
	private boolean use_ewc = false;
	private boolean use_curse_filter = false;
	private boolean use_mc = false;
	private boolean use_committee_commands = false;
	
	public GroupMeChat(int port,String bot_id, String group_id, String name) {
		super(port, name);
		this.bot_id = bot_id;
		this.group_id = group_id;
		curse_filter = new CurseFilter(bot_id, group_id);
		committee_commands = new CommitteeCommands(bot_id, name);
	}

	@Override
	public void readMessage(String message) {
		try {
			JSONObject json = new JSONObject(message);
			String user_id = json.getString("user_id");
			String name = json.getString("name");
			message = json.getString("text");
			message = message.toLowerCase();
			
			GroupMeIds.addUserId(name, user_id);
			if(use_mc) messageCounter(user_id);
			
			if(use_curse_filter) curse_filter.filterMessage(name, user_id, message);
			if(use_ewc) word_capture.appendWordsToFile(user_id, message);
			if(use_committee_commands) committee_commands.readMessage(message, name);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void messageCounter(String user_id) throws IOException, JSONException{
		//Get time
		long epoch_time = System.currentTimeMillis() / 1000;
		//Open .json
		String json_text = new String(Files.readAllBytes(Paths.get(chat_name + "_messages.json")), StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(json_text);
		//Append time : id pair
		json.put(epoch_time + "", user_id);
		PrintWriter file_writer = new PrintWriter(chat_name + "_messages.json");
		file_writer.println(json.toString(1));
		file_writer.close();
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
