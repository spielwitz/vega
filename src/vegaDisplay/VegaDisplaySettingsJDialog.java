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

package vegaDisplay;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;

import common.Game;
import common.VegaResources;
import common.CommonUtils;
import commonUi.MessageBox;
import commonUi.IServerMethods;
import commonUi.IVegaDisplayMethods;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.PasswordField;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class VegaDisplaySettingsJDialog extends Dialog implements IButtonListener
{
	private Button butClose;
	private Button butConnect;
	private Button butGetMyIpAddress;
	private VegaDisplayConfiguration config;
	private Label labStatus;
	private VegaDisplay parent;
	private PasswordField tfClientCode;
	private TextField tfMyIpAddress;

	private TextField tfMyName;
	
	private TextField tfServerIpAddress;
	
	VegaDisplaySettingsJDialog(
			VegaDisplay parent,
			String title,
			boolean modal,
			VegaDisplayConfiguration config)
	{
		super (parent, title, new BorderLayout());
		
		this.config = config;
		this.parent = parent;
				
		Panel panBase = new Panel(new BorderLayout(10,10));
		// ---------------
		Panel panMain = new Panel(new BorderLayout(20,10));
		
		// ---------------
		Panel panServer = new Panel(VegaResources.VegaDisplayServer(false), new GridBagLayout());

		GridBagConstraints cPanServer = new GridBagConstraints();
		cPanServer.insets = new Insets(5, 5, 5, 5);
		cPanServer.fill = GridBagConstraints.HORIZONTAL;
		cPanServer.weightx = 0.5;
		cPanServer.weighty = 0.5;
		
		cPanServer.gridx = 0; cPanServer.gridy = 0; 
		panServer.add(new Label(VegaResources.MyIp(false)), cPanServer);

		Panel panIpAddresses = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanIpAddresses = new GridBagConstraints();
		cPanIpAddresses.insets = new Insets(0, 5, 0, 5);
		cPanIpAddresses.fill = GridBagConstraints.HORIZONTAL;
		cPanIpAddresses.weightx = 0.5;
		cPanIpAddresses.weighty = 0.5;
		
		cPanIpAddresses.gridx = 0; cPanIpAddresses.gridy = 0;
		this.tfMyIpAddress = new TextField(this.config.getMyIpAddress(), null, 0, -1, null);
		this.tfMyIpAddress.setColumns(18);
		panIpAddresses.add(this.tfMyIpAddress, cPanIpAddresses);
		
		cPanIpAddresses.gridx = 1; cPanIpAddresses.gridy = 0;
		this.butGetMyIpAddress = new Button(VegaResources.GetIp(false) , this);
		panIpAddresses.add(this.butGetMyIpAddress, cPanIpAddresses);
		
		panServer.add(panIpAddresses);
		
		cPanServer.gridx = 0; cPanServer.gridy = 1;
		panServer.add(new Label(VegaResources.ServerIp(false)), cPanServer);
		
		cPanServer.gridx = 1; cPanServer.gridy = 1;
		this.tfServerIpAddress = new TextField(this.config.getServerIpAddress(), null, 0, -1, null);
		panServer.add(this.tfServerIpAddress, cPanServer);
		
		cPanServer.gridx = 0; cPanServer.gridy = 2;
		panServer.add(new Label(VegaResources.SecurityCode(false)), cPanServer);
		
		cPanServer.gridx = 1; cPanServer.gridy = 2;
		this.tfClientCode = new PasswordField(this.config.getClientCode());		
		panServer.add(this.tfClientCode, cPanServer);
		
		cPanServer.gridx = 0; cPanServer.gridy = 3;
		panServer.add(new Label(VegaResources.MyName(false)), cPanServer);
		
		cPanServer.gridx = 1; cPanServer.gridy = 3;
		this.tfMyName = new TextField(this.config.getMyName(), null, 0, -1, null);
		panServer.add(this.tfMyName, cPanServer);
		
		panMain.add(panServer, BorderLayout.CENTER);
		
		// ----
		Panel panStatus = new Panel(VegaResources.ConnectionStatus(false), new FlowLayout(FlowLayout.LEFT));
		
		this.labStatus = new Label("");
		panStatus.add(this.labStatus);
		this.updateConnectionStatus();
		
		panMain.add(panStatus, BorderLayout.SOUTH);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butClose = new Button(VegaResources.Close(false), this);
		panButtons.add(this.butClose);
		
		this.butConnect = new Button(VegaResources.Connect(false), this);
		this.setDefaultButton(this.butConnect);
		panButtons.add(this.butConnect);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		this.addToInnerPanel(panBase, BorderLayout.CENTER);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
	}

	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butClose)
		{
			this.updateSettings();
			
			this.close();
		}
		else if (source == this.butGetMyIpAddress)
		{
			this.tfMyIpAddress.setText(CommonUtils.getMyIPAddress());
		}
		else if (source == this.butConnect)
		{
			this.updateSettings();
			//System.setProperty("java.rmi.server.hostname",this.config.getMyIpAddress());
			
			String errorMsg = null;
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			try {
				try {
					LocateRegistry.createRegistry( Registry.REGISTRY_PORT    );
				}
				catch ( RemoteException e ) 
				{}

				if (parent.stub == null)
				{
					parent.stub = (IVegaDisplayMethods) UnicastRemoteObject.exportObject( parent, 0 );
					Registry registry = LocateRegistry.getRegistry(this.config.getMyIpAddress());
					registry.rebind( this.config.getClientId(), parent.stub );
				}
				
				if (!InetAddress.getByName( this.config.getServerIpAddress() ).isReachable( 2000 ))
					throw new Exception(
							VegaResources.ServerNotReached(false,
									this.config.getServerIpAddress()));
				IServerMethods rmiServer;
				Registry registryServer = LocateRegistry.getRegistry(this.config.getServerIpAddress());
				rmiServer = (IServerMethods) registryServer.lookup( CommonUtils.RMI_REGISTRATION_NAME_SERVER );
				
				errorMsg = rmiServer.rmiClientConnectionRequest(
						this.config.getClientId(),
						Game.BUILD,
						this.config.getMyIpAddress(),
						this.config.getClientCode(),
						this.config.getMyName());
				
				if (errorMsg.length() > 0)
					MessageBox.showError(
							this,
							VegaResources.getString(errorMsg),
							VegaResources.Error(false));
				else
					this.parent.connected = true;
			}
			catch (Exception e) {
				this.setCursor(Cursor.getDefaultCursor());
				
				MessageBox.showError(
						this,
						VegaResources.NoConnectionToServer(false, e.getMessage()),
						VegaResources.Error(false));
				
				this.parent.connected = false;
			}
			
			this.setCursor(Cursor.getDefaultCursor());
			
			this.updateConnectionStatus();
		}
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}

	private void updateConnectionStatus()
	{
		boolean authorized = false;
		
		String text = "";
		
		if (this.parent.connected)
		{
			try 
			{
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.config.getServerIpAddress());
				rmiServer = (IServerMethods) registry.lookup( CommonUtils.RMI_REGISTRATION_NAME_SERVER );

				authorized = rmiServer.rmiClientCheckRegistration(this.config.getClientId());
			}
			catch (Exception e)
			{
				text = VegaResources.ConnectionToServerNotEstablished(false);
			}

			if (text.length() == 0)
			{
				if (authorized)
					text = VegaResources.ConnectedWithServer(false, this.config.getServerIpAddress()); 
				else
					text = VegaResources.VegaDisplayNotRegistered(false, this.config.getServerIpAddress()); 
			}
		}
		else
			text = VegaResources.NotConnected(false);
		
		this.labStatus.setText(text);
	}
	
	private void updateSettings()
	{
		this.config.setClientCode(new String(this.tfClientCode.getPassword()));
		this.config.setMyName(this.tfMyName.getText());
		this.config.setMyIpAddress(this.tfMyIpAddress.getText());
		this.config.setServerIpAddress(this.tfServerIpAddress.getText());
	}
}
