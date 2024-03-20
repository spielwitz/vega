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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import common.VegaResources;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
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
	
	private Button butDeleteCredentials;
	private Button butCancel;
	private Button butOk;

	private PasswordField tfPassword;
	private PasswordField tfPasswordNew1;
	private PasswordField tfPasswordNew2;
	
	MessageBoxResult result = MessageBoxResult.CANCEL;
	
	ServerCredentialsPasswordJDialog(
			Component parent, 
			ServerCredentials serverCredentials, 
			ServerCredentialsPasswordJDialogMode mode)
	{
		super(
			parent,
			mode == ServerCredentialsPasswordJDialogMode.ENTER_PASSWORD_FIRST_TIME ?
					VegaResources.SetServerCredentialsPasswort(false) :
						mode == ServerCredentialsPasswordJDialogMode.CHANGE_PASSWORD ?
								VegaResources.ChangeServerCredentialsPasswort(false) :
								VegaResources.UnlockServerCredentials(false),
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
								VegaResources.OldPassword(false) :
								VegaResources.Password(false)), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPassword = new PasswordField("");
			this.tfPassword.setColumns(textFieldColumns);
			panPasswords.add(this.tfPassword, c);
			
			lineCount++;
		}
		
		if (mode != ServerCredentialsPasswordJDialogMode.UNLOCK_CREDENTIALS)
		{
			c.gridx = 0; c.gridy = lineCount; c.gridwidth = 1;
			panPasswords.add(new Label(VegaResources.NewPassword(false)), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPasswordNew1 = new PasswordField("");
			this.tfPasswordNew1.setColumns(textFieldColumns);
			panPasswords.add(this.tfPasswordNew1, c);
			
			lineCount++;
			
			c.gridx = 0; c.gridy = lineCount; c.gridwidth = 1;
			panPasswords.add(new Label(VegaResources.NewPasswordRepeat(false)), c);

			c.gridx = 1; c.gridy = lineCount; c.gridwidth = 2;
			this.tfPasswordNew2 = new PasswordField("");
			this.tfPasswordNew2.setColumns(textFieldColumns);
			panPasswords.add(this.tfPasswordNew2, c);
		}
		
		this.addToInnerPanel(panPasswords, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		if (mode == ServerCredentialsPasswordJDialogMode.UNLOCK_CREDENTIALS)
		{
			this.butDeleteCredentials = new Button(VegaResources.ClearServerCredentials(false), this);
			panButtons.add(this.butDeleteCredentials);
		}
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.butOk = new Button(VegaResources.OK(false), this);
		this.setDefaultButton(this.butOk);
		panButtons.add(this.butOk);
		
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
			char[] password = 
					this.tfPassword != null ?
							this.tfPassword.getPassword() :
							null;
			
			char[]  passwordNew1 = 
					this.tfPasswordNew1 != null ?
							this.tfPasswordNew1.getPassword() :
							null;
			
			char[]  passwordNew2 = 
					this.tfPasswordNew2 != null ?
							this.tfPasswordNew2.getPassword() :
							null;
			
			if (password != null)
			{
				if (!this.serverCredentials.unlockCredentials(VegaUtils.toBytes(password)))
				{
					MessageBox.showError(
							this, 
							VegaResources.PasswordWrong(false), 
							VegaResources.PasswordWrong2(false));
					return;
				}
			}
			
			if (passwordNew1 != null && passwordNew2 != null)
			{
				if (passwordNew1.length < ServerCredentials.PASSWORD_MIN_LENGTH ||
					passwordNew2.length < ServerCredentials.PASSWORD_MIN_LENGTH)
				{
					MessageBox.showError(
							this, 
							VegaResources.PasswordLength(false, Integer.toString(ServerCredentials.PASSWORD_MIN_LENGTH)),
							VegaResources.PasswordInvalid(false));
					return;
				}
				
				if (!this.tfPasswordNew1.arePasswordsEqual(this.tfPasswordNew2))
				{
					MessageBox.showError(
							this, 
							VegaResources.PasswordsNotEqual(false), 
							VegaResources.PasswordInvalid(false));
					return;
				}
				
				this.serverCredentials.changePassword(
						VegaUtils.toBytes(password), 
						VegaUtils.toBytes(passwordNew1));
			}
			
			this.result = MessageBoxResult.OK;
			this.close();
		}
		else if (source == this.butDeleteCredentials)
		{
			MessageBoxResult result = MessageBox.showOkCancel(
					this, 
					VegaResources.ClearServerCredentialsAys(false),
					VegaResources.ClearServerCredentials(false));
			
			if (result == MessageBoxResult.OK)
			{
				this.serverCredentials.clear();
				this.result = MessageBoxResult.NO;
			}
			else
			{
				this.result = MessageBoxResult.CANCEL;
			}
			
			this.close();
		}
	}
}
