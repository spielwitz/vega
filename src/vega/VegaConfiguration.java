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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.Gson;

import common.CommonUtils;
import common.VegaResources;
import spielwitz.biDiServer.ClientConfiguration;

class VegaConfiguration
{
	private static String fileName = "VegaConfiguration";
	private static final String PROPERTIES_FILE_NAME = "VegaProperties";
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
			try (BufferedReader br = new BufferedReader(new FileReader(getFileName())))
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
	static void setFileName(String name)
	{
		fileName = name;
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
	
	private boolean				clientsInactiveWhileEnterMoves;
	private String 				directoryNameLast;
	private ArrayList<String> 	emailAddresses;
	
	private String				emailSeparator;
	private boolean				firstTimeStart;
	
	private String				locale;
	private String				myIpAddress;
	private ServerCredentials	serverCredentials;
	
	private int					webserverPort;
	
	VegaConfiguration()
	{
		this.emailAddresses = new ArrayList<String>();
		this.serverCredentials = new ServerCredentials();
		this.firstTimeStart = true;
	}
	
	ClientConfiguration getClientConfiguration()
	{
		if (this.serverCredentials != null &&
			!this.serverCredentials.areCredentialsLocked() &&
			this.serverCredentials.connectionActive &&
			this.serverCredentials.userCredentialsSelected != null)
		{
			return this.serverCredentials.getCredentials(this.serverCredentials.userCredentialsSelected);
		}
		else
		{
			return null;
		}
	}
	
	private static File getFileName()
	{
		return Paths.get(CommonUtils.getHomeDir(), fileName).toFile();
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

	Messages getMessages()
	{
		if (this.serverCredentials == null) return null;
		if (this.serverCredentials.areCredentialsLocked()) return null;
		
		return this.serverCredentials.getMessages();
	}

	String getMyIpAddress()
	{
		return myIpAddress;
	}

	ServerCredentials getServerCredentials()
	{
		return serverCredentials;
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
		return this.serverCredentials != null &&
			   this.serverCredentials.connectionActive;
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
	
	void setMessages(Messages messages)
	{
		if (this.serverCredentials == null) return;
		if (this.serverCredentials.areCredentialsLocked()) return;
		
		this.serverCredentials.setMessages(messages);
		this.writeToFile();
	}
	
	void setMyIpAddress(String myIpAddress)
	{
		this.myIpAddress = myIpAddress;
		this.writeToFile();
	}
	
	void setServerCredentials(ServerCredentials serverCredentials)
	{
		this.serverCredentials = serverCredentials;
		this.writeToFile();
	}
	
	void setWebserverPort(int webserverPort)
	{
		this.webserverPort = webserverPort;
		this.writeToFile();
	}
	
	private boolean writeToFile()
	{
		boolean success = true;
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFileName())))
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
