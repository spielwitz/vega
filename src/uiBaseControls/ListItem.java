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

package uiBaseControls;

import java.util.ArrayList;
import java.util.Comparator;

public class ListItem implements Comparator<ListItem> 
{
	public static void renameDuplicateDisplayStrings(ArrayList<ListItem> items)
	{
		ArrayList<Integer> renamedIndices = new ArrayList<Integer>();
		
		for (int i = 0; i < items.size() - 1; i++)
		{
			if (renamedIndices.contains(i)) continue;
			
			ListItem item = items.get(i);
			int counter = 2;
			
			for (int j = i+1; j < items.size(); j++)
			{
				if (renamedIndices.contains(j)) continue;
				ListItem item2 = items.get(j);
				
				if (item.displayString.equals(item2.displayString))
				{
					item2.displayString = item2.displayString + " (" + counter + ")";
					renamedIndices.add(j);
					counter++;
				}
			}
			
			if (counter > 2)
			{
				item.displayString = item.displayString + " (1)";
			}
		}
	}
	
	private String displayString;
	private Object handle;
	
	public ListItem() {}
	
	public ListItem(String displayString, Object handle)
	{
		this.displayString = displayString;
		this.handle = handle;
	}

	@Override
	public int compare(ListItem o1, ListItem o2)
	{
		return o1.getDisplayString().compareTo(o2.getDisplayString());
	}

	public String getDisplayString()
	{
		return displayString;
	}

	public Object getHandle()
	{
		return handle;
	}
}
