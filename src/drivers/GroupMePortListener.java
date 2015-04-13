package drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public abstract class GroupMePortListener extends Thread{
	int PORT = 2000;
	public volatile ServerSocket server_socket;
	public String chat_name = "default name: listener";
	
	public void run(){
		try{
			server_socket = new ServerSocket(PORT);
			System.out.println(this.chat_name + " is powering up");
			while(true){
				Socket client = server_socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
				String input;
				while((input = in.readLine()) != null){
					readMessage(input);
				}
			}
		} catch(SocketException e){
			System.out.println(this.chat_name + " is powering down");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public GroupMePortListener(int port, String name){
		this.PORT = port;
		this.chat_name = name;
	}
	
	public void kill() {
		try {
			server_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	abstract public void readMessage(String message);
}
