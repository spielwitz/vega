package vega;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import common.VegaResources;
import commonUi.DialogWindow;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.ResponseInfo;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IListListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.Panel;
import uiBaseControls.TabbedPane;
import uiBaseControls.TextField;

class ServerCredentialsJDialog extends Dialog implements IButtonListener
{
	private TabbedPane tabpane;
	
	private Button butOk;
	private Button butClose;
	
	private UsersPanel panUsers;
	
	private ServerCredentials credentials;
	
	ServerCredentialsJDialog(
			Vega parent,
			ServerCredentials credentials)
	{
		super(parent, "Server-Zugangsdaten", new BorderLayout());
		
		this.credentials = credentials;
		
		this.tabpane = new TabbedPane();
		
		this.panUsers = new UsersPanel(this);
		this.tabpane.addTab("Users", this.panUsers);
				
		this.addToInnerPanel(tabpane, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butClose = new Button(VegaResources.Close(false), this);
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
	
	@Override
	public void buttonClicked(Button source)
	{
		// TODO Auto-generated method stub
		
	}
	
	private class UsersPanel extends Panel implements IButtonListener, IListListener
	{
		private List listUsers;
		private Button butAdd;
		private Button butDelete;
		private CredentialsPanel panCredentials;
		
		private UsersPanel(Dialog parent)
		{
			super(new BorderLayout());
			
			Panel panUsersList = new Panel(new BorderLayout(10, 10));
			
			this.listUsers = new List(new ArrayList<String>(), this);
			panUsersList.add(this.listUsers, BorderLayout.CENTER);
			
			Panel panUsersListButton = new Panel(new FlowLayout());
			
			this.butAdd = new Button("+", this);
			panUsersListButton.add(this.butAdd);
			
			this.butDelete = new Button("-", this);
			panUsersListButton.add(this.butDelete);
			
			panUsersList.add(panUsersListButton, BorderLayout.SOUTH);
			
			this.add(panUsersList, BorderLayout.WEST);
			
			this.panCredentials = new CredentialsPanel(parent, null);
			this.add(this.panCredentials, BorderLayout.CENTER);
		}

		@Override
		public void buttonClicked(Button source)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	private class CredentialsPanel extends Panel implements IButtonListener
	{
		private Dialog parent;
		
		private TextField tfUrl;
		private TextField tfPort;
		private TextField tfTimeout;
		private TextField tfUserId;
		private TextField tfAdminEmail;
		
		private Button butConnectionTest;
		private Button butWriteAdminEmail;
		
		private ClientConfiguration clientConfig;
		
		private CredentialsPanel(
				Dialog parent,
				ClientConfiguration clientConfig)
		{
			super(new GridBagLayout());
			
			this.parent = parent;
			this.clientConfig = clientConfig;
			
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
			
			c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
			this.add(new Label(VegaResources.ServerUrl(false)), c);
			
			c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
			this.tfUrl = new TextField(textFieldColumns); 
			this.add(this.tfUrl, c);
			
			c.gridx = 3; c.gridy = 1; c.gridwidth = 1;
			this.butConnectionTest = new Button(VegaResources.ConnectionTest(false), this);
			this.add(this.butConnectionTest, c);
			
			c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
			this.add(new Label(VegaResources.ServerPort(false)), c);
			
			c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
			this.tfPort = new TextField("", "[0-9]*", textFieldColumns, 5, null);
			this.add(this.tfPort, c);
			
			c.gridx = 0; c.gridy = 3;
			this.add(new Label(VegaResources.Timeout(false)), c);
			
			c.gridx = 1; c.gridy = 3; c.gridwidth = 2;
			this.tfTimeout = new TextField("", "[0-9]*", textFieldColumns, 8, null);
			this.add(this.tfTimeout, c);
			
			c.gridx = 0; c.gridy = 4; c.gridwidth = 1;
			this.add(new Label(VegaResources.EmailAdmin(false)), c);
			
			c.gridx = 1; c.gridy = 4; c.gridwidth = 2;
			this.tfAdminEmail = new TextField(textFieldColumns);
			this.tfAdminEmail.setEditable(false);
			this.add(this.tfAdminEmail, c);
			
			c.gridx = 3; c.gridy = 4; c.gridwidth = 1;
			this.butWriteAdminEmail = new Button(VegaResources.WriteEmail(false), this);
			this.add(this.butWriteAdminEmail, c);
			
			if (this.clientConfig == null) return;
			
			this.tfUrl.setText(
					clientConfig.getUrl() == null ? VegaResources.NoFileSelected(false): clientConfig.getUrl());
			
			this.tfPort.setText(
					clientConfig.getPort() == 0 ? "" : Integer.toString(clientConfig.getPort()));
			
			this.tfUserId.setText(
					clientConfig.getUserId() == null ? "" : clientConfig.getUserId());		
			
			this.tfAdminEmail.setText(
					clientConfig.getAdminEmail() == null ? "" : clientConfig.getAdminEmail());
			
			this.tfTimeout.setText(
					Integer.toString(clientConfig.getTimeout()));
		}
		
		private ClientConfiguration getClientConfig()
		{
			if (this.clientConfig == null) return null;
			
			this.clientConfig.setUrl(this.tfUrl.getText());
			this.clientConfig.setPort(this.tfPort.getTextInt());
			this.clientConfig.setTimeout(this.tfTimeout.getTextInt());
			
			return this.clientConfig;
		}

		@Override
		public void buttonClicked(Button source)
		{
			if (source == this.butConnectionTest)
			{
				ClientConfiguration testClientConfig = this.getClientConfig();
				
				VegaClient client = new VegaClient(testClientConfig, false, null);
				
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
			else if (source == this.butWriteAdminEmail)
			{
				ClientConfiguration testClientConfig = this.getClientConfig();
				
				EmailToolkit.launchEmailClient(
						parent, 
						testClientConfig.getAdminEmail(), 
						VegaResources.VegaServer(
								false, 
								testClientConfig.getUrl()),
						"", 
						null, 
						null);
			}
		}
	}

	
}
