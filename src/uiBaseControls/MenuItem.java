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

package uiBaseControls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MenuItem extends JMenuItem implements ActionListener
{
	private IMenuItemListener callback;
	
	public MenuItem(String text, IMenuItemListener callback)
	{
		super(text);
		
		this.callback = callback;
		
		if (callback != null)
		{
			this.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.callback.menuItemSelected(this);
	}

}
