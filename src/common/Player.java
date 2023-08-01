/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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

@SuppressWarnings("serial")
public class Player implements Serializable
{
	public static final int NEUTRAL = -1;
	public static final int PLAYER_NAME_LENGTH_MIN = 3;
	public static final int PLAYER_NAME_LENGTH_MAX = 10;
	public static final String PLAYER_NAME_REGEX_PATTERN = "[0-9a-zA-Z]*";
	static final String 	   PLAYER_DELETED_NAME = "_DELETED_";

	private String name;
	private String email;
	private byte colorIndex;
	private boolean emailPlayer;
	private boolean dead;
	
	public Player(
			String name, 
			String email, 
			byte colorIndex,
			boolean emailPlayer)
	{
		super();
		this.name = name;
		this.email = email;
		this.colorIndex = colorIndex;
		this.emailPlayer = emailPlayer;
	}

	public byte getColorIndex() {
		return colorIndex;
	}
	
	public String getEmail() {
		return this.email;
	}

	public String getName() {
		return name;
	}
	
	public boolean isDead()
	{
		return this.dead;
	}
	
	public boolean isEmailPlayer()
	{
		return this.emailPlayer;
	}
	
	public void setColorIndex(byte colorIndex)
	{
		this.colorIndex = colorIndex;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setEmailPlayer(boolean emailPlayer)
	{
		this.emailPlayer = emailPlayer;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	void setDead(boolean dead)
	{
		this.dead = dead;
	}
}
