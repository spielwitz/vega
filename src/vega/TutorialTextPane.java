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

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

import common.Colors;
import common.VegaResources;
import common.CommonUtils;
import uiBaseControls.Button;
import uiBaseControls.IButtonListener;

@SuppressWarnings("serial")
class TutorialTextPane extends JScrollPane
{
	private static final String STYLE_REGULAR = "!";
	static final String STYLE_BUTTON_NEXT = "!ButtonNext";
	
	private static final int FONT_SIZE_REGULAR = 16;
	private JTextPane textPane;
	private IButtonListener callback;
	
	TutorialTextPane(IButtonListener callback)
	{
		super();
		
		this.callback = callback;
		this.textPane = new JTextPane();
		
		this.textPane.setEditable(false);
        this.textPane.setOpaque(false);
        this.textPane.setEditorKit(new HTMLEditorKit());
		this.textPane.setFocusable(false);
		
        this.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		this.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
		
		this.setViewportView(this.textPane);
	}
	
	void setText(String text, boolean enableNextButton)
	{
		StyledDocument doc = this.textPane.getStyledDocument();
		
		try
		{
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e1)
		{
			e1.printStackTrace();
		}
		        
		ArrayList<TextFragment> textFragments = this.parseText(text, doc, enableNextButton);
		
		for (TextFragment textFragment: textFragments)
		{
			try
			{
				doc.insertString(
						doc.getLength(), 
						textFragment.text,
				        doc.getStyle(textFragment.style));
			} catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		
		this.textPane.setCaretPosition(0);
	}
	
	private ArrayList<TextFragment> parseText(String text, StyledDocument doc, boolean enableNextButton)
	{
		ArrayList<TextFragment> textFragments = new ArrayList<TextFragment>();
		HashSet<String> styleDict = new HashSet<String>();
		
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = doc.addStyle(STYLE_REGULAR, def);
		
		StyleConstants.setFontFamily(regular, this.textPane.getFont().getFamily()); 
		StyleConstants.setForeground(regular, this.textPane.getForeground());
		StyleConstants.setFontSize(regular, FONT_SIZE_REGULAR);
		
		int startIndex = 0;
		int pos = 0;
		
		while ((pos = text.indexOf("![", startIndex)) >= 0)
		{
			if (pos > startIndex)
			{
				String textFragment = text.substring(startIndex, pos);
				
				textFragments.add(new TextFragment(textFragment, STYLE_REGULAR));
			}
			
			int posIconEnd = text.indexOf("]", pos+2);
			String[] styleCommandsAndText = text.substring(pos+2, posIconEnd).split("\\|");
			
			String styleName = styleCommandsAndText[0];
			String styledText = 
					styleCommandsAndText.length == 2 ?
							styleCommandsAndText[1] : " ";
			
			if (!styleDict.contains(styleName))
			{
				Style s = doc.addStyle(styleName, regular);
		        
				for (String styleCommand: styleName.split(";"))
				{
					String[] styleCommandKeyValue = styleCommand.split("=");
					
					String styleCommandKey = styleCommandKeyValue[0].trim();
					String styleCommandValue = 
							styleCommandKeyValue.length > 1 ?
									styleCommandKeyValue[1].trim() : "";
					
					switch (styleCommandKey)
					{
						case "color":
							StyleConstants.setForeground(s, Colors.get(Byte.parseByte(styleCommandValue)));
							break;
							
						case "inverse":
							StyleConstants.setBackground(s, this.textPane.getForeground());
							StyleConstants.setForeground(s, this.textPane.getBackground());
							break;
						
						case "size":
							StyleConstants.setFontSize(
									s, 
									CommonUtils.round(
											(double)FONT_SIZE_REGULAR * Double.parseDouble(styleCommandValue)));
							break;
							
						case "bold":
							StyleConstants.setBold(s, true);
							break;
							
						case "italic":
							StyleConstants.setItalic(s, true);
							break;
							
						case "img":
							ImageIcon icon = new ImageIcon (ClassLoader.getSystemResource(styleCommandValue));
							StyleConstants.setIcon(s, icon);
							break;
					}
				}
		        
		        styleDict.add(styleName);
			}
			
			textFragments.add(
					new TextFragment(
							styledText,
							styleName));
			;
			
			startIndex = posIconEnd+1;
		}
		
		if (startIndex < text.length())
		{
			textFragments.add(new TextFragment(text.substring(startIndex), STYLE_REGULAR));
		}
		
		textFragments.add(new TextFragment("\n\n", STYLE_REGULAR));
		
		Style s = doc.addStyle(STYLE_BUTTON_NEXT, regular);
		Button button = new Button(VegaResources.TutorialButtonNext(false), this.callback);
		button.setFocusable(false);
		button.setEnabled(enableNextButton);
		button.setName(STYLE_BUTTON_NEXT);
		StyleConstants.setComponent(s, button);
		textFragments.add(new TextFragment(" ", STYLE_BUTTON_NEXT));
		
		return textFragments;
	}
	
	private class TextFragment
	{
		private String text;
		private String style;
		private TextFragment(String text, String style)
		{
			super();
			this.text = text;
			this.style = style;
		}
	}
}
