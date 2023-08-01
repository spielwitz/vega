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
import java.util.ArrayList;

@SuppressWarnings("serial") 
class ScreenContentBoard implements Serializable
{
	private ArrayList<ScreenContentBoardPlanet> p;
	private ArrayList<Point> m;
	private ArrayList<ScreenContentBoardObject> o;
	private ArrayList<ScreenContentBoardMine> n;
	
	ScreenContentBoard(
			ArrayList<ScreenContentBoardPlanet> planets,
			ArrayList<Point> positionsMarked,
			ArrayList<ScreenContentBoardObject> objects,
			ArrayList<ScreenContentBoardMine> mines) {

		this.p = planets;
		this.m = positionsMarked;
		this.o = objects;
		this.n = mines;
	}

	void clearMarks()
	{
		this.m = null;
		
		for (ScreenContentBoardObject object: this.o)
		{
			if (object.getRadar() != null && object.getRadar().isHighlighted())
			{
				object.setRadar(
						new ScreenContentBoardRadar(
								false, 
								object.getRadar().isForeground(),
								object.getRadar().getRadius()));
			}
		}
		
		for (ScreenContentBoardPlanet pl: this.p)
		{
			pl.clearFrames();
		}
	}
	
	ArrayList<ScreenContentBoardMine> getMines() {
		return n;
	}

	ArrayList<ScreenContentBoardObject> getObjects() {
		return o;
	}
	
	ArrayList<ScreenContentBoardPlanet> getPlanets() {
		return p;
	}

	ArrayList<Point> getPositionsMarked() {
		return this.m;
	}

	void setObjects(ArrayList<ScreenContentBoardObject> objects)
	{
		this.o = objects;
	}
}
