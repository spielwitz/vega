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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import common.HighscoreEntry;
import common.Highscores;
import common.Player;
import common.VegaResources;
import commonUi.FontHelper;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class HighscoreJDialog extends Dialog implements IButtonListener
{
	private Button butOk;
	
	HighscoreJDialog(
			Frame owner,
			boolean modal,
			Highscores highscores)
	{
		super (owner, VegaResources.HighScoreList(false), new BorderLayout(10, 10));
		
		Panel panHeader = new Panel(new FlowLayout(FlowLayout.CENTER));
		
		Label label = new Label(VegaResources.HighScoreList(false));
		label.setFont(FontHelper.getFont(24));
		panHeader.add(label);
		
		this.addToInnerPanel(panHeader, BorderLayout.NORTH);
		
		Panel panMain = new Panel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.5;
		
		ArrayList<HighscoreEntry> highscoreEntries = highscores.getEntries();
		
		for (int row = 0; row < Highscores.MAX_ENTRIES_COUNT; row++)
		{
			HighscoreEntry entry = row < highscoreEntries.size() ? highscoreEntries.get(row) : null;
			
			c.weightx = 0;
			c.gridx = 0;
			c.gridy = row;
			c.gridwidth = 1;
			c.ipadx = 0;
			
			TextField tfPosition = new TextField(
					entry != null ? this.padStringWithSpaces(Integer.toString(entry.getPosition()), 2) : "",
					null,
					2,
					2,
					null);
				
			tfPosition.setEditable(false);
			tfPosition.setFocusable(false);
			panMain.add(tfPosition, c);
			
			c.weightx = 1;
			c.gridx = 1;
			c.gridwidth = 3;
			c.ipadx = 20;
			
			TextField tfName = new TextField(
					entry != null ? entry.getPlayerName() : "",
					null,
					Player.PLAYER_NAME_LENGTH_MAX,
					Player.PLAYER_NAME_LENGTH_MAX,
					null);
			
			tfName.setEditable(false);
			tfName.setFocusable(false);
			panMain.add(tfName, c);
			
			c.weightx = 0;
			c.gridx = 5;
			c.gridwidth = 1;
			c.ipadx = 0;
			
			TextField tfDate = new TextField(
					entry != null ? VegaUtils.formatDateString(
							VegaUtils.convertMillisecondsToString(entry.getDateAdded())) : "",
					null,
					Player.PLAYER_NAME_LENGTH_MAX,
					Player.PLAYER_NAME_LENGTH_MAX,
					null);
			
			tfDate.setEditable(false);
			tfDate.setFocusable(false);
			panMain.add(tfDate, c);
			
			c.weightx = 0;
			c.gridx = 6;
			c.gridwidth = 1;
			c.ipadx = 0;
			
			TextField tfScore = new TextField(
					entry != null ? this.padStringWithSpaces(Integer.toString(entry.getScore()), 4) : "",
					null,
					4,
					4,
					null);
			
			tfScore.setEditable(false);
			tfScore.setFocusable(false);
			panMain.add(tfScore, c);
		}
		
		this.addToInnerPanel(panMain, BorderLayout.CENTER);
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(owner);	
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		this.close();
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	private String padStringWithSpaces(String text, int length)
	{
		String textWithSpaces = String.format("%" + length + "c", ' ') + text;
		return textWithSpaces.substring(textWithSpaces.length() - length);
	}
}
