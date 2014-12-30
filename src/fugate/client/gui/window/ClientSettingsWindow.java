package fugate.client.gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import fugate.client.gui.ClientGUI;
import fugate.client.gui.ClientPreferences;

public class ClientSettingsWindow {

	private JDialog settingsWindow;
	
	private JPanel mainPanel					= new JPanel(new FlowLayout());
	private JPanel topPanel						= new JPanel(new BorderLayout());
	private JPanel bottomPanel					= new JPanel(new BorderLayout());
	private JPanel devicePanel					= new JPanel(new BorderLayout());
	private JPanel outputDevicePanel			= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel outputDeviceLabelPanel		= new JPanel(new BorderLayout());
	private JPanel outputDeviceComboBoxPanel	= new JPanel(new BorderLayout());
	private JPanel inputDevicePanel				= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel inputDeviceLabelPanel		= new JPanel(new BorderLayout());
	private JPanel inputDeviceComboBoxPanel		= new JPanel(new BorderLayout());
	private JPanel systemSoundPanel				= new JPanel(new BorderLayout());
	private JPanel playbackPanel				= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel recordingPanel				= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel hotkeyPanel					= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel placeholderPanel				= new JPanel();
	private JPanel placeholderPanel1			= new JPanel();
	private JPanel bottomRightPanel				= new JPanel(new BorderLayout());
	private JPanel miscPanel					= new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel buttonPanel					= new JPanel(new FlowLayout(FlowLayout.RIGHT));

	private JButton saveButton;
	private JButton cancelButton;
	private JButton playbackButton;
	private JButton recordingButton;
	
	private JCheckBox logMessagesCheckBox;
	private JCheckBox minimizeSystemTrayCheckBox;
	private JCheckBox playNotificationCheckBox;
	private JCheckBox pushToTalkCheckBox;
	
	private JLabel outputDeviceLabel;
	private JLabel inputDeviceLabel;
	private JLabel hotkeyLabel;
	private JLabel systemSoundControlLabel;
	private JLabel amplifiersLabel;
	
	private JTextField hotkeyTextField;
	
	private static JComboBox<String> outputDeviceComboBox;
	private JComboBox<String> inputDeviceComboBox;
	
	private int buttonLength;
	private int buttonHeight;
	
	private boolean hotkeyEnabled = false;
	
	private static int hotkey;
	private static String hotkeyName;
	
	public ClientSettingsWindow(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex){
			System.out.println(ex);
		}
		
		settingsWindow = new JDialog(ClientGUI.getFrame(), "Client Settings");
		
		buttonLength = ClientGUI.getButtonLength();
		buttonHeight = ClientGUI.getButtonHeight();
		
		createPanels();
		createUIObjects();
		
