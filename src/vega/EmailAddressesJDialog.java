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

package vega;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

import common.VegaResources;
import common.CommonUtils;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IListListener;
import uiBaseControls.List;
import uiBaseControls.Panel;

@SuppressWarnings("serial") 
class EmailAddressesJDialog extends Dialog
			implements 	IButtonListener,
						IListListener
{
	private Button butCancel;
	private Button butSelect;
	private Button butDelete;
	
	private ArrayList<String> emailAddresses;
	private List list;
	private int[] seq;
	
	int selectedIndex = -1;
	
	EmailAddressesJDialog(
			Component parent,
			ArrayList<String> emailAddresses)
	{
		super (parent, VegaResources.RecentlyUsedEmailAddresses(false), new BorderLayout());
		
		this.list = new List(emailAddresses, this);
		this.list.setPreferredSize(new Dimension(400, 300));
		
		this.emailAddresses = emailAddresses;
		
		this.addToInnerPanel(this.list, BorderLayout.CENTER);
		
		// ----
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));

		this.butDelete = new Button(VegaResources.Delete(false), this);
		panButtons.add(this.butDelete);

		this.butSelect = new Button(VegaResources.Select(false), this);
		panButtons.add(this.butSelect);
		
		this.butCancel = new Button(VegaResources.Close(false), this);
		panButtons.add(this.butCancel);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		
		this.refreshListModel();
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butCancel)
		{
			this.close(true);
		}
		else if (source == this.butDelete)
		{
			this.emailAddresses.remove(this.selectedIndex);
			this.refreshListModel();
		}
		else if (source == this.butSelect)
		{
			this.close(false);
		}
	}
	
	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
	{
		this.selectedIndex = this.seq[selectedIndex];
		this.setControlsEnabled();
		
		if (clickCount >= 2)
		{
			this.close(false);
		}
	}
	
	private void close(boolean abort)
	{
		if (abort)
			this.selectedIndex = -1;
		
		this.setVisible(false);
		this.dispose();
	}

	private void refreshListModel()
	{
		Object[] objectList = this.emailAddresses.toArray();
		String[] addressesUnsorted =  Arrays.copyOf(objectList,objectList.length,String[].class);
		ArrayList<String> addressesSorted = new ArrayList<String>(objectList.length);
		
		this.seq = CommonUtils.sortList(addressesUnsorted, false);
		
		for (int i = 0; i < this.emailAddresses.size(); i++)
			addressesSorted.add(this.emailAddresses.get(this.seq[i]));
		
		this.list.refreshListModel(addressesSorted);
		
		this.setControlsEnabled();
	}

	private void setControlsEnabled()
	{
		this.butDelete.setEnabled(!this.list.isSelectionEmpty());
		this.butSelect.setEnabled(!this.list.isSelectionEmpty());
	}
}
