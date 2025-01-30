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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import common.VegaResources;
import common.CommonUtils;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class VegaDisplayServerSettingsJDialog extends Dialog implements IButtonListener, ICheckBoxListener
{
	public String myIpAddress;
	private Button butClose;
	private Button butGetIp;
	
	private Button butRefreshClients;
	
	private CheckBox cbInactiveWhileEnterMoves;
    private CheckBox cbServerEnabled;
	private boolean inactiveWhileEnterMoves;
	
	private List listClients;
	
	private Vega parent;
	
	private VegaDisplayFunctions serverFunctions;
	private TextField tfIpAddress;
	
	VegaDisplayServerSettingsJDialog(
			Vega parent,
			String myIpAddress,
			VegaDisplayFunctions serverFunctions)
	{
		super (parent, VegaResources.Terminalserver(false), new BorderLayout());
		
		this.myIpAddress = myIpAddress == null || myIpAddress.equals("") ?
				serverFunctions.getMyIpAddress() : myIpAddress;
		
		this.serverFunctions = serverFunctions;
		this.parent = parent;
		this.inactiveWhileEnterMoves = parent.areClientsInactiveWhileEnterMoves();
		
		Panel panBase = new Panel(new BorderLayout(10,10));

		// ---------------
		Panel panMain = new Panel(new BorderLayout(20,10));
		
		// ---------------
		Panel panSettings = new Panel(VegaResources.Terminalserver(false), new GridBagLayout());
		
		GridBagConstraints cPanSettings = new GridBagConstraints();
		
		cPanSettings.insets = new Insets(5, 5, 5, 5);
		cPanSettings.fill = GridBagConstraints.HORIZONTAL;
		cPanSettings.weightx = 0.5;
		cPanSettings.weighty = 0.5;
		
		cPanSettings.gridx = 0; cPanSettings.gridy = 0; cPanSettings.gridwidth = 3;
		this.cbServerEnabled = new CheckBox(VegaResources.ActivateServer(false), serverFunctions.isServerEnabled(), this);
		panSettings.add(this.cbServerEnabled, cPanSettings);

		cPanSettings.gridx = 0; cPanSettings.gridy = 1; cPanSettings.gridwidth = 3;
		this.cbInactiveWhileEnterMoves = new CheckBox(
				VegaResources.VegaDisplaysPassive(false), 
				this.inactiveWhileEnterMoves, 
				this);
		panSettings.add(this.cbInactiveWhileEnterMoves, cPanSettings);

		cPanSettings.gridx = 0; cPanSettings.gridy = 2; cPanSettings.gridwidth = 1;
		panSettings.add(new Label(VegaResources.ServerIp(false)), cPanSettings);
		
		cPanSettings.gridx = 1; cPanSettings.gridy = 2; cPanSettings.gridwidth = 1;
		this.tfIpAddress = new TextField(18);
		this.tfIpAddress.setText(this.myIpAddress);
		panSettings.add(this.tfIpAddress, cPanSettings);
		
		cPanSettings.gridx = 2; cPanSettings.gridy = 2; cPanSettings.gridwidth = 1;
		this.butGetIp = new Button(VegaResources.GetIp(false) , this);
		panSettings.add(this.butGetIp, cPanSettings);
		
		cPanSettings.gridx = 0; cPanSettings.gridy = 3; cPanSettings.gridwidth = 1; 
		panSettings.add(new Label(VegaResources.SecurityCode(false)), cPanSettings);
		
		cPanSettings.gridx = 1; cPanSettings.gridy = 3; cPanSettings.gridwidth = 1; 
		TextField tfClientCode = new TextField(serverFunctions.getClientCode(), "", 0, -1, null);
		tfClientCode.setEnabled(false);
		panSettings.add(tfClientCode, cPanSettings);
		
		panMain.add(panSettings, BorderLayout.NORTH);
		
		// ---------------
		Panel panClients = new Panel(VegaResources.ConnectedVegaDisplayClients(false), new GridBagLayout());
		
		GridBagConstraints cPanClients = new GridBagConstraints();
		
		cPanClients.insets = new Insets(5, 5, 5, 5);
		cPanClients.fill = GridBagConstraints.HORIZONTAL;
		cPanClients.weightx = 0.5;
		cPanClients.weighty = 0.5;
		
		cPanClients.gridx = 0; cPanClients.gridy = 0; cPanClients.gridwidth = 3;   
		this.listClients = new List(new ArrayList<String>(), null);
		this.listClients.setPreferredSize(new Dimension(300, 150));
		
		this.updateClientList();
		
		panClients.add(this.listClients, cPanClients);
		
		cPanClients.gridx = 4; cPanClients.gridy = 0; cPanClients.gridwidth = 1;   
		cPanClients.anchor = GridBagConstraints.PAGE_START;
		this.butRefreshClients = new Button(VegaResources.Refresh(false), this);
		panClients.add(this.butRefreshClients, cPanClients);
		
		panMain.add(panClients, BorderLayout.CENTER);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butClose = new Button(VegaResources.Close(false), this);
		this.setDefaultButton(this.butClose);
		panButtons.add(this.butClose);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		GridBagConstraints cPanShell = new GridBagConstraints();
		cPanShell.insets = new Insets(10, 10, 10, 10);
		cPanShell.fill = GridBagConstraints.HORIZONTAL;
		cPanShell.gridx = 0;
		cPanShell.gridy = 0;
		
		this.addToInnerPanel(panBase, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(parent);
		
		this.setControlsEnabled();
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butClose)
		{
			this.close();
		}
		else if (source == this.butRefreshClients)
		{
			this.updateClientList();
		}
		else if (source == this.butGetIp)
		{
			this.tfIpAddress.setText(CommonUtils.getMyIPAddress());
		}
	}
	
	@Override
	public void checkBoxValueChanged(CheckBox source, boolean newValue)
	{
		if (source == this.cbServerEnabled)
		{
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			if (this.cbServerEnabled.isSelected())
			{
				this.serverFunctions.setMyIpAddress(this.tfIpAddress.getText());
				this.serverFunctions.startServer(this.parent);
			}
			else
			{
				this.serverFunctions.stopServer(this.parent);
			}
			
			this.setCursor(Cursor.getDefaultCursor());
			this.setControlsEnabled();
		}
		else if (source == this.cbInactiveWhileEnterMoves)
		{
			this.inactiveWhileEnterMoves = this.cbInactiveWhileEnterMoves.isSelected();
			this.parent.setClientsInactiveWhileEnterMoves(this.inactiveWhileEnterMoves);
		}
	}
	
	protected void close()
	{
		this.myIpAddress = this.tfIpAddress.getText();
		super.close();
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}

	private void setControlsEnabled()
	{
		this.cbInactiveWhileEnterMoves.setEnabled(!this.cbServerEnabled.isSelected());
	}

	private void updateClientList()
	{
		ArrayList<String> clientNames = new ArrayList<String>(); 
		
		Object[] registeredClients = this.serverFunctions.getRegisteredClients();
		
		for (Object clientObj: registeredClients)
		{
			VegaDisplayFunctions.ClientScreenDisplayContentUpdater client =
					(VegaDisplayFunctions.ClientScreenDisplayContentUpdater)clientObj;
			
			String name = client.getClientName().length() == 0 ?
							VegaResources.Unknown(false) :
							client.getClientName();
			clientNames.add(name + " (" + client.getClientIp() + ")");
		}
		
		this.listClients.refreshListModel(clientNames);

	}
}
