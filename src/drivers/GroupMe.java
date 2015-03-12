package drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMe {
	static String GROUP_API = "https://api.groupme.com/v3";
	static String GROUP_TOKEN;
	
	public static void load(String token){
		GROUP_TOKEN = token;
	}
	public static void sendMessage(String message, String bot_id) throws IOException, JSONException{
		HttpURLConnection send_connection = (HttpURLConnection) new URL(GROUP_API + "/bots/post?token=" + GROUP_TOKEN).openConnection();
		send_connection.setRequestMethod("POST");
		send_connection.setDoOutput(true);
		System.out.println("here");
		JSONObject post_data = new JSONObject("{\"bot_id\":"+ bot_id + ",\"text\": \""+message+"\"}");
		OutputStream os = send_connection.getOutputStream();
		os.write(post_data.toString().getBytes("UTF-8"));
		BufferedReader r = new BufferedReader(new InputStreamReader(send_connection.getInputStream()));
		String out;
		while((out = r.readLine()) != null){
			System.out.println(out);
		}
		r.close();
	}
}
