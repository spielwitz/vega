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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import spielwitz.biDiServer.Tuple;

class EnterMoves
{
	private Game game;
	private int playerIndexNow;
	private boolean capitulated;

	@SuppressWarnings("unchecked")
	EnterMoves(Game game, int playerIndex)
	{
		game.setPlayerIndexEnteringMoves(playerIndex);
		this.game = game;

		this.game.togglePlanetListContentReset();

		this.game.setEnableParameterChange(false); 			
		this.game.createScreenContentWhileMovesEntered();

		boolean abort = this.enterMovesPlayer(playerIndex);

		if (abort)
			this.game.getMoves().remove(playerIndex);

		this.game.setPlanets((Planet[])CommonUtils.klon(this.game.getGameStartOfYear().getPlanets()));
		this.game.setShips((ArrayList<Ship>)CommonUtils.klon(this.game.getGameStartOfYear().getShips()));

		if (!abort && this.game.isSoloPlayer())
		{
			this.postMovesSoloPlayer(playerIndex);
		}

		this.game.getConsole().clear();

		if (!this.game.isSoloPlayer() && !abort)
		{
			this.game.autosave();
		}

		game.setPlayerIndexEnteringMoves(Player.NEUTRAL);

		this.game.setScreenContentWhileMovesEntered(null);

		this.game.updateBoard();
		this.game.togglePlanetListContentReset();
	}

