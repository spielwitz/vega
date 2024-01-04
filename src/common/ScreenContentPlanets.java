/**	VEGA - a strategy game
    Copyright (C) 1989-2024 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General License for more details.

    You should have received a copy of the GNU Affero General License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package common;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial") 
class ScreenContentPlanets implements Serializable
{
	private String l;
	private byte o;
	private boolean g;
	private ArrayList<String> t;
	private ArrayList<Byte> c;
	private ScreenContentPlanetsColoredList r;
	
	ScreenContentPlanets(
			String title,
			byte titleColor,
			boolean toggleContentsEnabled,
			ArrayList<String> text,
			ArrayList<Byte> textColorIndices) 
	{
		super();
		this.l = title;
		this.o = titleColor;
		this.t = text;
		this.c = textColorIndices;
		this.g = toggleContentsEnabled;
	}
	
	ScreenContentPlanets(
			String title,
			byte titleColor,
			boolean toggleContentsEnabled,
			ScreenContentPlanetsColoredList coloredList)
	{
		super();
		this.l = title;
		this.o = titleColor;
		this.r = coloredList;
		this.g = toggleContentsEnabled;
	}
	
	ScreenContentPlanetsColoredList getColoredList()
	{
		return r;
	}

	ArrayList<String> getText() {
		return t;
	}
	
	ArrayList<Byte> getTextColorIndices() {
		return c;
	}
	
	String getTitle()
	{
		return this.l;
	}
	
	byte getTitleColor()
	{
		return this.o;
	}
	
	boolean isToggleContentsEnabled()
	{
		return g;
	}
}
