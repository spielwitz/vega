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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.Gson;

import common.VegaResources;

class VegaConfiguration
{
	private static final String PROPERTIES_FILE_NAME = "VegaProperties";
	private static String fileName = "VegaConfiguration";
	private static Gson	serializer = new Gson();
	
	static VegaConfiguration get()
	{
		VegaConfiguration config = null;
		
		if (new File(PROPERTIES_FILE_NAME).exists())
		{
			config = getConfigurationFromProperties();
		}
		else
		{
			try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName))))
			{
				String json = br.readLine();
				config = serializer.fromJson(json, VegaConfiguration.class);
			} catch (Exception e)
			{
				config = new VegaConfiguration();
			}
		}
		
		if (config.locale != null)
			VegaResources.setLocale(config.locale);

		return config;
	}
	@SuppressWarnings("unchecked")
	private static VegaConfiguration getConfigurationFromProperties()
	{
		VegaConfiguration config = new VegaConfiguration();
		
		Reader reader = null;
		Properties properties = new Properties(); 

		try
		{
		  reader = new FileReader(PROPERTIES_FILE_NAME);

		  properties.load( reader );
		}
		catch ( Exception e )
		{
		}
		finally
		{
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
		if (properties.containsKey("lastDir"))
			config.directoryNameLast = properties.getProperty("lastDir");
		
		if (properties.containsKey("emails"))
		{
			String emailBase64 = properties.getProperty("emails");
			config.emailAddresses = 
					(ArrayList<String>) VegaUtils.convertFromBase64(emailBase64, ArrayList.class, null);
		}
		
		if (properties.containsKey("serverAdminCredentials"))
			config.serverAdminCredentialFile = properties.getProperty("serverAdminCredentials");
		
		if (properties.containsKey("serverUserCredentials"))
			config.serverUserCredentialsFile = properties.getProperty("serverUserCredentials");
		
		if (properties.containsKey("serverCommunicationEnabled"))
			config.serverCommunicationEnabled = Boolean.parseBoolean(properties.getProperty("serverCommunicationEnabled"));
		
		if (properties.containsKey("language"))
			config.locale = properties.getProperty("language");
		
		if (properties.containsKey("emailSeparator"))
			config.emailSeparator = properties.getProperty("emailSeparator");
		
		if (properties.containsKey("myIpAddress"))
			config.myIpAddress = properties.getProperty("myIpAddress");
		
		if (properties.containsKey("webserverPort"))
			config.webserverPort = Integer.parseInt(properties.getProperty("webserverPort"));
		
		if (properties.containsKey("clientInactiveWhileEnterMoves"))
			config.clientsInactiveWhileEnterMoves = 
				Boolean.parseBoolean(properties.getProperty("clientInactiveWhileEnterMoves"));
		
		new File(PROPERTIES_FILE_NAME).delete();
		
		config.writeToFile();
		
		return config;
	}
	
	static void setFileName(String name)
	{
		fileName = name;
	}
	
	private String 				directoryNameLast;
	private ArrayList<String> 	emailAddresses;
	private String				serverAdminCredentialFile;
	private String				serverUserCredentialsFile;
	private boolean				serverCommunicationEnabled;
	private String				locale;
	
	private String				emailSeparator;
	private String				myIpAddress;
	
	private int					webserverPort;
	
	private boolean				clientsInactiveWhileEnterMoves;
	
	private boolean				firstTimeStart;
	
	private ServerCredentials	serverCredentials;
	
	VegaConfiguration()
	{
		this.emailAddresses = new ArrayList<String>();
		this.serverCredentials = new ServerCredentials();
		this.firstTimeStart = true;
	}
	
	String getDirectoryNameLast()
	{
		return directoryNameLast;
	}


	ArrayList<String> getEmailAddresses()
	{
		ArrayList<String> retval = new ArrayList<String>();
		
		for (String emailAddress: this.emailAddresses)
			retval.add(emailAddress);
			
		return retval;
	}


	String getEmailSeparator()
	{
		return emailSeparator;
	}


	String getMyIpAddress()
	{
		return myIpAddress;
	}


	String getServerAdminCredentialFile()
	{
		return serverAdminCredentialFile;
	}


	String getServerUserCredentialsFile()
	{
		return serverUserCredentialsFile;
	}


	int getWebserverPort()
	{
		return webserverPort;
	}


	boolean isClientsInactiveWhileEnterMoves()
	{
		return clientsInactiveWhileEnterMoves;
	}


	boolean isFirstTimeStart()
	{
		return firstTimeStart;
	}


	boolean isServerCommunicationEnabled()
	{
		return serverCommunicationEnabled;
	}


	void setClientsInactiveWhileEnterMoves(boolean clientsInactiveWhileEnterMoves)
	{
		this.clientsInactiveWhileEnterMoves = clientsInactiveWhileEnterMoves;
		this.writeToFile();
	}


	void setDirectoryNameLast(String directoryNameLast)
	{
		this.directoryNameLast = directoryNameLast;
		this.writeToFile();
	}


	void setEmailAddresses(ArrayList<String> emailAddresses)
	{
		this.emailAddresses = emailAddresses;
		this.writeToFile();
	}


	void setEmailSeparator(String emailSeparator)
	{
		this.emailSeparator = emailSeparator;
		this.writeToFile();
	}


	void setFirstTimeStart(boolean firstTimeStart)
	{
		this.firstTimeStart = firstTimeStart;
		this.writeToFile();
	}


	void setLocale(String locale)
	{
		this.locale = locale;
		this.writeToFile();
	}


	void setMyIpAddress(String myIpAddress)
	{
		this.myIpAddress = myIpAddress;
		this.writeToFile();
	}


	void setServerAdminCredentialFile(String serverAdminCredentialFile)
	{
		this.serverAdminCredentialFile = serverAdminCredentialFile;
		this.writeToFile();
	}


	void setServerCommunicationEnabled(boolean serverCommunicationEnabled)
	{
		this.serverCommunicationEnabled = serverCommunicationEnabled;
		this.writeToFile();
	}


	void setServerUserCredentialsFile(String serverUserCredentialsFile)
	{
		this.serverUserCredentialsFile = serverUserCredentialsFile;
		this.writeToFile();
	}

	void setWebserverPort(int webserverPort)
	{
		this.webserverPort = webserverPort;
		this.writeToFile();
	}
	
	ServerCredentials getServerCredentials()
	{
		return serverCredentials;
	}
	void setServerCredentials(ServerCredentials serverCredentials)
	{
		this.serverCredentials = serverCredentials;
		this.writeToFile();
	}
	
	private boolean writeToFile()
	{
		boolean success = true;
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName)))
		{
			bw.write(serializer.toJson(this));			
		} catch (IOException e)
		{
			e.printStackTrace();
			success = false;
		}
		
		return success;
	}
}
