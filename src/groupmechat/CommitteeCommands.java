package groupmechat;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import drivers.GoogleCalendar;
import drivers.GroupMe;
import drivers.Todoist;

public class CommitteeCommands extends Thread {
	private String bot_id;
	private String committee;
	public final String[] KNOWN_COMMANDS = {"get todos", "say hi", "add todo", "complete todo", "get todos recruitment","get todos social","get todos philanthropy","get todos education","get todos org dev","add todo recruitment","add todo social","add todo philanthropy","add todo education","add todo org dev","complete todo recruitment","complete todo social","complete todo philanthropy","complete todo education","complete todo org dev","What can you do?","add calendar event"};
	private int last_command = -1;
	private String last_message = "";
	private int is_waiting_for_response = 0;
	ArrayList<String> stack = new ArrayList<String>();
	private String responder = "";

	
	public CommitteeCommands(String bot_id, String committee){ // constructor
		this.bot_id = bot_id;
		this.committee = committee;
	}
	
	// Called from GroupMeChat object.
	public void readMessage(String message, String name) throws Exception{
		if(message.equals("iris get todos") || message.equals("iris get tasks") || message.equals("iris add todo") || message.equals("iris add task") || message.equals("iris complete todo") || message.equals("iris complete task")){
			message = message + " " + committee; // if a generic command is given, specify the committee name for iris.
		} 
		if(message.equals("nevermind")){
			last_message = ""; // forget last message
			last_command = -1; // reset last command
			is_waiting_for_response = 0; // forget that am waiting for a response
		} else if(is_waiting_for_response > 0){ // if waiting for some sort of response, then jump straight to that command.
			executeCommand(message, name);
		} else if(message.contains("iris")){
			probablisticCommand(message, name); // use machine learning algorithm to probablistically determine desired command
		} else if(message.equals("yes")){
			if(last_command != -1){ // if looking for a response to a command confirmation
				executeCommand(message, name); // execute command
				learn(last_command, last_message, true); // learn from the positive confirmation
				if(is_waiting_for_response == 0) last_command = -1; // reset the command
			}
		} else if(message.equals("no")){
			if(last_command != -1){ // if looking for a response to a command confirmation
				System.out.println("hmm");
				learn(last_command, last_message, false); // learn from the negative confirmation
				if(is_waiting_for_response == 0) last_command = -1; // reset the command
			}
		}
	}
	
	private void executeCommand(String message, String name) throws JSONException, IOException, InterruptedException{
		String fname = name.split(" ")[0];
		// takes the command number from the last recognized command, and executes the appropriate response
		switch(last_command){
		case 0:
			getTodos(committee);
			break;
		case 1:
			GroupMe.sendMessage("hi " + fname, bot_id);
			break;
		case 2:
			addTodo(committee, message, name);
			break;
		case 3:
			completeTodo(committee, message, name);
			break;
		case 4:
			getTodos("Recruitment");
			break;
		case 5:
			getTodos("Social");
			break;
		case 6:
			getTodos("Philanthropy");
			break;
		case 7:
			getTodos("Education");
			break;
		case 8:
			getTodos("Org Dev");
			break;
		case 9:
			addTodo("Recruitment", message, name);
			break;
		case 10:
			addTodo("Social", message, name);
			break;
		case 11:
			addTodo("Philanthropy",message, name);
			break;
		case 12:
			addTodo("Education",message, name);
			break;
		case 13:
			addTodo("Org Dev",message, name);
			break;
		case 14:
			completeTodo("Recruitment",message, name);
			break;
		case 15:
			completeTodo("Social", message, name);
			break;
		case 16:
			completeTodo("Philanthropy", message, name);
			break;
		case 17:
			completeTodo("Education", message, name);
			break;
		case 18:
			completeTodo("Org Dev", message, name);
			break;
		case 19:
			listCommands();
			break;
		case 20:
			addCalendarEvent(message, name);
			break;
		case -1:
			System.out.println("No command had max probability");
			break;
		}
	}
	
