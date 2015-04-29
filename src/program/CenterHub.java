package program;

import groupmechat.GroupMeChat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;



//import studytablestimesheet.CheckOuts;


import drivers.GoogleCalendar;
import drivers.GroupMe;
import drivers.Todoist;

public class CenterHub {
	private static String GROUP_TOKEN = "";
	private static String TEST_BOT_ID = "0a45c83bbc595e96fbd9ab323d";
	private static String TEST_GROUP_ID = "12838361";
	private static String DISCUSSION_BOT_ID = "b4f94be6d7c65a3f38b44b4fca";
	private static String DISCUSSION_GROUP_ID = "13162200";
	private static String NSF_BOT_ID = "2a4d828677d2721cd9e84d2b5d";
	private static String NSF_GROUP_ID = "13079697";
	private static String ORG_DEV_BOT_ID = "07a6dcd93e0429107301bed316";
	private static String ORG_DEV_GROUP_ID = "13229402";
	private static String RECRUITMENT_BOT_ID = "3cd74ee74f54fc3bc7c0c70969";
	private static String RECRUITMENT_GROUP_ID = "10911018";
	private static String SOCIAL_BOT_ID = "648c163212b97f2fceffec9633";
	private static String SOCIAL_GROUP_ID = "11012799";
	private static String PHIL_BOT_ID = "6be869349880ef444171a2046e";
	private static String PHIL_GROUP_ID = "11012911";
	
	private static ServerSocket server_socket;
	
//	private static CheckOuts timesheetchecker = new CheckOuts();
	private static GroupMeChat test_group;
	private static GroupMeChat discussion;
	private static GroupMeChat nsf;
	private static GroupMeChat org_dev;
	private static GroupMeChat recruitment;
	private static GroupMeChat social;
	private static GroupMeChat phil;
	
	private static boolean server_on = false;
	private static int disc_kick_temp = 0;
	private static int test_kick_temp = 0;
	
	public static void main(String args[]) throws GeneralSecurityException, IOException, JSONException{
//		read groupme token from file
		Scanner sc = new Scanner(new File("groupmetokens.txt"));
		GROUP_TOKEN = sc.nextLine();
		sc.close();
		
//		Turn the bot on
		turnOn();
		//timesheetchecker.start();
		
//		List<Event> events = GoogleCalendar.getEvents();
//		for(Event ev : events){
//			System.out.println(ev.getSummary());
//		}
//		GoogleCalendar.quickAddEvent("this is a test today at 1pm");
		
//		Monitor bot status and listen for controls from webpage.
		server_socket = new ServerSocket(1999);
		while(true){
			readClient();
		}
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
		test_kick_temp = test_group.curse_filter.kick_count;
		disc_kick_temp = discussion.curse_filter.kick_count;
		test_group.kill();
		discussion.kill();
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
			json.put("Discussion Filter Kick Count", discussion.curse_filter.kick_count);
			json.put("Test Filter Kick Count", test_group.curse_filter.kick_count);
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
		discussion = new GroupMeChat(2001, DISCUSSION_BOT_ID, DISCUSSION_GROUP_ID, "Discussion_Filter");
		discussion.curse_filter.kick_count = disc_kick_temp;
		discussion.curseFilterOn();
		discussion.wordCaptureOn();
		discussion.messageCounterOn();
		discussion.start();
		test_group = new GroupMeChat(2000, TEST_BOT_ID, TEST_GROUP_ID, "Test_Filter");
		test_group.curse_filter.kick_count = test_kick_temp;
		test_group.curseFilterOn();
		test_group.messageCounterOn();
		test_group.wordCaptureOff();
		test_group.committeeCommandsOn();
		test_group.start();
		nsf = new GroupMeChat(2002, NSF_BOT_ID, NSF_GROUP_ID, "NSF");
		nsf.curseFilterOff();
		nsf.messageCounterOff();
		nsf.wordCaptureOn();
		nsf.start();
		org_dev = new GroupMeChat(2003, ORG_DEV_BOT_ID, ORG_DEV_GROUP_ID, "Org Dev");
		org_dev.wordCaptureOn();
		org_dev.committeeCommandsOn();
		org_dev.curseFilterOff();
		org_dev.messageCounterOff();
		org_dev.start();
		recruitment = new GroupMeChat(2004, RECRUITMENT_BOT_ID, RECRUITMENT_GROUP_ID, "Recruitment");
		recruitment.wordCaptureOn();
		recruitment.committeeCommandsOn();
		recruitment.curseFilterOff();
		recruitment.messageCounterOff();
		recruitment.start();
		social = new GroupMeChat(2005, SOCIAL_BOT_ID, SOCIAL_GROUP_ID, "Social");
		social.committeeCommandsOn();
		social.wordCaptureOn();
		social.curseFilterOff();
		social.messageCounterOff();
		social.start();
		phil = new GroupMeChat(2006, PHIL_BOT_ID, PHIL_GROUP_ID, "Philanthropy");
		phil.committeeCommandsOn();
		phil.wordCaptureOn();
		phil.curseFilterOff();
		phil.messageCounterOff();
		phil.start();
	}
	
	private static void loadStaticClasses(){
		GroupMe.load(GROUP_TOKEN);
		Todoist.init();
		try {
			GoogleCalendar.load();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
