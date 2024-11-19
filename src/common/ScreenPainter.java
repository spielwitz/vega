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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ScreenPainter
{	
	public static final int			SCREEN_WIDTH = 650;
	public static final int			SCREEN_HEIGHT = 480;
	
	private static final int		BOARD_OFFSET_X = 10;
	private static final int		BOARD_OFFSET_Y = 10;
	public static final int			BOARD_DX = 18;
	
	private static final double		SHIP_SIZE = 0.75;
	private static final int		SHIP_SIZE_PIXEL_MIN = 2;
	
	private static final int 		PLANET_EDITOR_COLUMN1 = 5;
	private static final int 		PLANET_EDITOR_COLUMN2 = 49;
	private static final int 		PLANET_EDITOR_COLUMN3 = 70;
	
	private static final String 	CURSOR_CHARACTER = "_";
	
	private static final int 		PLANETLIST_OFFSET_X = 2 * BOARD_OFFSET_X + Game.BOARD_MAX_X * BOARD_DX;
	private static final int 		PLANETLIST_OFFSET_Y = BOARD_OFFSET_Y;
	private static final int 		PLANETLIST_WIDTH = SCREEN_WIDTH - PLANETLIST_OFFSET_X - BOARD_OFFSET_X;
	private static final int 		PLANETLIST_HEIGHT = Game.BOARD_MAX_Y * BOARD_DX;
	private static final int		PLANETLIST_AXIS_X =CommonUtils.round((double)PLANETLIST_WIDTH / 2. + (double)PLANETLIST_OFFSET_X);
	private static final int		PLANETLIST_AXIS_DISTANCE = CommonUtils.round((double)PLANETLIST_WIDTH / 3.);
	
	private static final int		CONSOLE_LINES_COUNT = Console.TEXT_LINES_COUNT_MAX + 3;
	private static final int		CONSOLE_LINE_HEIGHT = 16;
	private static final int		CONSOLE_HEIGHT = CONSOLE_LINES_COUNT * CONSOLE_LINE_HEIGHT;
	
	private static final int	    PROGRESS_BAR_BORDER = 2;
	
	static ArrayList<String> titleLinesCount;
	
	static {
		titleLinesCount = new ArrayList<String>();
		
		titleLinesCount.add("VEGA             `     `    `    `      `    `    `    `  `   `             ");
		titleLinesCount.add(" `   `    `     `           `           `         `          jj.wwg@#@4k.  `");
		titleLinesCount.add("                  `    `         `           `         .,;!'` Qyxsj#@Qm     ");
		titleLinesCount.add("`     `    `   `                             `     .j!''`.` `..Q@$h$$@`     ");
		titleLinesCount.add("     `    `     ` `    `    `    `      `     `..!|:. jJ\"^s, `..7@$$Qk     ");
		titleLinesCount.add(" `                                           .z|`.``..b.jaoud,..j.7Qk   `   ");
		titleLinesCount.add("               `  `    `    `    `        ..T;::..``..pJ$Xoooa :!xgk   `    ");
		titleLinesCount.add("`    `    `      `                .jj,  .jl:``````````3yqSooo3.vzwt         ");
		titleLinesCount.add("      `                  `    ..g@@@@@ggl:''`'`''`'''`'.=&gwZvoa2`          ");
		titleLinesCount.add(" `         `   `  `    `    j2@@###$#$g@.:::'`'':'':'`:':::jxogP            ");
		titleLinesCount.add("     `    `     `       ` .@@@X$$$$@g8l:;''':''::''`'':::!uxq@l             ");
		titleLinesCount.add("`                `      j2@@$$qg#@gMu:::'::':':!''`':`::jxqpl            `  ");
		titleLinesCount.add("     `     `   `       .@@QgpP`  .k||!:::::':':::`:.::jxq2n  `          `   ");
		titleLinesCount.add(" `        `       `  .2@@@P`    jCv|,,,!::::::```:.jxzwH`      `   `        ");
		titleLinesCount.add("     `         `  ` .@@@l      Jn||i!,,!!:`:`.::j|agZF                      ");
		titleLinesCount.add("`                  .@R        jox||i,,::;:::!j2@#@E    `                    ");
		titleLinesCount.add("     `    `    ` ` = ..jjjj, jpvxxx\"xvvxxcsj@@$$pQI `                      ");
		titleLinesCount.add(" `         ` ``  j.qgpRsjuz|Yg.5paojaagpA^@@$XX#g@t   `                     ");
		titleLinesCount.add("     ` `       .q#Q8xzgpT     4.3PT='    .@$h#$@@t     `                 `  ");
		titleLinesCount.add("`          ` .@@@@sJo2l      .im       .@$$#$@@P         `                  ");
		titleLinesCount.add("     ` `   .@$@gE.nuF     ..5!jk     .@###p@@l   `     `            `       ");
		titleLinesCount.add(" `      ` .@@$qFjv2`   ..Zn;:j@   ..@$pg@PT       `           `         `   ");
		titleLinesCount.add("     `  .@$eKqk.iZ ..!n!|'ujQm  .@@@pPT                `                    ");
		titleLinesCount.add("`  `   .@@$Xgm.|;:!:|':jz@p@F .@MP`     `  ` `    `    `  `                 ");
		titleLinesCount.add("  `   .@@##Qm `:jjjwW$#pgpP                  `    `                         ");
		titleLinesCount.add("  `  .@@kh###D#VyyppogpP`   `           `              `  `                 ");
		titleLinesCount.add("`   .@$kh#opV#pppgpHT`      `    `      `                `         `        ");
		titleLinesCount.add("   .@$#k3f#gg#H='                `           `    `      (c) 1989-2024      ");
		titleLinesCount.add("   @@$@8PT'           `     `    `      `              Michael Schweitzer   ");
		titleLinesCount.add("`              `  `    `                     `    `        Build " + Game.BUILD + "       ");

	}
	
	private double factor;
		
	private Graphics2D dbGraphics;
	private Font fontPlanets, fontMines, fontSectors;
	
	private FontMetrics fmPlanets, fmMines, fmSectors;
	private ScreenContent screenContent;
	private boolean inputEnabled;
	
	public ScreenPainter(
			ScreenContent screenContent, 
			boolean inputEnabled,
			Graphics2D dbGraphics, 
			Font fontPlanets, 
			Font fontMines, 
			Font fontSector,
			double factor)
	{
		this.screenContent = screenContent;
		this.inputEnabled = inputEnabled;
		this.dbGraphics = dbGraphics;
		this.fontPlanets = fontPlanets;
		this.fontMines = fontMines;
		this.fontSectors = fontSector;
		this.factor = factor;
		
		this.dbGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.setColor(
				this.screenContent != null && this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX ?
						Color.WHITE:
						Color.BLACK);
		this.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		this.fmPlanets = this.dbGraphics.getFontMetrics(this.fontPlanets);
					
		if (this.screenContent != null)
		{
			this.fmSectors = this.dbGraphics.getFontMetrics(this.fontSectors);
			this.fmMines = this.dbGraphics.getFontMetrics(this.fontMines);
			
			this.drawConsole();
			
			if (this.screenContent.getMode() == ScreenContent.MODE_BOARD)
			{
				this.drawBoard();
				this.drawPlanetList();
			}
			else if (this.screenContent.getMode() == ScreenContent.MODE_STATISTICS)
				this.drawStatistics();
			else if (this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX)
				this.drawBoard();
			else
				this.drawPlanetEditor();
			
			if (!this.inputEnabled)
				this.drawLockSymbol();
		}
		else
			this.drawTitle();
	}
	
	private Rectangle clipToBoard()
	{
		Point screenPosition = this.getScreenPositionFromBoardPosition(new Point(-0.5, -0.5));
		
		Rectangle currentClipBounds = this.dbGraphics.getClipBounds();
		
		this.dbGraphics.setClip(
				CommonUtils.round(screenPosition.x) - 1, 
				CommonUtils.round(screenPosition.x) - 1, 
				CommonUtils.round(Game.BOARD_MAX_X * BOARD_DX * this.factor) + 2, 
				CommonUtils.round(Game.BOARD_MAX_Y * BOARD_DX * this.factor) + 2);
		
		return currentClipBounds;
	}
	
	private int consoleGetX(int column, int xOff, int charWidth)
	{
		return CommonUtils.round(this.factor * (double)xOff) + charWidth * (column+1);
	}
	
	private int consoleGetY(int line, int yOff, int fontHeight)
	{
		return CommonUtils.round(this.factor * (double)(yOff + (line + 0.5) * CONSOLE_LINE_HEIGHT) + (double)fontHeight/2.);
	}
	private void drawBoard()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentBoard screenContentBoard = this.screenContent.getBoard();
		if (screenContentBoard == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		
		for (int x = 0; x <= Game.BOARD_MAX_X; x++)
			this.drawLine(
					new Point(x - 0.5, -0.5),
					new Point(x - 0.5, Game.BOARD_MAX_Y - 0.5));
		
		for (int y = 0; y <= Game.BOARD_MAX_Y; y++)
			this.drawLine(
					new Point(-0.5, y - 0.5),
					new Point(Game.BOARD_MAX_X - 0.5, y - 0.5));
		
		for (int y = 0; y < Game.BOARD_MAX_Y; y++)
		{
			for (int x = 0; x < Game.BOARD_MAX_X; x++)
			{
				Point pt = new Point(x,y);
				this.drawBoardTextCentered(
						pt, 
						Game.getSectorNameFromPositionStatic(pt),
						new Color(60, 60, 60),
						this.fontSectors,
						this.fmSectors);
			}
		}
		
		this.drawBoardMines(screenContentBoard.getMines());
		this.drawBoardObjectsLines(screenContentBoard.getObjects());
		this.drawBoardObjectsRadarCircles(screenContentBoard.getObjects(), false);
		
		if (screenContentBoard.getPlanets() != null)
		{
			for (ScreenContentBoardPlanet screenContentBoardPlanet: screenContentBoard.getPlanets())
			{
				if (this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX)
				{
					this.drawBoardFillCircle(
							screenContentBoardPlanet.getPosition(), 
							1.2, 
							Color.white);
					
					this.drawBoardCircle(
							screenContentBoardPlanet.getPosition(), 
							1.2, 
							Colors.get(screenContentBoardPlanet.getColorIndex()));
				}
				else
				{
					this.drawBoardFillCircle(
							screenContentBoardPlanet.getPosition(), 
							1.2, 
							Colors.getColorDarker2(Colors.get(screenContentBoardPlanet.getColorIndex())));
				}
								
				this.drawBoardTextCentered(screenContentBoardPlanet.getPosition(), screenContentBoardPlanet.getName(),Colors.get(screenContentBoardPlanet.getColorIndex()), this.fontPlanets, this.fmPlanets);
				
				this.drawBoardPlanetFrames(screenContentBoardPlanet);
			}
		}

		this.drawBoardObjectsRadarCircles(screenContentBoard.getObjects(), true);
		this.drawBoardPositionsMarked(screenContentBoard.getPositionsMarked());		
		this.drawBoardObjectsShipSymbols(screenContentBoard.getObjects());		
	}
	
	private void drawBoardCircle(Point positionBoard, double diameterBoard, Color color)
	{
		this.setColor(color);
		
		Point positionScreen = this.getScreenPositionFromBoardPosition(positionBoard);
		double radiusScreen = diameterBoard * BOARD_DX * this.factor * 0.5;
		
		this.dbGraphics.draw(
				new Ellipse2D.Double(
						positionScreen.x - radiusScreen,
						positionScreen.y - radiusScreen,
						2 * radiusScreen,
						2 * radiusScreen));
	}
	
	private void drawBoardFillCircle(Point positionBoard, double diameterBoard, Color color)
	{
		this.setColor(color);
		
		Point positionScreen = this.getScreenPositionFromBoardPosition(positionBoard);
		double radiusScreen = diameterBoard * BOARD_DX * this.factor * 0.5;
		
		this.dbGraphics.fill(
				new Ellipse2D.Double(
						positionScreen.x - radiusScreen,
						positionScreen.y - radiusScreen,
						2 * radiusScreen,
						2 * radiusScreen));
	}
	
	private void drawBoardFillDiamond(Point positionBoard, Color color)
	{
		this.setColor(color);
		
		int[] x = new int[4];
		int[] y = new int[4];
		
		Point positionScreen = this.getScreenPositionFromBoardPosition(positionBoard);
		double radiusScreen = 0.5 * BOARD_DX * this.factor;
		
		
		x[0] = CommonUtils.round(positionScreen.x - radiusScreen);
		y[0] = CommonUtils.round(positionScreen.y);
		
		x[1] = CommonUtils.round(positionScreen.x);
		y[1] = CommonUtils.round(positionScreen.y - radiusScreen);
		
		x[2] = CommonUtils.round(positionScreen.x + radiusScreen);
		y[2] = CommonUtils.round(positionScreen.y);
		
		x[3] = CommonUtils.round(positionScreen.x);
		y[3] = CommonUtils.round(positionScreen.y + radiusScreen);
		
		this.dbGraphics.fillPolygon(x, y, 4);
	}
	
	private void drawBoardMines (ArrayList<ScreenContentBoardMine> screenContentBoardMine)
	{
		if (screenContentBoardMine == null || screenContentBoardMine.size() == 0)
			return;

		for (ScreenContentBoardMine mine: screenContentBoardMine)
		{
			Point position = new Point(mine.getPositionX(), mine.getPositionY()); 
			
			this.drawBoardFillDiamond(
					position,
					Color.darkGray);
		
			this.drawBoardTextCentered(
					position,
					Integer.toString(mine.getStrength()),
					Colors.get(Colors.NEUTRAL), this.fontMines, this.fmMines);
		}
	}
	
	private void drawBoardObjectsLines(ArrayList<ScreenContentBoardObject> objects)
	{
		if (objects == null)
			return;
		
		for (ScreenContentBoardObject object: objects)
		{
			if (object.getPosition() != null && object.getDestination() != null)
			{
				Color color = 
						object.isLineDarker() ?
								Colors.get(Colors.getColorIndexDarker(object.getColorIndex())) :
									Colors.get(object.getColorIndex());
				this.setColor(color);
				
				if (!object.getPosition().equals(object.getDestination()))
				{
					this.drawLine(object.getPosition(), object.getDestination());
				
					if (object.isEndpointAtDestination())
						this.drawBoardFillCircle(object.getDestination(), 0.33, color);
				}
			}
		}
	}

	
	private void drawBoardObjectsRadarCircles(ArrayList<ScreenContentBoardObject> objects, boolean foreground)
	{
		if (objects == null)
			return;
		
		Rectangle currentClipBounds = this.clipToBoard();
		
		for (ScreenContentBoardObject object: objects)
		{
			if (object.getRadar() != null && object.getRadar().isForeground() == foreground)
			{
				double rr = 2 * object.getRadar().getRadius() * (double)BOARD_DX;
				
				double x = (double)(BOARD_OFFSET_X + object.getPosition().getX() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
				double y = (double)(BOARD_OFFSET_X + object.getPosition().getY() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
				
				if (object.getRadar().isHighlighted())
				{
					this.setColor(Colors.get(object.getColorIndex()));
					
					AlphaComposite compositeBefore = (AlphaComposite) this.dbGraphics.getComposite();
					float alpha = 0.3f;
					int type = AlphaComposite.SRC_OVER; 
					AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
					this.dbGraphics.setComposite(composite);
					
					this.dbGraphics.fillOval(
							CommonUtils.round(this.factor * x), 
							CommonUtils.round(this.factor * y), 
							CommonUtils.round(this.factor * rr), 
							CommonUtils.round(this.factor * rr));
					
					this.dbGraphics.setComposite(compositeBefore);		
				}
				else
				{
					this.setColor(Colors.getColor50Percent(Colors.get(object.getColorIndex())));
				}
				
				this.dbGraphics.drawOval(
						CommonUtils.round(this.factor * x), 
						CommonUtils.round(this.factor * y), 
						CommonUtils.round(this.factor * rr), 
						CommonUtils.round(this.factor * rr));

			}
		}
		
		this.dbGraphics.setClip(currentClipBounds);
	}
		
	private void drawBoardObjectsShipSymbols(ArrayList<ScreenContentBoardObject> objects)
	{
		if (objects == null)
			return;
		
		double size = Math.max(
				4 * SHIP_SIZE * this.factor,
				SHIP_SIZE_PIXEL_MIN); 
		
		for (ScreenContentBoardObject object: objects)
		{
			if (object.getPosition() == null)
			{
				continue;
			}
			
			this.drawShipSymbol(
					getScreenPositionFromBoardPosition(object.getPosition()), 
					size, 
					object.getColorIndex(), 
					object.getSymbol());
		}
	}
	
	private void drawBoardPlanetFrames (ScreenContentBoardPlanet screenContentBoardPlanet)
	{
		if (screenContentBoardPlanet.getFrameColors() == null || screenContentBoardPlanet.getFrameColors().size() == 0)
			return;
		
		Point positionScreen = this.getScreenPositionFromBoardPosition(screenContentBoardPlanet.getPosition());

		for (int i = 0; i < screenContentBoardPlanet.getFrameColors().size(); i++)
		{
			this.setColor(Colors.get(screenContentBoardPlanet.getFrameColors().get(i)));
			
			double sizeScreen = this.factor * (BOARD_DX + 4 * (i+1));
			
			this.dbGraphics.draw(
					new Rectangle2D.Double(
							positionScreen.x - sizeScreen * 0.5,
							positionScreen.y - sizeScreen * 0.5,
							sizeScreen,
							sizeScreen));
		}
	}
	
	private void drawBoardPositionsMarked (ArrayList<Point> positions)
	{
		if (positions == null)
			return;
		
		for (Point position: positions)
		{
			this.drawBoardCircle(position, 0.8, Color.white);
			
			this.setColor(Color.white);
			
			this.drawLine(
					new Point(position.x - 0.5, position.y),
					new Point(position.x + 0.5, position.y));
			
			this.drawLine(
					new Point(position.x, position.y - 0.5),
					new Point(position.x, position.y + 0.5));
		}
	}
	
	private void drawBoardTextCentered(Point positionBoard, String text, Color color, Font font, FontMetrics fm)
	{
		this.dbGraphics.setFont(font);
		
		double width = fm.stringWidth(text);
		double height = fm.getAscent() - fm.getDescent();
		
		Point positionScreen = this.getScreenPositionFromBoardPosition(positionBoard);
		
		this.setColor(color);
		
		int x = CommonUtils.round(positionScreen.x - 0.5 * width);
		int y = CommonUtils.round(positionScreen.y + 0.5 * height);
		
		this.dbGraphics.drawString(text, x, y);
	}
	
	private void drawConsole()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentConsole screenContentConsole = this.screenContent.getConsole();
		if (screenContentConsole == null)
			return;

		this.setColor(new Color(50, 50, 50));

		int x0 = BOARD_OFFSET_X;
		int y0 = 2 * BOARD_OFFSET_Y + Game.BOARD_MAX_Y * BOARD_DX;
		int w = SCREEN_WIDTH - 2 * BOARD_OFFSET_X;
		
		this.drawRect(x0, y0, w, CONSOLE_HEIGHT);
		this.drawRect(x0, y0, w, CONSOLE_LINE_HEIGHT);
		
		if (screenContentConsole.getProgressBarDay() >= 0)
		{
			int progressBarX = x0 + w/2;
			int progressBarY = y0 + PROGRESS_BAR_BORDER;
			int progressBarWidth = w/2 - PROGRESS_BAR_BORDER;
			int progressBarHeight = Math.max(1, CONSOLE_LINE_HEIGHT - 2 * PROGRESS_BAR_BORDER) + 1;
			
			int progressActual = 0;
			String text = "";
			
			if (screenContentConsole.getProgressBarDay() == 0)
			{
				progressActual = 0;
				text = VegaResources.BeginningOfYear(false);
			}
			else if (screenContentConsole.getProgressBarDay() == Game.DAYS_OF_YEAR_COUNT)
			{
				progressActual = progressBarWidth;
				text = VegaResources.EndOfYear(false);
			}
			else
			{
				progressActual = CommonUtils.round(
						(double) progressBarWidth * (double)screenContentConsole.getProgressBarDay() / 
						(double)Game.DAYS_OF_YEAR_COUNT);
				text = VegaResources.DayOf(
						false,
						Integer.toString(screenContentConsole.getProgressBarDay()),
						Integer.toString(Game.DAYS_OF_YEAR_COUNT));
			}
			
			this.drawRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
			this.fillRect(progressBarX, progressBarY, progressActual, progressBarHeight);
			
			this.setColor(Colors.get(screenContentConsole.getHeaderCol()));
			
			this.dbGraphics.setFont(this.fontSectors);
			
			int textWidth = fmSectors.stringWidth(text);
			int textHeight = this.fmSectors.getAscent() - this.fmSectors.getDescent();
			int textX = CommonUtils.round(
					this.factor * (progressBarX + (progressBarWidth - textWidth) / 2));
			int textY = this.consoleGetY(0, y0, textHeight);
			this.dbGraphics.drawString(text, textX, textY);
		}
		
		this.dbGraphics.setFont(this.fontPlanets);
		int fontHeight = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		int charWidth = this.fmPlanets.charWidth('H');
		
		this.setColor(Colors.get(screenContentConsole.getHeaderCol()));
		
		int x = CommonUtils.round(this.factor * (double)x0) + charWidth;
		int y = this.consoleGetY(0, y0, fontHeight);
		
		if (screenContentConsole.getHeaderText() != null)
			this.dbGraphics.drawString(VegaResources.getString(screenContentConsole.getHeaderText()), x, y);
		
		for (int i = 0; i < Console.TEXT_LINES_COUNT_MAX; i++)
		{
			this.setColor(Colors.get(screenContentConsole.getLineColors()[Console.TEXT_LINES_COUNT_MAX-i-1]));
		
			y = this.consoleGetY(i+1, y0, fontHeight);
			
			if (screenContentConsole.isWaitingForInput() && (Console.TEXT_LINES_COUNT_MAX-i-1) == screenContentConsole.getOutputLine())
				this.dbGraphics.drawString(
						VegaResources.getString(
						screenContentConsole.getTextLines()[Console.TEXT_LINES_COUNT_MAX-i-1]) + CURSOR_CHARACTER, x, y);
			else
				this.dbGraphics.drawString(
						VegaResources.getString(
						screenContentConsole.getTextLines()[Console.TEXT_LINES_COUNT_MAX-i-1]), x, y);
		}
		
		if (screenContentConsole.getAllowedKeys() != null)
		{
			int column = 0;
			
			for (int counter = 0; counter < screenContentConsole.getAllowedKeys().size(); counter += 2)
			{
				ConsoleKey key1 = screenContentConsole.getAllowedKeys().get(counter);
				ConsoleKey key2 = counter+1 < screenContentConsole.getAllowedKeys().size() ? 
										screenContentConsole.getAllowedKeys().get(counter+1) :
										null;
				
				int maxKeyLength = ConsoleKey.getMaxKeyLength(key1, key2);
				int maxTextLength = ConsoleKey.getMaxTextLength(key1, key2);
				
				int xKey = this.consoleGetX(column, x0, charWidth);
				int xText = this.consoleGetX(column + maxKeyLength + 1, x0, charWidth);
				
				if (screenContentConsole.getAllowedKeys().size() == 1)
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				else
				{
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 1, charWidth, fontHeight);
					this.writeConsoleKey(key2, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				}
				
				column += (maxKeyLength + 1 + maxTextLength + 2);
			}
		}		
	}

	private void drawLine(Point p1Board, Point p2Board)
	{
		Point p1Screen = this.getScreenPositionFromBoardPosition(p1Board);
		Point p2Screen = this.getScreenPositionFromBoardPosition(p2Board);
		
		this.dbGraphics.draw(
				new Line2D.Double(
						p1Screen.x,
						p1Screen.y,
						p2Screen.x,
						p2Screen.y));
	}
		
	private void drawLockSymbol()
	{
		this.dbGraphics.setFont(this.fontPlanets);
		
		String text = VegaResources.InputDisabled(false);
		
		int lineHeight = this.fmPlanets.getHeight();
		int lineWidth = this.fmPlanets.stringWidth(text);
		
		this.dbGraphics.setColor(Color.DARK_GRAY);
		
		int x0 = CommonUtils.round(((double)SCREEN_WIDTH * this.factor - (double)lineWidth) / 2.);
		int y0 = CommonUtils.round(((double)SCREEN_HEIGHT * this.factor - (double)lineHeight) / 2.);
		
		this.dbGraphics.fillRect(
				   x0 - lineHeight,
				   y0 - lineHeight,
				   lineWidth + 2 * lineHeight,
				   lineHeight + 2 * lineHeight);
		
		this.dbGraphics.setColor(Color.WHITE);
		
		this.dbGraphics.drawString(text, x0, y0 + fmPlanets.getAscent());
	}
	
	private void drawPlanetEditor()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentPlanetEditor screenContentPlanetEditor = this.screenContent.getPlanetEditor();
		if (screenContentPlanetEditor == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		this.drawRect(
				BOARD_OFFSET_X,
				BOARD_OFFSET_Y,
				SCREEN_WIDTH - 2 * BOARD_OFFSET_X,
				Game.BOARD_MAX_Y * BOARD_DX);
		
		this.drawPlanetEditorTextLeft(CommonUtils.padString(screenContentPlanetEditor.getMoneySupply(),4), PLANET_EDITOR_COLUMN1, 1, Colors.get(screenContentPlanetEditor.getColorIndex()));
		this.drawPlanetEditorTextLeft(VegaResources.MoneySupply(false), PLANET_EDITOR_COLUMN1+5, 1, Colors.get(Colors.NEUTRAL));
		
		this.drawPlanetEditorTextCentered(VegaResources.BuyPrice(false), PLANET_EDITOR_COLUMN2+9, 1, Colors.get(Colors.NEUTRAL));
		this.drawPlanetEditorTextCentered(VegaResources.SellPrice(false), PLANET_EDITOR_COLUMN3+9, 1, Colors.get(Colors.NEUTRAL));
		
		this.drawPlanetEditorLine(ShipType.MONEY_PRODUCTION, screenContentPlanetEditor, 
				VegaResources.IncreaseMoneyProduction(
						false, 
						Integer.toString(screenContentPlanetEditor.getProductionIncrese())), 
				3);
		this.drawPlanetEditorLine(ShipType.BATTLESHIP_PRODUCTION, screenContentPlanetEditor, VegaResources.ProductionOfBattleships(false), 4);
		this.drawPlanetEditorLine(ShipType.DEFENSIVE_BATTLESHIPS, screenContentPlanetEditor, 
				VegaResources.BuySellDefensiveBattleships(
						false, 
						Integer.toString(screenContentPlanetEditor.getDefensiveBattleshipsBuy()), 
						Integer.toString(screenContentPlanetEditor.getDefensiveBattleshipsSell())), 
				5);
		this.drawPlanetEditorLine(ShipType.BONUS, screenContentPlanetEditor, 
				VegaResources.BuySellCombatStrength(
						false,
						Integer.toString(screenContentPlanetEditor.getCombatFactorBuy())), 
				6);
		
		this.drawPlanetEditorLine(ShipType.SPY, screenContentPlanetEditor, VegaResources.Spies(false), 8);
		this.drawPlanetEditorLine(ShipType.TRANSPORT, screenContentPlanetEditor, VegaResources.TransporterPlural(false), 9);
		this.drawPlanetEditorLine(ShipType.PATROL, screenContentPlanetEditor, VegaResources.PatrouillePlural(false), 10);
		this.drawPlanetEditorLine(ShipType.MINESWEEPER, screenContentPlanetEditor, VegaResources.MinenraeumerPlural(false), 11);
		
		this.drawPlanetEditorLine(ShipType.MINE50, screenContentPlanetEditor, VegaResources.Mine50Plural(false), 13);
		this.drawPlanetEditorLine(ShipType.MINE100, screenContentPlanetEditor, VegaResources.Mine100Plural(false), 14);
		this.drawPlanetEditorLine(ShipType.MINE250, screenContentPlanetEditor, VegaResources.Mine250Plural(false), 15);
		this.drawPlanetEditorLine(ShipType.MINE500, screenContentPlanetEditor, VegaResources.Mine500Plural(false), 16);
	}
	
	private void drawPlanetEditorLine(
			ShipType type, 
			ScreenContentPlanetEditor screenContentPlanetEditor, 
			String name, 
			int line)
	{
		this.drawPlanetEditorTextLeft(CommonUtils.padString(screenContentPlanetEditor.getCount().get(type),4), PLANET_EDITOR_COLUMN1, line, Colors.get(screenContentPlanetEditor.getColorIndex()));
		this.drawPlanetEditorTextLeft(name, PLANET_EDITOR_COLUMN1+5, line, 
				!screenContentPlanetEditor.isReadOnly() && screenContentPlanetEditor.getTypeHighlighted() == type ?
						Color.white :
						Colors.get(Colors.NEUTRAL));
		
		if (!screenContentPlanetEditor.isReadOnly() && screenContentPlanetEditor.getTypeHighlighted() == type)
			this.drawPlanetEditorTextLeft(">>>>", 0, line, Color.white);

		if (type == ShipType.BATTLESHIP_PRODUCTION)
			return;
		
		byte colorIndex = screenContentPlanetEditor.getColorIndex();
		if (screenContentPlanetEditor.getBuyImpossible().contains(type))
			colorIndex = Colors.NEUTRAL;
				
		this.drawPlanetEditorTextLeft(
				CommonUtils.padString(
						screenContentPlanetEditor.getPriceBuy(type),
						2), 
				PLANET_EDITOR_COLUMN2 + 4, line, Colors.get(colorIndex));
		this.drawPlanetEditorTextLeft(VegaResources.Money(false), PLANET_EDITOR_COLUMN2+7, line, Colors.get(colorIndex));
		
		this.drawPlanetEditorTextLeft(this.getPriceRangeString(type, false), PLANET_EDITOR_COLUMN2+9, line, Colors.get(Colors.NEUTRAL));
		
		if (type == ShipType.MONEY_PRODUCTION ||
			type == ShipType.BONUS)
			return;
		
		colorIndex = screenContentPlanetEditor.getColorIndex();
		if (screenContentPlanetEditor.getSellImpossible().contains(type))
			colorIndex = Colors.NEUTRAL;
		
		this.drawPlanetEditorTextLeft(
				CommonUtils.padString(
						screenContentPlanetEditor.getPriceSell(type),
						2), 
				PLANET_EDITOR_COLUMN3 + 4, line, Colors.get(colorIndex));
		this.drawPlanetEditorTextLeft(VegaResources.Money(false), PLANET_EDITOR_COLUMN3+7, line, Colors.get(colorIndex));
		
		this.drawPlanetEditorTextLeft(this.getPriceRangeString(type, true), PLANET_EDITOR_COLUMN3+9, line, Colors.get(Colors.NEUTRAL));
		
	}
		
	private void drawPlanetEditorTextCentered(String text, int columnCenter, int line, Color color)
	{
		int charWidth = this.fmPlanets.charWidth('H');
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		int column = columnCenter - text.length() / 2;
		
		this.dbGraphics.setColor(color);
		
		this.dbGraphics.drawString(
				text,
				CommonUtils.round((double)charWidth + this.factor * (double)BOARD_OFFSET_X + (double)(column* charWidth)),
				CommonUtils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)line+0.5) * (double)BOARD_DX)));
	}
	
	private void drawPlanetEditorTextLeft(String text, int column, int line, Color color)
	{
		int charWidth = this.fmPlanets.charWidth('H');
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		this.dbGraphics.setColor(color);
		
		this.dbGraphics.drawString(
				text,
				CommonUtils.round((double)charWidth + this.factor * (double)BOARD_OFFSET_X + (double)(column* charWidth)),
				CommonUtils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)line+0.5) * (double)BOARD_DX)));
	}
	
	private void drawPlanetList()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentPlanets screenContentPlanets = this.screenContent.getPlanets();
		if (screenContentPlanets == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		
		this.drawRect(PLANETLIST_OFFSET_X, PLANETLIST_OFFSET_Y, PLANETLIST_WIDTH, PLANETLIST_HEIGHT);
		
		this.dbGraphics.drawLine(
				CommonUtils.round(PLANETLIST_OFFSET_X * this.factor),
				CommonUtils.round((PLANETLIST_OFFSET_Y + BOARD_DX) * this.factor),
				CommonUtils.round((PLANETLIST_OFFSET_X + PLANETLIST_WIDTH) * this.factor),
				CommonUtils.round((PLANETLIST_OFFSET_Y + BOARD_DX) * this.factor));
		
		this.dbGraphics.setFont(this.fontPlanets);
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		if (screenContentPlanets.getTitle() != null && screenContentPlanets.getTitle().length() > 0)
		{
			String text = VegaResources.getString(screenContentPlanets.getTitle());
			
			if (screenContentPlanets.isToggleContentsEnabled())
			{
				text = "\u2190" + " " + text + " " + "\u2192";
			}
			
			this.setColor(Colors.get(screenContentPlanets.getTitleColor()));
			
			int width = this.fmPlanets.stringWidth(text);
			
			int x = CommonUtils.round(this.factor * (PLANETLIST_OFFSET_X + PLANETLIST_WIDTH / 2) - width/2);
			int y = CommonUtils.round(this.factor * ((double)BOARD_OFFSET_Y + 0.5 * (double)BOARD_DX) + (double)height/2.);
			
			this.dbGraphics.drawString(text, x, y);
		}

		int lineCounter = 0;

		if (screenContentPlanets.getColoredList() != null)
		{
			ScreenContentPlanetsColoredList list = screenContentPlanets.getColoredList();
			
			int characterWidth = this.fmPlanets.stringWidth("H");
			
			int x00 = CommonUtils.round((double)PLANETLIST_OFFSET_X * factor);
			
			int x0 = x00+ 3 * characterWidth;
			int y0 = CommonUtils.round(this.factor * ((double)BOARD_OFFSET_Y + ((double)lineCounter+1.5) * (double)BOARD_DX) + (double)height/2.);
			
			int xScrollUpIndicator = x00 + characterWidth;
			int yScrollUpIndicator = y0;
			int yScrollDownIndicator = CommonUtils.round(this.factor * ((double)BOARD_OFFSET_Y + ((double)(BOARD_DX-2)+1.5) * (double)BOARD_DX) + (double)height/2.);
			
			ArrayList<ScreenContentPlanetsColoredListHeaderColumn> headers = list.getHeaders();
			this.setColor(Colors.get(list.getHeadersColorIndex()));

			for (int column = 0; column < headers.size(); column++)
			{
				ScreenContentPlanetsColoredListHeaderColumn header = headers.get(column);
				String textPadded = header.isRightAlign() ?
						CommonUtils.padString(
								VegaResources.getString(header.getText()), 
								header.getTextLength()) :
						VegaResources.getString(header.getText());
				
				this.dbGraphics.drawString(textPadded, x0, y0);
				
				x0 += (header.getTextLength() +1 ) * characterWidth;
			}
			
			lineCounter++;
			
			for (ScreenContentPlanetsColoredListCellValue[] values: list.getLines())
			{
				x0 = x00 + 3 * characterWidth;
				y0 = CommonUtils.round(this.factor * ((double)BOARD_OFFSET_Y + ((double)lineCounter+1.5) * (double)BOARD_DX) + (double)height/2.);
				
				for (int column = 0; column < headers.size(); column++)
				{
					ScreenContentPlanetsColoredListHeaderColumn header = headers.get(column);
					ScreenContentPlanetsColoredListCellValue value = values[column];
					
					if (value != null)
					{
						if (header.isSymbol())
						{
							this.drawShipSymbol(
									new Point(x0 + 0.5 * characterWidth, y0 - 0.5 * height),
									characterWidth / 2,
									value.getColorIndex(),
									Byte.parseByte(value.getText()));
						}
						else
						{
							this.setColor(Colors.get(value.getColorIndex()));

							String textPadded = header.isRightAlign() ?
									CommonUtils.padString(
											VegaResources.getString(value.getText()), 
											header.getTextLength()) :
									VegaResources.getString(value.getText());
							
							this.dbGraphics.drawString(textPadded, x0, y0);
						}
					}
					
					x0 += (header.getTextLength() +1 ) * characterWidth;
				}
				
				lineCounter++;
			}
			
			if (list.isScrollUpIndicator())
			{
				this.setColor(Colors.get(list.getHeadersColorIndex()));
				this.dbGraphics.drawString("\u2191", xScrollUpIndicator, yScrollUpIndicator);
			}
			
			if (list.isScrollDownIndicator())
			{
				this.setColor(Colors.get(list.getHeadersColorIndex()));
				this.dbGraphics.drawString("\u2193", xScrollUpIndicator, yScrollDownIndicator);
			}
		}
		else
		{
			int counter = 0;
			byte lastColorIndex = -1;
	
			for (String text: screenContentPlanets.getText())
			{
				int width = this.fmPlanets.stringWidth(text);
				
				byte colorIndex = screenContentPlanets.getTextColorIndices().get(counter);
				this.setColor(Colors.get(colorIndex));
				
				int line = lineCounter % (Game.BOARD_MAX_Y - 1);
				int column = lineCounter / (Game.BOARD_MAX_Y - 1);
				
				if (counter > 0 && line == Game.BOARD_MAX_Y-2 && colorIndex != lastColorIndex)
				{
					lineCounter++;
					line = lineCounter % (Game.BOARD_MAX_Y - 1);
					column = lineCounter / (Game.BOARD_MAX_Y - 1);
				}
				
				int dx = (column-1) * PLANETLIST_AXIS_DISTANCE + PLANETLIST_AXIS_X;
				
				int x = CommonUtils.round(this.factor * (double)dx - (double)width/2.);
				int y = CommonUtils.round(this.factor * ((double)BOARD_OFFSET_Y + ((double)line+1.5) * (double)BOARD_DX) + (double)height/2.);
				
				this.dbGraphics.drawString(text, x, y);
				
				counter++;
				lineCounter++;
				
				lastColorIndex = colorIndex;
			}
		}
	}
	
	private void drawRect(int x, int y, int width, int height)
	{
		this.dbGraphics.drawRect(
				   CommonUtils.round((double)x * factor),
				   CommonUtils.round((double)y * factor),
				   CommonUtils.round((double)width * this.factor),
				   CommonUtils.round((double)height * this.factor));
	}
	
	private void drawShipSymbol(Point positionScreen, double sizeScreen, byte colorIndex, byte symbol)
	{
		this.setColor(Colors.get(colorIndex));
		
		switch (symbol)
		{
		case 1: // Battleship
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen));
			break;
		case 2: // Spy
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y,
					positionScreen.x + sizeScreen,
					positionScreen.y));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x,
					positionScreen.y + sizeScreen,
					positionScreen.x,
					positionScreen.y - sizeScreen));
			break;
		case 3: // Patrol
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y,
					positionScreen.x + sizeScreen,
					positionScreen.y));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x,
					positionScreen.y + sizeScreen,
					positionScreen.x,
					positionScreen.y - sizeScreen));
			break;
		case 4: // Transport
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen));
			break;
					
		case 5: // Minelayer
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y));
			
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x + sizeScreen,
					positionScreen.y,
					positionScreen.x,
					positionScreen.y + sizeScreen));
			
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x,
					positionScreen.y + sizeScreen,
					positionScreen.x - sizeScreen,
					positionScreen.y));
			
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y,
					positionScreen.x,
					positionScreen.y - sizeScreen));
			
			break;
		case 6: // Minesweeper
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y - sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y + sizeScreen));
			this.dbGraphics.draw(new Line2D.Double(
					positionScreen.x - sizeScreen,
					positionScreen.y + sizeScreen,
					positionScreen.x + sizeScreen,
					positionScreen.y - sizeScreen));

			break;
		case 7: // Black Hole
			this.setColor(Color.black);
			this.dbGraphics.fill(
					new Ellipse2D.Double(
							positionScreen.x - sizeScreen,
							positionScreen.y - sizeScreen,
							sizeScreen * 2,
							sizeScreen * 2));
			
			this.setColor(Colors.get(colorIndex));
			this.dbGraphics.draw(
					new Ellipse2D.Double(
							positionScreen.x - sizeScreen,
							positionScreen.y - sizeScreen,
							sizeScreen * 2,
							sizeScreen * 2));
		}
	}
	
	private void drawStatistics()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentStatistics screenContentStatistics = this.screenContent.getStatistics();
		if (screenContentStatistics == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		this.drawRect(BOARD_OFFSET_X, BOARD_OFFSET_Y, SCREEN_WIDTH - 2 * BOARD_OFFSET_X, Game.BOARD_MAX_Y * BOARD_DX);
		
		String title = "";
		String yearString = Integer.toString(screenContentStatistics.getSelectedYearIndex() + 1);
		
		switch (screenContentStatistics.getMode())
		{
		case SCORE:
			title = VegaResources.PointsInYear(
					false,
					yearString);
			break;
			
		case BATTLESHIPS:
			title = VegaResources.BattleshipsInYear(
					false,
					yearString);
			break;
			
		case PLANETS:
			title = VegaResources.PlanetsInYear(
					false,
					yearString);
			break;
			
		case PRODUCTION:
			title = VegaResources.MoneyProductionInYear(
					false,
					yearString);
			break;
		}

		
		this.drawPlanetEditorTextLeft(
				title,
				0,
				0,
				Color.white);
		
		int valuePerPlayer[] = screenContentStatistics.getValues()[screenContentStatistics.getSelectedYearIndex()];
		int playerListSequence[] = CommonUtils.sortValues(valuePerPlayer, true);
		
		for (int i = 0; i < playerListSequence.length; i++)
		{
			int playerIndex = playerListSequence[i];
			Player player = screenContentStatistics.getPlayers()[playerIndex];
		
			this.drawPlanetEditorTextLeft(screenContentStatistics.getPlayers()[playerIndex].getName(), 0, 2 + i, Colors.get(player.getColorIndex()));
			
			this.drawPlanetEditorTextLeft(CommonUtils.padString(Integer.toString(valuePerPlayer[playerIndex]), 7), 14, 2 + i, Colors.get(player.getColorIndex()));
		}
		
		this.drawPlanetEditorTextLeft(VegaResources.GameStartedOn(false), 0, 16, Color.white);
		
		this.drawPlanetEditorTextLeft(CommonUtils.convertDateToString(screenContentStatistics.getDateStart()), 0, 17, Color.white);
		
		this.drawPlanetEditorTextLeft(
				VegaResources.MaxInYear(
						false, 
						Integer.toString(screenContentStatistics.getValueMax()), 
						Integer.toString(screenContentStatistics.getValueMaxYear()+1)),
				25,
				0,
				Colors.get(screenContentStatistics.getPlayers()[screenContentStatistics.getMaxValuePlayerIndex()].getColorIndex()));
		
		this.drawPlanetEditorTextLeft(
				VegaResources.MinInYear(false,
						Integer.toString(screenContentStatistics.getValueMin()),
						Integer.toString(screenContentStatistics.getValueMinYear()+1)),
				50,
				0,
				Colors.get(screenContentStatistics.getPlayers()[screenContentStatistics.getMinValuePlayerIndex()].getColorIndex()));
		
		int charWidth = this.fmPlanets.charWidth('H');
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		int columnGraphic = 25;
		int columnGraphic0 = 1;
		int columnGraphic1 = 17;
		
		int x0 = CommonUtils.round((double)charWidth + this.factor * (double)BOARD_OFFSET_X + (double)(columnGraphic* charWidth));
		int y0 = CommonUtils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)columnGraphic0+0.5) * (double)BOARD_DX));
		
		int frameY = CommonUtils.round((double)(Game.BOARD_MAX_Y * BOARD_DX) * this.factor);
		int y1 = CommonUtils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)columnGraphic1+0.5) * (double)BOARD_DX));
		
		int x1 = CommonUtils.round((double)(SCREEN_WIDTH - BOARD_OFFSET_X) * this.factor + (frameY - y1));
		
		y1 -= 2 * height;
		
		this.setColor(Color.gray);
		this.dbGraphics.drawLine(x0, y0, x0, y1);
		this.dbGraphics.drawLine(x0, y1, x1, y1);
		this.dbGraphics.drawLine(x1, y0, x1, y1);
		
		int yMax = y0 + height;
		
		Graphics2D g2d = (Graphics2D) this.dbGraphics.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{12}, 0);
        g2d.setStroke(dashed);		
        g2d.drawLine(
        		x0,
        		this.getStatisticsY(screenContentStatistics.getValueMax(), screenContentStatistics.getValueMax(), yMax, y1),
        		x1, this.getStatisticsY(screenContentStatistics.getValueMax(), screenContentStatistics.getValueMax(), yMax, y1));
        
        g2d.drawLine(
        		x0,
        		this.getStatisticsY(screenContentStatistics.getValueMin(), screenContentStatistics.getValueMax(), yMax, y1),
        		x1, this.getStatisticsY(screenContentStatistics.getValueMin(), screenContentStatistics.getValueMax(), yMax, y1));
        
		g2d.dispose();
		
		double dx = (double)(x1 - x0) / (double)(screenContentStatistics.getYears() - 1);
		
		for (int year = 0; year < screenContentStatistics.getYears(); year++)
		{
			int xx0 = x0 + CommonUtils.round((double)year * dx);
			int xx1 = x0 + CommonUtils.round((double)(year+1) * dx);
			
			int x0champ = Math.max(x0, xx0 - CommonUtils.round(dx / 2.));
			
			int champsCount = screenContentStatistics.getChampionsPerYear()[year].length;
			int dy = CommonUtils.round((2 * height -1 ) / champsCount);
			
			for (int champ = 0; champ < champsCount; champ++)
			{
				int colChamp = screenContentStatistics.getChampionsPerYear()[year][champ];
				
				this.dbGraphics.setColor(
						Colors.get(screenContentStatistics.getPlayers()[colChamp].getColorIndex()));
				
				int y = y1+1 + (champ * dy);
				int h = (y1 + 2 * height - 1) - y;
								
				this.dbGraphics.fillRect(
						x0champ, 
						y, 
						(year < screenContentStatistics.getYears() - 1) ? xx1-xx0 : CommonUtils.round(dx / 2.), 
						h);
			}
			
			if (year < screenContentStatistics.getYears() - 1)
			{
				for (int playerIndex = 0; playerIndex < playerListSequence.length; playerIndex++)
				{
					this.dbGraphics.setColor(Colors.get(screenContentStatistics.getPlayers()[playerIndex].getColorIndex()));
					
					this.dbGraphics.drawLine(
							xx0,
							this.getStatisticsY(screenContentStatistics.getValues()[year][playerIndex], screenContentStatistics.getValueMax(), yMax, y1),
							xx1,
							this.getStatisticsY(screenContentStatistics.getValues()[year+1][playerIndex], screenContentStatistics.getValueMax(), yMax, y1));
				}
			}
		}
		
		this.dbGraphics.setColor(Color.white);
		int xx0 = x0 + CommonUtils.round((double)screenContentStatistics.getSelectedYearIndex() * dx);
		this.dbGraphics.drawLine(xx0, y0, xx0, y1 + 2 * height - 1);		
	}
	
	private void drawTitle()
	{
		this.dbGraphics.setFont(this.fontPlanets);
		this.dbGraphics.setColor(Colors.get((byte)8));
		
		int lineHeight = this.fmPlanets.getHeight();
		
		int maxLineWidth = 0;
		
		for (String line: titleLinesCount)
		{
			int lineWidth = this.fmPlanets.stringWidth(line);
			if (lineWidth > maxLineWidth)
				maxLineWidth = lineWidth;
		}
		
		int maxTotalHeight = titleLinesCount.size() * lineHeight;
		
		int x = CommonUtils.round(((double)SCREEN_WIDTH * this.factor - (double)maxLineWidth) / 2.);
		int yOff = CommonUtils.round(((double)(SCREEN_HEIGHT * this.factor) - (double)maxTotalHeight) / 2.) +
				this.fmPlanets.getAscent(); 
		
		for (int i = 0; i < titleLinesCount.size(); i++)
		{
			String line = titleLinesCount.get(i);
			this.dbGraphics.drawString(line, x, yOff + i * lineHeight);
		}		
	}
	
	private void fillRect(int x, int y, int width, int height)
	{
		this.dbGraphics.fillRect(
				   CommonUtils.round((double)x * factor),
				   CommonUtils.round((double)y * factor),
				   CommonUtils.round((double)width * this.factor),
				   CommonUtils.round((double)height * this.factor));
	}
	
	private String getPriceRangeString(ShipType type, boolean sell)
	{
		int minVal = sell ?
				CommonUtils.round(Planet.PRICES_MIN_MAX.get(type).getMin() * Planet.PRICE_RATIO_BUY_SELL) :
				CommonUtils.round(Planet.PRICES_MIN_MAX.get(type).getMin());
		
		int maxVal = sell ?
				CommonUtils.round(Planet.PRICES_MIN_MAX.get(type).getMax() * Planet.PRICE_RATIO_BUY_SELL) :
				CommonUtils.round(Planet.PRICES_MIN_MAX.get(type).getMax());
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append(CommonUtils.padString(Integer.toString(minVal), 2));
		sb.append("-");
		sb.append(CommonUtils.padString(Integer.toString(maxVal), 2));
		sb.append(")");
		
		return sb.toString();
	}
	
	private Point getScreenPositionFromBoardPosition(Point boardPosition)
	{
		return new Point(
		        ((double)BOARD_OFFSET_X + (boardPosition.x + 0.5) * (double)BOARD_DX) * this.factor,
				((double)BOARD_OFFSET_Y + (boardPosition.y + 0.5)* (double)BOARD_DX) * this.factor);
	}
	
	private int getStatisticsY(int value, int valueMax, int yMax, int y1)
	{
		double ratio = (double)value / (double)valueMax;
		return CommonUtils.round((double)y1 - ratio * (double)(y1 - yMax));
	}
	
	private void setColor(Color color)
	{
		this.dbGraphics.setColor(color);
	}
	
	private void writeConsoleKey(ConsoleKey key, int xKey, int xText, int maxKeyLength, int y0, int line, int charWidth, int fontHeight)
	{
		if (key == null)
			return;
		
		this.setColor(Colors.get(Colors.NEUTRAL));
		
		this.dbGraphics.fillRect(
				xKey + (maxKeyLength - key.getKey().length()) * charWidth,
				CommonUtils.round(this.factor * (double)(y0 + (CONSOLE_LINES_COUNT-1-line) * CONSOLE_LINE_HEIGHT)),
				key.getKey().length() * charWidth,
				CommonUtils.round((double)CONSOLE_LINE_HEIGHT * this.factor));
		
		this.setColor(Color.black);
		this.dbGraphics.drawString(CommonUtils.padString(key.getKey(), maxKeyLength), xKey, this.consoleGetY(CONSOLE_LINES_COUNT-1-line, y0, fontHeight));
		
		this.setColor(Colors.get(Colors.NEUTRAL));
		this.dbGraphics.drawString(
				key.getText(),
				xText,
				this.consoleGetY(CONSOLE_LINES_COUNT-1-line, y0, fontHeight));
	}
}
