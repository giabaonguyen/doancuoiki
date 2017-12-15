package server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;

public class MainForm extends JFrame {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
    private Thread t;
    private ServerThread serverThread;
    protected boolean ok = false, conflict = false;
    /** Chat List  **/
    Vector<Socket> socketList = new Vector<>();
    Vector<String> clientList = new Vector<>();
    /** File Sharing List **/
    private Vector clientFileSharingUsername = new Vector();
    private Vector clientFileSharingSocket = new Vector();
    /** Server **/
    ServerSocket server;
    
    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
    }

	public boolean checkLogin(String username,String pass){
		try{
			//create file
			File fread = new File("database.txt");
			FileReader fr = new FileReader(fread);
			//read data
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null){
				String[] str= line.split(" ");
				String subUsername = str[0];
				String subPass = str[1];
				if (subUsername.compareTo(username)==0 && (subPass.compareTo(pass)==0)){
					System.out.println("VALID");
					ok = true;
					return true;
				}

			}
			//close
			fr.close();
			br.close();

		} catch (Exception ex) {
			appendMessage("CHECK LOGIN: "+ex);
		}
		return ok;
	}
	public boolean checkExist(String username){
		try {
			//create file
			File fread = new File("database.txt");
			FileReader fr = new FileReader(fread);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null){
				String[] str= line.split(" ");
				String subUsername = str[0];
				if (subUsername.compareTo(username)==0){
					System.out.println("CONFLICT");
					conflict = true;
				}
			}
			//close
			fr.close();
			br.close();

		} catch (Exception ex) {
			appendMessage("CHECK EXIST: "+ex);
		}
		return conflict;
	}
	public void save(String username,String pass){
		try {
			UserInfo user1 = new UserInfo(username,pass);
			//create file
			File fwrite = new File("database.txt");
			PrintWriter fw = new PrintWriter(new FileWriter(fwrite, true));
			//store data
			fw.append(user1.userName).append(" ").append(user1.password).append("\n");
			//close
			System.out.println("SAVE SUCCESSFULLY");
			fw.close();
		} catch (IOException ex) {
			appendMessage("Loi ghi file: " + ex);
		}

	}
    public void appendMessage(String msg){
        Date date = new Date();
        jTextArea1.append(sdf.format(date) +": "+ msg +"\n");
        jTextArea1.setCaretPosition(jTextArea1.getText().length() - 1);
    }
    
    /** Setters **/
    public void setSocketList(Socket socket){
        try {
            socketList.add(socket);
            appendMessage("[setSocketList]: Added");
        } catch (Exception e) { appendMessage("[setSocketList]: "+ e.getMessage()); }
    }
    public void setClientList(String client){
        try {
            clientList.add(client);
            appendMessage("[setClientList]: Added");
        } catch (Exception e) { appendMessage("[setClientList]: "+ e.getMessage()); }
    }

    public void setClientFileSharingUsername(String user){
        try {
            clientFileSharingUsername.add(user);
        } catch (Exception e) { }
    }
    
    public void setClientFileSharingSocket(Socket soc){
        try {
            clientFileSharingSocket.add(soc);
        } catch (Exception e) { }
    }
   
    public Socket getClientList(String client){
        Socket tsoc = null;
        for(int x=0; x < clientList.size(); x++){
            if(clientList.get(x).equals(client)){
                tsoc = (Socket) socketList.get(x);
                break;
            }
        }
        return tsoc;
    }
    
    
    public void removeFromTheList(String client){
        try {
            for(int x=0; x < clientList.size(); x++){
                if(clientList.elementAt(x).equals(client)){
                    clientList.removeElementAt(x);
                    socketList.removeElementAt(x);
                    appendMessage("[Removed]: "+ client);
                    break;
                }
            }
        } catch (Exception e) {
            appendMessage("[RemovedException]: "+ e.getMessage());
        }
    }
    
    public Socket getClientFileSharingSocket(String username){
        Socket tsoc = null;
        for(int x=0; x < clientFileSharingUsername.size(); x++){
            if(clientFileSharingUsername.elementAt(x).equals(username)){
                tsoc = (Socket) clientFileSharingSocket.elementAt(x);
                break;
            }
        }
        return tsoc;
    }
    
    public void removeClientFileSharing(String username){
        for(int x=0; x < clientFileSharingUsername.size(); x++){
            if(clientFileSharingUsername.elementAt(x).equals(username)){
                try {
                    Socket rSock = getClientFileSharingSocket(username);
                    if(rSock != null){
                        rSock.close();
                    }
                    clientFileSharingUsername.removeElementAt(x);
                    clientFileSharingSocket.removeElementAt(x);
                    appendMessage("[FileSharing]: Removed "+ username);
                } catch (IOException e) {
                    appendMessage("[FileSharing]: "+ e.getMessage());
                    appendMessage("[FileSharing]: Unable to Remove "+ username);
                }
                break;
            }
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server");
        setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setText("Port:");

        jTextField1.setText("2809");

        jButton1.setText("Start Server");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setText("Stop Server");
        jButton2.setEnabled(false);
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new Color(0, 0, 0));
        jTextArea1.setColumns(20);
        jTextArea1.setForeground(new Color(0, 255, 255));
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
        int port = Integer.parseInt(jTextField1.getText());
        serverThread = new ServerThread(port, this);
        t = new Thread(serverThread);
        t.start();
        
        new Thread(new OnlineListThread(this)).start();
        
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
    }

    private void jButton2ActionPerformed(ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(null, "Close Server.?");
        if(confirm == 0){
            serverThread.stop();
        }
    }
    
    public static void main(String args[]) {
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }
    private JButton jButton1;
    private JButton jButton2;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JTextField jTextField1;
}
