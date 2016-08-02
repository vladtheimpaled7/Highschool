import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;

import java.awt.Color;

import javax.swing.JTextArea;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ClientWindow extends JFrame {

	private JPanel contentPane;
	private Client c;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow frame = new ClientWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientWindow(int port,String userName,String IP) {
		setTitle("Client");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 396, 516);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		
		JLabel lblConnectedIp = new JLabel("Connected IP: ");
		lblConnectedIp.setBounds(10, 22, 87, 14);
		contentPane.add(lblConnectedIp);
		
		JLabel label = new JLabel(IP);
		label.setBounds(99, 13, 105, 32);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("Port: ");
		label_1.setBounds(294, 11, 46, 32);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel(String.valueOf(port));
		label_2.setBounds(334, 11, 46, 32);
		contentPane.add(label_2);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 54, 342, 364);
		contentPane.add(scrollPane);
		
		JList list = new JList();
		list.setModel(new DefaultListModel());
		scrollPane.setViewportView(list);
		list.setForeground(Color.WHITE);
		list.setBackground(new Color(0, 128, 0));
		
		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		textArea.setBackground(Color.BLUE);
		textArea.setBounds(10, 428, 272, 48);
		contentPane.add(textArea);
		
		JButton button = new JButton("Send");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					c.sendMessage(new Texts(userName,textArea.getText()));
					textArea.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(contentPane, "Error sending message", "could not send Message", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		button.setForeground(Color.WHITE);
		button.setBackground(Color.BLUE);
		button.setBounds(294, 426, 72, 50);
		contentPane.add(button);
		
		try {
			c = new Client(IP, port, userName);
		}catch(SocketTimeoutException e){
			JOptionPane.showMessageDialog(contentPane, "Could not find host", "Host not found", JOptionPane.ERROR_MESSAGE);
			Start s =new Start();
			s.setVisible(true);
			setVisible(false);
			contentPane.setVisible(false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//System.out.println("Starting error handle");
			JOptionPane.showMessageDialog(contentPane, "Could not find host","Host not found",JOptionPane.ERROR_MESSAGE);
			Start s =new Start();
			s.setVisible(true);
			dispose();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("Starting error handle");
			JOptionPane.showMessageDialog(contentPane, "Unknown Error","Error",JOptionPane.ERROR_MESSAGE);
			Start s = new Start();
			s.setVisible(true);
			dispose();
		}
		Thread readMessages = new Thread(new Runnable(){

			@Override
			public void run() {
				for(;;){
				try {
					ArrayList<Message> newMes = c.readMessage();
					System.out.println("messages " + newMes);
					for(Message m : newMes){
						((DefaultListModel)list.getModel()).addElement(m.toString());
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(java.io.EOFException e){
					System.out.println("Socket error");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					c.close();
					System.out.println("Closed");
					try {
						c = new Client(IP, port, userName);
					} catch(Exception e2){
						JOptionPane.showMessageDialog(contentPane, e.getStackTrace().toString(),"Lost connection to server",JOptionPane.ERROR_MESSAGE);
						System.exit(1);
						e.printStackTrace();
					}
					
					
				}
				}
				
			}
			
		});
		readMessages.start();
	}
}
