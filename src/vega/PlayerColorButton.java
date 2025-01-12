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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import common.Colors;
import uiBaseControls.Dialog;

@SuppressWarnings("serial") class PlayerColorButton extends JButton implements ActionListener
{
	byte colorIndex;
	private Dialog parent;
	private IColorChooserCallback callback;
	int playerIndex;
	private boolean isEnabled;
	private boolean showColor;
	
	PlayerColorButton(Dialog parent, IColorChooserCallback callback, int playerIndex, byte colorIndex)
	{
		super();
		this.colorIndex = colorIndex;
		this.parent = parent;
		this.playerIndex = playerIndex;
		this.callback = callback;
		
		this.setBackground(Colors.get(colorIndex));
		
		this.addActionListener(this);
		this.isEnabled = true;
		this.showColor = true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (!this.isEnabled)
		{
			return;
		}
		
		ColorChooser dlg = new ColorChooser(this.parent, this.colorIndex);
		dlg.setVisible(true);
		
		if (!dlg.abort)
		{
			byte oldColorIndex = this.colorIndex;
			this.setColor(dlg.selectedColor);				
			this.callback.colorChanged(this.playerIndex, this.colorIndex, oldColorIndex);
		}
	}
	
	public void setColor(byte colorIndex)
	{
		this.colorIndex = colorIndex;
		
		if (this.showColor)
		{
			this.setBackground(Colors.get(this.colorIndex));
		}
	}

	public void setEnabled(boolean enabled)
	{
		this.isEnabled = enabled;
	}
	
	public void showColor(boolean showColor)
	{
		if (showColor)
		{
			this.setBackground(Colors.get(this.colorIndex));
		}
		else
		{
			this.setBackground(Color.gray);
		}
		
		this.showColor = showColor;
	}

}
