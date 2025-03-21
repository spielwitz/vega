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

import common.VegaResources;
import commonUi.MessageBox;
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
	private VegaDisplayConfiguration config;
	private Label labStatus;
	private VegaDisplay parent;
	private PasswordField tfClientCode;

	private TextField tfMyName;
	
	private TextField tfServerIpAddress;
	private TextField tfServerPort;
	
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
		panServer.add(new Label(VegaResources.ServerIp(false)), cPanServer);
		
		cPanServer.gridx = 1; cPanServer.gridy = 0;
		this.tfServerIpAddress = new TextField(this.config.getServerIpAddress(), null, 0, -1, null);
		panServer.add(this.tfServerIpAddress, cPanServer);

		cPanServer.gridx = 0; cPanServer.gridy = 1; 
		panServer.add(new Label(VegaResources.ServerPort(false)), cPanServer);

		cPanServer.gridx = 1; cPanServer.gridy = 1;
		this.tfServerPort = new TextField(Integer.toString(this.config.getServerPort()), "[0-9]*", 0, 5, null);
		panServer.add(this.tfServerPort, cPanServer);
		
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
		else if (source == this.butConnect)
		{
			this.updateSettings();
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			VegaDisplayClientStartResult result = this.parent.startDisplayClient(config);
			
			this.setCursor(Cursor.getDefaultCursor());
			
			if (result.isSuccess())
			{
				this.close();
			}
			else
			{
				MessageBox.showError(
						this,
						VegaResources.getString(result.getErrorMsg()),
						VegaResources.Error(false));
			}
			
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
		if (this.parent.isDisplayClientEnabled())
		{
			this.labStatus.setText(VegaResources.ConnectedWithServer(false, this.config.getServerIpAddress()));
		}
		else
		{
			this.labStatus.setText(VegaResources.NotConnected(false));
		}
	}
	
	private void updateSettings()
	{
		this.config.setClientCode(new String(this.tfClientCode.getPassword()));
		this.config.setMyName(this.tfMyName.getText());
		this.config.setServerIpAddress(this.tfServerIpAddress.getText());
	}
}
