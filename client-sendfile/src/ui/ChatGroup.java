package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChatGroup {

	JFrame frame;
	private JTextField textField;
	private JTextField txtMember;
	JTextArea textArea;
	StringTokenizer st;
	JTextPane txtGroupMember;
	JLabel lblGroupMembers;
	public MainForm main;
	String username;
	String groupname;
	Socket socket;
	DefaultStyledDocument doc;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatGroup window = new ChatGroup();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public ChatGroup() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(groupname);
		frame.setBounds(100, 100, 603, 403);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(0, 0, 422, 303);
		
		frame.getContentPane().add(textArea);
		
		
		textField = new JTextField();
		textField.setBounds(0, 321, 422, 32);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				chatMember(groupname, username, textField.getText(),socket);
				System.out.println("SEND "+textField.getText());
				textField.setText("");
			}
		});
		btnSend.setBounds(459, 326, 89, 23);
		frame.getContentPane().add(btnSend);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				addMember(groupname,txtMember.getText(),socket);
				txtMember.setText("");
				
			}
		});
		btnAdd.setBounds(459, 32, 89, 23);
		frame.getContentPane().add(btnAdd);
		txtMember = new JTextField();
		txtMember.setText("Add member");
		txtMember.setBounds(432, 74, 145, 20);
		frame.getContentPane().add(txtMember);
		txtMember.setColumns(10);
		doc = new DefaultStyledDocument();

		txtGroupMember = new JTextPane(doc);
		txtGroupMember.setEditable(true);
		txtGroupMember.setBounds(432, 126, 145, 114);
		frame.getContentPane().add(txtGroupMember);
		
		lblGroupMembers = new JLabel("Group members");
		lblGroupMembers.setBounds(432, 105, 97, 14);
		frame.getContentPane().add(lblGroupMembers);
	}
	public void init(String groupname,String username,Socket socket){
		this.groupname = groupname;
		this.username = username;
		this.socket = socket;
		initialize();
		
	}
	public void addMember(String groupName,String member,Socket socket){
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("CMD_ADD_MEMBER "+groupName+" "+member);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void chatMember(String groupName,String member,String msg,Socket socket) {
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("CMD_CHAT_MEMBER "+groupName+" "+member+" "+msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void showOnLineList(Vector list){
//	     try {
//	         txtGroupMember.setEditable(true);
//	         txtGroupMember.setContentType("text/html");
//	         StringBuilder sb = new StringBuilder();
//	         Iterator it = list.iterator();
//	         sb.append("<html><table>");
//	         while(it.hasNext()){
//	             Object e = it.next();
////	             URL url = getImageFile();
////	             Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
//	             //sb.append("<tr><td><img src='").append(url).append("'></td><td>").append(e).append("</td></tr>");
//	             sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
//	             System.out.println("Online: "+ e);
//	         }
//	         sb.append("</table></body></html>");
//	         txtGroupMember.removeAll();
//	         txtGroupMember.setText(sb.toString());
//	         txtGroupMember.setEditable(false);
//	     } catch (Exception e) {
//	         System.out.println(e.getMessage());
//	     }
		try {
		StyleContext context = new StyleContext();
        // build a style
        Style style = context.addStyle("test", null);
		for(int i=0;i<list.size();i++) {
			doc.insertString(i,list.elementAt(i).toString(),style);
			System.out.println("MEMBER: "+list.elementAt(i).toString());
		}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	 }
	public void sampleOnlineList(Vector list){
	     txtGroupMember.setEditable(true);
	     txtGroupMember.removeAll();
	     txtGroupMember.setText("");
	     Iterator i = list.iterator();
	     while(i.hasNext()){
	         Object e = i.next();
	         /*  Show Online Username   */
	         JPanel panel = new JPanel();
	         panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	         panel.setBackground(Color.white);
	         
//	         Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
	         JLabel label = new JLabel();
	         label.setText(" "+ e);
	         panel.add(label);
	         int len = txtGroupMember.getDocument().getLength();
	         txtGroupMember.setCaretPosition(len);
	         txtGroupMember.insertComponent(panel);
	         /*  Append Next Line   */
	         sampleAppend();
	     }
	     txtGroupMember.setText("bbb");
	     txtGroupMember.setEditable(false);
		
	 }
	public void appendOnlineList(Vector list){
//	       showOnLineList(list);  //  Original Method()
	     sampleOnlineList(list);  // - Sample Method()
	 }
	public void sampleAppend(){
	     int len = txtGroupMember.getDocument().getLength();
	     txtGroupMember.setCaretPosition(len);
	     txtGroupMember.replaceSelection("\n");
	 }
	public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
	 	textArea.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        textArea.setEditable(false);
    }
	public void getMsgHeader(String header, Color color){
	     int len = textArea.getDocument().getLength();
	     txtGroupMember.setCaretPosition(len);
	     textArea.replaceSelection(header+":");
	 }
	public void getMsgContent(String msg, Color color){
	     int len = textArea.getDocument().getLength();
	     textArea.setCaretPosition(len);
	     textArea.replaceSelection(msg +"\n\n");
	 }
}
