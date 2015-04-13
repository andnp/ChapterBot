package groupmechat;

import java.io.File;
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
import java.util.Scanner;

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
	
	public void train() throws IOException, JSONException, InterruptedException{
		Scanner sc = new Scanner(new File("train.txt"));
		String message = "";
		int command = -1;
		float right = 0;
		float wrong = 0;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			message = line.split(" // ")[0];
			if(message.equals("learn get todos") || message.equals("learn get tasks") || message.equals("learn add todo") || message.equals("learn add task") || message.equals("learn complete todo") || message.equals("learn complete task")){
				if(committee.equals("Test_Filter")) message = message + " org dev";
				else message = message + " " + committee;
			}
			//System.out.println(message);
			//System.out.println(line.split(" // ")[1]);
			command = Integer.parseInt(line.split(" // ")[1]);
			probablisticCommand(message, "andy");
			if(last_command == command){ 
//				System.out.println("success");
				learn(last_command, message, true);
				right++;
			}
			else {
				while(last_command != command){
//					System.out.println("fail");
					System.out.println(message);
					learn(last_command, message, false);
					wrong++;
					probablisticCommand(message, "andy");
					if(last_command == command){
//						System.out.println("success");
						learn(last_command, message, true);
						right++;
					}
				}
			}
		}
		System.out.println("right: " + right + " wrong: " + wrong + " percent: " + (right / (right + wrong)));
		sc.close();
	}
	public void train_guess(int command){
		last_command = command;
	}
	
	public void readMessage(String message, String name) throws Exception{
		if(message.equals("train")){
//			for(int i = 0; i <= 3; i++){
//				train();
//			}
		}
		if(message.equals("nevermind")){
			last_message = "";
			last_command = -1;
			is_waiting_for_response = 0;
		}
		if(is_waiting_for_response > 0){
			executeCommand(message, name);
		}
		if(message.equals("iris get todos") || message.equals("iris get tasks") || message.equals("iris add todo") || message.equals("iris add task") || message.equals("iris complete todo") || message.equals("iris complete task")){
			message = message + " " + committee;
		}
		if(message.contains("iris")){
			probablisticCommand(message, name);
		}
		if(message.equals("yes")){
			if(last_command != -1){
				executeCommand(message, name);
				learn(last_command, last_message, true);
				if(is_waiting_for_response == 0) last_command = -1;
			}
		}
		if(message.equals("no")){
			if(last_command != -1){
				System.out.println("hmm");
				learn(last_command, last_message, false);
				if(is_waiting_for_response == 0) last_command = -1;
			}
		}
	}
	
	private void executeCommand(String message, String name) throws JSONException, IOException, InterruptedException{
		String fname = name.split(" ")[0];
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
		if(is_waiting_for_response == 0){
			String f_name = name.split(" ")[0];
			Thread.sleep(500);
			GroupMe.sendMessage("What calendar event would you like to add "+ f_name + "?", bot_id);
			is_waiting_for_response = 1;
			responder = name;
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			stack.add(message);
			Thread.sleep(500);
			GroupMe.sendMessage("What date is the event?", bot_id);
			is_waiting_for_response = 2;
		} else if(is_waiting_for_response == 2 && responder.equals(name)){
			stack.add(message);
//			GoogleCalendar.quickAddEvent(stack.remove(0) + " " + stack.remove(0));
			Thread.sleep(500);
			GroupMe.sendMessage("What time is the event?", bot_id);
			is_waiting_for_response = 3;
		} else if(is_waiting_for_response == 3 && responder.equals(name)){
			stack.add(message);
			// stack pos 0 = event name || pos 1 = date || pos 2 = time
			GoogleCalendar.quickAddEvent(stack.get(0) + " " + stack.get(1) + " at " + stack.get(2));
			Thread.sleep(500);
			GroupMe.sendMessage("Added: " + stack.remove(0) + ", scheduled for: " + stack.remove(0) + " at " + stack.remove(0) + " to google calendar.", bot_id);
			is_waiting_for_response = 0;
			responder = "";
		}
	}
	
	private void listCommands() throws InterruptedException, IOException, JSONException{
		String[] commands = {"Get todos", "Add todo", "Complete todo", "Add calendar event"};
		String to_send = "";
		for(String command : commands){
			to_send += command + "\n";
		}
		Thread.sleep(100);
		GroupMe.sendMessage(to_send, bot_id);
	}
	
	private void completeTodo(String comm, String message, String name) throws InterruptedException, IOException, JSONException{
		if(is_waiting_for_response == 0){
			Thread.sleep(500);
			GroupMe.sendMessage("What todo would you like to complete?", bot_id);
			is_waiting_for_response = 1;
			responder = name;
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			Todoist.completeItem(message, comm);
			Thread.sleep(500);
			GroupMe.sendMessage("Completed todo: " + message, bot_id);
			is_waiting_for_response = 0;
			responder = "";
		} 
	}
	
	private void getTodos(String comm) throws JSONException, IOException, InterruptedException{
		List<String> tasks;
		tasks = Todoist.getUncompletedTasks(comm);
		String to_send = "";
		if(tasks.isEmpty()){
			Thread.sleep(500);
			GroupMe.sendMessage("No todos", bot_id);
			return;
		}
		for(String task : tasks){
			to_send += task + "\n";
		}
		Thread.sleep(100);
		GroupMe.sendMessage(to_send, bot_id);
	}
	
	private void addTodo(String comm, String message, String name) throws IOException, JSONException, InterruptedException{
		if(is_waiting_for_response == 0){
			Thread.sleep(500);
			GroupMe.sendMessage("What todo would you like to add?", bot_id);
			is_waiting_for_response = 1;
			responder = name;
		} else if(is_waiting_for_response == 1 && responder.equals(name)){
			stack.add(message);
			Thread.sleep(500);
			GroupMe.sendMessage("When is it due?", bot_id);
			is_waiting_for_response = 2;
		} else if(is_waiting_for_response == 2 && responder.equals(name)){
			Todoist.addItem(stack.get(0), message, comm);
			Thread.sleep(500);
			GroupMe.sendMessage("Added: " + stack.remove(0) + " to: " + comm + ". Due on: " + message, bot_id);
			is_waiting_for_response = 0;
			responder = "";
		}
	}
	
	public CommitteeCommands(String bot_id, String committee){
		this.bot_id = bot_id;
		this.committee = committee;
	}
	
	public void run(){
		Calendar cal = new GregorianCalendar();
//		System.out.println("here");
		while(true){
			String month = "";
			switch(cal.get(Calendar.MONTH)){
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
			String day = cal.get(Calendar.DAY_OF_MONTH) + "";
			String date = month + " " + day;
			if(committee.equals("Test_Filter")) committee = "Org Dev";
//			System.out.println(date);
			try {
				List<String> todos = Todoist.getItemByDate(date, committee);
				String to_send = "";
				for(String task : todos){
					to_send += task + "\n";
				}
				if(cal.get(Calendar.HOUR_OF_DAY) == 10){
					if(!todos.isEmpty()) GroupMe.sendMessage("These TODO's still need to be completed today!", bot_id);
					Thread.sleep(1000);
					if(!todos.isEmpty()) GroupMe.sendMessage(to_send, bot_id);
					Thread.sleep(1000 * 60 * 60 * 22);
				}
				Thread.sleep(1000 * 60 * 1);
			} catch (IOException | JSONException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void probablisticCommand(String message, String name) throws JSONException, IOException, InterruptedException{
		String[] words = message.split(" ");
		String[] doubles = getDoubles(words);
		String[] triples = getTriples(words);
		if(words.length < 3) {triples = new String[1]; triples[0] = "";}
		
		float[] command_probabilities = new float[KNOWN_COMMANDS.length];
//		float[] doubles_probabilities = new float[KNOWN_COMMANDS.length];
		
		for(int i = 0; i < KNOWN_COMMANDS.length; i++){
			String json_text = new String(Files.readAllBytes(Paths.get("CommandDictionary/"+ i +".json")), StandardCharsets.UTF_8);
//			System.out.println(i);
			JSONObject json = new JSONObject(json_text);
			float word_sum = 0; float doubles_sum = 0; float triples_sum = 0;
			float word_num = 0; float doubles_num = 0; float triples_num = 0;
			JSONArray json_words = json.getJSONArray("words");
			JSONArray json_doubles = json.getJSONArray("doubles");
			JSONArray json_triples = json.getJSONArray("triples");
			ArrayList<JSONObject> word_list = sortList(populateList(json_words), "word");
			ArrayList<JSONObject> doubles_list = populateList(json_doubles);
			ArrayList<JSONObject> triples_list = populateList(json_triples);
			//System.out.println((String)json_words.getJSONObject(0).keys().next());
			for(int k = 0; k < words.length; k++){
				int j = binarySearch(word_list, words[k], "word");
				boolean contains = j >= 0;
				if(contains){
					JSONArray data = json_words.getJSONObject(j).getJSONArray("data");
					word_sum += (float)(data.getDouble(0) / data.getDouble(1));
					word_num++;
				} else {
					word_num++;
				}
			}
			for(int k = 0; k < doubles.length; k++){
				int j = binarySearch(doubles_list, doubles[k], "double");
				boolean contains = j >= 0;
				if(contains){
					JSONArray data = json_doubles.getJSONObject(j).getJSONArray("data");
					doubles_sum += (float)(data.getDouble(0) / data.getDouble(1));
					doubles_num++;
				} else {
					doubles_num++;
				}
			}
			for(int k = 0; k < triples.length; k++){
				int j = binarySearch(triples_list, triples[k], "triple");
				boolean contains = j >= 0;
				if(contains){
					JSONArray data = json_triples.getJSONObject(j).getJSONArray("data");
					triples_sum += (float)(data.getDouble(0) / data.getDouble(1));
					triples_num++;
				} else {
					triples_num++;
				}
			}
			command_probabilities[i] = .1f * (word_sum / word_num) + .35f * (doubles_sum / doubles_num) + .55f * (triples_sum / triples_num);
			System.out.println(KNOWN_COMMANDS[i] +": " + command_probabilities[i]);
//			System.out.println("Doubles P: " + (doubles_sum / doubles_num));
//			System.out.println("Triples P: " + (triples_sum / triples_num));
		}
		
		int loc_max = -1;
		float cur_max = -2;
		for(int i = 0; i < command_probabilities.length; i++){
			if(command_probabilities[i] > cur_max){
				loc_max = i;
				cur_max = command_probabilities[i];
			}
		}
//		train_guess(loc_max);
		if(cur_max < .05) return;
		if(cur_max > .4){
			last_command = loc_max;
			executeCommand(message, name);
			learn(loc_max, message, true);
			if(is_waiting_for_response == 0) last_command = -1;
			last_message = message;
			return;
		}
		guess(loc_max);
		last_message = message;
	}
	
	private void learn(int command, String message, boolean correct) throws IOException, JSONException{
		for(int k = 0; k < KNOWN_COMMANDS.length; k++){
			String json_text = new String(Files.readAllBytes(Paths.get("CommandDictionary/"+ k +".json")), StandardCharsets.UTF_8);
			JSONObject json = new JSONObject(json_text);
			JSONArray json_words = json.getJSONArray("words");
			JSONArray json_doubles = json.getJSONArray("doubles");
			JSONArray json_triples = json.getJSONArray("triples");
			ArrayList<JSONObject> word_list = populateList(json_words);
			ArrayList<JSONObject> doubles_list = populateList(json_doubles);
			ArrayList<JSONObject> triples_list = populateList(json_triples);
			
			String[] words = message.split(" ");
			String[] doubles = getDoubles(words);
			String[] triples = getTriples(words);
			for(int j = 0; j < words.length; j++){
				int i = binarySearch(word_list, words[j], "word");
				//System.out.println("file: " + k +".json : Word " + words[j] + ": " + i);
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_word = json_words.getJSONObject(i);
					JSONArray word_info = json_word.getJSONArray("data");
					int count = word_info.getInt(0);
					int of = word_info.getInt(1);
					
					if(correct && k == command){
						word_info.put(0, ++count);
					} else if(!correct && k == command){
						word_info.put(0, --count);
					}
					word_info.put(1, ++of);
				}
				if(!contained && !words[j].equals("learn")){
					JSONObject json_word = new JSONObject();
					JSONArray word_info = new JSONArray();
					if(correct && k == command){
						word_info.put(1);
					} else {
						word_info.put(0);
					}
					if(!correct && k == command){
						word_info.put(-1);
					}
					word_info.put(1);
					json_word.put("word", words[j]);
					json_word.put("data", word_info);
					word_list.add(json_word);
				}
				contained = false;
			}
			for(int j = 0; j < doubles.length; j++){
				int i = binarySearch(doubles_list, doubles[j], "double");
//				System.out.println("file: " + k +".json : Double " + doubles[j] + ": " + i);
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_double = json_doubles.getJSONObject(i);
					JSONArray double_info = json_double.getJSONArray("data");
					int count = double_info.getInt(0);
					int of = double_info.getInt(1);
					
					if(correct && k == command){
						double_info.put(0, ++count);
					} else if(!correct && k == command){
						double_info.put(0, --count);
					}
					double_info.put(1, ++of);
				}
				if(!contained){
					JSONObject json_double = new JSONObject();
					JSONArray double_info = new JSONArray();
					if(correct && k == command){
						double_info.put(1);
					} else {
						double_info.put(0);
					}
					if(!correct && k == command){
						double_info.put(-1);
					}
					double_info.put(1);
					json_double.put("double", doubles[j]);
					json_double.put("data", double_info);
					doubles_list.add(json_double);
				}
				contained = false;
			}
			for(int j = 0; j < triples.length; j++){
				int i = binarySearch(triples_list, triples[j], "triple");
//				System.out.println("file: " + k +".json : Double " + doubles[j] + ": " + i);
				boolean contained = i >= 0;
				if(contained){
					JSONObject json_triple = json_triples.getJSONObject(i);
					JSONArray triple_info = json_triple.getJSONArray("data");
					int count = triple_info.getInt(0);
					int of = triple_info.getInt(1);
					
					if(correct && k == command){
						triple_info.put(0, ++count);
					} else if(!correct && k == command){
						triple_info.put(0, --count);
					}
					triple_info.put(1, ++of);
				}
				if(!contained){
					JSONObject json_triple = new JSONObject();
					JSONArray triple_info = new JSONArray();
					if(correct && k == command){
						triple_info.put(1);
					} else {
						triple_info.put(0);
					}
					if(!correct && k == command){
						triple_info.put(-1);
					}
					triple_info.put(1);
					json_triple.put("triple", triples[j]);
					json_triple.put("data", triple_info);
					triples_list.add(json_triple);
				}
				contained = false;
			}
			json_words = listToArray(sortList(word_list, "word"));
			json_doubles = listToArray(sortList(doubles_list, "double"));
			json_triples = listToArray(sortList(triples_list, "triple"));
			json.put("words", json_words);
			json.put("doubles", json_doubles);
			json.put("triples", json_triples);
			PrintWriter file_writer = new PrintWriter("CommandDictionary/" + k + ".json");
			file_writer.println(json.toString(1));
			file_writer.close();
		}
	}
	
	public String[] getDoubles(String[] words){
		String [] doubles = new String[words.length - 1];
		for(int i = 0; i < words.length - 1; i++){
			doubles[i] = words[i] + " " + words[i + 1];
		}
		return doubles;
	}
	
	public String[] getTriples(String[] words){
		String[] triples = new String[words.length - 2];
		for(int i = 0; i < words.length - 2; i++){
			triples[i] = words[i] + " " + words[i + 1] + " " + words[i + 2];
		}
		return triples;
	}
	
	private void guess(int command) throws IOException, JSONException, InterruptedException{
		if(command == -1){System.out.println("error"); return;}
		Thread.sleep(500);
		GroupMe.sendMessage("Is this the command you want (yes/no)? " + KNOWN_COMMANDS[command], bot_id);
		last_command = command;
	}

	
	public ArrayList<JSONObject> populateList(JSONArray json_words) throws JSONException{
		ArrayList<JSONObject> word_list = new ArrayList<JSONObject>();
		for(int i = 0; i < json_words.length(); i++){
			word_list.add(json_words.getJSONObject(i));
		}
		return word_list;
	}
	
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
//	private ArrayList<JSONObject> sortJSONArray(JSONArray json_words) throws JSONException{
//		ArrayList<JSONObject> word_list = populateList(json_words);
//		Collections.sort(word_list, new Comparator<JSONObject>(){
//			private static final String KEY = "word";
//			
//			public int compare(JSONObject a, JSONObject b){
//				String valA = "";
//				String valB = "";
//				
//				try{
//					valA = a.getString(KEY);
//					valB = b.getString(KEY);
//				} catch(JSONException e){
//					
//				}
//				return valA.compareTo(valB);
//			}
//		});
//		return word_list;
//	}
	public JSONArray listToArray(ArrayList<JSONObject> word_list){
		JSONArray array = new JSONArray();
		for(JSONObject json : word_list){
			array.put(json);
		}
		return array;
	}
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
