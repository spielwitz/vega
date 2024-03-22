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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import common.CommonUtils;
import common.Player;
import common.VegaResources;
import spielwitz.biDiServer.Tuple;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IListListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.ListItem;
import uiBaseControls.Panel;
import uiBaseControls.PanelWithInsets;
import uiBaseControls.TextArea;

@SuppressWarnings("serial")
class MessengerNewJDialog extends Dialog implements IListListener, IButtonListener
{
	private IMessengerCallback callback;
	private Hashtable<String, MessagePanel> messagePanelsByRecipientStrings;
	
	private Button butAdd;
	private Button butDelete;
	private List listRecipients;
	private ArrayList<ListItem> listRecipientsModel;
	
	MessengerNewJDialog(Messages messages, IMessengerCallback callback)
	{
		super((Component)callback, VegaResources.Messenger(false), new BorderLayout(10, 10));
		
		this.callback = callback;
		this.messagePanelsByRecipientStrings = new Hashtable<String, MessagePanel>();
		
		this.setModal(false);
		this.setAlwaysOnTop(true);
		
		PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 10));
		
		//this.createListUsersModel();
		this.listRecipientsModel = new ArrayList<ListItem>();
		this.listRecipientsModel.add(new ListItem("1234567890", null));
		
		int widthList = CommonUtils.round(1.2 * 
				this.getFontMetrics(this.getFont()).stringWidth(new String(new char[Player.PLAYER_NAME_LENGTH_MAX]).replace("\0", "H")));
		this.listRecipients = new List(this, this.listRecipientsModel);
		this.listRecipients.setPreferredSize(new Dimension(widthList, 200));
		panUsersList.addToInnerPanel(this.listRecipients, BorderLayout.CENTER);
		
		Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		this.butAdd = new Button("+", this);
		this.butAdd.setToolTipText(VegaResources.AddCredentials(false));
		
		panUsersListButtons.add(this.butAdd);
		
		this.butDelete = new Button("-", this);
		this.butDelete.setToolTipText(VegaResources.DeleteCredentials(false));
		panUsersListButtons.add(this.butDelete);
		
		panUsersList.addToInnerPanel(panUsersListButtons, BorderLayout.SOUTH);
		
		this.addToInnerPanel(panUsersList, BorderLayout.WEST);
		
		this.pack();
		this.setResizable(true);
		this.setLocationRelativeTo((Component)callback);
	}

	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	public void onNewMessageReceived(Tuple<String,Message> t)
	{
//		MessagePanel messagePanel = null;
//		
//		if (!this.messagePanelsByRecipientStrings.containsKey(t.getE1()))
//		{
//			messagePanel = this.addMessagePanel(t.getE1());
//		}
//		else
//		{
//			messagePanel = this.messagePanelsByRecipientStrings.get(t.getE1());			
//			messagePanel.addMessageToTextArea(t.getE2());
//		}
//
//		messagePanel.setNewMessageIndicator(!messagePanel.isVisiblePanel());
	}

	
	private class MessagePanel extends Panel implements IButtonListener, DocumentListener
	{
		private static final int MAX_CHARACTERS_COUNT = 1000;
		private Button butSend;
		private Button butTo;
		private Label labCharactersLeft;
		private ArrayList<String> recipients;
		
		private String recipientsString;
		private int tabIndex;
		private TextArea taComposeMessage;
		
		private TextArea taMessages;
		
		public MessagePanel(String recipientsString, ArrayList<Message> messages, int tabIndex)
		{
			super(new GridBagLayout());
			
			this.recipientsString = recipientsString;
			this.tabIndex = tabIndex;
			
			GridBagConstraints cPanOuter = new GridBagConstraints();
			cPanOuter.insets = new Insets(10, 0, 0, 0);
			cPanOuter.fill = GridBagConstraints.HORIZONTAL;
			
			Panel panInner = new Panel(new BorderLayout(10,10));
			
			Panel panRecipients = new Panel(new BorderLayout(10, 10));
			
			this.butTo = new Button(VegaResources.MessengerRecipients(false)+":", this);
			
			panRecipients.add(this.butTo, BorderLayout.WEST);
			
			this.recipients = Messages.getRecipientsFromRecipientsString(
					recipientsString, callback.getClientUserIdForMessenger());
			
			StringBuilder sb = new StringBuilder();
			for (String recipient: recipients)
			{
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(recipient);
			}

			TextArea taRecipients = new TextArea(sb.toString());
			taRecipients.setRowsAndColumns(2, 50);
			taRecipients.setEditable(false);
			taRecipients.setBorder(null);
			panRecipients.add(taRecipients, BorderLayout.CENTER);
			
			panInner.add(panRecipients, BorderLayout.NORTH);
			
			this.taMessages = new TextArea("");
			this.taMessages.setRowsAndColumns(20, 50);
			this.taMessages.setEditable(false);
			panInner.add(taMessages, BorderLayout.CENTER);
			
			Panel panCompose = new Panel(new BorderLayout(10, 10));
			
			this.taComposeMessage = new TextArea("");
			this.taComposeMessage.setRowsAndColumns(3, 40);
			
			if (recipientsString != null)
				this.taComposeMessage.getDocument().addDocumentListener(this);
			else
				this.taComposeMessage.setEditable(false);
			 
			panCompose.add(this.taComposeMessage, BorderLayout.CENTER);
			
			this.butSend = new Button(VegaResources.MessengerSend(false), this);
			this.butSend.setEnabled(recipientsString != null);
			panCompose.add(this.butSend, BorderLayout.EAST);
			
			this.labCharactersLeft = new Label(
					recipientsString != null ?
							VegaResources.MessengerCharactersLeft(false, Integer.toString(MAX_CHARACTERS_COUNT)) :
							"");

			panCompose.add(this.labCharactersLeft, BorderLayout.NORTH);
			
			panInner.add(panCompose, BorderLayout.SOUTH);
			
			this.add(panInner, cPanOuter);
			
			if (messages != null)
			{
				for (Message message: messages)
				{
					this.addMessageToTextArea(message);
				}
			}
		}
		
		public void addMessageToTextArea(Message message)
		{
			if (this.taMessages.getText().length() > 0)
			{
				this.taMessages.appendText("\n\n");
			}
			
			this.taMessages.appendText("--- " + message.getSender());
			
			this.taMessages.appendText(" " + VegaUtils.formatDateTimeString(
							VegaUtils.convertMillisecondsToString(message.getDateCreated())));
			
			this.taMessages.appendText(" ---\n" + message.getText());
			
			this.taMessages.scrollDown();
		}
		
		@Override
		public void buttonClicked(Button source)
		{
			if (source == this.butSend)
			{
				String text = this.taComposeMessage.getText();
				
				if (text.endsWith("\n"))
				{
					text = text.substring(0, text.length() - 1);
				}
				
				if (text.length() > 0)
				{
					Message message = new Message(
							callback.getClientUserIdForMessenger(),
							System.currentTimeMillis(),
							text);
					
					this.addMessageToTextArea(message);
					callback.pushNotificationFromMessenger(recipients, message);
				}
				
				taComposeMessage.setText("");
			}
			else if (source == this.butTo)
			{
				//addNewButtonClicked(this.recipients);
			}
		}
		
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			this.onComposeMessageChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			this.onComposeMessageChanged();
		}

		public boolean isVisiblePanel()
		{
			return true;
			//return tabPane.getSelectedIndex() == this.tabIndex;
		}
		
		@Override
		public void removeUpdate(DocumentEvent e)
		{
			this.onComposeMessageChanged();
		}

		public void setNewMessageIndicator(boolean newMessages)
		{
//			TabLabelComponent tabLabel = (TabLabelComponent)tabPane.getTabComponentAt(this.tabIndex);
//			
//			tabLabel.setNewMessageIndicatorVisible(newMessages);
//			
//			callback.setMessagesByRecipientsRead(this.recipientsString, !newMessages);
		}
		
		private void onComposeMessageChanged()
		{
			String text = this.taComposeMessage.getText(); 
			
			int charactersLeft = MAX_CHARACTERS_COUNT - text.length();
			
			if (charactersLeft < 0)
			{
				text = text.substring(0, MAX_CHARACTERS_COUNT);
				
				this.taComposeMessage.setText(text.substring(0, MAX_CHARACTERS_COUNT));
				charactersLeft = 0;
			}
			
			this.labCharactersLeft.setText(VegaResources.MessengerCharactersLeft(false, Integer.toString(charactersLeft)));
			
			if (text.length()> 0)
			{
				if (text.charAt(text.length() - 1) == '\n')
				{
					this.butSend.doClick();
				}
			}
		}
	}
	
	private class RecipientsSelector extends Dialog implements IButtonListener, IListListener
	{
		boolean ok = false;
		ArrayList<String> recipients = new ArrayList<String>();
		private Button butAbort;
		
		private Button butOk;
		private List listRecipients;
		private ArrayList<String> userIds; 
		
		public RecipientsSelector(Component parent, ArrayList<String> selectedUserIds, ArrayList<String> allUserIds)
		{
			super(parent, VegaResources.MessengerRecipients(false), new BorderLayout(10, 10));
			
			this.userIds = allUserIds;
			
			this.listRecipients = new List(allUserIds, this);
			this.listRecipients.setSelectionMethod(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			this.listRecipients.setPreferredSize(new Dimension(150, 200));
			
			ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
			
			for (int i = 0; i < allUserIds.size(); i++)
			{
				if (selectedUserIds.contains(allUserIds.get(i)))
				{
					selectedIndices.add(i);
				}
			}
			
			if (selectedIndices.size() > 0)
			{
				int[] selectedIndicesArray = new int[selectedIndices.size()];
				
				for (int i = 0; i < selectedIndicesArray.length; i++)
					selectedIndicesArray[i] = selectedIndices.get(i);
				
				this.listRecipients.setSelectedIndices(selectedIndicesArray);
			}
			
			this.addToInnerPanel(this.listRecipients, BorderLayout.CENTER);
			
			Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
			
			this.butAbort = new Button(VegaResources.Cancel(false), this);
			panButtons.add(this.butAbort);
			
			this.butOk = new Button(VegaResources.OK(false), this);
			panButtons.add(this.butOk);
			
			this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
			
			this.pack();
			this.setLocationRelativeTo(parent);
		}
		@Override
		public void buttonClicked(Button source)
		{
			if (source == this.butOk)
			{
				int[] selectedIndices = this.listRecipients.getSelectedIndices();
				
				if (selectedIndices != null)
				{
					for (int i = 0; i < selectedIndices.length; i++)
						this.recipients.add(this.userIds.get(selectedIndices[i]));
				}
				
				this.ok = true;
			}
			
			this.close();
		}
		
		@Override
		public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount)
		{
			if (source == this.listRecipients && clickCount >= 2)
			{
				this.recipients.add(this.userIds.get(selectedIndex));
				this.ok = true;
				this.close();
			}
		}
		
		@Override
		protected boolean confirmClose()
		{
			return true;
		}
	}

	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonClicked(Button source) {
		// TODO Auto-generated method stub
		
	}
}
