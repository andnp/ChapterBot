package drivers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class GroupMePortListener extends Thread{
	int PORT = 2000;
	public void run(){
		try{
			ServerSocket server_socket = new ServerSocket(PORT);
			while(true){
				Socket client = server_socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
				String input;
				while((input = in.readLine()) != null){
					System.out.println(input);
					readMessage(input);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public GroupMePortListener(int port){
		this.PORT = port;
	}
	
	abstract public void readMessage(String message);
}
