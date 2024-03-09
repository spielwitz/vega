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
import java.util.Hashtable;

class EvaluationEvents
{
	private ArrayList<EvaluationEvent> events;
	private ArrayList<EvaluationEventsShip> ships;
	
	private transient Game game;
	private transient EvaluationEvent event;
	private transient Hashtable<Ship, Integer> shipMap;
	
	EvaluationEvents(Game game)
	{
		this.game = game;
		
		this.events = new  ArrayList<EvaluationEvent>();
		this.ships = new ArrayList<EvaluationEventsShip>();
		
		this.shipMap = new Hashtable<Ship, Integer>();
		
		this.event = new EvaluationEvent(-1);
		
		this.addAllPlanets();
		this.addAllMines();
		
		for (Ship ship: game.getShips())
		{
			this.showShip(0, ship);
		}
	}
	
	void addConsoleLine(int day, String text, byte colorIndex)
	{
		EvaluationEvent event = this.getEvent(day);
		
		if (event.consoleLines == null)
		{
			event.consoleLines = new ArrayList<EvaluationEventsConsoleLine>();
		}
		
		EvaluationEventsConsoleLine eConsoleLine = new EvaluationEventsConsoleLine();
		eConsoleLine.text = text;
		eConsoleLine.col = colorIndex;
		
		event.consoleLines.add(eConsoleLine);
	}
	
	void addHighlight(int day, Point pos, EvaluationEventsHighlightType type)
	{
		EvaluationEvent event = this.getEvent(day);
		
		if (event.highlights == null)
		{
			event.highlights = new ArrayList<EvaluationEventsHighlight>();
		}
		
		EvaluationEventsHighlight eHighlight = new EvaluationEventsHighlight();
		eHighlight.pos = pos;
		eHighlight.type = type;
		
		event.highlights.add(eHighlight);
	}
	
	void addStartedShips()
	{
		for (Ship ship: game.getShips())
		{
			if (!this.shipMap.containsKey(ship)) 
			{
				this.showShip(0, ship);
			}
		}
	}
	
	void addAllPlanets()
	{
		for (Planet planet: game.getPlanets())
		{
			this.addPlanetInfo(0, planet);
		}
	}
	
	void addAllMines()
	{
		for (Mine mine: game.getMines().values())
		{
			this.setMine(0, new Point(mine.getPositionX(), mine.getPositionY()), mine.getStrength());
		}
	}
	
	void setMine(int day, Point pos, int strength)
	{
		EvaluationEvent event = this.getEvent(day);
		
		if (event.mines == null)
		{
			event.mines = new Hashtable<Point,Integer>();
		}
		
		event.mines.put(pos, strength);
	}
	
	void waitForKeyPressed()
	{
		if (this.event != null)
		{
			this.events.add(this.event);
			this.event = null;
		}
	}
	
	private EvaluationEvent getEvent(int day)
	{
		if (this.event == null)
		{
			this.event = new EvaluationEvent(day);
		}
		else
		{
			this.event.day = day;
		}
		
		return this.event;
	}
	
	private int getShipIndex(Ship ship)
	{
		Integer index = this.shipMap.get(ship);
		
		if (index == null)
		{
			EvaluationEventsShip eShip = new EvaluationEventsShip();
			
			eShip.posStart = ship.getPositionStart();
			eShip.posDestination = ship.getPositionDestination();
			eShip.destIsPlanet = ship.getPlanetIndexDestination() != Planet.NO_PLANET;
			eShip.speed = Ship.getSpeed(ship.getType(), ship.isTransfer());
			
			this.ships.add(eShip);
			index = this.ships.size() - 1;
			this.shipMap.put(ship, index);
		}
		
		return index;
	}
	
	void addPlanetInfo(int day, Planet planet)
	{
		EvaluationEvent event = this.getEvent(day);
		
		if (event.planets == null)
		{
			event.planets = new Hashtable<Integer,EvaluationEventsPlanet>();
		}
		
		EvaluationEventsPlanet ePlanet = new EvaluationEventsPlanet();
		ePlanet.count = planet.getShipsCount(ShipType.BATTLESHIPS);
		ePlanet.col = 
				planet.getOwner() == Player.NEUTRAL ?
					Colors.NEUTRAL :
					game.getPlayers()[planet.getOwner()].getColorIndex();
		
		event.planets.put(
				game.getPlanetIndexFromPosition(planet.getPosition()), 
				ePlanet);
	}
	
	void stopShip(int day, Ship ship, boolean deleteAfterStop)
	{
		EvaluationEventsShip eShip = this.ships.get(this.shipMap.get(ship));
		
		eShip.stopEventId = this.events.size();
		eShip.deleteAfterStop = deleteAfterStop;
	}
	
	private void showShip(int day, Ship ship)
	{
		EvaluationEvent event = this.getEvent(day);
		
		if (event.appearingShips == null)
		{
			event.appearingShips = new Hashtable<Integer,Byte>();
		}
		
		int shipIndex = this.getShipIndex(ship);
		event.appearingShips.put(
				shipIndex, 
				ship.getOwner() == Player.NEUTRAL ?
						Colors.WHITE :
						game.getPlayers()[ship.getOwner()].getColorIndex());
	}
}
