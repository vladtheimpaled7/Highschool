import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import java.util.*;


public class Communication extends Server {
	private String password;
	private boolean isDiscoverable;
	private ArrayList<Message> messageList;
	private boolean askBeforeConnect;
	private boolean isPassworded;
	private ServerSocket serv;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public Communication(String pass,String user,int por,boolean ask, boolean discoverable) throws IOException {
		// TODO Auto-generated constructor stub
		super(user);
		port = por;
		askBeforeConnect = ask;
		isDiscoverable = discoverable;
		
		if(pass == null){
			isPassworded = false;
		}
		else{
			isPassworded = true;
		}
		//start server
		serv = new ServerSocket(port);
		serv.setSoTimeout(100);
		System.out.println("Started Server");
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				for(;;){
				try {
					acceptUser(true,pass);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					int numMessages = readMessage();
					sendMessage(numMessages);
				} catch(SocketTimeoutException e){
					
				}catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//close and remove socket
					
					//e.printStackTrace();
				}
				
				}
				
			}
			
		});
		t.start();
		
	}
	public ArrayList<Message> getMessages(int start){
		ArrayList<Message> temp = new ArrayList<Message>();
		for(int i = start;i<safeMessages.size();i++){
			temp.add(safeMessages.get(i));
		}
		return temp;
	}
	//throws error if it can not send a message
	public void sendMessage(int numMessages) {
		if(numMessages>0){
		ArrayList <Message> temp = new ArrayList<Message>();
		for(int i = safeMessages.size() - numMessages;i<safeMessages.size();i++){
			temp.add(safeMessages.get(i));
		}
		//temp.addAll(safeMessages);
		System.out.println(temp);
		for(int i =0; i<safeOutStreams.size();i++){
				
				try {
					safeOutStreams.get(i).writeObject(temp);
					safeOutStreams.get(i).flush();
					System.out.println("Sent to " + i);
				}catch (java.net.SocketException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					try {
						safeOutStreams.remove(i).close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("Could not close out");
					}
					
					try {
						safeInStreams.remove(i).close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("Could not close in");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
				
		}
		}
	}
	//returns number of extra messages
	public int readMessage() throws ClassNotFoundException, IOException,SocketTimeoutException{
		int origLength = safeMessages.size();
		
		for(int i=0; i<safeInStreams.size();i++){
			ObjectInputStream in = safeInStreams.get(i);
			try{
			safeMessages.add((Message) (in.readObject()));
			System.out.println("i: " + i);
			}catch(SocketTimeoutException s){
				
			}catch(ClassCastException e){
				
			}catch(ClassNotFoundException e){
				
			}catch(IOException e){
				/*in.close();
				safeOutStreams.remove(i);
				safeInStreams.remove(i);
				System.out.println("IO error");*/
			}
			
		}
		/*Iterator<ObjectInputStream> it = safeInStreams.iterator();
		
		while(it.hasNext()){
			ObjectInputStream i = it.next();
				try{
					safeMessages.add((Message)i.readObject());
					}catch(SocketTimeoutException e){
						
					} catch (ClassNotFoundException e) {

						e.printStackTrace();
					} catch (IOException e) {
						i.close();
						it.remove();
						//e.printStackTrace();
					}
			}*/
		return safeMessages.size() - origLength;
			}
	
	public String getPassword(){
		return password;
	}
	//TODO accepts user if b is true denies otherwise
	public void acceptUser(boolean b,String pass) throws IOException{
		
		if(b){
			if(authenticateUser(pass)){
			try{
			Socket s = serv.accept();
			s.setSoTimeout(100);
			super.joinedUserSockets.add(s);
			super.safeOutStreams.add(new ObjectOutputStream(s.getOutputStream()));
			super.safeInStreams.add(new ObjectInputStream(s.getInputStream()));
			}catch(java.net.SocketTimeoutException e){
				
			}
			//outStreams.get(0).write(1); Messed with writing Dont remember why I did this but will keep commented out
		}
		}
	}
	//TODO kicks user
	public void kickUser(User user){
		
	}
	//TODO makes sure user is allowed to connect
	private boolean authenticateUser(String pass){
		if(isPassworded){
			if(pass.equals(password)){
			return true;
			}
			return false;
		}
		
		return true;
	}
}
