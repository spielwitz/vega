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

package vegaDisplayCommon;

import common.ScreenContent;

public class VegaDisplayScreenContent
{
	private ScreenContent screenContent;
	private boolean keepAlive;

	public VegaDisplayScreenContent(ScreenContent screenContent)
	{
		this.screenContent = screenContent;
		this.keepAlive = false;
	}
	
	public VegaDisplayScreenContent(boolean keepAlive)
	{
		this.screenContent = null;
		this.keepAlive = true;
	}

	public ScreenContent getScreenContent()
	{
		return screenContent;
	}
	
	public boolean isKeepAlive()
	{
		return keepAlive;
	}
}
