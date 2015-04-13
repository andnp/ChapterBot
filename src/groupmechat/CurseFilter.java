package groupmechat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONException;
import program.CenterHub;
import drivers.GroupMe;

public class CurseFilter{
	String bot_id;
	String group_id;
	public int kick_count = 0;

	public CurseFilter(String bot_id, String group_id){
		this.bot_id = bot_id;
		this.group_id = group_id;
	}
	public void filterMessage(String name, String user_id, String message){	
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
