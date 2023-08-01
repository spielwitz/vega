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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import uiBaseControls.IconLabel;

@SuppressWarnings("serial")
public class Toolbar extends JPanel
{
	private static final Color backgroundColor = Color.black;
	private JPanel panServerControls;
	private GridBagConstraints c;
	
	public Toolbar(IconLabel labMenu)
	{
		super(new BorderLayout());
		
		this.setBackground(backgroundColor);
		
		JPanel panMenu = new JPanel(new GridBagLayout());
		panMenu.setBackground(backgroundColor);
		
		this.c = new GridBagConstraints();
		
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 0; c.gridy = 0;
		panMenu.add(labMenu, c);
		
		this.add(panMenu, BorderLayout.NORTH);
		
		this.panServerControls = new JPanel(new GridBagLayout());
		this.panServerControls.setBackground(backgroundColor);
		
		this.add(this.panServerControls, BorderLayout.SOUTH);
	}
	
	public void addIconLabel(IconLabel label, int y)
	{
		this.c.gridx = 0;
		this.c.gridy = y;
		
		this.panServerControls.add(label, this.c);
	}
}
