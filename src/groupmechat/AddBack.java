package groupmechat;

import java.io.IOException;

import org.json.JSONException;

import drivers.GroupMe;

public class AddBack extends Thread {
	private long time;
	private String group_id, member_id, member_name;
	public void run(){
		try {
			Thread.sleep(time);
			GroupMe.addMember(group_id, member_id, member_name);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	AddBack(long time, String group_id, String member_id, String member_name){
		this.time = time;
		this.group_id = group_id;
		this.member_id = member_id;
		this.member_name = member_name;
	}
}