	private void addCalendarEvent(String message, String name) throws InterruptedException, IOException, JSONException{
		if(is_waiting_for_response == 0){ // first state
			String f_name = name.split(" ")[0]; // get first name
			Thread.sleep(500);
			GroupMe.sendMessage("What calendar event would you like to add "+ f_name + "?", bot_id); // prompt for event info
			is_waiting_for_response = 1; // set to second state
			responder = name; // remember who issued the command so no-one can interrupt
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			stack.add(message); // push calendar event information to the 'stack'
			Thread.sleep(500);
			GroupMe.sendMessage("What date is the event?", bot_id); // prompt for event date
			is_waiting_for_response = 2; // set to third state
		} else if(is_waiting_for_response == 2 && responder.equals(name)){
			stack.add(message); // push calendar event date to the 'stack'
			Thread.sleep(500);
			GroupMe.sendMessage("What time is the event?", bot_id); // prompt for event time
			is_waiting_for_response = 3; // set to fouth state
		} else if(is_waiting_for_response == 3 && responder.equals(name)){
			stack.add(message); // push calendar event time to the 'stack'
			// stack pos 0 = event name || pos 1 = date || pos 2 = time
			GoogleCalendar.quickAddEvent(stack.get(0) + " " + stack.get(1) + " at " + stack.get(2)); // add the event with appropriate data
			Thread.sleep(500);
			GroupMe.sendMessage("Added: " + stack.remove(0) + ", scheduled for: " + stack.remove(0) + " at " + stack.remove(0) + " to google calendar.", bot_id);
			is_waiting_for_response = 0; // reset state machine
			responder = ""; // forget who issued the command
		}
	}
	
	private void listCommands() throws InterruptedException, IOException, JSONException{
		String[] commands = {"Get todos", "Add todo", "Complete todo", "Add calendar event"}; // general list of commands
		String to_send = ""; // string of information to send
		for(String command : commands){
			to_send += command + "\n";
		}
		Thread.sleep(500);
		GroupMe.sendMessage(to_send, bot_id); // send compiled list of events
	}
	
	private void completeTodo(String comm, String message, String name) throws InterruptedException, IOException, JSONException{
		if(is_waiting_for_response == 0){ // if in first state
			Thread.sleep(500);
			GroupMe.sendMessage("What todo would you like to complete?", bot_id); // prompt for todo name to complete
			is_waiting_for_response = 1; // set to second state
			responder = name; // remember who issued the command
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			Todoist.completeItem(message, comm); // complete an item by name
			Thread.sleep(500);
			GroupMe.sendMessage("Completed todo: " + message, bot_id); // report completion in the groupme
			is_waiting_for_response = 0; // reset state machine
			responder = ""; // forget who issued the command
		} 
	}
	
	private void getTodos(String comm) throws JSONException, IOException, InterruptedException{
		List<String> tasks = Todoist.getUncompletedTasks(comm); // get the list of uncompleted tasks based on committee
		String to_send = "";
		if(tasks.isEmpty()){ // if there are no todos, then report that
			Thread.sleep(500);
			GroupMe.sendMessage("No todos", bot_id);
			return;
		}
		for(String task : tasks){
			to_send += task + "\n";
		}
		Thread.sleep(100);
		GroupMe.sendMessage(to_send, bot_id); // report todos to finish
	}
	
	private void addTodo(String comm, String message, String name) throws IOException, JSONException, InterruptedException{
		if(is_waiting_for_response == 0){ // if in the first state
			Thread.sleep(500);
			GroupMe.sendMessage("What todo would you like to add?", bot_id); // prompt for todo information
			is_waiting_for_response = 1; // set to first state
			responder = name; // remember name of person who issued command
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			stack.add(message); // push the todo information to the 'stack'
			Thread.sleep(500);
			GroupMe.sendMessage("When is it due?", bot_id); // prompt for due date
			is_waiting_for_response = 2; // set to third state
		} else if(is_waiting_for_response == 2 && responder.equals(name)){
			Todoist.addItem(stack.get(0), message, comm); // add todo
			Thread.sleep(500);
			GroupMe.sendMessage("Added: " + stack.remove(0) + " to: " + comm + ". Due on: " + message, bot_id);
			is_waiting_for_response = 0; // reset state machine
			responder = ""; // forget who issued the command
		}
	}
	
