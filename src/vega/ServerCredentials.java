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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.UUID;

import common.CommonUtils;
import spielwitz.biDiServer.ClientConfiguration;

@SuppressWarnings("serial")
class ServerCredentials implements Serializable
{
	static final int PASSWORD_MIN_LENGTH = 4;
	private static final String ACTIVATION_CODE_SEPARATOR = "@";
	private static Object lockObject = new Object();
	
	static String getActivationCode(ClientConfiguration clientConfiguration)
	{
		if (clientConfiguration == null) return null;
		
		if (clientConfiguration.getUserId().contains(ACTIVATION_CODE_SEPARATOR))
		{
			String[] parts = clientConfiguration.getUserId().split(ACTIVATION_CODE_SEPARATOR);
			return parts[1];
		}
		else
		{
			return null;
		}
	}
	static String getCredentialsDisplayName(ClientConfiguration clientConfiguration)
	{
		return getUserId(clientConfiguration) + "@" + clientConfiguration.getUrl() + ":" + clientConfiguration.getPort(); 
	}
	
	static String getUserId(ClientConfiguration clientConfiguration)
	{
		if (clientConfiguration == null) return null;
		
		if (clientConfiguration.getUserId().contains(ACTIVATION_CODE_SEPARATOR))
		{
			String[] parts = clientConfiguration.getUserId().split(ACTIVATION_CODE_SEPARATOR);
			return parts[0];
		}
		else
		{
			return clientConfiguration.getUserId();
		}
	}
	
	static boolean isUserActive(ClientConfiguration clientConfiguration)
	{
		return getActivationCode(clientConfiguration) == null;
	}
	
	static void setActivationCode(ClientConfiguration clientConfiguration, String activationCode)
	{
		clientConfiguration.setUserId(clientConfiguration.getUserId() + ACTIVATION_CODE_SEPARATOR + activationCode);
	}
	
	UUID adminCredentialsSelected;
	boolean connectionActive;
	UUID userCredentialsSelected;
	private String credentialsEncrypted;
	
	private transient byte[] password;
	
	ServerCredentials()
	{
	}
	
	boolean areCredentialsLocked()
	{
		synchronized(lockObject)
		{
			return 
					this.credentialsEncrypted != null &&
					this.password == null;
		}
	}
	
