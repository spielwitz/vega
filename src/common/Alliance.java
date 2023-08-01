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
class Alliance implements Serializable
{
	private boolean[] members;
	private int[] battleshipsCount;
	
	Alliance(int playersCount)
	{
		this.members = new boolean[playersCount];
		this.battleshipsCount = new int[playersCount];
	}
	
	void addBattleshipsCount(int playerIndex, int battleshipsCount)
	{
		if (this.members[playerIndex])
			this.battleshipsCount[playerIndex] += battleshipsCount;
		else
			this.setBattleshipsCount(playerIndex, battleshipsCount);
	}
	
	void addPlayer(int playerIndex)
	{
		this.members[playerIndex] = true;
	}
	boolean doEqualsTutorial(Alliance other)
	{
		if (other == null)
			return false;
		
		boolean areDifferent = false;
		
		for (int playerIndex = 0; playerIndex < this.battleshipsCount.length; playerIndex++)
		{
			areDifferent |= this.battleshipsCount[playerIndex] != other.battleshipsCount[playerIndex];
			areDifferent |= this.members[playerIndex] != other.members[playerIndex];
		}
		
		return !areDifferent;
	}
	
	int getBattleshipsCount()
	{
		int sum = 0;
		
		for (int i = 0; i < this.members.length; i++)
			if (this.members[i])
				sum += this.battleshipsCount[i];
		
		return sum;
	}
	
	int getBattleshipsCount(int playerIndex)
	{
		if (this.isMember(playerIndex))
			return this.battleshipsCount[playerIndex];
		else
			return 0;
	}
	
	boolean[] getMembers()
	{
		return this.members;
	}
	
	int getMembersCount()
	{
		int membersCount = 0;
		
		for (int playerIndex = 0; playerIndex < this.members.length; playerIndex++)
			if (this.members[playerIndex])
				membersCount++;
		
		return membersCount;
	}
	
	int getPlayersCount()
	{
		return this.members.length;
	}
	
	boolean isMember(int playerIndex)
	{
		if (playerIndex != Player.NEUTRAL)
			return this.members[playerIndex];
		else
			return false;
	}
	
	void replacePlayer(int playerIndexBefore, int playerIndexAfter)
	{
		if (!this.isMember(playerIndexBefore))
			return;
		
		if (playerIndexAfter != Player.NEUTRAL)
			this.addBattleshipsCount(playerIndexAfter, this.getBattleshipsCount(playerIndexBefore));
		
		this.removePlayer(playerIndexBefore);
	}
	
	int[] subtractBattleships(int battleshipsCount, int playerIndexPreferred)
	{
		int[] losses = CommonUtils.distributeLoss(this.battleshipsCount, battleshipsCount, playerIndexPreferred);
		
		for (int playerIndex = 0; playerIndex < this.members.length; playerIndex++)
			this.subtractBattleshipsCount(playerIndex, losses[playerIndex]);
		
		return losses;
	}	
	
	void subtractBattleshipsCount(int playerIndex, int battleshipsCount)
	{
		if (this.members[playerIndex])
		{
			this.battleshipsCount[playerIndex] -= battleshipsCount;
			if (this.battleshipsCount[playerIndex] < 0)
				this.battleshipsCount[playerIndex] = 0;
		}
	}
	
	private void removePlayer(int playerIndex)
	{
		this.members[playerIndex] = false;
		this.battleshipsCount[playerIndex] = 0;
	}
	
	private void setBattleshipsCount(int playerIndex, int battleshipsCount)
	{
		this.members[playerIndex] = true;
		this.battleshipsCount[playerIndex] = battleshipsCount;
	}	
}
