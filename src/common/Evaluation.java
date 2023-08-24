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

import java.util.ArrayList;
import java.util.Hashtable;

import spielwitz.biDiServer.Tuple;

class Evaluation
{
	private static final double BAR_LENGTH_CHARS = 20;
	
	static Tuple<Integer,Integer> fight(Console console, String offenderName, int offenderCount, int defenderCount)
	{
		int offenderCountAfterFight = offenderCount;
		int defenderCountAfterFight = defenderCount;
		
		while (offenderCountAfterFight > 0 && defenderCountAfterFight > 0)
		{
			if (CommonUtils.getRandomInteger(1000) < 550)
				offenderCountAfterFight--;
			else
				defenderCountAfterFight--;
		}
		
		String offenderCountString = Integer.toString(offenderCount);
		String defenderCountString = Integer.toString(defenderCount);
		
		String offenderCountAfterFightString = Integer.toString(offenderCountAfterFight);
		String defenderCountAfterFightString = Integer.toString(defenderCountAfterFight);
		
		int digitsOffender = Math.max(offenderCountString.length(), offenderCountAfterFightString.length());
		int digitsDefender = Math.max(defenderCountString.length(), defenderCountAfterFightString.length());
		
		double maxCount = Math.max(1, Math.max(offenderCount, defenderCount));
		
		console.appendText(VegaResources.Fight(
				true, 
				offenderName,
				CommonUtils.padString(offenderCountString, digitsOffender), 
				getBar(offenderCount, maxCount),
				CommonUtils.padString(defenderCountString, digitsDefender), 
				getBar(defenderCount, maxCount)));
		
		console.lineBreak();
		
		if (offenderCount > 0 && defenderCount > 0)
		{
			console.appendText(VegaResources.Fight(
					true, 
					offenderName,
					CommonUtils.padString(offenderCountAfterFightString, digitsOffender), 
					getBar(offenderCountAfterFight, maxCount),
					CommonUtils.padString(defenderCountAfterFightString, digitsDefender), 
					getBar(defenderCountAfterFight, maxCount)));
			
			console.lineBreak();
		}
		
		return new Tuple<Integer,Integer>(offenderCountAfterFight, defenderCountAfterFight);
	}
	
	private static String getBar(double count, double maxCount)
	{
		int barLength = count > 0 ?
				Math.max(
						1, 
						CommonUtils.round(BAR_LENGTH_CHARS * count / maxCount)) :
				0;
		
		return 
				CommonUtils.getStringWithGivenLength('#', barLength) +
				CommonUtils.getStringWithGivenLength('-', (int)(BAR_LENGTH_CHARS - barLength));
	}
	
	private Game game;

	private ArrayList<ScreenContent> replay = new ArrayList<ScreenContent>(); 

	// First key: Hashcode of the sighted ship
	// Second key: Hashcode of the patrol
	// Values: Events
	private Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> events = new Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>>();

	@SuppressWarnings("unchecked")
	Evaluation(Game game)
	{
		this.game = game;

		this.replay = new ArrayList<ScreenContent>();

		this.game.getConsole().setMode(Console.ConsoleModus.EVALUATION);

		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.Evaluation(true),
				Colors.NEUTRAL);

		this.game.getConsole().clear();
		this.printDayBeginOfYear();
		this.game.getConsole().appendText(
				VegaResources.EvaluationBegins(true));
		this.waitForKeyPressed();

		this.processMoves();

		for (Ship ship: this.game.getShips())
		{
			ship.setStartedRecently(false);
		}
		
		this.game.launchNeutralFleet();
		Ship blackHoleShip = this.game.setBlackHoleDirection();

		this.game.updatePlanetList(false);
		this.game.updateBoard();

		this.game.getConsole().appendText(
				VegaResources.ShipsLaunched(true));
		this.waitForKeyPressed();

		this.processCapitulations();

		for (int day = 0; day <= Game.DAYS_OF_YEAR_COUNT; day++)
		{
			if (day > 0)
			{
				int shipsSequence[] = CommonUtils.getRandomList(this.game.getShips().size());

				for (int i = 0; i < shipsSequence.length; i ++)
				{
					this.moveShip(this.game.getShips().get(shipsSequence[i]), day);
				}
			}

			this.blackHoleDoWatch(blackHoleShip, day);
			this.patrolsDoWatch(day);

			for (int i = this.game.getShips().size() - 1; i >= 0; i--)
			{
				Ship ships = this.game.getShips().get(i);

				if (ships.isToBeDeleted())
				{
					this.game.getShips().remove(i);
				}
			}
		}

		this.game.updateBoard(Game.DAYS_OF_YEAR_COUNT);
		this.game.updatePlanetList(false);

