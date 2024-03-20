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

package vegaServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import common.Game;
import common.GameInfo;
import common.Highscores;
import common.Migrator;
import common.Player;
import common.VegaResources;
import commonServer.ClientServerConstants;
import commonServer.PayloadNotificationNewEvaluation;
import commonServer.PayloadRequestMessageDeleteGame;
import commonServer.PayloadRequestMessageDeleteUserFromHighscores;
import commonServer.PayloadRequestMessageFinalizeGame;
import commonServer.PayloadRequestMessageGetGame;
import commonServer.PayloadRequestMessageGetGamesAndUsers;
import commonServer.PayloadRequestPostMoves;
import commonServer.PayloadRequestPostNewGame;
import commonServer.RequestMessageGetGamesWaitingForInput;
import commonServer.PayloadRequestMessageGetHighscores;
import commonServer.ResponseMessageGamesAndUsers;
import commonServer.ServerUtils;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.DataSet;
import spielwitz.biDiServer.DataSetInfo;
import spielwitz.biDiServer.LogLevel;
import spielwitz.biDiServer.PayloadRequestMessagePushNotification;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.Server;
import spielwitz.biDiServer.ServerClientBuildCheckResult;
import spielwitz.biDiServer.ServerConfiguration;
import spielwitz.biDiServer.ServerException;
import spielwitz.biDiServer.Tuple;
import spielwitz.biDiServer.User;

public class VegaServer extends Server
{
	private static ServerConfiguration serverConfig;
	private final static File fileServerCredentials = 
			Paths.get(
					ServerUtils.getHomeFolder(),
					Server.FOLDER_NAME_ROOT,
					"ServerConfig.json").
			toFile(); 
	
	private static final String SERVER_HOSTNAME = "localhost"; 
	
	private static final LogLevel SERVER_DEFAULT_LOGLEVEL = LogLevel.Information;
	private static final int SERVER_PORT = 56084;
	
