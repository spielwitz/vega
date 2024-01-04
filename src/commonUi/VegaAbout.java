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

package commonUi;

import java.awt.Frame;

import common.Game;

public class VegaAbout
{
	public static void show(Frame parent)
	{
		DialogWindow.showInformation(
				parent, 
				new MessageWithLink(
						parent,
						"VEGA - (c) 1989-2024 Michael Schweitzer - <a href=\""+UiConstants.VEGA_URL+"\">"+UiConstants.VEGA_URL+"</a>"),
				"Build " + Game.BUILD);
	}
}