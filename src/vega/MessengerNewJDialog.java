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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.UUID;

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
	private static final Color selectionBackground = (Color) UIManager.get("List.selectionBackground");
	
	private Button butAdd;
	private Button butDelete;
	
	private MessagePanel panMessage;
	
	private IMessengerCallback callback;
	
	private List listRecipients;
	private ArrayList<ListItem> listRecipientsModel;
	
	MessengerNewJDialog(Messages messages, IMessengerCallback callback)
	{
		super((Component)callback, VegaResources.Messenger(false), new BorderLayout(0, 10));
		
		this.callback = callback;
		
		this.setModal(false);
		this.setAlwaysOnTop(true);
		
		PanelWithInsets panUsersList = new PanelWithInsets(new BorderLayout(10, 10));
				
		this.listRecipientsModel = new ArrayList<ListItem>();
		int widthList = CommonUtils.round(1.2 * 
				this.getFontMetrics(this.getFont()).stringWidth(new String(new char[Player.PLAYER_NAME_LENGTH_MAX]).replace("\0", "H")));
		this.listRecipients = new List(this, this.listRecipientsModel);
		
		for (String recipientString: messages.getMessagesByRecipients().keySet())
		{
			this.addNewListItem(recipientString, messages.getMessagesByRecipients().get(recipientString));
		}
		
		this.sortListItems();
		this.listRecipients.refreshListItems(this.listRecipientsModel);
		
		this.listRecipients.setPreferredSize(new Dimension(widthList, 200));
		this.listRecipients.setCellRenderer(new RecipientsListCellRenderer());
		
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
		
		this.panMessage = new MessagePanel();
		this.addToInnerPanel(this.panMessage, BorderLayout.CENTER);
		
		this.pack();
		this.setResizable(true);
		this.setLocationRelativeTo((Component)callback);
	}
	
	private void addNewListItem(String recipientsString, ArrayList<Message> messages)
	{
		synchronized(this.listRecipientsModel)
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
			
			this.listRecipientsModel.add(newListItem);
		}
	}
	
	private void sortListItems()
	{
		String[] dateTimeStrings = new String[this.listRecipientsModel.size()];
		
		for (int i = 0; i < this.listRecipientsModel.size(); i++)
		{
			MessagePanelContent content = (MessagePanelContent) this.listRecipientsModel.get(i).getHandle();
			dateTimeStrings[i] = Long.toString(content.createDateTime);
		}
		
		int[] seq = CommonUtils.sortList(dateTimeStrings, true);
		
		ArrayList<ListItem> listNew = new ArrayList<ListItem>(this.listRecipientsModel.size());
		
		for (int i = 0; i < this.listRecipientsModel.size(); i++)
		{
			listNew.add(this.listRecipientsModel.get(seq[i]));
		}
		
		this.listRecipientsModel = listNew;
	}
	
	private int getListIndexByRecipientsString(String recipientsString)
	{
		if (recipientsString == null) return -1;
		
		int index = -1;
		
		for (int i = 0; i < this.listRecipientsModel.size(); i++)
		{
			MessagePanelContent content = (MessagePanelContent) this.listRecipientsModel.get(i).getHandle();
			
			if (content.recipientsString.equals(recipientsString))
			{
				index = i;
				break;
			}
		}
		
		return index;
	}

	@Override
	public void buttonClicked(Button source) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount) 
	{
		// TODO Auto-generated method stub
		
	}

	public void onNewMessageReceived(Tuple<String,Message> t)
	{
//		String recipientsString = t.getE1();
//		int index = this.getListIndexByRecipientsString(recipientsString);
//		
//		if (index < 0)
//		{
//			this.addListItem(recipientsString);
//			index = 0;
//		}
		
		
		
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
	
//	private void appendMessage(int index, Message message)
//	{
//		ListItem listItem = this.listRecipientsModel.get(index);
//		
//		MessagePanelContent content = (MessagePanelContent)listItem.getHandle();
//		
//		StringBuilder sb = new StringBuilder(content.messages);
//		
//		if (sb.length() > 0)
//		{
//			sb.append("\n\n");
//		}
//		
//		sb.append("--- " + message.getSender());
//		
//		sb.append(" " + VegaUtils.formatDateTimeString(
//						VegaUtils.convertMillisecondsToString(message.getDateCreated())));
//		
//		sb.append(" ---\n" + message.getText());
//	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	private class MessagePanel extends PanelWithInsets implements IButtonListener, DocumentListener
	{
		private static final int MAX_CHARACTERS_COUNT = 1000;
		
		private TextArea taMessages;
		private TextArea taRecipients;
		private TextArea taComposeMessage;
		
		private Label labCharactersLeft;
		
		private Button butSend;
		
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
			
			this.labCharactersLeft = new Label(VegaResources.MessengerCharactersLeft(false, Integer.toString(MAX_CHARACTERS_COUNT)));
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
				for (String recipient: recipients)
				{
					if (sb.length() > 0)
						sb.append(", ");
					sb.append(recipient);
				}
				
				this.taRecipients.setText(sb.toString());
				this.taMessages.setText(content.messages);
				this.taComposeMessage.setText(content.composeMessage);
			}
			else
			{
				this.taRecipients.setText("To/From: ");
				this.taMessages.setText("");
				this.taComposeMessage.setText("");
			}
		}

		@Override
		public void buttonClicked(Button source)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub
			
		}
	}

	private class RecipientsListCellRenderer extends JPanel implements ListCellRenderer<String>
	{
		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus)
		{
			if (index < 0) return null;
			
			boolean unread = index == 0;
			
			ListItem listItem = listRecipientsModel.get(index);
			String[] lines = listItem.getDisplayString().split("\n");
			
			Color backgroundColor =
					isSelected ?
							selectionBackground :
								index % 2 == 0 ?
										new Color(30, 30, 30) :
										new Color(50, 50, 50);
			
			PanelWithInsets panel = null;
			
			if (unread)
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
	
	private class MessagePanelContent implements Comparator<MessagePanelContent> 
	{
		private String recipientsString;
		private String messages;
		private String composeMessage;
		private boolean hasUnreadMessages;
		private long createDateTime;
		
		private MessagePanelContent() {}
		
		private MessagePanelContent(String recipientString)
		{
			super();
			this.recipientsString = recipientString;
			this.messages = "";
			this.composeMessage = "";
			this.hasUnreadMessages = false;
			this.createDateTime = 0;
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
			this.createDateTime = message.getDateCreated();
			this.hasUnreadMessages = hasUnreadMessages;
		}
	}
}
