package groupmefilter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import drivers.GroupMe;
import drivers.GroupMePortListener;

public class CurseFilter extends GroupMePortListener{
	String bot_id;
	String group_id;
	public CurseFilter(int port, String bot_id, String group_id){
		super(port);
		this.bot_id = bot_id;
		this.group_id = group_id;
	}
	
	public void readMessage(String message){
		try {
			JSONObject json = new JSONObject(message);
			String user_id = json.getString("user_id");
			String name = json.getString("name");
			message = json.getString("text");
			message = message.toLowerCase();
			
			if(isInBlacklist(message)){
				try {
					System.out.println("Kicking: " + name);
					AddBack add = new AddBack(24 * 60 * 60 * 1000, group_id,user_id,name);
					GroupMe.removeMember(GroupMe.getMemberID(group_id, name), group_id);
					add.start();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (JSONException e1) {
		}
	}
	
	private boolean isInBlacklist(String message){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("Blacklist.txt"));
		String line;
		while((line = br.readLine()) != null){
			line = line.trim().toLowerCase();
			if((message.contains(" " + line + " ")) || (message.contains(line) && message.length() == line.length()) ) {br.close(); return true;}
		}
		br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
