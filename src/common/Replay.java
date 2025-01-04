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
import java.util.Hashtable;

class Replay
{
	static final int 			PAUSE_MILLISECS = 1000;
	private static final int 	PAUSE_MILLISECS_ANIMATION = 200;
	
	private Game game;

	Replay(Game game)
	{
		this.game = game;

		if (game.isGoToReplay())
		{
			game.setGoToReplay(false);

			if (this.game.evaluationExists())
			{
				this.replayArchive();
			}

			return;
		}

		if (!this.game.evaluationExists())
		{
			return;
		}

		this.game.getConsole().clear();
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> " + VegaResources.ReplayEvaluation(true), Colors.NEUTRAL);

		this.replayArchive();

		this.game.updatePlanetList(false);
		this.game.updateBoard();
	}

	private void animate(ScreenContent screenContentDayEventPrevious, ScreenContent screenContentDayEventCurrent)
	{
		int day = screenContentDayEventPrevious.getEventDay() + 2;

		if (day >= screenContentDayEventCurrent.getEventDay() - 1)
			return;

		screenContentDayEventPrevious.getBoard().clearMarks();
		screenContentDayEventPrevious.getConsole().clearKeys();

		Hashtable<Integer, ScreenContentBoardObject> shipPositionsPrevious = new Hashtable<Integer, ScreenContentBoardObject>();

		for (ScreenContentBoardObject position: screenContentDayEventPrevious.getBoard().getObjects())
		{
			shipPositionsPrevious.put(position.getHashCode(), position);
		}

		Hashtable<Integer, ScreenContentBoardObject> shipPositionsCurrent = new Hashtable<Integer, ScreenContentBoardObject>();

		for (ScreenContentBoardObject position: screenContentDayEventCurrent.getBoard().getObjects())
		{
			shipPositionsCurrent.put(position.getHashCode(), position);
		}

		ArrayList<ScreenContentBoardObject> positions = 
				new ArrayList<ScreenContentBoardObject>();

		while (day < screenContentDayEventCurrent.getEventDay())
		{
			this.game.pause(PAUSE_MILLISECS_ANIMATION);

			screenContentDayEventPrevious.getConsole().setProgressBarDay(day);

			positions.clear();

			for (Integer hashShip: shipPositionsPrevious.keySet())
			{
				if (!shipPositionsCurrent.containsKey(hashShip))
					continue;

				ScreenContentBoardObject positionPrevious = shipPositionsPrevious.get(hashShip);
				ScreenContentBoardObject positionCurrent = shipPositionsCurrent.get(hashShip);

				double t = (double)(day - screenContentDayEventPrevious.getEventDay()) / 
						(double)(screenContentDayEventCurrent.getEventDay() - screenContentDayEventPrevious.getEventDay());

				double x = positionPrevious.getPosition().x + t * (positionCurrent.getPosition().x - positionPrevious.getPosition().x);
				double y = positionPrevious.getPosition().y + t * (positionCurrent.getPosition().y - positionPrevious.getPosition().y);

				ScreenContentBoardObject point = 
						new ScreenContentBoardObject(
								positionPrevious.getHashCode(),
								new Point(x, y), 
								positionPrevious.getDestination(),
								positionPrevious.getSymbol(),
								positionPrevious.getColorIndex(), 
								positionPrevious.isLineDarker(),
								positionPrevious.isEndpointAtDestination(),
								positionPrevious.getRadar());

				positions.add(point);
			}

			screenContentDayEventPrevious.getBoard().setObjects(positions);

			this.game.getGameThread().updateDisplay(screenContentDayEventPrevious);

			day += 2;
		}

		this.game.pause(PAUSE_MILLISECS_ANIMATION);
	}

	private void replayArchive()
	{
		this.game.getConsole().enablePlanetListContentToggle(false);

		this.game.getConsole().clear();
		this.game.getConsole().setHeaderText(
				VegaResources.ReplayOfEvaluationOfYear(
						true,
						Integer.toString(this.game.getYear())),
				Colors.NEUTRAL);

		ScreenContent screenContentDayEventPrevious = null;
		ReplayKeyPressed keyPressed = ReplayKeyPressed.NEXT; 
		
		for (ScreenContent screenContentDayEventCurrent: this.game.getReplayLast())
		{
			if (keyPressed == ReplayKeyPressed.NEXT_ANIMATED &&
					screenContentDayEventPrevious != null && 
					screenContentDayEventCurrent.getEventDay() != screenContentDayEventPrevious.getEventDay())
			{
				this.animate(
						screenContentDayEventPrevious, 
						screenContentDayEventCurrent);
			}

			this.game.getGameThread().updateDisplay(screenContentDayEventCurrent);

			if (screenContentDayEventCurrent.isPause())
				this.game.pause(PAUSE_MILLISECS);
			else if (!screenContentDayEventCurrent.isSnapshot())
				keyPressed = this.game.getConsole().waitForKeyPressedReplay();

			if (keyPressed == ReplayKeyPressed.ABORT)
				break;

			screenContentDayEventPrevious = (ScreenContent)CommonUtils.klon(screenContentDayEventCurrent);
		}		

		this.game.getConsole().enablePlanetListContentToggle(true);
	}
}
