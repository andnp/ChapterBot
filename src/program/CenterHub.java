package program;

import groupmefilter.CurseFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import org.json.JSONException;
import org.json.JSONObject;

import studytablestimesheet.CheckOuts;

import drivers.GroupMe;

public class CenterHub {
	private static String GROUP_TOKEN = "dde55e80aa3301322150761316d99941";
	private static String TEST_BOT_ID = "0a45c83bbc595e96fbd9ab323d";
	private static String TEST_GROUP_ID = "12838361";
	private static String DISCUSSION_BOT_ID = "17577deb3363947a4326cff0cf";
	private static String DISCUSSION_GROUP_ID = "11283452";
	
	private static ServerSocket server_socket;
	
	private static CheckOuts timesheetchecker = new CheckOuts();
	private static CurseFilter test_group;
	private static CurseFilter discussion_filter;
	
	private static boolean server_on = false;
	private static int disc_kick_temp = 0;
	private static int test_kick_temp = 0;
	
	public static void main(String args[]) throws GeneralSecurityException, IOException, JSONException{
//		Turn the bot on
//		turnOn();
		timesheetchecker.start();
		
//		List<Event> events = GoogleCalendar.getEventsContaining("Study Tables");
//		for(Event ev : events){
//			System.out.println(ev.getStart().getDateTime().getValue());
//			
//		}
		
//		Monitor bot status and listen for controls from webpage.
//		server_socket = new ServerSocket(1999);
//		while(true){
//			readClient();
//		}
	}
	
	private static void turnOn(){
		if(server_on) return;
//		Load static classes with necessary information before utilizing functions
		loadStaticClasses();
		
//		Start helper threads
		loadThreads();
		server_on = true;
		monitorStatus();
	}
	
	private static void turnOff() throws IOException{
		if(!server_on) return;
		test_kick_temp = test_group.kick_count;
		disc_kick_temp = discussion_filter.kick_count;
		test_group.kill();
		discussion_filter.kill();
		server_on = false;
		monitorStatus();
	}
	
	private static void readClient() throws IOException{
		Socket client = server_socket.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	
		String input;
		while((input = in.readLine()) != null){
			if(input.equals("0")) turnOff();
			if(input.equals("1")) turnOn();
		}
		client.close();
	}
	
	public static void monitorStatus(){
		try {
			JSONObject json = new JSONObject();
			json.put("Filter Power", server_on);
			json.put("Discussion Filter Kick Count", discussion_filter.kick_count);
			json.put("Test Filter Kick Count", test_group.kick_count);
			PrintWriter file_writer = new PrintWriter("botstatus.json");
			file_writer.println(json.toString(1));
			file_writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} 
	}
	
	private static void loadThreads(){
		discussion_filter = new CurseFilter(2001, DISCUSSION_BOT_ID, DISCUSSION_GROUP_ID, "Discussion Filter");
		discussion_filter.kick_count = disc_kick_temp;
		discussion_filter.start();
		test_group = new CurseFilter(2000, TEST_BOT_ID, TEST_GROUP_ID, "Test Filter");
		test_group.kick_count = test_kick_temp;
		test_group.start();
	}
	
	private static void loadStaticClasses(){
		GroupMe.load(GROUP_TOKEN);
//		GoogleCalendar.load();
	}
}
