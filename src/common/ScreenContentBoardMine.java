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
class ScreenContentBoardMine implements Serializable
{
	private int x;
	private int y;
	private int s;

	ScreenContentBoardMine(int positionX, int positionY, int strength) {
		super();
		this.x = positionX;
		this.y = positionY;
		this.s = strength;
	}
	
	int getPositionX() {
		return x;
	}
	
	int getPositionY() {
		return y;
	}
	
	int getStrength() {
		return s;
	}
}
