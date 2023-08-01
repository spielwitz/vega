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

package vega;

class Message
{
	private String sender;
	private long dateCreated;
	private String text;
	
	Message(String sender, long dateCreated, String text)
	{
		super();
		this.sender = sender;
		this.dateCreated = dateCreated;
		this.text = text;
	}

	long getDateCreated() {
		return dateCreated;
	}

	String getSender() {
		return sender;
	}

	String getText() {
		return text;
	}
}