	private void alliance()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Alliance(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		int planetIndex = -1;
		Planet planet = null;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.Planet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndex = input.planetIndex;
			planet = this.game.getPlanets()[planetIndex];

			if (planet.getOwner() == Player.NEUTRAL)
			{
				this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			break;
		} while (true);

		allowedKeys = new ArrayList<ConsoleKey>();

		if (planet.isAllianceMember(this.playerIndexNow))
		{
			allowedKeys.add(new ConsoleKey("0",VegaResources.TerminateAlliance(true)));
		}

		for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
		{
			allowedKeys.add(new ConsoleKey(
					Integer.toString(playerIndex+1), 
					this.game.getPlayers()[playerIndex].getName()));
		}

		allowedKeys.add(new ConsoleKey("-", VegaResources.Info(true)));

		do
		{
			this.game.getConsole().appendText(
					VegaResources.EnterAllianceMembers(true)+": ");

			ConsoleInput input = this.game.getConsole().waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().outAbort();
				return;
			}

			if (input.getInputText().toUpperCase().equals("-"))
			{
				if (!planet.areDetailsVisibleForPlayer(this.playerIndexNow))
				{
					this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
					this.game.getConsole().lineBreak();
					continue;
				}

				this.printAlliance(planet, planetIndex);
				this.game.getConsole().waitForKeyPressed();
				continue;
			}

			try
			{
				Integer.parseInt(input.getInputText());
			}
			catch (Exception e)
			{
				this.game.getConsole().outInvalidInput();
				continue;
			}

			if (input.getInputText().indexOf('0') >= 0 && input.getInputText().length() > 1)
			{
				this.game.getConsole().appendText(
						VegaResources.AllianceDefinitionError(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			boolean[] allianceChanges = new boolean[this.game.getPlayersCount()];

			if (input.getInputText().equals("0"))
			{
				if (!planet.isAllianceMember(this.playerIndexNow))
				{
					this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
					this.game.getConsole().lineBreak();
					continue;
				}
			}
			else
			{
				boolean error = false;

				for (int i = 0; i < input.getInputText().length(); i++)
				{
					int playerIndex = Integer.parseInt(input.getInputText().substring(i,i+1)) - 1;

					if (playerIndex < 0 || playerIndex >= this.game.getPlayersCount())
					{
						error = true;
						break;
					}

					allianceChanges[playerIndex] = true;
				}

				if (error)
				{
					this.game.getConsole().outInvalidInput();
					continue;
				}

				int playersCount = 0;

				for (int playerIndex = 0; playerIndex < this.game.getPlayersCount(); playerIndex++)
				{
					if (allianceChanges[playerIndex])
					{
						playersCount++;
					}
					else
					{
						if (playerIndex == planet.getOwner() ||
								playerIndex == this.playerIndexNow)
						{
							this.game.getConsole().appendText(
									VegaResources.AllianceOwnerNotIncluded(true));
							this.game.getConsole().lineBreak();
							error = true;
							break;
						}
						else if (planet.isAllianceMember(this.playerIndexNow) &&
								planet.isAllianceMember(playerIndex))
						{
							this.game.getConsole().appendText(
									VegaResources.AllianceOwnerNotIncluded2(true));
							this.game.getConsole().lineBreak();
							error = true;
							break;
						}
					}
				}

				if (error)
				{
					continue;
				}

				if (playersCount <= 1)
				{
					this.game.getConsole().outInvalidInput();
					continue;
				}
			}

			this.game.getMoves().get(this.playerIndexNow).add(
					new Move(planetIndex, allianceChanges));

			this.game.getConsole().appendText(
					VegaResources.MoveEntered(true));
			this.game.getConsole().lineBreak();

			break;
		} while (true);
	}

	private void capitulate()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Capitulate(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		this.game.getConsole().appendText(VegaResources.AreYouSure(true));
		this.game.getConsole().lineBreak();

		String input = this.game.getConsole().waitForKeyPressedYesNo(!game.isMoveEnteringOpen()).getInputText().toUpperCase();

		if (!input.equals(Console.KEY_YES))
		{
			this.game.getConsole().outAbort();
			return;
		}

		Ship ship = new Ship(
				0,
				0,
				null,
				null,
				ShipType.CAPITULATION,
				1,
				this.playerIndexNow,
				false,
				true,
				null); 				

		this.game.getShips().add(ship);

		this.game.getMoves().get(this.playerIndexNow).add(
				new Move(
						0,
						ship,
						null));

		this.capitulated = true;

		this.game.getConsole().appendText(VegaResources.MoveEntered(true));
		this.game.getConsole().lineBreak();
	}

	private boolean enterMovesPlayer(int playerIndex)
	{
		this.playerIndexNow = playerIndex; 			
		this.game.getConsole().clear();

		ScreenContentPlanets pdc = 
				(ScreenContentPlanets)CommonUtils.klon(game.getScreenContent().getPlanets());

		ArrayList<Move> moves = new ArrayList<Move>();
		this.game.getMoves().put(playerIndex, moves);

		boolean exit = false;
		this.capitulated = false;

		this.game.setShipsOfPlayerHidden(new HashSet<Integer>());

		if (this.game.isMoveEnteringOpen())
		{
			this.game.updateBoard(null, null, 0, playerIndex, 0);
		}

		do
		{
			int movesBeforeCount = this.game.getMoves().get(this.playerIndexNow).size();

			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName(),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());

			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("TAB",VegaResources.Finish(true)));
			allowedKeys.add(new ConsoleKey("-",VegaResources.Undo(true)));

			if (!this.capitulated)
			{
				allowedKeys.add(new ConsoleKey("0",VegaResources.Planet(true))); 					
				allowedKeys.add(new ConsoleKey("1",VegaResources.Battleships(true))); 					
				allowedKeys.add(new ConsoleKey("2",VegaResources.AlliedBattleships(true)));
				allowedKeys.add(new ConsoleKey("3",VegaResources.Spy(true)));
				allowedKeys.add(new ConsoleKey("4",VegaResources.Patrol(true)));
				allowedKeys.add(new ConsoleKey("5",VegaResources.Transporter(true)));
				allowedKeys.add(new ConsoleKey("6",VegaResources.Mine(true)));
				allowedKeys.add(new ConsoleKey("7",VegaResources.Minesweeper(true)));					
				allowedKeys.add(new ConsoleKey("8",VegaResources.Alliance(true)));
				allowedKeys.add(new ConsoleKey("9",VegaResources.More(true)));
			}

			boolean quit = this.stoppedShips();
			if (quit)
			{
				this.game.setShipsOfPlayerHidden(null);
				this.game.updateBoard();

				return true;
			}

			ConsoleInput consoleInput = this.game.getConsole().waitForKeyPressed(allowedKeys, true);
			String input = consoleInput.getInputText().toUpperCase();

			if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				this.game.getConsole().clear();
			else if (input.equals("\t"))
				exit = this.finish();
			else if (!capitulated && input.equals("1"))
				this.battleships(false);
			else if (!capitulated && input.equals("2"))
				this.battleships(true);
			else if (!capitulated && input.equals("3"))
				this.spiesTransports(ShipType.SPY);
			else if (!capitulated && input.equals("4"))
				this.PatrolsMinesAndSweepers(ShipType.PATROL);
			else if (!capitulated && input.equals("5"))
				this.spiesTransports(ShipType.TRANSPORT);
			else if (!capitulated && input.equals("5"))
				this.capitulate();
			else if (!capitulated && input.equals("6"))
				this.PatrolsMinesAndSweepers(ShipType.MINE50);
			else if (!capitulated && input.equals("7"))
				this.PatrolsMinesAndSweepers(ShipType.MINESWEEPER);
			else if (!capitulated && input.equals("8"))
				this.alliance();
			else if (!capitulated && input.equals("0"))
				this.planetEditor();
			else if (!capitulated && input.equals("9"))
			{
				this.enterMovesPlayerMore(playerIndex);
			}
			else if (input.equals("-"))
			{
				if (this.game.isTutorial())
				{
					this.game.getConsole().appendText(VegaResources.TutorialActionNotAllowed(true));
					this.game.getConsole().lineBreak();
				}
				else
				{
					quit = this.undo();
					if (quit)
					{
						this.game.setShipsOfPlayerHidden(null);
						this.game.updateBoard();

						return true;
					}
				}
			}
			else
				this.game.getConsole().outInvalidInput();

			if (this.game.isTutorial())
			{
				int movesAfterCount = this.game.getMoves().get(this.playerIndexNow).size();

				if (movesAfterCount > movesBeforeCount)
				{
					boolean undo = this.game.getTutorial().checkMove(
							this.game.getMoves().get(this.playerIndexNow).get(movesAfterCount-1));

					if (undo)
					{
						this.game.getConsole().appendText(VegaResources.TutorialActionNotExpected(true));
						this.game.getConsole().lineBreak();

						this.undo(false);
					}
				}
			}

			if (game.isMoveEnteringOpen())
			{
				this.game.updateBoard(null, null, 0, playerIndex, 0);
				game.updatePlanetList(this.playerIndexNow, false);
			}
		} while (!exit);

		this.game.setShipsOfPlayerHidden(null);
		this.game.updateBoard();

		if (!game.isSoloPlayer())
		{
			this.game.getScreenContent().setPlanets(pdc);
			this.game.getGameThread().updateDisplay(this.game.getScreenContent());
		}

		return false;
	}

