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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

class Statistics
{
	private Game game;

	private int yearMarked;
	private StatisticsMode mode;

	Statistics(
			Game game,
			boolean showProduction)
	{
		this.game = game;

		this.yearMarked = game.getYear();

		this.mode = StatisticsMode.SCORE;

		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.STATISTICS);
		this.updateDisplay();
		game.setScreenContentMode(ScreenContent.MODE_STATISTICS);

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		allowedKeys.add(new ConsoleKey("\u2190",VegaResources.YearBack(true)));
		allowedKeys.add(new ConsoleKey("\u2192",VegaResources.YearForward(true)));
		allowedKeys.add(new ConsoleKey("1",VegaResources.Points(true)));
		allowedKeys.add(new ConsoleKey("2",VegaResources.Battleships(true)));
		allowedKeys.add(new ConsoleKey("3",VegaResources.Planets(true)));
		
		if (showProduction)
			allowedKeys.add(new ConsoleKey("4",VegaResources.MoneyProduction(true)));

		allowedKeys.add(new ConsoleKey("ESC",VegaResources.CloseStatistics(true)));

		do
		{
			ConsoleInput input = game.getConsole().waitForKeyPressed(allowedKeys, false);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				break;
			}

			else if (input.getLastKeyCode() == KeyEvent.VK_LEFT && this.yearMarked > 0)
			{
				this.yearMarked--;
			}

			else if (input.getLastKeyCode() == KeyEvent.VK_RIGHT && this.yearMarked < this.game.getYear())
			{
				this.yearMarked++;
			}
			else
			{
				switch (input.getInputText())
				{
					case "1":
						this.mode = StatisticsMode.SCORE;
						break;
						
					case "2":
						this.mode = StatisticsMode.BATTLESHIPS;
						break;
						
					case "3":
						this.mode = StatisticsMode.PLANETS;
						break;
						
					case "4":
						if (showProduction)
							this.mode = StatisticsMode.PRODUCTION;
						break;
				}
			}

			this.updateDisplay();

		} while (true);

		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.TEXT_INPUT);
		game.setScreenContentMode(ScreenContent.MODE_BOARD);

		game.getConsole().lineBreak();
	}

	private void updateDisplay()
	{
		int[][] values = new int[this.game.getYear() + 1][this.game.getPlayersCount()];
		int[][] championsByYear = new int[this.game.getYear() + 1][];

		int valueMax = 0;
		int yearValueMax = 0;
		int playerValueMax = 0;
		int valueMin = 0;
		int yearValueMin = 0;
		int playerValueMin = 0;

		boolean start = true;

		ArrayList<Integer> years = new ArrayList<Integer>(this.game.getArchive().keySet());
		Collections.sort(years);

		int counter = 0;
		for (int i = 0; i < years.size(); i++)
		{
			int year = years.get(i);

			Archive archive = this.game.getArchive().get(year);
			int valueBest = 0;

			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
			{
				int value = 0;

				switch (this.mode)
				{
					case SCORE:
						value = archive.getScore()[playerIndex];
						break;

					case BATTLESHIPS:
						value = archive.getBattleships()[playerIndex];
						break;

					case PLANETS:
						value = archive.getPlanetsCount()[playerIndex];
						break;

					case PRODUCTION:
						value = archive.getMoneyProduction()[playerIndex];
						break;
				}

				if (value <= valueMin || start)
				{
					valueMin = value;
					playerValueMin = playerIndex;
					yearValueMin = year;
				}
				if (value >= valueMax || start)
				{
					valueMax = value;
					playerValueMax = playerIndex;
					yearValueMax = year;
				}

				start = false;

				values[counter][playerIndex] = value;

				if (value > valueBest)
				{
					valueBest = value;
				}
			}

			int[] champions = CommonUtils.sortValues(values[counter], true);
			int championsCount = 1;

			for (int t = 1; t < champions.length; t++)
			{
				if (values[counter][champions[t]] < values[counter][champions[0]])
					break;

				championsCount++;
			}

			championsByYear[year] = new int[championsCount];

			int c = 0;
			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
			{
				if (values[counter][champions[playerIndex]] == values[counter][champions[0]])
				{
					championsByYear[year][c] = champions[playerIndex];
					c++;
				}
			}

			counter++;
		} 			

		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());

		this.game.getScreenContent().setStatistik(
				new ScreenContentStatistics(
						this.game.getDateStart(),
						this.mode,
						(Player[])CommonUtils.klon(this.game.getPlayers()),
						values,
						championsByYear,
						this.game.getYear() + 1,
						valueMax,
						yearValueMax,
						playerValueMax,
						valueMin,
						yearValueMin,
						playerValueMin,
						this.yearMarked));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());
	}	
}
