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

package vega;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import common.VegaResources;
import uiBaseControls.Button;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.LookAndFeel;
import uiBaseControls.Panel;

@SuppressWarnings("serial") 
class TutorialPanel extends JPanel implements IButtonListener
{
	private TutorialTextPane textPane;
	private Label labTitle;
	private Vega parent;
	
	TutorialPanel(Vega parent)
	{
		super(new GridBagLayout());
		
		this.parent = parent;
		
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(350,300));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets, 
				LookAndFeel.dialogInsets);
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		
		Panel panInner = new Panel(
				new BorderLayout());
		
		Panel panTitle = new Panel(new BorderLayout());
		panTitle.setBackground(Color.black);
		
		Panel panTitleText = new Panel(new FlowLayout(FlowLayout.CENTER));
		panTitleText.setBackground(Color.black);
		this.labTitle = new Label("");
		panTitleText.add(this.labTitle);
		panTitle.add(panTitleText, BorderLayout.CENTER);
		
		panInner.add(panTitle, BorderLayout.NORTH);
		
		this.textPane = new TutorialTextPane(this);
		panInner.add(this.textPane, BorderLayout.CENTER);
		
		this.add(panInner, c);
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		switch (source.getName())
		{
			case (TutorialTextPane.STYLE_BUTTON_NEXT):
				this.parent.setTutorialNextStep();
				break;
		}
		
	}

	void setText(String text, int currentStep, int totalSteps, boolean enableNextButton)
	{
		this.textPane.setText(VegaResources.getString(text), enableNextButton);
		
		this.labTitle.setText(
				VegaResources.TutorialTitle(
						false, 
						Integer.toString(currentStep+1), 
						Integer.toString(totalSteps)));
	}
}
