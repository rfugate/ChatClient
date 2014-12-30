package fugate.client.gui.window;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fugate.client.Client;
import fugate.client.gui.ClientGUI;
import fugate.client.gui.ClientPreferences;

public class ClientConnectWindow {
	
	JDialog connectWindow;
	ClientGUI clientGUI;
	public static Client client;
	
	private JPanel mainPanel	= new JPanel(new FlowLayout());
	private JPanel labelPanel	= new JPanel();
	private JPanel textBoxPanel	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	private JTextField hostNameTextBox;
	private JTextField portNumberTextBox;
	private JTextField passwordTextBox;
	
	private JButton clearButton;
	private JButton connectButton;
	
	private int buttonLength;
	private int buttonHeight;
	
	public static boolean connected = false;

	public ClientConnectWindow(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			System.out.println(ex);
		}
		
		buttonLength = ClientGUI.getButtonLength();
		buttonHeight = ClientGUI.getButtonHeight();
		
		connectWindow = new JDialog(ClientGUI.getFrame(),"Connection Editor");
		
		createPanels();
		createUIObjects();
		
		connectWindow.pack();
		connectWindow.setVisible(true);
		connectWindow.setResizable(false);
		connectWindow.setLocationRelativeTo(ClientGUI.getFrame());
	}
	
	public void createPanels(){
		mainPanel.setPreferredSize(new Dimension(350,125));
		labelPanel.setPreferredSize(new Dimension(125,125));
		textBoxPanel.setPreferredSize(new Dimension(215,125));
		
		connectWindow.add(mainPanel);
		mainPanel.add(labelPanel);
		mainPanel.add(textBoxPanel);
	}
	
	public void createUIObjects(){
		createLabel("Host Name: ");
		createLabel("Port Number: ");
		createLabel("Password: ");
		
		hostNameTextBox		= createTextBox();
		portNumberTextBox	= createTextBox();
		passwordTextBox		= createTextBox();
		
		hostNameTextBox.setText(ClientPreferences.getPrefs().get(ClientPreferences.getHostName(), ""));
		portNumberTextBox.setText(ClientPreferences.getPrefs().get(ClientPreferences.getPortNumber(), ""));
		passwordTextBox.setText(ClientPreferences.getPrefs().get(ClientPreferences.getPassword(), ""));
		
		clearButton = createButton("Clear");
		connectButton = createButton("Connect");
		
		clearButton.addActionListener(new ButtonAction());
		connectButton.addActionListener(new ButtonAction());
	}
	
	public void createLabel(String text){
		JLabel label = new JLabel(text);
		Font font = new Font("Verdana", Font.BOLD, 10);
		label.setFont(font);
		label.setPreferredSize(new Dimension(115,25));
		labelPanel.add(label);
	}
	
	public JTextField createTextBox(){
		JTextField textBox = new JTextField();
		textBox.setPreferredSize(new Dimension(200,25));
		textBoxPanel.add(textBox);
		return textBox;
	}
	
	public JButton createButton(String text){
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(98,25));
		textBoxPanel.add(button);
		return button;
	}
	
	public static void isConnected(){
		connected = true;
	}
	
	private class ButtonAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("static-access")
		@Override
		public void actionPerformed(ActionEvent e){
			Object src = e.getSource();
			if (src == clearButton){
				hostNameTextBox.setText("");
				portNumberTextBox.setText("");
				passwordTextBox.setText("");
			}
			else if(src == connectButton){
				try {
					client = new Client(hostNameTextBox.getText(),Integer.parseInt(portNumberTextBox.getText()),passwordTextBox.getText(), ClientGUI.getUserNameCBox().getSelectedItem());
					if(!client.connected){
						client = null;
					} else {
						ClientGUI.getTextArea1().setText("***CONNECTED***");
						ClientGUI.getTextArea1().setEnabled(true);
						ClientGUI.getControlConnectButton().setEnabled(false);
						ClientGUI.getControlDisconnectButton().setEnabled(true);
						ClientGUI.getUserNameCBox().setEnabled(false);
						ClientGUI.getUserEditNameButton().setEnabled(false);
						ClientGUI.getMsgSend().setEnabled(true);
						ClientGUI.getSendButton().setEnabled(true);
						ClientPreferences.getPrefs().put(ClientPreferences.getHostName(), hostNameTextBox.getText());
						ClientPreferences.getPrefs().put(ClientPreferences.getPortNumber(), portNumberTextBox.getText());
						ClientPreferences.getPrefs().put(ClientPreferences.getPassword(), passwordTextBox.getText());
					}
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				connectWindow.dispose();
			}
		}
	}
}
