package drivers;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupMeIds {
	public static String getUserID(String name) throws IOException, JSONException{
		// Check userid.json for (name, id) pair
		String json_text = new String(Files.readAllBytes(Paths.get("userid.json")), StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(json_text);
		if(json.has(name)) return json.getString(name);
		// If no id, then start checking Groups for ID
		JSONObject groups_json = GroupMe.getGroups();
		JSONArray groups_array = groups_json.getJSONArray("response");
		String id = null;
		for(int i = 0; i < groups_array.length(); i++){
			JSONObject group = groups_array.getJSONObject(i);
			JSONArray members = group.getJSONArray("members");
			for(int j = 0; j < members.length(); j++){
				JSONObject mem = members.getJSONObject(j);
				if(mem.getString("nickname").equals(name)){
					id = mem.getString("user_id");
				}
			}
			if(id != null) break;
		}
		// If found in group, add to userid.json
		if(id != null) addUserId(name, id);
		return id;
	}
	public static void addUserId(String name, String id) throws JSONException, IOException{
		String json_text = new String(Files.readAllBytes(Paths.get("userid.json")), StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(json_text);
		if(json.has(name)) return;
		
		json.put(name, id);
		PrintWriter file_writer = new PrintWriter("userid.json");
		file_writer.println(json.toString(1));
		file_writer.close();
	}
}
