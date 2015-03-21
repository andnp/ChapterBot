package drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupMe {
	static String GROUP_API = "https://api.groupme.com/v3";
	static String GROUP_TOKEN;
	
	// must be envoked before calling methods from this class
	public static void load(String token){
		GROUP_TOKEN = token;
	}
	// takes the ID of a bot and sends a message through the GroupMe API.
	public static void sendMessage(String message, String bot_id) throws IOException, JSONException{
		HttpURLConnection send_connection = (HttpURLConnection) new URL(GROUP_API + "/bots/post?token=" + GROUP_TOKEN).openConnection();
		send_connection.setRequestMethod("POST");
		send_connection.setDoOutput(true);
		
		JSONObject post_data = new JSONObject("{\"bot_id\":"+ bot_id + ",\"text\": \""+message+"\"}");
		OutputStream os = send_connection.getOutputStream();
		os.write(post_data.toString().getBytes("UTF-8"));
		BufferedReader r = new BufferedReader(new InputStreamReader(send_connection.getInputStream()));
		r.close();
	}
	
	public static void sendDirectMessage(String message, String user_id) throws IOException, JSONException{
		HttpURLConnection send_connection = (HttpURLConnection) new URL(GROUP_API + "/direct_messages?token=" + GROUP_TOKEN).openConnection();
		send_connection.setRequestMethod("POST");
		send_connection.setDoOutput(true);
		send_connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		send_connection.setRequestProperty("Accept", "application/json");
		
		JSONObject post_data = new JSONObject();
		JSONObject message_data = new JSONObject();
		message_data.put("source_guid", "GUID");
		message_data.put("recipient_id", user_id);
		message_data.put("text", message);
		post_data.put("direct_message", message_data);
		OutputStream os = send_connection.getOutputStream();
		os.write(post_data.toString().getBytes("UTF-8"));
		BufferedReader r = new BufferedReader(new InputStreamReader(send_connection.getInputStream()));
		r.close();
	}
	
	public static void removeMember(String member_id, String group_id) throws MalformedURLException, IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(GROUP_API + "/groups/"  + group_id + "/members/" + member_id + "/remove?token=" + GROUP_TOKEN).openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		
		OutputStream os = conn.getOutputStream();
		os.write(new byte[0]);
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		r.close();
	}
	
	public static void addMember(String group_id, String member_id, String member_name) throws IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(GROUP_API + "/groups/"  + group_id + "/members/add?token=" + GROUP_TOKEN).openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		
		JSONObject post_data = new JSONObject();
		JSONObject mem = new JSONObject();
		mem.put("nickname", member_name);
		mem.put("user_id", member_id);
		mem.put("guid", "GUID-1");
		post_data.append("members", mem);
		OutputStream os = conn.getOutputStream();
		os.write(post_data.toString().getBytes("UTF-8"));
		//PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		//writer.append(post_data.toString());
		//writer.flush();
		os.flush();
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		r.close();
	}
	
	public static String getMemberID(String group_id, String name) throws IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(GROUP_API + "/groups/"  + group_id + "?token=" + GROUP_TOKEN).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String out;
		String total = "";
		while((out = r.readLine()) != null){
			total += out;
		}
		r.close();
		JSONObject json = new JSONObject(total);
		JSONArray array = json.getJSONObject("response").getJSONArray("members");
	
		for(int i = 0; i < array.length(); i++){
			if(array.getJSONObject(i).getString("nickname").equals(name)){
				return array.getJSONObject(i).getString("id");
			}
		}
		return "";
	}
	
	public static String getUserID(String group_id, String name) throws IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(GROUP_API + "/groups/"  + group_id + "?token=" + GROUP_TOKEN).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String out;
		String total = "";
		while((out = r.readLine()) != null){
			total += out;
		}
		r.close();
		JSONObject json = new JSONObject(total);
		JSONArray array = json.getJSONObject("response").getJSONArray("members");
	
		for(int i = 0; i < array.length(); i++){
			if(array.getJSONObject(i).getString("nickname").equals(name)){
				return array.getJSONObject(i).getString("user_id");
			}
		}
		return "";
	}
	
	public static JSONObject getGroups() throws MalformedURLException, IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(GROUP_API + "/groups" + "?token=" + GROUP_TOKEN).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String out;
		String total = "";
		while((out = r.readLine()) != null){
			total += out;
		}
		r.close();
		return new JSONObject(total);
	}
}
