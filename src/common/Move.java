/**	VEGA - a strategy game
    Copyright (C) 1989-2024 Michael Schweitzer, spielwitz@icloud.com

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
import java.util.UUID;

@SuppressWarnings("serial") 
class Move implements Serializable
{
	transient private Planet planetBefore;

	private int planetIndex;
	private Planet planetAfter;
	private Ship ship;
	private boolean[] allianceChanges;
	private UUID stopLabel;
	
	Move(int planetIndex, boolean[] allianceChanges)
	{
		super();
		this.planetIndex = planetIndex;
		this.allianceChanges = allianceChanges;
	}
	
	Move(int planetIndex, Planet planetBefore, Planet planetAfter)
	{
		this(planetIndex, planetBefore, planetAfter, true);
	}
	
	Move(int planetIndex, Planet planetBefore, Planet planetAfter, boolean clone)
	{
		super();
		this.planetIndex = planetIndex;
		this.planetBefore = planetBefore;
		this.planetAfter = 
				clone ?
						(Planet)CommonUtils.klon(planetAfter) :
						planetAfter;
	}
	
	Move(int planetIndex, Ship ship, Planet planetBefore)
	{
		super();
		this.planetIndex = planetIndex;
		this.ship = ship;
		this.planetBefore = planetBefore;
	}
	
	Move(Ship ship, UUID stopLabel, int planetIndex)
	{
		super();
		this.ship = ship;
		this.stopLabel = stopLabel;
		this.planetIndex = planetIndex;
	}
	
	boolean doEqualsTutorial(Move other)
	{
    	if (other == null)
    		return false;
    	
        boolean areDifferent = (this.allianceChanges == null && other.allianceChanges != null) ||
        		(this.allianceChanges != null && other.allianceChanges == null);
        
        if (this.allianceChanges != null)
        {
        	for (int playerIndex = 0; playerIndex < this.allianceChanges.length; playerIndex++)
        	{
        		areDifferent |= this.allianceChanges[playerIndex] != other.allianceChanges[playerIndex];
        	}
        }
        
        areDifferent |= (this.planetAfter == null && other.planetAfter != null);
        
        if (this.planetAfter != null)
        {
        	areDifferent |= !this.planetAfter.doEqualsTutorial(other.planetAfter);
        }
        
        areDifferent |= this.planetIndex != other.planetIndex;
        
        areDifferent |= (this.ship == null && other.ship != null) ||
        		(this.ship != null && other.ship == null);
        
        if (this.ship != null)
        {
        	areDifferent |= !this.ship.doEqualsTutorial(other.ship);
        }
        
        return !areDifferent;
	}
	
	boolean[] getAllianceChanges()
	{
		return this.allianceChanges;
	}
	
	Planet getPlanetAfter() {
		return this.planetAfter;
	}

	Planet getPlanetBefore() {
		return this.planetBefore;
	}	
	
	int getPlanetIndex() {
		return planetIndex;
	}
	
	Ship getShip() {
		return ship;
	}
	
    UUID getStopLabel()
	{
		return this.stopLabel;
	}
}
