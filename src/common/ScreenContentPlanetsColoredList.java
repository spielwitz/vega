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

package common;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
class ScreenContentPlanetsColoredList implements Serializable
{
	private boolean scrollUpIndicator;
	private boolean scrollDownIndicator;
	private byte headersColorIndex;
	private ArrayList<ScreenContentPlanetsColoredListHeaderColumn> headers;
	private ArrayList<ScreenContentPlanetsColoredListCellValue[]> lines;

	ScreenContentPlanetsColoredList(boolean scrollUpIndicator, boolean scrollDownIndicator, byte headersColorIndex,
			ArrayList<ScreenContentPlanetsColoredListHeaderColumn> headers,
			ArrayList<ScreenContentPlanetsColoredListCellValue[]> lines)
	{
		super();
		this.headersColorIndex = headersColorIndex;
		this.headers = headers;
		this.lines = lines;
		this.scrollDownIndicator = scrollDownIndicator;
		this.scrollUpIndicator = scrollUpIndicator;
	}

	ArrayList<ScreenContentPlanetsColoredListHeaderColumn> getHeaders()
	{
		return headers;
	}

	byte getHeadersColorIndex()
	{
		return headersColorIndex;
	}

	ArrayList<ScreenContentPlanetsColoredListCellValue[]> getLines()
	{
		return lines;
	}

	boolean isScrollDownIndicator()
	{
		return scrollDownIndicator;
	}

	boolean isScrollUpIndicator()
	{
		return scrollUpIndicator;
	}

}