	// This opens an infinite loop on a new thread that reminds the committee to do todo's due today
	public void run(){
		Calendar cal = new GregorianCalendar();
		while(true){
			String month = "";
			switch(cal.get(Calendar.MONTH)){ // get the String representation of a month as defined by Todoist.com
			case 0:
				month = "Jan";
				break;
			case 1:
				month = "Feb";
				break;
			case 2:
				month = "Mar";
				break;
			case 3:
				month = "Apr";
				break;
			case 4:
				month = "May";
				break;
			case 5:
				month = "Jun";
				break;
			case 6:
				month = "Jul";
				break;
			case 7:
				month = "Aug";
				break;
			case 8:
				month = "Sep";
				break;
			case 9:
				month = "Oct";
				break;
			case 10:
				month = "Nov";
				break;
			case 11:
				month = "Dec";
				break;
			}
			String day = cal.get(Calendar.DAY_OF_MONTH) + ""; // day of month as string
			String date = month + " " + day; // get combined date IE. (apr 12)
			if(committee.equals("Test_Filter")) committee = "Org Dev"; // This gives the test group a real committee name.
			try {
				List<String> todos = Todoist.getItemByDate(date, committee); // get the list of items due on a specific date
				String to_send = "";
				for(String task : todos){
					to_send += task + "\n";
				}
				if(cal.get(Calendar.HOUR_OF_DAY) == 10){ // if it is currently 10 o'clock
					if(!todos.isEmpty()) GroupMe.sendMessage("These TODO's still need to be completed today!", bot_id);
					Thread.sleep(1000);
					if(!todos.isEmpty()) GroupMe.sendMessage(to_send, bot_id);
					Thread.sleep(1000 * 60 * 60 * 2); // sleep for 2 hours.
				}
				if(cal.get(Calendar.HOUR_OF_DAY) == 9) { // if it is 9
					Thread.sleep(1000 * 60 * 1); // sleep for a minute
				} else {
					Thread.sleep(1000 * 60 * 60 * 1); // sleep for an hour
				}
			} catch (IOException | JSONException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void probablisticCommand(String message, String name) throws JSONException, IOException, InterruptedException{
		System.out.println(message);
		
		String[] words = message.split(" "); // get the words from the message
		String[] doubles = getDoubles(words);// get pairs of words from the message
		String[] triples = getTriples(words);// get triples of words from the message
		if(words.length < 3) {triples = new String[1]; triples[0] = "";} // if there are not enough words for a triple, make a fake
		if(words.length < 2) {doubles = new String[1]; doubles[0] = "";} // if there are not enough words for a double, make a fake
		
		float[] command_probabilities = new float[KNOWN_COMMANDS.length]; // an array of probabilities
		
		for(int command_index = 0; command_index < KNOWN_COMMANDS.length; command_index++){ // for every known command
			String json_text = new String(Files.readAllBytes(Paths.get("CommandDictionary/"+ command_index +".json")), StandardCharsets.UTF_8); // get data from the command file
			JSONObject json = new JSONObject(json_text); // parson data into a json object
			float word_sum = 0; float doubles_sum = 0; float triples_sum = 0; // initialize variables
			float word_num = 0; float doubles_num = 0; float triples_num = 0;
			
			JSONArray json_words = json.getJSONArray("words"); // get the words from the data file
			JSONArray json_doubles = json.getJSONArray("doubles"); // get the doubles from the data file
			JSONArray json_triples = json.getJSONArray("triples"); // get the triples from the data file
			
			ArrayList<JSONObject> word_list = sortList(populateList(json_words), "word"); // sorted list of words
			ArrayList<JSONObject> doubles_list = populateList(json_doubles); // sorted list of doubles
			ArrayList<JSONObject> triples_list = populateList(json_triples); // sorted list of triples
			
			for(int word_index = 0; word_index < words.length; word_index++){ // iterate over words
				int j = binarySearch(word_list, words[word_index], "word"); // find index of word (if it exists)
				boolean contains = j >= 0; // if j > 0, then word exists
				if(contains){
					JSONArray data = json_words.getJSONObject(j).getJSONArray("data"); // get the jth word
					word_sum += (float)(data.getDouble(0) / data.getDouble(1)); // sum of (number of times correct / total number of uses)
					word_num++; // increase number words
				} else {
					word_num++; // increase number words
				}
			}
			for(int k = 0; k < doubles.length; k++){ // iterate over doubles
				int j = binarySearch(doubles_list, doubles[k], "double"); // find index of double (if it exists)
				boolean contains = j >= 0;
				if(contains){
					JSONArray data = json_doubles.getJSONObject(j).getJSONArray("data"); // get the jth double
					doubles_sum += (float)(data.getDouble(0) / data.getDouble(1)); // sum of (number of times correct / total number of uses)
					doubles_num++; // increase number doubles
				} else {
					doubles_num++; // increase number of doubles
				}
			} 
			for(int k = 0; k < triples.length; k++){ // iterate over triples
				int j = binarySearch(triples_list, triples[k], "triple"); // find index of triple (if it exists)
				boolean contains = j >= 0;
				if(contains){
					JSONArray data = json_triples.getJSONObject(j).getJSONArray("data"); // get the jth triple
					triples_sum += (float)(data.getDouble(0) / data.getDouble(1)); // sum of (number of times correct / total number of uses)
					triples_num++; // increase number triples
				} else {
					triples_num++; // increase number triples
				}
			}
			command_probabilities[command_index] = .1f * (word_sum / word_num) + .35f * (doubles_sum / doubles_num) + .55f * (triples_sum / triples_num); // compute weighted average of (word average, double average, triple average)
			System.out.println(KNOWN_COMMANDS[command_index] +": " + command_probabilities[command_index]);
		}
		
		int loc_max = -1;
		float cur_max = -2;
		for(int i = 0; i < command_probabilities.length; i++){ // iterate over command probabilities
			if(command_probabilities[i] > cur_max){ // if current command probability is greater than the current max
				loc_max = i; // save location of max
				cur_max = command_probabilities[i]; // save current max probability
			}
		}
		if(cur_max < .05) return; // if best guess is less than 5%, give up
		if(cur_max > .4){ // if best guess is greater than 40% automatically execute
			last_command = loc_max;
			executeCommand(message, name);
			learn(loc_max, message, true);
			if(is_waiting_for_response == 0) last_command = -1;
			last_message = message;
			return;
		}
		guess(loc_max); // if best guess is greater than 5% but less than 40%, ask if best guess is correct
		last_message = message; // remember the last message
	}
	
	private void learn(int command, String message, boolean correct) throws IOException, JSONException{
		for(int command_index = 0; command_index < KNOWN_COMMANDS.length; command_index++){
			String json_text = new String(Files.readAllBytes(Paths.get("CommandDictionary/"+ command_index +".json")), StandardCharsets.UTF_8); // read data file
			JSONObject json = new JSONObject(json_text); // convert data into json object
			
			JSONArray json_words = json.getJSONArray("words"); // get the words from the json object
			JSONArray json_doubles = json.getJSONArray("doubles"); // get the doubles from the json object
			JSONArray json_triples = json.getJSONArray("triples"); // get the triples from the json object
			
			ArrayList<JSONObject> word_list = populateList(json_words); // get a list of words
			ArrayList<JSONObject> doubles_list = populateList(json_doubles); // get a list of doubles
			ArrayList<JSONObject> triples_list = populateList(json_triples); // get a list of triples
			
			String[] words = message.split(" "); // get array of message words
			String[] doubles = getDoubles(words); // get array of message doubles
			String[] triples = getTriples(words); // get array of message triples
			
			for(int word_index = 0; word_index < words.length; word_index++){ // iterate over words
				int i = binarySearch(word_list, words[word_index], "word");
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_word = json_words.getJSONObject(i);
					JSONArray word_info = json_word.getJSONArray("data");
					int count = word_info.getInt(0);
					int of = word_info.getInt(1);
					
					if(correct && command_index == command){
						word_info.put(0, ++count);
					} else if(!correct && command_index == command){
						word_info.put(0, --count);
					}
					word_info.put(1, ++of);
				}
				if(!contained && !words[word_index].equals("learn")){
					JSONObject json_word = new JSONObject();
					JSONArray word_info = new JSONArray();
					if(correct && command_index == command){
						word_info.put(1);
					} else {
						word_info.put(0);
					}
					if(!correct && command_index == command){
						word_info.put(-1);
					}
					word_info.put(1);
					json_word.put("word", words[word_index]);
					json_word.put("data", word_info);
					word_list.add(json_word);
				}
				contained = false;
			}
			for(int double_index = 0; double_index < doubles.length; double_index++){
				int i = binarySearch(doubles_list, doubles[double_index], "double");
//				System.out.println("file: " + k +".json : Double " + doubles[j] + ": " + i);
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_double = json_doubles.getJSONObject(i);
					JSONArray double_info = json_double.getJSONArray("data");
					int count = double_info.getInt(0);
					int of = double_info.getInt(1);
					
					if(correct && command_index == command){
						double_info.put(0, ++count);
					} else if(!correct && command_index == command){
						double_info.put(0, --count);
					}
					double_info.put(1, ++of);
				}
				if(!contained){
					JSONObject json_double = new JSONObject();
					JSONArray double_info = new JSONArray();
					if(correct && command_index == command){
						double_info.put(1);
					} else {
						double_info.put(0);
					}
					if(!correct && command_index == command){
						double_info.put(-1);
					}
					double_info.put(1);
					json_double.put("double", doubles[double_index]);
					json_double.put("data", double_info);
					doubles_list.add(json_double);
				}
				contained = false;
			}
			for(int triple_index = 0; triple_index < triples.length; triple_index++){
				int i = binarySearch(triples_list, triples[triple_index], "triple");
//				System.out.println("file: " + k +".json : Double " + doubles[j] + ": " + i);
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_triple = json_triples.getJSONObject(i);
					JSONArray triple_info = json_triple.getJSONArray("data");
					int count = triple_info.getInt(0);
					int of = triple_info.getInt(1);
					
					if(correct && command_index == command){
						triple_info.put(0, ++count);
					} else if(!correct && command_index == command){
						triple_info.put(0, --count);
					}
					triple_info.put(1, ++of);
				}
				if(!contained){
					JSONObject json_triple = new JSONObject();
					JSONArray triple_info = new JSONArray();
					if(correct && command_index == command){
						triple_info.put(1);
					} else {
						triple_info.put(0);
					}
					if(!correct && command_index == command){
						triple_info.put(-1);
					}
					triple_info.put(1);
					json_triple.put("triple", triples[triple_index]);
					json_triple.put("data", triple_info);
					triples_list.add(json_triple);
				}
				contained = false;
			}
			
			json_words = listToArray(sortList(word_list, "word")); // sort data then put back to JSON Array
			json_doubles = listToArray(sortList(doubles_list, "double"));
			json_triples = listToArray(sortList(triples_list, "triple"));
			
			json.put("words", json_words); // put data back into main json object
			json.put("doubles", json_doubles);
			json.put("triples", json_triples);
			
			PrintWriter file_writer = new PrintWriter("CommandDictionary/" + command_index + ".json"); // write the json object back to file
			file_writer.println(json.toString(1));
			file_writer.close();
		}
	}
	
