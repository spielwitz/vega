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

package vega;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import common.Game;
import common.Player;
import common.ScreenContent;
import common.ScreenPainter;
import common.VegaResources;
import commonUi.CommonUiUtils;
import commonUi.FontHelper;
import commonUi.PanelScreenContent;
import common.CommonUtils;

class WebServer implements Runnable
{
	static final int PORT = 8090;
	private static final double FACTOR = 1.5;
	private static final String DISTANCE_MATRIX_URL = "dm";
	static final String MANUAL_URL = "manual";
	private static String[] getWebInventoryKeys(int playersCount)
	{
		String[] keys = new String[playersCount];
		DecimalFormat df = new DecimalFormat("000000");
		
		for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
		{
			boolean ok = false;
			
			while(!ok)
			{
				keys[playerIndex] = df.format(CommonUtils.getRandomInteger(1000000));
				ok = true;
				
				for (int playerIndex2 = 0; playerIndex2 < playerIndex; playerIndex2++)
				{
					if (keys[playerIndex].equals(keys[playerIndex2]))
					{
						ok = false;
						break;
					}
				}
			}
		}
		
		return keys;
	}
	
	private Vega callback;
	private Thread t;
	
	private ServerSocket serverSocket;
	private boolean serverSocketClosed;
	private UUID gameId;
	private String[] webInventoryKeys;
	
	private int port;
	
	WebServer(Vega callback)
	{
		this.callback = callback;
		this.webInventoryKeys = null;
	}
	
