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

package uiBaseControls;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class HyperlinkLabel extends JLabel implements MouseListener
{
	private String text;
	
	public HyperlinkLabel(String text)
	{
		super(text);
		
		this.text = text;
		
		this.setForeground(new Color(51, 153, 255));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this.addMouseListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		try
		{
	        Desktop.getDesktop().browse(new URI(this.text));
	    } catch (Exception x)
		{
	    }
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		this.setText("<html><a href=''>"+this.text+"</a></html>");
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		this.setText(this.text);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	public void updateText(String text)
	{
		this.text = text;
		this.setText(this.text);
	}	
}
