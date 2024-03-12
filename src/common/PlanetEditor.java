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
import java.util.Hashtable;

class PlanetEditor
{
	private ArrayList<ShipType> itemSequence;
	private Hashtable<ShipType, Integer> pricesBuy = new Hashtable<ShipType, Integer>();
	private Hashtable<ShipType, Integer> pricesSell = new Hashtable<ShipType, Integer>();
	private Game game;
	private boolean readOnly;

	PlanetEditor(
			Game game,
			int planetIndex,
			ArrayList<Move> moves,
			boolean readOnly)
	{
		this.readOnly = readOnly;

		this.game = game;

		this.itemSequence = new ArrayList<ShipType>();

		this.itemSequence.add(ShipType.MONEY_PRODUCTION);
		this.itemSequence.add(ShipType.BATTLESHIP_PRODUCTION);
		this.itemSequence.add(ShipType.DEFENSIVE_BATTLESHIPS);

		this.itemSequence.add(ShipType.SPY);
		this.itemSequence.add(ShipType.TRANSPORT);
		this.itemSequence.add(ShipType.PATROL);
		this.itemSequence.add(ShipType.MINESWEEPER);

		this.itemSequence.add(ShipType.MINE50);
		this.itemSequence.add(ShipType.MINE100);
		this.itemSequence.add(ShipType.MINE250);
		this.itemSequence.add(ShipType.MINE500);

		int lineIndex = 1;

		for (ShipType shipType: this.game.getEditorPrices().keySet())
		{
			pricesBuy.put(shipType, this.game.getPriceBuy(shipType));
			pricesSell.put(shipType, this.game.getPriceSell(shipType));
		}

		Planet planet = (Planet)CommonUtils.klon(game.getPlanets()[planetIndex]);

		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.PLANET_EDITOR);
		this.updateDisplay(planet, planetIndex, lineIndex, false);
		game.setScreenContentMode(ScreenContent.MODE_PLANET_EDITOR);

		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

