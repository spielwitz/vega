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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class IconLabel extends JLabel implements MouseListener
{
	private ImageIcon icon1;
	private ImageIcon icon2;
	
	private IIconLabelListener callback;
	
	public IconLabel(ImageIcon icon, IIconLabelListener callback)
	{
		this(icon, null, callback);
	}
	
	public IconLabel(ImageIcon icon1, ImageIcon icon2, IIconLabelListener callback)
	{
		super();
		
		this.setIcon(icon1);
		
		this.icon1 = icon1;
		this.icon2 = icon2;
		
		this.callback = callback;
		
		if (callback != null)
		{
			this.addMouseListener(this);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
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
		this.callback.iconLabelClicked(this);
	}

	public void setIcon2(boolean setIcon2)
	{
		if (this.icon2 != null)
		{
			this.setIcon(
					setIcon2 ?
							this.icon2 :
							this.icon1);
		}
	}
}
