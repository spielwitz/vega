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
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import common.VegaResources;
import spielwitz.biDiServer.Tuple;
import spielwitz.biDiServer.User;
import uiBaseControls.Button;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IIconLabelListener;
import uiBaseControls.IListListener;
import uiBaseControls.IconLabel;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.Panel;
import uiBaseControls.TabbedPane;
import uiBaseControls.TextArea;

@SuppressWarnings("serial")
class MessengerJDialog extends Dialog implements ChangeListener
{
	private IMessengerCallback callback;
	private Hashtable<String, MessagePanel> messagePanelsByRecipientStrings;
	
	private TabbedPane tabPane;
	private boolean tabPanelEventsDisbled;
	
	MessengerJDialog(Messages messages, IMessengerCallback callback)
	{
		super((Component)callback, VegaResources.Messenger(false), new BorderLayout(10, 10));
		
		this.callback = callback;
		this.messagePanelsByRecipientStrings = new Hashtable<String, MessagePanel>();
		
		this.setModal(false);
		this.setAlwaysOnTop(true);
		
		// --------------------
		this.tabPane = new TabbedPane();
		tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		
		MessagePanel messagePanelAddNew = new MessagePanel(null, null, 0);
		tabPane.add("+", messagePanelAddNew);

		for (String recipientString: messages.getMessagesByRecipients().keySet())
		{
			this.addMessagePanel(recipientString);
		}
		
		// ----
		this.addToInnerPanel(tabPane, BorderLayout.CENTER);
		
		this.pack();
		this.setLocationRelativeTo((Component)callback);
		
		this.tabPane.setSelectedIndex(this.tabPane.getTabCount() - 1);
		this.onTabSwitch();
		
		this.tabPane.addChangeListener(this);

	}
	
