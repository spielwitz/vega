/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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

import java.io.Serializable;

@SuppressWarnings("serial") 
class ConsoleKey implements Serializable
{
	static int getMaxKeyLength(ConsoleKey key1, ConsoleKey key2)
	{
		int len1 = 0;
		int len2 = 0;
		
		if (key1 != null && !key1.key.isEmpty())
			len1 = key1.getKey().length();
		if (key2 != null && !key2.key.isEmpty())
			len2 = key2.getKey().length();
		
		return Math.max(len1, len2);
	}
	static int getMaxTextLength(ConsoleKey key1, ConsoleKey key2)
	{
		int len1 = 0;
		int len2 = 0;
		
		if (key1 != null && !key1.text.isEmpty())
			len1 = key1.getText().length();
		if (key2 != null && !key2.text.isEmpty())
			len2 = key2.getText().length();
		
		return Math.max(len1, len2);
	}
	
	private String key;
	private String text;
	ConsoleKey(String key, String text) {
		super();
		this.key = key;
		this.text = text;
	}
	
	String getKey() {
		return VegaResources.getString(key);
	}
	
	String getText() {
		return VegaResources.getString(text);
	}
}
