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

package uiBaseControls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class ComboBox extends JComboBox<String> implements ActionListener
{
	private IComboBoxListener callback;
	private ArrayList<ListItem> listItems;
	private ListItem selectedItem;
	private boolean eventsEnabled;
	
	public ComboBox(String[] data, int widthNumCharacters, String selectedItem, IComboBoxListener callback)
	{
		super(data);
		
		ArrayList<ListItem> listItems = getListItems(data);
		Optional<ListItem> selectedListItem = listItems.stream().filter(i -> i.getDisplayString().equals(selectedItem)).findFirst();
		
		if (selectedListItem.isPresent())
		{
			this.initialize(listItems, widthNumCharacters, selectedListItem.get(), callback);
		}
		else
		{
			this.initialize(listItems, widthNumCharacters, null, callback);
		}
	}
	
	public ComboBox(ArrayList<ListItem> listItems, int widthNumCharacters, ListItem selectedItem, IComboBoxListener callback)
	{
		super(getStringData(listItems));
		this.initialize(listItems, widthNumCharacters, selectedItem, callback);
	}
	
	private void initialize(ArrayList<ListItem> listItems, int widthNumCharacters, ListItem selectedItem, IComboBoxListener callback)
	{
		this.callback = callback;
		this.listItems = listItems;
		
		this.setPrototypeDisplayValue( new String(new char[widthNumCharacters]).replace('\0', ' '));
		
		if (listItems.size() > 0 && selectedItem != null)
		{
			this.selectedItem = selectedItem;
			this.setSelectedItem(selectedItem.getDisplayString());
		}
		
		if (callback != null)
		{
			this.addActionListener(this);
			this.eventsEnabled = true;
		}
	}
	
	private static ArrayList<ListItem> getListItems(String[] data)
	{
		ArrayList<ListItem> listItems = new ArrayList<ListItem>();
		
		for (String value: data)
		{
			listItems.add(new ListItem(value, null));
		}
		
		return listItems;
	}
	
	private static String[] getStringData(ArrayList<ListItem> listItems)
	{
		String[] data = new String[listItems.size()];
		
		for (int i = 0; i < listItems.size(); i++)
		{
			data[i] = listItems.get(i).getDisplayString();
		}
		
		return data;
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (!this.eventsEnabled)
		{
			return;
		}
		
		ListItem newSelectedItem = this.getSelectedListItem();
		
		if (this.selectedItem == null ||
			!this.selectedItem.equals(newSelectedItem))
		{
			this.selectedItem = newSelectedItem;
			this.callback.comboBoxItemSelected(
					this, 
					this.selectedItem != null ?
							this.selectedItem.getDisplayString() :
							null);
		}
	}
	
	public ListItem getSelectedListItem()
	{
		int index = this.getSelectedIndex();
		
		return 
				index >= 0 ?
						this.listItems.get(index) :
						null;
	}
	
	public void enableEvents(boolean enabled)
	{
		this.eventsEnabled = enabled;
	}
	
	@Override
	public void removeAllItems()
	{
		this.removeActionListener(this);
		super.removeAllItems();
		this.selectedItem = null;
		this.addActionListener(this);
	}
	
	public void setItems(String[] data)
	{
		this.setItems(getListItems(data));
	}
	
	public void setItems(ArrayList<ListItem> listItems)
	{
		this.removeActionListener(this);
		super.removeAllItems();
		this.selectedItem = null;
		this.listItems = listItems;
		
		for (ListItem listItem: listItems)
		{
			this.addItem(listItem.getDisplayString());
		}
		this.addActionListener(this);
	}
}
