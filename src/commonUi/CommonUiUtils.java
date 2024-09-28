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

import java.awt.Desktop;
import java.io.File;

import common.VegaResources;
import uiBaseControls.Frame;

public class CommonUiUtils
{
	public static void showManual(Frame parent)
	{
		Desktop desktop = Desktop.getDesktop();
		String filename = "VegaHelp_"+
	    		VegaResources.getLocale() +
	    		".pdf";
		
		try
		{
			desktop.open(new File(filename));   
		}
		catch (Exception x)
		{
			MessageBox.showError(
					parent, 
					VegaResources.VegaHelpError(false, filename, x.getLocalizedMessage()), 
					VegaResources.Error(false));
		}
	}
}