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

package common;

class PlanetInputStruct
{
	static final int ALLOWED_INPUT_PLANET = 1;
	static final int ALLOWED_INPUT_SECTOR = 2;
	
	int planetIndex = Planet.NO_PLANET;
	Point sector = null;
	
	PlanetInputStruct(int planetIndex)
	{
		this.planetIndex = planetIndex;
	}
	
	PlanetInputStruct(Point position, int planetIndex)
	{
		this.sector = position;
		this.planetIndex = planetIndex;
	}
}
