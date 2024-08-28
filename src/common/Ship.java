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

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial") 
class Ship implements Serializable
{
	private static final int 		SPEED_NORMAL = 2;
	private static final int 		SPEED_FAST = 4;
	private static final int 		SPEED_SLOW = 1;
	
	static ShipTravelTime getTravelTime(
			ShipType type, 
			boolean transfer, 
			Point positionStart,
			Point positionDestination)
	{
		double dist = positionStart.distance(positionDestination);
		double v = (double)getSpeed(type, transfer);
		
		return getTravelTimeInternal(dist, v);
	}
	private static int getSpeed(ShipType type, boolean transfer)
	{
		if (type == ShipType.SPY && !transfer)
			return SPEED_FAST;
		else if (type == ShipType.MINESWEEPER && !transfer)
			return SPEED_SLOW;
		else if (type == ShipType.PATROL && !transfer)
			return SPEED_SLOW;
		else
			return SPEED_NORMAL;
	}
	private static ShipTravelTime getTravelTimeInternal(double dist, double v)
	{
		double yearFraction = dist/v;
		
		int daysCount = CommonUtils.round(yearFraction * (double)Game.DAYS_OF_YEAR_COUNT);
		
		if (daysCount % Game.DAYS_OF_YEAR_COUNT == 0)
		{
			return new ShipTravelTime(daysCount / Game.DAYS_OF_YEAR_COUNT - 1, Game.DAYS_OF_YEAR_COUNT);
		}
		else
		{
			return new ShipTravelTime(daysCount / Game.DAYS_OF_YEAR_COUNT, daysCount % Game.DAYS_OF_YEAR_COUNT);
		}
	}
	private static double getYearFraction(int day)
	{
		return (double)day / (double)Game.DAYS_OF_YEAR_COUNT;
	}
	
	private int planetIndexStart;
	private int planetIndexDestination;
	private Point positionStart;
	private Point positionDestination;
	private int yearCount;
	private ShipType type;
	private int count;
	private int bonus;
	
	private int owner;
	
	private boolean transfer;
	
	private UUID stopLabel;
	
	private Alliance alliance;

	transient private boolean startedRecently;
	transient private boolean wasStoppedBefore;
	transient private boolean toBeDeleted;
	transient private boolean toBeTurned;
	
	Ship(
			int planetIndexStart, 
			int planetIndexDestination,
			Point pointStart,
			Point pointDestination,
			int yearCount,
			ShipType type,
			int count,
			int owner,
			boolean transfer,
			Alliance alliance)
	{
		this.planetIndexStart = planetIndexStart;
		this.planetIndexDestination = planetIndexDestination;
		this.positionStart = pointStart;
		this.positionDestination = pointDestination;
		this.yearCount = yearCount;
		this.type = type;
		this.count = count;
		this.owner = owner;
		this.transfer = transfer;
		this.toBeDeleted = false;
		this.alliance = alliance;
	}
	
	Ship(
			int planetIndexStart, 
			int planetIndexDestination,
			Point pointStart,
			Point pointDestination,
			ShipType type,
			int count, 
			int owner, 
			boolean transfer, 
			boolean recentlyStarted, 
			Alliance alliance,
			int bonus)
	{
		this.planetIndexStart = planetIndexStart;
		this.planetIndexDestination = planetIndexDestination;
		this.positionStart = pointStart;
		this.positionDestination = pointDestination;
		this.yearCount = 0;
		this.type = type;
		this.count = count;
		this.owner = owner;
		this.transfer = transfer;
		this.startedRecently = recentlyStarted;
		this.toBeDeleted = false;
		this.alliance = alliance;
		this.bonus = bonus;
	}

	void capture(int ownerNew, Point positionCurrent)
	{
		this.owner = ownerNew;
		this.yearCount = 0;
		this.transfer = true;
		this.positionDestination   = positionCurrent;
		this.positionStart	 = positionCurrent;
		this.planetIndexStart = Planet.NO_PLANET;
		this.planetIndexDestination = Planet.NO_PLANET;
		
		this.setStopped(true);
		
		if (this.alliance != null)
		{
			int count = this.getCount();
			
			this.alliance = null;
			this.count = count;
		}
	}

	boolean doEqualsTutorial(Ship other)
	{
		if (other == null)
			return false;
		
		boolean areDifferent = (this.alliance == null && other.alliance != null);
		
		if (this.alliance != null)
			areDifferent |= !this.alliance.doEqualsTutorial(other.alliance);
		
		areDifferent |= this.count != other.count;
		areDifferent |= this.planetIndexDestination != other.planetIndexDestination;
		areDifferent |= this.planetIndexStart != other.planetIndexStart;
		areDifferent |= !this.positionDestination.equals(other.positionDestination);
		areDifferent |= this.transfer != other.transfer;
		areDifferent |= this.type != other.type;
				
		return !areDifferent;
	}

	int getCount() {
		return count;
	}

	int getBattleshipsCount(int playerIndex)
	{
		if (this.type != ShipType.BATTLESHIPS)
			return 0;
		
		if (this.alliance == null)
		{
			if (playerIndex == this.owner)
				return this.count;
			else
				return 0;
		}
		else
			return this.alliance.getBattleshipsCount(playerIndex);
	}
	
	int getBonus() {
		return bonus;
	}

	int getOwner() {
		return owner;
	}
	
	byte getOwnerColorIndex(Game game)
	{
		if (this.owner == Player.NEUTRAL)
		{
			return Colors.WHITE;
		}
		else
		{
			return game.getPlayers()[this.owner].getColorIndex();
		}
	}
	
	String getOwnerName(Game game)
	{
		if (this.owner == Player.NEUTRAL)
		{
			return VegaResources.NeutralFleet(false);
		}
		else
		{
			return game.getPlayers()[this.owner].getName();
		}
	}
	
