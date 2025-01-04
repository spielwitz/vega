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

import java.util.Comparator;

public class HighscoreEntry implements Comparator<HighscoreEntry> 
{
	private int position;
	
	private String playerName;
	
	private int score;
	
	private long dateAdded;

	HighscoreEntry() {}
	
	HighscoreEntry(String playerName, int score, long dateAdded)
	{
		super();
		this.playerName = playerName;
		this.score = score;
		this.dateAdded = dateAdded;
	}

	@Override
	public int compare(HighscoreEntry o1, HighscoreEntry o2)
	{
		if (o1.score == o2.score)
		{
			if (o1.dateAdded < o2.dateAdded)
				return 1;
			else if (o1.dateAdded > o2.dateAdded)
				return -1;
			else
				return 0;
		}
		else if (o1.score > o2.score)
			return -1;
		else
			return 1;
	}

	public long getDateAdded()
	{
		return dateAdded;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public int getPosition()
	{
		return position;
	}

	public int getScore()
	{
		return score;
	}

	void setPosition(int position)
	{
		this.position = position;
	}
}
