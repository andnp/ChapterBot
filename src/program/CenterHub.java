package program;

import groupmefilter.CurseFilter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.json.JSONException;

import com.google.api.services.calendar.model.Event;

import studytablestimesheet.CheckOuts;

import drivers.GoogleCalendar;
import drivers.GroupMe;
import drivers.GroupMePortListener;

public class CenterHub {
	static String GROUP_TOKEN = "dde55e80aa3301322150761316d99941";
	static String BOT_ID = "0a45c83bbc595e96fbd9ab323d";
	public static void main(String args[]) throws GeneralSecurityException, IOException, JSONException{
		GroupMe.load(GROUP_TOKEN);
		GroupMe.addMember("12838361", "23314953", "Dad");
		CheckOuts timesheetchecker = new CheckOuts();
		//timesheetchecker.start();
		CurseFilter read_group = new CurseFilter(2000, BOT_ID);
		read_group.start();
//		GoogleCalendar.load();
//		List<Event> events = GoogleCalendar.getEventsContaining("Study Tables");
//		for(Event ev : events){
//			System.out.println(ev.getStart().getDateTime().getValue());
//			
//		}
	}
}
