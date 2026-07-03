/**	VEGA - a strategy game
    Copyright (C) 1989-2026 Michael Schweitzer, spielwitz@icloud.com

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

class EnterAlliance
{
	static final int IS_NOT_MEMBER = 0;
	static final int IS_MEMBER = 1;
	static final int IS_NOT_VISIBLE = 2;
	
	boolean[] allianceMembersChanged;
	boolean takeOverChanges = false;

	private Game game;
	private ScreenContentPlanetEditorPlayerInfo[] playerInfos;
	private int[] allianceMembersCurrent;

	EnterAlliance(
			Game game,
			int playerIndex,
			int planetIndex,
			boolean[] allianceMembersNewLast)
	{
		this.game = game;
		
		Planet planet = game.getPlanets()[planetIndex];
		
		// Current alliance members
		this.allianceMembersCurrent = new int[game.getPlayersCount()];
		
		for (int i = 0; i < game.getPlayersCount(); i++)
		{
			if (planet.areDetailsVisibleForPlayer(playerIndex))
			{
				this.allianceMembersCurrent[i] =
						planet.isAllianceMember(i) ? IS_MEMBER : IS_NOT_MEMBER;
			}
			else
			{
				this.allianceMembersCurrent[i] =
						i == playerIndex ? IS_NOT_MEMBER : IS_NOT_VISIBLE;
			}
		}
		
		// Current changes to alliance members
		this.allianceMembersChanged = new boolean[game.getPlayersCount()];
		boolean[] allianceMembersChangedStart = new boolean[game.getPlayersCount()];
		
		for (int i = 0; i < game.getPlayersCount(); i++)
		{
			this.allianceMembersChanged[i] = 
					allianceMembersNewLast != null ?
							allianceMembersNewLast[i] :
							this.allianceMembersCurrent[i] == IS_MEMBER;
			
			allianceMembersChangedStart[i] = this.allianceMembersChanged[i];
		}
		
		// Names and number of ships
		this.playerInfos = new ScreenContentPlanetEditorPlayerInfo[game.getPlayersCount()];
		
		for (int i = 0; i < this.game.getPlayersCount(); i++)
		{
			if (planet.areDetailsVisibleForPlayer(playerIndex))
			{
				this.playerInfos[i] = new ScreenContentPlanetEditorPlayerInfo(
						game.getPlayers()[i].getColorIndex(),
						game.getPlayers()[i].getName(),
						planet.getBattleshipsCount(i));
			}
			else
			{
				this.playerInfos[i] = new ScreenContentPlanetEditorPlayerInfo(
						game.getPlayers()[i].getColorIndex(),
						game.getPlayers()[i].getName(),
						i == playerIndex ? 0 : -1);
			}
		}
		
		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.ENTER_ALLIANCE);
		this.updateDisplay();
		game.setScreenContentMode(ScreenContent.MODE_ENTER_ALLIANCE);

		do
		{
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			
			// Which players can be changed (not the current player and not the owner of the planet)
			boolean canBeChanged[] = new boolean[this.game.getPlayersCount()];
			boolean canCreateAlliance = false;
			boolean canTerminateAlliance = false;
			boolean canAcceptChanges = false;
			
			if (allianceMembersChanged[playerIndex])
			{			
				for (int i = 0; i < this.game.getPlayersCount(); i++)
				{
					if (this.allianceMembersCurrent[i] == IS_MEMBER ||
							i == playerIndex ||
							i == planet.getOwner())
							continue;
					
					canBeChanged[i] = true;
					
					allowedKeys.add(
						new ConsoleKey(
								Integer.toString(i + 1),
								this.game.getPlayers()[i].getName()));
				}
				
				allowedKeys.add(new ConsoleKey("0",VegaResources.TerminateAlliance(true)));
				canTerminateAlliance = true;
			}
			else
			{
				canTerminateAlliance = false;
			}
			
			canCreateAlliance = !this.allianceMembersChanged[playerIndex];
			
			if (canCreateAlliance)
			{
				allowedKeys.add(new ConsoleKey("9","Bündnis erstellen"));
			}
				
			allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));
			
			// Can accept changes if the alliance members have changed compared to the start and
			// if there are at least two members in the alliance (including the current player)
			int allianceMembersCount = 0;
			boolean allianceMembersChangedComparedToStart = false;
			
			for (int i = 0; i < this.game.getPlayersCount(); i++)
			{
				if (this.allianceMembersChanged[i])
				{
					allianceMembersCount++;
				}
				allianceMembersChangedComparedToStart |= this.allianceMembersChanged[i] != allianceMembersChangedStart[i];
			}
			
			if (allianceMembersChangedComparedToStart && allianceMembersCount != 1)
			{
				canAcceptChanges = true;
				allowedKeys.add(new ConsoleKey("ENTER",VegaResources.AcceptChanges(true)));
			}
			else
			{
				canAcceptChanges = false;
			}
			
			ConsoleInput input = game.getConsole().waitForKeyPressed(allowedKeys);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				break;
			}
			else if (input.getLastKeyCode() == KeyEvent.VK_ENTER)
			{
				if (!canAcceptChanges)
				{
					continue;
				}
				
				takeOverChanges = true;
				break;
			}
			
			int numberInput = -1;
			
			try
			{
				numberInput = Integer.parseInt(input.getInputText());
			}
			catch (Exception e)
			{
				continue;
			}
			
			switch (numberInput)
			{
				case 0:
					if (canTerminateAlliance)
					{
						this.allianceMembersChanged = new boolean[this.game.getPlayersCount()];
					}
					break;
					
				case 9:
					if (canCreateAlliance)
					{
						// Create new alliance with all current members, including the current player and the owner of the planet
						for (int i = 0; i < this.game.getPlayersCount(); i++)
						{
							this.allianceMembersChanged[i] = this.allianceMembersCurrent[i] == IS_MEMBER;
						}
						
						this.allianceMembersChanged[playerIndex] = true;
						this.allianceMembersChanged[planet.getOwner()] = true;
					}
					break;
										
				default:
					if (numberInput >= 1 && numberInput <= this.game.getPlayersCount())
					{
						int playerIndexSelected = numberInput - 1;
						
						if (!canBeChanged[playerIndexSelected])
						{
							continue;
						}
						
						this.allianceMembersChanged[playerIndexSelected] =
									!this.allianceMembersChanged[playerIndexSelected];
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
		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());
		
		this.game.getScreenContent().setEnterAlliance(
				new ScreenContentEnterAlliance(
						"Bla",
						Colors.NEUTRAL,
						this.playerInfos, 
						this.allianceMembersCurrent,
						this.allianceMembersChanged));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());
	}	
}
