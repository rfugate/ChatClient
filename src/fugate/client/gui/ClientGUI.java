package fugate.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fugate.client.Client;
import fugate.client.ClientAudio;
import fugate.client.gui.window.ClientConnectWindow;
import fugate.client.gui.window.ClientEditNameWindow;
import fugate.client.gui.window.ClientSettingsWindow;

public class ClientGUI{

	Client client;
	ClientConnectWindow clientConnect;
	
	private static JFrame frame = new JFrame();
	
	// Main Panels
	private JPanel mainPanel			= new JPanel(new BorderLayout());
	private JPanel topPanel				= new JPanel(new BorderLayout());
	private JPanel centerPanel			= new JPanel(new BorderLayout());
	private JPanel bottomPanel			= new JPanel(new BorderLayout());
	
	// Top Panels
	private JPanel topLeftPanel			= new JPanel(new FlowLayout());
	private JPanel topRightPanel		= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel topUserNamePanel		= new JPanel(new BorderLayout());
	private JPanel topUserStatusPanel	= new JPanel(new BorderLayout());
	private JPanel topSoundOptionPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel topImageIconPanel	= new JPanel(new BorderLayout());
	
	// Center Panels
	private JPanel centerLeftPanel		= new JPanel(new FlowLayout()); //JTree Panel
	private JPanel centerRightPanel		= new JPanel(new FlowLayout(FlowLayout.LEFT));//Button Panel
	
	// Bottom Panels
	private JPanel bottomTopPanel		= new JPanel(new FlowLayout());
	private JPanel bottomBPanel			= new JPanel(new FlowLayout());
	private JPanel bottomBLeftPanel		= new JPanel();
	private JPanel bottomBRightPanel	= new JPanel();
	
	//Image Variables
	private static ImageIcon[] statusIcons;
	private static ImageIcon userAvailIcon;
	private static ImageIcon userBusyIcon;
	private static ImageIcon userAwayIcon;
	private static ImageIcon userBatmanIcon;
	private static ImageIcon channelOpenIcon;
	private static ImageIcon channelClosedIcon;
	private static ImageIcon rootNodeIcon;
	
	//Tree Variables
	private static JTree tree;
	private static DefaultTreeModel treeModel;
	private static DefaultMutableTreeNode rootNode;
	
	//Objects with ActionListeners
	private static JButton userEditNameButton;
	private static JButton userEditStatusButton;
	private static JButton connectButton;
	private static JButton disconnectButton;
	private static JButton sendButton;
	private static JTextField msgSend;
	private JButton settingsButton;
	private JButton aboutButton;
	private JButton closeButton;
	
	private static JComboBox<String> userNameCBox;
	private static JComboBox<String> userStatusCBox;
	
	private static JCheckBox muteSound;
	private static JCheckBox muteMicrophone;

	private static String[] statusCollection = new String[]{"Available", "Busy", "Away", "Batman"};
	private static String[] userNameCollection = new String[]{};
	
	private static LinkedHashMap<String, String> userNames;
	private static LinkedHashMap<String, String> availabilities;
	
	private static JEditorPane textArea1;
	
	//Message to send to Server
	private static String msgToSend;
	
	private static HashMap<String, String> userConnected = new HashMap<String, String>();
	
	private static boolean capturing = false;
	
	//Action Event
	//private static ActionClass actionEvent = new ActionClass();
	private static int buttonLength;
	private static int buttonHeight;

