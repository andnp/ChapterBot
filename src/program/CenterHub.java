package program;

import java.io.IOException;
import java.security.GeneralSecurityException;
import studytablestimesheet.CheckOuts;

import drivers.GroupMe;

public class CenterHub {
	static String GROUP_TOKEN = "dde55e80aa3301322150761316d99941";
	public static void main(String args[]) throws GeneralSecurityException, IOException{
		GroupMe.load(GROUP_TOKEN);
		CheckOuts timesheetchecker = new CheckOuts();
		timesheetchecker.start();
//		GoogleCalendar.load();
//		List<Event> events = GoogleCalendar.getEventsContaining("Study Tables");
//		for(Event ev : events){
//			System.out.println(ev.getStart().getDateTime().getValue());
//			
//		}
	}
}