	boolean changePassword(byte[] oldPassword, byte[] newPassword)
	{
		synchronized(lockObject)
		{
			if (this.unlockCredentials(oldPassword))
			{
				Hashtable<UUID, UserData> dict = this.decryptCredentials();
				this.password = newPassword;
				this.encryptCredentials(dict);
				
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	void clear()
	{
		synchronized(lockObject)
		{
			this.password = null;
			this.adminCredentialsSelected = null;
			this.userCredentialsSelected = null;
			this.connectionActive = false;
			this.credentialsEncrypted = null;
		}
	}
	
	boolean containsCredentials()
	{
		synchronized(lockObject)
		{
			return this.credentialsEncrypted != null;
		}
	}
	
	void deleteCredentials(UUID key)
	{
		synchronized(lockObject)
		{
			Hashtable<UUID,UserData> dict = this.decryptCredentials();
			dict.remove(key);
			this.encryptCredentials(dict);
		}
	}
	
	ServerCredentials getClone()
	{
		synchronized(lockObject)
		{
			ServerCredentials clone = (ServerCredentials)CommonUtils.klon(this);
			clone.password = this.password;
			
			return clone;
		}
	}
	
	ArrayList<UUID> getCredentialKeys()
	{
		synchronized(lockObject)
		{
			ArrayList<UUID> credentialKeys = new ArrayList<UUID>();
			credentialKeys.addAll(this.decryptCredentials().keySet());
			return credentialKeys;
		}
	}
	
	ClientConfiguration getCredentials(UUID key)
	{
		synchronized(lockObject)
		{
			UserData userData = this.decryptCredentials().get(key);
			if (userData == null) return null;
			
			return userData.config;
		}
	}
	
	Messages getMessages()
	{
		synchronized(lockObject)
		{
			if (this.password == null) return null;
			if (this.userCredentialsSelected == null) return null;
			
			Hashtable<UUID,UserData> dict = this.decryptCredentials();
			UserData userData = dict.get(this.userCredentialsSelected);
			if (userData == null) return null;
			
			String userId = userData.config.getUserId();
			
			if (userData.messages == null)
			{
				return new Messages(this.userCredentialsSelected, userId);
			}
			else
			{
				return userData.messages;
			}
		}
	}
	
	boolean hasChanges(ServerCredentials other)
	{
		synchronized(lockObject)
		{
			Hashtable<UUID,UserData> thisDict = this.decryptCredentials();
			Hashtable<UUID,UserData> otherDict = other.decryptCredentials();
	
			if (this.connectionActive != other.connectionActive) return true;
			if (!Arrays.equals(this.password, other.password)) return true;
			
			if (this.userCredentialsSelected == null && other.userCredentialsSelected != null) return true;
			if (this.userCredentialsSelected != null && other.userCredentialsSelected == null) return true;
			if (this.userCredentialsSelected != null && other.userCredentialsSelected != null &&
				!this.userCredentialsSelected.equals(other.userCredentialsSelected)) return true;
			
			if (this.adminCredentialsSelected == null && other.adminCredentialsSelected != null) return true;
			if (this.adminCredentialsSelected != null && other.adminCredentialsSelected == null) return true;
			if (this.adminCredentialsSelected != null && other.adminCredentialsSelected != null &&
				!this.adminCredentialsSelected.equals(other.adminCredentialsSelected)) return true;
			
			if (thisDict.size() != otherDict.size()) return true;
			
			for (UUID credentialsKey: thisDict.keySet())
			{
				if (!otherDict.containsKey(credentialsKey)) return true;
				if (!thisDict.get(credentialsKey).config.equals(otherDict.get(credentialsKey).config)) return true;
			}
		}
		
		return false;
	}
	
	void setCredentials(UUID key, ClientConfiguration credentials)
	{
		if (this.password == null) return;
		
		synchronized(lockObject)
		{
			Hashtable<UUID,UserData> dict = this.decryptCredentials();
			UserData userData = dict.get(key);
			
			if (userData == null)
			{
				userData = new UserData();
			}
			
			userData.config = credentials;
			
			dict.put(key, userData);
			this.encryptCredentials(dict); 
		}
	}
	
	void setMessages(Messages messages)
	{
		if (this.password == null) return;
		
		synchronized(lockObject)
		{
			Hashtable<UUID,UserData> dict = this.decryptCredentials();
			UserData userData = dict.get(messages.getCredentialsKey());
			if (userData == null) return;
			
			userData.messages = messages;
			
			dict.put(messages.getCredentialsKey(), userData);
			this.encryptCredentials(dict); 
		}
	}
	
	boolean unlockCredentials(byte[] passwordBytes)
	{
		synchronized(lockObject)
		{
			boolean passwordIsValid = true;
			
			this.password = passwordBytes;
			
			try
			{
				this.decryptCredentials();
			}
			catch (Exception x)
			{
				passwordIsValid = false;
			}
			
			return passwordIsValid;
		}
	}
	
	private Hashtable<UUID,UserData> decryptCredentials()
	{
		if (this.credentialsEncrypted != null)
		{
			ServerCredentialsDict dict = (ServerCredentialsDict) VegaUtils.convertFromBase64(credentialsEncrypted, ServerCredentialsDict.class, password);
			return dict.dict;
		}
		else
		{
			return new Hashtable<UUID,UserData>();
		}
	}
	
	private void encryptCredentials(Hashtable<UUID,UserData> dict)
	{
		if (dict.size() > 0)
		{
			ServerCredentialsDict scd = new ServerCredentialsDict();
			scd.dict = dict;
			this.credentialsEncrypted = VegaUtils.convertToBase64(scd, password);
		}
		else
		{
			this.credentialsEncrypted = null;
		}
	}
	
	private class ServerCredentialsDict
	{
		Hashtable<UUID,UserData> dict;
	}
	
	private class UserData
	{
		ClientConfiguration config;
		Messages messages;
	}
}
