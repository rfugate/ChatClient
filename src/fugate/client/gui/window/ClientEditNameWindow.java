package fugate.client.gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import fugate.client.gui.ClientGUI;
import fugate.client.gui.ClientPreferences;

public class ClientEditNameWindow {
	
	JDialog editNameWindow;
	
	private JPanel mainPanel	= new JPanel(new BorderLayout());
	private JPanel leftPanel	= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JPanel rightPanel	= new JPanel(new FlowLayout());
	private JPanel textPanel	= new JPanel(new BorderLayout());
	
	private JTextField userNameField;
	private JTextField userPasswordField;
	
	private JButton addButton;
	private JButton deleteButton;
	private JButton saveButton;
	private JButton cancelButton;
	
	private DefaultTableModel tableModel;
	private JTable userNameTable;
	
	private static LinkedHashMap<String, String> savedUserNames;
	
	private int buttonLength;
	private int buttonHeight;

	public ClientEditNameWindow(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		buttonLength = ClientGUI.getButtonLength();
		buttonHeight = ClientGUI.getButtonHeight();
		
		editNameWindow = new JDialog(ClientGUI.getFrame(), "Create/Edit User Name");
		
		createPanels();
		createUIObjects();
		
		editNameWindow.setResizable(false);
		
		editNameWindow.pack();
		editNameWindow.setVisible(true);
		editNameWindow.setLocationRelativeTo(ClientGUI.getFrame());
	}
	
	private void createPanels(){
		mainPanel.setPreferredSize(new Dimension(350,125));
			leftPanel.setPreferredSize(new Dimension(200,125));
				textPanel.setPreferredSize(new Dimension(190,115));
			rightPanel.setPreferredSize(new Dimension(150,125));
		
		editNameWindow.add(mainPanel);
			mainPanel.add(leftPanel, BorderLayout.WEST);
				leftPanel.add(textPanel);
			mainPanel.add(rightPanel, BorderLayout.CENTER);
	}
	
	private void createUIObjects(){
		
		userNameField		= createTextField("User Name", "Name to be displayed in channels");
		userPasswordField	= createTextField("Password (Optional)", "Supplied by the Server for Admin rights");
		
		addButton		= createButton("Add");
		deleteButton	= createButton("Delete");
		saveButton		= createButton("Save");
		cancelButton	= createButton("Cancel");
		
		userNameTable = createTable();
	}
	
	private JButton createButton(String name){
		JButton button = new JButton(name);
		button.setPreferredSize(new Dimension(65,25));
		button.addActionListener(new ButtonAction());
		rightPanel.add(button);
		return button;
	}
	
	private JTextField createTextField(String hintText, String toolTip){
		JTextField textField = new HintTextField(hintText);
		textField.setPreferredSize(new Dimension(140,25));
		textField.addActionListener(new ButtonAction());
		textField.setToolTipText(toolTip);
		rightPanel.add(textField);
		return textField;
	}
	
	private JTable createTable(){
		tableModel = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		tableModel.addColumn("User Name");
		tableModel.addColumn("Password");
		
		savedUserNames = new LinkedHashMap<String, String>();
		try {
			savedUserNames = (LinkedHashMap<String, String>) convertByte(ClientPreferences.prefs.getByteArray(ClientPreferences.getUserNameList(), convertObject(savedUserNames)));
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		for(Entry<String, String> entry : savedUserNames.entrySet()){
			tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
		}
		
		final JTable table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (table.getSelectedRow() > -1) {
					userNameField.requestFocus();
		            userNameField.setText((String) table.getModel().getValueAt(table.getSelectedRow(), 0));
		            userPasswordField.requestFocus();
		            userPasswordField.setText((String) table.getModel().getValueAt(table.getSelectedRow(), 1));
		            addButton.setText("Edit");
		        }
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		textPanel.add(scrollPane);
		return table;
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
	
	private void resetDisplay(){
		addButton.setText("Add");
		userNameTable.clearSelection();
		userNameField.requestFocus();
		userNameField.setText("");
		userPasswordField.requestFocus();
		userPasswordField.setText("");
		editNameWindow.requestFocus();
	}
	
	private class ButtonAction extends AbstractAction{

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if((src == addButton && addButton.getText().equals("Add")) || src == userNameField || src == userPasswordField){
				if(!userNameField.getText().isEmpty() && !userNameField.getText().equals("User Name")){
					ArrayList<String> data = new ArrayList<String>();
					if(userPasswordField.getText().equals("Password (Optional)") || userPasswordField.getText().equals("")){
						data.add(userNameField.getText());
						data.add("N/A");
					} else {
						data.add(userNameField.getText());
						data.add(userPasswordField.getText());
					}
					
					tableModel.insertRow(tableModel.getRowCount(), new Vector<Object>(data));
					resetDisplay();
				}
			}
			else if((src == addButton && addButton.getText().equals("Edit")) || src == userNameField || src == userPasswordField){
				int selectedRow = userNameTable.getSelectedRow();
				if(selectedRow != -1){
					if(!(tableModel.getValueAt(selectedRow, 0).equals(userNameField.getText()))){
						if(userNameField.getText().equals("User Name")){
							tableModel.removeRow(selectedRow);
						} else {
							tableModel.setValueAt(userNameField.getText(), selectedRow, 0);
						}
					} else if(!(tableModel.getValueAt(selectedRow, 1).equals(userPasswordField.getText()))){
						if(userPasswordField.getText().equals("Password (Optional)")){
							tableModel.setValueAt("N/A", selectedRow, 1);
						} else {
							tableModel.setValueAt(userPasswordField.getText(), selectedRow, 1);
						}
					}
					resetDisplay();
				}
			}
			else if(src == deleteButton){
				int selectedRow = userNameTable.getSelectedRow();
				if(selectedRow != -1){
					tableModel.removeRow(selectedRow);
					resetDisplay();
				}
			}
			else if(src == saveButton){
				int rowCount = userNameTable.getRowCount();
				savedUserNames.clear();
				for(int i = 0; i < rowCount; i++){
					savedUserNames.put((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
				}
				
				try {
					ClientPreferences.prefs.putByteArray(ClientPreferences.getUserNameList(), convertObject(savedUserNames));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				ClientGUI.getUserNameCBox().removeAllItems();
				for(Entry<String, String> entry : savedUserNames.entrySet()){
					ClientGUI.getUserNameCBox().addItem(entry.getKey());
				}
				System.out.println(ClientGUI.getUserNameCBox().getModel().getSize());
				if(ClientGUI.getUserNameCBox().getModel().getSize() != 0){
					ClientGUI.getControlConnectButton().setEnabled(true);
				} else {
					ClientGUI.getControlConnectButton().setEnabled(false);
				}
				editNameWindow.dispose();
			}
			else if(src == cancelButton){
				editNameWindow.dispose();
			}
		}
	}
	
	class HintTextField extends JTextField implements FocusListener {

		  private final String hint;
		  private boolean showingHint;

		  public HintTextField(final String hint) {
		    super(hint);
		    this.hint = hint;
		    this.showingHint = true;
		    super.addFocusListener(this);
		  }

		  @Override
		  public void focusGained(FocusEvent e) {
		    if(this.getText().equals(hint)) {
		      super.setText("");
		      showingHint = false;
		    }
		  }
		  @Override
		  public void focusLost(FocusEvent e) {
		    if(this.getText().isEmpty()) {
		      super.setText(hint);
		      showingHint = true;
		    }
		  }
		}
}
