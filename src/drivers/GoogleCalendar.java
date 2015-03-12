package drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendar {
	static Calendar service;
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
	
	public static void load() throws GeneralSecurityException, IOException{
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		
		String clientId = "584834534448-tc4d543ufsi463v9lptflgu61e9ubt53.apps.googleusercontent.com";
		String clientSecret = "YhtmUN5uwyBunXNOm2bNkNi5";
		String scope = "https://www.googleapis.com/auth/calendar";
		String redirect = "urn:ietf:wg:oauth:2.0:oob";
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow(httpTransport, jsonFactory, clientId, clientSecret, Collections.singleton(scope));
		String authURL = flow.newAuthorizationUrl().setRedirectUri(redirect).build();
		
		System.out.println(authURL);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String code = in.readLine();
		
		GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirect).execute();
		Credential cred = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).setClientSecrets(clientId, clientSecret).build().setFromTokenResponse(response);
		
		service = new Calendar.Builder(httpTransport, jsonFactory, cred).setApplicationName("appname").build();
	}
	
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
