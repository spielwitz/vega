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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanelWithInsets extends JPanel
{
	private Panel panInner;
	
	public PanelWithInsets(LayoutManager lm)
	{
		super(new BorderLayout());
		
		Panel panBase = new Panel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		
		this.panInner = new Panel(lm);
		
		panBase.add(panInner, c);
		
		this.add(panBase, BorderLayout.CENTER);
	}
	
	public void addToInnerPanel(Component comp) // NO_UCD (unused code)
	{
		this.panInner.add(comp);
	}
	
	public void addToInnerPanel(Component comp, Object constraints) // NO_UCD (unused code)
	{
		this.panInner.add(comp, constraints);
	}
}
