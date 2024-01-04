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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

@SuppressWarnings("serial") 
class Planet implements Serializable
{
	static final int NO_PLANET = -1;
	
	static final double PRICE_RATIO_BUY_SELL = 2./3.;
	
	static Hashtable<ShipType, PriceRange> PRICES_MIN_MAX;
	
	static 
	{
		PRICES_MIN_MAX = new Hashtable<ShipType, PriceRange>();
		
		PRICES_MIN_MAX.put(ShipType.DEFENSIVE_BATTLESHIPS, new PriceRange(60, 90));
		PRICES_MIN_MAX.put(ShipType.MONEY_PRODUCTION, new PriceRange(60, 90));
		PRICES_MIN_MAX.put(ShipType.BATTLESHIP_PRODUCTION, new PriceRange(0, 0));
		
		PRICES_MIN_MAX.put(ShipType.SPY, new PriceRange(3, 6));
		PRICES_MIN_MAX.put(ShipType.PATROL, new PriceRange(12, 18));
		PRICES_MIN_MAX.put(ShipType.TRANSPORT, new PriceRange(4, 7));
		PRICES_MIN_MAX.put(ShipType.MINESWEEPER, new PriceRange(20, 28));
		
		PRICES_MIN_MAX.put(ShipType.MINE50, new PriceRange(12, 17));
		PRICES_MIN_MAX.put(ShipType.MINE100, new PriceRange(20, 25));
		PRICES_MIN_MAX.put(ShipType.MINE250, new PriceRange(40, 47));
		PRICES_MIN_MAX.put(ShipType.MINE500, new PriceRange(60, 70));		
	}
	
	private Point position;
	private Alliance alliance;
	private Hashtable<ShipType,Integer> ships;
	private int owner;
	private int defensiveBattleshipsCount;
	private int moneySupply;
	private int moneyProduction;
	private int battleshipProduction;
	private HashSet<Integer> radioStationsByPlayer;
	
	Planet(Point position, Alliance alliance,
			Hashtable<ShipType, Integer> ships, int owner, int defensiveBattleshipsCount,
			int moneySupply, int moneyProduction, int battleshipProduction)
	{
		super();
		this.position = position;
		this.alliance = alliance;
		this.ships = ships;
		this.owner = owner;
		this.defensiveBattleshipsCount = defensiveBattleshipsCount;
		this.moneySupply = moneySupply;
		this.moneyProduction = moneyProduction;
		this.battleshipProduction = battleshipProduction;
	}
	
	@SuppressWarnings("unchecked") 
	void acceptPlanetDataChange(Planet planet)
	{
		Integer battleshipsCount = this.ships.get(ShipType.BATTLESHIPS);
		
		this.ships = (Hashtable<ShipType, Integer>)CommonUtils.klon(planet.ships);
		
		if (battleshipsCount == null)
			this.ships.remove(ShipType.BATTLESHIPS);
		else
			this.ships.replace(ShipType.BATTLESHIPS, battleshipsCount.intValue());
		
		this.moneySupply = planet.moneySupply;
		this.moneyProduction = planet.moneyProduction;
		this.battleshipProduction = planet.battleshipProduction;
		this.defensiveBattleshipsCount = planet.defensiveBattleshipsCount;
	}
	
	void addPlayerToAlliance(int playersCount, int playerIndex)
	{
		if (!this.allianceExists())
		{
			if (this.alliance == null)
				this.alliance = new Alliance(playersCount);
			
			this.alliance.addBattleshipsCount(this.owner, this.getShipsCount(ShipType.BATTLESHIPS));
		}
		
		this.alliance.addPlayer(playerIndex);
		this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
	}
	
	void addToMoneySupply(int count)
	{
		this.moneySupply += count;
	}
	
	boolean allianceExists()
	{
		if (this.alliance == null)
			return false;
		
		return (this.alliance.getMembersCount() > 1);
	}
	
	boolean areDetailsVisibleForPlayer(int playerIndex)
	{
		return this.getOwner() == playerIndex ||
			   this.hasRadioStation(playerIndex) ||
			   this.isAllianceMember(playerIndex);
	}
	
