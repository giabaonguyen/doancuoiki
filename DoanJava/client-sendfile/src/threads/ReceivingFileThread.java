package threads;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ui.MainForm;

public class ReceivingFileThread extends Thread{
	private MainForm main;
	private Socket fSoc;
	String filePath;
	public ReceivingFileThread(MainForm main, Socket fSoc,String filePath) {
		super();
		this.main = main;
		this.fSoc = fSoc;
		this.filePath = filePath;
	}
	public void run()
	{
		int progress=0;
		int size = 10000;
		byte[] contents = new byte[size];

		//Initialize the FileOutputStream to the output file's full path.
		FileOutputStream fos;
		try {
			//ServerSocket ssock = new ServerSocket(main.getMyPort());			
			//Socket socket = ssock.accept();
			fos = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);			
			InputStream is = this.fSoc.getInputStream();
			int bytesRead = 0; 
	        
			//receiving file Message dialog window
			JFrame recFileMessFrame =  new JFrame("Recieving File");
			recFileMessFrame.setBounds(500, 300, 350, 200);
		  			
		  	JPanel panel = new JPanel();
		  	panel.setLayout(new GridLayout(9, 1));
		  	
		 	//progress bar for recieved data
		  	JProgressBar progressbar = new JProgressBar(0, 100); 	
		  	progressbar.setSize(new Dimension(100, 15));
		    progressbar.setBackground(Color.white);
		    progressbar.setForeground(Color.gray);		    
		   
		    //adding blank frame
		    for(int i = 0; i <= 2; i++){
			    panel.add(new JPanel());
			}
		    
		    JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
			JLabel progressLabel = new JLabel();
			
			//adding progress label to progress panel
			progressPanel.add(progressLabel);			
			panel.add(progressPanel);
			
		    //adding progress panel to main panel
			panel.add(progressbar);
		
			recFileMessFrame.add(panel);
			recFileMessFrame.setVisible(true);
			long recFileSize = 0;
			
		    while((bytesRead=is.read(contents))!=-1){
		    			   	
		    	recFileSize += bytesRead; 
		    	//change the progressbar value
				progressbar.setValue((progress = (int)((recFileSize*100)/filePath.length())));
				progressLabel.setText("File Recieved......." + progress + "%");		
				bos.write(contents, 0, bytesRead);
			}
			bos.flush();
			recFileMessFrame.setVisible(false);
			JOptionPane.showMessageDialog(null, "File recieved ! " + Paths.get(filePath).getFileName() );
			fSoc.close();
			//socket.close();
			//ssock.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
