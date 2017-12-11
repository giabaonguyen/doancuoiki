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
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utils.MessageStyle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import threads.ClientThread;

public class MainForm {
	public String username;
    String password;
    String host;
    int port;
    Socket socket;
    DataOutputStream dos;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "D:\\";
	protected JFrame frame;
	private JTextField textField;
	private JTextField txtTypeYourGroup;
	JTextArea jTextPane1;
	private JTextPane txtpane2;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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
		
		JButton btnChatWith = new JButton("Chat with");
		btnChatWith.setBounds(445, 11, 89, 23);
		frame.getContentPane().add(btnChatWith);
		
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
		
		txtpane2 = new JTextPane();
		txtpane2.setBounds(445, 45, 127, 141);
		frame.getContentPane().add(txtpane2);
	}
	public void connect(String cmd){
        appendMessage(" Connecting...", "Status", Color.GREEN, Color.GREEN);
        try {
        	if (cmd.compareTo("LOGIN")==0){
        		 socket = new Socket(host, port);
                 dos = new DataOutputStream(socket.getOutputStream());
                 /** Send our username and password**/
                 dos.writeUTF("CMD_LOGIN "+ username +" " +password);
                 appendMessage(" Connected", "Status", Color.GREEN, Color.GREEN);
                 appendMessage(" type your message now.!", "Status", Color.GREEN, Color.GREEN);
                 DataInputStream dis = new DataInputStream(socket.getInputStream());
                 String str = dis.readUTF();
                 if (str.compareTo("CMD_LOGIN_SUCCESS")==0){
                	 /** Start Client Thread **/
                     new Thread(new ClientThread(socket, this)).start();
                     // were now connected
                     isConnected = true;
                 }else {
                	 isConnected = false;
                 }
                 
        	}
        	else if (cmd.compareTo("REGISTER") == 0){
        		 socket = new Socket(host, port);
                 dos = new DataOutputStream(socket.getOutputStream());
                 /** Send our username and password**/
                 dos.writeUTF("CMD_REGISTER "+ username +" " +password);
                 dos.flush();
                 appendMessage(" Connected", "Status", Color.GREEN, Color.GREEN);
                 appendMessage(" type your message now.!", "Status", Color.GREEN, Color.GREEN);
                 DataInputStream dis = new DataInputStream(socket.getInputStream());
                 String str = dis.readUTF();
                
                 if (str.compareTo("CMD_REGISTER_SUCCESS")==0){
                	 /** Start Client Thread **/
                     new Thread(new ClientThread(socket, this)).start();
                     // were now connected
                     isConnected = true;
                 }
                 else{
                	 isConnected = false;
                 }
                 
        	}
           
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(frame, "Unable to Connect to Server, please try again later.!","Connection Failed",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: "+ e.getMessage(), "Error", Color.RED, Color.RED);
        }
    }
	 public boolean isConnected(){
	        return this.isConnected;
	    }
 public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
	 	jTextPane1.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        jTextPane1.setEditable(false);
    }
 public void getMsgHeader(String header, Color color){
     int len = jTextPane1.getDocument().getLength();
     jTextPane1.setCaretPosition(len);
     jTextPane1.replaceSelection(header+":");
 }
 public void getMsgContent(String msg, Color color){
     int len = jTextPane1.getDocument().getLength();
     jTextPane1.setCaretPosition(len);
     jTextPane1.replaceSelection(msg +"\n\n");
 }
 public void appendOnlineList(Vector list){
     //  showOnLineList(list);  -  Original Method()
     sampleOnlineList(list);  // - Sample Method()
 }
 private void sampleOnlineList(Vector list){
     txtpane2.setEditable(true);
     txtpane2.removeAll();
     txtpane2.setText("");
     Iterator i = list.iterator();
     while(i.hasNext()){
         Object e = i.next();
         /*  Show Online Username   */
         JPanel panel = new JPanel();
         panel.setLayout(new FlowLayout(FlowLayout.LEFT));
         panel.setBackground(Color.white);
         
         Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
         JLabel label = new JLabel(icon);
         label.setText(" "+ e);
         panel.add(label);
         int len = txtpane2.getDocument().getLength();
         txtpane2.setCaretPosition(len);
         txtpane2.insertComponent(panel);
         /*  Append Next Line   */
         sampleAppend();
     }
     txtpane2.setEditable(false);
 }
 private void sampleAppend(){
     int len = txtpane2.getDocument().getLength();
     txtpane2.setCaretPosition(len);
     txtpane2.replaceSelection("\n");
 }
 public URL getImageFile(){
     URL url = this.getClass().getResource("/images/online.png");
     return url;
 }
 
 public void showOnLineList(Vector list){
     try {
         txtpane2.setEditable(true);
         txtpane2.setContentType("text/html");
         StringBuilder sb = new StringBuilder();
         Iterator it = list.iterator();
         sb.append("<html><table>");
         while(it.hasNext()){
             Object e = it.next();
             URL url = getImageFile();
             Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
             //sb.append("<tr><td><img src='").append(url).append("'></td><td>").append(e).append("</td></tr>");
             sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
             System.out.println("Online: "+ e);
         }
         sb.append("</table></body></html>");
         txtpane2.removeAll();
         txtpane2.setText(sb.toString());
         txtpane2.setEditable(false);
     } catch (Exception e) {
         System.out.println(e.getMessage());
     }
 }
 public void appendMyMessage(String msg, String header){
        jTextPane1.setEditable(true);
        getMsgHeader(header, Color.ORANGE);
        getMsgContent(msg, Color.LIGHT_GRAY);
        jTextPane1.setEditable(false);
    }
 public void initFrame(String username,String password, String host, int port,String reg){
	 this.username = username;
     this.password = password;
     this.host = host;
     this.port = port;
     frame.setTitle("you are logged in as: " + username);
     /** Connect **/
     connect(reg);
 }

}