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
	private Planet planet;
	private boolean canTerminateAlliance = false;

	EnterAlliance(
			Game game,
			int playerIndex,
			int planetIndex,
			boolean[] allianceMembersNewLast)
	{
		this.game = game;
		this.planet = game.getPlanets()[planetIndex];
		
		// Current alliance members
		this.allianceMembersCurrent = new int[game.getPlayersCount()];
		
		for (int i = 0; i < game.getPlayersCount(); i++)
		{
			if (this.planet.areDetailsVisibleForPlayer(playerIndex))
			{
				this.allianceMembersCurrent[i] =
						this.planet.isAllianceMember(i) ? IS_MEMBER : IS_NOT_MEMBER;
			}
			else
			{
				this.allianceMembersCurrent[i] =
						i == playerIndex ? IS_NOT_MEMBER : IS_NOT_VISIBLE;
			}
		}
		
		// Current changes to alliance members
		this.allianceMembersChanged = new boolean[game.getPlayersCount()];
		
		for (int i = 0; i < game.getPlayersCount(); i++)
		{
			this.allianceMembersChanged[i] = 
					allianceMembersNewLast != null ?
							allianceMembersNewLast[i] :
							this.allianceMembersCurrent[i] == IS_MEMBER;
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
						this.planet.getBattleshipsCount(i));
			}
			else
			{
				this.playerInfos[i] = new ScreenContentPlanetEditorPlayerInfo(
						game.getPlayers()[i].getColorIndex(),
						game.getPlayers()[i].getName(),
						i == playerIndex ? 0 : -1);
			}
		}
		
		if (planet.isAllianceMember(playerIndex))
		{
			canTerminateAlliance = true;
		}
				
		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.ENTER_ALLIANCE);
		this.updateDisplay();
		game.setScreenContentMode(ScreenContent.MODE_ENTER_ALLIANCE);

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
		
		// Set allowed keys
		boolean canBeChanged[] = new boolean[this.game.getPlayersCount()];
		
		for (int i = 0; i < this.game.getPlayersCount(); i++)
		{
			if (this.allianceMembersCurrent[i] == IS_MEMBER &&
				i != playerIndex &&
				i != planet.getOwner())
				continue;
			
			allowedKeys.add(
				new ConsoleKey(
						Integer.toString(i + 1),
						this.game.getPlayers()[i].getName()));
			
			canBeChanged[i] = true;
		}

		if (canTerminateAlliance)
		{
			allowedKeys.add(new ConsoleKey("0",VegaResources.TerminateAlliance(true)));
		}
			
		allowedKeys.add(new ConsoleKey("ENTER",VegaResources.AcceptChanges(true)));
		allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));

		do
		{
			ConsoleInput input = game.getConsole().waitForKeyPressed(allowedKeys);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				break;
			}
			else if (input.getLastKeyCode() == KeyEvent.VK_ENTER)
			{
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
										
				default:
					if (numberInput >= 1 && numberInput <= this.game.getPlayersCount())
					{
						int playerIndexSelected = numberInput - 1;
						
						if (!canBeChanged[playerIndexSelected])
						{
							continue;
						}
						
						if (playerIndexSelected == playerIndex ||
							playerIndexSelected == planet.getOwner())
						{
							if (this.allianceMembersChanged[playerIndexSelected])
							{
								// Terminate alliance
								this.allianceMembersChanged = new boolean[this.game.getPlayersCount()];
							}
							else
							{
								// Create new alliance with all current members
								for (int i = 0; i < this.game.getPlayersCount(); i++)
								{
									if (this.allianceMembersCurrent[i] == IS_MEMBER)
									{
										this.allianceMembersChanged[i] = true;
									}
								}
								
								this.allianceMembersChanged[playerIndex] = true;
								this.allianceMembersChanged[planet.getOwner()] = true;
							}
						}
						else
						{
							this.allianceMembersChanged[playerIndexSelected] =
									!this.allianceMembersChanged[playerIndexSelected];
						}
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
