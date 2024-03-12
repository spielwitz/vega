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

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class TextArea extends JScrollPane
{
	private JTextArea textArea;
	
	public TextArea(String text)
	{
		super();
		
		this.textArea = new JTextArea(text);
		
		this.textArea.setLineWrap(true);
		
		this.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		this.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
		
		this.setViewportView(this.textArea);
	}
	
	public void appendText(String text)
	{
		this.textArea.append(text);
	}
	
	public Document getDocument()
	{
		return this.textArea.getDocument();
	}
	
	public String getText()
	{
		return this.textArea.getText();
	}
	
	public void scrollDown()
	{
		this.textArea.setCaretPosition(this.textArea.getText().length());
	}
	
	public void setEditable(boolean editable)
	{
		this.textArea.setEditable(editable);
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.textArea.setEnabled(enabled);
	}
	
	public void setRowsAndColumns(int rows, int columns)
	{
		this.textArea.setRows(rows);
		this.textArea.setColumns(columns);
	}
	
	public void setText(String text)
	{
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	textArea.setText(text);
            }
        });
	}
}
