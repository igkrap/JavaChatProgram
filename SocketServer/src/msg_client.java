import java.net.Socket;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public class msg_client {
	public static void main(String[] args) {
		new StartFrame();
	}
}

class StartFrame extends JFrame {
	JTextField ipnum, name;
	ImageIcon icon;
	URL imgURL;
	JPanel down_panel;

	public StartFrame() {
		setTitle("connect");
		// setSize(500,300);
		setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 3,
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
		setLocation(
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
						- (int) (this.getSize().getWidth() / 2),
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
						- (int) (this.getSize().getHeight() / 2));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		imgURL = getClass().getClassLoader().getResource("Title.png");
		icon = new ImageIcon(imgURL);
		Image img = icon.getImage();
		img = img.getScaledInstance((int) getSize().getWidth(),
				(int) getSize().getHeight() - (int) (getSize().getHeight() / 10), java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		JPanel background = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(), 0, 0, null);
				setOpaque(false); // 그림을 표시하게 설정,투명하게 조절
				super.paintComponent(g);
			}
		};

		background.setBounds(0, 0, (int) this.getSize().getWidth(),
				(int) (this.getSize().getHeight()) - (int) (getSize().getHeight() / 5));
		background.setLayout(null);
		down_panel = new JPanel();
		JLabel ipnum_label = new JLabel("IP: ");
		ipnum = new JTextField(15);
		ipnum.setText("220.69.249.219");
		JLabel name_label = new JLabel("NickName: ");
		name = new JTextField(15);
		name.setText("Name");
		JButton start_button = new JButton("Connect");
		int down_panel_X = (int) (background.getSize().getWidth() / 3) - (int) (background.getSize().getWidth() / 9);
		int down_panel_Y = (int) (background.getSize().getHeight() - (int) background.getSize().getHeight() / 10);
		int down_panel_width = (int) background.getSize().getWidth() / 2;
		int down_panel_height = (int) background.getSize().getHeight() / 5;
		down_panel.setBounds(down_panel_X, down_panel_Y, down_panel_width, down_panel_height);
		down_panel.setLayout(new FlowLayout());
		down_panel.add(ipnum);
		down_panel.add(name);
		down_panel.add(start_button);
		down_panel.setBackground(new Color(255, 0, 0, 0));
		background.add(down_panel);
		add(background);
		start_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ipnum.getText();
				String nick_name = name.getText();
				if (isIPv4(ip)) {
					dispose();
					new Chater(ip, nick_name);
				} else {
					JOptionPane.showMessageDialog(null, "ip를 제대로 입력해주세요.");
				}
			}
		});
		// setUndecorated(true);

		setVisible(true);
	}

	public static boolean isIPv4(String str) {
		return Pattern.matches("((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])([.](?!$)|$)){4}", str);
	}
}

class Chater {
	Receiver receiver;
	Socket socket;
	ChatFrame frame;
	Sender sender;
	public Chater(String ip, String name) {
		try {
			socket = new Socket(ip, 2400);
			sender =new Sender(socket,name);
			sender.start();
			frame = new ChatFrame(sender);
			receiver = new Receiver(socket, frame);
			receiver.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("서버 접속 실패");
		}
	}
}

class ChatFrame extends JFrame {
	Sound sound;
	Socket socket;
	Sender sender;
	JTextArea receiveArea, usersListArea, sendArea;
	JTextPane receivePane;
	JLabel users_label;