	public ClientGUI(){
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
		
		UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Color.white));
		UIManager.put("ComboBox.background", new ColorUIResource(Color.white));
		
		setFrame(new JFrame("Client"));
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getFrame().addKeyListener(new KeyAction());
		getFrame().addWindowListener(new MyWindowListener());
		
		if("Windows 7".equals(System.getProperty("os.name"))){
			System.out.println("Windows 7:");
			setWindowsVariables();
		}
		if("Mac OS X".equals(System.getProperty("os.name"))){
			System.out.println("OS X:");
			setMacVariables();
		}
		
		createPanels();
		createImages();
		createUIObjects();
		
		frame.setResizable(false);
		getFrame().pack();
		getFrame().setVisible(true);
		getFrame().setLocationRelativeTo(null);
		getFrame().requestFocus();
		
		System.out.println("ClientGUI HotKey: " + ClientPreferences.prefs.getInt(ClientPreferences.getHotkey(), 0));
	}
	
	public void setWindowsVariables(){
		setButtonLength(90);
		setButtonHeight(25);
	}
	
	public void setMacVariables(){
		setButtonLength(100);
		setButtonHeight(25);
	}
	
	private void createPanels(){
		
		mainPanel.setPreferredSize(new Dimension(300,500));
			topPanel.setPreferredSize(new Dimension(300,90));
				topLeftPanel.setPreferredSize(new Dimension(200,90));
					topUserNamePanel.setPreferredSize(new Dimension(130,25));
					topUserStatusPanel.setPreferredSize(new Dimension(130,25));
					topSoundOptionPanel.setPreferredSize(new Dimension(190,35));
				topRightPanel.setPreferredSize(new Dimension(100,90));
					topImageIconPanel.setPreferredSize(new Dimension(90,80));
			centerPanel.setPreferredSize(new Dimension(300,260));
				centerLeftPanel.setPreferredSize(new Dimension(200,230));
				centerRightPanel.setPreferredSize(new Dimension(100,230));
			bottomPanel.setPreferredSize(new Dimension(300,170));
				bottomTopPanel.setPreferredSize(new Dimension(300,130));
				bottomBPanel.setPreferredSize(new Dimension(300,40));
				
				topImageIconPanel.setBackground(Color.LIGHT_GRAY);
				
		getFrame().add(mainPanel);
			mainPanel.add(topPanel, BorderLayout.NORTH);
				topPanel.add(topLeftPanel, BorderLayout.WEST);	
				topPanel.add(topRightPanel, BorderLayout.EAST);
					topRightPanel.add(topImageIconPanel);
			mainPanel.add(centerPanel, BorderLayout.CENTER);
				centerPanel.add(centerLeftPanel, BorderLayout.WEST);
				centerPanel.add(centerRightPanel, BorderLayout.EAST);
			mainPanel.add(bottomPanel, BorderLayout.SOUTH);
				bottomPanel.add(bottomTopPanel, BorderLayout.NORTH);
				bottomPanel.add(bottomBPanel, BorderLayout.SOUTH);
	}
	
	@SuppressWarnings("unchecked")
	private void createUIObjects(){
		/**
		 * Set up Control Panel with Buttons
		 */
	
		connectButton	= createButton("Connect", 90, 25, false, centerRightPanel);
		disconnectButton	= createButton("Disconnect", 90, 25, false, centerRightPanel);
		settingsButton	= createButton("Settings", 90, 25, true, centerRightPanel);
		aboutButton		= createButton("About", 90, 25, true, centerRightPanel);
		closeButton		= createButton("Close", 90, 25, true, centerRightPanel);
		
		/**
		 * Set up User Name and Availability Panel
		 */
		
		topLeftPanel.add(topUserNamePanel);
		userEditNameButton		= createButton("Edit", 55, 25, true, topLeftPanel);
		
		topLeftPanel.add(topUserStatusPanel);
		userEditStatusButton	= createButton("Edit", 55, 25, false, topLeftPanel);
		
		availabilities	= createHashMap(statusCollection);
		userNames		= createHashMap(userNameCollection);
		
		userNameCBox 	= createComboBox(topUserNamePanel, userNames, ClientPreferences.getUserNameList());
		userStatusCBox	= createComboBox(topUserStatusPanel, availabilities, ClientPreferences.getUserAvailList());
		
		if(userNameCBox.getModel().getSize() != 0) connectButton.setEnabled(true);
		
		userStatusCBox.setRenderer(new AvailComboBoxRenderer());
		userNameCBox.setRenderer(new NameComboBoxRenderer());
		
		/**
		 * Set up Mute Options
		 */
		
		muteSound			= createCheckBox("Mute Sound", topLeftPanel, ClientPreferences.getMuteSound());
		muteMicrophone		= createCheckBox("Mute Microphone", topLeftPanel, ClientPreferences.getMuteMicrophone());
		
		/**
		 * Set up textPanel with Text Area
		 */
		
		setTextArea1(new JEditorPane());
		msgSend  = new JTextField();
		JScrollPane scrollPane = new JScrollPane(getTextArea1());
		Font font = new Font("Verdana", Font.BOLD, 10);
		
		getTextArea1().setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		getTextArea1().putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		getTextArea1().setEditable(false);
		getTextArea1().setFont(font);
		getTextArea1().setEnabled(false);
		//getTextArea1().setBackground(Color.white);
		getTextArea1().addHyperlinkListener(new HyperlinkListener(){
			public void hyperlinkUpdate(HyperlinkEvent e){
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					if(Desktop.isDesktopSupported()){
						try {
							System.out.println(e.getDescription());
							URL url = new URL(e.getDescription());
							Desktop.getDesktop().browse(url.toURI());
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(290,120));
		scrollPane.setOpaque(true);
		
		msgSend.setEnabled(false);
		
		msgSend.setPreferredSize(new Dimension(195,25));
		msgSend.addActionListener(new ButtonAction());
		
		bottomTopPanel.add(scrollPane);
		//bottomTopPanel.add(textMsgSndPanel);
		
		bottomBPanel.add(msgSend, BorderLayout.WEST);
		sendButton  = createButton("Send", 90, 25, false, bottomBPanel);
		
		/**
		 * Set up JTree to hold Voice Channels
		 */
		
		treeModel = ClientTree.getTreeModel();
		tree = new JTree(treeModel);
		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2 && !e.isConsumed()){
					if(!tree.isSelectionEmpty()){
						TreePath path = tree.getSelectionPath();
						try {
							ClientTree.removeFromChannel(userNameCBox.getSelectedItem().toString());
							ClientTree.moveToChannel(path, userNameCBox.getSelectedItem().toString());
							//new Thread(new Client.Sender("Moved: " + ))
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						expandAllNodes(ClientGUI.getTree(), 0, ClientGUI.getTree().getRowCount());
						new Thread(new Client.Sender(ClientTree.getTreeModel())).start();
						getFrame().requestFocus();
					}
					e.consume();
				}
			}
		});
		tree.setVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(190,230));
		centerLeftPanel.add(treeScrollPane);
	}
	
	private void createImages(){
		
		setUserAvailIcon(createImageIcon("/UserIconAvail.png"));
		setUserBusyIcon(createImageIcon("/UserIconBusy.png"));
		setUserAwayIcon(createImageIcon("/UserIconAway.png"));
		setUserBatmanIcon(createImageIcon("/UserIconBatman.png"));
		
		setChannelOpenIcon(createImageIcon("/ChannelOpen.png"));
		setChannelClosedIcon(createImageIcon("/ChannelClosed.png"));
		
		setRootNodeIcon(createImageIcon("/RootNode.png"));
	}
	
	private JButton createButton(String buttonTitle, int length, int height, Boolean isEnabled, JPanel panel){
		JButton button = new JButton(buttonTitle);
		button.setPreferredSize(new Dimension(length, height));
		button.addActionListener(new ButtonAction());
		button.setEnabled(isEnabled);
		panel.add(button);
		return button;
	}
	
	private JCheckBox createCheckBox(String checkBoxTitle, JPanel panel,String preference){
		JCheckBox checkBox = new JCheckBox(checkBoxTitle);
		ClientPreferences.prefs.getBoolean(preference, false);
		//Add ActionListener - Update Preference
		panel.add(checkBox);
		return checkBox;
	}
	
	private LinkedHashMap<String, String> createHashMap(String[] items){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if(items != null){
			for(String item : items){
				map.put(item, null);
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private JComboBox<String> createComboBox(JPanel panel, LinkedHashMap<String, String> items, String preferences){
		JComboBox<String> comboBox = new JComboBox<String>();
		try {
			items = (LinkedHashMap<String, String>) convertByte(ClientPreferences.prefs.getByteArray(preferences, convertObject(items)));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		for(Entry<String, String> entry : items.entrySet()){
			comboBox.addItem(entry.getKey());
		}
		comboBox.addActionListener(new ComboBoxAction());
		panel.add(comboBox);
		return comboBox;
	}
	
	public static void expandAllNodes(JTree tree, int startingIndex, int rowCount){
		for(int i = startingIndex; i < rowCount; i++){
			tree.expandRow(i);
		}
		
		if(tree.getRowCount() != rowCount){
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
	
	private ImageIcon createImageIcon(String path){
		java.net.URL imgURL = getClass().getResource(path);
		if(imgURL != null){
			ImageIcon imageIcon = new ImageIcon(imgURL);
			Image image = imageIcon.getImage();
			Image newImg = image.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImg);
			return imageIcon;
		} else {
			System.err.println("Couldn't find the file: " + path);
			return null;
		}
	}
	
	private static Object convertByte(byte raw[]) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bais = new ByteArrayInputStream(raw);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object object = ois.readObject();
		return object;
	}
	
	private static byte[] convertObject(Object object) throws IOException{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
		objectOut.writeObject(object);
		return byteOut.toByteArray();
	}
	
	private class ButtonAction extends AbstractAction{
		/* " ..Never use ActionListener, and to instead use Actions such as an AbstractAction
		 * It's almost always a bad idea to have your GUI class implement your listeners though as this breaks
		 * the Single Responsibility Principle and makes your code more difficult to maintain and extend.."
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == msgSend || src == sendButton){
				/*
				 * Action for when sending text messages - Accepts Enter key or Send button
				 */
				new Thread(new Client.Sender("Public: " + getUserNameCBox().getSelectedItem().toString() + " " + msgSend.getText())).start();
				String message = msgSend.getText();
				setMsgToSend(message);
				msgSend.setText("");
				getFrame().requestFocus();
			}
			else if(src == connectButton){
				/*
				 * User clicks on the connect button
				 */
				new ClientConnectWindow();
				getFrame().requestFocus();
			}
			else if(src == disconnectButton){
				try {
					Client.client.close();
					Client.halt();
					getControlConnectButton().setEnabled(true);
					getControlDisconnectButton().setEnabled(false);
					getUserNameCBox().setEnabled(true);
					getUserEditNameButton().setEnabled(true);
					getTextArea1().setEnabled(false);
					getTree().setVisible(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(src == settingsButton){
				new ClientSettingsWindow();
				getFrame().requestFocus();
			}
			else if(src == aboutButton){
				JDialog d = new JDialog(getFrame(), "About", true);
				d.setSize(new Dimension(200,200));
				d.setLocationRelativeTo(getFrame());
				d.setVisible(true);
				getFrame().requestFocus();
			}
			else if(src == closeButton){
				System.exit(0);
			}
			else if(src == userEditNameButton){
				new ClientEditNameWindow();
			}
		}
	}
	
	public class ComboBoxAction implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == userStatusCBox){
				ClientGUI.getTree().setCellRenderer(new ClientGUI.MyRenderer());
				new Thread(new Client.Sender("Status: " + getUserStatusCBox().getSelectedItem())).start();
			}
		}
	}
	
	public static class MyWindowListener implements WindowListener{

		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			try {
				if(ClientConnectWindow.client != null){
					System.out.println("Closing Client Socket.");
					Client.client.close();
					Client.halt();
					System.out.println("Closed Client Socket.");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
		
	}
	
	public static class MyRenderer extends DefaultTreeCellRenderer{
		
		private static final long serialVersionUID = 1L;

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus){
			super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			String s = node.getUserObject().toString();
			if(node.isRoot()){
				setIcon(ClientGUI.getRootNodeIcon());
			} else if (ClientGUI.getUserConnected().containsKey(s)){
				if(getUserConnected().get(s).equals("Available")){
					setIcon(getUserAvailIcon());
				} else if(getUserConnected().get(s).equals("Busy")){
					setIcon(getUserBusyIcon());
				} else if(getUserConnected().get(s).equals("Away")){
					setIcon(getUserAwayIcon());
				} else if(getUserConnected().get(s).equals("Batman")){
					setIcon(getUserBatmanIcon());
				}
			} else if (node.isLeaf()){
				setIcon(ClientGUI.getChannelClosedIcon());
			} else if (!node.isLeaf()){
				setIcon(ClientGUI.getChannelOpenIcon());
			}
			return this;
		}
	}
	
	public static class NameComboBoxRenderer extends JLabel implements ListCellRenderer {
		public NameComboBoxRenderer(){
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
	
	public static class AvailComboBoxRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		public AvailComboBoxRenderer(){
			setOpaque(true);
		}
		
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			if(value.equals("Available")){
				setIcon(getUserAvailIcon());
				setText(statusCollection[0]);
			} else if(value.equals("Busy")){
				setIcon(getUserBusyIcon());
				setText(statusCollection[1]);
			} else if(value.equals("Away")){
				setIcon(getUserAwayIcon());
				setText(statusCollection[2]);
			} else if(value.equals("Batman")){
				setIcon(getUserBatmanIcon());
				setText(statusCollection[3]);
			}
			
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
	
	public static class KeyAction implements KeyListener{

		public void keyPressed(KeyEvent key) {
			if(!ClientGUI.isCapturing()){
				if(key.getKeyCode() == ClientPreferences.prefs.getInt(ClientPreferences.getHotkey(), 0)){
					System.out.println("Hotkey Pressed.");
					ClientAudio.captureAudio();
				}
			}
		}

		public void keyReleased(KeyEvent key) {
			ClientGUI.setCapturing(false);
		}

		public void keyTyped(KeyEvent key) {
		}
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new ClientAudio();
				new ClientTree();
				new ClientPreferences();
				new ClientGUI();
			}
		});
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static void setFrame(JFrame frame) {
		ClientGUI.frame = frame;
	}

	public static int getButtonLength() {
		return buttonLength;
	}

	public static void setButtonLength(int buttonLength) {
		ClientGUI.buttonLength = buttonLength;
	}

	public static int getButtonHeight() {
		return buttonHeight;
	}

	public static void setButtonHeight(int buttonHeight) {
		ClientGUI.buttonHeight = buttonHeight;
	}

	public static String getMessage(){
		return msgToSend;
	}
	
	public static void setMsgToSend(String msgToSend){
		ClientGUI.msgToSend = msgToSend;
	}

	public static JEditorPane getTextArea1() {
		return textArea1;
	}

	public void setTextArea1(JEditorPane textArea1) {
		ClientGUI.textArea1 = textArea1;
	}
	
	public static JTextField getMsgSend() {
		return msgSend;
	}

	public void setMsgSend(JTextField msgSend) {
		this.msgSend = msgSend;
	}

	public static JComboBox<String> getUserNameCBox() {
		return userNameCBox;
	}

	public static void setUserNameCBox(JComboBox<String> userNameCBox) {
		ClientGUI.userNameCBox = userNameCBox;
	}

	public static JButton getControlConnectButton() {
		return connectButton;
	}

	public void setControlConnectButton(JButton controlConnectButton) {
		ClientGUI.connectButton = controlConnectButton;
	}

	public static HashMap<String, String> getUserConnected() {
		return userConnected;
	}

	public static void setUserConnected(HashMap<String, String> userConnected) {
		ClientGUI.userConnected = userConnected;
	}

	public static JButton getControlDisconnectButton() {
		return disconnectButton;
	}

	public void setControlDisconnectButton(JButton controlDisconnectButton) {
		ClientGUI.disconnectButton = controlDisconnectButton;
	}

	public static JTree getTree() {
		return tree;
	}

	public static void setTree(JTree tree) {
		ClientGUI.tree = tree;
	}

	public static DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public static void setTreeModel(DefaultTreeModel model) {
		ClientGUI.treeModel = model;
	}

	public static DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	public static void setRootNode(Object object) {
		ClientGUI.rootNode = (DefaultMutableTreeNode) object;
		ClientGUI.rootNode.setUserObject(object.toString());
	}

	public static ImageIcon getUserAvailIcon() {
		return userAvailIcon;
	}

	public void setUserAvailIcon(ImageIcon userAvailIcon) {
		this.userAvailIcon = userAvailIcon;
	}

	public static ImageIcon getUserBusyIcon() {
		return userBusyIcon;
	}

	public void setUserBusyIcon(ImageIcon userBusyIcon) {
		this.userBusyIcon = userBusyIcon;
	}

	public static ImageIcon getUserAwayIcon() {
		return userAwayIcon;
	}

	public void setUserAwayIcon(ImageIcon userAwayIcon) {
		this.userAwayIcon = userAwayIcon;
	}

	public static ImageIcon getUserBatmanIcon() {
		return userBatmanIcon;
	}

	public void setUserBatmanIcon(ImageIcon userBatmanIcon) {
		this.userBatmanIcon = userBatmanIcon;
	}

	public static ImageIcon getChannelOpenIcon() {
		return channelOpenIcon;
	}

	public void setChannelOpenIcon(ImageIcon channelOpenIcon) {
		this.channelOpenIcon = channelOpenIcon;
	}

	public static ImageIcon getChannelClosedIcon() {
		return channelClosedIcon;
	}

	public void setChannelClosedIcon(ImageIcon channelClosedIcon) {
		this.channelClosedIcon = channelClosedIcon;
	}

	public static ImageIcon getRootNodeIcon() {
		return rootNodeIcon;
	}

	public void setRootNodeIcon(ImageIcon rootNodeIcon) {
		this.rootNodeIcon = rootNodeIcon;
	}

	public static boolean isCapturing() {
		return capturing;
	}

	public static void setCapturing(boolean capturing) {
		ClientGUI.capturing = capturing;
	}

	public static JComboBox<String> getUserStatusCBox() {
		return userStatusCBox;
	}

	public static void setUserStatusCBox(JComboBox<String> userStatusCBox) {
		ClientGUI.userStatusCBox = userStatusCBox;
	}

	public static JButton getUserEditNameButton() {
		return userEditNameButton;
	}

	public static void setUserEditNameButton(JButton userEditNameButton) {
		ClientGUI.userEditNameButton = userEditNameButton;
	}

	public void setSendButton(JButton sendButton) {
		this.sendButton = sendButton;
	}
	
	public static JButton getSendButton(){
		return sendButton;
	}
}
