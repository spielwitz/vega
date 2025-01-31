package vega;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Game;
import common.ScreenContent;
import vegaDisplayCommon.DataTransferLib;
import vegaDisplayCommon.VegaDisplayConnectionRequest;
import vegaDisplayCommon.VegaDisplayConnectionResponse;

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
					// Receive the current screen content
					// ##################
					
					while(true)
					{
						ScreenContent screenContent = 
								(ScreenContent)DataTransferLib.receiveObjectAesEncrypted(in, securityCode, ScreenContent.class);
						if (screenContent == null) break;
						System.out.println("Client received: " + screenContent);
						Thread.sleep(5000);
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
