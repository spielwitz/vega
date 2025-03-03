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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

class Bot
{
	private Game game;
	private int playerIndex;
	
	Bot(Game game, int playerIndex)
	{
		this.game = (Game)CommonUtils.klon(game);
		this.playerIndex = playerIndex;
	}
	
	void getMoves()
	{
		// Bestehende Bündnisse aufkündigen
		
		// Längerfristige Ziele setzen und überprüfen
		
		// Gefahren bewerten
		Hashtable<Planet, Double> threatScoresByPlanet = this.getThreatScores();
		Hashtable<Planet, AttackScore> attackScoresByPlanet = this.getAttackScores(threatScoresByPlanet);
		
		// Chancen auf fremde Planeten bewerten
		
		// Entwicklung von Planeten ($-Produktion, Abwehrkampfschiffe, Kampfbonus)
	}
	
	private Hashtable<Planet, Double> getThreatScores()
	{
		Hashtable<Planet, Double> threatsByPlanet = new Hashtable<Planet, Double>();
		
		for (Planet planet: this.game.getPlanets())
		{
			if (planet.getOwner() != this.playerIndex) continue;
			
			double threatScore = 0;
			
			int shipsOnPlanet = 
					CommonUtils.round(
						(planet.getShipsCount(ShipType.BATTLESHIPS) +
									planet.getDefensiveBattleshipsCount()) *
									Evaluation.getCombatStrength(planet.getBonus()));
			
			for (Planet otherPlanet: this.game.getPlanets())
			{
				if (planet == otherPlanet) continue;
				
				int shipsOnOtherPlanet =
						planet.areDetailsVisibleForPlayer(playerIndex) ?
								CommonUtils.round(
										otherPlanet.getShipsCount(ShipType.BATTLESHIPS) *
										Evaluation.getCombatStrength(otherPlanet.getBonus())) :
								otherPlanet.getShipsCount(ShipType.BATTLESHIPS);
				
				double distance = planet.getPosition().distance(otherPlanet.getPosition());
				double distanceFactor = distance / Math.pow(Math.E, distance);
				
				if (otherPlanet.getOwner() == this.playerIndex)
				{
					// Own nearby planets reduce the threat
					double supportingShips =
							(otherPlanet.getShipsCount(ShipType.BATTLESHIPS) *
							Evaluation.getCombatStrength(otherPlanet.getBonus())) * distanceFactor * 0.5;
					
					threatScore -= supportingShips;
				}
				else
				{
					// Foreign planets increase the threat
					double shipsDifference = (shipsOnOtherPlanet - shipsOnPlanet) * distanceFactor;
					
					if (shipsDifference > 0)
					{
						threatScore += shipsDifference;
					}
				}
			}
			
			threatsByPlanet.put(planet, threatScore);
		}
		
		return threatsByPlanet;
	}
	
	private Hashtable<Planet, AttackScore> getAttackScores(Hashtable<Planet, Double> threatsByPlanet)
	{
		Hashtable<Planet, AttackScore> attackScores = new Hashtable<Planet, AttackScore>();
		
		for (int planetIndex = 0; planetIndex < this.game.getPlanets().length; planetIndex++)
		{
			Planet planet = this.game.getPlanets()[planetIndex];
			
			if (planet.getOwner() != this.playerIndex) continue;
			
			// Is the planet actually being attacked?
			int planetIndexForSearch = planetIndex;
			
			List<Ship> attackingShips =
					game.getShips().stream()
						.filter(s -> s.getType() == ShipType.BATTLESHIPS &&
									 s.getOwner() != this.playerIndex &&
									 s.getPlanetIndexDestination() == planetIndexForSearch)
						.collect(Collectors.toList());
			
			if (attackingShips.size() == 0) continue;
			
			ArrayList<ShipTravelTime> arrivals = new ArrayList<ShipTravelTime>(); 
			
			for (Ship attackingShip: attackingShips)
			{
				ShipTravelTime arrival = attackingShip.getTravelTimeRemaining();
				arrival.ship = attackingShip;
				
				arrivals.add(arrival);
			}
			
			Collections.sort(arrivals, new ShipTravelTime());
			int shipsOnPlanetUponArrival = planet.getShipsCount(ShipType.BATTLESHIPS);
			int lastArrivalYear = 0;
			
			AttackScore attackScore = new AttackScore();
			
			for (ShipTravelTime arrival: arrivals)
			{
				shipsOnPlanetUponArrival +=
					CommonUtils.round(
							(planet.getBattleshipProduction() * (arrival.year - lastArrivalYear) +
							 planet.getDefensiveBattleshipsCount()) *
										Evaluation.getCombatStrength(planet.getBonus()));
				
				lastArrivalYear = arrival.year;
				int arrivingShipCount = CommonUtils.round(arrival.ship.getCount() * 1.25);
		
				if (shipsOnPlanetUponArrival > arrivingShipCount)
				{
					// Assume a combat strength of 1.25
					shipsOnPlanetUponArrival -= arrivingShipCount;
					
					if (attackScore.type != AttackType.WILL_BE_DEFEATED)
						attackScore.type = AttackType.BEING_ATTACKED_BUT_SAFE;
				}
				else
				{
					if (attackScore.type != AttackType.WILL_BE_DEFEATED)
					{
						attackScore.type = AttackType.WILL_BE_DEFEATED;
						attackScore.year = arrival.year;
					}
					attackScore.missingShips += arrivingShipCount;
				}
			}
			
			attackScores.put(planet, attackScore);
		}
		
		return attackScores;
	}
	
	private class AttackScore
	{
		int missingShips;
		int year;
		AttackType type;
	}
	
	private enum AttackType
	{
		BEING_ATTACKED_BUT_SAFE,
		NEEDS_SUPPORT,
		WILL_BE_DEFEATED
	}
	
}
