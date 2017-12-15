package threads;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ui.MainForm;

public class SendFileThread extends Thread{
	private File filePath;
	MainForm main;
	DataOutputStream dos;
	public SendFileThread(File filePath, MainForm main) {
		super();
		this.filePath = filePath;
		this.main = main;
	}
	public void run()
	{
		FileInputStream fis;
		int progress = 0;
		try {
			//Socket socket = new Socket(InetAddress.getByName(main.getMyHost()), main.getMyPort());
			
			//The InetAddress specification
			//InetAddress IA = InetAddress.getByName("localhost"); 
			fis = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			dos = new DataOutputStream(main.socket.getOutputStream());
			byte[] contents;
			long fileLength = filePath.length(); 
			long current = 0;
			//Frame to display the progress bar
			JFrame progFrame =  new JFrame("Progress frame");
		  	progFrame.setBounds(500, 300, 350, 200);
		  	
		  	JPanel panel = new JPanel();
		  	panel.setLayout(new GridLayout(10, 1));
		  	
		  	//Progress bar
		  	JProgressBar progressbar = new JProgressBar(0, 100); 	
		  	progressbar.setSize(new Dimension(100, 15));
		    progressbar.setBackground(Color.white);
		    progressbar.setForeground(Color.green);
		    for(int i = 0; i <= 2; i++){
			    panel.add(new JPanel());
			}
			JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
			JLabel progressLabel = new JLabel();
		
			progressPanel.add(progressLabel);	    
			panel.add(progressPanel, BorderLayout.EAST);
			panel.add(progressbar);
			progFrame.add(panel);
		    progFrame.setVisible(true);	
			//Going to receive data
			while(current!=fileLength){ 
				int size = 10000;
				if(fileLength - current >= size)
					current += size;    
				else{ 
					size = (int)(fileLength - current); 
					current = fileLength;
				} 
				contents = new byte[size]; 
				bis.read(contents, 0, size); 
				dos.write(contents);
			   	
				System.out.print("Sending file ... "+ (progress = (int) ((current*100)/fileLength)) +"% complete!");
				progressLabel.setText("Sending file...." + progress + "%");
				progressbar.setValue(progress);
				
			}
			progFrame.setVisible(false);
			dos.flush(); 
			//File transfer done. Close the socket connection!
			//socket.close();
			//showing the success message
		  JOptionPane.showMessageDialog(null, "File sent successfully !");		 
		  System.out.println("[SendFileThread] :: File sent succesfully!");  	
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
			e1.printStackTrace();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,"Failed to send file. Please send again");
			System.out.println(" File not sent !");  
			e1.printStackTrace();
		}
	}

}
