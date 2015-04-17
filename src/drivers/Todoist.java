package drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Todoist {
	final static String token = "bd3a6a9f661bc3b4003c84cbbd82ce6a05a470c0";
	final static String TODO_API = "https://todoist.com/API/";
	final static String TODO_TOKEN= "token=" + token;
	
	static HashMap<String, String> project_id_map = new HashMap<String, String>();
	
	public static void init(){
		project_id_map.put("Org Dev", "140264466");
		project_id_map.put("Recruitment", "139565328");
		project_id_map.put("Social", "139565348");
		project_id_map.put("Philanthropy", "139565368");
		project_id_map.put("PR", "139565384");
		project_id_map.put("Education", "139565383");
	}
	
	public static List<String> getUncompletedTasks(String committee) throws JSONException, IOException{
		if(committee.equals("Test_Filter")) committee = "Org Dev";
		HttpURLConnection conn = (HttpURLConnection) new URL(TODO_API + "getUncompletedItems?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee)).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String out;
		String total = "";
		while((out = r.readLine()) != null){
			total += out;
		}
		//System.out.println(total);
		r.close();
		JSONArray json_array = new JSONArray(total);
		JSONObject json;
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < json_array.length(); i++){
			json = json_array.getJSONObject(i);
			String todo = json.getString("content");
			if(!json.getString("date_string").isEmpty()) todo += " due: " + json.getString("date_string");
			list.add(todo);
		}
		//System.out.println(json_array.getJSONObject(0).toString(1));
		return list;
	}
	
	public static List<String> getItemByDate(String date, String committee) throws IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(TODO_API + "getUncompletedItems?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee)).openConnection();
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
		JSONArray json_array = new JSONArray(total);
		JSONObject json;
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < json_array.length(); i++){
			json = json_array.getJSONObject(i);
			if(json.getString("date_string").equals(date)){
				list.add(json.getString("content"));
			}
		}
		return list;
	}
	
	public static int getItemId(String item, String committee) throws MalformedURLException, IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(TODO_API + "getUncompletedItems?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee)).openConnection();
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
		JSONArray json_array = new JSONArray(total);
		JSONObject json;
		for(int i = 0; i < json_array.length(); i++){
			json = json_array.getJSONObject(i);
			if(json.getString("content").toLowerCase().replaceAll(" ", "").contains(item.toLowerCase().replaceAll(" ", ""))) return json.getInt("id");
		}
		
		return 0;
	}
	
	public static ArrayList<Integer> getItemIds(String committee) throws MalformedURLException, IOException, JSONException{
		HttpURLConnection conn = (HttpURLConnection) new URL(TODO_API + "getUncompletedItems?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee)).openConnection();
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
		JSONArray json_array = new JSONArray(total);
		JSONObject json;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < json_array.length(); i++){
			json = json_array.getJSONObject(i);
			ret.add(json.getInt("id"));
		}
		
		return ret;
	}
	
	public static void addItem(String item, String date, String committee) throws MalformedURLException, IOException{
		String url = "";
		if(date.equals("")){
			url = TODO_API + "addItem?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee) + "&priority=1"  + "&content=" + item;
		} else {
			url = TODO_API + "addItem?" + TODO_TOKEN + "&project_id=" + project_id_map.get(committee) + "&priority=1"  + "&content=" + item + "&date_string=" + date;
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(url.replaceAll(" ", "%20")).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		r.close();
	}
	
	public static void completeItem(int item, String committee) throws MalformedURLException, IOException, JSONException{
		ArrayList<Integer> ids = getItemIds(committee);
		String url = TODO_API + "completeItems?" + TODO_TOKEN + "&ids=[" + ids.get(item) + "]";
		HttpURLConnection conn = (HttpURLConnection) new URL(url.replaceAll(" ", "%20")).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		r.close();
	}
}
