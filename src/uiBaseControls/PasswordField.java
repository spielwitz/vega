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

import javax.swing.JPasswordField;

@SuppressWarnings("serial")
public class PasswordField extends JPasswordField
{
	public PasswordField(String text)
	{
		super(text);
	}
	
	public boolean arePasswordsEqual(PasswordField other)
	{
		if (this.getPassword().length != other.getPassword().length) return false;
		
		for (int i = 0; i < this.getPassword().length; i++)
		{
			if (this.getPassword()[i] != other.getPassword()[i])
			{
				return false;
			}
		}
		
		return true;
	}
}
