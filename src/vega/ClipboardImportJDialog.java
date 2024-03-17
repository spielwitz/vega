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

import common.Game;
import common.VegaResources;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.PasswordField;
import uiBaseControls.TextArea;

@SuppressWarnings("serial")
class ClipboardImportJDialog<T> extends Dialog
				implements IButtonListener
{
	DialogWindowResult dlgResult = DialogWindowResult.CANCEL;
	Object obj;
	private Button butCancel;
	private Button butDelete;
	
	private Button butImport;
	
	private Button butOk;
	private Class<T> expectedClass;
	
	private boolean passwordProtected;
	
	private TextArea taImportData;
	private PasswordField tfPassword;
	
	ClipboardImportJDialog(
			Component parent, 
			Class<T> expectedClass,
			boolean passwordProtected)
	{
		super (parent, VegaResources.ConnectionSettings(false), new BorderLayout(0, 10));
		this.passwordProtected = passwordProtected;
		
		this.expectedClass = expectedClass;
		
		Panel panTextArea = new Panel(
				VegaResources.PasteClipboardHere(false),
				new GridBagLayout());
		
		GridBagConstraints cPanTextArea = new GridBagConstraints();
		cPanTextArea.insets = new Insets(5, 5, 5, 5);
		cPanTextArea.fill = GridBagConstraints.BOTH;
		cPanTextArea.weightx = 0.5;
		cPanTextArea.weighty = 0.5;

		this.taImportData = new TextArea("");
		this.taImportData.setRowsAndColumns(15, 50);
		
		cPanTextArea.gridx = 0; cPanTextArea.gridy = 0; cPanTextArea.gridwidth = 8;
		panTextArea.add(this.taImportData, cPanTextArea);
		
		if (this.passwordProtected)
		{
			cPanTextArea.fill = GridBagConstraints.HORIZONTAL;
			
			cPanTextArea.gridx = 0; cPanTextArea.gridy = 1; cPanTextArea.gridwidth = 1;
			panTextArea.add(new Label(VegaResources.Password(false)), cPanTextArea);
			
			cPanTextArea.weightx = 1;
			cPanTextArea.gridx = 1; cPanTextArea.gridy = 1; cPanTextArea.gridwidth = 1;
			
			this.tfPassword = new PasswordField("");
			this.tfPassword.setColumns(30);
			panTextArea.add(this.tfPassword, cPanTextArea);
		}
		
		this.addToInnerPanel(panTextArea, BorderLayout.CENTER);
						
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butImport = new Button(VegaResources.Insert(false), this);
		panButtons.add(this.butImport);
		
		this.butDelete = new Button(VegaResources.Delete(false), this);
		panButtons.add(this.butDelete);
		
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
			this.close();
		else if (source == this.butDelete)
			this.taImportData.setText("");
		else if (source == this.butImport)
			this.taImportData.setText(EmailToolkit.getClipboardContent());
		else if (source == this.butOk)
		{
			String password = this.passwordProtected ?
								new String(this.tfPassword.getPassword()) :
								null;
								
			boolean ok = false;
			try
			{
				this.obj = EmailToolkit.parseEmail(this.taImportData.getText(), this.expectedClass, password);
				ok = (this.obj != null);
			}
			catch (Exception x)
			{
				this.obj = null;
			}
			
			if (ok)
			{
				this.dlgResult = DialogWindowResult.OK;
				this.close();
			}
			else
			{
				this.obj = null;
				if (password == null)
					DialogWindow.showError(
							this,
							VegaResources.ClipboardImportError(false,
									Game.BUILD),
							VegaResources.LoadError(false));
				else
					DialogWindow.showError(
							this,
							VegaResources.ClipboardImportErrorPassword(false,
									Game.BUILD),
							VegaResources.LoadError(false));
			}
		}
	}

	@Override
	protected boolean confirmClose()
	{
		return true;
	}
}
