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
import java.util.Hashtable;
import java.util.UUID;

@SuppressWarnings("serial")
class Tutorial implements Serializable
{
	private int currentStep;
	private Game game;
	private ArrayList<Integer> numberOfMovesByYear;
	private ArrayList<Hashtable<Integer,ArrayList<Move>>> opponentsMovesByYear;
	private ArrayList<TutorialStep> steps;

	Tutorial(Game game)
	{
		this.game = game;
	}

	boolean checkMove(Move move)
	{
		boolean undo = true;

		TutorialStep step = this.steps.get(this.currentStep);
		
		if (step.isSkipMoveCheck())
			return false;
		
		Move expectedMove = step.getExpectedMove();

		if (expectedMove != null)
		{
			if (expectedMove.doEqualsTutorial(move))
				undo = false;
		}

		if (!undo)
			this.nextStep();

		return undo;
	}

	boolean checkMovesAtEnd()
	{
		return this.game.getMoves().get(0).size() == this.numberOfMovesByYear.get(this.game.getYear()).intValue();
	}

	void nextStep()
	{
		this.currentStep++;

		if (this.currentStep < this.steps.size())
		{
			this.updateTutorialPanel();
		}
		else
		{
			this.game.getGameThread().endTutorial();
		}
	}

	void setOpponentsMoves()
	{
		if (this.opponentsMovesByYear.size() <= this.game.getYear())
			return;

		Hashtable<Integer,ArrayList<Move>> opponentsMoves = this.opponentsMovesByYear.get(this.game.getYear());

		for (Integer opponentPlayerIndex: opponentsMoves.keySet())
		{
			this.game.getMoves().put(opponentPlayerIndex, opponentsMoves.get(opponentPlayerIndex));
		}
	}

	void start()
	{
		this.steps = new ArrayList<TutorialStep>();

		this.defineSteps();
		this.defineYearsMoves();

		this.currentStep = 0;
		this.updateTutorialPanel();
	}

