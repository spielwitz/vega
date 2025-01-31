package vega;

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

public class VegaDisplayClientTester
{

	public static void main(String[] args)
	{
		Socket kkSocket = null;
		OutputStream out = null;
		String securityCode = "1234";
		
		try {
			kkSocket = new Socket();
			kkSocket.connect(
					new InetSocketAddress("127.0.0.1", 55663), 
					10000);
			
			out = kkSocket.getOutputStream();
			
			boolean success = DataTransferLib.sendObjectAesEncrypted(
					out, 
					new VegaDisplayConnectionRequest(Game.BUILD, "Michael"), 
					securityCode);
			
			if (success)
			{
				DataInputStream in = new DataInputStream(kkSocket.getInputStream());
				
				VegaDisplayConnectionResponse response = 
						(VegaDisplayConnectionResponse)DataTransferLib.receiveObjectAesEncrypted(
								in, 
								securityCode,
								VegaDisplayConnectionResponse.class);
				System.out.println(response);
				
				if (response != null && response.isSuccess())
				{
					while(true)
					{
						VegaDisplayScreenContent screenContent = 
								(VegaDisplayScreenContent)DataTransferLib.receiveObjectAesEncrypted(in, securityCode, VegaDisplayScreenContent.class);
						if (screenContent == null) break;
						
						if (screenContent.isKeepAlive())
						{
							System.out.println("Client: Keep-alive signal received");
						}
						else
						{
							System.out.println("Client: Screen content received: " + screenContent.getScreenContent());
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		try
		{
			System.out.println("Shutdown client");
			kkSocket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
