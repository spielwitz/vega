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

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import common.CommonUtils;
import common.ScreenContent;
import common.VegaResources;
import vegaDisplayCommon.DataTransferLib;
import vegaDisplayCommon.VegaDisplayConnectionRequest;
import vegaDisplayCommon.VegaDisplayConnectionResponse;
import vegaDisplayCommon.VegaDisplayScreenContent;

class VegaDisplayServer extends Thread
{
	private ServerSocket serverSocket;
	private int port;
	private int maxConnectionsCount;
	private String securityCode;
	private ArrayList<ClientThread> clientThreads;
	private Object threadLockObject = new Object();
	
	VegaDisplayServer(String securityCode, int port, int maxConnectionsCount)
	{
		this.port = port;
		this.maxConnectionsCount = maxConnectionsCount;
		this.securityCode = securityCode;
		
		this.clientThreads = new ArrayList<ClientThread>();		
	}
	
	public void run()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.port);
		}
		catch (Exception x)
		{
			return;
		}
		
		while (true)
		{
			try
			{
			    Socket clientSocket = this.serverSocket.accept();
			    
			    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			    OutputStream out = clientSocket.getOutputStream();
			    
			    VegaDisplayConnectionRequest connectionRequest = 
			    		(VegaDisplayConnectionRequest)DataTransferLib.receiveObjectAesEncrypted(
			    				in, 
			    				this.securityCode, 
			    				VegaDisplayConnectionRequest.class);
			    
			    if (connectionRequest == null)
			    {
			    	clientSocket.close();
			    	continue;
			    }
			    
			    if (!CommonUtils.areBuildsCompatible(connectionRequest.getBuild()))
			    {
			    	VegaDisplayConnectionResponse connectionResponse = 
			    			new VegaDisplayConnectionResponse(false, "Builds are not compatible");
			    	DataTransferLib.sendObjectAesEncrypted(
			    			out, 
			    			connectionResponse,
			    			securityCode);
			    	
			    	clientSocket.close();
			    	continue;
			    }
			    
			    boolean maxConnectionsCountReached = false;
			    ClientThread serverThread = null;
			    
			    synchronized(this.threadLockObject)
			    {
			    	maxConnectionsCountReached = this.clientThreads.size() >= this.maxConnectionsCount;
			    	
			    	if (!maxConnectionsCountReached)
			    	{
			    		// Create a separate thread for every connection
				    	serverThread = this.new ClientThread(
								clientSocket,
								out,
								connectionRequest.getUserName());
				    	
				    	this.clientThreads.add(serverThread);
			    	}
			    }
			    
			    if (maxConnectionsCountReached)
			    {
			    	// Reject connection, since we already have enough connections
			    	VegaDisplayConnectionResponse connectionResponse = 
			    			new VegaDisplayConnectionResponse(
			    					false, 
			    					"Maximum number of connections reached.");
			    	
			    	DataTransferLib.sendObjectAesEncrypted(out, connectionResponse, securityCode);
			    	
			    	clientSocket.close();
			    	continue;
			    }
			    
			    // Make the socket a long-polling socket
			    clientSocket.setSoTimeout(0);
			    
			    // Send a positive connection response
			    VegaDisplayConnectionResponse connectionResponse = 
		    			new VegaDisplayConnectionResponse(true, null);
			    
		    	DataTransferLib.sendObjectAesEncrypted(out, connectionResponse, securityCode);
		    	
		    	// Send the current screen content
		    	// #########
			    
		    	// Start the thread
			    serverThread.start();
			}
			catch (Exception x)
			{
				break;
			}
		}
		
		this.closeServerSocket();
	}
		
	public ArrayList<String> getRegisteredClients()
	{
		ArrayList<String> clientsInfo = new ArrayList<String>(); 
		
		synchronized(this.threadLockObject)
		{
			for (ClientThread clientThread: this.clientThreads)
			{
				if (clientThread.socket == null) continue;
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(
						clientThread.userName.length() == 0 ?
								VegaResources.Unknown(false) :
								clientThread.userName);
				
				sb.append(" (");
				sb.append(clientThread.socket.getInetAddress().toString());
				sb.append(")");
				
				clientsInfo.add(sb.toString());
			}
		}
		
		return clientsInfo;
	}
	
	void updateScreen(ScreenContent screenContent)
	{
		synchronized(this.threadLockObject)
		{
			for (ClientThread clientThread: this.clientThreads)
			{
				try
				{
					clientThread.queue.add(screenContent);
					
					synchronized(clientThread.syncObject)
					{
						clientThread.syncObject.terminateThread = false;
						clientThread.syncObject.notify();
					}
				}
				catch (Exception x) {}
			}
		}
	}
	
	void shutdown()
	{
		this.closeServerSocket();
		
		synchronized(this.threadLockObject)
		{
			for (ClientThread clientThread: this.clientThreads)
			{
				synchronized(clientThread.syncObject)
				{
					clientThread.syncObject.terminateThread = true;
					clientThread.syncObject.notify();
				}
			}
		}
		
		this.interrupt();
	}
	
	private void closeServerSocket()
	{
		try
		{
		serverSocket.close();
		}
		catch (Exception x)
		{
		}
	}
	
	// ---------------
	private class ClientThread extends Thread
	{
		private Socket socket;
		private String userName;
		private OutputStream out;
		
		private SyncObject syncObject = new SyncObject();
		private Queue<ScreenContent> queue;
		
		private ConnectionCheckThread connectionCheckThread;
		
		ClientThread(Socket socket, OutputStream out, String userName)
		{
			this.socket = socket;
			this.out = out;
			this.userName = userName;
			
			this.queue = new LinkedList<ScreenContent>();
			this.connectionCheckThread = new ConnectionCheckThread();
		}
		
		public void run()
		{
			this.connectionCheckThread.start();
			boolean terminateThread = false;
			
			while (!terminateThread)
			{
				synchronized(syncObject)
				{
					try
					{
						syncObject.wait();
					}
					catch (Exception e)
					{
						break;
					}
				}
				
				terminateThread = syncObject.terminateThread;
				
				while(!terminateThread)
				{
					try
					{
						ScreenContent screenContent = this.queue.remove();
						
						if (!DataTransferLib.sendObjectAesEncrypted(
								out, 
								new VegaDisplayScreenContent(screenContent), // Send the screen contents
								securityCode))
						{
							terminateThread = true;
							break;
						}
					}
					catch (NoSuchElementException x)
					{
						break;
					}
				}
			}
			
			try
			{
				this.socket.close();
			} catch (Exception e) {}
			
			try
			{
				this.connectionCheckThread.interrupt();
			}
			catch (Exception e) {}
			
			synchronized(threadLockObject)
			{
				clientThreads.remove(this);
			}
		}
		
		private class ConnectionCheckThread extends Thread
		{
			public void run()
			{
				do
				{
					try {
						Thread.sleep(10000);
					} catch (Exception e)
					{
						break;
					}
					
					if (!DataTransferLib.sendObjectAesEncrypted(
							out, 
							new VegaDisplayScreenContent(true), // Try to send a keep-alive message to the client
							securityCode))
					{
						synchronized(syncObject)
						{
							syncObject.terminateThread = true;
							syncObject.notify();
						}
						
						break;
					}					
				} while (true);
			}
		}
	}
	
	// ----------------------------
		
	private class SyncObject
	{
		boolean terminateThread;
	}
}
