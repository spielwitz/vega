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

import common.Game;
import vegaDisplayCommon.DataTransferLib;
import vegaDisplayCommon.VegaDisplayConnectionRequest;
import vegaDisplayCommon.VegaDisplayConnectionResponse;
import vegaDisplayCommon.VegaDisplayScreenContent;

class VegaDisplayClient extends Thread
{
	private VegaDisplayConfiguration config;
	private boolean enabled;
	private VegaDisplay parent;
	
	VegaDisplayClient(VegaDisplay parent, VegaDisplayConfiguration config)
	{
		this.parent = parent;
		this.config = config;
	}
	
	public void run()
	{
		Socket socket = null;
		OutputStream out = null;
		
		try {
			socket = new Socket();
			socket.connect(
					new InetSocketAddress(this.config.getServerIpAddress(), this.config.getServerPort()), 
					10000);
			
			out = socket.getOutputStream();
			
			boolean success = DataTransferLib.sendObjectAesEncrypted(
					out, 
					new VegaDisplayConnectionRequest(Game.BUILD, this.config.getMyName()), 
					this.config.getClientCode());
			
			if (success)
			{
				DataInputStream in = new DataInputStream(socket.getInputStream());
				
				VegaDisplayConnectionResponse response = 
						(VegaDisplayConnectionResponse)DataTransferLib.receiveObjectAesEncrypted(
								in, 
								this.config.getClientCode(),
								VegaDisplayConnectionResponse.class);
				
				if (response != null && response.isSuccess())
				{
					VegaDisplayScreenContent screenContent = 
							(VegaDisplayScreenContent)DataTransferLib.receiveObjectAesEncrypted(in, this.config.getClientCode(), VegaDisplayScreenContent.class);
					
					if (screenContent != null)
					{
						this.enabled = true;
						this.parent.updateScreen(screenContent.getScreenContent());
						
						while(true)
						{
							screenContent = 
									(VegaDisplayScreenContent)DataTransferLib.receiveObjectAesEncrypted(in, this.config.getClientCode(), VegaDisplayScreenContent.class);
							if (screenContent == null) break;
							if (screenContent.isKeepAlive()) continue;
							
							this.parent.updateScreen(screenContent.getScreenContent());
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
		}
		
		this.enabled = false;

		try
		{
			socket.close();
		} catch (IOException e)
		{
		}
	}
	
	boolean isEnabled()
	{
		return enabled;
	}
}
