/**	VEGA - a strategy game
    Copyright (C) 1989-2025 Michael Schweitzer, spielwitz@icloud.com

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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import common.CommonUtils;
import common.Game;
import common.Player;
import common.VegaResources;
import commonServer.ClientServerConstants;
import commonServer.ResponseMessageChangeUser;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import spielwitz.biDiServer.Client;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.LogLevel;
import spielwitz.biDiServer.PayloadRequestMessageChangeUser;
import spielwitz.biDiServer.PayloadResponseMessageChangeUser;
import spielwitz.biDiServer.PayloadResponseMessageGetLog;
import spielwitz.biDiServer.PayloadResponseMessageGetServerStatus;
import spielwitz.biDiServer.PayloadResponseMessageGetUsers;
import spielwitz.biDiServer.Response;
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
import uiBaseControls.PasswordField;
import uiBaseControls.TabbedPane;
import uiBaseControls.TextField;

@SuppressWarnings("serial")
class ServerSettingsJDialog extends Dialog implements IButtonListener
{
	private static String lastSelectedDirectory;
	static private File selectedDirectoryActivationFile;
	
	boolean ok;
	
	private Button butCancel;
	private Button butOk;
	private Button butPasswordChange;
	
	private AdminPanel panAdmin;
	private UsersPanel panUsers;
	
	private ServerCredentials serverCredentials;
	private ServerCredentials serverCredentialsBefore;
	
	private TabbedPane tabpane;
	
	ServerSettingsJDialog(
			Vega parent,
			ServerCredentials credentials)
	{
		super(parent, VegaResources.ServerSettings(false), new BorderLayout());
		
		this.serverCredentialsBefore = credentials;
		this.serverCredentials = credentials.getClone();
		
		this.tabpane = new TabbedPane();
		
		this.panAdmin = new AdminPanel(this);
		
		this.panUsers = new UsersPanel(this);
		this.tabpane.addTab(VegaResources.Credentials(false), this.panUsers);
		
		this.tabpane.addTab(VegaResources.ServerAdministration(false), this.panAdmin);
				
		this.addToInnerPanel(tabpane, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butPasswordChange = new Button(VegaResources.ChangePassword(false), this);
		panButtons.add(this.butPasswordChange);
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		panButtons.add(new JSeparator());
		panButtons.add(new JSeparator());
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
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
		else if (source == this.butPasswordChange)
		{
			this.changePassword();
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
					VegaResources.UnsavedChangesSave(false), 
					VegaResources.UnsavedChanges(false));
			
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

	private void changePassword()
	{
		ServerCredentialsPasswordJDialog dlg = 
				new ServerCredentialsPasswordJDialog(
						this, 
						this.serverCredentials,
						this.serverCredentials.containsCredentials() ?
								ServerCredentialsPasswordJDialogMode.CHANGE_PASSWORD :
								ServerCredentialsPasswordJDialogMode.ENTER_PASSWORD_FIRST_TIME
								);
			
		dlg.setVisible(true);
		
		if (dlg.result == MessageBoxResult.OK)
		{
			this.serverCredentials = dlg.getServerCredentials();
		}
	}
	
	private static String getCredentialFileName(String userId, String url, int port)
	{
		String userIdTrimmed = userId.replaceAll("[^a-zA-Z0-9.-]", "_");
		String urlTrimmed = url.replaceAll("[^a-zA-Z0-9.-]", "_");
		return userIdTrimmed + "_" + urlTrimmed + "_" + port; 
	}
	
	private class AdminPanel extends Panel implements IButtonListener, IComboBoxListener, IListListener
	{
		private Button butAdd;
		private Button butDelete;
		private Button butLoadServerData;
		
		private Button butServerLogDownload;
		private Button butServerLogLevelChange;
		
		private Button butShutdown;
		private Button butSubmit;
		private ComboBox comboCredentialsAdmin;
		private ArrayList<ListItem> comboCredentialsAdminModel;
		
		private ComboBox comboServerLogLevel;
		private List listServerUsers;
		private ArrayList<ListItem> listServerUsersModel;
		private PanelUserData panUserDetails;
		private Dialog parent;
		private TextField tfClientBuild;
		private TextField tfServerBuild;
		
		private TextField tfServerLogSize;
		private TextField tfServerStartDate;
		
		private AdminPanel(Dialog parent)
		{
			super(new BorderLayout());
			
			this.parent = parent;
			PanelWithInsets panMain = new PanelWithInsets(new BorderLayout(10, 10));
			
			Panel panAdminCredentials = new Panel(new FlowLayout(FlowLayout.LEFT));
			panAdminCredentials.add(new Label(VegaResources.AdminCredentials(false)));
			panAdminCredentials.add(new JSeparator());
			
			this.comboCredentialsAdminModel = new ArrayList<ListItem>();
			this.comboCredentialsAdmin = new ComboBox(this.comboCredentialsAdminModel, 40, null, this);
			panAdminCredentials.add(this.comboCredentialsAdmin);
			panAdminCredentials.add(new JSeparator());
			
			this.butLoadServerData = new Button(VegaResources.LoadServerData(false), this);
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
			PanelWithInsets panServerUsers = new PanelWithInsets(VegaResources.Users(false), new BorderLayout(30, 0));
			
			Panel panServerUsersList = new Panel(new BorderLayout(0, 5));
			
			this.listServerUsersModel = new ArrayList<ListItem>();
			this.listServerUsers = new List(this, this.listServerUsersModel);
			this.listServerUsers.setPreferredSize(new Dimension(200, 200));
			panServerUsersList.add(this.listServerUsers, BorderLayout.CENTER);
			
			Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
			
			this.butAdd = new Button("+", this);
			this.butAdd.setToolTipText(VegaResources.CreateNewUser(false));
			
			panUsersListButtons.add(this.butAdd);
			
			this.butDelete = new Button("-", this);
			this.butDelete.setToolTipText(VegaResources.DeleteUser(false));
			panUsersListButtons.add(this.butDelete);
			
			panServerUsersList.add(panUsersListButtons, BorderLayout.SOUTH);
			
			panServerUsers.addToInnerPanel(panServerUsersList, BorderLayout.WEST);
			
			// -------
			Panel panUserDetailsOuter = new Panel(new BorderLayout(0, 0));
			
			this.panUserDetails = new PanelUserData(PanelUserDataMode.NoDataFromServer);
			panUserDetailsOuter.add(this.panUserDetails, BorderLayout.NORTH);
			
			this.butSubmit = new Button(VegaResources.SubmitChangesToServer(false), this);
			panUserDetailsOuter.add(this.butSubmit, BorderLayout.CENTER);
			
			panServerUsers.addToInnerPanel(panUserDetailsOuter, BorderLayout.CENTER);
			// -------
			
			panServerData.add(panServerUsers, BorderLayout.CENTER);
			
			panMain.addToInnerPanel(panServerData, BorderLayout.CENTER);
			
			this.add(panMain, BorderLayout.CENTER);
			
			this.clearData();
		}

		@Override
		public void buttonClicked(Button source)
		{
			if (source == this.butLoadServerData)
			{
				this.refresh();
			}
			else if (source == this.butAdd)
			{
				this.panUserDetails.setMode(PanelUserDataMode.NewUser);
				this.setControlsEnabled();
			}
			else if (source == this.butDelete)
			{
				this.deleteUser();
			}
			else if (source == this.butSubmit)
			{
				String userId =  this.panUserDetails.tfUserId.getText().trim();
				
				if (this.panUserDetails.mode == PanelUserDataMode.NewUser)
				{
					this.submitNewUser(userId);
				}
				else if (this.panUserDetails.mode == PanelUserDataMode.ChangeUser)
				{
					this.submitChangeUser(userId);
				}
				else if (this.panUserDetails.mode == PanelUserDataMode.RenewCredentials)
				{
					this.submitRenewCredentials(userId);
				}
			}
			else if (source == this.butShutdown)
			{
				this.shutdownServer();
			}
			else if (source == this.butServerLogDownload)
			{
				this.downloadLog();
			}
			else if (source == this.butServerLogLevelChange)
			{
				this.changeLogLevel();
			}
		}
		
		@Override
		public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
		{
			serverCredentials.adminCredentialsSelected = (UUID)selectedListItem.getHandle();
			this.clearData();
		}
		
		@Override
		public void comboBoxItemSelected(ComboBox source, String selectedValue)
		{
		}
		
		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			this.panUserDetails.setMode(PanelUserDataMode.ChangeUser);
			this.setControlsEnabled();
		}
		
		@Override
		public int[] sortListItems(ArrayList<ListItem> listItems)
		{
			return null;
		}

		private void changeLogLevel()
		{
			String newLogLevel = (String) this.comboServerLogLevel.getSelectedItem();
			
			MessageBoxResult dialogResult = MessageBox.showYesNo(
					this,
					VegaResources.ChangeLogLevelQuestion(false, newLogLevel),
				    VegaResources.ChangeLogLevel(false));
			
			if (dialogResult != MessageBoxResult.YES)
				return;
			
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
			VegaClient client = new VegaClient(clientConfiguration, false, null);
			
			Vega.showWaitCursor(this);
			ResponseInfo info = client.setLogLevel(LogLevel.valueOf(newLogLevel));
			Vega.showDefaultCursor(this);
			
			if (info.isSuccess())
			{
				MessageBox.showInformation(
						this, 
						VegaResources.LogLevelChanged(false), 
						VegaResources.ChangeLogLevel(false));
			}
			else
				Vega.showServerError(this, info);

		}

		private void clearData()
		{
			this.tfServerBuild.setText("");
			this.tfServerStartDate.setText("");
			this.tfServerLogSize.setText("");
			this.comboServerLogLevel.setSelectedItem("");
			
			this.listServerUsersModel = new ArrayList<ListItem>();
			this.listServerUsers.refreshListItems(this.listServerUsersModel);
			
			this.panUserDetails.clearData();
		}

		private void deleteUser()
		{
			ListItem selectedListItem = this.listServerUsers.getSelectedListItem();
			
			if (selectedListItem == null)
				return;
			
			String userId = ((User)selectedListItem.getHandle()).getId();
			
			MessageBoxResult dialogResult = MessageBox.showOkCancel(
					this,
					VegaResources.DeleteUserQuestion(false, userId),
				    VegaResources.Users(false));
			
			if (dialogResult != MessageBoxResult.OK)
				return;
			
			dialogResult = MessageBox.showYesNo(
					this,
					VegaResources.AreYouSure(false),
				    VegaResources.Users(false));
			
			if (dialogResult != MessageBoxResult.YES)
				return;
			
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
			VegaClient client = new VegaClient(clientConfiguration, false, null);
			
			Vega.showWaitCursor(this);
			ResponseInfo info = client.deleteUser(userId);
			Vega.showDefaultCursor(this);
			
			if (info.isSuccess())
			{
				this.butLoadServerData.doClick();
				MessageBox.showInformation(
						this, 
						VegaResources.UserDeleted(false, userId), 
						VegaResources.Users(false));
			}
			else
				Vega.showServerError(this, info);
		}
		
		private void downloadLog()
		{
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
			VegaClient client = new VegaClient(clientConfiguration, false, null);
			
			Vega.showWaitCursor(this);
			Response<PayloadResponseMessageGetLog> response = client.getServerLog();
			Vega.showDefaultCursor(this);
			
			if (response.getResponseInfo().isSuccess())
			{
				if (response.getPayload().getFileName() != null && 
					response.getPayload().getLogCsv() != null && 
					response.getPayload().getLogCsv().length() > 0)
				{	
					File file = new File(response.getPayload().getFileName());
					
					JFileChooser fc = new JFileChooser();
					
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setDialogTitle(VegaResources.SaveLogFile(false));
					fc.setSelectedFile(file);
					
					int returnVal = fc.showSaveDialog(this);
					
					if(returnVal != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					
					file = fc.getSelectedFile();
					
					try
					{
						BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
					    writer.write(response.getPayload().getLogCsv());
					    writer.close();
					    
					    MessageBox.showInformation(
					    		this, 
					    		VegaResources.SaveFileSuccess(false), 
					    		VegaResources.SaveLogFile(false));
					}
					catch (Exception x)
					{
						MessageBox.showError(
								this, 
								VegaResources.SaveLogFileError(false, x.getMessage()), 
								VegaResources.SaveLogFile(false));
					}
				}
				else
					MessageBox.showInformation(
							this, 
							VegaResources.ServerLogEmpty(false), 
							VegaResources.DownloadLog(false));
			}
			else
				Vega.showServerError(this, response.getResponseInfo());
		}
		
		private void refresh()
		{
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
			
			VegaClient client = new VegaClient(clientConfiguration, false, null);
			
			Vega.showWaitCursor(this);
			Response<PayloadResponseMessageGetUsers> responseUsers = client.getUsers();
			Vega.showDefaultCursor(this);
			
			if (!responseUsers.getResponseInfo().isSuccess())
			{
				Vega.showServerError(parent, responseUsers.getResponseInfo());
				return;
			}
			
			Vega.showWaitCursor(this);
			Response<PayloadResponseMessageGetServerStatus> responseServerStatus = client.getServerStatus();
			Vega.showDefaultCursor(this);
			
			if (!responseServerStatus.getResponseInfo().isSuccess())
			{
				Vega.showServerError(parent, responseServerStatus.getResponseInfo());
				return;
			}
			
			this.tfServerBuild.setText(responseServerStatus.getPayload().getBuild());
			this.tfServerStartDate.setText(VegaUtils.formatDateTimeString(VegaUtils.convertMillisecondsToString(responseServerStatus.getPayload().getServerStartDate())));
			this.tfServerLogSize.setText(
					VegaResources.Bytes(
							false,
							Long.toString(responseServerStatus.getPayload().getLogSizeBytes())));
			this.comboServerLogLevel.setSelectedItem(responseServerStatus.getPayload().getLogLevel().toString());
			
			this.listServerUsersModel.clear();
			for (User user: responseUsers.getPayload().getUsers())
			{
				this.listServerUsersModel.add(
						new ListItem(
								user.getId(), 
								user));
			}
			Collections.sort(this.listServerUsersModel, new ListItem());
			this.listServerUsers.refreshListItems(this.listServerUsersModel);
			
			if (this.listServerUsersModel.size() > 0)
			{
				this.listServerUsers.setSelectedIndex(0);
				this.listItemSelected(this.listServerUsers, null, 0, 1);
			}
			else
			{
				this.panUserDetails.clearData();
				this.panUserDetails.setMode(PanelUserDataMode.NoUserSelected);
				this.setControlsEnabled();
			}
		}
		
		private void setControlsEnabled()
		{
			PanelUserDataMode mode = this.panUserDetails.mode;
			ListItem selectedUser = this.listServerUsers.getSelectedListItem();
			User userInfo = null;
			
			if (selectedUser != null)
			{
				userInfo = (User) selectedUser.getHandle();
			}
			
			switch (mode)
			{
			case NoDataFromServer:
				this.panUserDetails.labUserId.setEnabled(false);
				this.panUserDetails.tfUserId.setText("");
				this.panUserDetails.tfUserId.setEditable(false);
				
				this.panUserDetails.labName.setEnabled(false);
				this.panUserDetails.tfName.setText("");
				this.panUserDetails.tfName.setEditable(false);
				
				this.panUserDetails.labEmail.setEnabled(false);
				this.panUserDetails.tfEmail.setText("");
				this.panUserDetails.tfEmail.setEditable(false);
				
				this.panUserDetails.labPassword1.setEnabled(false);
				this.panUserDetails.tfPassword1.setText("");
				this.panUserDetails.tfPassword1.setEditable(false);
				
				this.panUserDetails.labPassword2.setEnabled(false);
				this.panUserDetails.tfPassword2.setText("");
				this.panUserDetails.tfPassword2.setEditable(false);
				
				this.panUserDetails.cbCredentials.setEnabled(false);
				this.panUserDetails.cbCredentials.setSelected(false);
				
				this.panUserDetails.cbUserActive.setEnabled(false);
				this.panUserDetails.cbUserActive.setSelected(false);
				
				this.butAdd.setEnabled(false);
				this.butDelete.setEnabled(false);
				this.butSubmit.setEnabled(false);
				
				this.butServerLogDownload.setEnabled(false);
				this.butServerLogLevelChange.setEnabled(false);
				this.butShutdown.setEnabled(false);
				this.comboServerLogLevel.setEnabled(false);
				
				break;
					
			case NoUserSelected:
					this.userListClearSelection();
				
					this.panUserDetails.labUserId.setEnabled(false);
					this.panUserDetails.tfUserId.setText("");
					this.panUserDetails.tfUserId.setEditable(false);
					
					this.panUserDetails.labName.setEnabled(false);
					this.panUserDetails.tfName.setText("");
					this.panUserDetails.tfName.setEditable(false);
					
					this.panUserDetails.labEmail.setEnabled(false);
					this.panUserDetails.tfEmail.setText("");
					this.panUserDetails.tfEmail.setEditable(false);
					
					this.panUserDetails.labPassword1.setEnabled(false);
					this.panUserDetails.tfPassword1.setText("");
					this.panUserDetails.tfPassword1.setEditable(false);
					
					this.panUserDetails.labPassword2.setEnabled(false);
					this.panUserDetails.tfPassword2.setText("");
					this.panUserDetails.tfPassword2.setEditable(false);
					
					this.panUserDetails.cbCredentials.setEnabled(false);
					this.panUserDetails.cbCredentials.setSelected(false);
					
					this.panUserDetails.cbUserActive.setEnabled(false);
					this.panUserDetails.cbUserActive.setSelected(false);
					
					this.butAdd.setEnabled(true);
					this.butDelete.setEnabled(false);
					this.butSubmit.setEnabled(false);
					
					this.butServerLogDownload.setEnabled(true);
					this.butServerLogLevelChange.setEnabled(true);
					this.butShutdown.setEnabled(true);
					this.comboServerLogLevel.setEnabled(true);
					
					break;
			
			case ChangeUser:
					this.panUserDetails.labUserId.setEnabled(true);
					this.panUserDetails.tfUserId.setText(userInfo.getId());
					this.panUserDetails.tfUserId.setEditable(false);
					
					this.panUserDetails.labName.setEnabled(true);
					this.panUserDetails.tfName.setText(userInfo.getName());
					this.panUserDetails.tfName.setEditable(true);
					
					this.panUserDetails.labEmail.setEnabled(true);
					this.panUserDetails.tfEmail.setText(userInfo.getCustomData().get(ClientServerConstants.USER_EMAIL_KEY));
					this.panUserDetails.tfEmail.setEditable(true);
					
					this.panUserDetails.labPassword1.setEnabled(false);
					this.panUserDetails.tfPassword1.setText("");
					this.panUserDetails.tfPassword1.setEditable(false);
					
					this.panUserDetails.labPassword2.setEnabled(false);
					this.panUserDetails.tfPassword2.setText("");
					this.panUserDetails.tfPassword2.setEditable(false);
					
					this.panUserDetails.cbCredentials.setEnabled(true);
					this.panUserDetails.cbCredentials.setSelected(false);
					
					this.panUserDetails.cbUserActive.setEnabled(false);
					this.panUserDetails.cbUserActive.setSelected(userInfo.isActive());
					
					this.butAdd.setEnabled(true);
					this.butDelete.setEnabled(true);
					this.butSubmit.setEnabled(true);
					
					this.butServerLogDownload.setEnabled(true);
					this.butServerLogLevelChange.setEnabled(true);
					this.butShutdown.setEnabled(true);
					this.comboServerLogLevel.setEnabled(true);
					
					break;
			
			case NewUser:
					this.userListClearSelection();
					
					this.panUserDetails.labUserId.setEnabled(true);
					this.panUserDetails.tfUserId.setText("");
					this.panUserDetails.tfUserId.setEditable(true);
					
					this.panUserDetails.labName.setEnabled(true);
					this.panUserDetails.tfName.setText("");
					this.panUserDetails.tfName.setEditable(true);
					
					this.panUserDetails.labEmail.setEnabled(true);
					this.panUserDetails.tfEmail.setText("");
					this.panUserDetails.tfEmail.setEditable(true);
					
					this.panUserDetails.labPassword1.setEnabled(true);
					this.panUserDetails.tfPassword1.setText("");
					this.panUserDetails.tfPassword1.setEditable(true);
					
					this.panUserDetails.labPassword2.setEnabled(true);
					this.panUserDetails.tfPassword2.setText("");
					this.panUserDetails.tfPassword2.setEditable(true);
					
					this.panUserDetails.cbCredentials.setEnabled(false);
					this.panUserDetails.cbCredentials.setSelected(true);
					
					this.panUserDetails.cbUserActive.setEnabled(false);
					this.panUserDetails.cbUserActive.setSelected(false);
					
					this.butAdd.setEnabled(false || serverCredentials.adminCredentialsSelected == null);
					this.butDelete.setEnabled(false);
					this.butSubmit.setEnabled(true);
					
					this.butServerLogDownload.setEnabled(true);
					this.butServerLogLevelChange.setEnabled(true);
					this.butShutdown.setEnabled(true);
					this.comboServerLogLevel.setEnabled(true);
					
					break;
			
			case RenewCredentials:
					this.panUserDetails.labUserId.setEnabled(true);
					this.panUserDetails.tfUserId.setText(userInfo.getId());
					this.panUserDetails.tfUserId.setEditable(false);
					
					this.panUserDetails.labName.setEnabled(true);
					this.panUserDetails.tfName.setText(userInfo.getName());
					this.panUserDetails.tfName.setEditable(true);
					
					this.panUserDetails.labEmail.setEnabled(true);
					this.panUserDetails.tfEmail.setText(userInfo.getCustomData().get(ClientServerConstants.USER_EMAIL_KEY));
					this.panUserDetails.tfEmail.setEditable(true);
					
					this.panUserDetails.labPassword1.setEnabled(true);
					this.panUserDetails.tfPassword1.setText("");
					this.panUserDetails.tfPassword1.setEditable(true);
					
					this.panUserDetails.labPassword2.setEnabled(true);
					this.panUserDetails.tfPassword2.setText("");
					this.panUserDetails.tfPassword2.setEditable(true);
					
					this.panUserDetails.cbCredentials.setEnabled(true);
					this.panUserDetails.cbCredentials.setSelected(true);
					
					this.panUserDetails.cbUserActive.setEnabled(false);
					this.panUserDetails.cbUserActive.setSelected(userInfo.isActive());
					
					this.butAdd.setEnabled(true && serverCredentials.adminCredentialsSelected != null);
					this.butDelete.setEnabled(true);
					this.butSubmit.setEnabled(true);
					
					this.butServerLogDownload.setEnabled(true);
					this.butServerLogLevelChange.setEnabled(true);
					this.butShutdown.setEnabled(true);
					this.comboServerLogLevel.setEnabled(true);
					
					break;
			}		
		}
		
		private void shutdownServer()
		{
			MessageBoxResult dialogResult = MessageBox.showOkCancel(
					this,
					VegaResources.ShutdownServerQuestion(false),
					VegaResources.ShutdownServer(false));
			
			if (dialogResult == MessageBoxResult.OK)
			{
				dialogResult = MessageBox.showYesNo(
						this,
					    VegaResources.AreYouSure(false),
					    VegaResources.ShutdownServer(false));
				
				if (dialogResult == MessageBoxResult.YES)
				{
					ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
					VegaClient client = new VegaClient(clientConfiguration, false, null);
					
					Vega.showWaitCursor(this);
					ResponseInfo info = client.shutdownServer();
					Vega.showDefaultCursor(this);
					
					if (info.isSuccess())
						MessageBox.showInformation(
								this,
							    VegaResources.ServerShutdownSuccessfully(false),
							    VegaResources.ShutdownServer(false));
					else
						Vega.showServerError(this, info);
				}
			}
		}
		
		private void submitChangeUser(String userId)
		{
			if (!this.submitCheckEmail(userId)) return;
			
			if(MessageBox.showOkCancel(
					this,
					VegaResources.UpdateUserQuestion(false, userId),
				    VegaResources.Users(false)) != MessageBoxResult.OK) return;
			
			this.submitExecute(false, false);
		}
		
		private boolean submitCheckEmail(String userId)
		{
			String eMail = this.panUserDetails.tfEmail.getText().trim();
			
			if (!Pattern.matches(EmailToolkit.EMAIL_REGEX_PATTERN, eMail))
			{
				MessageBox.showError(
						parent,
						VegaResources.EmailAddressInvalid(
								false, 
								userId),
						VegaResources.Error(false));
				return false;
			}
			
			return true;
		}
		
		private boolean submitCheckPasswords()
		{
			if (!this.panUserDetails.tfPassword1.arePasswordsEqual(this.panUserDetails.tfPassword2))
			{
				MessageBox.showError(
						this,
						VegaResources.PasswordsNotEqual(false),
					    VegaResources.Users(false));
				return false;
			}
			
			if (this.panUserDetails.tfPassword1.getPassword().length < 3)
			{
				MessageBox.showError(
						this,
						VegaResources.ActivationPasswordTooShort(false),
					    VegaResources.Users(false));
				return false;
			}
			
			return true;
		}
		
		private void submitExecute(boolean create, boolean renew)
		{
			Hashtable<String,String> customData = new Hashtable<String,String>();
			customData.put(ClientServerConstants.USER_EMAIL_KEY, this.panUserDetails.tfEmail.getText().trim());
			
			PayloadRequestMessageChangeUser reqMsgChangeUser = new PayloadRequestMessageChangeUser(
					this.panUserDetails.tfUserId.getText().trim(), 
					customData,
					this.panUserDetails.tfName.getText().trim(), 
					create,
					renew);
			
			ClientConfiguration clientConfiguration = serverCredentials.getCredentials(serverCredentials.adminCredentialsSelected);
			VegaClient client = new VegaClient(clientConfiguration, false, null);
			
			Vega.showWaitCursor(this);
			Response<PayloadResponseMessageChangeUser> response = client.changeUser(reqMsgChangeUser);
			Vega.showDefaultCursor(this);
			
			if (!response.getResponseInfo().isSuccess())
			{
				Vega.showServerError(parent, response.getResponseInfo());
				return;
			}
			
			if (renew)
			{
				int result = MessageBox.showCustomButtons(
						parent, 
						VegaResources.SendActivationDataQuestion(false, reqMsgChangeUser.getUserId()), 
						VegaResources.Users(false), 
						new String[] {
								VegaResources.Email(false),
								VegaResources.TextFile(false),
								VegaResources.CopyToClipboard(false)
						});
				
				ResponseMessageChangeUser activationData = ResponseMessageChangeUser.GetInstance(response.getPayload());
				
				if (result == 0)
				{
					EmailToolkit.launchEmailClient(
							parent,
							customData.get(ClientServerConstants.USER_EMAIL_KEY), 
							VegaResources.EmailSubjectNewUser(false, reqMsgChangeUser.getUserId()), 
							VegaResources.NewUserEmailBody(
									false, 
									reqMsgChangeUser.getName(), 
									reqMsgChangeUser.getUserId(), 
									clientConfiguration.getUrl(), 
									Integer.toString(clientConfiguration.getPort()),
									response.getResponseInfo().getServerBuild()), 
							VegaUtils.toBytes(this.panUserDetails.tfPassword1.getPassword()),
							activationData);
				}
				else if (result == 1)
				{
					JFileChooser fc = new JFileChooser();
					
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setDialogTitle(VegaResources.AuthenticationFile(false));
					fc.setCurrentDirectory(
							selectedDirectoryActivationFile != null ?
									selectedDirectoryActivationFile :
									new File(CommonUtils.getHomeDir()));
					
					String filename = getCredentialFileName(
															reqMsgChangeUser.getUserId(),
															clientConfiguration.getUrl(),
															clientConfiguration.getPort())
									+ "_activation.txt";
					
					fc.setSelectedFile(new File(filename));
					
					int returnVal = fc.showSaveDialog(this);
					
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file = fc.getSelectedFile();
						selectedDirectoryActivationFile = fc.getCurrentDirectory();
					
						String base64 = EmailToolkit.getEmailObjectPayload(
								VegaUtils.toBytes(this.panUserDetails.tfPassword1.getPassword()), 
								activationData);
						
						String fileContent = VegaResources.CredentialsTextFileInstructions(
								false, 
								reqMsgChangeUser.getName(), 
								reqMsgChangeUser.getUserId(), 
								clientConfiguration.getUrl(), 
								Integer.toString(clientConfiguration.getPort()),
								response.getResponseInfo().getServerBuild(),
								base64);
						
						try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath())))
						{
							bw.write(fileContent);
							
							MessageBox.showInformation(
									parent, 
									VegaResources.SaveFileSuccess(false),
									VegaResources.Success(false));
							
						} catch (IOException e)
						{
							MessageBox.showError(
									parent, 
									VegaResources.ActionNotPossible(false, e.getMessage()),
									VegaResources.Success(false));
						}
					}
				}
				else
				{
					String base64 = 
							EmailToolkit.getEmailObjectPayload(
									VegaUtils.toBytes(this.panUserDetails.tfPassword1.getPassword()), 
									activationData);
					
					try
					{
						StringSelection selection = new StringSelection(base64);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(selection, selection);
						
						MessageBox.showInformation(
								parent, 
								VegaResources.CopiedToClipboard(false),
								VegaResources.Success(false));
					}
					catch (Exception e)
					{
						MessageBox.showError(
								parent, 
								VegaResources.ActionNotPossible(false, e.getMessage()),
								VegaResources.Success(false));
					}
				}
			}
			else
			{
				MessageBox.showInformation(
						parent, 
						VegaResources.UserUpdated(false, reqMsgChangeUser.getUserId()), 
						VegaResources.Users(false));
			}
			
			ListItem selectedItemBefore = this.listServerUsers.getSelectedListItem();
			this.butLoadServerData.doClick();
			
			if (selectedItemBefore != null)
			{
				this.listServerUsers.setSelectedValue(selectedItemBefore.getDisplayString());
				this.setControlsEnabled();
			}
		}
		
		private void submitNewUser(String userId)
		{
			if (!this.submitCheckPasswords()) return;
			if (!this.submitCheckEmail(userId)) return;
			
			if (MessageBox.showOkCancel(
					this,
					VegaResources.CreateUserQuestion(false, userId),
				    VegaResources.Users(false)) != MessageBoxResult.OK) return;
			
			this.submitExecute(true, true);
		}
		
		private void submitRenewCredentials(String userId)
		{
			if (!this.submitCheckPasswords()) return;
			if (!this.submitCheckEmail(userId)) return;
			
			if (MessageBox.showOkCancel(
					this,
					VegaResources.RenewUserCredentialsQuestion(false, userId),
				    VegaResources.Users(false)) != MessageBoxResult.OK) return;
			
			this.submitExecute(false, true);
		}
		
		private void userListClearSelection()
		{
			this.listServerUsers.clearSelection();
		}

		private class PanelUserData extends Panel implements ICheckBoxListener
		{
			private CheckBox cbCredentials;
			private CheckBox cbUserActive;
			private Label labEmail;
			private Label labName;
			private Label labPassword1;
			private Label labPassword2;
			private Label labUserId;
			private PanelUserDataMode mode;
			private TextField tfEmail;
			private TextField tfName;
			private PasswordField tfPassword1;
			private PasswordField tfPassword2;
			
			private TextField tfUserId;
			
			public PanelUserData(PanelUserDataMode mode)
			{
				super(new GridBagLayout());
				
				this.mode = mode;
				
				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5, 5, 5, 5);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0.5;
				
				c.gridx = 0; c.gridy = 0;
				this.labUserId = new Label(VegaResources.UserId(false)); 
				this.add(this.labUserId, c);
				
				c.gridx = 1; c.gridy = 0;
				this.tfUserId = new TextField("", Player.PLAYER_NAME_REGEX_PATTERN, 30, Player.PLAYER_NAME_LENGTH_MAX, null);
				this.add(this.tfUserId, c);
				
				c.gridx = 0; c.gridy = 1;
				this.labName = new Label(VegaResources.Name(false));
				this.add(this.labName, c);
				
				c.gridx = 1; c.gridy = 1;
				this.tfName = new TextField(30);
				this.add(this.tfName, c);
				
				c.gridx = 0; c.gridy = 2;
				this.labEmail = new Label(VegaResources.EmailAddress(false));
				this.add(this.labEmail, c);
				
				c.gridx = 1; c.gridy = 2;
				this.tfEmail = new TextField(30);
				this.add(this.tfEmail, c);
				
				c.gridx = 0; c.gridy = 3;
				this.labPassword1 = new Label(VegaResources.ActivationPassword(false));
				this.add(this.labPassword1, c);
				
				c.gridx = 1; c.gridy = 3;
				this.tfPassword1 = new PasswordField("");
				this.tfPassword1.setColumns(30);
				this.add(this.tfPassword1, c);
				
				c.gridx = 0; c.gridy = 4;
				this.labPassword2 = new Label(VegaResources.RepeatPassword(false));
				this.add(this.labPassword2, c);
				
				c.gridx = 1; c.gridy = 4;
				this.tfPassword2 = new PasswordField("");
				this.tfPassword2.setColumns(30);
				this.add(this.tfPassword2, c);
				
				c.gridx = 1; c.gridy = 5;
				this.cbCredentials = new CheckBox(VegaResources.RenewCredentials(false), false, this);
				this.add(this.cbCredentials, c);

				c.gridx = 1; c.gridy = 6;
				this.cbUserActive = new CheckBox(VegaResources.UserIsActive(false), false, null);
				this.add(this.cbUserActive, c);
			}
			
			@Override
			public void checkBoxValueChanged(CheckBox source, boolean newValue)
			{
				if (newValue == true)
				{
					this.setMode(PanelUserDataMode.RenewCredentials);
					setControlsEnabled();
				}
				else if (this.mode == PanelUserDataMode.RenewCredentials)
				{
					this.setMode(PanelUserDataMode.ChangeUser);
					setControlsEnabled();
				}
			}

			public void setMode(PanelUserDataMode mode)
			{
				this.mode = mode;
			}
			
			private void clearData()
			{
				this.setMode(PanelUserDataMode.NoDataFromServer);
				setControlsEnabled();
			}
		}

	}
	
	private enum PanelUserDataMode 
	{
		ChangeUser,
		NewUser,
		NoDataFromServer,
		NoUserSelected,
		RenewCredentials
	}

	private class UsersPanel extends Panel implements IButtonListener, IListListener, ActionListener, IComboBoxListener, ICheckBoxListener
	{
		private Button butAdd;
		private Button butDelete;
		private CheckBox cbActivate;
		private ComboBox comboCredentialsUser;
		private ArrayList<ListItem> comboCredentialsUserModel;
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
			
			this.popupMenuItemUser = new JMenuItem (VegaResources.InactiveUserFromClipboard(false));
		    this.popupMenuItemUser.addActionListener(this);
		    popupMenu.add (this.popupMenuItemUser);
		    
		    this.popupMenuItemAdmin = new JMenuItem (VegaResources.ActiveUserFromFile(false));
		    this.popupMenuItemAdmin.addActionListener(this);
		    popupMenu.add (this.popupMenuItemAdmin);
		    
		    PanelWithInsets panActivate = new PanelWithInsets(new FlowLayout(FlowLayout.LEFT));
			
			this.cbActivate = new CheckBox(
					VegaResources.ConnectAsPlayerWithCredentials(false), 
					serverCredentials.connectionActive, 
					this);
			panActivate.addToInnerPanel(this.cbActivate);
			
			panActivate.addToInnerPanel(new JSeparator());
			
			this.comboCredentialsUserModel = new ArrayList<ListItem>();
			this.comboCredentialsUser = new ComboBox(this.comboCredentialsUserModel, 40, null, this);
			panActivate.addToInnerPanel(this.comboCredentialsUser);
			
			this.add(panActivate, BorderLayout.NORTH);
			
			PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 5));
			
			this.createListUsersModel();
			this.listUsers = new List(this, this.listUsersModel);
			this.listUsers.setPreferredSize(new Dimension(300, 200));
			panUsersList.addToInnerPanel(this.listUsers, BorderLayout.CENTER);
			
			Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
			
			this.butAdd = new Button("+", this);
			this.butAdd.setToolTipText(VegaResources.AddCredentials(false));
			
			panUsersListButtons.add(this.butAdd);
			
			this.butDelete = new Button("-", this);
			this.butDelete.setToolTipText(VegaResources.DeleteCredentials(false));
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
				UUID credentialsKey = (UUID)selectedListItem.getHandle();
				serverCredentials.userCredentialsSelected = credentialsKey;
				int index = this.getListIndexByCredentialsKey(credentialsKey);
				
				if (index >= 0)
				{
					this.listUsers.setSelectedIndex(index);
					this.listItemSelected(this.listUsers, null, index, 1);
				}
			}
		}
		
		@Override
		public void comboBoxItemSelected(ComboBox source, String selectedValue)
		{
		}
		
		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			this.setCredentialsPanelValues();
		}
		
		@Override
		public int[] sortListItems(ArrayList<ListItem> listItems)
		{
			return null;
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
			this.comboCredentialsUserModel = new ArrayList<ListItem>();
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
					this.comboCredentialsUserModel.add(
							new ListItem(
									ServerCredentials.getCredentialsDisplayName(clientConfiguration),
									credentialKey));
				}
			}
			
			Collections.sort(this.listUsersModel, new ListItem());
			ListItem.renameDuplicateDisplayStrings(this.listUsersModel);
			
			Collections.sort(this.comboCredentialsUserModel, new ListItem());
			ListItem.renameDuplicateDisplayStrings(this.comboCredentialsUserModel);
			this.comboCredentialsUser.setItems(this.comboCredentialsUserModel);
			
			if (!this.comboCredentialsUser.setSelectedListItemByHandle(serverCredentials.userCredentialsSelected))
			{
				if (this.comboCredentialsUserModel.size() > 0)
				{
					ListItem selectedListItem = this.comboCredentialsUserModel.get(0);
					this.comboCredentialsUser.setSelectedListItemByHandle(selectedListItem.getHandle());
					serverCredentials.userCredentialsSelected = (UUID)selectedListItem.getHandle();
					this.cbActivate.setEnabled(true);
				}
				else
				{
					this.cbActivate.setSelected(false);
					this.cbActivate.setEnabled(false);
					serverCredentials.userCredentialsSelected = null;
					serverCredentials.connectionActive = false;
				}
			}
			
			Collections.sort(panAdmin.comboCredentialsAdminModel, new ListItem());
			ListItem.renameDuplicateDisplayStrings(panAdmin.comboCredentialsAdminModel);
			panAdmin.comboCredentialsAdmin.setItems(panAdmin.comboCredentialsAdminModel);
			
			if (!panAdmin.comboCredentialsAdmin.setSelectedListItemByHandle(serverCredentials.adminCredentialsSelected))
			{
				if (panAdmin.comboCredentialsAdminModel.size() > 0)
				{
					ListItem selectedListItem = panAdmin.comboCredentialsAdminModel.get(0);
					panAdmin.comboCredentialsAdmin.setSelectedListItemByHandle(selectedListItem.getHandle());
					serverCredentials.adminCredentialsSelected = (UUID)selectedListItem.getHandle();
					panAdmin.butLoadServerData.setEnabled(true);
				}
				else
				{
					serverCredentials.adminCredentialsSelected = null;
					panAdmin.butLoadServerData.setEnabled(false);
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
										VegaResources.DeleteCredentialsAYS(
												false, 
												ServerCredentials.getCredentialsDisplayName(clientConfiguration)),
										VegaResources.DeleteCredentials(false));
			
			if (result != MessageBoxResult.YES) return;
			
			serverCredentials.deleteCredentials((UUID)listItem.getHandle());
			
			if (serverCredentials.adminCredentialsSelected != null &&
					(UUID)listItem.getHandle() == serverCredentials.adminCredentialsSelected)
			{
				panAdmin.clearData();
			}

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
							
				Vega.showWaitCursor(parent);
				Tuple<ClientConfiguration, ResponseInfo> tuple = VegaClient.activateUser(
						userActivationData,
						VegaResources.getLocale(),
						Game.BUILD);
				Vega.showDefaultCursor(parent);
									
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
}