		settingsWindow.setResizable(false);
		settingsWindow.pack();
		settingsWindow.setVisible(true);
		settingsWindow.setLocationRelativeTo(ClientGUI.getFrame());
	}
	
	private void createPanels(){
		mainPanel.setPreferredSize(new Dimension(400,400));
		topPanel.setPreferredSize(new Dimension(395,195));
			devicePanel.setPreferredSize(new Dimension(198,195));
				outputDevicePanel.setPreferredSize(new Dimension(195,65));
					outputDeviceLabelPanel.setPreferredSize(new Dimension(175,25));
					outputDeviceComboBoxPanel.setPreferredSize(new Dimension(175,25));
				inputDevicePanel.setPreferredSize(new Dimension(195,70));
					inputDeviceLabelPanel.setPreferredSize(new Dimension(175,25));
					inputDeviceComboBoxPanel.setPreferredSize(new Dimension(175,25));
			systemSoundPanel.setPreferredSize(new Dimension(197,195));
				playbackPanel.setPreferredSize(new Dimension(194,65));
					placeholderPanel.setPreferredSize(new Dimension(174,25));
				recordingPanel.setPreferredSize(new Dimension(194,65));
					placeholderPanel1.setPreferredSize(new Dimension(174,25));
		bottomPanel.setPreferredSize(new Dimension(395,195));
			hotkeyPanel.setPreferredSize(new Dimension(198,160));
			bottomRightPanel.setPreferredSize(new Dimension(197,195));
				miscPanel.setPreferredSize(new Dimension(197,160));
				buttonPanel.setPreferredSize(new Dimension(197,35));
			
		devicePanel.setBorder(BorderFactory.createTitledBorder("Select Device"));
		systemSoundPanel.setBorder(BorderFactory.createTitledBorder("System Sound Control Panels"));
		miscPanel.setBorder(BorderFactory.createTitledBorder("Client Options"));
	
		settingsWindow.add(mainPanel);
		mainPanel.add(topPanel);
			topPanel.add(devicePanel, BorderLayout.WEST);
				devicePanel.add(outputDevicePanel, BorderLayout.NORTH);
					outputDevicePanel.add(outputDeviceLabelPanel);
					outputDevicePanel.add(outputDeviceComboBoxPanel);
				devicePanel.add(inputDevicePanel, BorderLayout.CENTER);
					inputDevicePanel.add(inputDeviceLabelPanel);
					inputDevicePanel.add(inputDeviceComboBoxPanel);
			topPanel.add(systemSoundPanel, BorderLayout.EAST);
				systemSoundPanel.add(playbackPanel, BorderLayout.NORTH);
					playbackPanel.add(placeholderPanel);
				systemSoundPanel.add(recordingPanel, BorderLayout.CENTER);
					recordingPanel.add(placeholderPanel1);
		mainPanel.add(bottomPanel);
			bottomPanel.add(miscPanel, BorderLayout.WEST);
			bottomPanel.add(bottomRightPanel, BorderLayout.EAST);
				bottomRightPanel.add(hotkeyPanel, BorderLayout.NORTH);
				bottomRightPanel.add(buttonPanel, BorderLayout.SOUTH);			
	}
	
	private void createUIObjects(){
		saveButton		= createButton("Save", buttonLength, buttonHeight);
		cancelButton	= createButton("Cancel", buttonLength, buttonHeight);
		
		saveButton.addActionListener(new ButtonAction());
		cancelButton.addActionListener(new ButtonAction());
		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		
		logMessagesCheckBox = new JCheckBox("Log Messages");
		logMessagesCheckBox.setSelected(ClientPreferences.getPrefs().getBoolean(ClientPreferences.getLogMessages(), false));
		minimizeSystemTrayCheckBox = new JCheckBox("Minimize to System Tray");
		minimizeSystemTrayCheckBox.setSelected(ClientPreferences.getPrefs().getBoolean(ClientPreferences.getMinimizeSystemTray(), false));
		playNotificationCheckBox = new JCheckBox("Play PTT Notification");
		playNotificationCheckBox.setSelected(ClientPreferences.getPrefs().getBoolean(ClientPreferences.getPttNotification(), false));
		pushToTalkCheckBox = new JCheckBox("Use Push To Talk Hotkey");
		pushToTalkCheckBox.setSelected(ClientPreferences.getPrefs().getBoolean(ClientPreferences.getPttHotKey(), false));
		
		logMessagesCheckBox.addActionListener(new CheckBoxAction());
		minimizeSystemTrayCheckBox.addActionListener(new CheckBoxAction());
		playNotificationCheckBox.addActionListener(new CheckBoxAction());
		pushToTalkCheckBox.addActionListener(new CheckBoxAction());
		
		miscPanel.add(logMessagesCheckBox);
		miscPanel.add(minimizeSystemTrayCheckBox);
		miscPanel.add(playNotificationCheckBox);
		miscPanel.add(pushToTalkCheckBox);
		miscPanel.setBorder(BorderFactory.createTitledBorder("Client Options"));
		
		outputDeviceLabel = new JLabel("Output Device");
		outputDeviceComboBox = new JComboBox<String>();
		outputDeviceComboBox.setRenderer(new MyComboBoxRenderer());
		//Set Preference
		outputDeviceLabelPanel.add(outputDeviceLabel);
		outputDeviceComboBoxPanel.add(outputDeviceComboBox);
	
		Mixer.Info[] info = AudioSystem.getMixerInfo();
		for(Mixer.Info outputDevice : info){
			if(outputDevice.getName().equals("Primary Sound Capture Driver")) break;
			if(outputDevice.getName().equals("Primary Sound Driver")) continue;
			outputDeviceComboBox.addItem(outputDevice.getName());
		}

		inputDeviceLabel = new JLabel("Input Device");
		inputDeviceComboBox = new JComboBox<String>();
		inputDeviceComboBox.setRenderer(new MyComboBoxRenderer());
		//Set Preference
		inputDeviceLabelPanel.add(inputDeviceLabel);
		inputDeviceComboBoxPanel.add(inputDeviceComboBox);
		
		
		for(Mixer.Info inputDevice : info){
			if(inputDevice.getName().contains("Microphone") && inputDevice.getDescription().equals("Direct Audio Device: DirectSound Capture")){
				inputDeviceComboBox.addItem(inputDevice.getName());
			}
		}
		
		hotkeyLabel = new JLabel("Hotkey");
		hotkeyLabel.setPreferredSize(new Dimension(175,25));
		hotkeyTextField = new JTextField();
		hotkeyTextField.setEnabled(ClientPreferences.getPrefs().getBoolean(ClientPreferences.getPttHotKey(), false));
		hotkeyTextField.addKeyListener(new TextFieldAction());
		hotkeyTextField.setText(ClientPreferences.prefs.get(ClientPreferences.getHotkeyName(), ""));
		hotkeyTextField.setPreferredSize(new Dimension(165,25));
		hotkeyPanel.add(hotkeyLabel);
		hotkeyPanel.add(hotkeyTextField);
		
		playbackButton = createButton("Playback (Output)", 174, buttonHeight);
		recordingButton = createButton("Recording (Input)", 174, buttonHeight);
		
		playbackButton.addActionListener(new ButtonAction());
		recordingButton.addActionListener(new ButtonAction());
		
		playbackPanel.add(playbackButton);
		recordingPanel.add(recordingButton);
	}
	
	private JButton createButton(String text, int w, int h){
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(w,h));
		button.setFont(new Font("Dialog", Font.PLAIN, 12));
		return button;
	}
	
	public class ButtonAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == playbackButton){
				if("Windows 7".equals(System.getProperty("os.name"))){
					try {
						Runtime.getRuntime().exec("control.exe mmsys.cpl,,0");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else if(src == recordingButton){
				if("Windows 7".equals(System.getProperty("os.name"))){
					try {
						Runtime.getRuntime().exec("control.exe mmsys.cpl,,1");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else if(src == saveButton){
				ClientPreferences.getPrefs().putBoolean(ClientPreferences.getLogMessages(), logMessagesCheckBox.isSelected());
				ClientPreferences.getPrefs().putBoolean(ClientPreferences.getMinimizeSystemTray(), minimizeSystemTrayCheckBox.isSelected());
				ClientPreferences.getPrefs().putBoolean(ClientPreferences.getPttNotification(), playNotificationCheckBox.isSelected());
				ClientPreferences.getPrefs().putBoolean(ClientPreferences.getPttHotKey(), pushToTalkCheckBox.isSelected());
				ClientPreferences.getPrefs().putInt(ClientPreferences.getHotkey(), ClientSettingsWindow.getHotkey());
				ClientPreferences.getPrefs().put(ClientPreferences.getHotkeyName(), ClientSettingsWindow.getHotkeyName());
				settingsWindow.dispose();
			} else if(src == cancelButton){
				settingsWindow.dispose();
			}
		}
	}
	
	public class CheckBoxAction implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == logMessagesCheckBox){
				AbstractButton abstractButton = (AbstractButton) src;
				if(abstractButton.getModel().isSelected()){
					//Set Preference
				} else {
					//Set Preference
				}
			} else if (src == minimizeSystemTrayCheckBox){
				AbstractButton abstractButton = (AbstractButton) src;
				if(abstractButton.getModel().isSelected()){
					//Set Preference
				} else {
					//Set Preference
				}
			} else if (src == playNotificationCheckBox){
				AbstractButton abstractButton = (AbstractButton) src;
				if(abstractButton.getModel().isSelected()){
					//Set Preference
				} else {
					//Set Preference
				}
			} else if (src == pushToTalkCheckBox){
				AbstractButton abstractButton = (AbstractButton) src;
				hotkeyEnabled = abstractButton.getModel().isSelected();
				if(hotkeyEnabled){
					//Set Preference
				} else {
					//Set Preference
				}
				hotkeyTextField.setEnabled(hotkeyEnabled);
			}
		}
	}
	
	public class TextFieldAction implements KeyListener{

		public void keyPressed(KeyEvent e) {
			hotkeyTextField.setText("");
		}

		public void keyReleased(KeyEvent e) {
			hotkeyTextField.setText("HotKey: " + KeyEvent.getKeyText(e.getKeyCode()));
			ClientSettingsWindow.setHotkeyName("HotKey: " + KeyEvent.getKeyText(e.getKeyCode()));
			ClientSettingsWindow.setHotkey(e.getKeyCode());
		}

		public void keyTyped(KeyEvent e) {
		}
	}
	
	public static class MyComboBoxRenderer extends JLabel implements ListCellRenderer {
		public MyComboBoxRenderer(){
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			setText((String) value);
			if(isSelected){
				setBackground(new Color(51, 153, 255));
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}
	}

	public static int getHotkey() {
		return hotkey;
	}

	public static void setHotkey(int hotkey) {
		ClientSettingsWindow.hotkey = hotkey;
	}

	public static JComboBox<String> getOutputDeviceComboBox() {
		return outputDeviceComboBox;
	}

	public void setOutputDeviceComboBox(JComboBox<String> outputDeviceComboBox) {
		this.outputDeviceComboBox = outputDeviceComboBox;
	}

	public static String getHotkeyName() {
		return hotkeyName;
	}

	public static void setHotkeyName(String hotkeyName) {
		ClientSettingsWindow.hotkeyName = hotkeyName;
	}
}
