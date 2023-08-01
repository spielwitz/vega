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

package vega;

import java.awt.Frame;

import common.Game;
import common.VegaResources;
import commonUi.DialogWindow;
import commonUi.MessageWithLink;
import commonUi.UiConstants;

class RequiredBuildChecker
{
	static boolean doCheck(Frame parent, String buildRequired)
	{
		boolean success = true;
		
		if (buildRequired == null)
			return true;
		
		if (Game.BUILD.compareTo(buildRequired) < 0)
		{
			DialogWindow.showError(
					parent, 
					new MessageWithLink(
						parent,
						VegaResources.MinBuild(false,
									buildRequired,
									Game.BUILD,
							"<a style=\"color:#ADD8E6;\" href=\""+UiConstants.VEGA_URL+"\">"+UiConstants.VEGA_URL+"</a>")),
					VegaResources.Error(false));
			
			success = false;
		}
		
		return success;
	}
}
