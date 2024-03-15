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

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public abstract class Dialog extends JDialog implements ActionListener
{
	private Panel panInner;
	
	public Dialog(Component parent, String title, LayoutManager lm)
	{
		super(
				parent instanceof JDialog ?
						(JDialog)parent :
						(Frame)parent,
				title);
		
		this.setModal(true);

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
		
		this.add(panBase);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		this.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.getRootPane())
		{
			this.close();
		}
	}
	
	protected void addToInnerPanel(Component comp) // NO_UCD (unused code)
	{
		this.panInner.add(comp);
	}
	
	protected void addToInnerPanel(Component comp, Object constraints) // NO_UCD (unused code)
	{
		this.panInner.add(comp, constraints);
	}
	
	protected void close() // NO_UCD (unused code)
	{
		this.setVisible(false);
		this.dispose();
	}
}
