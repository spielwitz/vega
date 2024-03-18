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

import common.VegaResources;
import commonUi.MessageBox;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.PasswordField;

@SuppressWarnings("serial")
class ServerCredentialsPasswordJDialog extends Dialog implements IButtonListener
{
	private ServerCredentials serverCredentials;
	
	private Button butCancel;
	private Button butOk;

	private PasswordField tfPassword;
	private PasswordField tfPasswordNew1;
	private PasswordField tfPasswordNew2;
	
	boolean ok;
	
	ServerCredentialsPasswordJDialog(Vega parent, ServerCredentials serverCredentials, ServerCredentialsPasswordJDialogMode mode)
	{
		super(
			parent,
			mode == ServerCredentialsPasswordJDialogMode.ENTER_PASSWORD_FIRST_TIME ?
					"Passwort für Server-Zugangsdaten vergeben" :
						mode == ServerCredentialsPasswordJDialogMode.CHANGE_PASSWORD ?
								"Passwort für Server-Zugangsdaten ändern" :
								"Server-Zugangsdaten entsperren",
			new BorderLayout());
		
		this.serverCredentials = serverCredentials.getClone();
		
		Panel panPasswords = new Panel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		int textFieldColumns = 40;
		int lineCount = 0;
		
		if (mode != ServerCredentialsPasswordJDialogMode.ENTER_PASSWORD_FIRST_TIME)
		{
			c.gridx = 0; c.gridy = lineCount; c.gridwidth = 1;
			panPasswords.add(new Label(
					mode == ServerCredentialsPasswordJDialogMode.CHANGE_PASSWORD ?
							"Altes Passwort" :
								"Passwort"), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPassword = new PasswordField("");
			this.tfPassword.setColumns(textFieldColumns);
			panPasswords.add(this.tfPassword, c);
			
			lineCount++;
		}
		
		if (mode != ServerCredentialsPasswordJDialogMode.UNLOCK_CREDENTIALS)
		{
			c.gridx = 0; c.gridy = lineCount; c.gridwidth = 1;
			panPasswords.add(new Label("Neues Passwort"), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPasswordNew1 = new PasswordField("");
			this.tfPasswordNew1.setColumns(textFieldColumns);
			panPasswords.add(this.tfPasswordNew1, c);
			
			lineCount++;
			
			c.gridx = 0; c.gridy = lineCount; c.gridwidth = 1;
			panPasswords.add(new Label("Neues Passwort (Wiederholung)"), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPasswordNew2 = new PasswordField("");
			this.tfPasswordNew2.setColumns(textFieldColumns);
			panPasswords.add(this.tfPasswordNew2, c);
		}
		
		this.addToInnerPanel(panPasswords, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
	}
	
	ServerCredentials getServerCredentials()
	{
		return serverCredentials;
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
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
			String password = 
					this.tfPassword != null ?
							this.tfPassword.getText() :
							null;
			
			String passwordNew1 = 
					this.tfPasswordNew1 != null ?
							this.tfPasswordNew1.getText() :
							null;
			
			String passwordNew2 = 
					this.tfPasswordNew2 != null ?
							this.tfPasswordNew2.getText() :
							null;
			
			if (password != null)
			{
				if (!this.serverCredentials.unlockCredentials(password))
				{
					MessageBox.showError(
							this, 
							"Das eingegebene Passwort ist falsch", 
							"Passwort falsch");
					return;
				}
			}
			
			if (passwordNew1 != null && passwordNew2 != null)
			{
				if (passwordNew1.length() < ServerCredentials.PASSWORD_MIN_LENGTH ||
					passwordNew2.length() < ServerCredentials.PASSWORD_MIN_LENGTH)
				{
					MessageBox.showError(
							this, 
							"Das neue Passwort muss mindestens " + ServerCredentials.PASSWORD_MIN_LENGTH + " Zeichen lang sein.", 
							"Ungültiges Passwort");
					return;
				}
				
				if (!passwordNew1.equals(passwordNew2))
				{
					MessageBox.showError(
							this, 
							"Bitte geben Sie beidesmal dasselbe neue Passwort ein.", 
							"Ungültiges Passwort");
					return;
				}
				
				this.serverCredentials.changePassword(password, passwordNew1);
			}
			
			this.ok = true;
			this.close();
		}
	}
}