	public static void main(String[] args)
	{
		ServerConfiguration serverConfiguration = getServerConfig();
		
		String homeDir = ServerUtils.getHomeFolder();
		
		try {
			VegaServer server = new VegaServer(
					serverConfiguration, 
					homeDir);
			Highscores.setRootFolder(server.getPathToServerData());
			server.start();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
		
	private static String getKeyInput()
	{
		String line = "";
		System.out.print(">");
		
		  try{
		     BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		     line = bufferRead.readLine();
		 }
		 catch(Exception ex)
		 {
		    ex.printStackTrace();
		 }
		
		return line;
	}
	
	private static ServerConfiguration getServerConfig()
	{
		if (fileServerCredentials.exists())
		{
			System.out.println(VegaResources.ReadConfiguration(false));
			
			ServerConfiguration serverConfig = ServerConfiguration.readFromFile(fileServerCredentials.getAbsolutePath());
			
			if (serverConfig.getLocale() != null)
				VegaResources.setLocale(serverConfig.getLocale());
			
			return serverConfig;
		}
		else
		{
			String serverUrl = null;
			int port = 0;
			String adminEmail = null;
			String locale = null;
			
			while (true)
			{
				System.out.println("Deutsch [de] / English [en]: ");
				
				String language = getKeyInput();
				
				if (language.toLowerCase().equals("de"))
				{
					locale = "de-DE";
					break;
				}
				else if (language.toLowerCase().equals("en"))
				{
					locale = "en-US";
					break;
				}
			}
			
			VegaResources.setLocale(locale);
			
			System.out.println("\n"+VegaResources.VegaServerSetupWelcome(false)+"\n");

			System.out.print(VegaResources.ServerUrl(false)+ " ("+VegaResources.Default(false)+": "+SERVER_HOSTNAME+"): ");
		    serverUrl = getKeyInput();
		    
		    serverUrl = serverUrl.length() == 0 ?
		    				SERVER_HOSTNAME :
		    				serverUrl;
			
		    System.out.print(VegaResources.ServerPort(false ) + " ("+VegaResources.Default(false)+": "+SERVER_PORT+"): ");
		    String serverPort = getKeyInput();
		    
		    port = serverPort.length() == 0 ? 
		    		SERVER_PORT :
		    		Integer.parseInt(serverPort);
		    
		    System.out.print(VegaResources.EmailAdmin(false)+": ");
		    adminEmail = getKeyInput();
		    
		    System.out.println("\n"+VegaResources.ServerUrl(false)+": " + serverUrl);
		    System.out.println(VegaResources.ServerPort(false )+": " + port);
		    System.out.println(VegaResources.EmailAdmin(false)+": " + adminEmail);
		    
		    System.out.print("\n"+VegaResources.EntriesCorrectQuestion(false)+": ");
		    String ok = getKeyInput();
		    		    
		    if (!ok.equals("1"))
		    {
		    	 System.out.print(VegaResources.ServerSetupAborted(false));
		    	 System.exit(0);
		    }
			
			serverConfig = new ServerConfiguration(
					serverUrl, 
					port, 
					adminEmail, 
					SERVER_DEFAULT_LOGLEVEL,
					locale);
			
			serverConfig.writeToFile(fileServerCredentials.getAbsolutePath());
			
			String adminCredentialsFile = 
					Paths.get(
							fileServerCredentials.getParent(), 
							ClientConfiguration.getFileName(User.ADMIN_USER_ID, serverUrl, port)).toString();
			
			System.out.println("=============================================================================");
			System.out.println(VegaResources.ServerAdminCredentialsCreated(
					false, 
					adminCredentialsFile));
			System.out.println("=============================================================================");
			
			return serverConfig;
		}
	}
	
	private Hashtable<String, Object> lockObjects = new Hashtable<String, Object>();

	private VegaServer(ServerConfiguration config, String homeDir)
			throws ServerException
	{
		super(config, homeDir);
	}

	@Override
	public ServerClientBuildCheckResult checkServerClientBuild(String clientBuild)
	{
		if (clientBuild == null || clientBuild.compareTo(Game.BUILD_COMPATIBLE) < 0)
		{
			return new ServerClientBuildCheckResult(false, Game.BUILD_COMPATIBLE);
		}
		else
		{
			return new ServerClientBuildCheckResult(true, Game.BUILD_COMPATIBLE);
		}
	}

	@Override
	public String getBuild()
	{
		return Game.BUILD;
	}

	@Override
	protected JsonElement migrateDataSet(String className, JsonElement jsonElementBeforeMigration)
	{
		Migrator.migrate((JsonObject) jsonElementBeforeMigration);
		return jsonElementBeforeMigration;
	}
	
	@Override
	protected void onConfigurationUpdated(ServerConfiguration config)
	{
		config.writeToFile(fileServerCredentials.getAbsolutePath());
	}
	
	@Override
	protected Tuple<ResponseInfo, Object> onCustomRequestMessageReceived(
			String userId, 
			Object payloadRequest)
	{
		if (payloadRequest.getClass() == RequestMessageGetGamesWaitingForInput.class)
		{
			return this.getGamesWaitingForInput(userId);
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageGetGamesAndUsers.class)
		{
			return this.getGamesAndUsers(userId);
		}
		else if (payloadRequest.getClass() == PayloadRequestPostNewGame.class)
		{
			return this.postNewGame((PayloadRequestPostNewGame)payloadRequest);
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageGetGame.class)
		{
			return this.getGame(userId, (PayloadRequestMessageGetGame)payloadRequest);
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageDeleteGame.class)
		{
			return this.deleteGame(userId, (PayloadRequestMessageDeleteGame)payloadRequest);
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageFinalizeGame.class)
		{
			return this.finalizeGame(userId, (PayloadRequestMessageFinalizeGame)payloadRequest);
		}
		else if (payloadRequest.getClass() == PayloadRequestPostMoves.class)
		{
			return this.postMoves(userId, (PayloadRequestPostMoves)payloadRequest);
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageGetHighscores.class)
		{
			return new Tuple<ResponseInfo, Object>(
					new ResponseInfo(true),
					Highscores.getInstance());
		}
		else if (payloadRequest.getClass() == PayloadRequestMessageDeleteUserFromHighscores.class)
		{
			return this.deleteUserFromHighscores((PayloadRequestMessageDeleteUserFromHighscores)payloadRequest);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	protected Object setDataSetInfoPayloadObject(
					String dataId, 
					HashSet<String> userIds, 
					Object dataPayloadObject,
					Object currentDataInfoPayloadObject)
	{
		Game game = (Game)dataPayloadObject;
		return game.getGameInfo();
	}
	
	private Tuple<ResponseInfo, Object> deleteGame(String userId, PayloadRequestMessageDeleteGame payloadRequest)
	{
		synchronized(this.getLockObject(payloadRequest.getGameId()))
		{
			DataSet dataSet = this.getDataSet(payloadRequest.getGameId());
			
			if (dataSet == null)
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.GameNotExists(true, payloadRequest.getGameId())),
						null);
			}
			else
			{
				Game game = (Game) dataSet.getPayloadObject();
				
				if (userId.equals(game.getPlayers()[0].getName()))
				{
					this.deleteDataSet(payloadRequest.getGameId());
					
					return new Tuple<ResponseInfo, Object>(
							new ResponseInfo(true),
							null);
				}
				else
				{
					return new Tuple<ResponseInfo, Object>(
							new ResponseInfo(
									false,
									VegaResources.YouAreNotGameHost(true, payloadRequest.getGameId())),
							null);
				}
			}
		}
	}
	
	private Tuple<ResponseInfo, Object> deleteUserFromHighscores(PayloadRequestMessageDeleteUserFromHighscores payload)
	{
		Highscores.getInstance().deletePlayer(payload.getUserId());
		
		return new Tuple<ResponseInfo, Object>(
				new ResponseInfo(true),
				null);
	}
	
	private Tuple<ResponseInfo, Object> finalizeGame(String userId, PayloadRequestMessageFinalizeGame payloadRequest)
	{
		synchronized(this.getLockObject(payloadRequest.getGameId()))
		{
			DataSet dataSet = this.getDataSet(payloadRequest.getGameId());
			
			if (dataSet == null)
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.GameNotExists(true, payloadRequest.getGameId())),
						null);
			}
			else
			{
				Game game = (Game) dataSet.getPayloadObject();
				
				if (game.isFinalized())
				{
					return new Tuple<ResponseInfo, Object>(
							new ResponseInfo(
									false,
									VegaResources.GameHasBeenFinalized(true)),
							null);
				}
				else
				{
					if (userId.equals(game.getPlayers()[0].getName()))
					{
						game.finalizeGameServer();
						dataSet.setPayloadObject(game);
						this.setDataSet(dataSet);
						
						return new Tuple<ResponseInfo, Object>(
								new ResponseInfo(true),
								null);
					}
					else
					{
						return new Tuple<ResponseInfo, Object>(
								new ResponseInfo(
										false,
										VegaResources.YouAreNotGameHost(true, payloadRequest.getGameId())),
								null);
					}
				}
			}
		}
	}
	
	private Tuple<ResponseInfo, Object> getGame(String userId, PayloadRequestMessageGetGame payloadRequest)
	{
		synchronized(this.getLockObject(payloadRequest.getGameId()))
		{
			DataSet dataSet = this.getDataSet(payloadRequest.getGameId());
			
			if (dataSet == null)
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.GameNotExists(true, payloadRequest.getGameId())),
						null);
			}
			
			if (!dataSet.getUserIds().contains(userId))
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.UserNotParticipating(true, userId)),
						null);
			}
			
			Game game = (Game) dataSet.getPayloadObject();
			
			int playerIndex = -1;
			
			for (int i = 0; i < game.getPlayers().length; i++)
			{
				Player player = game.getPlayers()[i];
				
				if (player.getName().equals(userId))
				{
					playerIndex = i;
					break;
				}
			}
			
			Game gameCopy = game.createCopyForPlayer(playerIndex);
				
			return new Tuple<ResponseInfo, Object>(
					new ResponseInfo(true),
					gameCopy);
		}
	}
	
	private Tuple<ResponseInfo, Object> getGamesAndUsers(String userId)
	{
		ArrayList<GameInfo> gameInfos = new ArrayList<GameInfo>(); 
		
		for (DataSetInfo dataSetInfo: this.getDataSetInfosOfUser(userId).getDataSetInfos())
		{
			gameInfos.add((GameInfo) dataSetInfo.getPayloadObject());
			
		}
		
		Hashtable<String,String> users = new Hashtable<String,String>(); 
		
		for (User user: this.getUsers())
		{
			users.put(user.getId(), user.getCustomData().get(ClientServerConstants.USER_EMAIL_KEY));
		}
		
		ResponseMessageGamesAndUsers responseMessageGamesAndUsers = new ResponseMessageGamesAndUsers(
				this.getConfig().getAdminEmail(),
				users,
				gameInfos);
		
		return new Tuple<ResponseInfo, Object>(
				new ResponseInfo(true),
				responseMessageGamesAndUsers);
	}
	
	private Tuple<ResponseInfo, Object> getGamesWaitingForInput(String userId)
	{
		Boolean gamesWaitingForInput = false; 
		
		for (DataSetInfo dataSetInfo: this.getDataSetInfosOfUser(userId).getDataSetInfos())
		{
			GameInfo gameInfo = (GameInfo)dataSetInfo.getPayloadObject();
			
			if (!gameInfo.finalized && 
					 !gameInfo.moveEnteringFinalized.contains(userId))
			{
				gamesWaitingForInput = true;
				break;
			}
		}
		
		return new Tuple<ResponseInfo, Object>(
				new ResponseInfo(true),
				gamesWaitingForInput);
	}
	
	private Object getLockObject(String uuid)
	{
		if (this.lockObjects.containsKey(uuid))
			return this.lockObjects.get(uuid);
		else
		{
			Object lockObject = new Object();
			this.lockObjects.put(uuid, lockObject);
			return lockObject;
		}
	}
	
	private Tuple<ResponseInfo, Object> postMoves(String userId, PayloadRequestPostMoves payloadRequest)
	{
		synchronized(this.getLockObject(payloadRequest.getGameId()))
		{
			DataSet dataSet = this.getDataSet(payloadRequest.getGameId());
			
			if (dataSet == null)
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.GameNotExists(true, payloadRequest.getGameId())),
						null);
			}
			else
			{
				boolean allPlayersHaveEnteredMoves = false;
				Game game = (Game) dataSet.getPayloadObject();
				
				if (game.isFinalized())
				{
					return new Tuple<ResponseInfo, Object>(
							new ResponseInfo(
									false,
									VegaResources.GameHasBeenFinalized(true)),
							null);
				}
				else
				{
					int playerIndex = game.importMovesFromEmail(payloadRequest.getMovesTransportObject());
					
					if (playerIndex >= 0)
					{
						allPlayersHaveEnteredMoves = game.startEvaluationServer();
						
						dataSet.setPayloadObject(game);
						this.setDataSet(dataSet);
						
						if (allPlayersHaveEnteredMoves)
						{
							ArrayList<String> recipients = new ArrayList<String>();
							
							for (Player player: game.getPlayers())
							{
								recipients.add(player.getName());
							}
							
							this.pushNotification(
									game.getPlayers()[0].getName(), 
									new PayloadRequestMessagePushNotification(
											recipients,
											new PayloadNotificationNewEvaluation(
													game.getName())));
						}

						return new Tuple<ResponseInfo, Object>(
								new ResponseInfo(true),
								null);
					}
					else
					{
						return new Tuple<ResponseInfo, Object>(
								new ResponseInfo(
										false,
										VegaResources.YearAlreadyEvaluated(true)),
								null);
					}
				}
			}
		}
	}
	
	private Tuple<ResponseInfo, Object> postNewGame(PayloadRequestPostNewGame payload)
	{
		Game game = payload.getGame();
		
		game.setDateUpdate();
		
		if (this.dataSetExists(game.getName()))
		{
			return new Tuple<ResponseInfo, Object>(
					new ResponseInfo(
							false,
							VegaResources.GameWithSameNameExists(true)),
					null);
		}
		
		HashSet<String> userIds = new HashSet<String>();
		ArrayList<String> notificationRecipients = new ArrayList<String>();
		
		for (int playerIndex = 0; playerIndex < game.getPlayersCount(); playerIndex++)
		{
			User user = this.getUser(game.getPlayers()[playerIndex].getName());
			
			if (user == null)
			{
				return new Tuple<ResponseInfo, Object>(
						new ResponseInfo(
								false,
								VegaResources.UserNotExists(true, game.getPlayers()[playerIndex].getName())),
						null);
			}
			else
				userIds.add(user.getId());
			
			if (playerIndex > 0)
				notificationRecipients.add(user.getId());
		}
		
		DataSet dataSet = new DataSet(
								game.getName(),
								userIds,
								game);
				
		this.setDataSet(dataSet);
		
		this.pushNotification(
				game.getPlayers()[0].getName(), 
				new PayloadRequestMessagePushNotification(
						notificationRecipients,
						new PayloadNotificationNewEvaluation(
								game.getName())));
		
		return new Tuple<ResponseInfo, Object>(
				new ResponseInfo(true),
				game.getName());

	}
}
