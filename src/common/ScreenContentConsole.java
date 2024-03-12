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
class ScreenContentConsole implements Serializable
{
	private String[] t;
	private byte[] c;
	private ArrayList<ConsoleKey> k;
	private String h;
	private byte a;
	private int o;
	private int p;
	
	private boolean w;
	
	ScreenContentConsole(
			String[] textLines, 
			byte[] lineColors,
			ArrayList<ConsoleKey> allowedKeys, 
			String headerText,
			byte headerCol, 
			int outputLine, 
			boolean waitingForInput,
			int progressBarDay)
	{
		super();
		this.t = textLines;
		this.c = lineColors;
		this.k = allowedKeys;
		this.h = headerText;
		this.a = headerCol;
		this.w = waitingForInput;
		this.o = outputLine;
		this.p = progressBarDay;
	}

	void clearKeys()
	{
		this.k = new ArrayList<ConsoleKey>();
	}

	ArrayList<ConsoleKey> getAllowedKeys() {
		return k;
	}

	byte getHeaderCol() {
		return a;
	}
	
	String getHeaderText() {
		return h;
	}

	byte[] getLineColors() {
		return c;
	}
	
	int getOutputLine() {
		return o;
	}
	
	int getProgressBarDay()
	{
		return p;
	}
	
	String[] getTextLines() {
		return t;
	}

	boolean isWaitingForInput() {
		return w;
	}

	void setProgressBarDay(int p)
	{
		this.p = p;
	}
}
