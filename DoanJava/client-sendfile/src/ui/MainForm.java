package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utils.MessageStyle;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;

//import sun.applet.Main;
//import sun.rmi.runtime.NewThreadAction;
import threads.ClientThread;

public class MainForm {
	public String username;
	String password;
	String host;
	int port;
	public Socket socket;
	DataOutputStream dos;
	public boolean attachmentOpen = false;
	private boolean isConnected = false;
	private String mydownloadfolder = "D:\\";
	protected JFrame frame;
	private JTextField textField;
	private JTextField txtTypeYourGroup;
	JTextArea jTextPane1;
	private JList<FriendEntry> txtpaneListFriend;
	private List<ChatSingle> listChatSignle = new ArrayList<>();
	private MainForm main = null;

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 598, 373);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		jTextPane1 = new JTextArea();
		jTextPane1.setBounds(0, 0, 435, 280);
		frame.getContentPane().add(jTextPane1);

		textField = new JTextField();
		textField.setBounds(0, 291, 435, 32);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnCreateGroup = new JButton("Create group");
		btnCreateGroup.setBounds(445, 197, 127, 23);
		frame.getContentPane().add(btnCreateGroup);

		txtTypeYourGroup = new JTextField();
		txtTypeYourGroup.setText("Type group name");
		txtTypeYourGroup.setBounds(448, 231, 124, 20);
		frame.getContentPane().add(txtTypeYourGroup);
		txtTypeYourGroup.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(445, 296, 89, 23);
		frame.getContentPane().add(btnSend);

		txtpaneListFriend = new JList<>();
		txtpaneListFriend.setBounds(445, 11, 127, 175);
		txtpaneListFriend.setCellRenderer(new FriendCellRenderer());
		txtpaneListFriend.addMouseListener(new ItemClickHanlder());
		frame.getContentPane().add(txtpaneListFriend);
	}

	public void connect(String cmd) {
		appendMessage(" Connecting...", "Status", Color.GREEN, Color.GREEN);
		try {
			if (cmd.compareTo("LOGIN") == 0) {
				socket = new Socket(host, port);
				dos = new DataOutputStream(socket.getOutputStream());
				/** Send our username and password **/
				dos.writeUTF("CMD_LOGIN " + username + " " + password);
				appendMessage(" Connected", "Status", Color.GREEN, Color.GREEN);
				appendMessage(" type your message now.!", "Status", Color.GREEN, Color.GREEN);
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String str = dis.readUTF();
				if (str.compareTo("CMD_LOGIN_SUCCESS") == 0) {
					/** Start Client Thread **/
					new Thread(new ClientThread(socket, this)).start();
					// were now connected
					isConnected = true;
				} else {
					isConnected = false;
				}

			} else if (cmd.compareTo("REGISTER") == 0) {
				socket = new Socket(host, port);
				dos = new DataOutputStream(socket.getOutputStream());
				/** Send our username and password **/
				dos.writeUTF("CMD_REGISTER " + username + " " + password);
				dos.flush();
				appendMessage(" Connected", "Status", Color.GREEN, Color.GREEN);
				appendMessage(" type your message now.!", "Status", Color.GREEN, Color.GREEN);
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String str = dis.readUTF();

				if (str.compareTo("CMD_REGISTER_SUCCESS") == 0) {
					/** Start Client Thread **/
					new Thread(new ClientThread(socket, this)).start();
					// were now connected
					isConnected = true;
				} else {
					isConnected = false;
				}

			}

		} catch (IOException e) {
			isConnected = false;
			JOptionPane.showMessageDialog(frame, "Unable to Connect to Server, please try again later.!",
					"Connection Failed", JOptionPane.ERROR_MESSAGE);
			appendMessage("[IOException]: " + e.getMessage(), "Error", Color.RED, Color.RED);
		}
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	public void appendMessage(String msg, String header, Color headerColor, Color contentColor) {
		jTextPane1.setEditable(true);
		getMsgHeader(header, headerColor);
		getMsgContent(msg, contentColor);
		jTextPane1.setEditable(false);
	}

	public void getMsgHeader(String header, Color color) {
		int len = jTextPane1.getDocument().getLength();
		jTextPane1.setCaretPosition(len);
		jTextPane1.replaceSelection(header + ":");
	}

	public void getMsgContent(String msg, Color color) {
		int len = jTextPane1.getDocument().getLength();
		jTextPane1.setCaretPosition(len);
		jTextPane1.replaceSelection(msg + "\n\n");
	}

	public URL getImageFile() {
		URL url = this.getClass().getResource("/images/online.png");
		return url;
	}

	public void loadListFriend(List<String> friends) {
		DefaultListModel<FriendEntry> friendEntries = new DefaultListModel<FriendEntry>();
		for (String friend : friends) {
			friendEntries.addElement(new FriendEntry(friend));
		}
		txtpaneListFriend.setModel(friendEntries);
	}

	public void appendMyMessage(String msg, String header) {
		jTextPane1.setEditable(true);
		getMsgHeader(header, Color.ORANGE);
		getMsgContent(msg, Color.LIGHT_GRAY);
		jTextPane1.setEditable(false);
	}

	public void initFrame(String username, String password, String host, int port, String reg) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		frame.setTitle("You are logged in as: " + username);
		/** Connect **/
		connect(reg);
	}

	private class ItemClickHanlder extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				try {
					String friendName = txtpaneListFriend.getSelectedValue().getFriendName();
					if (!friendName.isEmpty()) {
						ChatSingle cs = getChatSingle(friendName);
						if(cs!=null) {
							cs.setVisible(true);
							return;
						}else {
						cs= new ChatSingle(friendName, getInstance());
						cs.setVisible(true);
						addToChatSingleList(cs);
						}
					}
				} catch (Exception e2) {

				}

			}
		}
	}

	private static class FriendEntry {
		private final String friendName;

		public FriendEntry(String name) {
			this.friendName = name;

		}

		public String getFriendName() {
			return friendName;
		}

	}

	private static class FriendCellRenderer extends JLabel implements ListCellRenderer<FriendEntry> {
		private static final long serialVersionUID = 7285154224115806852L;
		private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

		public FriendCellRenderer() {
			setOpaque(true);
			setIconTextGap(12);
			setHorizontalAlignment(CENTER);
		}

		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, FriendEntry value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText(String.format("<html>" + "<b>%s</b>" + "</html>", value.getFriendName().toUpperCase()));
			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			} else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}

	public void addToChatSingleList(ChatSingle chatSingle) {
		listChatSignle.add(chatSingle);
	}

	public void removeFromChatSingleList(ChatSingle chatSingle) {
		listChatSignle.remove(chatSingle);
	}

	public ChatSingle getChatSingle(String friendName) {
		for (ChatSingle cs : listChatSignle) {
			if (cs.getFrienName().equals(friendName)) {
				return cs;
			}

		}
		return null;
	}

	public MainForm getInstance() {
		return main;
	}

	public void createInstance(MainForm main) {
		this.main = main;
	}

	public String getMyHost() {
		return host;
	}

	public int getMyPort() {
		
		return port;
	}

	public String getMyUsername() {
		
		return username;
	}

}
