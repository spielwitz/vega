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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;

public class Highscores
{
	public static final int MAX_ENTRIES_COUNT = 20;
	private static Object lockObject = new Object();
	private static String rootFolder = "";
	private static final String FILE_NAME = "VegaHighscores";
	
	public static Highscores getInstance()
	{
		synchronized(lockObject)
		{
			File file = new File(getFilePath());
			if (file.exists())
			{
				try (BufferedReader br = new BufferedReader(new FileReader(file)))
				{
					String jsonString = br.readLine();
					Highscores highscores = new Gson().fromJson(jsonString, Highscores.class);
					return highscores;
				} catch (Exception e)
				{
					file.delete();
					return new Highscores();
				}
			}
			else
				return new Highscores();
		}
	}
	
	public static void setRootFolder(String folderName)
	{
		rootFolder = folderName;
	}
	
	private static String getFilePath()
	{
		return 
				rootFolder.equals("") ?
						Paths.get(CommonUtils.getHomeDir(), FILE_NAME).toString() :
						Paths.get(rootFolder, FILE_NAME).toString();
	}
	
	private ArrayList<HighscoreEntry> entries;
	
	private Highscores()
	{
		this.entries = new ArrayList<HighscoreEntry>();
	}
	
	public void deletePlayer(String playerName)
	{
		synchronized(lockObject)
		{
			for (int i = this.entries.size()-1; i >= 0; i--)
			{
				HighscoreEntry entry = this.entries.get(i);
				
				if (entry.getPlayerName().equals(playerName))
				{
					this.entries.remove(i);
				}
			}
			
			this.update();
		}
	}
	
	public ArrayList<HighscoreEntry> getEntries()
	{
		synchronized(lockObject)
		{
			return this.entries;
		}
	}
	
	void add(Archive archive, Player[] players)
	{
		synchronized(lockObject)
		{
			long dateAdded = System.currentTimeMillis();
			
			for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
			{
				if (archive.getScore()[playerIndex] > 0)
				{
					this.entries.add(
							new HighscoreEntry(
									players[playerIndex].getName(),
									archive.getScore()[playerIndex],
									dateAdded));
				}				
			}
			
			this.update();
		}
	}
	
	private void toFile()
	{
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFilePath())))
		{
			bw.write(new Gson().toJson(this));			
		} catch (IOException e)
		{
		}
	}
	
	private void update()
	{
		Collections.sort(this.entries, new HighscoreEntry());
		
		for (int i = this.entries.size() - 1; i >= MAX_ENTRIES_COUNT; i--)
			this.entries.remove(i);
		
		for (int i = 0; i < this.entries.size(); i++)
		{
			HighscoreEntry entry = this.entries.get(i);
			int position = i +1;
			
			if (i > 0)
			{
				if (entry.getScore() == this.entries.get(i-1).getScore())
					position = this.entries.get(i-1).getPosition();
			}
			
			entry.setPosition(position);
		}
		
		this.toFile();

	}
}
