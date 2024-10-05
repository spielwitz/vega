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

import java.util.ArrayList;

import commonUi.CommonUiUtils;

class DistanceMatrix
{
	private Game game;
	private InventoryPdfData pdfData;
	private GameInformation gameInformation;
	private ScreenContent screenContentCopy;

	DistanceMatrix(Game game)
	{
		this.game = game;
	}

	byte[] create(String languageCode)
	{
		this.screenContentCopy = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());

		String languageCodeCopy = VegaResources.getLocale();
		VegaResources.setLocale(languageCode);

		this.gameInformation = new GameInformation();
		this.gameInformation.setGame(this.game);

		this.pdfData = new InventoryPdfData(
				"",
				this.game.getYear(),
				this.game.getYearMax(),
				0,
				true);

		this.createData();

		this.game.setScreenContent(screenContentCopy);

		byte[] pdfByteArray = null;			
		try
		{
			pdfByteArray = InventoryPdf.create(pdfData);
		}
		catch (Exception e)
		{
		}

		VegaResources.setLocale(languageCodeCopy);

		return pdfByteArray;
	}

	void showUserDialog()
	{
		this.game.getConsole().setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+VegaResources.MainMenu(true)+" -> "+VegaResources.DistanceMatrix(true),
				Colors.NEUTRAL);

		this.game.getConsole().appendText(VegaResources.OpenPdfViewerQuestion(true) + " ");

		ConsoleInput input = this.game.getConsole().waitForKeyPressedYesNo(false);

		if (input.getInputText().equals(Console.KEY_YES))
		{
			byte[] pdfBytes = this.create(input.getLanguageCode());
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

	private void createData()
	{
		InventoryPdfChapter chapter = 
				new InventoryPdfChapter(VegaResources.DistanceMatrixHeader(false), "");

		this.game.setScreenContent(new ScreenContent());

		chapter.table = new InventoryPdfTable(this.game.getPlanetsCount() + 2);
		chapter.table.highlightFirstColumn = true;
		chapter.table.highlightLastColumn = true;
		chapter.table.highlightLastRow = true;
		chapter.table.smallFont = true;

		this.createGridHeaderLine(chapter.table);

		String decimalSeparator = CommonUiUtils.getDecimalSeparator();

		for (int i = 0; i < this.game.getPlanetsCount(); i++)
		{
			int planetIndexStart = game.getPlanetsSorted()[i];

			chapter.table.cells.add(this.game.getPlanetNameFromIndex(planetIndexStart));

			for (int j = 0; j < this.game.getPlanetsCount(); j++)
			{
				int planetIndexDestination = game.getPlanetsSorted()[j];

				double distance = 
						CommonUtils.round(this.game.getPlanets()[planetIndexStart].getPosition().distance(this.game.getPlanets()[planetIndexDestination].getPosition()),2);

				if (distance > 0)
				{
					String distanceString = CommonUtils.formatNumericValue(distance);
					
					String[] s = distanceString.split(decimalSeparator);

					if (s[1].equals("00"))
					{
						chapter.table.cells.add(s[0]);
					}
					else
					{
						chapter.table.cells.add(
								s[0]+decimalSeparator+"\n"+s[1]);
					}
				}
				else
					chapter.table.cells.add("");
			}
			
			chapter.table.cells.add(this.game.getPlanetNameFromIndex(planetIndexStart));
		}
		
		this.createGridHeaderLine(chapter.table);

		ArrayList<ScreenContentBoardPlanet> planets = this.getBoard();

		this.game.getScreenContent().setBoard(
				new ScreenContentBoard(planets,
						null,
						null,
						null));

		chapter.screenContent = (ScreenContent)CommonUtils.klon(this.game.getScreenContent());
		chapter.screenContent.setMode(ScreenContent.MODE_DISTANCE_MATRIX);
		this.pdfData.chapters.add(chapter);
	}
	
	private void createGridHeaderLine(InventoryPdfTable table)
	{
		table.cells.add("");
		table.colAlignRight[0] = true;

		for (int i = 0; i < this.game.getPlanetsCount(); i++)
		{
			int planetIndex = game.getPlanetsSorted()[i];
			table.cells.add(CommonUtils.padString(" "+this.game.getPlanetNameFromIndex(planetIndex), 2));
			table.colAlignRight[i+1] = false;
		}
		
		table.cells.add("");
	}

	private ArrayList<ScreenContentBoardPlanet> getBoard()
	{
		ArrayList<ScreenContentBoardPlanet> planets = new ArrayList<ScreenContentBoardPlanet>(this.game.getPlanetsCount());

		for (int planetIndex = 0; planetIndex < this.game.getPlanetsCount(); planetIndex++)
		{
			planets.add(new ScreenContentBoardPlanet(
					this.game.getPlanetNameFromIndex(planetIndex),
					this.game.getPlanets()[planetIndex].getPosition(),
					Colors.BLACK,
					null)); 				
		}

		if (this.game.getScreenContent() == null)
			this.game.setScreenContent(new ScreenContent());

		return planets;
	}
}
