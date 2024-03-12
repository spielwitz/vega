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

import java.awt.Frame;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.ScreenContent;
import common.VegaResources;
import common.CommonUtils;
import commonUi.DialogWindow;
import commonUi.IVegaDisplayMethods;
import commonUi.IServerMethods;

class VegaDisplayFunctions
{
	private String myIpAddress;
	private String clientCode; 
	private boolean serverEnabled = false;
	
	private Hashtable<String,ClientScreenDisplayContentUpdater> registeredClients;
	private ExecutorService threadPool;
	
	VegaDisplayFunctions(String myIpAddress)
	{
		this.serverEnabled = false;
		this.myIpAddress = (myIpAddress == null) ? CommonUtils.getMyIPAddress() : myIpAddress;
		System.setProperty("java.rmi.server.hostname",this.myIpAddress);
		
		int clientCode = CommonUtils.getRandomInteger(10000);
		this.clientCode = ("0000"+Integer.toString(clientCode));
		this.clientCode = this.clientCode.substring(this.clientCode.length()-4, this.clientCode.length());
		
		this.registeredClients = new Hashtable<String,ClientScreenDisplayContentUpdater>();
		this.threadPool = Executors.newCachedThreadPool();
	}
	
	public String getClientCode() {
		return clientCode;
	}

	public String getMeineIp() {
		return myIpAddress;
	}

	public Object[] getRegisteredClients()
	{
		return this.registeredClients.values().toArray();
	}

	public boolean isServerEnabled() {
		return serverEnabled;
	}
	
	public void setIp(String meineIp)
	{
		this.myIpAddress = (meineIp == null) ? CommonUtils.getMyIPAddress() : meineIp;
		System.setProperty("java.rmi.server.hostname",this.myIpAddress);
	}
		
	String connectClient(
			String clientId, 
			String ip, 
			String clientCode, 
			String clientName,
			boolean inactiveWhileEnterMoves)
	{
		if (clientCode.equals(this.clientCode))
		{
			ClientScreenDisplayContentUpdater updater = 
					new ClientScreenDisplayContentUpdater(clientId, ip, clientName, inactiveWhileEnterMoves);
			this.registeredClients.put(clientId, updater);
			return "";
		}
		else
			return VegaResources.SecurityCodeInvalid(false);
	}
	
	void disconnectClient(String clientId)
	{
		this.registeredClients.remove(clientId);
	}
	
	boolean isClientRegistered(String clientId)
	{
		return this.registeredClients.containsKey(clientId);
	}
	
	boolean openPdf(byte[] pdfBytes, String clientId)
	{
		ClientScreenDisplayContentUpdater c = this.registeredClients.get(clientId);
		
		if (c != null)
		{
			try {
				IVegaDisplayMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(c.clientIp);
				rmiServer = (IVegaDisplayMethods) registry.lookup( c.clientId );
				return rmiServer.openPdf(pdfBytes);
			}
			catch (Exception e)
			{
				return false;
			}
		}
		else
			return false;
	}
	
	boolean startServer(Remote parent)
	{
		boolean ok = false;
		
		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT    );
		}
		catch (Exception e) {}
		
		IServerMethods stub;
		try {
			stub = (IServerMethods) UnicastRemoteObject.exportObject(parent, 0 );
			Registry registry;
			registry = LocateRegistry.getRegistry();
			registry.rebind( CommonUtils.RMI_REGISTRATION_NAME_SERVER, stub );
			
			ok = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			Frame parentFrame = (Frame)parent;
			
			DialogWindow.showError(
					parentFrame, 
					e.toString(),
					VegaResources.Error(false));
		}
		
		if (ok)
			this.serverEnabled = true;

		return ok;
		
	}
	
	
	boolean stopServer(Remote parent)
	{
		boolean ok = false;
		try {
			Registry registry = LocateRegistry.getRegistry();
			this.unbindRegistry(registry);

            UnicastRemoteObject.unexportObject(parent, true);
            ok = true;

        } catch (Exception e)
		{
        	e.printStackTrace();
			
			Frame parentFrame = (Frame)parent;
			
			DialogWindow.showError(
					parentFrame, 
					e.toString(),
					VegaResources.Error(false));
		}
		
		if (ok)
		{
			this.serverEnabled = false;
			this.registeredClients = new Hashtable<String,ClientScreenDisplayContentUpdater>();
		}
		
		return ok;
	}
	
	void updateClients(
			ScreenContent screenContent, 
			ScreenContent screenContentCopy,
			boolean inputEnabled)
	{
		for (ClientScreenDisplayContentUpdater updater: this.registeredClients.values())
		{
			if (screenContentCopy != null && updater.inactiveWhileEnterMoves)
			{
				updater.setContent(screenContentCopy, false, false);
			}
			else
			{
				updater.setContent(screenContent, inputEnabled, !inputEnabled);
			}
			
			try
			{
				this.threadPool.execute(updater);
			}
			catch (Exception x) {}
		}
	}
	
	private void unbindRegistry(Registry registry)
	{
		try
		{
			registry.unbind(CommonUtils.RMI_REGISTRATION_NAME_SERVER);
		}
		catch (Exception x) {}
	}
	
	class ClientScreenDisplayContentUpdater implements Runnable
	{
		private String clientId;
		private String clientIp;
		private String clientName;
		private boolean inactiveWhileEnterMoves;
		private ScreenContent content;
		private boolean inputEnabled;
		private boolean showInputDisabled;
		
		private ClientScreenDisplayContentUpdater(
				String clientId, 
				String clientIp, 
				String clientName,
				boolean inactiveWhileEnterMoves)
		{
			this.clientId = clientId;
			this.clientIp = clientIp;
			this.clientName = clientName;
			this.inactiveWhileEnterMoves = inactiveWhileEnterMoves;
		}
		
		public String getClientId() {
			return clientId;
		}
		
		public String getClientIp() {
			return clientIp;
		}

		public String getClientName() {
			return clientName;
		}

		public boolean isInactiveWhileEnterMoves() {
			return inactiveWhileEnterMoves;
		}

		public boolean isShowInputDisabled() {
			return showInputDisabled;
		}

		@Override
		public void run()
		{
			try {
				IVegaDisplayMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.clientIp);
				rmiServer = (IVegaDisplayMethods) registry.lookup( this.clientId );
				rmiServer.updateScreen(
						this.content, 
						this.inputEnabled,
						this.showInputDisabled);
			}
			catch (Exception e)
			{
			}
		}

		private void setContent(
				ScreenContent content, 
				boolean inputEnabled,
				boolean showInputDisabled)
		{
			this.content = content;
			this.inputEnabled = inputEnabled;
			this.showInputDisabled = showInputDisabled;
		}
	}
}
