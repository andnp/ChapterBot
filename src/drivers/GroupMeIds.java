package drivers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupMeIds {
	public String getUserID(String name) throws IOException, JSONException{
		// Check userid.json for (name, id) pair
		String json_text = new String(Files.readAllBytes(Paths.get("userid.json")), StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(json_text);
		if(json.has(name)) return json.getString(name);
		// If no id, then start checking Groups for ID
		
		// If found in group, add to userid.json
		
		return "";
	}
}
