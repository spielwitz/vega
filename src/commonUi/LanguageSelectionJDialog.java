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

package commonUi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Hashtable;

import common.VegaResources;
import uiBaseControls.Button;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.Frame;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;

@SuppressWarnings("serial")
public class LanguageSelectionJDialog extends Dialog implements IButtonListener
{
	private Button butOk;
	private Button butCancel;
	private ComboBox comboLanguages;
	
	public boolean ok = false;
	public String languageCode;

	private boolean allowCancel;
	private Hashtable<String, String> languages;

	public LanguageSelectionJDialog(
			Frame parent,
			String languageCode,
			boolean allowCancel)
	{
		super (parent, VegaResources.VegaLanguage(false), new BorderLayout(0, 10));
		
		this.languageCode = languageCode;
		this.allowCancel = allowCancel;
		
		this.languages = new Hashtable<String, String>();
		
		this.languages.put("Deutsch", "de-DE");
		this.languages.put("English", "en-US");
				
		// ---------------
		Panel panLanguage = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		panLanguage.add(new Label(VegaResources.Language(false)));
		
		String[] languagesArray = this.languages.keySet().toArray(new String[this.languages.size()]);
		
		this.comboLanguages = new ComboBox(
				languagesArray,
				20,
				getKeyFromValue(this.languages, languageCode),
				null);
		
		panLanguage.add(this.comboLanguages);
		
		this.addToInnerPanel(panLanguage, BorderLayout.CENTER);
		
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		if (allowCancel)
		{
			this.butCancel = new Button(VegaResources.Cancel(false), this);
			panButtons.add(this.butCancel);
		}
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
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
		else if (source == this.butOk)
		{
			String selectedKey = (String)this.comboLanguages.getSelectedItem();
			String languageCodeNew = this.languages.get(selectedKey);
			
			if (languageCodeNew.equals(VegaResources.getLocale()))
			{
				this.close();
				return;
			}
			
			String languageCodeOld = VegaResources.getLocale();
			
			VegaResources.setLocale(languageCodeNew);
			
			DialogWindowResult dialogResult =  
					this.allowCancel ?
							DialogWindow.showOkCancel(
									this,
									VegaResources.NewLanguageEffectiveAfterRestart(false),
									VegaResources.VegaLanguage(false)) :
							DialogWindowResult.OK;
			
			if (dialogResult == DialogWindowResult.OK)
			{
				
				this.languageCode = languageCodeNew;
				this.ok = true;
				this.close();
			}
			else
				VegaResources.setLocale(languageCodeOld);
		}
	}
	
	private static String getKeyFromValue(Hashtable<String,String> ht, String value)
	{
		String retval = null;
		
		for (String key: ht.keySet())
		{
			String value2 = ht.get(key);
			
			if (value.equals(value2))
			{
				retval = key;
				break;
			}
		}
		
		return retval;
	}
	
}