	void buyDefensiveBattleships(int price)
	{
		int buyCountMax = this.getDefensiveBattleshipsBuyCountMax();
		
		if (buyCountMax == 0)
			return;
		
		int actualPrice = this.getDefensiveBattleshipsBuyActualPrice(price);
		if (actualPrice == 0)
			return;
		
		if (this.moneySupply >= actualPrice)
		{
			this.defensiveBattleshipsCount += buyCountMax;
			this.moneySupply -= actualPrice;
		}
	}
	
	void buyMoneyProduction(int price)
	{
		int buyCountMax = this.getMoneyProductionMaxIncrease();
		
		if (buyCountMax == 0)
			return;
		
		int actualPrice = this.getMoneyProductionIncreaseActualPrice(price);
		if (actualPrice == 0)
			return;
		
		if (this.moneySupply >= actualPrice)
		{
			this.moneyProduction += buyCountMax;
			this.moneySupply -= actualPrice;
		}
	}
	
	void buyShip(ShipType type, int count, int price)
	{
		if (this.moneySupply >= price)
		{
			this.incrementShipsCount(type,count);
			this.moneySupply -= price;
		}
	}
	
	void changeOwner(int playerIndexBefore, int playerIndexAfter)
	{
		if (this.owner == playerIndexBefore)
		{
			if (playerIndexAfter == Player.NEUTRAL)
			{
				if (this.allianceExists())
				{
					this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount(this.owner));
					this.alliance = null;
				}
			}
			
			this.owner = playerIndexAfter;

		}
		
		if (this.radioStationsByPlayer != null && this.radioStationsByPlayer.contains(playerIndexBefore))
		{
			if (playerIndexAfter == Player.NEUTRAL)
				this.radioStationsByPlayer.remove(playerIndexBefore);
			else
			{
				this.radioStationsByPlayer.add(playerIndexAfter);
			}
		}

		if (this.isAllianceMember(playerIndexBefore))
		{
			this.alliance.replacePlayer(playerIndexBefore, playerIndexAfter);
			this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
			
			if (this.alliance.getMembersCount() <= 1)
				this.alliance = null;
		}
		
