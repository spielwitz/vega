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

package commonServer;

import java.util.ArrayList;
import java.util.Hashtable;

import common.GameInfo;

public class ResponseMessageGamesAndUsers
{
	private String 						emailGameHost;
	private Hashtable<String, String> 	users;
	private ArrayList<GameInfo> 		games;

	public ResponseMessageGamesAndUsers(
			String emailGameHost, 
			Hashtable<String, String> users,
			ArrayList<GameInfo> games)
	{
		super();
		this.emailGameHost = emailGameHost;
		this.users = users;
		this.games = games;
	}

	public String getEmailGameHost()
	{
		return emailGameHost;
	}

	public ArrayList<GameInfo> getGames()
	{
		return games;
	}

	public Hashtable<String, String> getUsers()
	{
		return users;
	}
}