		if (this.readOnly)
			allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));
		else
		{
			allowedKeys.add(new ConsoleKey("\u2191",VegaResources.ChangeSelection(true)));
			allowedKeys.add(new ConsoleKey("\u2193",VegaResources.ChangeSelection(true)));
			allowedKeys.add(new ConsoleKey("\u2192",VegaResources.Buy(true)));
			allowedKeys.add(new ConsoleKey("\u2190",VegaResources.Sell(true)));
			allowedKeys.add(new ConsoleKey("ESC",VegaResources.Cancel(true)));
			allowedKeys.add(new ConsoleKey("ENTER",VegaResources.AcceptChanges(true)));
		}

		boolean takeOver = false;

		do
		{
			ConsoleInput input = game.getConsole().waitForKeyPressed(allowedKeys, false);

			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				takeOver = false;
				break;
			}

			if (this.readOnly)
				continue;

			if (input.getLastKeyCode() == KeyEvent.VK_ENTER)
			{
				takeOver = !this.readOnly ;
				break;
			}

			if (input.getLastKeyCode() == KeyEvent.VK_DOWN ||
					input.getInputText().equals("2"))
			{
				lineIndex++;
				if (lineIndex >= itemSequence.size())
					lineIndex = 0;
			}

			if (input.getLastKeyCode() == KeyEvent.VK_UP  ||
					input.getInputText().equals("8"))
			{
				lineIndex--;
				if (lineIndex < 0)
					lineIndex = itemSequence.size() - 1;
			}

			if (input.getLastKeyCode() == KeyEvent.VK_RIGHT  ||
					input.getInputText().equals("6"))
				this.buySell(true, planetIndex, planet, itemSequence.get(lineIndex));

			if (input.getLastKeyCode() == KeyEvent.VK_LEFT  ||
					input.getInputText().equals("4"))
				this.buySell(false, planetIndex, planet, itemSequence.get(lineIndex));

			this.updateDisplay(planet, planetIndex, lineIndex, false);

		} while (true);

		game.getConsole().clear();
		game.getConsole().setMode(Console.ConsoleModus.TEXT_INPUT);
		game.setScreenContentMode(ScreenContent.MODE_BOARD);

		if (takeOver)
		{
			moves.add(new Move(planetIndex, game.getPlanets()[planetIndex], planet));
			game.getPlanets()[planetIndex] = planet;

			game.getConsole().appendText(VegaResources.MoveEntered(true));
		}
		else
			game.getConsole().appendText(VegaResources.ActionCancelled(true));

		game.getConsole().lineBreak();
	}

	private void buySell(
			boolean buy,
			int planetIndex,
			Planet planet,
			ShipType itemType)

	{
		if (itemType == ShipType.MONEY_PRODUCTION)
		{
			if (buy)
				planet.buyMoneyProduction(this.game.getPriceBuy(ShipType.MONEY_PRODUCTION));
		}
		else if (itemType == ShipType.BATTLESHIP_PRODUCTION)
		{
			if (buy)
				planet.incrementBattleshipProduction();
			else
				planet.decrementBattleshipProduction();
		}
		else if (itemType == ShipType.DEFENSIVE_BATTLESHIPS)
		{
			if (buy)
				planet.buyDefensiveBattleships(this.game.getPriceBuy(itemType));				
			else
				planet.sellDefensiveBattleships(this.game.getPriceSell(itemType));
		}
		else
		{
			if (buy)
				planet.buyShip(itemType, 1, this.game.getPriceBuy(itemType));
			else
				planet.sellShip(itemType, this.game.getPriceSell(itemType));
		}
	}

	private void updateDisplay (Planet planet, int planetIndex, int lineIndex, boolean readOnly)
	{
		Hashtable<ShipType,String> ships = new Hashtable<ShipType,String>();
		HashSet<ShipType> buyImpossible = new HashSet<ShipType>();
		HashSet<ShipType> sellImpossible = new HashSet<ShipType>();

		for (ShipType itemType: this.itemSequence)
		{
			int moneySupply = planet.getMoneySupply();

			String countString = "";
			int count = 0;

			if (itemType == ShipType.MONEY_PRODUCTION)
			{
				count = planet.getMoneyProduction();
				countString = Integer.toString(count);
			}
			else if (itemType == ShipType.BATTLESHIP_PRODUCTION)
			{
				count = planet.getBattleshipProduction();
				countString = Integer.toString(count);
			}
			else if (itemType == ShipType.DEFENSIVE_BATTLESHIPS)
			{
				count = planet.getDefensiveBattleshipsCount();
				countString = CommonUtils.convertToString(count);
			}
			else
			{
				count = planet.getShipsCount(itemType);
				countString = CommonUtils.convertToString(count);
			}

			ships.put(itemType, countString);

			if (itemType == ShipType.DEFENSIVE_BATTLESHIPS)
			{
				if (planet.getDefensiveBattleshipsBuyCountMax() == 0 ||
					planet.getDefensiveBattleshipsBuyActualPrice(this.game.getPriceBuy(itemType)) > moneySupply)
				{
					buyImpossible.add(itemType);
				}
			}
			else if (itemType == ShipType.MONEY_PRODUCTION && planet.getMoneyProduction() >= Game.MONEY_PRODUCTION_MAX)
				buyImpossible.add(itemType);
			else if (this.game.getPriceBuy(itemType) > moneySupply)
				buyImpossible.add(itemType);

			if (count < 1)
				sellImpossible.add(itemType);
		}

		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());

		byte colorIndex = Colors.WHITE;
		
		@SuppressWarnings("unchecked")
		Hashtable<ShipType,Integer> pricesBuyClone = (Hashtable<ShipType, Integer>) CommonUtils.klon(this.pricesBuy);
		
		pricesBuyClone.put(
				ShipType.DEFENSIVE_BATTLESHIPS, 
				planet.getDefensiveBattleshipsBuyActualPrice(this.pricesBuy.get(ShipType.DEFENSIVE_BATTLESHIPS)));
		
		pricesBuyClone.put(
				ShipType.MONEY_PRODUCTION, 
				planet.getMoneyProductionIncreaseActualPrice(this.pricesBuy.get(ShipType.MONEY_PRODUCTION)));
		
		@SuppressWarnings("unchecked")
		Hashtable<ShipType,Integer> pricesSellClone = (Hashtable<ShipType, Integer>) CommonUtils.klon(this.pricesSell);
		
		pricesSellClone.put(
				ShipType.DEFENSIVE_BATTLESHIPS, 
				planet.getDefensiveBattleshipsSellActualPrice(this.pricesSell.get(ShipType.DEFENSIVE_BATTLESHIPS)));

		this.game.getScreenContent().setPlanetEditor(
				new ScreenContentPlanetEditor(
						this.itemSequence.get(lineIndex),
						ships,
						pricesBuyClone,
						pricesSellClone,
						buyImpossible,
						sellImpossible,
						colorIndex,
						planet.getMoneySupply(),
						planet.getMoneyProductionMaxIncrease(),
						planet.getDefensiveBattleshipsBuyCountMax(),
						planet.getDefensiveBattleshipsSellCountMax(),
						this.readOnly));

		this.game.getGameThread().updateDisplay(this.game.getScreenContent());
	}
}