		this.battleshipProduction = this.moneyProduction;
	}
	
	void conquer(int playersCount, int newOwner, Ship ship)
	{
		this.ships.remove(ShipType.BATTLESHIPS);
		this.alliance = null;
		this.defensiveBattleshipsCount = 0;
		
		this.owner = newOwner;
		
		if (ship != null)
			this.mergeBattleships(playersCount, ship);
		
		if (newOwner == Player.NEUTRAL)
		{
			this.moneyProduction = CommonUtils.round((double)this.moneyProduction / 2);
		}
	}
	
	Alliance copyAllianceStructure(int[] reductions)
	{
		Alliance alliance = this.getAllianceStructure();
		if (alliance == null)
			return null;
		
		for (int playerIndex = 0; playerIndex < alliance.getPlayersCount(); playerIndex++)
			if (this.isAllianceMember(playerIndex))
				alliance.addBattleshipsCount(playerIndex, reductions[playerIndex]);
		
		return alliance;
	}
	
	Planet createCopyForPlayer(int playerIndex)
	{
		Planet plClone = (Planet)CommonUtils.klon(this);
		
		if (this.areDetailsVisibleForPlayer(playerIndex))
		{
			return plClone;
		}

		if (!this.isAllianceMember(playerIndex))
		{
			plClone.alliance = null;
		}

		plClone.ships = new Hashtable<ShipType,Integer>();
		plClone.ships.put(ShipType.BATTLESHIPS, this.getShipsCount(ShipType.BATTLESHIPS));
		
		plClone.moneyProduction = 0;
		plClone.battleshipProduction = 0;
		plClone.moneySupply = 0;
		plClone.defensiveBattleshipsCount = 0;
		
		return plClone;
	}
	
	void decrementBattleshipProduction()
	{
		if (this.battleshipProduction > 0)
			this.battleshipProduction--;
	}
	
	void decrementShipsCount(ShipType type, int ount)
	{
		if (this.ships.containsKey(type))
		{
			if (this.ships.get(type) - ount > 0)
				this.ships.put(type, this.ships.get(type) - ount);			
			else
				this.ships.remove(type);
		}
	}
	
	void dissolveAlliance()
	{
		if (!this.allianceExists())
			return;
		
		this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount(this.owner));
		this.alliance = null;
	}
	
	boolean doEqualsTutorial(Planet other)
	{
		if (other == null)
			return false;
		
		boolean areDifferent = false;
		
		areDifferent =  this.battleshipProduction != other.battleshipProduction;
		areDifferent |= this.moneyProduction != other.moneyProduction;
		areDifferent |= this.defensiveBattleshipsCount != other.defensiveBattleshipsCount;
		
		areDifferent |= (this.ships == null && other.ships != null) ||
				(this.ships != null && other.ships == null);
		
		if (this.ships != null)
		{
			areDifferent |= this.ships.size() != other.ships.size();
			
			for (ShipType type: this.ships.keySet())
			{
				if (type == ShipType.BATTLESHIPS)
				{
					continue;
				}
				
				areDifferent |= !other.ships.containsKey(type);
				
				if (other.ships.containsKey(type))
					areDifferent |= this.ships.get(type).intValue() != other.ships.get(type).intValue();
			}
		}
		
		return !areDifferent;
	}
	
	boolean[] getAllianceMembers()
	{
		if (this.alliance != null)
			return this.alliance.getMembers();
		else
			return null;
	}
	
	Alliance getAllianceStructure()
	{
		if (!this.allianceExists())
			return null;
		else
		{
			Alliance allianceCopy = new Alliance(this.alliance.getPlayersCount());
			
			for (int playerIndex = 0; playerIndex < this.alliance.getPlayersCount(); playerIndex++)
				if (this.alliance.isMember(playerIndex))
					allianceCopy.addPlayer(playerIndex);
			
			return allianceCopy;
		}
	}
	
	int getDefensiveBattleshipsBuyActualPrice(int price)
	{
		int buyCountMax = this.getDefensiveBattleshipsBuyCountMax();
		
		if (buyCountMax == 0)
			return price;
		
		return Math.max(
				1,
				CommonUtils.round(((double)buyCountMax / (double) Game.DEFENSIVE_BATTLESHIPS_BUY_SELL) *
				(double)price));
	}
	
	int getDefensiveBattleshipsBuyCountMax()
	{
		return Math.min(
				Game.DEFENSIVE_BATTLESHIPSS_COUNT_MAX - this.defensiveBattleshipsCount, 
				Game.DEFENSIVE_BATTLESHIPS_BUY_SELL);
	}
	
	int getDefensiveBattleshipsCount()
	{
		return defensiveBattleshipsCount;
	}
	
	int getDefensiveBattleshipsSellActualPrice(int price)
	{
		int sellCountMax = this.getDefensiveBattleshipsSellCountMax();
		
		if (sellCountMax == 0)
			return price;
		
		return 
				Math.max(
						1,
						CommonUtils.round(((double)sellCountMax / (double) Game.DEFENSIVE_BATTLESHIPS_BUY_SELL) *
							(double)price));
	}
	
	int getDefensiveBattleshipsSellCountMax()
	{
		return Math.min(
				this.defensiveBattleshipsCount, 
				Game.DEFENSIVE_BATTLESHIPS_BUY_SELL);
	}
	
	int getBattleshipProduction()
	{
		return this.battleshipProduction;
	}
	
	int getBattleshipsCount(int playerIndex)
	{
		if (!this.allianceExists())
		{
			if (playerIndex == this.owner)
				return this.getShipsCount(ShipType.BATTLESHIPS);
			else
				return 0;
		}
		else
			return this.alliance.getBattleshipsCount(playerIndex);
	}
	
	int getMoneyProduction()
	{
		return this.moneyProduction;
	}
	
	int getMoneyProductionIncreaseActualPrice(int price)
	{
		int maxMoneyProductionIncrease = this.getMoneyProductionMaxIncrease();
		
		if (maxMoneyProductionIncrease == 0)
			return price;
		
		return 
				Math.max(
						1,
						CommonUtils.round(((double)maxMoneyProductionIncrease / (double) Game.MONEY_PRODUCTION_PURCHASE) *
								(double)price));
	}
	
	int getMoneyProductionMaxIncrease()
	{
		return Math.min(
				Game.MONEY_PRODUCTION_MAX - this.moneyProduction,
				Game.MONEY_PRODUCTION_PURCHASE);
	}
	
	int getMoneySupply()
	{
		return this.moneySupply;
	}
	
	int getOwner()
	{
		return this.owner;
	}
	
	byte getOwnerColorIndex(Game game)
	{
		if (this.owner == Player.NEUTRAL)
		{
			return Colors.NEUTRAL;
		}
		else
		{
			return game.getPlayers()[this.owner].getColorIndex();
		}
	}
	
	ArrayList<Integer> getPlayersWithRadioStations()
	{
		ArrayList<Integer> playersIndices = new ArrayList<Integer>();
		
		if (this.radioStationsByPlayer != null)
		{
			for (Integer playerIndex: this.radioStationsByPlayer)
				playersIndices.add(playerIndex);
		}
		
		return playersIndices;
	}
	
	Point getPosition()
	{
		return this.position;
	}
	
	int getShipsCount(ShipType type)
	{
		Integer count = this.ships.get(type);
		
		if (count != null)
			return count.intValue();
		else
			return 0;
	}
	
	int getShipsCount(ShipType type, int playerIndex)
	{
		if (type == ShipType.BATTLESHIPS && this.allianceExists())
			return this.alliance.getBattleshipsCount(playerIndex);
		else if (this.owner == playerIndex)
			return this.getShipsCount(type);
		else
			return 0;
	}
	
	boolean hasRadioStation(int playerIndex)
	{
		if (this.radioStationsByPlayer == null)
			return false;
		
		return this.radioStationsByPlayer.contains(playerIndex);
	}
	
	void incrementBattleshipProduction()
	{
		if (this.battleshipProduction < this.moneyProduction)
			this.battleshipProduction++;
	}
	
	void incrementShipsCount(ShipType type, int count)
	{
		if (this.ships.containsKey(type))
			this.ships.put(type, this.ships.get(type)+ count);
		else
			this.ships.put(type, count);
	}
	
	boolean isAllianceMember(int playerIndex)
	{
		if (!this.allianceExists())
			return false;
		else
			return this.alliance.isMember(playerIndex);
	}
	
	boolean isNeutral()
	{
		return (this.owner == Player.NEUTRAL);
	}
	
	boolean isPlayerInvolved(int playerIndex)
	{
		if (this.owner == playerIndex)
			return true;
		else
			return this.isAllianceMember(playerIndex);
	}
	
	void mergeBattleships(int playerIndex, Ship ship)
	{
		if (ship.isAlliance())
		{
			if (!this.allianceExists())
			{
				this.alliance = new Alliance(playerIndex);
				this.alliance.addBattleshipsCount(this.owner, this.getShipsCount(ShipType.BATTLESHIPS));
			}
			
			for (int playerIndex2 = 0; playerIndex2 < playerIndex; playerIndex2++)
				if (ship.isAllianceMember(playerIndex2))
					this.alliance.addBattleshipsCount(playerIndex2, ship.getBattleshipsCount(playerIndex2));
			
			this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
		}
		else
			this.incrementBattleshipsCount(ship.getOwner(), ship.getBattleshipsCount(ship.getOwner()));
			
	}
	
	void produceBattleships()
	{
		if (this.battleshipProduction <= 0)
			return;
		
		if (this.allianceExists())
		{
			this.alliance.addBattleshipsCount(this.owner, this.battleshipProduction);
			this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
		}
		else
		{
			if (this.ships.containsKey(ShipType.BATTLESHIPS))
				this.ships.put(ShipType.BATTLESHIPS, this.ships.get(ShipType.BATTLESHIPS)+ this.battleshipProduction);
			else
				this.ships.put(ShipType.BATTLESHIPS, battleshipProduction);
		}
	}
	
	void produceMoneySupply()
	{
		this.moneySupply += (this.moneyProduction - this.battleshipProduction);
	}
	
	void sellDefensiveBattleships(int price)
	{
		int sellCountMax = this.getDefensiveBattleshipsSellCountMax();
		
		if (sellCountMax == 0)
			return;
		
		int actualPrice = this.getDefensiveBattleshipsSellActualPrice(price);
		
		this.defensiveBattleshipsCount -= sellCountMax;
		this.moneySupply += actualPrice;
	}

	void sellShip(ShipType type, int price)
	{
		int count = this.getShipsCount(type);
		
		if (count > 0)
		{
			if (count > 1)
				this.ships.put(type, this.ships.get(type)- 1);
			else
				this.ships.remove(type);
			
			this.moneySupply += price;
		}
	}
	
	void setRadioStation(int playerIndex)
	{
		if (this.radioStationsByPlayer == null)
			this.radioStationsByPlayer = new HashSet<Integer>();
		
		this.radioStationsByPlayer.add(playerIndex);
	}
	
	int[] subtractBattleshipsCount(int playersCount, int count, int playerIndexPreferred, boolean launchAllianceBattleships, boolean includeDefensiveBattleships)
	{
		if (this.owner == Player.NEUTRAL)
		{
			if (this.ships.containsKey(ShipType.BATTLESHIPS))
				this.ships.put(ShipType.BATTLESHIPS, this.ships.get(ShipType.BATTLESHIPS) - count);

			return new int[playersCount];
		}
		
		int[] reductions = new int[playersCount];
		int[] originalCountsIncludingDefensiveBattleships = new int[playersCount + 1];
		
		if (this.allianceExists())
		{
			if (launchAllianceBattleships)
			{
				for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
				{
					if (this.alliance.isMember(playerIndex))
						originalCountsIncludingDefensiveBattleships[playerIndex] = 
							this.alliance.getBattleshipsCount(playerIndex);
				}
			}
			else
			{
				originalCountsIncludingDefensiveBattleships[playerIndexPreferred] = this.alliance.getBattleshipsCount(playerIndexPreferred);
			}
		}
		else
		{
			originalCountsIncludingDefensiveBattleships[this.owner] = this.getShipsCount(ShipType.BATTLESHIPS);
		}
		
		if (includeDefensiveBattleships)
			originalCountsIncludingDefensiveBattleships[playersCount] = this.defensiveBattleshipsCount;
		
		int[] reductionsIncludingDefensiveBattleships = 
				CommonUtils.distributeLoss(
						originalCountsIncludingDefensiveBattleships, 
						count,
						includeDefensiveBattleships ?
								playersCount :
								playerIndexPreferred);
		
		for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
		{
			reductions[playerIndex] = reductionsIncludingDefensiveBattleships[playerIndex];
			
			if (this.allianceExists())
			{
				this.alliance.subtractBattleshipsCount(playerIndex, reductions[playerIndex]);
			}
		}
		
		if (this.allianceExists())
			this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
		else
			this.ships.put(ShipType.BATTLESHIPS, this.ships.get(ShipType.BATTLESHIPS) - reductions[this.owner]);
		
		if (includeDefensiveBattleships)
			this.defensiveBattleshipsCount -= reductionsIncludingDefensiveBattleships[playersCount];
		
		return reductions;
	}
	
	void subtractMoneySupply(int count)
	{
		this.moneySupply -= count;
		
		if (this.moneySupply < 0)
			this.moneySupply = 0;
	}

	private void incrementBattleshipsCount(int playerIndex, int count)
	{
		if (this.allianceExists())
		{
			this.alliance.addBattleshipsCount(playerIndex, count);
			this.ships.put(ShipType.BATTLESHIPS, this.alliance.getBattleshipsCount());
		}
		else
			this.incrementShipsCount(ShipType.BATTLESHIPS, count);
	}
}