	private boolean enterMovesPlayerMore(int playerIndex)
	{
		this.playerIndexNow = playerIndex; 			
		this.game.getConsole().clear();

		this.game.setShipsOfPlayerHidden(new HashSet<Integer>());

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		allowedKeys.add(new ConsoleKey("ESC",VegaResources.Back(true)));
		allowedKeys.add(new ConsoleKey("-",VegaResources.Capitulate(true)));
		
		if (this.game.getYear() > 0)
			allowedKeys.add(new ConsoleKey("6",VegaResources.Statistics(true)));
		
		allowedKeys.add(new ConsoleKey("7",VegaResources.HideOrShowSpaceships(true)));
		allowedKeys.add(new ConsoleKey("8",VegaResources.DistanceMatrix(true)));
		allowedKeys.add(new ConsoleKey("9",VegaResources.PhysicalInventoryShort(true)));
		allowedKeys.add(new ConsoleKey("0",VegaResources.FightSimulation(true)));

		boolean exit = false;

		do
		{ 				
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName(),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());

			ConsoleInput consoleInput = this.game.getConsole().waitForKeyPressed(allowedKeys, true);
			String input = consoleInput.getInputText().toUpperCase();

			if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().clear();
				exit = true;
			}
			else if (input.equals("6") && this.game.getYear() > 0)
				new Statistics(this.game, false);
			else if (input.equals("7"))
				this.hideShips();
			else if (input.equals("8"))
				new DistanceMatrix(this.game).showUserDialog();
			else if (input.equals("9"))
				this.inventory();
			else if (input.equals("0"))
				this.fightSimulation();
			else if (input.equals("-"))
			{
				this.capitulate();
				exit = this.capitulated;
			}
			else
				this.game.getConsole().outInvalidInput();
		} while (!exit);

		return false;
	}

	private void battleships(boolean isAlliance)
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Battleships(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		int planetIndexStart = -1;

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();	

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.StartPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndexStart = input.planetIndex;

			if (!isAlliance && this.game.getPlanets()[planetIndexStart].getShipsCount(ShipType.BATTLESHIPS,this.playerIndexNow) > 0)
				break;
			else if (isAlliance && this.game.getPlanets()[planetIndexStart].allianceExists() && this.game.getPlanets()[planetIndexStart].getBattleshipsCount(this.playerIndexNow) > 0)
				break;
			else
			{
				this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
				this.game.getConsole().lineBreak();
			}

		} while (true);

		int planetIndexDestination = -1;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.DestinationPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndexDestination = input.planetIndex;

			if (planetIndexStart != planetIndexDestination)
				break;
			else
			{
				this.game.getConsole().appendText(VegaResources.ThisIsTheStartPlanet(true));
				this.game.getConsole().lineBreak();
			}

		} while (true);

		int count = -1;
		String inputText = "";

		allowedKeys = new ArrayList<ConsoleKey>();

		allowedKeys.add(new ConsoleKey("+",VegaResources.AllBattleships(true)));
		allowedKeys.add(new ConsoleKey("-",VegaResources.Info(true)));

		do
		{
			this.game.getConsole().appendText(VegaResources.Count(true)+": ");

			ConsoleInput input = this.game.getConsole().waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().outAbort();
				count = -1;
				break;
			}

			inputText = input.getInputText().toUpperCase();

			if (inputText.length() == 0)
			{
				this.game.getConsole().outInvalidInput();
				continue;
			}

			int countTemp = 0;
			int countMaxTemp = 0;

			if (isAlliance)
				countMaxTemp = this.game.getPlanets()[planetIndexStart].getShipsCount(ShipType.BATTLESHIPS);
			else
				countMaxTemp = this.game.getPlanets()[planetIndexStart].getShipsCount(ShipType.BATTLESHIPS, this.playerIndexNow);

			if (inputText.equals("-"))
			{
				this.game.getConsole().appendText(
						VegaResources.YouCannotStartMoreBattleships(
								true, 
								Integer.toString((countMaxTemp))) + " ");

				ShipTravelTime travelTime = Ship.getTravelTime(
						ShipType.BATTLESHIPS, 
						false, 
						this.game.getPlanets()[planetIndexStart].getPosition(), 
						this.game.getPlanets()[planetIndexDestination].getPosition());

				this.game.getConsole().appendText(
						VegaResources.Arrival2(true));

				travelTime.year += this.game.getYear();
				this.game.getConsole().appendText(
						travelTime.toOutputString(true));

				this.game.getConsole().waitForKeyPressed();
				continue;
			}

			if (inputText.equals("+"))
				countTemp = countMaxTemp;
			else
			{
				try { countTemp = Integer.parseInt(inputText); }
				catch (Exception e)
				{
					this.game.getConsole().outInvalidInput();
					continue;
				}

				if (countTemp < 0 || countTemp > countMaxTemp)
				{
					this.game.getConsole().appendText(VegaResources.NotEnoughBattleships(true));
					this.game.getConsole().lineBreak();
					continue;
				}
			}

			if (countTemp > 0)
			{
				count = countTemp;
				break;
			}

		} while (true);

		if (count < 0)
			return;

		Planet planetCopy = (Planet)CommonUtils.klon(this.game.getPlanets()[planetIndexStart]);

		int[] reductions = 
				this.game.getPlanets()[planetIndexStart].subtractBattleshipsCount(this.game.getPlayersCount(), count, this.playerIndexNow, isAlliance, false);

		Alliance alliance = null;

		if (isAlliance)
			alliance = this.game.getPlanets()[planetIndexStart].copyAllianceStructure(reductions);

		Ship ship = new Ship(
				planetIndexStart,
				planetIndexDestination,
				this.game.getPlanets()[planetIndexStart].getPosition(),
				this.game.getPlanets()[planetIndexDestination].getPosition(),
				ShipType.BATTLESHIPS,
				count,
				this.playerIndexNow,
				false,
				true,
				alliance);

		this.game.getShips().add(ship);

		this.game.getMoves().get(this.playerIndexNow).add(
				new Move(
						planetIndexStart, 
						ship,
						planetCopy));

		this.game.getConsole().appendText(VegaResources.MoveEntered(true));
		this.game.getConsole().lineBreak();
	}

	private void fightSimulation()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.FightSimulation(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());
		
		this.game.getConsole().clear();
		
		ArrayList<ConsoleKey> allowedKeys = null;
		int offenderCount = 0;
		int defenderCount = 0;
		boolean enterNumbers = true;
		
		while (true)
		{
			if (enterNumbers)
			{
				allowedKeys = new ArrayList<ConsoleKey>();
				
				while (true)
				{
					this.game.getConsole().appendText(
							VegaResources.FightSimulationAttackerCount(true) + ": ");
					
					ConsoleInput input = this.game.getConsole().waitForTextEntered(5, allowedKeys, false, true);
					
					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.getConsole().clear();
						return;
					}
					
					try
					{
						offenderCount = Math.abs(Integer.parseInt(input.getInputText()));
						break;
					}
					catch (Exception x)
					{
						this.game.getConsole().outInvalidInput();
					}
				}
		
				while (true)
				{
					this.game.getConsole().appendText(
							VegaResources.FightSimulationPlanetCount(true) + ": ");
					
					ConsoleInput input = this.game.getConsole().waitForTextEntered(5, allowedKeys, false, true);
					
					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.getConsole().clear();
						return;
					}
					
					try
					{
						defenderCount = Math.abs(Integer.parseInt(input.getInputText()));
						break;
					}
					catch (Exception x)
					{
						this.game.getConsole().outInvalidInput();
					}
				}
			}
			
			Tuple<Integer,Integer> countsAfterFight = Evaluation.fight(
					this.game.getConsole(), 
					offenderCount, 
					defenderCount);
			
			if (countsAfterFight.getE1() > 0)
				this.game.getConsole().appendText(VegaResources.FightSimulationAttackSuccess(true));
			else
				this.game.getConsole().appendText(VegaResources.FightSimulationAttackNoSuccess(true));
			
			this.game.getConsole().lineBreak();
			
			allowedKeys = new ArrayList<ConsoleKey>();
			allowedKeys.add(new ConsoleKey("ESC", VegaResources.Cancel(true)));
			allowedKeys.add(new ConsoleKey("ENTER", VegaResources.FightSimulationRepeat(true)));
			allowedKeys.add(new ConsoleKey(VegaResources.OtherKey(true), VegaResources.FightSimulationOtherValues(true)));
			
			ConsoleInput input = this.game.getConsole().waitForKeyPressed(allowedKeys, false);
			
			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().clear();
				return;
			}
			
			enterNumbers =  input.getLastKeyCode() != KeyEvent.VK_ENTER;
		}
	}

	private boolean finish()
	{
		if (game.isTutorial() && !game.getTutorial().checkMovesAtEnd())
		{
			this.game.getConsole().appendText(VegaResources.TutorialActionNotExpected(true));
			this.game.getConsole().lineBreak();

			return false;
		}

		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.FinishEnterMoves(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().appendText(VegaResources.FinishEnterMovesQuestion(true));
		this.game.getConsole().lineBreak();

		String input = this.game.getConsole().waitForKeyPressedYesNo(false).getInputText().toUpperCase();

		if (!input.equals(Console.KEY_YES))
		{
			this.game.getConsole().outAbort();
			return false;
		}

		return true;
	}

	private void hideShips()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.HideOrShowSpaceships(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		do
		{
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("ESC",VegaResources.Back(true)));
			allowedKeys.add(new ConsoleKey("-",VegaResources.AllOff(true)));
			allowedKeys.add(new ConsoleKey("+",VegaResources.AllOn(true)));

			for (int playerIndex = 1; playerIndex <= game.getPlayersCount(); playerIndex++)
			{
				String onOff = 
						game.getShipsOfPlayerHidden().contains(playerIndex-1) ?
								VegaResources.On(true) :
								VegaResources.Off(true);
				allowedKeys.add(
						new ConsoleKey(
								Integer.toString(playerIndex), 
								game.getPlayers()[playerIndex-1].getName() + " " + onOff));
			}

			ConsoleInput input = this.game.getConsole().waitForKeyPressed(allowedKeys, false);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				break;

			String key = input.getInputText().toLowerCase();
			boolean error = false;

			if (key.equals("+"))
				this.game.getShipsOfPlayerHidden().clear();
			else if (key.equals("-"))
			{
				this.game.getShipsOfPlayerHidden().clear();
				for (int playerIndex = 0; playerIndex < game.getPlayersCount(); playerIndex++)
					this.game.getShipsOfPlayerHidden().add(playerIndex);
			}
			else
			{
				try
				{
					int playerIndex = Integer.parseInt(key) - 1;

					if (playerIndex < 0 || playerIndex >= game.getPlayersCount())
						error = true;
					else
					{
						if (this.game.getShipsOfPlayerHidden().contains(playerIndex))
							this.game.getShipsOfPlayerHidden().remove(playerIndex);
						else
							this.game.getShipsOfPlayerHidden().add(playerIndex);
					}
						
				}
				catch (Exception x)
				{
					error = true;
				}
			}

			if (error)
				this.game.getConsole().outInvalidInput();
			else
			{
				if (this.game.isMoveEnteringOpen())
				{
					this.game.updateBoard(null, null, 0, playerIndexNow, 0);
				}
				else
				{
					this.game.updateBoard();
				}
			}

		} while (true);

		this.game.getConsole().clear();
	}

	private void inventory()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> " +VegaResources.EnterMoves(true) +" "+ this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.PhysicalInventory(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		this.game.getConsole().appendText(VegaResources.OpenPdfViewerQuestion(true) + " ");

		ConsoleInput input = this.game.getConsole().waitForKeyPressedYesNo(false);

		if (input.getInputText().equals(Console.KEY_YES))
		{
			Inventory inventory = new Inventory(this.game, this.playerIndexNow);
			byte[] pdfBytes = inventory.create(input.getLanguageCode());
			boolean success = false;

			if (input.getClientId() == null)
				success = PdfLauncher.showPdf(pdfBytes);
			else
				success = game.getGameThread().openPdf(pdfBytes, input.getClientId());

			if (success)
				this.game.getConsole().appendText(VegaResources.PdfOpened(true));
			else
				this.game.getConsole().appendText(VegaResources.PdfOpenError(true));

			this.game.getConsole().lineBreak();
		}
		else
		{
			this.game.getConsole().outAbort();
		}
	}

	private void PatrolsMinesAndSweepers(ShipType shipCategory)
	{
		if (shipCategory == ShipType.MINESWEEPER)
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Minesweeper(true),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());
		else if (shipCategory == ShipType.PATROL)
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Patrol(true),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());
		else					
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+VegaResources.Mine(true),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		int planetIndexStart = -1;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.StartPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndexStart = input.planetIndex;

			if (this.game.getPlanets()[planetIndexStart].getOwner() != this.playerIndexNow)
			{
				this.game.getConsole().appendText(VegaResources.YouAreNotOwnerOfPlanet(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			if (shipCategory == ShipType.MINESWEEPER)
			{
				if (this.game.getPlanets()[planetIndexStart].getShipsCount(ShipType.MINESWEEPER) <= 0)
				{
					this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
					this.game.getConsole().lineBreak();
					continue;
				}
			}
			else if (shipCategory == ShipType.PATROL)
			{
				if (this.game.getPlanets()[planetIndexStart].getShipsCount(ShipType.PATROL) <= 0)
				{
					this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
					this.game.getConsole().lineBreak();
					continue;
				}
			}

			break;

		} while (true);

		boolean transfer = false;
		ShipType type = shipCategory;

		if (shipCategory != ShipType.MINESWEEPER && shipCategory != ShipType.PATROL)
		{
			allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("1",VegaResources.Mine50(true)));
			allowedKeys.add(new ConsoleKey("2",VegaResources.Mine100(true)));
			allowedKeys.add(new ConsoleKey("3",VegaResources.Mine250(true)));
			allowedKeys.add(new ConsoleKey("4",VegaResources.Mine500(true)));

			boolean abort = false;

			do
			{
				this.game.getConsole().appendText(VegaResources.WhichType(true) + " ");

				ConsoleInput input = this.game.getConsole().waitForKeyPressed(allowedKeys, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.game.getConsole().outAbort();
					abort = true;
					break;
				}

				if (input.getInputText().equals("1"))
				{
					type = ShipType.MINE50;
				}
				else if (input.getInputText().equals("2"))
				{
					type = ShipType.MINE100;
				}
				else if (input.getInputText().equals("3"))
				{
					type = ShipType.MINE250;
				}
				else if (input.getInputText().equals("4"))
				{
					type = ShipType.MINE500;
				}
				else
				{
					this.game.getConsole().outInvalidInput();
					continue;
				}

				if (this.game.getPlanets()[planetIndexStart].getShipsCount(type) <= 0)
				{
					this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
					this.game.getConsole().lineBreak();
					continue;
				}

				break;

			} while (true);

			if (abort)
				return;
		}
		else
		{
			Optional<Boolean> transferOptional = this.missionOrTransfer();
			if (transferOptional.isEmpty())
				return;
			
			transfer = transferOptional.get();
		}

		PlanetInputStruct inputDestination;

		allowedKeys = new ArrayList<ConsoleKey>();						

		do
		{
			inputDestination = this.game.getPlanetInput(
					VegaResources.DestinationSectorOrPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_SECTOR);

			if (inputDestination == null)
			{
				return;
			}

			if (inputDestination.sector.equals(this.game.getPlanets()[planetIndexStart].getPosition()))
			{
				this.game.getConsole().appendText(VegaResources.ThisIsTheStartPlanet(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			if (((type == ShipType.MINESWEEPER || type == ShipType.PATROL) && transfer) &&
					inputDestination.planetIndex == Planet.NO_PLANET)
			{
				this.game.getConsole().appendText(VegaResources.DestinationOfTransferMustBePlanet(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			break;
		} while (true);

		Planet planetCopy = (Planet)CommonUtils.klon(this.game.getPlanets()[planetIndexStart]);

		if (type != ShipType.MINESWEEPER && type != ShipType.PATROL)
			transfer = (inputDestination.planetIndex != Planet.NO_PLANET);

		this.game.getPlanets()[planetIndexStart].decrementShipsCount(type, 1);

		Ship ship = new Ship(
				planetIndexStart,
				inputDestination.planetIndex,
				this.game.getPlanets()[planetIndexStart].getPosition(),
				inputDestination.sector,
				type,
				1,
				this.playerIndexNow,
				transfer,
				true,
				null);

		this.game.getShips().add(ship);

		this.game.getMoves().get(this.playerIndexNow).add(
				new Move(
						planetIndexStart,
						ship,
						planetCopy));

		this.game.getConsole().appendText(VegaResources.MoveEntered(true));
		this.game.getConsole().lineBreak();
	}
	
	private Optional<Boolean> missionOrTransfer()
	{
		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		allowedKeys.add(new ConsoleKey("1",VegaResources.Mission(true)));
		allowedKeys.add(new ConsoleKey("2", VegaResources.Transfer(true)));
		allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));

		do
		{
			this.game.getConsole().appendText(VegaResources.MissionOrTransfer(true) + " ");

			ConsoleInput input = this.game.getConsole().waitForKeyPressed(allowedKeys, true);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.game.getConsole().outAbort();
				return Optional.empty();
			}

			if (!input.getInputText().toUpperCase().equals("1") && 
					!input.getInputText().toUpperCase().equals("2"))
			{
				this.game.getConsole().outInvalidInput();
				continue;
			}

			return Optional.of(input.getInputText().toUpperCase().equals("2"));

		} while (true);
	}

	private void planetEditor()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> " + VegaResources.Planet(true),
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		int planetIndex = -1;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.Planet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndex = input.planetIndex;

			if (this.game.getPlanets()[planetIndex].areDetailsVisibleForPlayer(playerIndexNow))
				break;
			else
			{
				this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
				this.game.getConsole().lineBreak();
			}

		} while (true);

		if (planetIndex < 0)
			return;

		if (game.isMoveEnteringOpen())
		{
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> " + VegaResources.Planet(true, this.game.getPlanetNameFromIndex(planetIndex)),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());
		}

		new PlanetEditor(
				this.game,
				planetIndex,
				this.game.getMoves().get(this.playerIndexNow),
				this.game.getPlanets()[planetIndex].getOwner() != this.playerIndexNow);
	}

	private void postMovesSoloPlayer(int playerIndex)
	{
		MovesTransportObject movesTransportObject = new MovesTransportObject(
				this.game.getPlayerReferenceCodes()[playerIndex],
				Game.BUILD_COMPATIBLE,
				this.game.getMoves().get(playerIndex));

		boolean success = false;

		do
		{
			if (this.game.getOptions().contains(GameOptions.SERVER_BASED))
			{
				success = this.game.getGameThread().postMovesToServer(
						game.getName(),
						this.game.getPlayers()[this.playerIndexNow].getName(),
						movesTransportObject);

				if (success)
				{
					this.game.getConsole().appendText(
							VegaResources.MovesTransmittedToServer(true));
				}
				else
				{
					this.game.getConsole().appendText(
							VegaResources.MovesNotTransmittedToServer(true));
				}

				this.game.getConsole().lineBreak();
			}
			else
			{
				this.game.getConsole().appendText(
						VegaResources.EmailWasCreatedInStandardClient(true));
				this.game.getConsole().lineBreak();
				this.game.getConsole().appendText(
						VegaResources.SendEmailToGameHost(true));
				this.game.getConsole().lineBreak();

				String subject = "[Vega] " + this.game.getName();

				String bodyText = 
						VegaResources.EmailGameEmailBodyMoves(
								false,
								this.game.getName(),
								Integer.toString(this.game.getYear() + 1),
								this.game.getPlayers()[playerIndex].getName(),
								Game.BUILD,
								this.game.getPlayers()[playerIndex].getName(),
								this.game.getName());

				success = this.game.getGameThread().launchEmail(
						this.game.getEmailAddressGameHost(), 
						subject, 
						bodyText, 
						movesTransportObject);
			}

			if (!success)
			{
				this.game.getConsole().appendText(
						VegaResources.MovesNotTransmittedToServer(true));
				this.game.getConsole().lineBreak();

				ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

				allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));
				allowedKeys.add(new ConsoleKey(
						VegaResources.OtherKey(true),
						VegaResources.TryAgain(true)));

				ConsoleInput consoleInput = this.game.getConsole().waitForKeyPressed(allowedKeys, false);

				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					success = true;
				}
			}


		} while (!success);

		do
		{
			// Endless loop!
			if (this.game.getOptions().contains(GameOptions.SERVER_BASED))
			{
				if (this.game.getYear() >= this.game.getYearMax() - 1)
				{
					this.game.getConsole().appendText(
							VegaResources.LastTimeEnteredMoves(true));
					this.game.getConsole().lineBreak();
					this.game.getConsole().appendText(
							VegaResources.FinalizedGameUnderMyServerBasedGames(true));
				}
				else
				{
					this.game.getConsole().appendText(
							VegaResources.WaitForEvaluation(true));
				}
			}
			else
				this.game.getConsole().appendText(
						VegaResources.WaitForEmailFromGameHost(true));

			this.game.getConsole().waitForKeyPressed();
		} while (true);
	}

	private void printAlliance(Planet planet, int planetIndex)
	{
		if (planet.allianceExists())
		{
			this.game.getConsole().appendText(
					VegaResources.CurrentAllies(true)+":");
			this.game.getConsole().lineBreak();

			this.game.printAllianceInfo(planetIndex);
		}
		else
		{
			this.game.getConsole().appendText(
					VegaResources.NoAlliance(true));
		}
	}

	private void spiesTransports(ShipType type)
	{
		String typeDisplayName = "";
		if (type == ShipType.SPY)
			typeDisplayName = VegaResources.Spy(true);
		else
			typeDisplayName = VegaResources.Transporter(true);

		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> "+typeDisplayName,
				this.game.getPlayers()[this.playerIndexNow].getColorIndex());

		this.game.getConsole().clear();

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();			

		int planetIndexStart = -1;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.StartPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndexStart = input.planetIndex;

			if (this.game.getPlanets()[planetIndexStart].getOwner() != this.playerIndexNow || this.game.getPlanets()[planetIndexStart].getShipsCount(type) < 1)
			{
				this.game.getConsole().appendText(VegaResources.ActionNotPossible(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			break;

		} while(true);
		
		boolean transfer = false;
		
		if (type == ShipType.SPY)
		{
			Optional<Boolean> transferOptional = this.missionOrTransfer();
			if (transferOptional.isEmpty())
				return;
			
			transfer = transferOptional.get();
		}

		int planetIndexDestination = -1;

		do
		{
			PlanetInputStruct input = this.game.getPlanetInput(
					VegaResources.DestinationPlanet(true), 
					!game.isMoveEnteringOpen(), 
					PlanetInputStruct.ALLOWED_INPUT_PLANET);

			if (input == null)
			{
				return;
			}

			planetIndexDestination = input.planetIndex;

			if (planetIndexDestination == planetIndexStart &&
				(type == ShipType.TRANSPORT ||
				 (type == ShipType.SPY && transfer)))
			{
				this.game.getConsole().appendText(
						VegaResources.ThisIsTheStartPlanet(true));
				this.game.getConsole().lineBreak();
				continue;
			}

			break;

		} while (true);

		Planet planetCopy = (Planet)CommonUtils.klon(this.game.getPlanets()[planetIndexStart]);

		Ship ship = null;

		if (type == ShipType.SPY)
		{
			ship = new Ship(
					planetIndexStart,
					planetIndexDestination,
					this.game.getPlanets()[planetIndexStart].getPosition(),
					this.game.getPlanets()[planetIndexDestination].getPosition(),
					type,
					1,
					this.playerIndexNow,
					transfer,
					true,
					null);

		}
		else if (type == ShipType.TRANSPORT)
		{
			allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("+",VegaResources.MaximumLoad(true)));

			int count = -1;

			do
			{
				this.game.getConsole().appendText(
						VegaResources.HowMuchMoney(true,
								Integer.toString(Game.TRANSPORT_MONEY_MAX)) + " ");

				ConsoleInput input = this.game.getConsole().waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					count = -1;
					this.game.getConsole().outAbort();
					break;
				}

				if (input.getInputText().toUpperCase().equals("+"))
					count = Math.min(this.game.getPlanets()[planetIndexStart].getMoneySupply(), Game.TRANSPORT_MONEY_MAX);
				else
				{
					try
					{
						count = Integer.parseInt(input.getInputText());
					}
					catch (Exception e)
					{
						this.game.getConsole().outInvalidInput();
						continue;
					}

					if (count < 0 || count > Math.min(this.game.getPlanets()[planetIndexStart].getMoneySupply(), Game.TRANSPORT_MONEY_MAX))
					{
						this.game.getConsole().appendText(VegaResources.YouCannotTransportThisAmountOfMoney(true));
						this.game.getConsole().lineBreak();
						continue;
					}
				}

				break;

			} while (true);

			if (count < 0)
				return;

			this.game.getPlanets()[planetIndexStart].subtractMoneySupply(count);

			ship = new Ship(
					planetIndexStart,
					planetIndexDestination,
					this.game.getPlanets()[planetIndexStart].getPosition(),
					this.game.getPlanets()[planetIndexDestination].getPosition(),
					type,
					count,
					this.playerIndexNow,
					false,
					true,
					null);

		}

		this.game.getPlanets()[planetIndexStart].decrementShipsCount(type, 1);
		this.game.getShips().add(ship);

		this.game.getMoves().get(this.playerIndexNow).add(
				new Move(
						planetIndexStart,
						ship,
						planetCopy));

		this.game.getConsole().appendText(VegaResources.MoveEntered(true));
		this.game.getConsole().lineBreak();
	}

	private boolean stoppedShips()
	{
		boolean quitEnterMoves = false;

		do
		{
			Ship ship = null;

			for (Ship ship2: this.game.getShips())
			{
				if (ship2.getOwner() == this.playerIndexNow && ship2.isStopped())
				{
					ship = ship2;
					break;
				}
			}

			if (ship == null)
				break;

			ArrayList<Point> positionsMarked = new ArrayList<Point>();
			positionsMarked.add(ship.getPositionStart());
			this.game.updateBoard(positionsMarked, 0);

			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("-", VegaResources.Undo(true)));

			String shipDisplayName = "";

			switch (ship.getType())
			{
				case BATTLESHIPS:
					shipDisplayName = VegaResources.ToWhichPlanetBattleships(true, Integer.toString(ship.getCount()));
					break;
				case SPY:
					shipDisplayName = VegaResources.ToWhichPlanetSpy(true);
					break;
				case TRANSPORT:
					shipDisplayName = VegaResources.ToWhichPlanetTransporter(true);
					break;
				case PATROL:
					shipDisplayName = VegaResources.ToWhichPlanetPatrol(true);
					break;
				case MINE50:
					shipDisplayName = VegaResources.ToWhichPlanetMine50(true);
					break;
				case MINE100:
					shipDisplayName = VegaResources.ToWhichPlanetMine100(true);
					break;
				case MINE250:
					shipDisplayName = VegaResources.ToWhichPlanetMine250(true);
					break;
				case MINE500:
					shipDisplayName = VegaResources.ToWhichPlanetMine500(true);
					break;
				case MINESWEEPER:
					shipDisplayName = VegaResources.ToWhichPlanetMinesweeper(true);
					break;
				default:
					break;
			}

			do
			{
				this.game.getConsole().appendText(shipDisplayName + " ");

				ConsoleInput input = this.game.getConsole().waitForTextEntered(Game.PLANET_NAME_LENGTH_MAX, allowedKeys, !game.isMoveEnteringOpen(), false);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.game.getConsole().outInvalidInput();
					continue;
				}

				if (input.getInputText().toUpperCase().equals(("-")))
				{
					quitEnterMoves = this.undo();
					break;
				}

				int planetIndexDestination = this.game.getPlanetIndexFromName(input.getInputText());

				if (planetIndexDestination >= 0)
				{
					boolean undo = false;
					
					if (this.game.isTutorial())
					{
						undo = this.game.getTutorial().checkMove(new Move(null, UUID.randomUUID(), planetIndexDestination));
					}
					
					if (!undo)
					{
						this.game.getMoves().get(this.playerIndexNow).add(
								new Move(ship, (UUID)CommonUtils.klon(ship.getStopLabel()), planetIndexDestination));
	
						ship.setPlanetIndexDestination(planetIndexDestination);
						ship.setPositionDestination(this.game.getPlanets()[planetIndexDestination].getPosition());
						ship.setStopped(false);
						ship.setWasStoppedBefore();
						ship.setStartedRecently(true);
							
						break;
					}
					
					this.game.getConsole().outInvalidInput();
				}
				else
					this.game.getConsole().outInvalidInput();

			} while (true);

			if (this.game.isMoveEnteringOpen())
				this.game.updateBoard(null, null, 0, this.playerIndexNow, 0);
			else
				this.game.updateBoard();

			if (quitEnterMoves)
				break;

		} while (true);

		return quitEnterMoves;
	}

	private boolean undo()
	{
		return this.undo(true);
	}
	
	private boolean undo(boolean interactive)
	{
		ArrayList<Move> moves = this.game.getMoves().get(this.playerIndexNow);

		if (moves.size() == 0)
		{
			this.game.getConsole().appendText(VegaResources.ThereAreNoMoves(true) + " ");

			String input = this.game.getConsole().waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			if (input.equals(Console.KEY_YES))
			{
				this.game.getConsole().lineBreak();
				return true;
			}
			else
			{
				return false;
			}
		}

		boolean undo = true;

		if (interactive)
		{
			this.game.getConsole().setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" " + this.game.getPlayers()[this.playerIndexNow].getName() + " -> " + VegaResources.Undo(true),
					this.game.getPlayers()[this.playerIndexNow].getColorIndex());

			this.game.getConsole().appendText(VegaResources.UndoLastMoveQuestion(true)+" ");

			String input = this.game.getConsole().waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			undo = input.equals(Console.KEY_YES);
		}

		if (undo)
		{
			Move move = moves.get(moves.size()-1);

			if (move.getStopLabel() != null)
			{
				move.getShip().setStopLabel(move.getStopLabel());
				move.getShip().setPositionDestination(move.getShip().getPositionStart());
				move.getShip().setPlanetIndexDestination(move.getShip().getPlanetIndexStart());
			}
			else
			{
				if (move.getPlanetBefore() != null)
					this.game.getPlanets()[move.getPlanetIndex()] = move.getPlanetBefore();

				if (move.getShip() != null)
				{
					if (move.getShip().getType() == ShipType.CAPITULATION)
						this.capitulated = false;
					else
						this.game.getShips().remove(move.getShip());
				}
			}

			moves.remove(moves.size()-1);

			this.game.getConsole().appendText(VegaResources.LastMoveUndone(true));
			this.game.getConsole().lineBreak();
		}
		else
			this.game.getConsole().outAbort();

		return false;
	}
}
