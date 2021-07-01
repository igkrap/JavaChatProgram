import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class msg_server extends Thread {
	static ArrayList<msg_server> users = new ArrayList<msg_server>();	
	Socket socket;
	String nick="";
	HashMap<String, Object> Data=new HashMap<String,Object>();
	InputStream in;
	ObjectInputStream ois;
	OutputStream out;
	ObjectOutputStream oos;
	static ServerSocket ss;
	public msg_server(Socket socket) {
		this.socket = socket;
		nick="";
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			ois=new ObjectInputStream(in);
			oos=new ObjectOutputStream(out);
			this.socket.setSoTimeout(15000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	public void broadCast(String msg) throws Exception {
		for(msg_server user :users)
		{ user.send(msg);}
	}
	public void broadCast(String[] userList) throws Exception {
		for(msg_server user :users)
		{ user.updateUsersList(userList);}
	}
	public void updateUsersList(String[] userList) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("protocol", "update");
		map.put("userList", userList);
		oos.writeObject(map);
	}
	public void send(String msg) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("protocol", "msg");
		map.put("message", msg);
		map.put("time", new SimpleDateFormat ( "HH:mm:ss").format(new Date()));
		oos.writeObject(map);
	}
	
	
	@Override
	public synchronized void run() {
		try {
			while (true) {
				Data= (HashMap<String, Object>) ois.readObject();
				if (Data.get("protocol")==null) break;
				String protocol=(String)Data.get("protocol");
				switch(protocol) {
					case "setName":
						this.nick=(String)Data.get("name");
						broadCast("Welcome. "+this.nick);
						
						ArrayList<String> names1 =new ArrayList<String>(); 

						for(msg_server user : users) {
							names1.add(user.nick);
						}
						broadCast(names1.toArray(new String[names1.size()]));
						break;
					case "changeName":
						this.nick=(String)Data.get("name");
						ArrayList<String> names2 =new ArrayList<String>(); 

						for(msg_server user : users) {
							names2.add(user.nick);
						}
						broadCast(names2.toArray(new String[names2.size()]));
						break;
					case "whisper":
						for(msg_server user : users) {
							if(user.nick.equals((String)Data.get("name"))) 
								user.send(">>"+this.nick+": "+(String) Data.get("message"));
						}
						break;
					case "quit":
						send("quit");
						break;
					case "msg":
						broadCast(this.nick+": "+(String) Data.get("message"));
						break;
					case "alive":
						break;
				}
	
		}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		finally{
			System.out.println("socket disconnect");
			try {
				in.close();
				out.close();
				ois.close();
				oos.close();
				socket.close();
				users.remove(this);
				ArrayList<String> names =new ArrayList<String>(); 

				for(msg_server user : users) {
					names.add(user.nick);
				}
				broadCast(names.toArray(new String[names.size()]));
				broadCast(nick+ " has left the chat");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		}
	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 2400;
		
		try {
			ss = new ServerSocket(port);
			System.out.println("Server Open");
			while (true) {
				Socket cs = ss.accept();
				System.out.println("Client " + cs.getRemoteSocketAddress() + " : " + cs.getPort());
				msg_server serverThread = new msg_server(cs);
				serverThread.start();
				users.add(serverThread);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			try {
				ss.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		}
	}
	}
