/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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

@SuppressWarnings("serial")
public class ScreenContent implements Serializable
{
	transient final static int MODE_BOARD = 0;
	transient final static int MODE_PLANET_EDITOR = 1;
	transient final static int MODE_STATISTICS = 2;
	transient final static int MODE_DISTANCE_MATRIX = 3;
	
	private ScreenContentConsole c;
	private ScreenContentPlanets p;
	private ScreenContentBoard f;
	private ScreenContentPlanetEditor e;
	private ScreenContentStatistics t;
	
	private int m;
	private boolean u;
	private boolean s;
	private int g;
	
	ScreenContent()
	{
		this.m = MODE_BOARD;
	}
	
	ScreenContentBoard getBoard() {
		return f;
	}

	ScreenContentConsole getConsole() {
		return c;
	}
	
	int getEventDay()
	{
		return this.g;
	}
	
	int getMode() {
		return m;
	}

	ScreenContentPlanetEditor getPlanetEditor() {
		return e;
	}

	ScreenContentPlanets getPlanets() {
		return p;
	}

	ScreenContentStatistics getStatistics() {
		return t;
	}
	
	boolean isPause()
	{
		return this.u;
	}

	boolean isSnapshot()
	{
		return this.s;
	}

	void setBoard(ScreenContentBoard screenContentBoard) {
		this.f = screenContentBoard;
	}

	void setConsole(ScreenContentConsole screenContentConsole) {
		this.c = screenContentConsole;
	}

	void setEventDay(int tag)
	{
		this.g = tag;
	}
	
	void setMode(int modus)
	{
		if (modus == MODE_PLANET_EDITOR || modus == MODE_STATISTICS || modus == MODE_DISTANCE_MATRIX)
			this.m = modus;
		else
			this.m = MODE_BOARD;
	}

	void setPlanetEditor(ScreenContentPlanetEditor screenContentPlanetEditor) {
		this.e = screenContentPlanetEditor;
	}
	
	void setPlanets(ScreenContentPlanets screenContentPlanets) {
		this.p = screenContentPlanets;
	}
	
	void setSnapshot()
	{
		this.s = true;
	}
	
	void setStatistik(ScreenContentStatistics screenContentStatistics) {
		this.t = screenContentStatistics;
	}
}
