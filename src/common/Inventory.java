/**	VEGA - a strategy game
    Copyright (C) 1989-2024 Michael Schweitzer, spielwitz@icloud.com

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

class Inventory
{
	private Game game;
	private int playerIndex;
	private InventoryPdfData pdfData;
	private GameInformation gameInformation;
	private ScreenContent screenContentCopy;

	Inventory(Game game, int playerIndex)
	{
		this.game = game;
		this.playerIndex = playerIndex;
	}

	byte[] create(String languageCode)
	{
		this.screenContentCopy = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());

		String languageCodeCopy = VegaResources.getLocale();
		VegaResources.setLocale(languageCode);

		this.gameInformation = new GameInformation();
		this.gameInformation.setGame(this.game);

		this.pdfData = new InventoryPdfData(
				this.game.getPlayers()[this.playerIndex].getName(),
				this.game.getYear(),
				this.game.getYearMax(),
				this.game.getArchive().get(this.game.getYear()).getScore()[this.playerIndex],
				false);

		this.planets();
		this.ships();

		this.game.setScreenContent(screenContentCopy);

		byte[] pdfByteArray = null;			
		try
		{
			pdfByteArray = InventoryPdf.create(pdfData);
		}
		catch (Exception e)
		{
		}

		VegaResources.setLocale(languageCodeCopy);

		return pdfByteArray;
	}

	private Hashtable<Integer, ArrayList<Byte>> alliances()
	{
		Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();

		for (int planetIndex = 0; planetIndex < this.game.getPlanetsCount(); planetIndex++)
		{
			Planet planet = this.game.getPlanets()[planetIndex];
			if (!planet.allianceExists() || !planet.isAllianceMember(this.playerIndex))
				continue;

			ArrayList<Byte> frameColors = new ArrayList<Byte>(); 
			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
				if (playerIndex != planet.getOwner() && planet.isAllianceMember(playerIndex))
					frameColors.add(this.game.getPlayers()[playerIndex].getColorIndex());

			frames.put(planetIndex, frameColors);
		}
		return frames;
	}

	private ArrayList<ScreenContentBoardMine> mines()
	{
		ArrayList<ScreenContentBoardMine> mines = new ArrayList<ScreenContentBoardMine>();

		if (game.getMines() != null)
		{
			for (Mine mine: game.getMines().values())
			{
				mines.add(new ScreenContentBoardMine(
						mine.getPositionX(),
						mine.getPositionY(),
						mine.getStrength()));
			}
		}

		return mines;
	}

	private void planets()
	{
		InventoryPdfChapter chapter = 
				new InventoryPdfChapter(
						VegaResources.PlanetsAlliancesSpies(false),
						VegaResources.YouHaveNoPlanets(false));

		this.game.setScreenContent(new ScreenContent());

		chapter.table = new InventoryPdfTable(18);

		// Columns headers
		chapter.table.cells.add(VegaResources.PlanetShort(false));
		chapter.table.colAlignRight[0] = false;

		chapter.table.cells.add(VegaResources.OwnerShort(false));
		chapter.table.colAlignRight[1] = false;

		chapter.table.cells.add(VegaResources.MoneySupplyShort(false));
		chapter.table.colAlignRight[2] = true;

		chapter.table.cells.add(VegaResources.MoneyProductionShort(false));
		chapter.table.colAlignRight[3] = true;

		chapter.table.cells.add(VegaResources.BattleshipProductionShort(false));
		chapter.table.colAlignRight[4] = true;

		chapter.table.cells.add(VegaResources.DefensiveBattleshipsShort(false));
		chapter.table.colAlignRight[5] = true;
		
		chapter.table.cells.add(VegaResources.BattleBonusShort(false));
		chapter.table.colAlignRight[6] = true;

		chapter.table.cells.add(VegaResources.BattleshipsShort(false));
		chapter.table.colAlignRight[7] = true;

		chapter.table.cells.add(VegaResources.SpyShort(false));
		chapter.table.colAlignRight[8] = true;

		chapter.table.cells.add(VegaResources.TransporterShort(false));
		chapter.table.colAlignRight[9] = true;

		chapter.table.cells.add(VegaResources.PatrolShort(false));
		chapter.table.colAlignRight[10] = true;

		chapter.table.cells.add(VegaResources.MinesweeperShort(false));
		chapter.table.colAlignRight[11] = true;

		chapter.table.cells.add(VegaResources.Mine50Short(false));
		chapter.table.colAlignRight[12] = true;

		chapter.table.cells.add(VegaResources.Mine100Short(false));
		chapter.table.colAlignRight[13] = true;

		chapter.table.cells.add(VegaResources.Mine250Short(false));
		chapter.table.colAlignRight[14] = true;

		chapter.table.cells.add(VegaResources.Mine500Short(false));
		chapter.table.colAlignRight[15] = true;

		chapter.table.cells.add(VegaResources.AllianceShort(false));
		chapter.table.colAlignRight[16] = false;

		chapter.table.cells.add(VegaResources.ForeignSpies(false));
		chapter.table.colAlignRight[17] = false;

		for (int index = 0; index < game.getPlanetsCount(); index++)
		{
			int planetIndex = game.getPlanetsSorted()[index];

			boolean visible = game.getPlanets()[planetIndex].areDetailsVisibleForPlayer(playerIndex);
			ArrayList<Integer> playersWithRadioStations = game.getPlanets()[planetIndex].getPlayersWithRadioStations();

			if (!visible && playersWithRadioStations.size() == 0)
				continue;

			Planet planet = game.getPlanets()[planetIndex];

			chapter.table.cells.add(CommonUtils.padString(" " + game.getPlanetNameFromIndex(planetIndex), 2));

			chapter.table.cells.add(planet.getOwner() == Player.NEUTRAL ?
					"" :
						game.getPlayers()[planet.getOwner()].getName());

			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getMoneySupply()) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getMoneyProduction()) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getBattleshipProduction()) : "?");

			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getDefensiveBattleshipsCount()) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getBonus()) : "?");
			chapter.table.cells.add(CommonUtils.convertToString(planet.getShipsCount(ShipType.BATTLESHIPS)));

			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.SPY)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.TRANSPORT)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.PATROL)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.MINESWEEPER)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.MINE50)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.MINE100)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.MINE250)) : "?");
			chapter.table.cells.add(visible ? CommonUtils.convertToString(planet.getShipsCount(ShipType.MINE500)) : "?");

			if (visible)
			{
				if (planet.allianceExists())
				{
					StringBuilder sb = new StringBuilder(this.game.getPlayers()[planet.getOwner()].getName() + ": "+
							planet.getBattleshipsCount(planet.getOwner()));

					for (int playerIndex = 0; playerIndex < game.getPlayersCount(); playerIndex++)
					{
						if (playerIndex == planet.getOwner() || !planet.isAllianceMember(playerIndex))
							continue;

						sb.append("\n" + this.game.getPlayers()[playerIndex].getName() + ": "+
								planet.getBattleshipsCount(playerIndex));
					}

					chapter.table.cells.add(sb.toString());
				}
				else
					chapter.table.cells.add("");
			}
			else
				chapter.table.cells.add("?");


			if (playersWithRadioStations.size() > 0)
			{
				StringBuilder playerNames = new StringBuilder();

				for (Integer playerIndex: playersWithRadioStations)
				{
					if (playerNames.length() > 0)
						playerNames.append("\n");
					playerNames.append(game.getPlayers()[playerIndex].getName());
				}

				chapter.table.cells.add(playerNames.toString());
			}
			else
			{
				chapter.table.cells.add("");
			}
		}

		ArrayList<ScreenContentBoardPlanet> planets = this.gameInformation.getDefaultBoard(this.alliances(), null);

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(
						planets,
						null,
						null,
						this.mines()));

		chapter.screenContent = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());
		chapter.screenContent.setPlanets(this.screenContentCopy.getPlanets());
		this.pdfData.chapters.add(chapter);
	}

	private void ships()
	{
		InventoryPdfChapter chapter = 
				new InventoryPdfChapter(
						VegaResources.Spaceships(false),
						VegaResources.NoSpaceships(false));

		ArrayList<ShipTravelTime> travelTimesRemaining = new ArrayList<ShipTravelTime>();
		ArrayList<ScreenContentBoardObject> objects = new ArrayList<ScreenContentBoardObject>();

		for (Ship ship: game.getShips())
		{
			if (ship.isToBeDeleted())
			{
				continue;
			}

			ShipTravelTime travelTimeRemaining = ship.getTravelTimeRemaining();
			travelTimeRemaining.ship = ship;
			travelTimesRemaining.add(travelTimeRemaining);
		}

		this.game.setScreenContent(new ScreenContent());
		chapter.table = new InventoryPdfTable(8);
		HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();

		// Column headers
		chapter.table.cells.add(VegaResources.Count(false));
		chapter.table.colAlignRight[0] = true;

		chapter.table.cells.add(VegaResources.Type(false));
		chapter.table.colAlignRight[1] = false;

		chapter.table.cells.add(VegaResources.Commander(false));
		chapter.table.colAlignRight[2] = false;

		chapter.table.cells.add(VegaResources.Start(false));
		chapter.table.colAlignRight[3] = false;

		chapter.table.cells.add(VegaResources.DestinationShort(false));
		chapter.table.colAlignRight[4] = false;

		chapter.table.cells.add(VegaResources.Freight(false));
		chapter.table.colAlignRight[5] = false;

		chapter.table.cells.add(VegaResources.Arrival(false));
		chapter.table.colAlignRight[6] = false;

		chapter.table.cells.add(VegaResources.Alliance(false));
		chapter.table.colAlignRight[7] = false;

		// Sort ships by time of arrival
		Collections.sort(travelTimesRemaining, new ShipTravelTime());

		for (ShipTravelTime travelTimeRemaining: travelTimesRemaining)
		{
			Ship ship2 = travelTimeRemaining.ship;
			
			Point shipPosition = ship2.getPositionOnDay(0);
			
			boolean drawSymbol = ship2.wasStoppedBefore() ||!ship2.isStartedRecently();
			
			ScreenContentBoardRadar radar = null;
			
			if (ship2.getType() == ShipType.PATROL && !ship2.isTransfer())
				radar = new ScreenContentBoardRadar(false, false, Game.PATROL_RADAR_RANGE);
			else if (ship2.getType() == ShipType.BLACK_HOLE)
				radar = new ScreenContentBoardRadar(false, true, Game.BLACK_HOLE_RANGE);
			
			ScreenContentBoardObject object =
					new ScreenContentBoardObject(
							ship2.hashCode(),
							shipPosition,
							ship2.isStopped() ? null : ship2.getPositionDestination(),
							drawSymbol ? ship2.getScreenDisplaySymbol() : (byte)-1,
							ship2.getOwnerColorIndex(this.game),
							drawSymbol,
							ship2.getPlanetIndexDestination() == Planet.NO_PLANET,
							radar);
			
			objects.add(object);
			
			if (ship2.getType() == ShipType.BLACK_HOLE)
				continue;

			if (this.game.getPlanetIndexFromPosition(ship2.getPositionStart()) != Planet.NO_PLANET)
				planetIndicesHighlighted.add(ship2.getPlanetIndexStart());

			if (!ship2.isStopped())
			{
				if (this.game.getPlanetIndexFromPosition(ship2.getPositionDestination()) != Planet.NO_PLANET)
					planetIndicesHighlighted.add(ship2.getPlanetIndexDestination());
			}

			chapter.table.cells.add(
					ship2.getType() == ShipType.BATTLESHIPS ?
							Integer.toString(ship2.getCount()) :
					"1");

			switch (ship2.getType())
			{
				case BATTLESHIPS:
					chapter.table.cells.add(VegaResources.Battleships(false));
					break;
				case SPY:
					if (ship2.isTransfer())
						chapter.table.cells.add(VegaResources.SpyTransfer(false));
					else
						chapter.table.cells.add(VegaResources.Spy(false));
					break;
				case PATROL:
					if (ship2.isTransfer())
						chapter.table.cells.add(VegaResources.PatrolTransfer(false));
					else
						chapter.table.cells.add(VegaResources.Patrol(false));
					break;
				case TRANSPORT:
					chapter.table.cells.add(VegaResources.Transporter(false));
					break;
				case MINE50:
					chapter.table.cells.add(VegaResources.Minelayer50(false));
					break;
				case MINE100:
					chapter.table.cells.add(VegaResources.Minelayer100(false));
					break;
				case MINE250:
					chapter.table.cells.add(VegaResources.Minelayer250(false));
					break;
				case MINE500:
					chapter.table.cells.add(VegaResources.Minelayer500(false));
					break;
				case MINESWEEPER:
					if (ship2.isTransfer())
						chapter.table.cells.add(VegaResources.MinesweeperTransfer(false));
					else
						chapter.table.cells.add(VegaResources.Minesweeper(false));
					break;
				default:
					chapter.table.cells.add("");
					break;
			}

			chapter.table.cells.add(ship2.getOwnerName(this.game));

			chapter.table.cells.add(CommonUtils.padString(" " + game.getSectorNameFromPosition(ship2.getPositionStart()), 2));

			if (ship2.isStopped())
				chapter.table.cells.add("");
			else
				chapter.table.cells.add(CommonUtils.padString(" " + game.getSectorNameFromPosition(ship2.getPositionDestination()), 2));

			if (ship2.getType() == ShipType.TRANSPORT)
			{
				chapter.table.cells.add(
						VegaResources.MoneyFreight(
								false, 
								Integer.toString(ship2.getCount())));
			}
			else
				chapter.table.cells.add("");

			travelTimeRemaining.year += this.game.getYear();
			chapter.table.cells.add(
					ship2.isStopped() ?
							"" :
								travelTimeRemaining.toOutputString(false));

			if (ship2.getType() == ShipType.BATTLESHIPS && ship2.isAlliance())
			{
				StringBuilder sb = new StringBuilder(ship2.getOwnerName(this.game) + ": "+
						ship2.getBattleshipsCount(ship2.getOwner()));

				for (int playerIndex = 0; playerIndex < game.getPlayersCount(); playerIndex++)
				{
					if (playerIndex == ship2.getOwner() || ship2.getBattleshipsCount(playerIndex) <= 0)
						continue;

					sb.append("\n" + this.game.getPlayers()[playerIndex].getName() + ": "+
							ship2.getBattleshipsCount(playerIndex));
				}

				chapter.table.cells.add(sb.toString());
			}
			else
				chapter.table.cells.add("");
		}

		ArrayList<ScreenContentBoardPlanet> planets = 
				this.gameInformation.getDefaultBoard(
						this.alliances(), 
						planetIndicesHighlighted);

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(
						planets,
						null,
						objects,
						this.mines()));

		chapter.screenContent = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());
		chapter.screenContent.setPlanets(this.screenContentCopy.getPlanets());

		this.pdfData.chapters.add(chapter);
	}

}