	private void defineSteps()
	{
		// 0: VEGA
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText00(true),
						null));

		// 1: The game board
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText01(true),
						null));

		// 2: The year
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText02(true),
						null));

		// 3: Battleships
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText03(true),
						null));

		// 4: Planets
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText04(true),
						null));

		// 5: The planet editor
		Hashtable<ShipType, Integer> ships05 = new Hashtable<ShipType, Integer>();
		ships05.put(ShipType.BATTLESHIPS, 350);

		Planet planetBefore = new Planet(
				new Point(12, 11),
				null,
				ships05,
				0,
				350,
				30,
				10,
				10);

		Planet planetAfter = new Planet(
				new Point(12, 11),
				null,
				ships05,
				0,
				350,
				30,
				10,
				0);

		Move expectedMove = new Move(0, planetBefore, planetAfter, false);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText05(true),
						expectedMove));

		// 6: Launch battleships
		Ship ship06 = new Ship(
				0,
				3,
				new Point(12, 11),
				new Point(12, 9),
				ShipType.BATTLESHIPS,
				35,
				0,
				false,
				true,
				null);

		expectedMove = new Move(0, ship06, null);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText06(true),
						expectedMove));

		// 7: Launch more battleships
		Ship ship07 = new Ship(
				0,
				10,
				new Point(12, 11),
				new Point(9, 9),
				ShipType.BATTLESHIPS,
				35,
				0,
				false,
				true,
				null);

		expectedMove = new Move(0, ship07, null);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText07(true),
						expectedMove));

		// 8: Buy a spy
		Hashtable<ShipType, Integer> ships08 = new Hashtable<ShipType, Integer>();
		ships08.put(ShipType.BATTLESHIPS, 0);
		ships08.put(ShipType.SPY, 1);

		planetBefore = planetAfter;

		planetAfter = new Planet(
				new Point(12, 11),
				null,
				ships08,
				0,
				350,
				27,
				10,
				0);

		expectedMove = new Move(0, planetBefore, planetAfter, false);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText08(true),
						expectedMove));

		// 9: Launch a spy
		Ship ship09 = new Ship(
				0,
				15,
				new Point(12, 11),
				new Point(8, 13),
				ShipType.SPY,
				1,
				0,
				false,
				true,
				null);

		expectedMove = new Move(0, ship09, null);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText09(true),
						expectedMove));

		// 10: The annual evaluation
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText10(true),
						null));

		// 11: The situation in year 2
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText11(true),
						null));

		// 12: Buy a transporter
		planetBefore = planetAfter;

		Hashtable<ShipType, Integer> ships12After = new Hashtable<ShipType, Integer>();
		ships12After.put(ShipType.BATTLESHIPS, 0);
		ships12After.put(ShipType.TRANSPORT, 1);

		planetAfter = new Planet(
				new Point(12, 11),
				null,
				ships12After,
				0,
				350,
				0,
				10,
				0);

		expectedMove = new Move(0, planetBefore, planetAfter, false);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText12(true),
						expectedMove));

		// 13: Launch a transporter
		Ship ship13 = new Ship(
				0,
				3,
				new Point(12, 11),
				new Point(12, 9),
				ShipType.TRANSPORT,
				30,
				0,
				false,
				true,
				null);

		expectedMove = new Move(0, ship13, null);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText13(true),
						expectedMove));
		// 14: Evaluation of year 2
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText14(true),
						null));

		// 15: Buy a patrol
		planetBefore = planetAfter;

		Hashtable<ShipType, Integer> ships15After = new Hashtable<ShipType, Integer>();
		ships15After.put(ShipType.BATTLESHIPS, 0);
		ships15After.put(ShipType.TRANSPORT, 1);
		ships15After.put(ShipType.PATROL, 1);

		planetAfter = new Planet(
				new Point(12, 9),
				null,
				ships15After,
				0,
				0,
				0,
				8,
				8);

		expectedMove = new Move(3, planetBefore, planetAfter, false);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText15(true),
						expectedMove));

		// 16: Launch a patrol
		Ship ship16 = new Ship(
				3,
				-1,
				new Point(12, 9),
				new Point(11, 8),
				ShipType.PATROL,
				1,
				0,
				false,
				true,
				null);

		expectedMove = new Move(3, ship16, null);

		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText16(true),
						expectedMove));

		// 17: Evaluation of year 3
		expectedMove = new Move(null, UUID.randomUUID(), 3);
		
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText17(true),
						expectedMove));

		// 18: Mines
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText18(true),
						null,
						true));
		
		// 19: The Black Hole
				this.steps.add(
						new TutorialStep(
								VegaResources.TutorialText19(true),
								null,
								true));

		// 20: Alliances
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText20(true),
						null,
						true));

		// 21: End of the tutorial
		this.steps.add(
				new TutorialStep(
						VegaResources.TutorialText21(true),
						null,
						true));
	}

	private void defineYearsMoves()
	{
		this.opponentsMovesByYear = new ArrayList<Hashtable<Integer,ArrayList<Move>>>();
		this.numberOfMovesByYear = new ArrayList<Integer>();

		// Year 0
		this.numberOfMovesByYear.add(5);

		Hashtable<Integer,ArrayList<Move>> moves = new Hashtable<Integer,ArrayList<Move>>();

		ArrayList<Move> movesPlayer1 = new ArrayList<Move>();

		Hashtable<ShipType, Integer> shipsBefore = new Hashtable<ShipType, Integer>();
		shipsBefore.put(ShipType.BATTLESHIPS, 350);

		Planet planetBefore = new Planet(
				new Point(7, 2),
				null,
				shipsBefore,
				1,
				0,
				30,
				10,
				10);

		Hashtable<ShipType, Integer> shipsAfter = new Hashtable<ShipType, Integer>();
		shipsAfter.put(ShipType.BATTLESHIPS, 350);
		shipsAfter.put(ShipType.SPY, 1);

		Planet planetAfter = new Planet(
				new Point(7, 2),
				null,
				shipsAfter,
				1,
				0,
				27,
				10,
				10);

		movesPlayer1.add(new Move(1, planetBefore, planetAfter, false));

		movesPlayer1.add(
				new Move(1, 
						new Ship(
								1,
								3,
								new Point(7, 2),
								new Point(12, 9),
								ShipType.SPY,
								1,
								1,
								false,
								true,
								null),
						null));

		movesPlayer1.add(
				new Move(1, 
						new Ship(
								1,
								7,
								new Point(7, 2),
								new Point(5, 2),
								ShipType.BATTLESHIPS,
								30,
								1,
								false,
								true,
								null),
						null));

		movesPlayer1.add(
				new Move(1, 
						new Ship(
								1,
								9,
								new Point(7, 2),
								new Point(9, 2),
								ShipType.BATTLESHIPS,
								1,
								1,
								false,
								true,
								null),
						null));

		moves.put(1, movesPlayer1);

		ArrayList<Move> movesPlayer2 = new ArrayList<Move>();
		
		shipsBefore = new Hashtable<ShipType, Integer>();
		shipsBefore.put(ShipType.BATTLESHIPS, 350);

		planetBefore = new Planet(
				new Point(3, 13),
				null,
				shipsBefore,
				2,
				0,
				30,
				10,
				10);

		shipsAfter = new Hashtable<ShipType, Integer>();
		shipsAfter.put(ShipType.BATTLESHIPS, 350);
		shipsAfter.put(ShipType.MINE100, 1);

		planetAfter = new Planet(
				new Point(3, 13),
				null,
				shipsAfter,
				2,
				0,
				7,
				10,
				10);

		movesPlayer2.add(new Move(2, planetBefore, planetAfter, false));

		movesPlayer2.add(
				new Move(2, 
						new Ship(
								2,
								11,
								new Point(3, 13),
								new Point(3, 11),
								ShipType.BATTLESHIPS,
								20,
								2,
								false,
								true,
								null),
						null));

		movesPlayer2.add(
				new Move(2, 
						new Ship(
								2,
								13,
								new Point(3, 13),
								new Point(5, 13),
								ShipType.BATTLESHIPS,
								9,
								2,
								false,
								true,
								null),
						null));
		
		movesPlayer2.add(
				new Move(2, 
						new Ship(
								2,
								Planet.NO_PLANET,
								new Point(3, 13),
								new Point(7, 11),
								ShipType.MINE100,
								1,
								2,
								false,
								true,
								null),
						null));

		moves.put(2, movesPlayer2);

		this.opponentsMovesByYear.add(moves);

		// Year 1
		this.numberOfMovesByYear.add(2);

		moves = new Hashtable<Integer,ArrayList<Move>>();
		moves.put(1, new ArrayList<Move>());
		moves.put(2, new ArrayList<Move>());

		this.opponentsMovesByYear.add(moves);

		// Year 2
		this.numberOfMovesByYear.add(2);

		moves = new Hashtable<Integer,ArrayList<Move>>();
		moves.put(1, new ArrayList<Move>());
		moves.put(2, new ArrayList<Move>());

		this.opponentsMovesByYear.add(moves);

		// Year 3
		this.numberOfMovesByYear.add(1);

		moves = new Hashtable<Integer,ArrayList<Move>>();
		moves.put(1, new ArrayList<Move>());
		moves.put(2, new ArrayList<Move>());

		this.opponentsMovesByYear.add(moves);
	}

	private void updateTutorialPanel()
	{
		TutorialStep step = this.steps.get(this.currentStep);

		this.game.getGameThread().updateTutorialPanel(
				step.getText(), 
				currentStep, 
				this.steps.size(),
				step.getExpectedMove() == null);
	}

	private class TutorialStep implements Serializable
	{
		private Move expectedMove;
		private String text;
		private boolean skipMoveCheck;

		private TutorialStep(String text, Move expectedMove)
		{
			this(text, expectedMove, false);
		}
		
		private TutorialStep(String text, Move expectedMove, boolean skipMoveCheck)
		{
			this.text = text;
			this.expectedMove = expectedMove;
			this.skipMoveCheck = skipMoveCheck;
		}

		private Move getExpectedMove()
		{
			return expectedMove;
		}

		private String getText()
		{
			return text;
		}

		private boolean isSkipMoveCheck()
		{
			return skipMoveCheck;
		}
	}
}
