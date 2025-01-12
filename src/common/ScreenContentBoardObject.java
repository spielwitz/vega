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

@SuppressWarnings("serial") 
class ScreenContentBoardObject implements Serializable
{
	private int h;		// HashCode of ship
	private Point p;	// Position
	private Point d;	// Destination
	private byte s;		// Symbol
	private byte c;		// Color
	private boolean l;	// Line is darker
	private boolean e;	// Draw endpoint at destination
	private ScreenContentBoardRadar r;	// Radar circle
	
	ScreenContentBoardObject(
			int hashCode,
			Point position,
			Point destination,
			byte symbol,
			byte colorIndex,
			boolean drawLineDarker,
			boolean endpointAtDestination,
			ScreenContentBoardRadar radar)
	{
		this.h = hashCode;
		this.p = position;
		this.d = destination;
		this.s = symbol;
		this.c = colorIndex;
		this.l = drawLineDarker;
		this.e = endpointAtDestination;
		this.r = radar;
	}

	byte getColorIndex()
	{
		return c;
	}

	Point getDestination()
	{
		return d;
	}

	int getHashCode()
	{
		return h;
	}

	Point getPosition()
	{
		return p;
	}

	ScreenContentBoardRadar getRadar()
	{
		return r;
	}

	byte getSymbol()
	{
		return s;
	}

	boolean isEndpointAtDestination()
	{
		return e;
	}

	boolean isLineDarker()
	{
		return l;
	}
	
	void setRadar(ScreenContentBoardRadar radar)
	{
		this.r = radar;
	}
}
