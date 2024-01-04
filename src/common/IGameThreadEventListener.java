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

import java.util.EventListener;

public interface IGameThreadEventListener extends EventListener
{
	void checkMenuEnabled();
	void endTutorial();
	MovesTransportObject importMovesFromEmail();
	boolean isMoveEnteringOpen();
	boolean launchEmailClient(String recipient, String subject, String body, EmailTransportBase obj);
	boolean openPdf(byte[] pdfBytes, String clientId);
	void pause(int milliseconds);
	boolean postMovesToServer(String gameId, String playerName, MovesTransportObject transportObject);
	void saveGame(Game game, boolean autoSave);
	void updateDisplay(ScreenUpdateEvent event);
	void updateTutorialPanel(String text, int currentStep, int totalSteps, boolean enableNextButton);
}
