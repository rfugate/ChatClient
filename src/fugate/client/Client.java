package fugate.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fugate.client.gui.ClientGUI;
import fugate.client.gui.ClientTree;


public class Client{
	
	public static Socket client;
	
	private static String userName;
	private static String serverAddress;
	private static String serverPassword;
	private static int serverPort;
	
	private static boolean dataAvailable = false;
	private static boolean stopped = false;
	public static boolean connected = false;
	
	private static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";
	private static final Object lock = new Object();
	
	public Client(String address, int port, String password, Object object){
		
		serverAddress	= address;
		serverPassword	= password;
		serverPort		= port;
		userName		= (String) object;
		
		try {
			client = new Socket(serverAddress, serverPort);
			Connected();
			new Thread(new Sender("User: " + ClientGUI.getUserNameCBox().getSelectedItem().toString())).start();
			new Thread(new Receiver()).start();
		} catch (IOException e) {
			Disconnected();
			System.out.println("Could not contact Server.");
		}
	}
	
	public static void Disconnected(){
		connected = false;
	}
	
	public static void Connected(){
		connected = true;
	}
	
	public static void halt(){
		synchronized(lock){
			stopped = true;
		}
	}
	
	public static void newMessage(){
		dataAvailable = true;
	}

	public static class Sender implements Runnable{
		
		private Object commandMessage = null;

		public Sender(Object object){
			commandMessage = object;
		}
		
		public String urlCheck(String part){
			Pattern p = Pattern.compile(URL_REGEX);
			Matcher m = p.matcher(part);
			String item = null;
			if(m.find()) item = "http://" + part;
			try{
				URL url = new URL(item);
				item = "<a href=\"" + url + "\">" + url + "</a> ";
				return item;
			} catch (MalformedURLException e){
				item = part + " ";
				return item;
			}
		}
		
		public void run() {
			try{
				OutputStream outToServer = client.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(outToServer);
				if(commandMessage.getClass().equals(String.class)){
					String message = (String) commandMessage;
					String parts[] = message.split("\\s+");
					if(parts[0].equals("User:")){
						out.writeObject(message);
					}
					else if(parts[0].equals("Status:")){
						out.writeObject(message);
					}
					else if(parts[0].equals("Moved:")){
						//out.writeObject(message);
					}
					else if(parts[0].equals("Private:")){
					}
					else if(parts[0].equals("Public:")){
						StringBuilder sb = new StringBuilder();
						sb.append(parts[0] + " " + parts[1] + ": ");
						for(int i = 2; i < parts.length; i++){
							sb.append(urlCheck(parts[i]));
						}
						out.writeObject(sb.toString());
					}
				} else if (commandMessage.getClass().equals(DefaultTreeModel.class)){
					out.writeObject(commandMessage);
				} else if (commandMessage.getClass().equals(Byte[].class)){
					out.writeObject(commandMessage);
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static class Receiver implements Runnable{
		HTMLEditorKit kit;
		HTMLDocument doc;
		
		InputStream inFromServer;
		ObjectInputStream in;
		
		public void run() {
			while(true){
				synchronized(lock){
					if(stopped) break;
				}
				try {
					kit = (HTMLEditorKit) ClientGUI.getTextArea1().getEditorKit();
					doc = (HTMLDocument) ClientGUI.getTextArea1().getDocument();
					
					inFromServer = client.getInputStream();
					in = new ObjectInputStream(inFromServer);
					Object o = in.readObject();
					
					if(o.getClass().equals(String.class)){
						/* Received Text Object from Server */
						
						String message = (String) o;
						String[] parts = message.split("\\s+");
						kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
					} else if(o.getClass().equals(HashMap.class)){
						HashMap<String, String> s = (HashMap<String, String>) o;
						ClientGUI.setUserConnected(s);
						ClientGUI.getTree().setCellRenderer(new ClientGUI.MyRenderer());
					}
					else if(o.getClass().equals(JTree.class)){
						/* Received Tree Object from Server */
						JTree tree = (JTree) o;
						ClientGUI.setTree(tree);
						ClientGUI.getTree().setVisible(true);
					}
					else if(o.getClass().equals(DefaultTreeModel.class)){
						/* Received Tree Model Object from Server */
						DefaultTreeModel treeModel = (DefaultTreeModel) o;
						ClientTree.setTreeModel(treeModel);
						ClientGUI.getTree().setVisible(true);
						ClientGUI.expandAllNodes(ClientGUI.getTree(), 0, ClientGUI.getTree().getRowCount());
					}
					else if(o.getClass().equals(Byte[].class)){
						Byte[] voice = (Byte[]) o;
						ClientAudio.playAudio(voice);
					}
					//Thread.yield();
				} catch (IOException | BadLocationException | ClassNotFoundException e) {
					try {
						//e.printStackTrace();
						System.err.println(e);
						//client.shutdownInput();
						kit.insertHTML(doc, doc.getLength(), "***DISCONNECTED***", 0, 0, null);
						ClientGUI.getTextArea1().setEnabled(false);
						ClientGUI.getMsgSend().setEnabled(false);
						ClientGUI.getControlConnectButton().setEnabled(true);
						ClientGUI.getControlDisconnectButton().setEnabled(false);
						ClientGUI.getTree().setVisible(false);
					} catch (IOException | BadLocationException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
			System.out.println("Exiting Client Receiver Thread.");
			stopped = false;
		}
	}
}