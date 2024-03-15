package vega;

import java.util.Hashtable;

import spielwitz.biDiServer.ClientConfiguration;

class ServerCredentials
{
	private static final String ACTIVATION_CODE_SEPARATOR = "@";
	
	private Hashtable<String,String> credentialsEncrypted;
	String userCredentialsSelected;
	String adminCredentialsSelected;
	
	ServerCredentials()
	{
		this.credentialsEncrypted = new Hashtable<String,String>();
		this.userCredentialsSelected = "";
		this.adminCredentialsSelected = "";
	}
	
	boolean credentialsExist(String key)
	{
		return this.credentialsEncrypted.containsKey(key);
	}
	
	ClientConfiguration getCredentials(String key, String password)
	{
		return (ClientConfiguration) VegaUtils.convertFromBase64(
					this.credentialsEncrypted.get(key), 
					ClientConfiguration.class, 
					password);
	}
	
	void setCredentials(String key, ClientConfiguration credentials, String password)
	{
		this.credentialsEncrypted.put(
					key, 
					VegaUtils.convertToBase64(credentials, password));
	}
	
	void deleteCredentials(String key)
	{
		this.credentialsEncrypted.remove(key);
	}
	
	static String getCredentialKey(ClientConfiguration clientConfiguration)
	{
		return getUserId(clientConfiguration) + "@" + clientConfiguration.getUrl() + ":" + clientConfiguration.getPort(); 
	}
	
	static void setActivationCode(ClientConfiguration clientConfiguration, String activationCode)
	{
		clientConfiguration.setUserId(clientConfiguration.getUserId() + ACTIVATION_CODE_SEPARATOR + activationCode);
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
	
	static void removeActivationCode(ClientConfiguration clientConfiguration)
	{
		int pos = clientConfiguration.getUserId().indexOf(ACTIVATION_CODE_SEPARATOR);
		
		if (pos >= 0)
		{
			clientConfiguration.setUserId(clientConfiguration.getUserId().substring(0, pos));
		}
			
	}
}
