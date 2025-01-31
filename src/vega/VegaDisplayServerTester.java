package vega;

import common.ScreenContent;

public class VegaDisplayServerTester
{

	public static void main(String[] args)
	{
		VegaDisplayServer server = new VegaDisplayServer("1234", 55663, 3);
		server.start();
		
		ScreenContent screenContent = new ScreenContent();
		
		server.updateScreen(screenContent);
		
		screenContent = new ScreenContent();
		
		server.updateScreen(screenContent);
		System.out.println("Sent");
		
		server.shutdown();
	}

}
