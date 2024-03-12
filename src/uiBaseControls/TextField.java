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

package uiBaseControls;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class TextField extends JTextField implements DocumentListener, FocusListener
{
	private ITextFieldListener callback;
	private int maxChars;
	private String allowedKeysRegexPattern;
	private String previousText;
	private boolean documentListenerDisabled;
	
	public TextField(int columns)
	{
		this("", null, columns, -1, null);
	}
	
	public TextField(
			String text, 
			String allowedKeysRegexPattern,
			int columns,
			int maxChars, 
			ITextFieldListener callback)
	{
		super(text, columns);

		this.previousText = text;
		this.callback = callback;
		
		this.maxChars = maxChars;
		this.allowedKeysRegexPattern = allowedKeysRegexPattern;
		
		if (this.allowedKeysRegexPattern != null ||
			this.maxChars > 0)
		{
			this.getDocument().addDocumentListener(this);
		}
		
		if (callback != null)
		{
			this.addFocusListener(this);
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		this.processChangedDocument();
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
	}
	
	@Override
	public void focusLost(FocusEvent e)
	{
		this.callback.textFieldFocusLost(this);
		
	}

	public int getTextInt()
	{
		int retval = 0;
		
		try
		{
			retval = Integer.parseInt(this.getText());
		}
		catch (Exception x)
		{
			
		}
		
		return retval;
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		this.processChangedDocument();
	}
	
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		this.processChangedDocument();
	}
	
	@Override
	public void setText(String text)
	{
		this.documentListenerDisabled = true;
		super.setText(text);
		this.documentListenerDisabled = false;
	}

	private void processChangedDocument()
	{
		if (this.documentListenerDisabled)
		{
			return;
		}
		
		if (this.maxChars > 0 && this.getText().length() > this.maxChars)
		{
			this.undoChange();
			return;
		}
		
		if (this.allowedKeysRegexPattern != null &&
			!Pattern.matches(this.allowedKeysRegexPattern, this.getText()))
		{
			this.undoChange();
			return;
		}
		
		this.previousText = this.getText();
	}

	private void undoChange()
	{
		Runnable executeUndo = new Runnable() {
	        @Override
	        public void run()
	        {
	        	int pos = getCaretPosition();
	            setText(previousText); 

	            if (pos == 0)
	            {
	            	setCaretPosition(0);
	            }
	            else if (pos <= getText().length())
	            {
	            	setCaretPosition(pos - 1);
	            }
	            else
	            {
	            	setCaretPosition(getText().length());
	            }
	        }
	    };      
	    
	    SwingUtilities.invokeLater(executeUndo);
	}
}
