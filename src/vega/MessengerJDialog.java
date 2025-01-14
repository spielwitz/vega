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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import common.CommonUtils;
import common.Player;
import common.VegaResources;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import spielwitz.biDiServer.Tuple;
import spielwitz.biDiServer.User;
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
class MessengerJDialog extends Dialog implements IListListener, IButtonListener
{
	private static final Color selectionBackground = (Color) UIManager.get("List.selectionBackground");
	
	private Button butAdd;
	private Button butDelete;
	private IMessengerCallback callback;
	private Object listLockObject = new Object();
	private List listRecipients;
	
	private MessagePanel panMessage;
	
	MessengerJDialog(Messages messages, IMessengerCallback callback)
	{
		super((Component)callback, VegaResources.Messenger(false), new BorderLayout(0, 10));
		
		this.callback = callback;
		
		this.setModal(false);
		this.setAlwaysOnTop(true);
		
		PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 5));
		
		ArrayList<ListItem> listItems = new ArrayList<ListItem>(); 
		for (String recipientString: messages.getMessagesByRecipients().keySet())
		{
			listItems.add(
					this.getNewListItem(
							recipientString, 
							messages.getMessagesByRecipients().get(recipientString)));
		}
		
		int widthList = CommonUtils.round(1.2 * 
				this.getFontMetrics(this.getFont()).stringWidth(new String(new char[Player.PLAYER_NAME_LENGTH_MAX]).replace("\0", "H")));
		this.listRecipients = new List(this, listItems);
		this.listRecipients.sort();
		this.listRecipients.setPreferredSize(new Dimension(widthList, 200));
		this.listRecipients.setCellRenderer(new RecipientsListCellRenderer());
		
		panUsersList.addToInnerPanel(this.listRecipients, BorderLayout.CENTER);
		
		Panel panUsersListButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		this.butAdd = new Button("+", this);
		this.butAdd.setToolTipText(VegaResources.ConversationNew(false));
		
		panUsersListButtons.add(this.butAdd);
		
		this.butDelete = new Button("-", this);
		this.butDelete.setToolTipText(VegaResources.ConversationDelete(false));
		panUsersListButtons.add(this.butDelete);
		
		panUsersList.addToInnerPanel(panUsersListButtons, BorderLayout.SOUTH);
		
		this.addToInnerPanel(panUsersList, BorderLayout.WEST);
		
		this.panMessage = new MessagePanel();
		this.addToInnerPanel(this.panMessage, BorderLayout.CENTER);
		
		this.pack();
		this.setResizable(true);
		this.setLocationRelativeTo((Component)callback);
		
		if (this.listRecipients.getListItems().size() > 0)
		{
			this.listRecipients.setSelectedIndex(0);
		}
		
		this.listItemSelected(
				null, 
				null, 
				this.listRecipients.getListItems().size() > 0 ? 0 : -1, 
				0);
	}
	
	@Override
	public void buttonClicked(Button source) 
	{
		if (source == this.butAdd)
		{
			this.addNewConversation();
		}
		else if (source == this.butDelete)
		{
			this.deleteConversation();
		}
	}
	
	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount) 
	{
		MessagePanelContent content = null;
		
		if (selectedIndex >= 0)
		{
			content = (MessagePanelContent)this.listRecipients.getListItems().get(selectedIndex).getHandle();
		
			this.callback.setMessagesByRecipientsRead(content.recipientsString, true);
			content.hasUnreadMessages = false;
			this.listRecipients.repaint();
		}
		
		this.panMessage.setContent(content);
	}
	
	public void onNewMessageReceived(Tuple<String,Message> t)
	{
		this.addMessage(t.getE1(), t.getE2());
	}
	
	private void addMessage(String recipientsString, Message message)
	{
		synchronized(this.listLockObject)
		{
			String recipientsStringSelected = null;
			
			if (this.listRecipients.getSelectedIndex() >= 0)
			{
				MessagePanelContent contentSelected = (MessagePanelContent) this.listRecipients.getSelectedListItem().getHandle();
				recipientsStringSelected = contentSelected.recipientsString;
			}
			else
			{
				recipientsStringSelected = recipientsString;
			}
			
			ListItem listItem = null;
			int index = this.getListIndexByRecipientsString(recipientsString);
			
			if (index < 0)
			{
				listItem = this.getNewListItem(
						recipientsString, 
						new ArrayList<Message>());
				
				this.listRecipients.getListItems().add(listItem);
			}
			else
			{
				listItem = this.listRecipients.getListItems().get(index);
			}
			
			MessagePanelContent content = (MessagePanelContent)listItem.getHandle();

			if (message != null)
			{
				content.addMessage(message);
				
				if (recipientsStringSelected == null || recipientsStringSelected.equals(recipientsString))
				{
					content.hasUnreadMessages = false;
				}
			}
			
			this.listRecipients.sort();
			
			index = this.getListIndexByRecipientsString(recipientsStringSelected);
			this.listRecipients.setSelectedIndex(index);
			this.listItemSelected(this.listRecipients, null, index, 1);
		}
	}
	
	@Override
	public int[] sortListItems(ArrayList<ListItem> listItems)
	{
		String[] dateTimeStrings = new String[listItems.size()];
		
		for (int i = 0; i < this.listRecipients.getListItems().size(); i++)
		{
			MessagePanelContent content = (MessagePanelContent)this.listRecipients.getListItems().get(i).getHandle();
			dateTimeStrings[i] = Long.toString(content.createDateTime);
		}
		
		return CommonUtils.sortList(dateTimeStrings, true);
	}

	@Override
	protected boolean confirmClose()
	{
		boolean hasUnsentMessages = false;
		boolean close = true;
		
		for (ListItem listItem: this.listRecipients.getListItems())
		{
			MessagePanelContent content = (MessagePanelContent) listItem.getHandle();
			
			if (content.composeMessage.trim().length() > 0)
			{
				hasUnsentMessages = true;
				break;
			}
		}
		
		if (hasUnsentMessages)
		{
			MessageBoxResult result = MessageBox.showYesNo(
					this, 
					VegaResources.UnsentMessages2(false), 
					VegaResources.UnsentMessages(false));
			
			close = result == MessageBoxResult.YES;
		}
		
		if (close)
		{
			this.callback.messengerClosed();
		}
		
		return close;
	}

	private void addNewConversation()
	{
		synchronized(this.listLockObject)
		{
			ArrayList<String> userIds = new ArrayList<String>();
			
			for (User user: this.callback.getUsersForMessenger())
			{
				if (!user.getId().equals(this.callback.getClientUserIdForMessenger()))
				{
					userIds.add(user.getId());
				}
			}
			
			RecipientsSelector dlg = new RecipientsSelector(this, userIds);
					
			dlg.setVisible(true);
			
			if (dlg.ok && dlg.recipients.size() > 0)
			{
				String recipientsString = Messages.getRecipientsStringFromRecipients(dlg.recipients, this.callback.getClientUserIdForMessenger());
				
				int index = this.getListIndexByRecipientsString(recipientsString);
				
				if (index < 0)
				{
					this.listRecipients.getListItems().add(0, this.getNewListItem(recipientsString, new ArrayList<Message>()));
					this.listRecipients.refresh();
					index = 0;
				}
				
				this.listRecipients.setSelectedIndex(index);
				this.listItemSelected(this.listRecipients, null, index, 1);
			}
		}
	}
	
	private void deleteConversation()
	{
		synchronized(this.listLockObject)
		{
			int index = this.listRecipients.getSelectedIndex();
			if (index < 0) return;
			
			ListItem listItem = this.listRecipients.getListItems().get(index);
			MessagePanelContent content = (MessagePanelContent) listItem.getHandle();
			
			this.callback.removeRecipientsString(content.recipientsString);
			
			this.listRecipients.getListItems().remove(index);
			this.listRecipients.refresh();
			
			if (this.listRecipients.getListItems().size() > 0)
			{
				this.listRecipients.setSelectedIndex(0);
				
				this.listItemSelected(
						null, 
						null, 
						0, 
						0);
			}
			else
			{
				this.listItemSelected(
						null, 
						null, 
						-1, 
						0);
			}
		}
	}

	private int getListIndexByRecipientsString(String recipientsString)
	{
		if (recipientsString == null) return -1;
		
		int index = -1;
		
		for (int i = 0; i < this.listRecipients.getListItems().size(); i++)
		{
			MessagePanelContent content = (MessagePanelContent)this.listRecipients.getListItems().get(i).getHandle();
			
			if (content.recipientsString.equals(recipientsString))
			{
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	private ListItem getNewListItem(String recipientsString, ArrayList<Message> messages)
	{
		synchronized(this.listLockObject)
		{
			this.callback.addRecipientsString(recipientsString);
			
			MessagePanelContent content = new MessagePanelContent(recipientsString);
			
			ArrayList<String> recipients = 
					Messages.getRecipientsFromRecipientsString(
							recipientsString, 
							this.callback.getClientUserIdForMessenger());
			
			int maxNames = 3;
			
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < Math.min(recipients.size(), maxNames); i++)
			{
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(recipients.get(i));
			}
			
			if (recipients.size() > maxNames)
			{
				sb.append("\n(+" + (recipients.size() - maxNames) + ")");
			}
			
			for (Message message: messages)
			{
				content.addMessage(
						message);
			}
			
			content.hasUnreadMessages = this.callback.hasUnreadMessages(recipientsString);
			
			ListItem newListItem = new 
					ListItem(
							sb.toString(),
							content);
			
			return newListItem;
		}
	}
	
	private class MessagePanel extends PanelWithInsets implements IButtonListener, DocumentListener
	{
		private static final int MAX_CHARACTERS_COUNT = 1000;
		
		private Button butSend;
		private Label labCharactersLeft;
		private TextArea taComposeMessage;
		
		private TextArea taMessages;
		
		private TextArea taRecipients;
		
		private MessagePanel()
		{
			super(new BorderLayout(0, 10));
			
			Panel panRecipients = new Panel(new BorderLayout(10, 0));
			
			this.taRecipients = new TextArea("");
			taRecipients.setRowsAndColumns(2, 40);
			taRecipients.setEditable(false);
			taRecipients.setBorder(null);
			panRecipients.add(taRecipients, BorderLayout.CENTER);
			
			this.addToInnerPanel(panRecipients, BorderLayout.NORTH);
			
			this.taMessages = new TextArea("");
			this.taMessages.setRowsAndColumns(20, 50);
			this.taMessages.setEditable(false);
			
			this.addToInnerPanel(this.taMessages, BorderLayout.CENTER);
			
			Panel panCompose = new Panel(new BorderLayout(10, 5));
			
			this.labCharactersLeft = new Label("");
			panCompose.add(this.labCharactersLeft, BorderLayout.NORTH);
			
			this.taComposeMessage = new TextArea("");
			this.taComposeMessage.setRowsAndColumns(3, 30);
			this.taComposeMessage.getDocument().addDocumentListener(this);
			
			panCompose.add(this.taComposeMessage, BorderLayout.CENTER);
			
			this.butSend = new Button(VegaResources.MessengerSend(false), this);
			panCompose.add(this.butSend, BorderLayout.EAST);
			
			this.addToInnerPanel(panCompose, BorderLayout.SOUTH);
			
			this.setContent(null);
		}
		
		@Override
		public void buttonClicked(Button source)
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
				
				MessagePanelContent content = (MessagePanelContent) listRecipients.getSelectedListItem().getHandle();
				addMessage(content.recipientsString, message);
				this.setContent(content);
				
				callback.pushNotificationFromMessenger(
						Messages.getRecipientsFromRecipientsString(
								content.recipientsString,
								callback.getClientUserIdForMessenger()), 
						message);
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

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			this.onComposeMessageChanged();
		}

		private void checkCharactersLeft()
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
		}

		private void onComposeMessageChanged()
		{
			this.checkCharactersLeft();
			
			String text = this.taComposeMessage.getText();
			
			if (text.length()> 0)
			{
				if (text.charAt(text.length() - 1) == '\n')
				{
					this.butSend.doClick();
					return;
				}
			}
			
			synchronized(listLockObject)
			{
				ListItem listItem = listRecipients.getSelectedListItem();
				MessagePanelContent content = (MessagePanelContent)listItem.getHandle();
				content.composeMessage = text;
			}
		}

		private void setContent(MessagePanelContent content)
		{
			this.taComposeMessage.setEditable(content != null);
			this.butSend.setEnabled(content != null);

			if (content != null)
			{
				ArrayList<String> recipients = Messages.getRecipientsFromRecipientsString(
						content.recipientsString, callback.getClientUserIdForMessenger());
				
				StringBuilder sb = new StringBuilder();
				sb.append("To/From: ");
				for (int i = 0; i < recipients.size(); i++)
				{
					if (i > 0) sb.append(", ");
					sb.append(recipients.get(i));
				}
				
				this.taRecipients.setText(sb.toString());
				this.taMessages.setText(content.messages);
				this.taComposeMessage.setText(content.composeMessage);
				this.taMessages.scrollDown();
			}
			else
			{
				this.taRecipients.setText("To/From: ");
				this.taMessages.setText("");
				this.taComposeMessage.setText("");
			}
			
			this.checkCharactersLeft();
		}
	}

	private class MessagePanelContent implements Comparator<MessagePanelContent> 
	{
		private String composeMessage;
		private long createDateTime;
		private boolean hasUnreadMessages;
		private String messages;
		private String recipientsString;
		
		private MessagePanelContent() {}
		
		private MessagePanelContent(String recipientString)
		{
			super();
			this.recipientsString = recipientString;
			this.messages = "";
			this.composeMessage = "";
			this.hasUnreadMessages = false;
			this.createDateTime = System.currentTimeMillis();
		}

		@Override
		public int compare(MessagePanelContent o1, MessagePanelContent o2)
		{
			if (o1.createDateTime > o2.createDateTime) return -1;
			else if (o1.createDateTime < o2.createDateTime) return 1;
			else return 0;
		}
		
		private void addMessage(Message message)
		{
			StringBuilder sb = new StringBuilder(this.messages);
			
			if (sb.length() > 0)
			{
				sb.append("\n\n");
			}
			
			sb.append("--- " + message.getSender());
			
			sb.append(" " + VegaUtils.formatDateTimeString(
							VegaUtils.convertMillisecondsToString(message.getDateCreated())));
			
			sb.append(" ---\n" + message.getText());
			
			this.messages = sb.toString();
			this.composeMessage = "";
			this.createDateTime = message.getDateCreated();
			this.hasUnreadMessages = true;
		}
	}
	
	private class NewMessageIndicatorPanel extends JPanel
	{
		NewMessageIndicatorPanel()
		{
			this.setPreferredSize(new Dimension(10, 10));
		}
		
		@Override
		public void paint(Graphics g)
		{
			g.setColor(Color.red);
			Dimension dim = this.getSize();
			
			int diameter = 6;
			
			g.fillOval(
					(dim.width - diameter) / 2,
					(dim.height - diameter) / 2,
					diameter,
					diameter);
		}
	}
	
	private class RecipientsListCellRenderer extends JPanel implements ListCellRenderer<String>
	{
		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus)
		{
			if (index < 0) return null;
			
			ListItem listItem = listRecipients.getListItems().get(index);
			String[] lines = listItem.getDisplayString().split("\n");
			MessagePanelContent content = (MessagePanelContent) listItem.getHandle();
			
			Color backgroundColor =
					isSelected ?
							selectionBackground :
								index % 2 == 0 ?
										new Color(30, 30, 30) :
										new Color(50, 50, 50);
			
			PanelWithInsets panel = null;
			
			if (content.hasUnreadMessages)
			{
				panel = new PanelWithInsets(new BorderLayout(5, 0));
				panel.setBackground(backgroundColor);
				
				Panel panIndicator = new Panel(new BorderLayout());
				panIndicator.add(new NewMessageIndicatorPanel(), BorderLayout.CENTER);
				panIndicator.setBackground(backgroundColor);
				panel.addToInnerPanel(panIndicator, BorderLayout.WEST);
				
				Panel panLines = new Panel(new GridLayout(lines.length, 1));
				panLines.setBackground(backgroundColor);
				for (int line = 0; line < lines.length; line++)
				{
					panLines.add(new JLabel(lines[line]));
				}
				
				panel.addToInnerPanel(panLines, BorderLayout.CENTER);
			}
			else
			{
				panel = new PanelWithInsets(new GridLayout(lines.length, 1));
				
				for (int line = 0; line < lines.length; line++)
				{
					panel.addToInnerPanel(new JLabel(lines[line]));
				}
			}
			
			panel.setBackgroundColor(backgroundColor);
			
			return panel;
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
		
		public RecipientsSelector(Component parent, ArrayList<String> allUserIds)
		{
			super(parent, VegaResources.MessengerRecipients(false), new BorderLayout(10, 10));
			
			this.userIds = allUserIds;
			
			this.listRecipients = new List(allUserIds, this);
			this.listRecipients.setSelectionMethod(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			this.listRecipients.setPreferredSize(new Dimension(150, 200));
						
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
		public int[] sortListItems(ArrayList<ListItem> listItems)
		{
			return null;
		}
		
		@Override
		protected boolean confirmClose()
		{
			return true;
		}
		
	}
}
