package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;

public class ChatGroup {

	private JFrame frame;
	private JTextField textField;
	private JTextField txtMember;

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
	 */
	public ChatGroup() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 603, 403);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(0, 0, 422, 303);
		frame.getContentPane().add(textArea);
		
		textField = new JTextField();
		textField.setBounds(0, 321, 422, 32);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(459, 326, 89, 23);
		frame.getContentPane().add(btnSend);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(459, 32, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		txtMember = new JTextField();
		txtMember.setText("Add member");
		txtMember.setBounds(432, 74, 145, 20);
		frame.getContentPane().add(txtMember);
		txtMember.setColumns(10);
	}
}