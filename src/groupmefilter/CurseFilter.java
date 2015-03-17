package groupmefilter;

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
		if(message.contains("cows")){
			try {
				GroupMe.sendMessage("bad", bot_id);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
