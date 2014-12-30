package fugate.client.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class ClientTree {
	
	private static JTree tree;
	private static DefaultTreeModel treeModel;
	private static DefaultMutableTreeNode mainNode;
	private static DefaultMutableTreeNode channelNode;
	private static DefaultMutableTreeNode userNode;
	
	static ImageIcon userAvailIcon;
	static ImageIcon userBusyIcon;
	static ImageIcon userAwayIcon;
	
	static ImageIcon channelOpenIcon;
	static ImageIcon channelClosedIcon;
	
	static ImageIcon rootNodeIcon;
	
	public ClientTree(){
		
		System.out.println("ClientTree loaded.");
		
		Integer timerInterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
		
		mainNode = new DefaultMutableTreeNode("Server:");
		treeModel = new DefaultTreeModel(mainNode);
		/* Pull Tree settings from Client Preferences. */
		/* Do we need to store Tree in preferences if Server cares about it? */
		/* It won't be loaded automatically and doesnt need to be saved. */
		//treeModel = (DefaultTreeModel)
		
		tree = new JTree(treeModel);
		//tree.addMouseListener(new MouseActionListener());
		
		userAvailIcon = createImageIcon("/UserIconAvail.png");
		userBusyIcon = createImageIcon("/UserIconBusy.png");
		userAwayIcon = createImageIcon("/UserIconAway.png");


		channelClosedIcon = createImageIcon("/ChannelClosed.png");
		channelOpenIcon = createImageIcon("/ChannelOpen.png");
		
		rootNodeIcon = createImageIcon("/RootNode.png");
	}
	
	private ImageIcon createImageIcon(String path){
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null){
			ImageIcon imageIcon = new ImageIcon(imgURL);
			Image image = imageIcon.getImage();
			Image newimg = image.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newimg);
			return imageIcon;
		} else {
			System.err.println("Couldn't find the file: " + path);
			return null;
		}
	}
	
	public static void moveToChannel(TreePath path, String userName) throws IOException{
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(userName);
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
		ClientTree.getTreeModel().insertNodeInto(child, parent, 0);
		ClientGUI.getTree().expandPath(new TreePath(parent.getPath()));
	}
	
	public static void removeFromChannel(String userName) throws IOException{
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) ClientTree.getTreeModel().getRoot();
		DefaultTreeModel model = ClientTree.getTreeModel();
		
		ClientTree.setTreeModel(deleteNode(rootNode, model, userName));
	}
	
	public static DefaultTreeModel deleteNode(DefaultMutableTreeNode node, DefaultTreeModel model, String userName){
		int childCount = node.getChildCount();
		
		for(int i = 0; i < childCount; i++){
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
			if(childNode.getChildCount() > 0){
				deleteNode(childNode, model, userName);
			} else {
				if(childNode.toString().equals(userName)){
					model.removeNodeFromParent(childNode);
					childCount--; i--;
				}
			}
		}
		return model;
	}
	
	public class MouseActionListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Ouch");
			if(e.getClickCount() == 2){
				System.out.println("Double Click");
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println("mousePressed.");
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	public static DefaultTreeModel getTreeModel(){
		return treeModel;
	}
	
	public static void setTreeModel(Object object) throws IOException{
		ClientTree.treeModel = (DefaultTreeModel) object;
		ClientGUI.getTree().setModel(ClientTree.treeModel);
		ClientGUI.getTree().setCellRenderer(new ClientGUI.MyRenderer());
	}

	public static ImageIcon getUserAvailIcon() {
		return userAvailIcon;
	}

	public static void setUserAvailIcon(ImageIcon userAvailIcon) {
		ClientTree.userAvailIcon = userAvailIcon;
	}

	public static ImageIcon getChannelOpenIcon() {
		return channelOpenIcon;
	}

	public static void setChannelOpenIcon(ImageIcon channelOpenIcon) {
		ClientTree.channelOpenIcon = channelOpenIcon;
	}

	public static ImageIcon getChannelClosedIcon() {
		return channelClosedIcon;
	}

	public static void setChannelClosedIcon(ImageIcon channelClosedIcon) {
		ClientTree.channelClosedIcon = channelClosedIcon;
	}

	public static ImageIcon getRootNodeIcon() {
		return rootNodeIcon;
	}

	public static void setRootNodeIcon(ImageIcon rootNodeIcon) {
		ClientTree.rootNodeIcon = rootNodeIcon;
	}
}