	// returns an array containing the doubles
	public String[] getDoubles(String[] words){
		String [] doubles = new String[words.length - 1];
		for(int i = 0; i < words.length - 1; i++){
			doubles[i] = words[i] + " " + words[i + 1];
		}
		return doubles;
	}
	
	// returns an array containing the triples
	public String[] getTriples(String[] words){
		String[] triples = new String[words.length - 2];
		for(int i = 0; i < words.length - 2; i++){
			triples[i] = words[i] + " " + words[i + 1] + " " + words[i + 2];
		}
		return triples;
	}
	
	// sends a groupme message asking if the command is correct
	private void guess(int command) throws IOException, JSONException, InterruptedException{
		if(command == -1){System.out.println("error"); return;}
		Thread.sleep(500);
		GroupMe.sendMessage("Is this the command you want (yes/no)? " + KNOWN_COMMANDS[command], bot_id);
		last_command = command;
	}

	// JSONArray to ArrayList
	public ArrayList<JSONObject> populateList(JSONArray json_words) throws JSONException{
		ArrayList<JSONObject> word_list = new ArrayList<JSONObject>();
		for(int i = 0; i < json_words.length(); i++){
			word_list.add(json_words.getJSONObject(i));
		}
		return word_list;
	}
	
	// Sort an ArrayList without side effects
	public ArrayList<JSONObject> sortList(ArrayList<JSONObject> word_list, final String key){
		Collections.sort(word_list, new Comparator<JSONObject>(){
			
			public int compare(JSONObject a, JSONObject b){
				String valA = "";
				String valB = "";
				
				try{
					valA = a.getString(key);
					valB = b.getString(key);
				} catch(JSONException e){
					
				}
				return valA.compareTo(valB);
			}
		});
		return word_list;
	}

	// Take an ArrayList and turn it into a JSONArray
	public JSONArray listToArray(ArrayList<JSONObject> word_list){
		JSONArray array = new JSONArray();
		for(JSONObject json : word_list){
			array.put(json);
		}
		return array;
	}
	
	// Do a binarySearch for in a list based on alphabetical order
	public int binarySearch(ArrayList<JSONObject> word_list, String word, final String key) throws JSONException{
		JSONObject temp = new JSONObject();
		temp.put(key, word);
		int i = Collections.binarySearch(word_list, temp,  new Comparator<JSONObject>(){
			
			public int compare(JSONObject a, JSONObject b){
				String valA = "";
				String valB = "";
				
				try{
					valA = a.getString(key);
					valB = b.getString(key);
				} catch(JSONException e){
					
				}
				return valA.compareTo(valB);
			}
		});
		return i;
	}
}