	public void onNewMessageReceived(Tuple<String,Message> t)
	{
		MessagePanel messagePanel = null;
		
		if (!this.messagePanelsByRecipientStrings.containsKey(t.getE1()))
		{
			messagePanel = this.addMessagePanel(t.getE1());
		}
		else
		{
			messagePanel = this.messagePanelsByRecipientStrings.get(t.getE1());			
			messagePanel.addMessageToTextArea(t.getE2());
		}

		messagePanel.setNewMessageIndicator(!messagePanel.isVisiblePanel());
	}
	
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == this.tabPane)
		{
			this.onTabSwitch();
		}
	}
	
	private MessagePanel addMessagePanel(String recipientsString)
	{
		synchronized(this.messagePanelsByRecipientStrings)
		{
			this.tabPanelEventsDisbled = true;
			
			this.callback.addRecipientsString(recipientsString);
			
			int tabCount = this.tabPane.getTabCount();
			
			MessagePanel messagePanel = 
					new MessagePanel(
							recipientsString, 
							this.callback.getMessagesByRecipientsString(recipientsString),
							tabCount);
			
			this.messagePanelsByRecipientStrings.put(
					recipientsString,
					messagePanel);
			
			ArrayList<String> recipients = 
					Messages.getRecipientsFromRecipientsString(
							recipientsString, 
							this.callback.getClientUserIdForMessenger());
			
			int maxNames = 3;
			
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < Math.min(recipients.size(), maxNames); i++)
			{
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(recipients.get(i));
			}
			
			if (recipients.size() > maxNames)
			{
				sb.append(" (+" + (recipients.size() - maxNames) + ")");
			}
			
			tabPane.add(sb.toString(), messagePanel);
			tabPane.setTabComponentAt(tabCount, new TabLabelComponent(tabPane, recipientsString, tabCount));
			
			messagePanel.setNewMessageIndicator(this.callback.hasUnreadMessages(recipientsString));
			
			this.tabPanelEventsDisbled = false;
			
			this.pack();
			
			return messagePanel;
		}
	}
	
	private void addNewButtonClicked(ArrayList<String> selectedUserIds)
	{
		ArrayList<User> users = this.callback.getUsersForMessenger();
		
		ArrayList<String> userIds = new ArrayList<String>();
		
		for (User user: users)
		{
			if (!user.getId().equals(this.callback.getClientUserIdForMessenger()))
			{
				userIds.add(user.getId());
			}
		}
		
		Collections.sort(userIds);
		
		RecipientsSelector dlg = new RecipientsSelector(this, selectedUserIds, userIds);
		dlg.setVisible(true);
		
		if (dlg.ok && dlg.recipients.size() > 0)
		{
			String recipientsString = Messages.getRecipientsStringFromRecipients(dlg.recipients, this.callback.getClientUserIdForMessenger());
			
			MessagePanel messagePanel = this.messagePanelsByRecipientStrings.get(recipientsString);
			
			if (messagePanel == null)
			{
				messagePanel = this.addMessagePanel(recipientsString);
			}
			
			this.tabPane.setSelectedIndex(messagePanel.tabIndex);
		}
	}
	
	private void closeMessagePanel(String recipientsString, int tabIndex)
	{
		synchronized(this.messagePanelsByRecipientStrings)
		{
			this.tabPanelEventsDisbled = true;
			
			this.callback.removeRecipientsString(recipientsString);
			this.messagePanelsByRecipientStrings.remove(recipientsString);
			
			Object[] messagePanels = this.messagePanelsByRecipientStrings.values().stream().filter(p -> p.tabIndex > tabIndex).toArray();
			
			for (Object messagePanel: messagePanels)
			{
				((MessagePanel)messagePanel).tabIndex--;
			}
			
			this.tabPane.remove(tabIndex);
			
			this.tabPanelEventsDisbled = false;
		}
	}
	
	private void onTabSwitch()
	{
		if (this.tabPanelEventsDisbled)
			return;
		
		int tabIndex = this.tabPane.getSelectedIndex();
		
		if (tabIndex != 0)
		{
			Optional<MessagePanel> messagePanel = this.messagePanelsByRecipientStrings.values().stream().filter(p -> p.tabIndex == tabIndex).findFirst();
			
			if (messagePanel.isPresent())
			{
				messagePanel.get().setNewMessageIndicator(false);
			}
		}
	}
	
	private class MessagePanel extends Panel implements IButtonListener, DocumentListener
	{
		private static final int MAX_CHARACTERS_COUNT = 1000;
		private TextArea taMessages;
		private TextArea taComposeMessage;
		private Button butTo;
		private Button butSend;
		
		private Label labCharactersLeft;
		private ArrayList<String> recipients;
		private String recipientsString;
		
		private int tabIndex;
		
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
				addNewButtonClicked(this.recipients);
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
			return tabPane.getSelectedIndex() == this.tabIndex;
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			this.onComposeMessageChanged();
		}

		public void setNewMessageIndicator(boolean newMessages)
		{
			TabLabelComponent tabLabel = (TabLabelComponent)tabPane.getTabComponentAt(this.tabIndex);
			
			tabLabel.setNewMessageIndicatorVisible(newMessages);
			
			callback.setMessagesByRecipientsRead(this.recipientsString, !newMessages);
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
		private List listRecipients;
		private Button butAbort;
		private Button butOk;
		
		private ArrayList<String> userIds;
		boolean ok = false;
		ArrayList<String> recipients = new ArrayList<String>(); 
		
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
	}
	
	private class TabLabelComponent extends JPanel implements IIconLabelListener
	{
		private int tabIndex;
		private String recipientsString;
		private NewMessageIndicator newMessageIndicator;
		
		TabLabelComponent(TabbedPane pane, String recipientsString, int tabIndex)
		{
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.setOpaque(false);
			
			this.tabIndex = tabIndex;
			this.recipientsString = recipientsString;
			
			this.newMessageIndicator = new NewMessageIndicator();
			this.add(newMessageIndicator);
			
	        JLabel label = new JLabel() {
	            public String getText() {
	                int i = pane.indexOfTabComponent(TabLabelComponent.this);
	                if (i != -1) {
	                    return pane.getTitleAt(i);
	                }
	                return null;
	            }
	        };
	        
	        this.add(label);

	        ImageIcon icon = new ImageIcon (ClassLoader.getSystemResource("cancel.png"));
	        IconLabel iconLabel = new IconLabel(icon, this);
	        this.add(iconLabel);
		}

		@Override
		public void iconLabelClicked(IconLabel source)
		{
			closeMessagePanel(this.recipientsString, this.tabIndex);
		}
		
		private void setNewMessageIndicatorVisible(boolean visible)
		{
			this.newMessageIndicator.setVisible(visible);
		}
		
		private class NewMessageIndicator extends JPanel
		{
			NewMessageIndicator()
			{
				this.setPreferredSize(new Dimension(10, 10));
			}
			
			@Override
			public void paint(Graphics g)
			{
				g.setColor(Color.red);
				Dimension dim = this.getSize();
				g.fillOval(2, 2, dim.width-5, dim.height-5);
			}
		}
	}
}
