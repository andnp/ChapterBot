package program;

import groupmefilter.CurseFilter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.json.JSONException;

import studytablestimesheet.CheckOuts;

import drivers.GroupMe;
import drivers.GroupMeIds;

public class CenterHub {
	static String GROUP_TOKEN = "dde55e80aa3301322150761316d99941";
	static String TEST_BOT_ID = "0a45c83bbc595e96fbd9ab323d";
	static String TEST_GROUP_ID = "12838361";
	static String DISCUSSION_BOT_ID = "17577deb3363947a4326cff0cf";
	static String DISCUSSION_GROUP_ID = "11283452";
	public static void main(String args[]) throws GeneralSecurityException, IOException, JSONException{
		GroupMe.load(GROUP_TOKEN);
		CheckOuts timesheetchecker = new CheckOuts();
		timesheetchecker.start();
		CurseFilter test_group = new CurseFilter(2000, TEST_BOT_ID, TEST_GROUP_ID);
		CurseFilter discussion_filter = new CurseFilter(2001, DISCUSSION_BOT_ID, DISCUSSION_GROUP_ID);
		discussion_filter.start();
		test_group.start();
//		GoogleCalendar.load();
//		List<Event> events = GoogleCalendar.getEventsContaining("Study Tables");
//		for(Event ev : events){
//			System.out.println(ev.getStart().getDateTime().getValue());
//			
//		}
	}
}
