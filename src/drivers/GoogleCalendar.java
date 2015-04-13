package drivers;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendar {
	static Calendar service;
	// Get a list of events from the calendar
	public static List<Event> getEvents() throws IOException{
		String pageToken = null;
		List<Event> items = new ArrayList<Event>();
		do{
			Events events = service.events().list("primary").setPageToken(pageToken).execute();
			items = events.getItems();
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		return items;
	}
	
	public static void quickAddEvent(String event_info) throws IOException{
		service.events().quickAdd("calendar@kdrnu.com", event_info).execute();
		
	}
	// Must be called before any calendar method is envoked.
	public static void load() throws GeneralSecurityException, IOException{
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		
		String clientId = "584834534448-tc4d543ufsi463v9lptflgu61e9ubt53.apps.googleusercontent.com";
		String clientSecret = "YhtmUN5uwyBunXNOm2bNkNi5";
		String scope = "https://www.googleapis.com/auth/calendar";
//		String redirect = "urn:ietf:wg:oauth:2.0:oob";
		
		FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("calendarstore"));
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientId, clientSecret, Collections.singleton(scope)).setDataStoreFactory(dataStoreFactory).setAccessType("offline").build();
//		String authURL = flow.newAuthorizationUrl().setRedirectUri(redirect).build();
//		
//		System.out.println(authURL);
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//		String code = in.readLine();
//		flow.loadCredential("");
//		GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirect).execute();
//		Credential cred = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).setClientSecrets(clientId, clientSecret).build().setFromTokenResponse(response);
//		Credential cred = flow.createAndStoreCredential(response, "userid");
		Credential cred = flow.loadCredential("userid");
		service = new Calendar.Builder(httpTransport, jsonFactory, cred).setApplicationName("appname").build();
	}
	// Returns a list of events that contain the given string.
	public static List<Event> getEventsContaining(String name) throws IOException{
		List<Event> events_contain = new ArrayList<Event>();
		List<Event> events = getEvents();
		for(Event ev : events){
			if(ev.getSummary() != null && ev.getSummary().contains(name)){
				events_contain.add(ev);
			}
		}
		return events_contain;
	}
}
