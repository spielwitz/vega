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
import java.util.Hashtable;
import java.util.UUID;

import common.CommonUtils;
import spielwitz.biDiServer.ClientConfiguration;

@SuppressWarnings("serial")
class ServerCredentials implements Serializable
{
	static final int PASSWORD_MIN_LENGTH = 4;
	private static final String ACTIVATION_CODE_SEPARATOR = "@";
	
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
	
	static void removeActivationCode(ClientConfiguration clientConfiguration)
	{
		int pos = clientConfiguration.getUserId().indexOf(ACTIVATION_CODE_SEPARATOR);
		
		if (pos >= 0)
		{
			clientConfiguration.setUserId(clientConfiguration.getUserId().substring(0, pos));
		}
	}
	
	static void setActivationCode(ClientConfiguration clientConfiguration, String activationCode)
	{
		clientConfiguration.setUserId(clientConfiguration.getUserId() + ACTIVATION_CODE_SEPARATOR + activationCode);
	}
	
	UUID adminCredentialsSelected;
	boolean connectionActive;
	UUID userCredentialsSelected;
	private String credentialsEncrypted;
	
	private transient String password;
	
	ServerCredentials()
	{
	}
	
	boolean areCredentialsLocked()
	{
		return this.password == null;
	}
	
	boolean changePassword(String oldPassword, String newPassword)
	{
		if (this.unlockCredentials(oldPassword))
		{
			Hashtable<UUID, ClientConfiguration> dict = this.decryptCredentials();
			this.password = newPassword;
			this.encryptCredentials(dict);
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	boolean credentialsExist(UUID key)
	{
		return this.decryptCredentials().containsKey(key);
	}
	
	void deleteCredentials(UUID key)
	{
		Hashtable<UUID,ClientConfiguration> dict = this.decryptCredentials();
		dict.remove(key);
		this.encryptCredentials(dict);
	}
	
	ServerCredentials getClone()
	{
		ServerCredentials clone = (ServerCredentials)CommonUtils.klon(this);
		clone.password = this.password;
		
		return clone;
	}
	
	ArrayList<UUID> getCredentialKeys()
	{
		ArrayList<UUID> credentialKeys = new ArrayList<UUID>();
		credentialKeys.addAll(this.decryptCredentials().keySet());
		return credentialKeys;
	}
	
	ClientConfiguration getCredentials(UUID key)
	{
		return this.decryptCredentials().get(key);
	}
	
	boolean hasChanges(ServerCredentials other)
	{
		Hashtable<UUID,ClientConfiguration> thisDict = this.decryptCredentials();
		Hashtable<UUID,ClientConfiguration> otherDict = other.decryptCredentials();
		
		if (thisDict.size() != otherDict.size()) return true;
		
		for (UUID credentialsKey: thisDict.keySet())
		{
			if (!otherDict.containsKey(credentialsKey)) return true;
			if (!thisDict.get(credentialsKey).equals(otherDict.get(credentialsKey))) return true;
			if (this.connectionActive != other.connectionActive) return true;
			
			if (this.userCredentialsSelected == null && other.userCredentialsSelected != null) return true;
			if (this.userCredentialsSelected != null && other.userCredentialsSelected == null) return true;
			if (this.userCredentialsSelected != null && other.userCredentialsSelected != null &&
				!this.userCredentialsSelected.equals(other.userCredentialsSelected)) return true;
			
			if (this.adminCredentialsSelected == null && other.adminCredentialsSelected != null) return true;
			if (this.adminCredentialsSelected != null && other.adminCredentialsSelected == null) return true;
			if (this.adminCredentialsSelected != null && other.adminCredentialsSelected != null &&
				!this.adminCredentialsSelected.equals(other.adminCredentialsSelected)) return true;
		}
		
		return false;
	}
	
	void setCredentials(UUID key, ClientConfiguration credentials)
	{
		if (this.password == null) return;
		
		Hashtable<UUID,ClientConfiguration> dict = this.decryptCredentials();
		dict.put(key, credentials);
		this.encryptCredentials(dict); 
	}
	
	boolean unlockCredentials(String password)
	{
		boolean passwordIsValid = true;
		
		this.password = password;
		
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
	
	private Hashtable<UUID,ClientConfiguration> decryptCredentials()
	{
		if (this.credentialsEncrypted != null)
		{
			ServerCredentialsDict dict = (ServerCredentialsDict) VegaUtils.convertFromBase64(credentialsEncrypted, ServerCredentialsDict.class, password);
			return dict.dict;
		}
		else
		{
			return new Hashtable<UUID,ClientConfiguration>();
		}
	}
	
	private void encryptCredentials(Hashtable<UUID,ClientConfiguration> dict)
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
	
	class ServerCredentialsDict
	{
		Hashtable<UUID,ClientConfiguration> dict;
	}
}
