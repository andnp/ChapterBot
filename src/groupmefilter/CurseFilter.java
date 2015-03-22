package groupmefilter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import program.CenterHub;

import drivers.GroupMe;
import drivers.GroupMeIds;
import drivers.GroupMePortListener;

public class CurseFilter extends GroupMePortListener{
	String bot_id;
	String group_id;
	public int kick_count = 0;
	public CurseFilter(int port, String bot_id, String group_id, String name){
		super(port, name);
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
			
			GroupMeIds.addUserId(name, user_id);
			
			if(isInBlacklist(message)){
				try {
					kick_count++;
					CenterHub.monitorStatus();
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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private boolean isInBlacklist(String message){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("Blacklist.txt"));
		String line;
		while((line = br.readLine()) != null){
			line = line.trim().toLowerCase();
			if(message.contains(" " + line + " ")) {br.close(); return true;}
			for(String word : message.split(" ")){
				if(word.equals(line)) {br.close(); return true;}
			}
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
