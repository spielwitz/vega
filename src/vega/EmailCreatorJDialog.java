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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JSeparator;

import common.Colors;
import common.Player;
import common.VegaResources;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial")
class EmailCreatorJDialog extends Dialog
			implements 	IButtonListener, ICheckBoxListener
{
	private static final String[] separators = new String[] {";", ","};
	
	private Hashtable<CheckBox, String> checkbox2emailMap;
	private Button butLaunchEmailClient;
	
	private Button butCancel;
	
	private ComboBox comboSeparators;
	private String body;
	
	private String subject;
	String separatorPreset;
	boolean launched = false;
	
	private Component parent;
	
	EmailCreatorJDialog(
			Component parent,
			Player[] players,
			String emailGameHost,
			String separatorPreset,
			String subject,
			String body)
	{
		super (parent, VegaResources.CreateEmail(false), new BorderLayout(0, 10));
		
		this.parent = parent;
		
		this.separatorPreset = separatorPreset == null ? separators[0] : separatorPreset;
		this.body = body;
		this.subject = subject;
		
		Panel panTable = new Panel(new GridBagLayout());
		GridBagConstraints cPanTable = new GridBagConstraints();
		
		cPanTable.insets = new Insets(5, 5, 5, 5);
		cPanTable.weighty = 0.5;
		cPanTable.fill = GridBagConstraints.HORIZONTAL;
		
		checkbox2emailMap = new Hashtable<CheckBox, String>();
		
		int columns = 40;
		
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
		{
			Player player = players[playerIndex];
			String email = player.getEmail();
			
			CheckBox cb = new CheckBox(player.getName(), false, this);
			cb.setForeground(Colors.get(player.getColorIndex()));
			cb.setEnabled(email.length() > 0);
			
			cPanTable.gridx = 0;
			cPanTable.gridy = playerIndex;
			cPanTable.gridwidth = 1;
			panTable.add(cb, cPanTable);
			
			checkbox2emailMap.put(cb, email);
			
			TextField tf = new TextField(
					email.length() > 0 ?
							email :
							VegaResources.EmailAddressUnknown(false),
					null,
					columns,
					-1,
					null);
			tf.setEditable(false);
			
			cPanTable.gridx = 1;
			cPanTable.gridy = playerIndex;
			cPanTable.gridwidth = 3;
			panTable.add(tf, cPanTable);
		}
		
		if (emailGameHost != null && emailGameHost.length() > 0)
		{
			CheckBox cb = new CheckBox(VegaResources.GameHost(false), false, this);
			
			cPanTable.gridx = 0;
			cPanTable.gridy = players.length;
			cPanTable.gridwidth = 1;
			panTable.add(cb, cPanTable);
			
			checkbox2emailMap.put(cb, emailGameHost);
			
			TextField tf = new TextField(
					emailGameHost, 
					null, 
					columns, 
					-1, 
					null);
			tf.setEditable(false);
			
			cPanTable.gridx = 1;
			cPanTable.gridy = players.length;
			cPanTable.gridwidth = 3;
			panTable.add(tf, cPanTable);
		}
		
		this.addToInnerPanel(panTable, BorderLayout.CENTER);

		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		panButtons.add(new Label(VegaResources.AddressSeparator(false)));
		this.comboSeparators = new ComboBox(separators, 2, this.separatorPreset, null);
		panButtons.add(this.comboSeparators);
		
		panButtons.add(new JSeparator());
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.butLaunchEmailClient = new Button(VegaResources.CreateEmail(false), this);
		this.butLaunchEmailClient.setEnabled(false);
		panButtons.add(this.butLaunchEmailClient);
		
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
		if (source == this.butLaunchEmailClient)
		{
			HashSet<String> emails = new HashSet<String>();
			
			for (CheckBox cb: this.checkbox2emailMap.keySet())
			{
				if (cb.isSelected())
					emails.add(this.checkbox2emailMap.get(cb));
			}
			
			this.separatorPreset = (String)this.comboSeparators.getSelectedItem();
			StringBuilder sbEmail = new StringBuilder();
			
			for (String email: emails)
			{
				if (sbEmail.length() > 0)
					sbEmail.append(this.separatorPreset);
				
				sbEmail.append(email);
			}
			
			EmailToolkit.launchEmailClient(
					this.parent,
					sbEmail.toString(), 
					this.subject, 
					this.body, 
					null, 
					null);
			
			this.launched = true;
			this.close();
		}
	}

	@Override
	public void checkBoxValueChanged(CheckBox source, boolean newValue)
	{
		int checkBoxesSelected = 0;
		
		for (CheckBox cb: this.checkbox2emailMap.keySet())
		{
			if (cb.isSelected())
				checkBoxesSelected++;
		}
		
		this.butLaunchEmailClient.setEnabled(checkBoxesSelected > 0);
	}

}
