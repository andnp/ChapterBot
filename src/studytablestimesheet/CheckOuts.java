package studytablestimesheet;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import drivers.GroupMe;

public class CheckOuts extends Thread{
	SpreadsheetService service = new SpreadsheetService("MySpreadsheet");
	SpreadsheetEntry spreadsheet = null;
	String BOT_ID = "0a45c83bbc595e96fbd9ab323d";

	public void run(){
		try{
			service.setProtocolVersion(SpreadsheetService.Versions.V3);
			service.setUserCredentials("andnpatt@umail.iu.edu", "Andnp972965");
	
			URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			String sheet_string = "Study Tables Timesheet (Responses)";
			for(SpreadsheetEntry sheet : spreadsheets) {
				if(sheet.getTitle().getPlainText().equals(sheet_string)){
					spreadsheet = sheet;
				}
			}
			while(true){
				List<String> messages = checkCheckOuts();
				for(String message : messages){
					GroupMe.sendMessage(message, BOT_ID);
				}
				Thread.sleep((long)(1000 * 60 * 60 * 1)); // sleep for an hour before checking again
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public List<String> checkCheckOuts() throws IOException, ServiceException, JSONException{
		List<String> names = new ArrayList<String>();

		WorksheetFeed worksheetFeed = this.service.getFeed(this.spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
		List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
		WorksheetEntry table = null;
		for(WorksheetEntry worksheet : worksheets){
			if(worksheet.getTitle().getPlainText().equals("Form Responses 1")){
				table = worksheet;
				break;
			}
		}
		URL listFeedUrl = table.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		for(ListEntry row : listFeed.getEntries()){
			//System.out.println(row.getCustomElements().getTags());
			int loc = inList(row.getCustomElements().getValue("name"), names);
			if(loc != -1){
				names.remove(loc);
			} else {
				names.add(row.getCustomElements().getValue("name"));
			}
		}
		String message = null;
		List<String> return_list = new ArrayList<String>();
		for(String name : names){
			message = exceedsTime(name, 2.5);
			if(message != null){
				return_list.add(message);
			}
		}
		return return_list;
	}
	public String exceedsTime(String name, double hours) throws IOException, ServiceException{
		WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
		List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
		WorksheetEntry table = null;
		for(WorksheetEntry worksheet : worksheets){
			if(worksheet.getTitle().getPlainText().equals("Form Responses 1")){
				table = worksheet;
				break;
			}
		}
		
		URL listFeedUrl = table.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		
		long ret = 0;
		long time = System.currentTimeMillis() / 1000;
		
		for(ListEntry row : listFeed.getEntries()){
			if(row.getCustomElements().getValue("name").equals(name)){
				ret = Long.parseLong(row.getCustomElements().getValue("epochtime"));
				if(time - ret > (60 * 60 * hours)){
					return name + " needs to check out";
				}
			}
		}
		return null;
	}
	
	public int inList(String string, List<String> list){
		int i = 0;
		for(String str : list){
			if(str.equals(string)) return i;
			i++;
		}
		return -1;
	}
}
