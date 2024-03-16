/**	VEGA - a strategy game
    Copyright (C) 1989-2024 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package vega;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import common.CommonUtils;
import common.Game;
import common.VegaResources;
import commonServer.ResponseMessageChangeUser;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import spielwitz.biDiServer.Client;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.PayloadResponseMessageChangeUser;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.Tuple;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IListListener;
import uiBaseControls.ITextFieldListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.ListItem;
import uiBaseControls.Panel;
import uiBaseControls.PanelWithInsets;
import uiBaseControls.TabbedPane;
import uiBaseControls.TextField;

@SuppressWarnings("serial")
class ServerCredentialsJDialog extends Dialog implements IButtonListener
{
	private TabbedPane tabpane;
	
	private Button butOk;
	private Button butClose;
	
	private UsersPanel panUsers;
	
	private ServerCredentials serverCredentials;
	
	private static String password = "1234";
	private static String lastSelectedDirectory;
	
	public boolean ok;
	
	ServerCredentialsJDialog(
			Vega parent,
			ServerCredentials credentials)
	{
		super(parent, "Server-Zugangsdaten", new BorderLayout());
		
		this.serverCredentials = (ServerCredentials) CommonUtils.klon(credentials);
		
		this.tabpane = new TabbedPane();
		
		this.panUsers = new UsersPanel(this);
		this.tabpane.addTab("Users", this.panUsers);
				
		this.addToInnerPanel(tabpane, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butClose = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butClose);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
				
//		if (this.clientConfigAdmin == null)
//		{
//			tabpane.setSelectedComponent(panAuthOuter);
//			tabpane.setEnabledAt(0, false);
//			tabpane.setEnabledAt(1, false);
//		}
		
		this.pack();
		this.setLocationRelativeTo(parent);	
	}
	
	ServerCredentials getServerCredentials()
	{
		return serverCredentials;
	}
	
	protected void close()
	{
		super.close();
	}

	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butClose)
		{
			this.close();
		}
		else if (source == this.butOk)
		{
			this.ok = true;
			super.close();
		}
	}
	
	private class UsersPanel extends Panel implements IButtonListener, IListListener, ActionListener
	{
		private ArrayList<ListItem> listUsersModel;
		private List listUsers;
		private Button butAdd;
		private Button butDelete;
		private CredentialsPanel panCredentials;
		
		private JPopupMenu popupMenu;
		private JMenuItem popupMenuItemUser;
		private JMenuItem popupMenuItemAdmin;
		
		private Dialog parent;
		
		private UsersPanel(Dialog parent)
		{
			super(new BorderLayout());
			
			this.parent = parent;
			this.popupMenu = new JPopupMenu();
			
			this.popupMenuItemUser = new JMenuItem ("Kopieren & Einfügen");
		    this.popupMenuItemUser.addActionListener(this);
		    popupMenu.add (this.popupMenuItemUser);
		    
		    this.popupMenuItemAdmin = new JMenuItem ("Admin-Zugangsdaten aus Datei importieren");
		    this.popupMenuItemAdmin.addActionListener(this);
		    popupMenu.add (this.popupMenuItemAdmin);
			
			PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 10));
			
			this.createListUsersModel();
			this.listUsers = new List(this, this.listUsersModel);
			this.listUsers.setPreferredSize(new Dimension(300, 300));
			panUsersList.addToInnerPanel(this.listUsers, BorderLayout.CENTER);
			
			Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
			
			this.butAdd = new Button("+", this);
			this.butAdd.setToolTipText("User hinzufügen");
			
			panUsersListButtons.add(this.butAdd);
			
			this.butDelete = new Button("-", this);
			this.butDelete.setToolTipText("User löschen");
			panUsersListButtons.add(this.butDelete);
			
			panUsersList.addToInnerPanel(panUsersListButtons, BorderLayout.SOUTH);
			
			this.add(panUsersList, BorderLayout.WEST);
			
			PanelWithInsets panCredentials = new PanelWithInsets(new BorderLayout(10, 10));
			this.panCredentials = new CredentialsPanel(parent, null);
			panCredentials.addToInnerPanel(this.panCredentials, BorderLayout.NORTH);
			this.add(panCredentials, BorderLayout.CENTER);
			
			int index = this.getListIndexByCredentialsKey(serverCredentials.userCredentialsSelected);
			
			if (index >= 0)
			{
				this.listUsers.setSelectedIndex(index);
			}
			else if (this.listUsersModel.size() > 0)
			{
				this.listUsers.setSelectedIndex(0);
			}
			this.setCredentialsPanelValues();
		}
		
		private void createListUsersModel()
		{
			ArrayList<UUID> credentialKeys = serverCredentials.getCredentialKeys();
			this.listUsersModel = new ArrayList<ListItem>();
			
			for (UUID credentialKey: credentialKeys)
			{
				ClientConfiguration clientConfiguration = serverCredentials.getCredentials(credentialKey, password);
				this.listUsersModel.add(
						new ListItem(
								ServerCredentials.getCredentialsDisplayName(clientConfiguration),
								credentialKey));
			}
			
			Collections.sort(this.listUsersModel, new ListItem());
		}
		
		private int getListIndexByCredentialsKey(UUID key)
		{
			if (key == null) return -1;
			
			int index = -1;
			
			for (int i = 0; i < this.listUsersModel.size(); i++)
			{
				UUID handle = (UUID)this.listUsersModel.get(i).getHandle();
				
				if (handle.equals(key))
				{
					index = i;
					break;
				}
			}
			
			return index;
		}

		@Override
		public void buttonClicked(Button source)
		{
			if (source == this.butAdd)
			{
				popupMenu.show(
						butAdd, 
						butAdd.getBounds().x, 
						butAdd.getBounds().y + butAdd.getBounds().height);
			}
		}

		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			this.setCredentialsPanelValues();
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == this.popupMenuItemUser)
			{
				this.importCopyPaste();
			}
			else if (e.getSource() == this.popupMenuItemAdmin)
			{
				this.importFromFile();
			}
		}
		
		private void importCopyPaste()
		{
			ClipboardImportJDialog<ResponseMessageChangeUser> dlg = 
					new ClipboardImportJDialog<ResponseMessageChangeUser>(
							parent, ResponseMessageChangeUser.class, true);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == DialogWindowResult.OK)
			{
				ResponseMessageChangeUser newUser = (ResponseMessageChangeUser)dlg.obj;
				
				if (newUser != null)
				{
					ClientConfiguration clientConfiguration = 
							new ClientConfiguration(
									newUser.userId, 
									newUser.serverUrl, 
									newUser.serverPort, 
									Client.CLIENT_SOCKET_TIMEOUT, 
									null,
									newUser.serverPublicKey, 
									newUser.adminEmail);
					
					ServerCredentials.setActivationCode(clientConfiguration, newUser.activationCode);
					
					this.addNew(clientConfiguration);
				}
			}
		}
		
		private void importFromFile()
		{
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setDialogTitle(VegaResources.AuthenticationFile(false));
			
			if (lastSelectedDirectory != null)
			{
				fc.setSelectedFile(new File(lastSelectedDirectory));
			}
			else
			{
				fc.setSelectedFile(new File(System.getProperty("user.dir")));
			}
			
			int returnVal = fc.showOpenDialog(parent);
			
			if(returnVal != JFileChooser.APPROVE_OPTION)
			{
				return;
			}
			
			File file = fc.getSelectedFile();
			lastSelectedDirectory = file.getParent();
			
			ClientConfiguration clientConfiguration = ClientConfiguration.readFromFile(file.getAbsolutePath()); 
			
			if (clientConfiguration != null && clientConfiguration.getUserId() != null)
			{
				this.addNew(clientConfiguration);
			}
			else
			{
				DialogWindow.showError(
					parent,
					VegaResources.FileContainsInvalidCredentials(false, file.getAbsolutePath().toString()),
				    VegaResources.Error(false));
			}
		}
		
		private void addNew(ClientConfiguration clientConfiguration)
		{
			UUID credentialsKey = UUID.randomUUID();
			serverCredentials.setCredentials(credentialsKey, clientConfiguration, password);
			
			this.createListUsersModel();
			this.listUsers.refreshListItems(this.listUsersModel);
			
			this.listUsers.setSelectedIndex(this.getListIndexByCredentialsKey(credentialsKey));
			this.setCredentialsPanelValues();
		}
		
		private void setCredentialsPanelValues()
		{
			ListItem selectedListItem = this.listUsers.getSelectedListItem();
			
			if (selectedListItem == null)
			{
				this.panCredentials.setValues(null);
			}
			else
			{
				ClientConfiguration clientConfiguration = serverCredentials.getCredentials((UUID)selectedListItem.getHandle(), password);
				this.panCredentials.setValues(clientConfiguration);
			}
		}
		
		private class CredentialsPanel extends Panel implements IButtonListener, ITextFieldListener
		{
			private Dialog parent;
			
			private TextField tfUrl;
			private TextField tfPort;
			private TextField tfTimeout;
			private TextField tfUserId;
			private TextField tfAdminEmail;
			
			private Button butActivate;
			private Button butConnectionTest;
			private Button butWriteAdminEmail;
			
			private ClientConfiguration clientConfig;
			
			private CredentialsPanel(
					Dialog parent,
					ClientConfiguration clientConfig)
			{
				super(new GridBagLayout());
				
				this.parent = parent;
				
				GridBagConstraints c = new GridBagConstraints();
				
				c.insets = new Insets(5, 5, 5, 5);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0.5;
				int textFieldColumns = 40;
				
				c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
				this.add(new Label(VegaResources.UserId(false)), c);
				
				c.gridx = 1; c.gridy = 0; c.gridwidth = 2;
				this.tfUserId = new TextField(textFieldColumns);
				this.tfUserId.setEditable(false);
				this.add(this.tfUserId, c);
				
				c.gridx = 3; c.gridy = 0; c.gridwidth = 1;
				this.butActivate = new Button(VegaResources.Activate(false), this);
				this.add(this.butActivate, c);
				
				c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
				this.add(new Label(VegaResources.ServerUrl(false)), c);
				
				c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
				this.tfUrl = new TextField("", null, textFieldColumns, 0, this); 
				this.add(this.tfUrl, c);
				
				c.gridx = 3; c.gridy = 1; c.gridwidth = 1;
				this.butConnectionTest = new Button(VegaResources.ConnectionTest(false), this);
				this.add(this.butConnectionTest, c);
				
				c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
				this.add(new Label(VegaResources.ServerPort(false)), c);
				
				c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
				this.tfPort = new TextField("", "[0-9]*", textFieldColumns, 5, this);
				this.add(this.tfPort, c);
				
				c.gridx = 0; c.gridy = 3;
				this.add(new Label(VegaResources.Timeout(false)), c);
				
				c.gridx = 1; c.gridy = 3; c.gridwidth = 2;
				this.tfTimeout = new TextField("", "[0-9]*", textFieldColumns, 8, this);
				this.add(this.tfTimeout, c);
				
				c.gridx = 0; c.gridy = 4; c.gridwidth = 1;
				this.add(new Label(VegaResources.EmailAdmin(false)), c);
				
				c.gridx = 1; c.gridy = 4; c.gridwidth = 2;
				this.tfAdminEmail = new TextField("", null, textFieldColumns, 0, this);
				this.add(this.tfAdminEmail, c);
				
				c.gridx = 3; c.gridy = 4; c.gridwidth = 1;
				this.butWriteAdminEmail = new Button(VegaResources.WriteEmail(false), this);
				this.add(this.butWriteAdminEmail, c);
				
				this.setValues(clientConfig);
			}
			
			private void setValues(ClientConfiguration clientConfig)
			{
				this.clientConfig = clientConfig;
				
				this.tfUrl.setEditable(clientConfig != null);
				this.tfPort.setEditable(clientConfig != null);
				this.tfTimeout.setEditable(clientConfig != null);
				this.tfAdminEmail.setEditable(clientConfig != null);
				
				this.butActivate.setEnabled(ServerCredentials.getActivationCode(clientConfig) != null);
				this.butConnectionTest.setEnabled(clientConfig != null && ServerCredentials.getActivationCode(clientConfig) == null);
				this.butWriteAdminEmail.setEnabled(clientConfig != null);
				
				if (clientConfig == null)
				{
					this.tfUserId.setText("");
					this.tfUrl.setText("");
					this.tfPort.setText("");
					this.tfTimeout.setText("");
					this.tfAdminEmail.setText("");
				}
				else
				{
					this.tfUserId.setText(
							ServerCredentials.getUserId(clientConfig));
					
					this.tfUrl.setText(
							clientConfig.getUrl() == null ? "": clientConfig.getUrl());
					
					this.tfPort.setText(
							clientConfig.getPort() == 0 ? "" : Integer.toString(clientConfig.getPort()));
										
					this.tfTimeout.setText(
							Integer.toString(clientConfig.getTimeout()));
					
					this.tfAdminEmail.setText(
							clientConfig.getAdminEmail() == null ? "" : clientConfig.getAdminEmail());
				}
			}
			
			private void activateUser()
			{
				this.acceptChanges();
				
				PayloadResponseMessageChangeUser userActivationData = 
						new PayloadResponseMessageChangeUser(
								ServerCredentials.getUserId(this.clientConfig), 
								ServerCredentials.getActivationCode(this.clientConfig), 
								this.clientConfig.getUrl(), 
								this.clientConfig.getPort(),
								this.clientConfig.getAdminEmail(), 
								this.clientConfig.getServerPublicKey());
								
				Tuple<ClientConfiguration, ResponseInfo> tuple = VegaClient.activateUser(
						userActivationData,
						VegaResources.getLocale(),
						Game.BUILD);
									
				if (tuple.getE2().isSuccess())
				{
					DialogWindow.showInformation(
							parent,
							VegaResources.UserActivationSuccess(false),
						    VegaResources.ActivateUser(false));
					
					this.clientConfig = tuple.getE1();
					this.setValues(this.clientConfig);
					
					ListItem selectedListItem = listUsers.getSelectedListItem();
					
					serverCredentials.setCredentials(
							(UUID)selectedListItem.getHandle(), 
							this.clientConfig, 
							password);
				}
				else
				{
					Vega.showServerError(this, tuple.getE2());
				}
			}
			
			private void connectionTest()
			{
				this.acceptChanges();
				
				VegaClient client = new VegaClient(this.clientConfig, false, null);
				
				Vega.showWaitCursor(parent);
				ResponseInfo info = client.pingServer();
				Vega.showDefaultCursor(parent);
				
				if (info.isSuccess())
				{
					DialogWindow.showInformation(
							parent, 
							VegaResources.ConnectionSuccessful(false), 
							VegaResources.ConnectionTest(false));
				}
				else
				{
					Vega.showServerError(parent, info);
				}
			}
			
			private void writeAdminEmail()
			{
				this.acceptChanges();
				
				EmailToolkit.launchEmailClient(
						parent, 
						this.clientConfig.getAdminEmail(), 
						VegaResources.VegaServer(
								false, 
								ServerCredentials.getCredentialsDisplayName(this.clientConfig)),
						"", 
						null, 
						null);
			}
			
			private void acceptChanges()
			{
				if (this.clientConfig == null);
				
				boolean credentialDisplayStringChanged = false;
				
				credentialDisplayStringChanged = !this.tfUrl.getText().equals(this.clientConfig.getUrl());
				credentialDisplayStringChanged |= this.tfPort.getTextInt() != this.clientConfig.getPort();
				
				this.clientConfig.setUrl(this.tfUrl.getText());
				this.clientConfig.setPort(this.tfPort.getTextInt());
				this.clientConfig.setTimeout(this.tfTimeout.getTextInt());
				this.clientConfig.setAdminEmail(this.tfAdminEmail.getText());

				UUID credentialsKey = (UUID) listUsers.getSelectedListItem().getHandle();
				serverCredentials.setCredentials(credentialsKey, this.clientConfig, password);
				
				if (credentialDisplayStringChanged)
				{
					createListUsersModel();
					listUsers.refreshListItems(listUsersModel);
					int index = getListIndexByCredentialsKey(credentialsKey);
					listUsers.setSelectedIndex(index);
				}
			}

			@Override
			public void buttonClicked(Button source)
			{
				if (source == this.butActivate)
				{
					this.activateUser();
				}
				else if (source == this.butConnectionTest)
				{
					this.connectionTest();
				}
				else if (source == this.butWriteAdminEmail)
				{
					this.writeAdminEmail();
				}
			}

			@Override
			public void textFieldFocusLost(TextField source)
			{
			}
			
			@Override
			public void textChanged(TextField source)
			{
				this.acceptChanges();
			}
		}
	}
}
