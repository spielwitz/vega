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

package commonUi;

import java.awt.Font;
import java.io.InputStream;

public class FontHelper 
{
	private static Font fontBase;
	
	public static Font getFont(float fontSize)
	{
		return fontBase.deriveFont(fontSize);
	}
	
	public static void initialize(String fontName)
	{
		new FontHelper(fontName);
	}
	
	private FontHelper(String fontName)
	{
		try
		{
			InputStream in = getClass().getResourceAsStream(fontName);
			FontHelper.fontBase = Font.createFont(
					Font.TRUETYPE_FONT,
					in);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
}
