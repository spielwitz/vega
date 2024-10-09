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

package vegaDisplay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.gson.Gson;

import common.CommonUtils;
import common.VegaResources;

class VegaDisplayConfiguration
{
	private static final String FILE_NAME = "VegaDisplayConfiguration";
	private static final String PROPERTIES_FILE_NAME = "VegaDisplayProperties";
	public static final int SERVER_PORT = 56085;
	private static Gson	serializer = new Gson();
	
	static VegaDisplayConfiguration get()
	{
		VegaDisplayConfiguration config = null;
		
		if (new File(PROPERTIES_FILE_NAME).exists())
		{
			config = getConfigurationFromProperties();
		}
		else
		{
			try (BufferedReader br = new BufferedReader(new FileReader(getFileName())))
			{
				String json = br.readLine();
				config = serializer.fromJson(json, VegaDisplayConfiguration.class);
			} catch (Exception e)
			{
				config = new VegaDisplayConfiguration();
			}
		}
		
        if (config.clientCode == null)
        	config.clientCode = "";
		
		if (config.locale != null)
			VegaResources.setLocale(config.locale);
		
		if (config.port == 0)
			config.port = SERVER_PORT;

		return config;
	}
	private static VegaDisplayConfiguration getConfigurationFromProperties()
	{
		VegaDisplayConfiguration config = new VegaDisplayConfiguration();
		
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
		
		if (properties.containsKey("myIpAddress"))
			config.myIpAddress = properties.getProperty("myIpAddress");
		if (properties.containsKey("language"))
			config.locale = properties.getProperty("language");
		
		new File(PROPERTIES_FILE_NAME).delete();
		
		config.writeToFile();
		
		return config;
	}
	
	private static File getFileName()
	{
		return Paths.get(CommonUtils.getHomeDir(), FILE_NAME).toFile();
	}
	
	private String				myIpAddress;
	private int					port;
	
	private String				locale;
	
	private boolean				firstTimeStart;
	
	private transient String	clientCode;
	
	VegaDisplayConfiguration()
	{
		this.firstTimeStart = true;
		this.port = SERVER_PORT;
	}
	
	String getClientCode()
	{
		return clientCode;
	}

	String getMyIpAddress()
	{
		return myIpAddress;
	}
	
	int getPort()
	{
		return this.port;
	}

	boolean isFirstTimeStart()
	{
		return firstTimeStart;
	}

	void setClientCode(String clientCode)
	{
		this.clientCode = clientCode;
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
