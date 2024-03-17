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
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import common.Colors;
import common.Player;
import common.VegaResources;
import common.CommonUtils;
import commonUi.DialogWindowResult;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ITextFieldListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class EmailSettingsJDialog extends Dialog implements IButtonListener, ITextFieldListener
{
	private final static int COLUMNS_TEXT_FIELS = 40;
	private final static String EMAIL_SELECT_BUTTON_TEXT = ".";
	
	public DialogWindowResult dlgResult = DialogWindowResult.CANCEL; // NO_UCD (unused code)
	String emailGameHost;
	ArrayList<Player> players;
	private Button butCancel;
	
	private Button butEmailGameHost;
	
	private Button[] butEmailPlayer;
	private Button butOk;
	private CheckBox[] cbEmailEnabled;
	
	private ArrayList<String> emailAdresses;
		
	private TextField tfEmailGameHost;
	
	private TextField[] tfEmailPlayer;
	
	@SuppressWarnings("unchecked") 
	EmailSettingsJDialog(
			Dialog parent,
			String emailGameHost,
			ArrayList<Player> players,
			ArrayList<String> emailAdresses,
			boolean readOnly)
	{
		super (parent, VegaResources.EmailModeSettings(false), new BorderLayout());
		
		this.emailAdresses = emailAdresses;
		this.emailGameHost = (String)CommonUtils.klon(emailGameHost);
		this.players = (ArrayList<Player>)CommonUtils.klon(players);
		
		Panel panUsers = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanUsers = new GridBagConstraints();
		
		cPanUsers.insets = new Insets(5, 5 ,5, 5);
		cPanUsers.fill = GridBagConstraints.HORIZONTAL;
		cPanUsers.weightx = 0.5;
		cPanUsers.weighty = 0.5;
		
		cPanUsers.gridx = 0;
		cPanUsers.gridy = 0;
		cPanUsers.gridwidth = 1;
		
		panUsers.add(new Label(VegaResources.GameHost(false)), cPanUsers);
		
		this.butEmailGameHost = new Button(EMAIL_SELECT_BUTTON_TEXT, this);
		this.butEmailGameHost.setEnabled(!readOnly);
		
		cPanUsers.gridx = 4;
		
		panUsers.add(this.butEmailGameHost, cPanUsers);
		
		this.tfEmailGameHost = new TextField(this.emailGameHost, null, COLUMNS_TEXT_FIELS, -1, this);
		this.tfEmailGameHost.setEditable(!readOnly);
		
		cPanUsers.gridx = 1;
		cPanUsers.gridy = 0;
		cPanUsers.gridwidth = 3;
		
		panUsers.add(this.tfEmailGameHost, cPanUsers);
		
		this.butEmailPlayer = new Button[players.size()];
		this.cbEmailEnabled = new CheckBox[players.size()];
		this.tfEmailPlayer = new TextField[players.size()];
		
		for (int playerIndex = 0; playerIndex < players.size(); playerIndex++)
		{
			Player player = this.players.get(playerIndex);
			
			this.cbEmailEnabled[playerIndex] = new CheckBox(player.getName(), player.isEmailPlayer(), null);
			this.cbEmailEnabled[playerIndex].setForeground(Colors.get(player.getColorIndex()));
			this.cbEmailEnabled[playerIndex].setEnabled(!readOnly);
			
			cPanUsers.gridx = 0;
			cPanUsers.gridy = 1 + playerIndex;
			cPanUsers.gridwidth = 1;
			
			panUsers.add(this.cbEmailEnabled[playerIndex], cPanUsers);
			
			this.butEmailPlayer[playerIndex] = new Button(EMAIL_SELECT_BUTTON_TEXT, this);
			this.butEmailPlayer[playerIndex].setEnabled(!readOnly);
			
			cPanUsers.gridx = 4;
			
			panUsers.add(this.butEmailPlayer[playerIndex], cPanUsers);
			
			this.tfEmailPlayer[playerIndex] = new TextField(player.getEmail(), null, COLUMNS_TEXT_FIELS, -1, this);
			this.tfEmailPlayer[playerIndex].setEditable(!readOnly);	
			
			cPanUsers.gridx = 1;
			cPanUsers.gridy = 1 + playerIndex;;
			cPanUsers.gridwidth = 3;
			
			panUsers.add(this.tfEmailPlayer[playerIndex], cPanUsers);
		}
		
		this.addToInnerPanel(panUsers, BorderLayout.CENTER);
		
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
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
		else if (source == this.butEmailGameHost)
		{
			EmailAddressesJDialog dlg = new EmailAddressesJDialog(this, emailAdresses);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
				this.tfEmailGameHost.setText(emailAdresses.get(dlg.selectedIndex));
		}
		else if (source == this.butOk)
		{
			for (int i = 0; i < this.butEmailPlayer.length; i++)
			{
				this.players.get(i).setEmailPlayer(this.cbEmailEnabled[i].isSelected());
				this.players.get(i).setEmail(this.tfEmailPlayer[i].getText().trim());
			}
			
			this.emailGameHost = this.tfEmailGameHost.getText().trim();
			
			boolean ok = GameParametersJDialog.checkEmailSettings(this, this.emailGameHost, this.players)				;
			
			if (ok)
			{
				this.dlgResult = DialogWindowResult.OK;
				this.close();
			}
		}
		else
		{
			int index = -1;
			
			for (int i = 0; i < this.butEmailPlayer.length; i++)
			{
				if (source == this.butEmailPlayer[i])
				{
					index = i;
					break;
				}
			}
			
			EmailAddressesJDialog dlg = new EmailAddressesJDialog(this, emailAdresses);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
			{
				this.tfEmailPlayer[index].setText(emailAdresses.get(dlg.selectedIndex));
				this.cbEmailEnabled[index].setSelected(true);
			}
		}
	}

	@Override
	public void textChanged(TextField source)
	{
	}

	@Override
	public void textFieldFocusLost(TextField source)
	{
		String a = source.getText().trim();
		
		if (a.length() > 0 && !this.emailAdresses.contains(a) && Pattern.matches(EmailToolkit.EMAIL_REGEX_PATTERN, a))
		{
			this.emailAdresses.add(a);
		}
		
		Collections.sort(this.emailAdresses);		
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
}