		for (Ship ships: this.game.getShips())
		{
			if (ships.isToBeTurned())
			{
				ships.turn();
			}
			else if (!ships.isStopped())
			{
				ships.incrementYearCount();
			}
		}

		this.checkIfPlayerIsDead();

		this.game.getConsole().setLineColor(Colors.WHITE);
		this.printDayEndOfYear();

		this.game.getConsole().appendText(
				VegaResources.PlanetsProducing(true));
		this.waitForKeyPressed();

		for (Planet planet: this.game.getPlanets())
		{
			planet.produceMoneySupply();
			planet.produceBattleships();
		}

		this.game.updatePlanetList(false);

		this.game.getConsole().appendText(VegaResources.EvaluationEnd(true));
		this.waitForKeyPressed();

		this.game.getConsole().enableEvaluationProgressBar(false);
		this.game.getConsole().setMode(Console.ConsoleModus.TEXT_INPUT);

		this.reducePatrolEvents(this.events);

		this.game.setReplayLast((ArrayList<ScreenContent>) CommonUtils.klon(this.replay));

		this.game.incYear();
		this.game.calculateScores();
		this.game.prepareYear();			
	}

	private void addScreenSnapshotToReplay(int day)
	{
		this.game.updateBoard(day);
		ScreenContent cont = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());
		cont.setPlanetEditor(null);
		cont.setSnapshot();
		cont.getConsole().clearKeys();
		cont.getConsole().setProgressBarDay(day);
		this.replay.add(cont);
	}

	private void blackHoleDoWatch(Ship blackHoleShip, int day)
	{
		if (blackHoleShip == null)
			return;
		
		for (Ship ship: this.game.getShips())
		{
			if (ship == blackHoleShip ||
				ship.isToBeDeleted())
			{
				continue;
			}
			
			if (blackHoleShip.getPositionOnDay(day).distance(ship.getPositionOnDay(day)) < Game.BLACK_HOLE_RANGE)
			{
				this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
				
				switch (ship.getType())
				{
					case BATTLESHIPS:
						this.game.getConsole().appendText(
								VegaResources.BlackHoleDestroyedBattleships(
										true, 
										Integer.toString(ship.getCount())));
						break;
						
					case SPY:
						this.game.getConsole().appendText(
								VegaResources.BlackHoleSpy(true));
						break;
						
					case TRANSPORT:
						this.game.getConsole().appendText(
								VegaResources.BlackHoleTransport(true));
						break;
						
					case PATROL:
						this.game.getConsole().appendText(
								VegaResources.BlackHolePatrol(true));
						break;
						
					case MINE50:
					case MINE100:
					case MINE250:
					case MINE500:
						this.game.getConsole().appendText(
								VegaResources.BlackHoleMine(true));
						break;
						
					case MINESWEEPER:
						this.game.getConsole().appendText(
								VegaResources.BlackHoleMinesweeper(true));
						break;
						
					default:
						continue;
				}
		
				this.game.updateBoard(
						this.game.getSimpleMarkedPosition(ship.getPositionOnDay(day)),
						day);

				this.waitForKeyPressed();
				
				ship.setToBeDeleted();
			}
		}
	}
	
	private void checkForArrival(Ship ship, int day)
	{
		if (ship.getType() == ShipType.BLACK_HOLE)
			return;
		
		double distanceTotal = ship.getPositionStart().distance(ship.getPositionDestination());
		double distanceCurrent = ship.getPositionStart().distance(ship.getPositionOnDay(day));

		if (distanceCurrent <= distanceTotal - Point.PRECISION)
		{
			return;
		}

		int planetIndex = ship.getPlanetIndexDestination();
		Planet planet = null;

		if (planetIndex != Planet.NO_PLANET)
			planet = this.game.getPlanets()[planetIndex];
		
		String shipOwnerName = ship.getOwnerName(this.game); 
				
		if (planet == null)
		{
			switch (ship.getType())
			{
				case PATROL:
				case MINESWEEPER:
					this.addScreenSnapshotToReplay(day);
					ship.setToBeTurned();
					break;
					
				case MINE50:
				case MINE100:
				case MINE250:
				case MINE500:
					this.placeMine(ship);

					this.game.updateBoard(
							this.game.getSimpleMarkedPosition(ship.getPositionOnDay(day)),
							day);

					this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
					
					this.game.getConsole().appendText(
							VegaResources.MinePlanted(
									true, 
									shipOwnerName));
					this.waitForKeyPressed();
					ship.setToBeDeleted();

					Point sectorShip = ship.getSectorOnDay(day);
					int shipsSequence[] = CommonUtils.getRandomList(this.game.getShips().size());

					for (int i = 0; i < shipsSequence.length; i++)
					{
						this.checkForMines(this.game.getShips().get(shipsSequence[i]), sectorShip, day);
					}
					break;
					
				default:
					break;
			}
		}
		else
		{
			String planetName = this.game.getPlanetNameFromIndex(planetIndex);
			this.game.getConsole().setLineColor(planet.getOwnerColorIndex(this.game));
			
			this.printDayEvent(day);

			if (ship.getType() == ShipType.BATTLESHIPS)
			{
				boolean attack = true;
				
				for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
				{
					if (planet.isPlayerInvolved(playerIndex) && ship.isPlayerInvolved(playerIndex))
					{
						attack = false;
						break;
					}
				}
				
				if (attack)
					this.battleshipsAttack(planet, ship, planetIndex, day);
				else
				{
					planet.mergeBattleships(this.game.getPlayersCount(), ship);

					this.game.getConsole().appendText(
							VegaResources.BattleshipsArrivedAtPlanet(
									true,
									Integer.toString(ship.getCount()),
									planetName));
				}
				
				this.game.updatePlanetList(false);
			}
			else
			{
				boolean crash = planet.getOwner() != ship.getOwner();
				
				if (!crash)
					planet.incrementShipsCount(ship.getType(), 1);
				
				switch (ship.getType())
				{
					case SPY:
						if (ship.isTransfer())
						{
							this.game.getConsole().appendText(
									crash ?
											VegaResources.SpyCrashed(
													true,
													planetName) :
											VegaResources.SpyArrived(
													true,
													planetName));
						}
						else
						{
							planet.setRadioStation(ship.getOwner());
							
							if (!crash)
								planet.incrementShipsCount(ship.getType(), -1);
							
							this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
							
							this.game.getConsole().appendText(
								VegaResources.SpyDropped(
										true,
										shipOwnerName,
										planetName));
						}
						break;
						
					case PATROL:
						this.game.getConsole().appendText(
								crash ?
										VegaResources.PatrolCrashed(
												true, 
												planetName) :
										VegaResources.PatrolArrived(
												true, 
												planetName));
						break;
						
					case TRANSPORT:
						planet.addToMoneySupply(ship.getCount());
						
						this.game.getConsole().appendText(
								crash ?
									VegaResources.TransporterCrashed(
											true, 
											planetName) :
									VegaResources.TransporterArrived(
											true, 
											planetName));
						break;
						
					case MINE50:
					case MINE100:
					case MINE250:
					case MINE500:
						this.game.getConsole().appendText(
								crash ?
										VegaResources.MinelayerCrashed(
												true, 
												planetName) :
										VegaResources.MinelayerArrived(
												true, 
												planetName));
						break;
						
					case MINESWEEPER:
						this.game.getConsole().appendText(
								crash ?
										VegaResources.MinesweeperCrashed(
												true,
												planetName) :
										VegaResources.MinesweeperArrived(
												true,
												planetName));
						break;
					
					default:
						break;
				}
			}
			
			this.game.updateBoard(
					this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
					day);

			this.waitForKeyPressed();
			ship.setToBeDeleted();
		}
	}  		

	private void checkForMines(Ship ship, Point sector, int day)
	{
		if (ship.isToBeDeleted() ||
				sector == null ||
				this.game.getMines() == null || 
				this.game.getMines().size() == 0)
		{
			return;
		}

		Point positionShip = ship.getSectorOnDay(day);
		if (positionShip == null || !positionShip.equals(sector))
		{
			return;
		}

		Mine mine = this.game.getMines().get(sector.getString());
		if (mine == null)
			return;

		boolean deleteShip = false;
		
		if (ship.getType() == ShipType.BLACK_HOLE)
		{
			this.game.getMines().remove(sector.getString());
			
			this.printDayEvent(day);
			
			this.game.getConsole().appendText(
					VegaResources.BlackHoleMines(
							true, 
							Integer.toString(mine.getStrength())));
		}
		else
		{
			String playerName = ship.getOwnerName(this.game);
			this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
	
			if (ship.getType() == ShipType.BATTLESHIPS)
			{
				this.printDayEvent(day);
	
				if (ship.getCount() >= mine.getStrength())
				{
					this.game.getConsole().appendText(
							VegaResources.BattleshipsKilledByMine2(
									true,
									playerName,
									Integer.toString(Math.min(ship.getCount(),mine.getStrength()))));
	
					ship.subtractBattleships(mine.getStrength(), ship.getOwner());
	
					if (ship.getCount() <= 0)
						deleteShip = true;
	
					this.game.getMines().remove(sector.getString());
				}
				else
				{
					this.game.getConsole().appendText(
							VegaResources.BattleshipsKilledByMine(
									true, 
									playerName,
									Integer.toString(Math.min(ship.getCount(),mine.getStrength()))));
	
					deleteShip = true;
	
					mine.setStrength(mine.getStrength() - ship.getCount());
				}
			}
			else if (ship.getType() == ShipType.MINESWEEPER)
			{
				this.printDayEvent(day);
				this.game.getConsole().appendText (
						VegaResources.MessageFromSector(true,
								ship.getOwnerName(this.game),
								Game.getSectorNameFromPositionStatic(
										new Point(mine.getPositionX(), mine.getPositionY())
										)));
				this.game.getConsole().lineBreak();
				this.game.getConsole().appendText (
						VegaResources.MineFieldSwept(
								true,
								Integer.toString(mine.getStrength())));
	
	
				this.game.getMines().remove(sector.getString());
			}
			else
				return;
	
		}
		
		this.game.updateBoard(
				this.game.getSimpleMarkedPosition(ship.getPositionOnDay(day)),
				day);

		this.waitForKeyPressed();

		if (deleteShip)
			ship.setToBeDeleted();
	}

	private void checkIfPlayerIsDead()
	{
		for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
		{
			if (this.game.getPlayers()[playerIndex].isDead())
				continue;

			boolean playerHasPlanet = false;
			for (Planet planet: game.getPlanets())
			{
				if (planet.getOwner() == playerIndex)
				{
					playerHasPlanet = true;
					break;
				}
			}
			if (playerHasPlanet)
				continue;

			boolean playerHasShips = false;

			for (Ship ship: game.getShips())
			{
				if (ship.getOwner() == playerIndex ||
					ship.getBattleshipsCount(playerIndex) > 0)
				{
					playerHasShips = true;
					break;
				}
			}

			if (playerHasShips)
				continue;

			this.game.getPlayers()[playerIndex].setDead(true);

			this.game.getConsole().setLineColor(this.game.getPlayers()[playerIndex].getColorIndex());
			this.printDayEndOfYear();
			this.game.getConsole().appendText(
					VegaResources.PlayerGameOver(
							true, 
							this.game.getPlayers()[playerIndex].getName()));
			this.waitForKeyPressed();
		}
	}

	private void battleshipsAttack(Planet planet, Ship ship, int planetIndex, int day)
	{
		String playerNameOffender = ship.getOwnerName(this.game);

		int offenderCount = ship.getCount();
		int defenderCount = planet.getShipsCount(ShipType.BATTLESHIPS) + planet.getDefensiveBattleshipsCount();
		int defenderCountStart = defenderCount;

		this.game.getConsole().appendText(
				VegaResources.PlanetIsAttacked(
						true,
						this.game.getPlanetNameFromIndex(planetIndex)));

		this.game.getConsole().lineBreak();
		this.game.getConsole().setLineColor(Colors.WHITE);
		
		Tuple<Integer,Integer> countsAfterFight = fight(
				this.game.getConsole(), 
				playerNameOffender,
				offenderCount, 
				defenderCount);
		
		offenderCount = countsAfterFight.getE1();
		defenderCount = countsAfterFight.getE2();

		if (offenderCount > defenderCount)
		{
			ship.subtractBattleships(ship.getCount() - offenderCount,ship.getOwner());

			planet.conquer(this.game.getPlayersCount(), ship.getOwner(), ship);

			this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
			
			if (ship.getOwner() == Player.NEUTRAL)
			{
				this.game.getConsole().appendText(
						VegaResources.PlanetConqueredNeutral(
								true));
			}
			else
			{
				this.game.getConsole().appendText(
						VegaResources.PlanetConquered(
								true,
								playerNameOffender));
			}
		}
		else
		{
			planet.subtractBattleshipsCount(this.game.getPlayersCount(), defenderCountStart - defenderCount, planet.getOwner(), true, true);

			this.game.getConsole().appendText(
					VegaResources.AttackFailed(true));					
		}
	}

	private void moveShip(Ship ship, int day)
	{
		if (ship.isToBeDeleted() || ship.isToBeTurned() || ship.isStopped())
		{
			return;
		}

		Point sectorCurrent = ship.getSectorOnDay(day);

		if (sectorCurrent != null)
		{
			Point sectorPrevious = ship.getSectorOnDay(day-1);

			if (sectorPrevious == null || !sectorCurrent.equals(sectorPrevious))
			{
				this.checkForMines(ship, sectorCurrent, day);
			}
		}

		if (ship.isToBeDeleted())
		{
			return;
		}

		this.checkForArrival(ship, day);
	}

	private void patrolAddEvent(
			int patrolHashCode, 
			int otherShipHashCode, 
			Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> list)
	{
		Hashtable<Integer, ArrayList<ScreenContent>> patrols = list.get(otherShipHashCode);

		if (patrols == null)
		{
			patrols = new Hashtable<Integer, ArrayList<ScreenContent>>();
			list.put(otherShipHashCode, patrols);
		}

		ArrayList<ScreenContent> eventList = patrols.get(patrolHashCode);

		if (eventList == null)
		{
			eventList = new ArrayList<ScreenContent>();
			patrols.put(patrolHashCode, eventList);
		}

		eventList.add(this.replay.get(this.replay.size() - 1));
	}

	private boolean patrolCombat(Ship patrol, Ship otherPatrol, int day)
	{
		Point patrolFlightDirection = patrol.getPositionDestination().subtract(patrol.getPositionStart());
		Point patrolShootDirection = otherPatrol.getPositionOnDay(day).subtract(patrol.getPositionOnDay(day));
		int patrolShootAngle = patrolFlightDirection.getAngleBetweenVectors(patrolShootDirection);

		Point otherPatrolFlightDirection = otherPatrol.getPositionDestination().subtract(otherPatrol.getPositionStart());
		Point otherPatrolShootDirection = patrol.getPositionOnDay(day).subtract(otherPatrol.getPositionOnDay(day));
		int otherPatrolShootAngle = otherPatrolFlightDirection.getAngleBetweenVectors(otherPatrolShootDirection);

		if (otherPatrolShootAngle == patrolShootAngle)
		{
			return CommonUtils.getRandomInteger(10) < 5;
		}
		else if (otherPatrolShootAngle < patrolShootAngle)
		{
			return true;
		}

		return false;
	}
	
	private void patrolEvent(
			Ship patrol1, 
			Ship otherShip1, 
			int day)
	{
		if (patrol1.isToBeDeleted() || otherShip1.isToBeDeleted() || patrol1.isTransfer() || patrol1.isStopped())
		{
			return;
		}

		if (patrol1.getOwner() == otherShip1.getOwner())
		{
			return;
		}

		if (otherShip1.getType() == ShipType.BATTLESHIPS &&
				otherShip1.getCount() > Game.PATROL_CAPUTURES_BATTLESHIPS_COUNT_MAX)
		{
			return;
		}

		if (otherShip1.getType() == ShipType.BLACK_HOLE ||
			otherShip1.isPlayerInvolved(patrol1.getOwner()))
		{
			return;
		}				

		boolean swapObjects = false;

		if (otherShip1.getType() == ShipType.PATROL &&
				!otherShip1.isTransfer())
		{
			swapObjects = this.patrolCombat(patrol1, otherShip1, day); 
		}

		Ship patrol = swapObjects ? otherShip1 : patrol1;
		Ship otherShip = swapObjects ? patrol1 : otherShip1;

		this.printDayEvent(day);			
		this.game.getConsole().setLineColor(patrol.getOwnerColorIndex(this.game));

		Point otherShipPosition = otherShip.getPositionOnDay(day);

		String patrolOwnerName = patrol.getOwnerName(this.game);
		String otherShipOwnerName = otherShip.getOwnerName(this.game);
		String otherShipDestinationName = this.game.getSectorNameFromPosition(otherShip.getPositionDestination());

		this.game.updateBoard(
				null,
				this.game.getSimpleMarkedPosition(otherShipPosition),
				patrol.hashCode(),
				Player.NEUTRAL, 
				day);

		this.game.getConsole().setLineColor(patrol.getOwnerColorIndex(this.game));

		if (otherShip.getType() == ShipType.SPY)
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedSpy(
							true, 
							patrolOwnerName, 
							otherShipDestinationName, 
							otherShipOwnerName));
		else if (otherShip.getType() == ShipType.TRANSPORT)
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedTransporter(
							true, 
							patrolOwnerName, 
							otherShipDestinationName, 
							otherShipOwnerName));
		else if (otherShip.getType() == ShipType.MINESWEEPER)
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedMinesweeper(
							true, 
							patrolOwnerName, 
							otherShipDestinationName, 
							otherShipOwnerName));
		else if (otherShip.getType() == ShipType.MINE50 || 
				otherShip.getType() == ShipType.MINE100 ||
				otherShip.getType() == ShipType.MINE250 ||
				otherShip.getType() == ShipType.MINE500)
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedMinelayer(
							true, 
							patrolOwnerName, 
							otherShipDestinationName, 
							otherShipOwnerName));
		else if (otherShip.getType() == ShipType.PATROL)
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedPatrol(
							true, 
							patrolOwnerName, 
							otherShipDestinationName, 
							otherShipOwnerName));
		else if (otherShip.getType() == ShipType.BATTLESHIPS)
		{
			this.game.getConsole().appendText(
					VegaResources.PatrolCapturedBattleships(
							true, 
							patrolOwnerName, 
							Integer.toString(otherShip.getCount()), 
							otherShipDestinationName, 
							otherShipOwnerName));
		}

		otherShip.capture(
				patrol.getOwner(),
				otherShipPosition);

		this.game.updateBoard(
				null,
				this.game.getSimpleMarkedPosition(otherShipPosition),
				patrol.hashCode(),
				Player.NEUTRAL, 
				day);

		this.waitForKeyPressed();

		this.patrolAddEvent(
				otherShip.hashCode(),
				patrol.hashCode(),
				this.events);
	}

	private void patrolsDoWatch(int day)
	{
		ArrayList<Ship> eventPatrols = new ArrayList<Ship>();
		ArrayList<Ship> eventOtherShips = new ArrayList<Ship>();

		for (Ship patrol: this.game.getShips())
		{
			if (!patrol.isToBeDeleted() && 
					patrol.getType() == ShipType.PATROL &&
					!patrol.isTransfer())
			{
				Point patrolPosition = patrol.getPositionOnDay(day);

				for (Ship otherShip: this.game.getShips())
				{
					if (otherShip == patrol || otherShip.isToBeDeleted())
					{
						continue;
					}

					Point otherShipPosition = otherShip.getPositionOnDay(day);

					if (patrolPosition.distance(otherShipPosition) > Game.PATROL_RADAR_RANGE + Point.PRECISION)
					{
						continue;
					}

					eventPatrols.add(patrol);
					eventOtherShips.add(otherShip);
				}
			}
		}

		if (eventPatrols.size() == 0)
		{
			return;
		}

		int seq[] = CommonUtils.getRandomList(eventPatrols.size());

		for (int i = 0; i < seq.length; i++)
		{
			int eventIndex = seq[i];

			this.patrolEvent(
					eventPatrols.get(eventIndex), 
					eventOtherShips.get(eventIndex), 
					day);
		}
	}

	private void placeMine(Ship obj)
	{
		if (this.game.getMines() == null)
			this.game.setMines(new Hashtable<String,Mine>());

		int strength = 0;

		if (obj.getType() == ShipType.MINE50)
			strength = 50;
		else if (obj.getType() == ShipType.MINE100)
			strength = 100;
		else if (obj.getType() == ShipType.MINE250)
			strength = 250;
		else
			strength = 500;

		String positionString = obj.getPositionDestination().getString();
		Mine mine = this.game.getMines().get(positionString);

		if (mine == null)
		{
			this.game.getMines().put(positionString, new Mine((int)obj.getPositionDestination().x, (int)obj.getPositionDestination().y, strength));
		}
		else
			mine.addToStrength(strength);
	}

	private void printDayBeginOfYear()
	{
		this.game.getConsole().appendText(">>> ");
		this.game.getConsole().enableEvaluationProgressBar(true);
	}

	private void printDayEndOfYear()
	{
		this.game.getConsole().appendText(">>> ");
		this.game.getConsole().setEvaluationProgressBarDay(Game.DAYS_OF_YEAR_COUNT);
	}

	private void printDayEvent(int day)
	{
		if (day < 1)
			printDayBeginOfYear();
		else if (day >= Game.DAYS_OF_YEAR_COUNT)
			printDayEndOfYear();
		else
		{		
			this.game.getConsole().appendText(">>> ");
			this.game.getConsole().setEvaluationProgressBarDay(day);
		}
	}

	private void processAllianceChanges()
	{
		Hashtable<Integer, Hashtable<Integer,Integer>> allianceChangesPerPlanet =
				new Hashtable<Integer, Hashtable<Integer,Integer>>();

		for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
		{
			ArrayList<Move> moves = this.game.getMoves().get(playerIndex);

			if (moves == null)
			{
				continue;
			}

			for (Move move: moves)
			{
				boolean[] allianceChanges = move.getAllianceChanges();

				if (allianceChanges == null)
				{
					continue;
				}

				int bitSum = 0;

				for (int playerIndex2 = 0; playerIndex2 < this.game.getPlayersCount(); playerIndex2++)
				{
					if (allianceChanges[playerIndex2])
					{
						bitSum += Math.pow(2, playerIndex2);
					}
				}

				Hashtable<Integer,Integer> allianceChangesPerPlayer = 
						allianceChangesPerPlanet.get(move.getPlanetIndex());

				if (allianceChangesPerPlayer == null)
				{
					allianceChangesPerPlayer = new Hashtable<Integer,Integer>();
					allianceChangesPerPlanet.put(move.getPlanetIndex(), allianceChangesPerPlayer);
				}

				allianceChangesPerPlayer.put(playerIndex, bitSum);
			}
		}

		for (int planetIndex: allianceChangesPerPlanet.keySet())
		{
			Planet planet = this.game.getPlanets()[planetIndex];

			Hashtable<Integer,Integer> allianceChangesPerPlayer = allianceChangesPerPlanet.get(planetIndex);

			boolean terminateAlliance = false;

			for (int playerIndex: allianceChangesPerPlayer.keySet())
			{
				if (allianceChangesPerPlayer.get(playerIndex) == 0 &&
						planet.isAllianceMember(playerIndex))
				{
					terminateAlliance = true;
					break;
				}
			}

			if (terminateAlliance)
			{
				for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
				{
					if (playerIndex == planet.getOwner())
					{
						continue;
					}

					int battleshipsCount = planet.getBattleshipsCount(playerIndex);

					if (battleshipsCount > 0)
					{
						Ship obj = new Ship(
								Planet.NO_PLANET,
								Planet.NO_PLANET,
								this.game.getPlanets()[planetIndex].getPosition(),
								this.game.getPlanets()[planetIndex].getPosition(),
								ShipType.BATTLESHIPS,
								battleshipsCount,
								playerIndex,
								false,
								false,
								null);

						obj.setStopped(true);

						this.game.getShips().add(obj);

						this.game.updateBoard(this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE), 0);

						this.game.getConsole().setLineColor(this.game.getPlayers()[playerIndex].getColorIndex());

						this.game.getConsole().appendText(
								VegaResources.BattleshipsMustLeavePlanet(
										true,
										Integer.toString(obj.getCount()),
										this.game.getPlayers()[playerIndex].getName(),
										this.game.getPlanetNameFromIndex(planetIndex)));
						this.game.getConsole().lineBreak();
						this.game.getConsole().appendText(
								VegaResources.BattleshipsWaiting(true));
						this.waitForKeyPressed();
					}
				}

				planet.dissolveAlliance();

				continue;
			}

			int planetOwner = planet.getOwner();

			if (!allianceChangesPerPlayer.containsKey(planetOwner))
			{
				continue;
			}

			int bitSetOwner = allianceChangesPerPlayer.get(planetOwner);
			boolean abortAllianceChange = false;

			for (int playerIndex = 0; playerIndex < this.game.getPlayers().length; playerIndex++)
			{
				if (playerIndex != planetOwner &&
						(bitSetOwner & (int)Math.pow(2, playerIndex)) > 0)
				{
					if (!allianceChangesPerPlayer.containsKey(playerIndex))
					{
						abortAllianceChange = true;
						break;
					}

					int bitSetMember = allianceChangesPerPlayer.get(playerIndex);

					if (bitSetOwner != bitSetMember)
					{
						abortAllianceChange = true;
						break;
					}
				}
			}

			if (abortAllianceChange)
			{
				continue;
			}

			for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
			{
				if (playerIndex != planetOwner &&
						(bitSetOwner & (int)Math.pow(2, playerIndex)) > 0)
				{
					planet.addPlayerToAlliance(this.game.getPlayersCount(), playerIndex);
				}
			}	
		}
	}

	private void processCapitulations()
	{
		int[] shipsSequence = CommonUtils.getRandomList(game.getShips().size());

		for (int i = 0; i < game.getShips().size(); i++)
		{
			Ship ship = game.getShips().get(shipsSequence[i]);

			if (!ship.isToBeDeleted() && ship.getType() == ShipType.CAPITULATION)
			{
				ship.setToBeDeleted();
				
				this.printDayEvent(0);

				this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
				this.game.getConsole().appendText(
						VegaResources.PlayerCapitulated(
								true,
								ship.getOwnerName(this.game)));

				for (Planet planet: this.game.getPlanets())
				{
					planet.changeOwner(ship.getOwner(), Player.NEUTRAL);
				}

				for (Ship ship2: this.game.getShips())
				{
					if (ship2.isPlayerInvolved(ship.getOwner()))
					{
						ship2.setToBeDeleted();
					}
				}

				this.game.updatePlanetList(false);
				this.game.updateBoard(0);
				
				this.waitForKeyPressed();
			}
		}
	}

	private void processMoves()
	{
		int[] playersSequence = CommonUtils.getRandomList(this.game.getPlayersCount());

		for (int i = 0; i < this.game.getPlayersCount(); i++)
		{
			int playerIndex = playersSequence[i];
			ArrayList<Move> movesOfPlayer = this.game.getMoves().get(playerIndex);

			if (movesOfPlayer == null)
				continue;

			for (Move move: movesOfPlayer)
			{
				if (move.getStopLabel() != null)
				{
					Ship ship = null;

					for (Ship ship2: this.game.getShips())
					{
						if (ship2.getStopLabel() != null && ship2.getStopLabel().equals(move.getStopLabel()))
						{
							ship = ship2;
							break;
						}
					}

					ship.setPositionStart(ship.getPositionDestination());
					ship.setPlanetIndexStart(Planet.NO_PLANET);
					ship.setPlanetIndexDestination(move.getPlanetIndex());
					ship.setPositionDestination(this.game.getPlanets()[move.getPlanetIndex()].getPosition());
					ship.setStopped(false);
				}
				else if (move.getShip() != null)
				{
					Ship ship = move.getShip();
					Planet planet = this.game.getPlanets()[ship.getPlanetIndexStart()];

					if (ship.getType() == ShipType.BATTLESHIPS)
					{
						if (ship.isAlliance())
						{
							boolean ok = true;

							if (planet.getAllianceMembers() == null || planet.getAllianceMembers().length <= 1)
								ok = false;
							else if (!(planet.getBattleshipsCount(playerIndex) > 0  &&
									planet.getShipsCount(ShipType.BATTLESHIPS) >= ship.getCount()))
								ok = false;

							if (ok)
							{
								int[] reductions = 
										planet.subtractBattleshipsCount(this.game.getPlayersCount(), ship.getCount(), playerIndex, true, false);

								Alliance alliance = planet.copyAllianceStructure(reductions);
								if (alliance == null)
									ok = false;
								else
									ship.setAlliance(alliance);
							}

							if (!ok)
							{
								this.game.updateBoard(this.game.getSimpleFrameObjekt(ship.getPlanetIndexStart(), Colors.WHITE), 0);
								this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
								this.game.getConsole().appendText(
										VegaResources.BattleshipsNotLaunchedFromPlanet(
												true,
												ship.getOwnerName(this.game),
												this.game.getPlanetNameFromIndex(ship.getPlanetIndexStart())));
								this.waitForKeyPressed();
								continue;
							}
						}
						else
						{
							if (planet.getBattleshipsCount(ship.getOwner()) < ship.getCount())
							{
								this.game.updateBoard(this.game.getSimpleFrameObjekt(ship.getPlanetIndexStart(), Colors.WHITE), 0);
								this.game.getConsole().setLineColor(ship.getOwnerColorIndex(this.game));
								this.game.getConsole().appendText(
										VegaResources.BattleshipsNotLaunchedFromPlanet(
												true,
												ship.getOwnerName(this.game),
												this.game.getPlanetNameFromIndex(ship.getPlanetIndexStart())));
								this.waitForKeyPressed();

								continue;
							}

							if (planet.isAllianceMember(playerIndex))
								planet.subtractBattleshipsCount(this.game.getPlayersCount(), ship.getCount(), playerIndex, false, false);
							else
								planet.decrementShipsCount(ShipType.BATTLESHIPS, ship.getCount());
						}

						this.game.getShips().add(ship);
					}
					else
					{
						if (ship.getType() == ShipType.TRANSPORT)
						{
							planet.subtractMoneySupply(ship.getCount());
						}

						planet.decrementShipsCount(ship.getType(), 1);
						this.game.getShips().add(ship);  					
					}
				}
				else if (move.getAllianceChanges() != null)
				{
					// Alliance changes will be processed next
				}
				else if (move.getPlanetAfter() != null)
				{
					this.game.getPlanets()[move.getPlanetIndex()].acceptPlanetDataChange(move.getPlanetAfter());
				}
			}
		}

		this.processAllianceChanges();

		this.game.setMoves(new Hashtable<Integer, ArrayList<Move>>());
	}

	private void reducePatrolEvents(
			Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> list)
	{
		for (Integer i1: list.keySet())
		{
			Hashtable<Integer, ArrayList<ScreenContent>> patrols = list.get(i1);

			for (Integer i2: patrols.keySet())
			{
				ArrayList<ScreenContent> eventList = patrols.get(i2);

				for (int eventIndex = 0; eventIndex < eventList.size(); eventIndex++)
				{
					if (eventIndex == 0 || eventIndex == eventList.size() - 1)
					{
						continue;
					}

					this.replay.remove(eventList.get(eventIndex));
				}
			}
		}
	}
	
	private void waitForKeyPressed()
	{
		this.game.getConsole().waitForKeyPressedEvaluation();

		ScreenContent cont = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());
		cont.setPlanetEditor(null);
		this.replay.add(cont);
	}
}
