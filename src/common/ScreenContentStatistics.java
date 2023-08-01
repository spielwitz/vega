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
class ScreenContentStatistics implements Serializable
{
	private long dateStart;
	private StatisticsMode mode;
	private Player[] players;
	private int[][] values;
	private int[][] championsPerYear;
	private int years;
	private int valueMax;
	private int valueMaxYear;
	private int valueMaxPlayerIndex;
	private int valueMin;
	private int valueMinYear;
	private int valueMinPlayerIndex;
	private int selectedYearIndex;
	
	ScreenContentStatistics(
			long dateStart,
			StatisticsMode mode, 
			Player[] players, 
			int[][] values, 
			int[][] championsPerYear,
			int years, 
			int valueMax,
			int valueMaxYear, 
			int valueMaxPlayerIndex, 
			int valueMin, 
			int valueMinYear,
			int valueMinPlayerIndex, 
			int selectedYearIndex)
	{
		super();
		this.dateStart = dateStart;
		this.mode = mode;
		this.players = players;
		this.values = values;
		this.championsPerYear = championsPerYear; 
		this.years = years;
		this.valueMax = valueMax;
		this.valueMaxYear = valueMaxYear;
		this.valueMaxPlayerIndex = valueMaxPlayerIndex;
		this.valueMin = valueMin;
		this.valueMinYear = valueMinYear;
		this.valueMinPlayerIndex = valueMinPlayerIndex;
		this.selectedYearIndex = selectedYearIndex;
	}

	int[][] getChampionsPerYear()
	{
		return championsPerYear;
	}

	long getDateStart()
	{
		return this.dateStart;
	}

	int getMaxValuePlayerIndex() {
		return valueMaxPlayerIndex;
	}
	
	int getMinValuePlayerIndex() {
		return valueMinPlayerIndex;
	}

	StatisticsMode getMode() {
		return mode;
	}

	Player[] getPlayers() {
		return players;
	}

	int getSelectedYearIndex()
	{
		return this.selectedYearIndex;
	}

	int getValueMax() {
		return valueMax;
	}

	int getValueMaxYear() {
		return valueMaxYear;
	}

	int getValueMin() {
		return valueMin;
	}

	int getValueMinYear() {
		return valueMinYear;
	}

	int[][] getValues() {
		return values;
	}
	
	int getYears()
	{
		return years;
	}
}
