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

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public abstract class Frame extends JFrame implements WindowListener
{
	private Panel panBase;
	
	public Frame(String title, LayoutManager lm)
	{
		super(title);
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.panBase = new Panel(lm);
		this.add(this.panBase);
		
		this.addWindowListener(this);
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		this.close();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	protected void addToBasePanel(Component comp) // NO_UCD (unused code)
	{
		this.panBase.add(comp);
	}

	protected void addToBasePanel(Component comp, Object constraints) // NO_UCD (unused code)
	{
		this.panBase.add(comp, constraints);
	}
	
	protected void close() // NO_UCD (use private)
	{
		if (this.confirmClose())
		{
			this.dispose();
			System.exit(0);
		}
	}
	
	protected abstract boolean confirmClose();
}
