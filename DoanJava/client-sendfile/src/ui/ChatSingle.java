package ui;

import org.apache.commons.lang3.StringEscapeUtils;

import threads.SendFileThread;

//import com.sun.glass.ui.Application;

import utils.FileUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.DropMode;

public class ChatSingle extends JFrame implements WindowStateListener, AdjustmentListener{
	private static final long serialVersionUID = 2690121762619830343L;
	private static final String STYLE_SHEET = ".chat-box { margin: 2px; }"
			+ ".chat-box p { display: block; padding: 7px; margin-top: 2px; margin-bottom: 2px; word-wrap: break-word;}"
			+ ".chat-msg1 { background-color: #add8e6; }" + ".chat-msg2 {background-color: #e6adb5; }";
	private static final String DEFAULT_HTML_FORMAT = "<style>" + STYLE_SHEET + "</style>"
			+ "<div id=content class=chat-box> </div>";
	private static final String DEFAULT_FRIEND_CHAT_FORMAT = "<p class=chat-msg1>%s</p>";
	private static final String DEFAULT_WE_CHAT_FORMAT = "<p class=chat-msg2>%s</p>";
	private JTextField inputField;
	private JTextPane dispField;
	private final String frienName;
	private JFileChooser fileChooser;
	private MainForm main;
	StringTokenizer st;
	DataInputStream dis;
	DataOutputStream dos;

	protected void initializeComponents() {
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(0, 392, 305, 30);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel);
		panel.setLayout(null);
		
		fileChooser = new JFileChooser();
		inputField = new JTextField();
		inputField.setBounds(0, 5, 214, 20);
		inputField.setToolTipText("Enter your chat content then enter to send");
		panel.add(inputField);
		inputField.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performChatSending();	
			}
		});

		JButton btnSend = new JButton("");
		btnSend.setBounds(262, 5, 33, 20);
		// btnSend.setIcon(new
		// ImageIcon(ResourceManager.getInstance().getImageByName("button-send.png")));
		btnSend.setToolTipText("Press to send your chat content");
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg) {
					performChatSending();		
			}
		});
		panel.add(btnSend);
		JButton btnFile = new JButton("file");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				performFileSending(arg);
			}
		});
		btnFile.setBounds(224, 4, 33, 21);
		panel.add(btnFile);
		

		dispField = new JTextPane();
		dispField.setEditable(false);
		dispField.setContentType("text/html");
		dispField.setText(DEFAULT_HTML_FORMAT);

		JScrollPane scrollPane = new JScrollPane(dispField);
		scrollPane.setBounds(0, 0, 305, 392);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);

		setSize(315, 460);

	}

	public ChatSingle(String friendname, MainForm main) {

		setTitle(friendname);
		this.frienName = friendname;
		this.main = main;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initializeComponents();
	}

	private void performChatSending() {
		String content = inputField.getText();
		if (content != null && (content = content.trim()).length() > 0) {
			try {
				dos = new DataOutputStream(main.socket.getOutputStream());
				dos.writeUTF("CMD_CHAT " + main.username + " " + frienName + " " + content);
			} catch (IOException e) {
				e.printStackTrace();
			}

			showWeChat(content);
			inputField.setText("");
		}
	}
	
	private void performFileSending(ActionEvent e) {
		int returnVal = fileChooser.showOpenDialog(this);
		if(returnVal==JFileChooser.APPROVE_OPTION) {
			File file =fileChooser.getSelectedFile();
			if(fileTypeAccept(file)) {
				JOptionPane.showMessageDialog(this, "OK");
				//Send file
				new SendFileThread(file, main).start();
				
			}else {
				JOptionPane.showMessageDialog(this, "File isn't supported");
			}
			
		}
		
	}
	private boolean fileTypeAccept(File file) {
		if(file.isDirectory()) {
			return true;
		}
		String extension = FileUtils.getExtension(file);
		if(extension!=null) {
			if(extension.equals(FileUtils.jpeg)||
			extension.equals(FileUtils.txt) ||
			extension.equals(FileUtils.png) ||
			extension.equals(FileUtils.mp3)){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public void windowStateChanged(WindowEvent e) {
		if (e.getNewState() == WindowEvent.WINDOW_ACTIVATED) {
			inputField.requestFocus();
		}
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			main.getInstance().removeFromChatSingleList(this);
		}
		super.processWindowEvent(e);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
	}

	private void appendRawHtmlChatContent(String html) {
		HTMLDocument doc = (HTMLDocument) dispField.getDocument();
		Element contentElement = doc.getElement("content");
		try {
			if (contentElement.getElementCount() > 0) {
				Element lastElement = contentElement.getElement(contentElement.getElementCount() - 1);
				doc = (HTMLDocument) contentElement.getDocument();
				doc.insertAfterEnd(lastElement, html);
			} else {
				doc.insertAfterStart(contentElement, html);
			}
		} catch (BadLocationException | IOException e) {

		}
	}

	public String getFrienName() {
		return frienName;
	}

	private void focusMe() {
		requestFocus();
		inputField.requestFocus();
	}

	public void showWeChat(String content) {
		appendRawHtmlChatContent(String.format(DEFAULT_WE_CHAT_FORMAT, prepareHtmlString(content)));
		focusMe();
	}

	public void showFriendChat(String content) {
		appendRawHtmlChatContent(String.format(DEFAULT_FRIEND_CHAT_FORMAT, prepareHtmlString(content)));
		focusMe();
	}

	//@SuppressWarnings("deprecation")
	private String prepareHtmlString(String rawContent) {
		return StringEscapeUtils.escapeXml(rawContent);
	}
}
