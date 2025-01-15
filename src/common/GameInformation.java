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
import java.util.HashSet;
import java.util.Hashtable;

class GameInformation
{
	private Game game;

	GameInformation()
	{
	}

	GameInformation(Game game)
	{
		this.game = game;
		this.game.getConsole().enablePlanetListContentToggle(false);
		this.game.togglePlanetListContentReset();

		do
		{
			this.game.getConsole().clear();

			this.game.updateBoard();
			this.game.updatePlanetList(false);

			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true), Colors.NEUTRAL);

			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("ESC", VegaResources.MainMenu(true)));
			allowedKeys.add(new ConsoleKey("0",VegaResources.Planet(true)));

			allowedKeys.add(new ConsoleKey("1", VegaResources.MoneyProduction(true)));

			allowedKeys.add(new ConsoleKey("2", VegaResources.DefensiveBattleships(true)));
			allowedKeys.add(new ConsoleKey("3",VegaResources.Spies(true)));
			allowedKeys.add(new ConsoleKey("4",VegaResources.Alliances(true)));

			ConsoleInput input = this.game.getConsole().waitForKeyPressed(allowedKeys);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().clear();
				break;
			}

			String inputString = input.getInputText().toUpperCase();

			if (inputString.equals("1"))
				this.moneyProduction();
			else if (inputString.equals("2"))
				this.defensiveBattleships();
			else if (inputString.equals("3"))
				this.radioStations();
			else if (inputString.equals("4"))
				this.alliances();
			else if (inputString.equals("0"))
				this.planetEditorDisplay();
			else
				this.game.getConsole().outInvalidInput();

		} while (true);

		this.game.getConsole().enablePlanetListContentToggle(true);
		this.game.updatePlanetList(false);
	}

	ArrayList<ScreenContentBoardPlanet> getDefaultBoard(Hashtable<Integer, ArrayList<Byte>> frames, HashSet<Integer> planetIndicesHighlighted)
	{
		ArrayList<ScreenContentBoardPlanet> planets = new ArrayList<ScreenContentBoardPlanet>(this.game.getPlanetsCount());

		for (int planetIndex = 0; planetIndex < this.game.getPlanetsCount(); planetIndex++)
		{
			ArrayList<Byte> frameCols = null;

			if (frames != null && frames.containsKey(planetIndex))
				frameCols = frames.get(planetIndex);

			byte colorIndex = this.game.getPlanets()[planetIndex].getOwnerColorIndex(this.game); 

			if (planetIndicesHighlighted != null && !planetIndicesHighlighted.contains(planetIndex))
			{
				colorIndex = Colors.getColorIndexDarker(colorIndex);
				if (frameCols != null)
				{
					ArrayList<Byte> frameColsDarker = new ArrayList<Byte>(frameCols.size());
					for (Byte fc: frameCols)
						frameColsDarker.add(Colors.getColorIndexDarker(fc));
					frameCols = frameColsDarker;
				}
			}

			planets.add(new ScreenContentBoardPlanet(
					this.game.getPlanetNameFromIndex(planetIndex),
					this.game.getPlanets()[planetIndex].getPosition(),
					colorIndex,
					frameCols)); 				

		}

		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());

		return planets;
	}

	void setGame(Game game)
	{
		this.game = game;
	}

	private void alliances()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true)+" -> "+VegaResources.Alliances(true), Colors.NEUTRAL);

		HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
		Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();

		for (int index = 0; index < this.game.getPlanetsCount(); index++)
		{
			int planetIndex = this.game.getPlanetsSorted()[index];

			ArrayList<Byte> frameCols = new ArrayList<Byte>();

			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
			{
				if (this.game.getPlanets()[planetIndex].getOwner() != playerIndex && this.game.getPlanets()[planetIndex].isAllianceMember(playerIndex))
					frameCols.add(this.game.getPlayers()[playerIndex].getColorIndex());
			}

			if (frameCols.size() > 0)
			{
				frames.put(planetIndex, frameCols);
				planetIndicesHighlighted.add(planetIndex);
			}
		}

		ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(
						planets,
						null,
						null,
						null));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());

		if (planetIndicesHighlighted.size() == 0)
		{
			this.game.getConsole().appendText(
					VegaResources.NoPlanetsWithAlliances(true));
			this.game.getConsole().waitForKeyPressed();
			return;
		}

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.DisplayAllianceOnPlanet(true), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				break;
			}

			int planetIndex = input.planetIndex;

			if (this.game.getPlanets()[planetIndex].allianceExists())
			{
				this.game.getConsole().setLineColor(this.game.getPlanets()[planetIndex].getOwnerColorIndex(this.game));
				this.game.getConsole().appendText(
						VegaResources.AllianceStructureOnPlanet(true, this.game.getPlanetNameFromIndex(planetIndex)) +
						":");
				this.game.getConsole().lineBreak();
				this.game.getConsole().setLineColor(Colors.WHITE);

				this.game.printAllianceInfo(planetIndex);
			}
			else
				this.game.getConsole().appendText(
						VegaResources.NoAllianceOnPlanet(true, this.game.getPlanetNameFromIndex(planetIndex)));

			this.game.getConsole().lineBreak();
		} while (true);
	}

	private void defensiveBattleships()
	{
		this.game.getConsole().setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true)+" -> "+VegaResources.DefensiveBattleships(true), Colors.NEUTRAL);

		HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
		Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();

		for (int index = 0; index < this.game.getPlanetsCount(); index++)
		{
			int planetIndex = this.game.getPlanetsSorted()[index];

			ArrayList<Byte> frameCols = new ArrayList<Byte>();

			if (this.game.getPlanets()[planetIndex].getDefensiveBattleshipsCount() > 0 ||
				this.game.getPlanets()[planetIndex].getBonus() > 0)
			{
				frameCols.add(Colors.WHITE);
				frames.put(planetIndex, frameCols);
				planetIndicesHighlighted.add(planetIndex);
			}
		}

		ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(
						planets,
						null,
						null,
						null));

		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Byte> textCol = new ArrayList<Byte>();

		for (int playerIndex = Player.NEUTRAL; playerIndex < this.game.getPlayersCount(); playerIndex++)
		{
			boolean firstLine = true;

			for (int index = 0; index < this.game.getPlanetsCount(); index++)
			{
				int planetIndex = this.game.getPlanetsSorted()[index];

				if (this.game.getPlanets()[planetIndex].getOwner() != playerIndex ||
					(this.game.getPlanets()[planetIndex].getDefensiveBattleshipsCount() == 0 &&
					 this.game.getPlanets()[planetIndex].getBonus() == 0))
				{
					continue;
				}

				String playerName = (playerIndex == Player.NEUTRAL) ?
						VegaResources.Neutral(false) :
							this.game.getPlayers()[playerIndex].getName();

				byte colorIndex = (playerIndex == Player.NEUTRAL) ?
						(byte)Colors.NEUTRAL :
							this.game.getPlayers()[playerIndex].getColorIndex();

				if (firstLine)
				{
					text.add(playerName);
					textCol.add(colorIndex);

					firstLine = false;
				}

				String planetName = this.game.getPlanetNameFromIndex(planetIndex);
				String defensiveBattleshipsCount = "     " + this.game.getPlanets()[planetIndex].getDefensiveBattleshipCombatStrengthConcatenated();
				text.add(planetName.substring(planetName.length()-2, planetName.length()) + 
						":" +
						defensiveBattleshipsCount.substring(defensiveBattleshipsCount.length()-8, defensiveBattleshipsCount.length()));

				textCol.add(colorIndex);
			}
		}

		this.game.getScreenContent().setPlanets(
				new ScreenContentPlanets(
						VegaResources.DefensiveBattleships(true),
						Colors.NEUTRAL,
						false,
						text, 
						textCol));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());

		this.game.getConsole().waitForKeyPressed();
	}

	private void moneyProduction()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + 
				" -> "+VegaResources.GameInfo(true)+
				" -> "+VegaResources.MoneyProductionOfPlanets(true), Colors.NEUTRAL);

		ArrayList<ScreenContentBoardPlanet> screenContentsPlanet = new ArrayList<ScreenContentBoardPlanet>(this.game.getPlanetsCount());

		for (int index = 0; index < this.game.getPlanetsCount(); index++)
		{
			int planetIndex = this.game.getPlanetsSorted()[index];

			screenContentsPlanet.add(new ScreenContentBoardPlanet(
					Integer.toString(this.game.getPlanets()[planetIndex].getMoneyProduction()),
					this.game.getPlanets()[planetIndex].getPosition(),
					this.game.getPlanets()[planetIndex].getOwnerColorIndex(this.game),
					null)); 					
		}

		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(screenContentsPlanet,
						null,
						null,
						null));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());

		this.game.getConsole().waitForKeyPressed();
	}

	private void planetEditorDisplay()
	{
		do
		{
			this.game.getConsole().setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true)+" -> "+VegaResources.Planet(true), Colors.NEUTRAL);

			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.Planet(true), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				break;
			}

			int planetIndex = input.planetIndex;
			Planet planet = this.game.getPlanets()[planetIndex];

			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true)+" -> "+VegaResources.Planet(true)+" " + this.game.getPlanetNameFromIndex(planetIndex), 
					planet.getOwnerColorIndex(this.game));

			new PlanetEditor(
					this.game,
					planetIndex,
					null,
					true);


		} while (true);
	}

	private void radioStations()
	{
		this.game.getConsole().setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.GameInfo(true)+" -> "+VegaResources.Spies(true), Colors.NEUTRAL);

		HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
		Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();

		for (int index = 0; index < this.game.getPlanetsCount(); index++)
		{
			int planetIndex = this.game.getPlanetsSorted()[index];

			ArrayList<Byte> frameCols = new ArrayList<Byte>();

			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
			{
				if (this.game.getPlanets()[planetIndex].hasRadioStation(playerIndex))
				{
					frameCols.add(this.game.getPlayers()[playerIndex].getColorIndex());
				}
			}

			if (frameCols.size() > 0)
			{
				frames.put(planetIndex, frameCols);
				planetIndicesHighlighted.add(planetIndex);
			}
		}

		ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(
						planets,
						null,
						null,
						null));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());

		this.game.getConsole().waitForKeyPressed();
	}
}
