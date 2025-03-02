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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class Button extends JButton implements ActionListener
{
	private IButtonListener callback;
	private JPopupMenu popup;
	
	public Button(String text, IButtonListener callback)
	{
		super(text);
		
		this.callback = callback;
		
		if (callback != null)
		{
			this.addActionListener(this);
		}
	}
	
	public Button(String text, MenuItem[] popupMenuItems)
	{
		super(text);
		
		this.popup = new JPopupMenu();
		
		for (MenuItem popupMenuItem: popupMenuItems)
		{
			this.popup.add(popupMenuItem);
		}
		
		this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	if (isEnabled()) popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.callback.buttonClicked(this);
	}
}
