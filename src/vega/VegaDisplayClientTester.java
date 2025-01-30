package vega;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Game;
import common.ScreenContent;
import vegaDisplayCommon.CryptoLib;
import vegaDisplayCommon.VegaDisplayConnectionRequest;
import vegaDisplayCommon.VegaDisplayConnectionResponse;

public class VegaDisplayClientTester
{

	public static void main(String[] args)
	{
		Socket kkSocket = null;
		OutputStream out = null;
		
		try {
			kkSocket = new Socket();
			kkSocket.connect(
					new InetSocketAddress("127.0.0.1", 55663), 
					10000);
			
			out = kkSocket.getOutputStream();
			
			CryptoLib.sendObjectAesEncrypted(
					out, 
					new VegaDisplayConnectionRequest(Game.BUILD, "Michael"), 
					"123");
			
			DataInputStream in = new DataInputStream(kkSocket.getInputStream());
			
			VegaDisplayConnectionResponse response = (VegaDisplayConnectionResponse)CryptoLib.receiveObject(in, VegaDisplayConnectionResponse.class);
			System.out.println(response);
			
			while(true)
			{
				ScreenContent screenContent = (ScreenContent) CryptoLib.receiveObject(in, ScreenContent.class);
				if (screenContent == null) break;
				System.out.println("Client received: " + screenContent);
				Thread.sleep(5000);
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
