package groupmechat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExperimentalWordCapture{
	public void appendWordsToFile(String user_id, String message){
		File f = new File("wordtracking/" + user_id +".txt");
		if(!f.isFile()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("wordtracking/" + user_id +".txt", true)));
		    out.println(message);
		    out.close();
		} catch (IOException e) {
		}
	}
}
