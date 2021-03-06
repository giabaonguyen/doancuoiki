package ui;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class Login {

	private JFrame frame;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JTextField txtHost;
	private JTextField txtPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
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
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 584, 346);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("User name");
		lblNewLabel.setBounds(152, 68, 77, 14);
		frame.getContentPane().add(lblNewLabel);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(239, 65, 86, 20);
		frame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(152, 104, 58, 17);
		frame.getContentPane().add(lblNewLabel_1);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(239, 102, 86, 20);
		frame.getContentPane().add(txtPassword);
		
		JLabel lblHost = new JLabel("Host");
		lblHost.setBounds(152, 144, 46, 14);
		frame.getContentPane().add(lblHost);
		
		txtHost = new JTextField();
		txtHost.setText("127.0.0.1");
		txtHost.setBounds(239, 141, 86, 20);
		frame.getContentPane().add(txtHost);
		txtHost.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Port");
		lblNewLabel_2.setBounds(152, 189, 46, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		txtPort = new JTextField();
		txtPort.setText("2809");
		txtPort.setBounds(239, 186, 86, 20);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(121, 236, 89, 23);
		frame.getContentPane().add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectToServerLogin();
			}
		});
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectoToServerRegister();
			}
		});
		btnRegister.setBounds(239, 236, 89, 23);
		frame.getContentPane().add(btnRegister);
		
		JLabel lblPleaseLoginHere = new JLabel("Please login here");
		lblPleaseLoginHere.setBounds(152, 43, 173, 14);
		frame.getContentPane().add(lblPleaseLoginHere);
	}
	private void connectoToServerRegister(){
		 if(txtHost.getText().length() > 0 
				 && txtPort.getText().length() > 0
				 && txtUsername.getText().length() > 0 
				 && String.valueOf(txtPassword.getPassword()).length()>0){
	           
	                /*   Clean Username  */
	                String username = txtUsername.getText();
	                String password = String.valueOf(txtPassword.getPassword());
	              
	                String u = username.replace(" ", "_");
	                /*  Show MainForm  */
	                MainForm main = new MainForm();
	                main.initFrame(u, password, txtHost.getText(), Integer.parseInt(txtPort.getText()), "REGISTER");
	                //  check if were connected
	                if(main.isConnected()){
//	                    main.setLocationRelativeTo(null);
//	                    main.setVisible(true);
//	                    frame.setVisible(false);
	                	JOptionPane.showMessageDialog(frame, "Register successfully","Information",JOptionPane.INFORMATION_MESSAGE);
	                }
	                else {
	            	JOptionPane.showMessageDialog(frame, "Username has already been used","Error",JOptionPane.ERROR_MESSAGE);
	                }
		 }else {
	            JOptionPane.showMessageDialog(frame, "Incomplete Form.!", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	}
	
	private void connectToServerLogin(){
        if(txtHost.getText().length() > 0 && txtPort.getText().length() > 0 && txtUsername.getText().length() > 0){
            
                /*   Clean Username  */
                String username = txtUsername.getText();
                String password = String.valueOf(txtPassword.getPassword());
              
                String u = username.replace(" ", "_");
                /*  Show MainForm  */
                MainForm main = new MainForm();
                main.initFrame(u,password, txtHost.getText(), Integer.parseInt(txtPort.getText()),"LOGIN");
                //  check if were connected
                if(main.isConnected()){
//                    main.setLocationRelativeTo(null);
                   main.frame.setVisible(true);
                    frame.setVisible(false);
                }else {
            	JOptionPane.showMessageDialog(frame, "Wrong at username or password!","Error",JOptionPane.ERROR_MESSAGE);
                }
        }else {
            JOptionPane.showMessageDialog(frame, "Incomplete Form.!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	
}
