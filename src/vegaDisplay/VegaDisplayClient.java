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

package vegaDisplay;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.CommonUtils;
import common.Game;
import common.VegaResources;
import vegaDisplayCommon.DataTransferLib;
import vegaDisplayCommon.VegaDisplayConnectionRequest;
import vegaDisplayCommon.VegaDisplayConnectionResponse;
import vegaDisplayCommon.VegaDisplayScreenContent;

class VegaDisplayClient extends Thread
{
	private VegaDisplayConfiguration config;
	private boolean enabled;
	private DataInputStream in;
	
	private VegaDisplay parent;
	private Socket socket;
		
	VegaDisplayClient(VegaDisplay parent, VegaDisplayConfiguration config)
	{
		this.parent = parent;
		this.config = config;
	}
	
	public void run()
	{
		while(true)
		{
			VegaDisplayScreenContent screenContent = 
					(VegaDisplayScreenContent)DataTransferLib.receiveObjectAesEncrypted(in, this.config.getClientCode(), VegaDisplayScreenContent.class);
			if (screenContent == null) break;
			if (screenContent.isKeepAlive()) continue;
			
			this.parent.updateScreen(screenContent.getScreenContent());
		}
		
		this.enabled = false;
		
		try
		{
			socket.close();
		} catch (IOException e)
		{
		}
	}
	
	String getServerIpAddress()
	{
		return this.config.getServerIpAddress();
	}
	
	int getServerPort()
	{
		return this.config.getServerPort();
	}
	
	VegaDisplayClientStartResult init()
	{
		OutputStream out = null;
		String errorMsg = "";
		boolean success = false;
		
		try {
			socket = new Socket();
			socket.connect(
					new InetSocketAddress(this.config.getServerIpAddress(), this.config.getServerPort()), 
					10000);
			
			out = socket.getOutputStream();
			
			if (DataTransferLib.sendObjectAesEncrypted(
					out, 
					new VegaDisplayConnectionRequest(Game.BUILD, this.config.getMyName()), 
					this.config.getClientCode()))
			{
				this.in = new DataInputStream(socket.getInputStream());
				
				VegaDisplayConnectionResponse response = 
						(VegaDisplayConnectionResponse)DataTransferLib.receiveObjectAesEncrypted(
								this.in, 
								this.config.getClientCode(),
								VegaDisplayConnectionResponse.class);
				
				if (response != null)
				{
					if (response.isSuccess())
					{
						if (CommonUtils.areBuildsCompatible(response.getServerBuild()))
						{
							VegaDisplayScreenContent screenContent = 
									(VegaDisplayScreenContent)DataTransferLib.receiveObjectAesEncrypted(
											this.in, 
											this.config.getClientCode(), 
											VegaDisplayScreenContent.class);
							
							if (screenContent != null)
							{
								this.enabled = true;
								this.parent.updateScreen(screenContent.getScreenContent());
								
								success = true;
								this.start();
							}
							else
							{
								errorMsg = VegaResources.InitialScreenNotReceived(true);
							}
						}
						else
						{
							errorMsg = VegaResources.ClientServerDifferentBuilds(true);
						}
					}
					else
					{
						errorMsg = response.getErrorMessage();
					}
				}
				else
				{
					errorMsg = VegaResources.ConnectionErrorSecurityCode(true);
				}
			}
			else
			{
				errorMsg = VegaResources.ConnectionError(true);
			}
		}
		catch (Exception e) 
		{
			success = false;
			errorMsg = e.getMessage();
		}
		
		if (!success)
		{
			this.enabled = false;
	
			try
			{
				socket.close();
			} catch (IOException e)
			{
			}
		}

		return new VegaDisplayClientStartResult(success, errorMsg);
	}
	
	boolean isEnabled()
	{
		return enabled;
	}
}
