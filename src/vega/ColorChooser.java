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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import common.Colors;
import common.Game;
import common.VegaResources;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.Panel;

@SuppressWarnings("serial") 
class ColorChooser extends Dialog implements IButtonListener
{
	boolean abort = true;
	byte selectedColor;
	private Button butCancel;
	private Button butOk;
	private ColorPanel[] colorPanels;
	
	ColorChooser(Dialog parent, byte currentColor)
	{
		super (parent, VegaResources.Color(false), new BorderLayout(0, 10));
		
		Panel panMain = new Panel(new GridLayout(3,2,10,10));
		
		this.colorPanels = new ColorPanel[Game.PLAYERS_COUNT_MAX];
		
		for (int i = 0; i < Game.PLAYERS_COUNT_MAX; i++)
		{
			this.colorPanels[i] = new ColorPanel(this, (byte)(i+Colors.COLOR_OFFSET_PLAYERS));
			this.colorPanels[i].setPreferredSize(new Dimension(50, 50));
			panMain.add(this.colorPanels[i]);
		}
		
		this.addToInnerPanel(panMain, BorderLayout.CENTER);
		
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.butOk = new Button(VegaResources.OK(false), this);
		this.setDefaultButton(this.butOk);
		panButtons.add(this.butOk);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		
		this.colorChanged(currentColor);
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butCancel)
		{
			this.abort = true;
			this.close();
		}
		else if (source == this.butOk)
		{
			this.abort = false;
			this.close();
		}
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	private void colorChanged(byte colorIndex)
	{
		this.selectedColor = colorIndex;
		
		for (int i = 0; i < this.colorPanels.length; i++)
			this.colorPanels[i].setSelected(i == colorIndex - Colors.COLOR_OFFSET_PLAYERS);
	}

	private class ColorPanel extends JPanel implements MouseListener
	{
		private static final int BORDER_SIZE = 5;
		
		private byte colorIndex;
		private ColorChooser parent;
		private boolean selected;
		
		public ColorPanel(ColorChooser parent, byte colorIndex)
		{
			super();
			this.parent = parent;
			this.colorIndex = colorIndex;
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			this.parent.colorChanged(this.colorIndex);
			
			if (e.getClickCount() == 2)
				this.parent.butOk.doClick();
		}

		public void paint( Graphics g )
		{
			Dimension dim = this.getSize();
			
			if (this.selected)
			{
				g.setColor(Color.white);
				g.fillRect(0, 0, dim.width, dim.height);
				
				g.setColor(Colors.get(this.colorIndex));
				g.fillRect(BORDER_SIZE, BORDER_SIZE, dim.width-2*BORDER_SIZE, dim.height-2*BORDER_SIZE);

				g.setColor(Color.BLACK);
				g.drawRect(BORDER_SIZE, BORDER_SIZE, dim.width-2*BORDER_SIZE, dim.height-2*BORDER_SIZE);
			}
			else
			{
				g.setColor(Colors.get(this.colorIndex));
				g.fillRect(0, 0, dim.width, dim.height);
			}
		}

		public void setSelected(boolean selected)
		{
			this.selected = selected;
			this.repaint();
		}
	}
}
