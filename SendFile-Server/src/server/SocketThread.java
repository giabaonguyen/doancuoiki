package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class SocketThread implements Runnable{
    private static int customPort = 8000;
    private Socket socket;
    private MainForm main;
    private DataInputStream dis;
    private StringTokenizer st;
    private String client, filesharing_username;

    private final int BUFFER_SIZE = 100;

    public SocketThread(Socket socket, MainForm main){
        this.main = main;
        this.socket = socket;
        
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            main.appendMessage("[SocketThreadIOException]: "+ e.getMessage());
        }
    }

    /*   This method will get the client socket in client socket list then establish a connection    */
    private void createConnection(String receiver, String sender, String filename){
        try {
            main.appendMessage("[createConnection]: creating file sharing connection.");
            Socket s = main.getClientList(receiver);
            if(s != null){ // Client was exists
                main.appendMessage("[createConnection]: Socket OK");
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                main.appendMessage("[createConnection]: DataOutputStream OK");
                // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                String format = "CMD_FILE_XD "+sender+" "+receiver +" "+filename;
                dosS.writeUTF(format);
                main.appendMessage("[createConnection]: "+ format);
            }else{// Client was not exist, send back to sender that receiver was not found.
                main.appendMessage("[createConnection]: Client was not found '"+receiver+"'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR "+ "Client '"+receiver+"' was not found in the list, make sure it is on the online list!");
            }
        } catch (IOException e) {
            main.appendMessage("[createConnection]: "+ e.getLocalizedMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while(true){
                /** Get Client Data **/
                String data = dis.readUTF();
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
                /** Check CMD **/
                switch(CMD){
	                case "CMD_LOGIN":
	                	String username = st.nextToken();
	                	String password = st.nextToken();
	                	client = username;
		                if (main.checkLogin(username, password)){
		                    main.setClientList(username);
		                    main.setSocketList(socket);
			                System.out.println("LOGIN"+username);
			                main.ok = false;
			                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
							dos.writeUTF("CMD_LOGIN_SUCCESS");
							dos.flush();
			                main.appendMessage("[Client]: "+ username +" joins the chatroom.!");
			            }
		                else {
			                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			                dos.writeUTF("CMD_LOGIN_FAIL"+username);
		                }
	                	break;
	                case "CMD_REGISTER":
		                String usernameR = st.nextToken();
		                String passwordR = st.nextToken();
		                client = usernameR;
		                if (main.checkExist(usernameR)){
			                DataOutputStream dd = new DataOutputStream(socket.getOutputStream());
			                dd.writeUTF("CMD_REGISTER_FAIL");
			                main.conflict = false;
		                }
		                else {
			                main.save(usernameR,passwordR);

			                DataOutputStream dd = new DataOutputStream(socket.getOutputStream());
			                dd.writeUTF("CMD_REGISTER_SUCCESS");
		                }
	                	break;
//                    case "CMD_JOIN":
//                        /** CMD_JOIN [clientUsername] **/
//                        String clientUsername = st.nextToken();
//                        client = clientUsername;
//                        main.setClientList(clientUsername);
//                        main.setSocketList(socket);
//                        main.appendMessage("[Client]: "+ clientUsername +" joins the chatroom.!");
//                        break;
	                case "CMD_CREATE_GROUP":
		                String groupMember1 = st.nextToken();
		                String groupName = st.nextToken();
		                main.setGroupName(groupName);
		                main.setGroupMemberList(groupMember1);
	                	main.appendGroup(groupName,socket);
	                	Socket gSoc = main.getGroupSocket(groupName);
	                	customPort++;
	                	new Thread(new ServerThread(++customPort,main)).start();
	                	try {
	                		DataOutputStream dos = new DataOutputStream(gSoc.getOutputStream());
	                		dos.writeUTF("CMD_CREATE_GROUP_SUCCESS");
	                		main.appendMessage("[CMD_CREATE_GROUP] "+groupMember1+" created group "+groupName);
		                }
		                catch (Exception e){
	                		main.appendMessage("[CMD_CREATE_GROUP] "+e.getMessage());
		                }
	                	break;
	                case "CMD_ADD_MEMBER":
	                	String groupName2 =st.nextToken();
		                main.appendGroup(groupName2,socket);
	                	String memList = "";
	                	boolean success = false;
		                String groupMember="";
	                	for (Map.Entry<String,Vector> entry:main.map.entrySet()){
	                		if (groupName2.equals(entry.getKey())){
								while (st.hasMoreTokens()){
									groupMember = st.nextToken();
									for (int i =0;i<main.clientList.size();i++){
										if(groupMember.compareTo(main.clientList.elementAt(i).toString())==0){
											main.setGroupMemberList(groupMember);
											memList = memList +" "+entry.getValue().toString();
											System.out.println("MEM LIST: "+main.map.get(groupName2).toString());
											success = true;
											break;
										}
									}
								}

			                }
		                }
		                if (success){
			                DataOutputStream addMem = new DataOutputStream(socket.getOutputStream());
			                addMem.writeUTF("CMD_ADD_SUCCESS "+groupMember+" "+memList);
			                addMem.flush();

		                }
		                else {
			                DataOutputStream addMem = new DataOutputStream(socket.getOutputStream());
			                addMem.writeUTF("CMD_ADD_FAIL");
			                addMem.flush();
		                }
	                	break;
	                case "CMD_CHAT_MEMBER":
		                String groupName1 = st.nextToken();
		                String chatFrom = st.nextToken();
	                	String msgMem = "";
	                	while (st.hasMoreTokens()){
	                		msgMem = msgMem +" "+ st.nextToken();
		                }
	                	for (Map.Entry<String,Vector> entry:main.map.entrySet()) {
			                if (groupName1.equals(entry.getKey())) {
				                for (int i = 0; i < main.clientList.size(); i++) {
				                	for(int j = 0;j<entry.getValue().size();j++){
				                		if(main.clientList.elementAt(i).toString().compareTo(entry.getValue().elementAt(j).toString())==0)
						                if (!main.clientList.elementAt(i).equals(chatFrom)) {
							                try {
								                Socket tsoc2 = (Socket) main.socketList.elementAt(i);
								                DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
								                dos2.writeUTF("CMD_CHAT_GROUP " + msgMem);
								                System.out.println("CMD_MES "+entry.getValue().elementAt(j).toString()+" "+msgMem);
								                break;
							                } catch (IOException e) {
								                main.appendMessage("[CMD_CHAT_MEMBER]: " + e.getMessage());
							                }
						                }
					                }

				                }
			                }
		                }
		                main.appendMessage("[MESSAGE]: From "+msgMem);
	                	break;
                    case "CMD_CHAT":
                        /** CMD_CHAT [from] [sendTo] [message] **/
                        String from = st.nextToken();
                        String sendTo = st.nextToken();
                        String msg = "";
                        while(st.hasMoreTokens()){
                            msg = msg +" "+ st.nextToken();
                        }
                        Socket tsoc = main.getClientList(sendTo);
                        try {
                            DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                            /** CMD_MESSAGE **/
                            String content = from +": "+ msg;
                            dos.writeUTF("CMD_MESSAGE "+ content);
                            main.appendMessage("[Message]: From "+ from +" To "+ sendTo +" : "+ msg);
                        } catch (IOException e) {  main.appendMessage("[IOException]: Unable to send message to "+ sendTo); }
                        break;
                    
                    case "CMD_CHATALL":
                        /** CMD_CHATALL [from] [message] **/
                        String chatall_from = st.nextToken();
                        String chatall_msg = "";
                        while(st.hasMoreTokens()){
                            chatall_msg = chatall_msg +" "+st.nextToken();
                        }
                        String chatall_content = chatall_from +" "+ chatall_msg;
                        for(int x=0; x < main.clientList.size(); x++){
	                    if(!main.clientList.elementAt(x).equals(chatall_from)){
		                    try {
			                    Socket tsoc2 = (Socket) main.socketList.elementAt(x);
			                    DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
			                    dos2.writeUTF("CMD_MESSAGE "+ chatall_content);
		                    } catch (IOException e) {
			                    main.appendMessage("[CMD_CHATALL]: "+ e.getMessage());
		                    }
	                    }
                    }
	                    main.appendMessage("[CMD_CHATALL]: "+ chatall_content);
                        break;
                    case "CMD_SHARINGSOCKET":
                        main.appendMessage("CMD_SHARINGSOCKET : Client stablish a socket connection for file sharing...");
                        String file_sharing_username = st.nextToken();
                        filesharing_username = file_sharing_username;
                        main.setClientFileSharingUsername(file_sharing_username);
                        main.setClientFileSharingSocket(socket);
                        main.appendMessage("CMD_SHARINGSOCKET : Username: "+ file_sharing_username);
                        main.appendMessage("CMD_SHARINGSOCKET : File sharing is now open");
                        break;
                    
                    case "CMD_SENDFILE":
                        main.appendMessage("CMD_SENDFILE : Client sending a file...");
                        /*
                        Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Consignee]  from: Sender Format
                        Format: CMD_SENDFILE [Filename] [Size] [Consignee] to Receiver Format
                        */
                        String file_name = st.nextToken();
                        String filesize = st.nextToken();
                        String sendto = st.nextToken();
                        String consignee = st.nextToken();
                        main.appendMessage("CMD_SENDFILE : From: "+ consignee);
                        main.appendMessage("CMD_SENDFILE : To: "+ sendto);
                        /**  Get the client Socket **/
                        main.appendMessage("CMD_SENDFILE : preparing connections..");
                        Socket cSock = main.getClientFileSharingSocket(sendto); /* Consignee Socket  */
                        /*   Now Check if the consignee socket was exists.   */
                        if(cSock != null){ /* Exists   */
                            try {
                                main.appendMessage("CMD_SENDFILE : Connected..!");
                                /** First Write the filename..  **/
                                main.appendMessage("CMD_SENDFILE : Sending file to client...");
                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("CMD_SENDFILE "+ file_name +" "+ filesize +" "+ consignee);
                                /** Second send now the file content  **/
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int cnt;
                                while((cnt = input.read(buffer)) > 0){
                                    sendFile.write(buffer, 0, cnt);
                                }
                                sendFile.flush();
                                sendFile.close();
                                /** Remove client list **/
                                main.removeClientFileSharing(sendto);
                                main.removeClientFileSharing(consignee);
                                main.appendMessage("CMD_SENDFILE : File was send to client...");
                            } catch (IOException e) {
                                main.appendMessage("[CMD_SENDFILE]: "+ e.getMessage());
                            }
                        }else{ /*   Not exists, return error  */
                            /*   FORMAT: CMD_SENDFILEERROR  */
                            main.removeClientFileSharing(consignee);
                            main.appendMessage("CMD_SENDFILE : Client '"+sendto+"' was not found.!");
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_SENDFILEERROR "+ "Client '"+sendto+"' was not found, File Sharing will exit.");
                        }                        
                        break;
                    case "CMD_SENDFILERESPONSE":
                        /*
                        Format: CMD_SENDFILERESPONSE [username] [Message]
                        */
                        String receiver = st.nextToken(); // get the receiver username
                        String rMsg = ""; // get the error message
                        main.appendMessage("[CMD_SENDFILERESPONSE]: username: "+ receiver);
                        while(st.hasMoreTokens()){
                            rMsg = rMsg+" "+st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) main.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("CMD_SENDFILERESPONSE" +" "+ receiver +" "+ rMsg);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_SENDFILERESPONSE]: "+ e.getMessage());
                        }
                        break;
                    case "CMD_SEND_FILE_XD":  // Format: CMD_SEND_FILE_XD [sender] [receiver]                        
                        try {
                            String send_sender = st.nextToken();
                            String send_receiver = st.nextToken();
                            String send_filename = st.nextToken();
                            main.appendMessage("[CMD_SEND_FILE_XD]: Host: "+ send_sender);
                            this.createConnection(send_receiver, send_sender, send_filename);
                        } catch (Exception e) {
                            main.appendMessage("[CMD_SEND_FILE_XD]: "+ e.getLocalizedMessage());
                        }
                        break;
                    case "CMD_SEND_FILE_ERROR":  // Format:  CMD_SEND_FILE_ERROR [receiver] [Message]
                        String eReceiver = st.nextToken();
                        String eMsg = "";
                        while(st.hasMoreTokens()){
                            eMsg = eMsg+" "+st.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket eSock = main.getClientFileSharingSocket(eReceiver); // get the file sharing host socket for connection
                            DataOutputStream eDos = new DataOutputStream(eSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ERROR [Message]
                            eDos.writeUTF("CMD_RECEIVE_FILE_ERROR "+ eMsg);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: "+ e.getMessage());
                        }
                        break;
                        
                    
                    case "CMD_SEND_FILE_ACCEPT": // Format:  CMD_SEND_FILE_ACCEPT [receiver] [Message]
                        String aReceiver = st.nextToken();
                        String aMsg = "";
                        while(st.hasMoreTokens()){
                            aMsg = aMsg+" "+st.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = main.getClientFileSharingSocket(aReceiver); // get the file sharing host socket for connection
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDos.writeUTF("CMD_RECEIVE_FILE_ACCEPT "+ aMsg);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: "+ e.getMessage());
                        }
                        break;
//	                case "CMD_CHAT_SINGLE":
//	                	//format [CMD_CHAT_SINGLE] [from] [to] [MSG]
//	                	String sReceiver = st.nextToken();
//	                	String sSender = st.nextToken();
//	                	String sMsg = "";
//	                	while (st.hasMoreTokens()){
//	                		sMsg = sMsg +" "+ st.nextToken();
//		                }
//		                String msgContent = sSender+" "+sMsg;
//		                for(int x=0; x < main.clientList.size(); x++){
//			                if(!main.clientList.elementAt(x).equals(sReceiver)){
//				                try {
//					                Socket tsoc2 = (Socket) main.socketList.elementAt(x);
//					                DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
//					                dos2.writeUTF("CMD_MESSAGE "+ msgContent);
//				                } catch (IOException e) {
//					                main.appendMessage("[CMD_CHAT_SINGLE]: "+ e.getMessage());
//				                }
//			                }
//		                }
//		                main.appendMessage("[CMD_CHAT_SINGLE]: "+ msgContent);
//		                break;
                        
                    default: 
                        main.appendMessage("[CMDException]: Unknown Command "+ CMD);
                    break;
                }
            }
        } catch (IOException e) {
            /*   this is for chatting client, remove if it is exists..   */
            System.out.println(client);
            System.out.println("File Sharing: " +filesharing_username);
            main.removeFromTheList(client);
            if(filesharing_username != null){
                main.removeClientFileSharing(filesharing_username);
            }
            main.appendMessage("[SocketThread]: Client connection closed..!");
        }
    }
    
}
