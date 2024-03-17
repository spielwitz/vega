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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JFileChooser;

import common.Game;
import common.VegaResources;
import commonServer.ResponseMessageChangeUser;
import commonServer.ServerUtils;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import spielwitz.biDiServer.Client;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.PayloadResponseMessageChangeUser;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.Tuple;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class VegaServerCredentialsJDialog extends Dialog implements IButtonListener, ICheckBoxListener
{
	boolean ok = false;
	boolean serverCommunicationEnabled;
	String serverUserCredentialsFile;
	private int authPortBefore;
	private String authUrlBefore;
	private Button butAuthActivate;
	private Button butAuthBrowse;
	private Button butClose;
	private Button butOk;
	private Button butPing;
	private Button butWriteEmail;
	private CheckBox cbServerCommunicationEnabled;
	
	private ClientConfiguration clientConfig;
	
	private TextField tfAdminEmail;
	private TextField tfAuthFile;
	private TextField tfAuthPort;
	private TextField tfAuthUrl;
	private TextField tfAuthUserId;
	private TextField tfTimeout;
	private int timeoutBefore;
	
	VegaServerCredentialsJDialog(
			Vega parent,
			boolean serverCommunicationEnabled,
			String serverUserCredentialsFile)
	{
		super (parent, VegaResources.VegaServerCredentials(false), new BorderLayout());
		
		this.serverCommunicationEnabled = serverCommunicationEnabled;
		this.serverUserCredentialsFile = serverUserCredentialsFile;
		this.clientConfig = ClientConfiguration.readFromFile(this.serverUserCredentialsFile);
		
		if (this.clientConfig != null)
		{
			this.authUrlBefore = this.clientConfig.getUrl();
			this.authPortBefore = this.clientConfig.getPort();
			this.timeoutBefore = this.clientConfig.getTimeout();
		}
		
		Panel panAuth = new Panel(new BorderLayout(10, 10));
		
		Panel panServerCommunicationEnabled = new Panel(
				VegaResources.ServerConnection(false), new GridBagLayout());
		
		GridBagConstraints cPanServerCommunicationEnabled = new GridBagConstraints();
		
		cPanServerCommunicationEnabled.insets = new Insets(5, 5, 5, 5);
		cPanServerCommunicationEnabled.fill = GridBagConstraints.HORIZONTAL;
		cPanServerCommunicationEnabled.weightx = 0.5;
		cPanServerCommunicationEnabled.weighty = 0.5;
		
		cPanServerCommunicationEnabled.gridx = 0;
		cPanServerCommunicationEnabled.gridy = 0;
		this.cbServerCommunicationEnabled = new CheckBox(
				VegaResources.Activate(false),
				this.serverCommunicationEnabled,
				this);
		panServerCommunicationEnabled.add(this.cbServerCommunicationEnabled, cPanServerCommunicationEnabled);
		
		panAuth.add(panServerCommunicationEnabled, BorderLayout.NORTH);
		
		Panel panAuthMain = new Panel(VegaResources.VegaServerCredentials(false), new GridBagLayout());
		
		GridBagConstraints cPanAuthMain = new GridBagConstraints();
		
		cPanAuthMain.insets = new Insets(5, 5, 5, 5);
		cPanAuthMain.fill = GridBagConstraints.HORIZONTAL;
		cPanAuthMain.weightx = 0.5;
		cPanAuthMain.weighty = 0.5;
		int textFieldColumns = 40;
		
		cPanAuthMain.gridx = 0; cPanAuthMain.gridy = 0; cPanAuthMain.gridwidth = 1;
		panAuthMain.add(new Label(VegaResources.File(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1; cPanAuthMain.gridy = 0; cPanAuthMain.gridwidth = 2;
		this.tfAuthFile = new TextField(textFieldColumns);
		this.tfAuthFile.setEditable(false);
		panAuthMain.add(this.tfAuthFile, cPanAuthMain);
		
		cPanAuthMain.gridx = 3; cPanAuthMain.gridy = 0; cPanAuthMain.gridwidth = 1;
		this.butAuthBrowse = new Button(VegaResources.Select(false), this);
		panAuthMain.add(this.butAuthBrowse, cPanAuthMain);
		
		cPanAuthMain.gridx = 0; cPanAuthMain.gridy = 1; cPanAuthMain.gridwidth = 1;
		panAuthMain.add(new Label(VegaResources.ServerUrl(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1; cPanAuthMain.gridy = 1; cPanAuthMain.gridwidth = 2;
		this.tfAuthUrl = new TextField(textFieldColumns); 
		panAuthMain.add(this.tfAuthUrl, cPanAuthMain);
		
		cPanAuthMain.gridx = 3; cPanAuthMain.gridy = 1; cPanAuthMain.gridwidth = 1;
		this.butPing = new Button(VegaResources.ConnectionTest(false), this);
		panAuthMain.add(this.butPing, cPanAuthMain);
		
		cPanAuthMain.gridx = 0; cPanAuthMain.gridy = 2; cPanAuthMain.gridwidth = 1;
		panAuthMain.add(new Label(VegaResources.ServerPort(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1; cPanAuthMain.gridy = 2; cPanAuthMain.gridwidth = 2;
		this.tfAuthPort = new TextField("", "[0-9]*", textFieldColumns, 5, null);
		panAuthMain.add(this.tfAuthPort, cPanAuthMain);
		
		cPanAuthMain.gridx = 0;
		cPanAuthMain.gridy = 3;
		panAuthMain.add(new Label(VegaResources.Timeout(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1;
		this.tfTimeout = new TextField("", "[0-9]*", textFieldColumns, 8, null);
		panAuthMain.add(this.tfTimeout, cPanAuthMain);
		
		cPanAuthMain.gridx = 0; cPanAuthMain.gridy = 4; cPanAuthMain.gridwidth = 1;
		panAuthMain.add(new Label(VegaResources.UserId(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1; cPanAuthMain.gridy = 4; cPanAuthMain.gridwidth = 2;
		this.tfAuthUserId = new TextField(textFieldColumns);
		this.tfAuthUserId.setEditable(false);
		panAuthMain.add(this.tfAuthUserId, cPanAuthMain);
		
		cPanAuthMain.gridx = 0; cPanAuthMain.gridy = 5; cPanAuthMain.gridwidth = 1;
		panAuthMain.add(new Label(VegaResources.EmailAdmin(false)), cPanAuthMain);
		
		cPanAuthMain.gridx = 1; cPanAuthMain.gridy = 5; cPanAuthMain.gridwidth = 2;
		this.tfAdminEmail = new TextField(textFieldColumns);
		this.tfAdminEmail.setEditable(false);
		panAuthMain.add(this.tfAdminEmail, cPanAuthMain);
		
		cPanAuthMain.gridx = 3; cPanAuthMain.gridy = 5; cPanAuthMain.gridwidth = 1;
		this.butWriteEmail = new Button(VegaResources.WriteEmail(false), this);
		panAuthMain.add(this.butWriteEmail, cPanAuthMain);
		
		this.fillAuthCredentials(this.clientConfig);
		
		panAuth.add(panAuthMain, BorderLayout.CENTER);
		
		this.addToInnerPanel(panAuth, BorderLayout.CENTER);
		
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butAuthActivate = new Button(VegaResources.ActivateUser(false), this);
		panButtons.add(this.butAuthActivate);
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butClose = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butClose);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
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
			if (this.clientConfig != null && this.authUrlBefore != null &&
				(!this.authUrlBefore.equals(this.tfAuthUrl.getText()) ||
				 this.timeoutBefore != this.tfTimeout.getTextInt() ||
				 this.authPortBefore != this.tfAuthPort.getTextInt()))
			{
				DialogWindowResult dialogResult = DialogWindow.showYesNoCancel(
						this,
						VegaResources.SaveServerCredentialsQuestion(false),
					    VegaResources.VegaServerCredentials(false));

				if (dialogResult == DialogWindowResult.YES)
				{
					this.clientConfig.setUrl(this.tfAuthUrl.getText());
					this.clientConfig.setPort(this.tfAuthPort.getTextInt());
					this.clientConfig.setTimeout(this.tfTimeout.getTextInt());
					this.clientConfig.writeToFile(this.serverUserCredentialsFile);
				}
				else if (dialogResult == DialogWindowResult.CANCEL)
				{
					return;
				}
			}
			
			this.ok = true;
			this.close();
		}
		else if (source == this.butAuthBrowse)
		{
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setDialogTitle(VegaResources.AuthenticationFile(false));
			
			if (this.serverUserCredentialsFile != null)
			{
				fc.setSelectedFile(new File(this.serverUserCredentialsFile));
			}
			
			int returnVal = fc.showOpenDialog(this);
			
			if(returnVal != JFileChooser.APPROVE_OPTION)
			{
				return;
			}
			
			File file = fc.getSelectedFile();
			
			this.clientConfig = ClientConfiguration.readFromFile(file.getAbsolutePath()); 
			
			if (clientConfig != null)
			{
				this.serverUserCredentialsFile = file.getAbsolutePath();
				this.fillAuthCredentials(this.clientConfig);
				this.authUrlBefore = this.clientConfig.getUrl();
				this.authPortBefore = this.clientConfig.getPort();
			}
			else
				DialogWindow.showError(
					this,
					VegaResources.FileContainsInvalidCredentials(false, file.getAbsolutePath().toString()),
				    VegaResources.Error(false));

		}
		else if (source == this.butPing && this.clientConfig != null)
		{
			this.clientConfig.setUrl(this.tfAuthUrl.getText());
			this.clientConfig.setPort(this.tfAuthPort.getTextInt());
			this.clientConfig.setTimeout(this.tfTimeout.getTextInt());
			
			VegaClient client = new VegaClient(this.clientConfig, false, null);
			
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
			{
				Vega.showServerError(this, info);
			}
		}
		else if (source == this.butAuthActivate)
		{
			ClipboardImportJDialog<ResponseMessageChangeUser> dlg = 
					new ClipboardImportJDialog<ResponseMessageChangeUser>(
							this, ResponseMessageChangeUser.class, true);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == DialogWindowResult.OK)
			{
				ResponseMessageChangeUser newUser = (ResponseMessageChangeUser)dlg.obj;
				
				if (newUser != null)
				{
					DialogWindowResult dialogResult = DialogWindow.showOkCancel(
							this,
							VegaResources.UserActicationQuestion(
									false, 
									newUser.userId, 
									newUser.serverUrl, 
									Integer.toString(newUser.serverPort)),
						    VegaResources.ActivateUser(false));
					
					if (dialogResult != DialogWindowResult.OK)
						return;
					
					PayloadResponseMessageChangeUser userActivationData = 
							new PayloadResponseMessageChangeUser(
									newUser.userId, 
									newUser.activationCode, 
									newUser.serverUrl, 
									newUser.serverPort,
									newUser.adminEmail, 
									newUser.serverPublicKey);
									
					
					String filename = ServerUtils.getCredentialFileName(newUser.userId, newUser.serverUrl, newUser.serverPort);
					
					File file = new File(ServerUtils.getHomeFolder(), filename);
					
					JFileChooser fc = new JFileChooser();
					
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setDialogTitle(VegaResources.AuthenticationFile(false));
					fc.setSelectedFile(file);
					
					int returnVal = fc.showSaveDialog(this);
					
					if(returnVal != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					
					File fileClientInfo = fc.getSelectedFile();
					
					Tuple<ClientConfiguration, ResponseInfo> tuple = VegaClient.activateUser(
							userActivationData,
							VegaResources.getLocale(),
							Game.BUILD);
										
					if (tuple.getE2().isSuccess())
					{
						boolean success = tuple.getE1().writeToFile(fileClientInfo.getAbsolutePath());
						
						if (success)
						{
							DialogWindow.showInformation(
									this,
									VegaResources.UserActivationSuccess(false),
								    VegaResources.ActivateUser(false));
							
							this.serverUserCredentialsFile = fileClientInfo.getAbsolutePath();
							this.clientConfig = tuple.getE1();
							
							this.fillAuthCredentials(this.clientConfig);
						}						
					}
					else
					{
						Vega.showServerError(this, tuple.getE2());
					}
				}
			}
		}
		else if (source == this.butWriteEmail)
		{
			EmailToolkit.launchEmailClient(
					this, 
					this.tfAdminEmail.getText(), 
					VegaResources.VegaServer(false, this.tfAuthUrl.getText()),
					"", 
					null, 
					null);
		}
	}
	
	@Override
	public void checkBoxValueChanged(CheckBox source, boolean newValue)
	{
		if (source == this.cbServerCommunicationEnabled)
		{
			this.serverCommunicationEnabled = newValue;
		}
	}
	
	protected void close()
	{
		super.close();
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}

	private void fillAuthCredentials(ClientConfiguration clientConfig)
	{
		try
		{
			File f = new File(this.serverUserCredentialsFile);
			String fileName = f.getName();
			this.tfAuthFile.setText(fileName);
			this.tfAuthFile.setCaretPosition(0);
		}
		catch (Exception x)
		{
			this.tfAuthFile.setText("");
		}
		
		
		this.tfAuthUrl.setText(
				clientConfig == null || clientConfig.getUrl() == null ? VegaResources.NoFileSelected(false): clientConfig.getUrl());
		
		this.tfAuthPort.setText(
				clientConfig == null || clientConfig.getPort() == 0 ? "" : Integer.toString(clientConfig.getPort()));
		
		this.tfAuthUserId.setText(
				clientConfig == null || clientConfig.getUserId() == null ? "" : clientConfig.getUserId());		
		
		this.tfAdminEmail.setText(
				clientConfig == null || clientConfig.getAdminEmail() == null ? "" : clientConfig.getAdminEmail());
		
		this.tfTimeout.setText(
				Integer.toString(
						clientConfig == null ? 
								Client.CLIENT_SOCKET_TIMEOUT :
								clientConfig.getTimeout()));
	}
}
