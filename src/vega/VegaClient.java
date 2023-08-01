/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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

import java.util.ArrayList;
import java.util.Hashtable;

import common.Game;
import common.Highscores;
import common.MovesTransportObject;
import common.VegaResources;
import commonServer.PayloadNotificationMessage;
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
import spielwitz.biDiServer.Client;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.DataSet;
import spielwitz.biDiServer.DataSetInfo;
import spielwitz.biDiServer.PayloadResponseGetDataSetInfosOfUser;
import spielwitz.biDiServer.Response;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.ServerClientBuildCheckResult;

class VegaClient extends Client
{
	private IVegaClientCallback callback;
	private Hashtable<String, Object> lockObjects = new Hashtable<String, Object>();
	
	VegaClient(ClientConfiguration config, boolean establishNotificationSocket, IVegaClientCallback callback)
	{
		super(config, establishNotificationSocket, VegaResources.getLocale());
		this.callback = callback;
	}

	@Override
	public ResponseInfo deleteUser(String userId)
	{
		Response<PayloadResponseGetDataSetInfosOfUser> response = getDataSetInfosOfUser(userId);
		
		if (!response.getResponseInfo().isSuccess())
		{
			return response.getResponseInfo();
		}
		
		ResponseInfo info = super.deleteUser(userId);
		
		if (!info.isSuccess())
		{
			return info;
		}
		
		for (DataSetInfo dataSetInfo: response.getPayload().getDataSetInfos())
		{
			synchronized(this.getLockObject(dataSetInfo.getId()))
			{
				Response<DataSet> dataSetResponse = this.getDataSet(dataSetInfo.getId());
				
				if (dataSetResponse.getResponseInfo().isSuccess() &&
					dataSetResponse.getPayload() != null)
				{
					DataSet dataSet = dataSetResponse.getPayload();
					Game game = (Game) dataSet.getPayloadObject();
					
					if (game.getPlayers()[0].getName().equals(userId))
					{
						this.deleteDataSet(dataSetInfo.getId());
					}
					else
					{
						game.removePlayerFromServerGame(userId);
						dataSet.setPayloadObject(game);
						
						this.updateDataSet(dataSet);
					}
				}
			}
		}
		
		return this.sendCustomRequestMessage(new PayloadRequestMessageDeleteUserFromHighscores(userId))
				.getResponseInfo();
	}

	@Override
	public void onNotificationReceived(
			String sender,
			ArrayList<String> recipients,
			long dateCreated,
			Object payload)
	{
		if (payload.getClass() == PayloadNotificationNewEvaluation.class)
		{
			this.callback.onNewEvaluationAvailable(
					(PayloadNotificationNewEvaluation)payload);
		}
		else if (payload.getClass() == PayloadNotificationMessage.class)
		{
			PayloadNotificationMessage payloadNotificationMessage = (PayloadNotificationMessage)payload;
			this.callback.onMessageReceived(
					sender,
					recipients,
					dateCreated,
					payloadNotificationMessage.getMessage());
		}
	}

	ResponseInfo deleteGame(String gameId)
	{
		synchronized(this.getLockObject(gameId))
		{
			return this.sendCustomRequestMessage(new PayloadRequestMessageDeleteGame(gameId)).getResponseInfo();
		}
	}

	ResponseInfo finalizeGame(String gameId)
	{
		synchronized(this.getLockObject(gameId))
		{
			return this.sendCustomRequestMessage(new PayloadRequestMessageFinalizeGame(gameId)).getResponseInfo();
		}
	}
	
	Response<Game> getGame(String gameId)
	{
		return this.sendCustomRequestMessage(new PayloadRequestMessageGetGame(gameId));
	}
	
	Response<ResponseMessageGamesAndUsers> getGamesAndUsers(String userId)
	{
		return this.sendCustomRequestMessage(new PayloadRequestMessageGetGamesAndUsers());
	}
	
	Response<Boolean> getGamesWaitingForInput()
	{
		return this.sendCustomRequestMessage(new RequestMessageGetGamesWaitingForInput());
	}
	
	Response<Highscores> getHighscores()
	{
		return this.sendCustomRequestMessage(new PayloadRequestMessageGetHighscores());
	}
	
	ResponseInfo postMoves(String gameId, MovesTransportObject movesTransportObject)
	{
		synchronized(this.getLockObject(gameId))
		{
			return this.sendCustomRequestMessage(
					new PayloadRequestPostMoves(
							gameId,
							movesTransportObject)).getResponseInfo();
		}
	}
	
	Response<String> postNewGame(Game game)
	{
		return this.sendCustomRequestMessage(new PayloadRequestPostNewGame(game));
	}
	
	@Override
	protected ServerClientBuildCheckResult checkServerClientBuild(String serverBuild)
	{
		if (serverBuild == null || serverBuild.compareTo(Game.BUILD_COMPATIBLE) < 0)
		{
			return new ServerClientBuildCheckResult(false, Game.BUILD_COMPATIBLE);
		}
		else
		{
			return new ServerClientBuildCheckResult(true, Game.BUILD_COMPATIBLE);
		}
	}
	
	@Override
	protected String getBuild()
	{
		return Game.BUILD;
	}
	
	@Override
	protected void onConnectionStatusChanged(boolean connected)
	{
		this.callback.onConnectionStatusChanged(connected);
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
}
