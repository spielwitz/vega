/**	VEGA - a strategy game
    Copyright (C) 1989-2024 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General License for more details.

    You should have received a copy of the GNU Affero General License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package common;

class ConsoleInput
{
	private String inputText;
	private int lastKeyCode;
	private String clientId;
	private String languageCode;
	
	ConsoleInput(String inputText, int lastKeyCode, String clientId, String languageCode)
	{
		super();
		this.inputText = inputText;
		this.lastKeyCode = lastKeyCode;
		this.clientId = clientId; 
		this.languageCode = languageCode;
	}
	String getClientId() {
		return clientId;
	}
	String getInputText() {
		return inputText;
	}
	String getLanguageCode() {
		return languageCode;
	}
	int getLastKeyCode() {
		return lastKeyCode;
	}
}
