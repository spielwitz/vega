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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class List extends JScrollPane implements MouseListener, ListSelectionListener
{
	private IListListener callback;
	private boolean eventsEnabled;
	private JList<String> list;
	private ArrayList<ListItem> listItems;
	private DefaultListModel<String> lm;
	
	public List(
			ArrayList<String> data,
			IListListener callback)
	{
		super();
		initialize(callback, this.getListItems(data));
	}
	
	public List(
			IListListener callback,
			ArrayList<ListItem> listItems)
	{
		super();
		initialize(callback, listItems);
	}
	
	public void clearSelection()
	{
		this.eventsEnabled = false;
		this.list.clearSelection();
		this.eventsEnabled = true;
	}
	
	public int[] getSelectedIndices()
	{
		return this.list.getSelectedIndices();
	}
	
	public ListItem getSelectedListItem()
	{
		int index = this.list.getSelectedIndex();
		
		if (index >= 0)
		{
			return this.listItems.get(index);
		}
		else
		{
			return null;
		}
	}
	
	public void setCellRenderer(ListCellRenderer<? super String> renderer)
	{
		this.list.setCellRenderer(renderer);
	}
	
	public String getSelectedValue()
	{
		return this.list.getSelectedValue();
	}
	
	public boolean isSelectionEmpty()
	{
		return this.list.isSelectionEmpty();
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (!this.eventsEnabled)
		{
			return;
		}
		
		int index = list.locationToIndex(e.getPoint());
		
		if (index >= 0)
		{
			this.callback.listItemSelected(
					this, 
					this.lm.get(index),
					index,
					e.getClickCount());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	
	public void refreshListItems(ArrayList<ListItem> listItems)
	{
		this.eventsEnabled = false;
		
		this.listItems = listItems;
		this.lm.removeAllElements();
		
		for (ListItem listItem: listItems)
		{
			this.lm.addElement(listItem.getDisplayString());
		}
		this.eventsEnabled = true;
	}
	
	public void refreshListModel(ArrayList<String> data)
	{
		this.refreshListItems(this.getListItems(data));
	}

	public void setSelectedIndex(int index)
	{
		this.eventsEnabled = false;
		this.list.setSelectedIndex(index);
		this.eventsEnabled = true;
	}
	
	public void setSelectedIndices(int[] indices)
	{
		this.eventsEnabled = false;
		this.list.setSelectedIndices(indices);
		this.eventsEnabled = true;
	}

	public void setSelectedValue(String value)
	{
		this.eventsEnabled = false;
		this.list.setSelectedValue(value, true);
		this.eventsEnabled = true;
	}

	public void setSelectionMethod(int selectionMode)
	{
		this.list.setSelectionMode(selectionMode);
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!this.eventsEnabled)
		{
			return;
		}
		
		this.callback.listItemSelected(
				this, 
				this.list.getSelectedValue(), 
				this.list.getSelectedIndex(), 
				1);
	}

	private ArrayList<ListItem> getListItems(ArrayList<String> data)
	{
		ArrayList<ListItem> listItems = new ArrayList<ListItem>();
		
		for (String value: data)
		{
			listItems.add(new ListItem(value, null));
		}
		
		return listItems;
	}

	private void initialize(
			IListListener callback,
			ArrayList<ListItem> listItems)
	{
		this.callback = callback;
		
		this.lm = new DefaultListModel<String>();
		this.listItems = listItems;
		
		for (ListItem listItem: listItems)
		{
			this.lm.addElement(listItem.getDisplayString());
		}
		
		this.list = new JList<String>(this.lm);
		
		this.list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.list.setLayoutOrientation(JList.VERTICAL);
		this.list.setVisibleRowCount(-1);
		
		if (this.callback != null)
		{
			this.list.addMouseListener(this);
			this.list.addListSelectionListener(this);
			this.eventsEnabled = true;
		}
		
		this.setViewportView(this.list);
		
		this.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		this.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
	
	}
}