	public ChatFrame(Sender s) {
		sound=new Sound();
		sender = s;
		setTitle("ChatRoom");
		setLayout(null);
		//setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 4,
		//		(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
		setSize(384,432);
		setLocation(
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
						- (int) (this.getSize().getWidth() / 2),
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
						- (int) (this.getSize().getHeight() / 2));
		System.out.println(getSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					quit();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		});
		JButton send_button = new JButton("Send");
		users_label = new JLabel("접속자 수: ");
		receivePane = new JTextPane();
	    receivePane.setContentType("text/html");
	    receivePane.setEditable(false);
	   //textPane.setOpaque(false); // 불투명-false (투명)
	    receivePane.setBackground(Color.WHITE);
		receiveArea = new JTextArea(19, 20);
		receiveArea.setEditable(false);
		receiveArea.setLineWrap(true);
		receiveArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		receiveArea.setWrapStyleWord(true);
		usersListArea = new JTextArea(19, 10);
		usersListArea.setEditable(false);
		usersListArea.setLineWrap(true);
		sendArea = new JTextArea(1, 20);
		sendArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(receiveArea);
		JScrollPane scrollPane2 = new JScrollPane(usersListArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		users_label.setBounds(0,0,70,20);
		scrollPane2.setBounds(0,20,100,300);
		scrollPane.setBounds(110,20,250,300);
		sendArea.setBounds(10,340,250,30);
		send_button.setBounds(280,340,80,30);
		add(users_label);
		add(sendArea);
		add(scrollPane2);
		add(scrollPane);
		add(send_button);
		sendArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					try {
						sendChat();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.consume();
				}
			}
		});

		send_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sendChat();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		System.out.println(scrollPane.getSize());
		System.out.println(scrollPane2.getSize());
		
		setVisible(true);
	}
	void updateChat(String s,String timestamp) throws Exception {
		String time="["+timestamp+"]";
		receiveArea.append(s+"\n");
		receiveArea.append("\t\t\t"+time+"\n");
		receiveArea.setCaretPosition(receiveArea.getDocument().getLength());
		if(!isFocused()) {toFront(); sound.dingdong(false,"note.wav");}	
	}
	void updateUserList(String[] userList) {
		usersListArea.setText("");
		users_label.setText("접속자 수: " + (userList.length));
		for (String user :userList) {
			usersListArea.append(user + "\n");
		}
	}
	void sendChat() throws Exception {
		if (sendArea.getText().isEmpty()) {
			return;
		}
		sender.send(sendArea.getText());
		sendArea.setText(null);
	}

	void quit() throws Exception {
		sender.quit();
	}
}
class Receiver extends Thread {
	Socket socket;
	ChatFrame cFrame;
	ArrayList<String> chatLogs = new ArrayList<String>();
	InputStream in;// 읽는 stream
	ObjectInputStream ois;
	HashMap<String, Object> Data=new HashMap<String,Object>();
	public Receiver(Socket s, ChatFrame frame) throws Exception {
		socket = s;
		cFrame = frame;
		in=socket.getInputStream();
		ois=new ObjectInputStream(in);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Data= (HashMap<String, Object>) ois.readObject();
				if (Data.get("protocol")==null) break;
				String protocol=(String)Data.get("protocol");
				switch(protocol) {
					case "quit":
						socket.close();
						break;
					case "msg":
						chatLogs.add((String) Data.get("message"));
						cFrame.updateChat((String) Data.get("message"),(String) Data.get("time"));
						break;
					case "update":
						cFrame.updateUserList((String[])Data.get("userList"));
						break;
				}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			
			System.exit(0);
		}
	}
}

class Sound {
	boolean isMute=false;
	public Sound() {	
	}
	public void dingdong(boolean LOOP,String filename) {
        try (InputStream in = getClass().getResourceAsStream(filename)) {
            InputStream bufferedIn = new BufferedInputStream(in);
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                if(LOOP)clip.loop(-1); 
            }
        } catch (Exception e) {
           //e.printStackTrace();
       }
    }	
}

class Sender extends Thread {
	OutputStream out;
	ObjectOutputStream oos;
	Socket socket;
	HashMap<String, Object> Data= new HashMap<String,Object>();
	
	public Sender(Socket s, String n) throws Exception {
		socket = s;
		out = socket.getOutputStream();
		oos = new ObjectOutputStream(out);
		setNick(n);
	}
	@Override
	public void run() {
		try {
		while (true) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("protocol", "alive");
			oos.writeObject(map);
			sleep(10000);
		}}
		catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	
	void send(String msg) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("protocol", "msg");
		map.put("message", msg);
		oos.writeObject(map);
	}
	void setNick(String n) throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("protocol", "setName");
		map.put("name", n);
		oos.writeObject(map);
	}
	void quit() throws Exception {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("protocol", "quit");
		oos.writeObject(map);
	}
}
