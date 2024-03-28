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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;

import spielwitz.biDiServer.Tuple;

class Messages
{
	private static final String RECIPIENTS_SEPARATOR = ">";
	
	static ArrayList<String> getRecipientsFromRecipientsString(String recipientsString, String ownUserId)
	{
		ArrayList<String> retval = new ArrayList<String>();
		
		if (recipientsString == null)
			return retval;
		
		String[] recipients = recipientsString.split(RECIPIENTS_SEPARATOR);
		
		for (String recipient: recipients)
		{
			if (!recipient.equals(ownUserId))
				retval.add(recipient);
		}
		
		Collections.sort(retval);
		
		return retval;
	}
	
	static String getRecipientsStringFromRecipients(ArrayList<String> recipients, String ownUserId)
	{
		ArrayList<String> recipientsCopy = new ArrayList<String>();
		
		for (String recipient: recipients)
			recipientsCopy.add(recipient);
		
		if (!recipientsCopy.contains(ownUserId))
			recipientsCopy.add(ownUserId);
		
		Collections.sort(recipientsCopy);
		
		StringBuilder sb = new StringBuilder();
		
		for (String recipient: recipientsCopy)
		{
			if (sb.length() > 0)
				sb.append(RECIPIENTS_SEPARATOR);
			
			sb.append(recipient);
		}
		
		return sb.toString();
	}
	
	private UUID credentialsKey;
	private Hashtable<String, ArrayList<Message>> messagesByRecipients;
	
	private HashSet<String> unreadMessages;
	
	private String userId;

	Messages(UUID credentialsKey, String userId)
	{
		this.credentialsKey = credentialsKey;
		this.userId = userId;
		this.messagesByRecipients = new Hashtable<String, ArrayList<Message>>();
		this.unreadMessages = new HashSet<String>();
	}
	
	Tuple<String,Message> addMessage(String sender, ArrayList<String> recipients, long dateCreated, String text)
	{
		String recipientsString = getRecipientsStringFromRecipients(recipients, sender);
		
		ArrayList<Message> messageList = this.messagesByRecipients.get(recipientsString);
		
		if (messageList == null)
		{
			messageList = new ArrayList<Message>();
			this.addRecipientsString(
					recipientsString,
					messageList);
		}
		
		Message message = new Message(sender, dateCreated, text);
		messageList.add(message);
		
		if (!sender.equals(this.userId))
		{
			this.unreadMessages.add(recipientsString);
		}
		
		return new Tuple<String,Message>(recipientsString, message);
	}
	
	void addRecipientsString(String recipientsString, ArrayList<Message> messageList)
	{
		this.messagesByRecipients.put(
				recipientsString,
				messageList);
	}
	
	UUID getCredentialsKey() {
		return credentialsKey;
	}
	
	Hashtable<String, ArrayList<Message>> getMessagesByRecipients() {
		return messagesByRecipients;
	}

	String getUserId()
	{
		return this.userId;
	}

	void removeRecipientsString(String recipientsString)
	{
		this.messagesByRecipients.remove(recipientsString);
	}
	
	protected HashSet<String> getUnreadMessages()
	{
		return unreadMessages;
	}
}
