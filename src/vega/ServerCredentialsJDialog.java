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
import javax.swing.JSeparator;

import common.Game;
import common.VegaResources;
import commonServer.ResponseMessageChangeUser;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import spielwitz.biDiServer.Client;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.LogLevel;
import spielwitz.biDiServer.PayloadResponseMessageChangeUser;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.Tuple;
import spielwitz.biDiServer.User;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.IComboBoxListener;
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
	private static String lastSelectedDirectory;
	
	boolean ok;
	
	private Button butCancel;
	private Button butOk;
	
	private ActivateServerConnectionPanel panActivateConnection;
	private AdminPanel panAdmin;
	private UsersPanel panUsers;
	
	private ServerCredentials serverCredentials;
	private ServerCredentials serverCredentialsBefore;
	
	private TabbedPane tabpane;
	
	ServerCredentialsJDialog(
			Vega parent,
			ServerCredentials credentials)
	{
		super(parent, "Server-Zugangsdaten", new BorderLayout());
		
		this.serverCredentialsBefore = credentials;
		this.serverCredentials = credentials.getClone();
		
		this.tabpane = new TabbedPane();
		
		this.panActivateConnection = new ActivateServerConnectionPanel();
		this.panAdmin = new AdminPanel();
		
		this.tabpane.addTab("Serververbindung", this.panActivateConnection);
		
		this.panUsers = new UsersPanel(this);
		this.tabpane.addTab("Zugangsdaten", this.panUsers);
		
		this.tabpane.addTab("Serververwaltung", this.panAdmin);
				
		this.addToInnerPanel(tabpane, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
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
	
	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butCancel)
		{
			this.close();
		}
		else if (source == this.butOk)
		{
			this.ok = true;
			super.close();
		}
	}
	
	ServerCredentials getServerCredentials()
	{
		return serverCredentials;
	}

	protected boolean confirmClose()
	{
		if (!this.ok &&
			 this.serverCredentials.hasChanges(this.serverCredentialsBefore))
		{
			MessageBoxResult result = MessageBox.showYesNoCancel(
					this, 
					"Sie haben Änderungen vorgenommen. Möchten Sie diese übernehmen?", 
					"Ungesicherte Änderungen");
			
			if (result == MessageBoxResult.YES)
			{
				this.ok = true;
			}
			else if (result == MessageBoxResult.CANCEL)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private class ActivateServerConnectionPanel extends Panel implements IComboBoxListener, ICheckBoxListener
	{
		private CheckBox cbActivate;
		private ComboBox comboCredentialsUser;
		private ArrayList<ListItem> comboCredentialsUserModel;
		
		private ActivateServerConnectionPanel()
		{
			super(new BorderLayout());
			
			PanelWithInsets panCredentials = new PanelWithInsets(new FlowLayout(FlowLayout.LEFT));
			
			this.cbActivate = new CheckBox(
					"Verbinden als Spieler mit Zugangsdaten", 
					serverCredentials.connectionActive, 
					this);
			panCredentials.addToInnerPanel(this.cbActivate);
			
			panCredentials.addToInnerPanel(new JSeparator());
			
			this.comboCredentialsUserModel = new ArrayList<ListItem>();
			this.comboCredentialsUser = new ComboBox(this.comboCredentialsUserModel, 40, null, this);
			panCredentials.addToInnerPanel(this.comboCredentialsUser);
			
			this.add(panCredentials, BorderLayout.NORTH);
		}

		@Override
		public void checkBoxValueChanged(CheckBox source, boolean newValue)
		{
			if (source == this.cbActivate)
			{
				serverCredentials.connectionActive = newValue;
			}
		}
		
		@Override
		public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
		{
			if (source == this.comboCredentialsUser)
			{
				serverCredentials.userCredentialsSelected = (UUID)selectedListItem.getHandle();
			}
		}

		@Override
		public void comboBoxItemSelected(ComboBox source, String selectedValue)
		{
		}
	}
	
	private class UsersPanel extends Panel implements IButtonListener, IListListener, ActionListener
	{
		private Button butAdd;
		private Button butDelete;
		private List listUsers;
		private ArrayList<ListItem> listUsersModel;
		private CredentialsPanel panCredentials;
		
		private Dialog parent;
		private JPopupMenu popupMenu;
		private JMenuItem popupMenuItemAdmin;
		
		private JMenuItem popupMenuItemUser;
		
		private UsersPanel(Dialog parent)
		{
			super(new BorderLayout());
			
			this.parent = parent;
			this.popupMenu = new JPopupMenu();
			
			this.popupMenuItemUser = new JMenuItem ("Inaktiver User aus der Zwischenablage");
		    this.popupMenuItemUser.addActionListener(this);
		    popupMenu.add (this.popupMenuItemUser);
		    
		    this.popupMenuItemAdmin = new JMenuItem ("Aktiver User aus einer Datei");
		    this.popupMenuItemAdmin.addActionListener(this);
		    popupMenu.add (this.popupMenuItemAdmin);
			
			PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 10));
			
			this.createListUsersModel();
			this.listUsers = new List(this, this.listUsersModel);
			this.listUsers.setPreferredSize(new Dimension(300, 200));
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
			else if (source == this.butDelete)
			{
				this.deleteUser();
			}
		}
		
		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			this.setCredentialsPanelValues();
		}

		private void addNew(ClientConfiguration clientConfiguration)
		{
			UUID credentialsKey = UUID.randomUUID();
			serverCredentials.setCredentials(credentialsKey, clientConfiguration);
			
			this.createListUsersModel();
			this.listUsers.refreshListItems(this.listUsersModel);
			
			this.listUsers.setSelectedIndex(this.getListIndexByCredentialsKey(credentialsKey));
			this.setCredentialsPanelValues();
		}

		private void createListUsersModel()
		{
			ArrayList<UUID> credentialKeys = serverCredentials.getCredentialKeys();
			this.listUsersModel = new ArrayList<ListItem>();
			panActivateConnection.comboCredentialsUserModel = new ArrayList<ListItem>();
			panAdmin.comboCredentialsAdminModel = new ArrayList<ListItem>();
			
			for (UUID credentialKey: credentialKeys)
			{
				ClientConfiguration clientConfiguration = serverCredentials.getCredentials(credentialKey);
				boolean isUserActive = ServerCredentials.isUserActive(clientConfiguration);
				
				this.listUsersModel.add(
						new ListItem(
								isUserActive ?
										ServerCredentials.getCredentialsDisplayName(clientConfiguration) :
										"(inaktiv) "+ServerCredentials.getCredentialsDisplayName(clientConfiguration),
								credentialKey));
				
				if (clientConfiguration.getUserId().equals(User.ADMIN_USER_ID))
				{
					panAdmin.comboCredentialsAdminModel.add(
							new ListItem(
									ServerCredentials.getCredentialsDisplayName(clientConfiguration),
									credentialKey));
				}
				else if (isUserActive)
				{
					panActivateConnection.comboCredentialsUserModel.add(
							new ListItem(
									ServerCredentials.getCredentialsDisplayName(clientConfiguration),
									credentialKey));
				}
			}
			
			Collections.sort(this.listUsersModel, new ListItem());
			
			Collections.sort(panActivateConnection.comboCredentialsUserModel, new ListItem());
			panActivateConnection.comboCredentialsUser.setItems(panActivateConnection.comboCredentialsUserModel);
			
			if (!panActivateConnection.comboCredentialsUser.setSelectedListItemByHandle(serverCredentials.userCredentialsSelected))
			{
				if (panActivateConnection.comboCredentialsUserModel.size() > 0)
				{
					ListItem selectedListItem = panActivateConnection.comboCredentialsUserModel.get(0);
					panActivateConnection.comboCredentialsUser.setSelectedListItemByHandle(selectedListItem.getHandle());
					serverCredentials.userCredentialsSelected = (UUID)selectedListItem.getHandle();
				}
				else
				{
					serverCredentials.userCredentialsSelected = null;
				}
			}
			
			Collections.sort(panAdmin.comboCredentialsAdminModel, new ListItem());
			panAdmin.comboCredentialsAdmin.setItems(panAdmin.comboCredentialsAdminModel);
			
			if (!panAdmin.comboCredentialsAdmin.setSelectedListItemByHandle(serverCredentials.adminCredentialsSelected))
			{
				if (panAdmin.comboCredentialsAdminModel.size() > 0)
				{
					ListItem selectedListItem = panAdmin.comboCredentialsAdminModel.get(0);
					panAdmin.comboCredentialsAdmin.setSelectedListItemByHandle(selectedListItem.getHandle());
					serverCredentials.adminCredentialsSelected = (UUID)selectedListItem.getHandle();
				}
				else
				{
					serverCredentials.adminCredentialsSelected = null;
				}
			}
		}
		
		private void deleteUser()
		{
			ListItem listItem = this.listUsers.getSelectedListItem();
			if (listItem == null) return;
			
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials((UUID)listItem.getHandle());
			
			MessageBoxResult result = MessageBox.showYesNo(
										parent, 
										"Möchten Sie die Zugangsdaten des Users [" + ServerCredentials.getCredentialsDisplayName(clientConfiguration) + "] wirklich löschen?", 
										"User löschen");
			
			if (result != MessageBoxResult.YES) return;
			
			serverCredentials.deleteCredentials((UUID)listItem.getHandle());
			
			this.createListUsersModel();
			this.listUsers.refreshListItems(this.listUsersModel);
			
			if (this.listUsersModel.size() > 0)
			{
				this.listUsers.setSelectedIndex(0);
			}
			
			this.setCredentialsPanelValues();
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
		
		private void importCopyPaste()
		{
			ClipboardImportJDialog<ResponseMessageChangeUser> dlg = 
					new ClipboardImportJDialog<ResponseMessageChangeUser>(
							parent, ResponseMessageChangeUser.class, true);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == MessageBoxResult.OK)
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
			
			//fc.setFileSelectionMode(JFileChooser);
			fc.setDialogTitle(VegaResources.AuthenticationFile(false));
			fc.setMultiSelectionEnabled(true);
			
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
			
			File[] files = fc.getSelectedFiles();
			lastSelectedDirectory = files[0].getParent();
			
			ArrayList<ClientConfiguration> clientConfigurations = new ArrayList<ClientConfiguration>();
			
			for (File file: files)
			{
				ClientConfiguration clientConfiguration = ClientConfiguration.readFromFile(file.getAbsolutePath());
				
				if (clientConfiguration != null && clientConfiguration.getUserId() != null)
				{
					clientConfigurations.add(clientConfiguration);
				}
				else
				{
					MessageBox.showError(
						parent,
						VegaResources.FileContainsInvalidCredentials(false, file.getAbsolutePath().toString()),
					    VegaResources.Error(false));
					
					return;
				}
			}
			
			for (ClientConfiguration clientConfiguration: clientConfigurations)
			{
				this.addNew(clientConfiguration);
			}
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
				ClientConfiguration clientConfiguration = serverCredentials.getCredentials((UUID)selectedListItem.getHandle());
				this.panCredentials.setValues(clientConfiguration);
			}
		}
		
		private class CredentialsPanel extends Panel implements IButtonListener, ITextFieldListener
		{
			private Button butActivate;
			
			private Button butConnectionTest;
			private Button butWriteAdminEmail;
			private ClientConfiguration clientConfig;
			private Dialog parent;
			private TextField tfAdminEmail;
			
			private TextField tfPort;
			private TextField tfTimeout;
			private TextField tfUrl;
			
			private TextField tfUserId;
			
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
			public void textChanged(TextField source)
			{
				this.acceptChanges();
			}
			
			@Override
			public void textFieldFocusLost(TextField source)
			{
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
				serverCredentials.setCredentials(credentialsKey, this.clientConfig);
				
				if (credentialDisplayStringChanged)
				{
					createListUsersModel();
					listUsers.refreshListItems(listUsersModel);
					int index = getListIndexByCredentialsKey(credentialsKey);
					listUsers.setSelectedIndex(index);
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
					this.clientConfig = tuple.getE1();
					this.setValues(this.clientConfig);
					
					ListItem selectedListItem = listUsers.getSelectedListItem();
					
					serverCredentials.setCredentials(
							(UUID)selectedListItem.getHandle(), 
							this.clientConfig);
					
					createListUsersModel();
					listUsers.refreshListItems(listUsersModel);
					
					listUsers.setSelectedIndex(getListIndexByCredentialsKey((UUID)selectedListItem.getHandle()));
					
					MessageBox.showInformation(
							parent,
							VegaResources.UserActivationSuccess(false),
						    VegaResources.ActivateUser(false));
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
					MessageBox.showInformation(
							parent, 
							VegaResources.ConnectionSuccessful(false), 
							VegaResources.ConnectionTest(false));
				}
				else
				{
					Vega.showServerError(parent, info);
				}
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
		}
	}

	private class AdminPanel extends Panel implements IButtonListener, IComboBoxListener, IListListener
	{
		private ComboBox comboServerLogLevel;
		private ComboBox comboCredentialsAdmin;
		private ArrayList<ListItem> comboCredentialsAdminModel;
		
		private List listServerUsers;
		private ArrayList<ListItem> listServerUsersModel;
		
		private TextField tfClientBuild;
		private TextField tfServerBuild;
		private TextField tfServerLogSize;
		private TextField tfServerStartDate;
		
		private Button butLoadServerData;
		private Button butShutdown;
		private Button butServerLogDownload;
		private Button butServerLogLevelChange;
		private Button butAdd;
		private Button butDelete;
		
		private AdminPanel()
		{
			super(new BorderLayout());
			
			PanelWithInsets panMain = new PanelWithInsets(new BorderLayout(10, 10));
			
			Panel panAdminCredentials = new Panel(new FlowLayout(FlowLayout.LEFT));
			panAdminCredentials.add(new Label("Administrator-Zugangsdaten"));
			panAdminCredentials.add(new JSeparator());
			
			this.comboCredentialsAdminModel = new ArrayList<ListItem>();
			this.comboCredentialsAdmin = new ComboBox(this.comboCredentialsAdminModel, 40, null, this);
			panAdminCredentials.add(this.comboCredentialsAdmin);
			panAdminCredentials.add(new JSeparator());
			
			this.butLoadServerData = new Button("Server-Daten laden", this);
			panAdminCredentials.add(this.butLoadServerData);
			
			panMain.addToInnerPanel(panAdminCredentials, BorderLayout.NORTH);
			
			// -----------
			Panel panServerData = new Panel(new BorderLayout(10, 10));
			
			PanelWithInsets panServerStatus = new PanelWithInsets(VegaResources.ServerStatus(false), new GridBagLayout());
			
			GridBagConstraints cPanServerStatus = new GridBagConstraints();
			
			cPanServerStatus.insets = new Insets(5, 5, 5, 5);
			cPanServerStatus.fill = GridBagConstraints.HORIZONTAL;
			cPanServerStatus.weightx = 0.5;
			cPanServerStatus.weighty = 0.5;
			
			cPanServerStatus.gridx = 0;
			cPanServerStatus.gridy = 0;
			panServerStatus.addToInnerPanel(new Label(VegaResources.ClientBuild(false)),cPanServerStatus);
			
			cPanServerStatus.gridx = 1;
			this.tfClientBuild = new TextField(Game.BUILD, null, 30, -1, null);
			this.tfClientBuild.setEditable(false);
			panServerStatus.addToInnerPanel(this.tfClientBuild, cPanServerStatus);
			
			cPanServerStatus.gridx = 0;
			cPanServerStatus.gridy = 1;
			panServerStatus.addToInnerPanel(new Label(VegaResources.ServerBuild(false)), cPanServerStatus);
			
			cPanServerStatus.gridx = 1;
			this.tfServerBuild = new TextField("", null, 30, -1, null);
			this.tfServerBuild.setEditable(false);
			panServerStatus.addToInnerPanel(this.tfServerBuild, cPanServerStatus);
			
			cPanServerStatus.gridx = 0;
			cPanServerStatus.gridy = 2;
			panServerStatus.addToInnerPanel(new Label(VegaResources.RunningSince(false)), cPanServerStatus);
			
			cPanServerStatus.gridx = 1;
			this.tfServerStartDate = new TextField("", null, 30, -1, null);
			this.tfServerStartDate.setEditable(false);
			panServerStatus.addToInnerPanel(this.tfServerStartDate, cPanServerStatus);
			
			cPanServerStatus.gridx = 2;
			this.butShutdown = new Button(VegaResources.ShutdownServer(false), this);
			panServerStatus.addToInnerPanel(this.butShutdown, cPanServerStatus);
			
			cPanServerStatus.gridx = 0;
			cPanServerStatus.gridy = 3;
			panServerStatus.addToInnerPanel(new Label(VegaResources.LogSize(false)), cPanServerStatus);
			
			cPanServerStatus.gridx = 1;
			this.tfServerLogSize = new TextField("", null, 30, -1, null);
			this.tfServerLogSize.setEditable(false);
			panServerStatus.addToInnerPanel(this.tfServerLogSize, cPanServerStatus);
			
			cPanServerStatus.gridx = 2;
			this.butServerLogDownload = new Button(VegaResources.DownloadLog(false), this);
			panServerStatus.addToInnerPanel(this.butServerLogDownload, cPanServerStatus);
			
			cPanServerStatus.gridx = 0;
			cPanServerStatus.gridy = 4;
			panServerStatus.addToInnerPanel(new Label(VegaResources.LogLevel(false)), cPanServerStatus);
			
			String[] logLevels = new String[LogLevel.values().length];
			int counter = 0;
			for (LogLevel logEventType: LogLevel.values())
			{
				logLevels[counter] = logEventType.toString();
				counter++;
			}
			
			cPanServerStatus.gridx = 1;
			this.comboServerLogLevel = new ComboBox(logLevels, 10, null, null);
			panServerStatus.addToInnerPanel(this.comboServerLogLevel, cPanServerStatus);
			
			cPanServerStatus.gridx = 2;
			this.butServerLogLevelChange = new Button(VegaResources.ChangeLogLevel(false), this);
			panServerStatus.addToInnerPanel(this.butServerLogLevelChange, cPanServerStatus);
			
			panServerData.add(panServerStatus, BorderLayout.NORTH);
			
			// -----------
			PanelWithInsets panServerUsers = new PanelWithInsets(VegaResources.Users(false), new BorderLayout(10, 0));
			
			this.listServerUsersModel = new ArrayList<ListItem>();
			this.listServerUsers = new List(this, this.listServerUsersModel);
			this.listServerUsers.setPreferredSize(new Dimension(150, 200));
			panServerUsers.addToInnerPanel(this.listServerUsers, BorderLayout.CENTER);
			
			Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
			
			this.butAdd = new Button("+", this);
			this.butAdd.setToolTipText("User hinzufügen");
			
			panUsersListButtons.add(this.butAdd);
			
			this.butDelete = new Button("-", this);
			this.butDelete.setToolTipText("User löschen");
			panUsersListButtons.add(this.butDelete);
			
			panServerUsers.addToInnerPanel(panUsersListButtons, BorderLayout.SOUTH);
			
			panServerData.add(panServerUsers, BorderLayout.CENTER);
			
			panMain.addToInnerPanel(panServerData, BorderLayout.CENTER);
			
			this.add(panMain, BorderLayout.CENTER);
		}

		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
		}

		@Override
		public void buttonClicked(Button source)
		{
		}

		@Override
		public void comboBoxItemSelected(ComboBox source, String selectedValue)
		{
		}

		@Override
		public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
		{
		}
		
	}
}