	int getPlanetIndexDestination() {
		return planetIndexDestination;
	}

	int getPlanetIndexStart()
	{
		return planetIndexStart;
	}

	Point getPositionDestination() {
		return positionDestination;
	}
	
	Point getPositionOnDay(int day)
	{
		double distTotal = this.positionStart.distance(this.positionDestination);
		
		if (distTotal < Point.PRECISION)
		{
			return this.positionStart;
		}
		
		if (day <= 0)
		{
			double x0 = this.positionStart.getX() + (double)this.yearCount * (this.positionDestination.getX()-this.positionStart.getX()) / distTotal;
			double y0 = this.positionStart.getY() + (double)this.yearCount * (this.positionDestination.getY()-this.positionStart.getY()) / distTotal;
			
			return new Point(x0, y0);
		}
		
		double v = getSpeed(this.getType(), this.transfer);
		
		double yearFraction = Ship.getYearFraction(day);
		
		double yearCountDay = (double)this.yearCount +  yearFraction * v;
		
		if (yearCountDay > distTotal - Point.PRECISION)
		{
			return this.positionDestination;
		}
		
		double x = this.positionStart.getX() + yearCountDay * (this.positionDestination.getX()-this.positionStart.getX()) / distTotal;
		double y = this.positionStart.getY() + yearCountDay * (this.positionDestination.getY()-this.positionStart.getY()) / distTotal;
		
		return new Point(x, y);
	}
	
	Point getPositionStart() {
		return positionStart;
	}

	byte getScreenDisplaySymbol()
	{
		switch (this.type)
		{
		case BATTLESHIPS:
			return 1;
		case SPY:
			return 2;
		case PATROL:
			return 3;
		case TRANSPORT:
			return 4;
		case MINE50:
		case MINE100:
		case MINE250:
		case MINE500:
			return 5;
		case MINESWEEPER:
			return 6;
		case BLACK_HOLE:
			return 7;
		default:
			return 0;
		}
	}
	
	Point getSectorOnDay(int day)
	{
		return this.getPositionOnDay(day).getSector();
	}

	UUID getStopLabel()
	{
		return this.stopLabel;
	}

	ShipTravelTime getTravelTimeRemaining()
	{
		Point posNow = this.getPositionOnDay(0);
		
		double dist = posNow.distance(this.positionDestination);
		double v = (double)getSpeed(this.type, this.transfer);
		
		return getTravelTimeInternal(dist, v);
	}
	
	ShipType getType() {
		return type;
	}
	
	void incrementYearCount()
	{
		this.yearCount += getSpeed(this.type, this.transfer);
	}
	
	boolean isAlliance()
	{
		return (this.alliance != null);
	}
	
	boolean isAllianceMember(int playerIndex)
	{
		if (this.alliance == null)
			return false;
		else
			return this.alliance.isMember(playerIndex);
	}
	
	boolean isPlayerInvolved(int playerIndex)
	{
		if (this.owner == playerIndex)
			return true;
		else
			return this.isAllianceMember(playerIndex);
	}
	
	boolean isStartedRecently()
	{
		return this.startedRecently;
	}
	
	boolean isStopped()
	{
		return this.stopLabel != null;
	}
	
	boolean isToBeDeleted()
	{
		return this.toBeDeleted;
	}
	
	boolean isToBeTurned()
	{
		return this.toBeTurned;
	}
	
	boolean isTransfer() {
		return transfer;
	}
		
	void resetYearCount()
	{
		this.yearCount = 0;
	}
	
	void setAlliance(Alliance alliance)
	{
		this.alliance = alliance;
	}
	
	void setPlanetIndexDestination(int planetIndexDestination) 
	{
		this.planetIndexDestination = planetIndexDestination;
	}

	void setPlanetIndexStart(int planetIndexStart)
	{
		this.planetIndexStart = planetIndexStart;
	}
	
	void setPositionDestination(Point positionDestination) 
	{
		this.positionDestination = positionDestination;
	}
	
	void setPositionStart(Point positionStart)
	{
		this.positionStart = positionStart;
	}

	void setStartedRecently(boolean startedRecently)
	{
		this.startedRecently = startedRecently;
	}
	
	void setStopLabel(UUID stopLabel)
	{
		this.stopLabel = stopLabel; 
	}
	
	void setStopped(boolean stop)
	{
		if (stop)
			this.stopLabel = UUID.randomUUID();
		else
			this.stopLabel = null;
	}
	
	void setToBeDeleted() {
		this.toBeDeleted = true;
	}
	
	void setToBeTurned()
	{
		this.toBeTurned = true;
	}
	
	void setWasStoppedBefore()
	{
		this.wasStoppedBefore = true;
	}
	
	void subtractBattleships(int count, int playerIndexPreferred)
	{
		if (this.type != ShipType.BATTLESHIPS)
			return;
		
		if (this.alliance == null)
			this.count -= count;
		else
		{
			this.alliance.subtractBattleships(count, playerIndexPreferred);
			this.count = this.alliance.getBattleshipsCount();
		}
	}
	
	void turn()
	{
		int planetIndexTemp = this.planetIndexStart;
		Point positionTemp = this.positionStart.klon();
		
		this.planetIndexStart = this.planetIndexDestination;
		this.planetIndexDestination = planetIndexTemp;
		this.positionStart = this.positionDestination;
		this.positionDestination = positionTemp;
		this.yearCount = 0;
		
		this.transfer = (this.type == ShipType.MINESWEEPER);
		
		this.toBeDeleted = false;
		this.toBeTurned = false;
	}
	
	boolean wasStoppedBefore()
	{
		return this.wasStoppedBefore;
	}
}
