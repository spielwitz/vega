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
	UUID userCredentialsSelected;
	private Hashtable<UUID,String> credentialsEncrypted;
	private transient String password;
	
	ServerCredentials()
	{
		this.credentialsEncrypted = new Hashtable<UUID,String>();
	}
	
	boolean areCredentialsLocked()
	{
		return this.password == null;
	}
	
	boolean changePassword(String oldPassword, String newPassword)
	{
		if (this.unlockCredentials(oldPassword))
		{
			Hashtable<UUID, ClientConfiguration> credentials = new Hashtable<UUID, ClientConfiguration>();
			
			for (UUID credentialKey: this.credentialsEncrypted.keySet())
			{
				credentials.put(credentialKey, this.getCredentials(credentialKey)); 
			}
			
			this.password = newPassword;
			this.credentialsEncrypted.clear();
			
			for (UUID credentialKey: credentials.keySet())
			{
				this.setCredentials(credentialKey, credentials.get(credentialKey));
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	boolean credentialsExist(UUID key)
	{
		return this.credentialsEncrypted.containsKey(key);
	}
	
	void deleteCredentials(UUID key)
	{
		this.credentialsEncrypted.remove(key);
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
		credentialKeys.addAll(this.credentialsEncrypted.keySet());
		return credentialKeys;
	}
	
	ClientConfiguration getCredentials(UUID key)
	{
		return 
				this.password == null ?
						null :
						(ClientConfiguration) VegaUtils.convertFromBase64(
							this.credentialsEncrypted.get(key), 
							ClientConfiguration.class, 
							this.password);
	}
	
	boolean hasChanges(ServerCredentials other)
	{
		if (this.credentialsEncrypted.size() != other.credentialsEncrypted.size()) return true;
		
		for (UUID credentialsKey: this.credentialsEncrypted.keySet())
		{
			if (!other.credentialsExist(credentialsKey)) return true;
			if (!this.credentialsEncrypted.get(credentialsKey).equals(other.credentialsEncrypted.get(credentialsKey))) return true;
		}
		
		return false;
	}
	
	void setCredentials(UUID key, ClientConfiguration credentials)
	{
		if (this.password == null) return;
		
		this.credentialsEncrypted.put(
					key, 
					VegaUtils.convertToBase64(credentials, this.password));
	}
	
	boolean unlockCredentials(String password)
	{
		boolean passwordIsValid = true;
		
		this.password = password;
		
		for (UUID credentialKey: this.credentialsEncrypted.keySet())
		{
			ClientConfiguration clientConfiguration = null;
			
			try
			{
				clientConfiguration = this.getCredentials(credentialKey); 
			}
			catch (Exception x)
			{
			}
			
			if (clientConfiguration == null)
			{
				passwordIsValid = false;
				break;
			}
		}
		
		return passwordIsValid;
	}
}
