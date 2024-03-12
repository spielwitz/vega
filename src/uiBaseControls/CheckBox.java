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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CheckBox extends JCheckBox implements ActionListener
{
	private ICheckBoxListener callback;
	
	public CheckBox(String text, boolean selected, ICheckBoxListener callback)
	{
		super(text, selected);
		
		this.callback = callback;
		
		if (callback != null)
		{
			this.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.callback.checkBoxValueChanged(this, this.isSelected());
	}
}
