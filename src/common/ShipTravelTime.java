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

import java.util.Comparator;

class ShipTravelTime implements Comparator<ShipTravelTime> 
{
	int year;
	private int day;
	Ship ship;
	
	ShipTravelTime() {}
	
	ShipTravelTime(int year, int day)
	{
		this.year = year;
		this.day = day;
	}
	
	@Override
	public int compare(ShipTravelTime o1, ShipTravelTime o2) 
	{
		int days1 = o1.year * Game.DAYS_OF_YEAR_COUNT + o1.day;
		int days2 = o2.year * Game.DAYS_OF_YEAR_COUNT + o2.day;
		
		if (days1 == days2)
			return 0;
		if (days1 > days2)
			return 1;
		else
			return -1;
	}
	
	String toOutputString(boolean symbol)
	{
		if (day < Game.DAYS_OF_YEAR_COUNT)
			return VegaResources.YearDay(
					symbol, 
					Integer.toString(year+1), 
					Integer.toString(day+1));
		else
			return VegaResources.YearEndOfYear(
					symbol, 
					Integer.toString(year+1));
	}
	
	String toOutputStringForPlanetList(int currentYear, boolean symbol)
	{
		if (day < Game.DAYS_OF_YEAR_COUNT)
			return VegaResources.YearDayShort(
					symbol, 
					Integer.toString(currentYear + year + 1), 
					Integer.toString(day+1));
		else
			return VegaResources.YearEndShort(
					symbol, 
					Integer.toString(currentYear + year+1));
	}
}
