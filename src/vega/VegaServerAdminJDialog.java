/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import common.Game;
import common.Player;
import common.VegaResources;
import commonServer.ClientServerConstants;
import commonServer.ResponseMessageChangeUser;
import commonServer.ServerUtils;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
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
import spielwitz.biDiServer.User;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.IListListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.Panel;
import uiBaseControls.PasswordField;
import uiBaseControls.TabbedPane;
import uiBaseControls.TextField;

@SuppressWarnings("serial")
class VegaServerAdminJDialog extends Dialog 
			implements IButtonListener, IListListener, WindowListener
{
	static private File selectedDirectory;
	
	private TabbedPane tabpane;
	
	private Button butClose;
	private Button butNewUserOk;
	private Button butPing;
	private Button butAuthBrowse;
	private Button butShutdown;
	private Button butAddUser;
	private Button butDeleteUser;
	private Button butRefreshUserList;
	private Button butServerLogDownload;
	private Button butServerLogLevelChange;
	
	private Button butServerStatusRefresh;
	private TextField tfServerStartDate;
	private TextField tfServerLogSize;
	private TextField tfServerBuild;
	private TextField tfClientBuild;
	private ComboBox comboServerLogLevel;
	private TextField tfAuthUrl;
	private TextField tfAuthPort;
	private TextField tfAuthUserId;
	private TextField tfAuthFile;
	
	private TextField tfTimeout;
	
	private PanelUserData panUsersDetailsInner;
	String serverAdminCredentialsFile;
	
	private ClientConfiguration clientConfigAdmin;
	private List listUsers;

	private Hashtable<String, User> usersOnServer;
	private Component parent;
	private String authUrlBefore;
	private int timeoutBefore;
	
	private int authPortBefore;
	
	VegaServerAdminJDialog(
			Vega parent,
			String serverAdminCredentialsFile)
	{
		super (parent, VegaResources.AdministrateVegaServer(false), new BorderLayout(10, 10));
		
		this.parent = parent;
		this.serverAdminCredentialsFile = serverAdminCredentialsFile;
		this.usersOnServer = new Hashtable<String, User>();
		
		this.clientConfigAdmin = ClientConfiguration.readFromFile(this.serverAdminCredentialsFile); 
				
		if (this.clientConfigAdmin != null)
		{
			this.authUrlBefore = this.clientConfigAdmin.getUrl();
			this.authPortBefore = this.clientConfigAdmin.getPort();
			this.timeoutBefore = this.clientConfigAdmin.getTimeout();
		}
		
		this.addWindowListener(this);
		
		// ---------------
		tabpane = new TabbedPane();
		
		// ---------------
		Panel panUsersOuter = new Panel(new GridBagLayout());
		
		Panel panUsersInner = new Panel(new BorderLayout(10, 10));
		
		Panel panUsersButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		this.butRefreshUserList = new Button(VegaResources.ReloadUserList(false), this);
		panUsersButtons.add(this.butRefreshUserList);
		this.butAddUser = new Button(VegaResources.CreateNewUser(false), this);
		panUsersButtons.add(this.butAddUser);

		this.butNewUserOk = new Button(VegaResources.SubmitChangesToServer(false), this);
		panUsersButtons.add(this.butNewUserOk);
		panUsersInner.add(panUsersButtons, BorderLayout.SOUTH);
		
		Panel panUsersListAndDetails = new Panel(new BorderLayout(10, 10));
		
		Panel panUsersList = new Panel(new BorderLayout(0,10));
		
		this.listUsers = new List(new ArrayList<String>(), this);
		this.listUsers .setPreferredSize(new Dimension(125, 200));
		
		panUsersList.add(this.listUsers, BorderLayout.CENTER);
		
		this.butDeleteUser = new Button(VegaResources.DeleteUser(false), this);		
		panUsersList.add(this.butDeleteUser, BorderLayout.SOUTH);
				
		panUsersListAndDetails.add(panUsersList, BorderLayout.WEST);
		
		Panel panUsersDetailsOuter = new Panel(new BorderLayout());
		
		this.panUsersDetailsInner = new PanelUserData(Mode.NoUserSelected);
		panUsersDetailsOuter.add(this.panUsersDetailsInner, BorderLayout.NORTH);
		
		panUsersListAndDetails.add(panUsersDetailsOuter, BorderLayout.CENTER);
		
		panUsersInner.add(panUsersListAndDetails, BorderLayout.CENTER);
		
		GridBagConstraints cPanOuter = new GridBagConstraints();
		cPanOuter.insets = new Insets(10, 10, 10, 10);
		cPanOuter.fill = GridBagConstraints.HORIZONTAL;
		
		panUsersOuter.add(panUsersInner, cPanOuter);
		
		tabpane.addTab(VegaResources.CreateNewUser(false), panUsersOuter);
		
		// ---------------
		Panel panShutdownOuter = new Panel(new GridBagLayout());
		
		Panel panShutdown = new Panel(new BorderLayout(10, 10));
		
		Panel panServerStatus = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanServerStatus = new GridBagConstraints();
		
		cPanServerStatus.insets = new Insets(5, 5, 5, 5);
		cPanServerStatus.fill = GridBagConstraints.HORIZONTAL;
		cPanServerStatus.weightx = 0.5;
		cPanServerStatus.weighty = 0.5;
		
		cPanServerStatus.gridx = 0;
		cPanServerStatus.gridy = 0;
		panServerStatus.add(new Label(VegaResources.ClientBuild(false)),cPanServerStatus);
		
		cPanServerStatus.gridx = 1;
		this.tfClientBuild = new TextField(Game.BUILD, null, 30, -1, null);
		this.tfClientBuild.setEditable(false);
		panServerStatus.add(this.tfClientBuild, cPanServerStatus);
		
		cPanServerStatus.gridx = 0;
		cPanServerStatus.gridy = 1;
		panServerStatus.add(new Label(VegaResources.ServerBuild(false)), cPanServerStatus);
		
		cPanServerStatus.gridx = 1;
		this.tfServerBuild = new TextField("", null, 30, -1, null);
		this.tfServerBuild.setEditable(false);
		panServerStatus.add(this.tfServerBuild, cPanServerStatus);
		
		cPanServerStatus.gridx = 0;
		cPanServerStatus.gridy = 2;
		panServerStatus.add(new Label(VegaResources.RunningSince(false)), cPanServerStatus);
		
		cPanServerStatus.gridx = 1;
		this.tfServerStartDate = new TextField("", null, 30, -1, null);
		this.tfServerStartDate.setEditable(false);
		panServerStatus.add(this.tfServerStartDate, cPanServerStatus);
		
		cPanServerStatus.gridx = 2;
		this.butShutdown = new Button(VegaResources.ShutdownServer(false), this);
		panServerStatus.add(this.butShutdown, cPanServerStatus);
		
		cPanServerStatus.gridx = 0;
		cPanServerStatus.gridy = 3;
		panServerStatus.add(new Label(VegaResources.LogSize(false)), cPanServerStatus);
		
		cPanServerStatus.gridx = 1;
		this.tfServerLogSize = new TextField("", null, 30, -1, null);
		this.tfServerLogSize.setEditable(false);
		panServerStatus.add(this.tfServerLogSize, cPanServerStatus);
		
		cPanServerStatus.gridx = 2;
		this.butServerLogDownload = new Button(VegaResources.DownloadLog(false), this);
		panServerStatus.add(this.butServerLogDownload, cPanServerStatus);
		
		cPanServerStatus.gridx = 0;
		cPanServerStatus.gridy = 4;
		panServerStatus.add(new Label(VegaResources.LogLevel(false)), cPanServerStatus);
		
		String[] logLevels = new String[LogLevel.values().length];
		int counter = 0;
		for (LogLevel logEventType: LogLevel.values())
		{
			logLevels[counter] = logEventType.toString();
			counter++;
		}
		
		cPanServerStatus.gridx = 1;
		this.comboServerLogLevel = new ComboBox(logLevels, 10, null, null);
		panServerStatus.add(this.comboServerLogLevel, cPanServerStatus);
		
		cPanServerStatus.gridx = 2;
		this.butServerLogLevelChange = new Button(VegaResources.ChangeLogLevel(false), this);
		panServerStatus.add(this.butServerLogLevelChange, cPanServerStatus);
		
		panShutdown.add(panServerStatus, BorderLayout.NORTH);
		
		Panel panServerStatusButtons = new Panel(new FlowLayout());
		
		this.butServerStatusRefresh = new Button(VegaResources.RefreshStatus(false), this);
		panServerStatusButtons.add(this.butServerStatusRefresh);
		
		panShutdown.add(panServerStatusButtons, BorderLayout.CENTER);
		
		panShutdownOuter.add(panShutdown, cPanOuter);
		
		tabpane.addTab(VegaResources.ServerStatus(false), panShutdownOuter);
		
		// ---------------
		Panel panAuthOuter = new Panel(new GridBagLayout());
		
		Panel panAuth = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanAuth = new GridBagConstraints();
		
		cPanAuth.insets = new Insets(5, 5, 5, 5);
		cPanAuth.fill = GridBagConstraints.HORIZONTAL;
		cPanAuth.weightx = 0.5;
		cPanAuth.weighty = 0.5;
		
		int columns = 40;
		
		cPanAuth.gridx = 0;
		cPanAuth.gridy = 0;
		panAuth.add(new Label(VegaResources.File(false)), cPanAuth);
		
		cPanAuth.gridx = 1;
		this.tfAuthFile = new TextField("", null, columns, -1, null);
		this.tfAuthFile.setEditable(false);
		panAuth.add(this.tfAuthFile, cPanAuth);
		
		cPanAuth.gridx = 2;
		this.butAuthBrowse = new Button(VegaResources.Select(false), this);
		panAuth.add(this.butAuthBrowse, cPanAuth);
		
		cPanAuth.gridx = 0;
		cPanAuth.gridy = 1;
		panAuth.add(new Label(VegaResources.ServerUrl(false)), cPanAuth);
		
		cPanAuth.gridx = 1;
		this.tfAuthUrl = new TextField("", null, columns, -1, null);
		panAuth.add(this.tfAuthUrl, cPanAuth);
		
		cPanAuth.gridx = 2;
		this.butPing = new Button(VegaResources.ConnectionTest(false), this);
		panAuth.add(this.butPing, cPanAuth);
		
		cPanAuth.gridx = 0;
		cPanAuth.gridy = 2;
		panAuth.add(new Label(VegaResources.ServerPort(false)), cPanAuth);
		
		cPanAuth.gridx = 1;
		this.tfAuthPort = new TextField("", "[0-9]*", columns, 5, null);
		panAuth.add(this.tfAuthPort, cPanAuth);
		
		cPanAuth.gridx = 0;
		cPanAuth.gridy = 3;
		panAuth.add(new Label(VegaResources.UserId(false)), cPanAuth);
		
		cPanAuth.gridx = 1;
		this.tfAuthUserId = new TextField("", Player.PLAYER_NAME_REGEX_PATTERN, columns, Player.PLAYER_NAME_LENGTH_MAX, null);
		this.tfAuthUserId.setEnabled(false);
		panAuth.add(this.tfAuthUserId, cPanAuth);
		
		cPanAuth.gridx = 0;
		cPanAuth.gridy = 4;
		panAuth.add(new Label(VegaResources.Timeout(false)), cPanAuth);
		
		cPanAuth.gridx = 1;
		this.tfTimeout = new TextField("", "[0-9]*", columns, 8, null);
		panAuth.add(this.tfTimeout, cPanAuth);
		
		this.fillAuthCredentials(this.clientConfigAdmin);
		
		panAuthOuter.add(panAuth, cPanOuter);
		
		tabpane.add(VegaResources.AdminCredentials(false), panAuthOuter);
		
		// ----
		this.addToInnerPanel(tabpane, BorderLayout.CENTER);
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butClose = new Button(VegaResources.Close(false), this);
		panButtons.add(this.butClose);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
				
		if (this.clientConfigAdmin == null)
		{
			tabpane.setSelectedComponent(panAuthOuter);
			tabpane.setEnabledAt(0, false);
			tabpane.setEnabledAt(1, false);
		}
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		
		this.setControlsEnabledUsers();
	}
	
	@Override
	public void buttonClicked(Button source) 
	{
		if (source == this.butClose)
		{
			this.close();
		}
		else if (source == this.butAuthBrowse)
		{
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setDialogTitle(VegaResources.AuthenticationFile(false));
			
			if (this.serverAdminCredentialsFile != null)
			{
				fc.setSelectedFile(new File(this.serverAdminCredentialsFile));
			}
			
			int returnVal = fc.showOpenDialog(this);
			
			if(returnVal != JFileChooser.APPROVE_OPTION)
			{
				return;
			}
			
			File file = fc.getSelectedFile();
				
			ClientConfiguration clientConfigAdminBefore = this.clientConfigAdmin;
			this.clientConfigAdmin = ClientConfiguration.readFromFile(file.getAbsolutePath());
			
			if (this.clientConfigAdmin != null)
			{
				this.serverAdminCredentialsFile = file.getAbsolutePath();
				
				this.fillAuthCredentials(this.clientConfigAdmin);
				
				if (clientConfigAdminBefore == null)
				{
					tabpane.setEnabledAt(0, true);
					tabpane.setEnabledAt(1, true);
				}
			}
			else
				DialogWindow.showError(
						this,
						VegaResources.FileContainsInvalidCredentials(false, file.getAbsoluteFile().toString()),
					    VegaResources.Error(false));

		}
		else if (source == this.butPing &&
				this.tfAuthUrl.getText().length() > 0 &&
				this.tfAuthPort.getTextInt() > 0)
		{
			if (this.clientConfigAdmin != null)
			{
				this.clientConfigAdmin.setUrl(this.tfAuthUrl.getText());
				this.clientConfigAdmin.setPort(this.tfAuthPort.getTextInt());
				this.clientConfigAdmin.setTimeout(this.tfTimeout.getTextInt());
			}
			
			VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
			
			Vega.showWaitCursor(this);
			ResponseInfo info = client.pingServer();
			Vega.showDefaultCursor(this);
			
			if (info.isSuccess())
			{
				DialogWindow.showInformation(
						this, 
						VegaResources.ConnectionSuccessful(false), 
						VegaResources.ConnectionTest(false));
			}
			else
				Vega.showServerError(this, info);
		}
		else if (source == this.butNewUserOk)
		{
			this.usersPostChangesToServer();
		}
		else if (source == this.butShutdown)
		{
			DialogWindowResult dialogResult = DialogWindow.showOkCancel(
					this,
					VegaResources.ShutdownServerQuestion(false),
					VegaResources.ShutdownServer(false));
			
			if (dialogResult == DialogWindowResult.OK)
			{
				dialogResult = DialogWindow.showYesNo(
						this,
					    VegaResources.AreYouSure(false),
					    VegaResources.ShutdownServer(false));
				
				if (dialogResult == DialogWindowResult.YES)
				{
					VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
					
					Vega.showWaitCursor(this);
					ResponseInfo info = client.shutdownServer();
					Vega.showDefaultCursor(this);
					
					if (info.isSuccess())
						DialogWindow.showInformation(
								this,
							    VegaResources.ServerShutdownSuccessfully(false),
							    VegaResources.ShutdownServer(false));
					else
						Vega.showServerError(this, info);
				}
			}
		}
		else if (source == this.butAddUser)
		{
			this.panUsersDetailsInner.setMode(Mode.NewUser);
			this.setControlsEnabledUsers();
		}
		else if (source == this.butRefreshUserList)
		{
			this.refreshUserList();
			this.panUsersDetailsInner.setMode(Mode.NoUserSelected);
			this.setControlsEnabledUsers();			
		}
		else if (source == this.butDeleteUser)
		{
			this.userDelete();
		}
		else if (source == this.butServerStatusRefresh)
		{
			this.serverStatusRefresh();
		}
		else if (source == this.butServerLogDownload)
		{
			this.serverLogDownload();
		}
		else if (source == this.butServerLogLevelChange)
		{
			this.serverLogLevelChange();
		}
	}

	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
	{
		this.panUsersDetailsInner.setMode(Mode.ChangeUser);		
		this.setControlsEnabledUsers();	
	}
	
	@Override
	public void windowActivated(WindowEvent e)
	{
	}	
	
	@Override
	public void windowClosed(WindowEvent e)
	{
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
	}
	
	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}
	
	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}
	
	@Override
	public void windowIconified(WindowEvent e)
	{
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		if (this.clientConfigAdmin != null)
		{
			this.butRefreshUserList.doClick();
		}
	}
	
	protected void close()
	{
		if (this.clientConfigAdmin != null &&
				this.tfAuthPort.getText().length() > 0 &&
				this.authUrlBefore != null &&
				(!this.authUrlBefore.equals(this.tfAuthUrl.getText()) ||
				 this.timeoutBefore != this.tfTimeout.getTextInt() ||
				 this.authPortBefore != this.tfAuthPort.getTextInt()))
		{
			DialogWindowResult dialogResult = DialogWindow.showYesNoCancel(
					this,
					VegaResources.SaveServerCredentialsQuestion(false),
				    VegaResources.SaveServerCredentialsQuestion(false));

			if (dialogResult == DialogWindowResult.YES)
			{
				this.clientConfigAdmin.setUrl(this.tfAuthUrl.getText());
				this.clientConfigAdmin.setPort(this.tfAuthPort.getTextInt());
				this.clientConfigAdmin.setTimeout(this.tfTimeout.getTextInt());
				this.clientConfigAdmin.writeToFile(this.serverAdminCredentialsFile);
			}
			else if (dialogResult == DialogWindowResult.CANCEL)
			{
				return;
			}
		}
		
		super.close();
	}

	private void fillAuthCredentials(ClientConfiguration clientConfigAdmin)
	{
		try
		{
			File f = new File(this.serverAdminCredentialsFile);
			String fileName = f.getName();
			this.tfAuthFile.setText(fileName);
		}
		catch (Exception x)
		{
			this.tfAuthFile.setText("");
		}
		
		this.tfAuthUrl.setText(
				clientConfigAdmin == null || clientConfigAdmin.getUrl() == null ? VegaResources.NoFileSelected(false) : clientConfigAdmin.getUrl());
		
		this.tfAuthPort.setText(
				clientConfigAdmin == null || clientConfigAdmin.getPort() == 0 ? "" : Integer.toString(clientConfigAdmin.getPort()));
		
		this.tfAuthUserId.setText(
				clientConfigAdmin == null || clientConfigAdmin.getUserId() == null ? "" : clientConfigAdmin.getUserId());
		
		this.tfTimeout.setText(
				Integer.toString(
						clientConfigAdmin == null ? 
								Client.CLIENT_SOCKET_TIMEOUT :
									clientConfigAdmin.getTimeout()));
	}
	
	private void refreshUserList()
	{
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
		Vega.showWaitCursor(this);
		Response<PayloadResponseMessageGetUsers> response = client.getUsers();
		Vega.showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			ArrayList<String> userIds = new ArrayList<String>();
			this.usersOnServer.clear();
			
			for (User user: response.getPayload().getUsers())
			{
				userIds.add(user.getId());
				
				this.usersOnServer.put(user.getId(), user);
			}
			
			Collections.sort(userIds);
			
			this.listUsers.refreshListModel(userIds);
		}
		else
			Vega.showServerError(this, response.getResponseInfo());
	}

	private void serverLogDownload()
	{
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
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
				    
				    DialogWindow.showInformation(
				    		this, 
				    		VegaResources.SaveFileSuccess(false), 
				    		VegaResources.SaveLogFile(false));
				}
				catch (Exception x)
				{
					DialogWindow.showError(
							this, 
							VegaResources.SaveLogFileError(false, x.getMessage()), 
							VegaResources.SaveLogFile(false));
				}
			}
			else
				DialogWindow.showInformation(
						this, 
						VegaResources.ServerLogEmpty(false), 
						VegaResources.DownloadLog(false));
		}
		else
			Vega.showServerError(this, response.getResponseInfo());
	}

	private void serverLogLevelChange()
	{
		String newLogLevel = (String) this.comboServerLogLevel.getSelectedItem();
		
		DialogWindowResult dialogResult = DialogWindow.showYesNo(
				this,
				VegaResources.ChangeLogLevelQuestion(false, newLogLevel),
			    VegaResources.ChangeLogLevel(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
		Vega.showWaitCursor(this);
		ResponseInfo info = client.setLogLevel(LogLevel.valueOf(newLogLevel));
		Vega.showDefaultCursor(this);
		
		if (info.isSuccess())
		{
			DialogWindow.showInformation(
					this, 
					VegaResources.LogLevelChanged(false), 
					VegaResources.ChangeLogLevel(false));
		}
		else
			Vega.showServerError(this, info);
	}

	private void serverStatusRefresh()
	{
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
		Vega.showWaitCursor(this);
		Response<PayloadResponseMessageGetServerStatus> response = client.getServerStatus();
		Vega.showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			this.tfServerBuild.setText(response.getPayload().getBuild());
			this.tfServerStartDate.setText(VegaUtils.formatDateTimeString(VegaUtils.convertMillisecondsToString(response.getPayload().getServerStartDate())));
			this.tfServerLogSize.setText(response.getPayload().getLogSizeBytes() + " Bytes");
			this.comboServerLogLevel.setSelectedItem(response.getPayload().getLogLevel().toString());
		}
		else
			Vega.showServerError(this, response.getResponseInfo());
	}

	private void setControlsEnabledUsers()
	{
		Mode mode = this.panUsersDetailsInner.mode;
		String selectedUser = listUsers.getSelectedValue();
		User userInfo = null;
		
		if (selectedUser != null)
			userInfo = this.usersOnServer.get(selectedUser);
		
		switch (mode)
		{
		case NoUserSelected:
				this.userListClearSelection();
			
				this.panUsersDetailsInner.labUserId.setEnabled(false);
				this.panUsersDetailsInner.tfUserId.setText("");
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(false);
				this.panUsersDetailsInner.tfName.setText("");
				this.panUsersDetailsInner.tfName.setEditable(false);
				
				this.panUsersDetailsInner.labEmail.setEnabled(false);
				this.panUsersDetailsInner.tfEmail.setText("");
				this.panUsersDetailsInner.tfEmail.setEditable(false);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(false);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(false);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(false);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(false);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(false);
				this.panUsersDetailsInner.cbCredentials.setSelected(false);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(false);
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(false);
				this.butNewUserOk.setEnabled(false);
				
				break;
		
		case ChangeUser:
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText(userInfo.getId());
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText(userInfo.getName());
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText(userInfo.getCustomData().get(ClientServerConstants.USER_EMAIL_KEY));
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(false);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(false);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(false);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(false);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(true);
				this.panUsersDetailsInner.cbCredentials.setSelected(false);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(userInfo.isActive());
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(true);
				this.butNewUserOk.setEnabled(true);
				
				break;
		
		case NewUser:
				this.userListClearSelection();
				
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText("");
				this.panUsersDetailsInner.tfUserId.setEditable(true);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText("");
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText("");
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(true);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(true);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(true);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(true);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(false);
				this.panUsersDetailsInner.cbCredentials.setSelected(true);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(false);
				
				this.butAddUser.setEnabled(false);
				this.butDeleteUser.setEnabled(false);
				this.butNewUserOk.setEnabled(true);
				
				break;
		
		case RenewCredentials:
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText(userInfo.getId());
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText(userInfo.getName());
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText(userInfo.getCustomData().get(ClientServerConstants.USER_EMAIL_KEY));
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(true);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(true);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(true);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(true);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(true);
				this.panUsersDetailsInner.cbCredentials.setSelected(true);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(userInfo.isActive());
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(true);
				this.butNewUserOk.setEnabled(true);
				
				break;
		}		
	}

	private void userDelete()
	{
		String userId = listUsers.getSelectedValue();
		
		if (userId == null)
			return;
		
		DialogWindowResult dialogResult = DialogWindow.showOkCancel(
				this,
				VegaResources.DeleteUserQuestion(false, userId),
			    VegaResources.Users(false));
		
		if (dialogResult != DialogWindowResult.OK)
			return;
		
		dialogResult = DialogWindow.showYesNo(
				this,
				VegaResources.AreYouSure(false),
			    VegaResources.Users(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
		Vega.showWaitCursor(this);
		ResponseInfo info = client.deleteUser(userId);
		Vega.showDefaultCursor(this);
		
		if (info.isSuccess())
		{
			this.butRefreshUserList.doClick();
			
			DialogWindow.showInformation(
					this, 
					VegaResources.UserDeleted(false, userId), 
					VegaResources.Users(false));
		}
		else
			Vega.showServerError(this, info);
	}

	private void userListClearSelection()
	{
		this.listUsers.clearSelection();
	}

	private void usersPostChangesToServer()
	{
		Mode mode = this.panUsersDetailsInner.mode;
		
		String password1 = null; 
				
		if (mode == Mode.NewUser || mode == Mode.RenewCredentials)
		{	
			password1 = new String(this.panUsersDetailsInner.tfPassword1.getPassword());
			String password2 = new String(this.panUsersDetailsInner.tfPassword2.getPassword());
			
			if (!password1.equals(password2))
			{
				DialogWindow.showError(
						this,
						VegaResources.PasswordsNotEqual(false),
					    VegaResources.Users(false));
				return;
			}
			
			if (password1.length() < 3)
			{
				DialogWindow.showError(
						this,
						VegaResources.ActivationPasswordTooShort(false),
					    VegaResources.Users(false));
				return;
			}
		}
		
		String userId =  this.panUsersDetailsInner.tfUserId.getText().trim();
		String eMail = this.panUsersDetailsInner.tfEmail.getText().trim();
		
		if (!Pattern.matches(EmailToolkit.EMAIL_REGEX_PATTERN, eMail))
		{
			DialogWindow.showError(
					this,
					VegaResources.EmailAddressInvalid(
							false, 
							userId),
					VegaResources.Error(false));
			return;
		}
		
		DialogWindowResult dialogResult = DialogWindowResult.OK;
		
		if (mode == Mode.NewUser)
		{
			dialogResult = DialogWindow.showOkCancel(
					this,
					VegaResources.CreateUserQuestion(false, userId),
				    VegaResources.Users(false));
		}
		else if (mode == Mode.RenewCredentials)
		{
			dialogResult = DialogWindow.showOkCancel(
					this,
					VegaResources.RenewUserCredentialsQuestion(false, userId),
				    VegaResources.Users(false));
		}
		else if (mode == Mode.ChangeUser)
		{
			dialogResult = DialogWindow.showOkCancel(
					this,
					VegaResources.UpdateUserQuestion(false, userId),
				    VegaResources.Users(false));
		}
		else
			return; 
			
		
		if (dialogResult != DialogWindowResult.OK)
			return;
		
		Hashtable<String,String> customData = new Hashtable<String,String>();
		customData.put(ClientServerConstants.USER_EMAIL_KEY, this.panUsersDetailsInner.tfEmail.getText().trim());
		
		PayloadRequestMessageChangeUser reqMsgChangeUser = new PayloadRequestMessageChangeUser(
				this.panUsersDetailsInner.tfUserId.getText().trim(), 
				customData,
				this.panUsersDetailsInner.tfName.getText().trim(), 
				(mode == Mode.NewUser),
				(mode == Mode.NewUser || mode == Mode.RenewCredentials));
		
		VegaClient client = new VegaClient(this.clientConfigAdmin, false, null);
		
		Vega.showWaitCursor(this);
		Response<PayloadResponseMessageChangeUser> response = client.changeUser(reqMsgChangeUser);
		Vega.showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			this.butRefreshUserList.doClick();
			
			if (reqMsgChangeUser.isRenewCredentials())
			{
				int result = DialogWindow.showCustomButtons(
						this, 
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
							this.parent,
							customData.get(ClientServerConstants.USER_EMAIL_KEY), 
							VegaResources.EmailSubjectNewUser(false, reqMsgChangeUser.getUserId()), 
							VegaResources.NewUserEmailBody(
									false, 
									reqMsgChangeUser.getName(), 
									reqMsgChangeUser.getUserId(), 
									this.clientConfigAdmin.getUrl(), 
									Integer.toString(this.clientConfigAdmin.getPort()),
									response.getResponseInfo().getServerBuild()), 
							password1,
							activationData);
				}
				else if (result == 1)
				{
					JFileChooser fc = new JFileChooser();
					
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setDialogTitle(VegaResources.AuthenticationFile(false));
					fc.setCurrentDirectory(
							selectedDirectory != null ?
									selectedDirectory :
									new File(ServerUtils.getHomeFolder()));
					
					String filename = ServerUtils.getCredentialFileName(
															reqMsgChangeUser.getUserId(),
															this.clientConfigAdmin.getUrl(),
															this.clientConfigAdmin.getPort())
									+ "_activation.txt";
					
					fc.setSelectedFile(new File(filename));
					
					int returnVal = fc.showSaveDialog(this);
					
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file = fc.getSelectedFile();
						selectedDirectory = fc.getCurrentDirectory();
					
						String base64 = EmailToolkit.getEmailObjectPayload(password1, activationData);
						
						try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath())))
						{
							bw.write(base64);
							
							DialogWindow.showInformation(
									this, 
									VegaResources.SaveFileSuccess(false),
									VegaResources.Success(false));
						} catch (IOException e)
						{
							DialogWindow.showError(
									this, 
									VegaResources.ActionNotPossible(false, e.getMessage()),
									VegaResources.Success(false));
						}
					}
				}
				else
				{
					String base64 = 
							EmailToolkit.getEmailObjectPayload(password1, activationData);
					
					try
					{
						StringSelection selection = new StringSelection(base64);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(selection, selection);
						
						DialogWindow.showInformation(
								this, 
								VegaResources.CopiedToClipboard(false),
								VegaResources.Success(false));
					}
					catch (Exception e)
					{
						DialogWindow.showError(
								this, 
								VegaResources.ActionNotPossible(false, e.getMessage()),
								VegaResources.Success(false));
					}
				}
			}
			else
			{
				DialogWindow.showInformation(
						this, 
						VegaResources.UserUpdated(false, reqMsgChangeUser.getUserId()), 
						VegaResources.Users(false));
			}
		}	
		else
			Vega.showServerError(this, response.getResponseInfo());
	}

	private enum Mode 
	{
		NoUserSelected,
		ChangeUser,
		NewUser,
		RenewCredentials
	}

	// ----------------------
	private class PanelUserData extends Panel implements ICheckBoxListener
	{
		private TextField tfUserId;
		private TextField tfName;
		private TextField tfEmail;
		private Label labUserId;
		private Label labName;
		private Label labEmail;
		private Label labPassword1;
		private Label labPassword2;
		private PasswordField tfPassword1;
		private PasswordField tfPassword2;
		private CheckBox cbCredentials;
		private CheckBox cbUserActive;
		
		private Mode mode;
		
		public PanelUserData(Mode mode)
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
				this.setMode(Mode.RenewCredentials);
				setControlsEnabledUsers();
			}
			else if (this.mode == Mode.RenewCredentials)
			{
				this.setMode(Mode.ChangeUser);
				setControlsEnabledUsers();
			}
		}

		public void setMode(Mode mode)
		{
			this.mode = mode;
		}
	}
}