	@Override
	public void run()
	{
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
			
		do
		{
			Socket socket = null;
			BufferedReader in = null;
			PrintWriter out = null;
			BufferedOutputStream dataOut = null;
			
			try {
				socket = serverSocket.accept();
				socket.setSoTimeout(1000);

				synchronized(this)
				{
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream());
					dataOut = new BufferedOutputStream(socket.getOutputStream());
					
					StringBuilder requestSb = new StringBuilder();
					int b;
					
					try
					{
						while ((b = in.read()) != -1)
						{
							requestSb.append((char)b);
						}
					}
					catch (Exception xx) {}
					
					Request request = new Request(requestSb.toString());
					
					byte[] responseBytes = null;
					String contentType = "";
					
					if (request.arg != null)
					{
						if (request.arg.equals(""))
						{
							responseBytes = this.createPicture();
							contentType = "text/plain";
						}
						else if (!request.arg.equals("favicon.ico"))
						{
							if (request.arg.equals(DISTANCE_MATRIX_URL))
							{
								responseBytes = this.callback.getInventoryPdfBytes(-1);
								contentType = "application/pdf";
							}
							else if (request.arg.equals(MANUAL_URL))
							{
								try
								{
									responseBytes = Files.readAllBytes(
											Path.of(CommonUiUtils.getManualFileName()));
								} catch (IOException e)
								{
								}
								
								contentType = "application/pdf";
							} 
							else
							{
								String[] webInventoryKeys = this.getWebInventoryKeys();
								
								if (webInventoryKeys != null && request.arg != null)
								{
									String key = request.arg;
									
									int playerIndex = Player.NEUTRAL;
									
									for (int i = 0; i < webInventoryKeys.length; i++)
									{
										if (webInventoryKeys[i] != null &&
										    webInventoryKeys[i].equals(key))
										{
											playerIndex = i;
											break;
										}
									}
									
									if (playerIndex != Player.NEUTRAL)
									{
										responseBytes = this.callback.getInventoryPdfBytes(playerIndex);
										contentType = "application/pdf";
									}
								}
							}
						}
					}
					
					if (responseBytes == null)
					{
						responseBytes = VegaResources.WebServerResourceNotFound(false, request.arg).getBytes();
						contentType = "text/plain";
					}
					
					out.println("HTTP/1.1 200 OK");
					out.println("Server: VEGA HTTP Server");
					out.println("Date: " + new Date());
					out.println("Content-type: " + contentType);   
					out.println("Content-length: " + responseBytes.length);
					out.println();
					out.flush();
					
					dataOut.write(responseBytes, 0, responseBytes.length);
					dataOut.flush();
				}
				
			} catch (Exception e)
			{
				if (this.serverSocketClosed)
				{
					break;
				}
				e.printStackTrace();
			}
			finally {
				try {
					if (in != null) in.close();
					if (out != null) out.close();
					if (dataOut != null) dataOut.close();
					if (socket != null) socket.close();
				} catch (Exception e) {
					e.printStackTrace();
			} 
		}
			
			
		} while (true);
	}
	
	String getDistanceMatrixKey()
	{
		if (this.callback.getGame() != null)
			return DISTANCE_MATRIX_URL;
		else
			return null;
	}
	
	String[] getWebInventoryKeys()
	{
		Game game = this.callback.getGame();
		
		if (game == null ||
			game.isSoloPlayer())
		{
			this.gameId = null;
			this.webInventoryKeys = null;
			return null;
		}
		
		if (!game.getId().equals(this.gameId))
		{
			this.gameId = game.getId();
			this.webInventoryKeys = getWebInventoryKeys(this.callback.getGame().getPlayersCount());
		}
		
		String[] webInventoryKeysCopy = new String[this.webInventoryKeys.length];
			
		for (int playerIndex = 0; playerIndex < this.callback.getGame().getPlayersCount(); playerIndex++)
		{
			Player player = this.callback.getGame().getPlayers()[playerIndex];
			
			if (game.isPlayerEmail(playerIndex)  ||
				player.isDead())
			{
				webInventoryKeysCopy[playerIndex] = null;
			}
			else
			{
				webInventoryKeysCopy[playerIndex] = this.webInventoryKeys[playerIndex];
			}
		}
		
		return webInventoryKeysCopy;
	}

	void start(int port)
	{
		if (this.t != null && this.t.isAlive())
		{
			return;
		}
		
		this.port = port;
		
		this.t = new Thread(this);
		t.start();
	}
	
	void stop()
	{
		if (t != null && serverSocket != null)
		{
			this.serverSocketClosed = true;
			
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	private byte[] createPicture()
	{
		byte[] bytes = null;

		ScreenContent screenContent = this.callback.getScreenContentStartOfYear();
		
		int width = CommonUtils.round((double)ScreenPainter.SCREEN_WIDTH * FACTOR);
		int height =
				screenContent == null ?
						CommonUtils.round((double)ScreenPainter.SCREEN_HEIGHT * FACTOR) :
						(int)((double)(Game.BOARD_MAX_Y * ScreenPainter.BOARD_DX+ 2 * PanelScreenContent.BORDER_SIZE) * FACTOR);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)image.createGraphics();
		
		g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        Font fontPlanets = FontHelper.getFont((float)CommonUtils.round((double)PanelScreenContent.FONT_SIZE_PLANETS * FACTOR));
        Font fontMines =   FontHelper.getFont((float)CommonUtils.round((double)PanelScreenContent.FONT_SIZE_MINES * FACTOR));
        Font fontSectors = FontHelper.getFont((float)CommonUtils.round((double)PanelScreenContent.FONT_SIZE_SECTORS * FACTOR));
        
        new ScreenPainter(screenContent, true, g2d, fontPlanets, fontMines, fontSectors, FACTOR);
        
        g2d.dispose();
				
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(image, "png", bos);
			bytes = bos.toByteArray();
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return bytes;
	}

	
	private class Request
	{
		String arg;
		
		Request(String requestString) throws Exception
		{
			if (requestString.equals(""))
			{
				return;
			}
			
			this.arg = "";
			ArrayList<String> requestHeadersStrings = new ArrayList<String>(Arrays.asList(requestString.split("\r\n")));
			
			String[] args = requestHeadersStrings.get(0).split(" ");
			
			this.arg = args[1].trim();
			
			if (this.arg.equals("/"))
			{
				this.arg = "";
				return;
			}
			else if (this.arg.startsWith("/"))
			{
				this.arg = this.arg.substring(1);
			}
		}
	}
}
