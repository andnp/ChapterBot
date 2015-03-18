package groupmefilter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;

import drivers.GroupMe;
import drivers.GroupMePortListener;

public class CurseFilter extends GroupMePortListener{
	String bot_id;
	public CurseFilter(int port, String bot_id){
		super(port);
		this.bot_id = bot_id;
	}
	
	public void readMessage(String message){
		message = message.toLowerCase();
		message = message.split(": ")[1];
		if(isInBlacklist(message)){
			try {
				GroupMe.sendMessage("bad", bot_id);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	
	private boolean isInBlacklist(String word){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("Blacklist.txt"));
		String line;
		while((line = br.readLine()) != null){
			line = line.trim().toLowerCase();
			if(line.equals(word)) {br.close(); return true;}
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
