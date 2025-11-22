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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import common.Colors;
import common.Game;
import common.Player;
import common.VegaResources;
import common.CommonUtils;
import common.GameInfo;
import common.GameOptions;
import common.PlanetDistribution;
import common.PlanetInfo;
import commonServer.ResponseMessageGamesAndUsers;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import spielwitz.biDiServer.Response;
import spielwitz.biDiServer.ResponseInfo;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.IButtonListener;
import uiBaseControls.IComboBoxListener;
import uiBaseControls.IListListener;
import uiBaseControls.IMenuItemListener;
import uiBaseControls.IRadioButtonListener;
import uiBaseControls.Label;
import uiBaseControls.List;
import uiBaseControls.ListItem;
import uiBaseControls.MenuItem;
import uiBaseControls.Panel;
import uiBaseControls.RadioButton;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class ServerGamesJDialog extends Dialog 
					implements  IButtonListener,
								IListListener,
								IRadioButtonListener,
								IMenuItemListener,
								IComboBoxListener,
								IColorChooserCallback
{
	Game gameLoaded;
	private BoardDisplay 	board;
	private Button			butGameHostActions;
	private Button			butGameLoad;
	private Button			butGameNew;
	private Button			butGameNewBoard;
	
	private Button			butGameSubmit;
	private VegaClient		client;
	private ComboBox 		comboPlanets;
	private ComboBox 		comboPlayers;
	private ComboBox 		comboYearLast;
	private Game							game;
	private GameInfo						gameInfo;
	private Hashtable<String, GameInfo>		gameInfoByName;
	private ResponseMessageGamesAndUsers 	gamesAndUsers;
	private Hashtable<RadioButton, ArrayList<String>> gamesByCategory;
	private Label			labDateStart;
	private Label			labUpdateLast;
	private Label			labYear;
	private List			listGames;
	private MenuItem		menuItemEvaluateYear;
	private MenuItem		menuItemGameDelete;
	private MenuItem		menuItemGameFinalize;
	private PanelPlayer[]	pansPlayer;
	private Vega							parent;
	private RadioButton 	rbGamesFinalized;
	
	private RadioButton 	rbGamesWaitingForMe;
	private RadioButton 	rbGamesWaitingForOthers;
	
	private TextField 		tfGameName;
	
	private String[]						userNames;
	
	ServerGamesJDialog(
			Vega parent,
			String currentGameId,
			VegaClient client,
			ResponseMessageGamesAndUsers gamesAndUsers)
	{
		super(parent, VegaResources.GamesOnVegaServer(false, client.getConfig().getUserId()), new BorderLayout(10, 10));

		this.parent = parent;
		this.client = client;
		this.gamesAndUsers = gamesAndUsers;
		this.gameInfoByName = new Hashtable<String, GameInfo>();
		
		for (GameInfo gameInfo: gamesAndUsers.getGames())
		{
			this.gameInfoByName.put(gameInfo.name, gameInfo);
		}
		
		this.userNames = new String[gamesAndUsers.getUsers().size()];
		
		userNames[0] = "";
		int index = 1;
		for (String userId: gamesAndUsers.getUsers().keySet())
		{
			if (!userId.equals(client.getConfig().getUserId()))
			{
				userNames[index] = userId;
				index++;
			}
		}
		
		Panel panNorth = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanNorth = new GridBagConstraints();
		cPanNorth.insets = new Insets(5, 5, 5, 5);
		cPanNorth.fill = GridBagConstraints.HORIZONTAL;
		cPanNorth.weightx = 0.5;
		cPanNorth.weighty = 0.5;
		
		ButtonGroup groupViewSelect = new ButtonGroup();
		
		cPanNorth.gridx = 0; cPanNorth.gridy = 0;  
		this.rbGamesWaitingForMe = new RadioButton(VegaResources.PlayersAreWaiting(false, ""), this);
		groupViewSelect.add(this.rbGamesWaitingForMe);
		panNorth.add(this.rbGamesWaitingForMe, cPanNorth);
		
		cPanNorth.gridx = 1; cPanNorth.gridy = 0;  
		this.rbGamesWaitingForOthers = new RadioButton(VegaResources.WaitingForOtherPlayers(false, ""), this);
		groupViewSelect.add(this.rbGamesWaitingForOthers);
		panNorth.add(this.rbGamesWaitingForOthers, cPanNorth);
		
		cPanNorth.gridx = 2; cPanNorth.gridy = 0;  
		this.rbGamesFinalized = new RadioButton(VegaResources.FinalizedGames(false, ""), this);
		groupViewSelect.add(this.rbGamesFinalized);
		panNorth.add(this.rbGamesFinalized, cPanNorth);
		
		this.rbGamesWaitingForMe.setSelected(true);
		
		this.addToInnerPanel(panNorth, BorderLayout.NORTH);
		
		Panel panWest = new Panel(new BorderLayout(0, 5));
		panWest.setPreferredSize(new Dimension(180, -1));
		
		GridBagConstraints cPanWest = new GridBagConstraints();
		cPanWest.insets = new Insets(5, 5, 0, 5);
		cPanWest.anchor = GridBagConstraints.PAGE_START;
		cPanWest.fill = GridBagConstraints.HORIZONTAL;
		cPanWest.weightx = 0.5;
		cPanWest.weighty = 0.5;
		
		Panel panWestGamesList = new Panel(new GridBagLayout());
		
		this.listGames = new List(new ArrayList<String>(), this);
		cPanWest.fill = GridBagConstraints.BOTH;
		panWestGamesList.add(this.listGames, cPanWest);
		
		panWest.add(panWestGamesList, BorderLayout.CENTER);
		
		Panel panWestButtons = new Panel(new GridBagLayout());
		
		cPanWest.fill = GridBagConstraints.HORIZONTAL;
		cPanWest.gridx = 0; cPanWest.gridy = 0;
		this.butGameNew = new Button(VegaResources.NewGamel(false), this);
		panWestButtons.add(this.butGameNew, cPanWest);
		
		cPanWest.gridx = 0; cPanWest.gridy = 1;
		this.butGameLoad = new Button(VegaResources.LoadGame(false), this);
		panWestButtons.add(this.butGameLoad, cPanWest);
		
		cPanWest.gridx = 0; cPanWest.gridy = 2;
		this.menuItemEvaluateYear = new MenuItem(VegaResources.EvaluateYear(false), this);
		this.menuItemGameFinalize = new MenuItem(VegaResources.FinalizeGame(false), this);
		this.menuItemGameDelete = new MenuItem(VegaResources.DeleteGame(false), this);
		this.butGameHostActions = new Button(
				VegaResources.GameHostActions(false),
				new MenuItem[] {
						this.menuItemEvaluateYear,
						this.menuItemGameFinalize, 
						this.menuItemGameDelete});
		
		panWestButtons.add(this.butGameHostActions, cPanWest);
		
		panWest.add(panWestButtons, BorderLayout.SOUTH);
		
		this.addToInnerPanel(panWest, BorderLayout.WEST);

		// ----
		Panel panCenter = new Panel(new BorderLayout());
		Panel panCenterInner = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanCenterInner = new GridBagConstraints();
		cPanCenterInner.insets = new Insets(5, 5, 5, 5);
		cPanCenterInner.fill = GridBagConstraints.HORIZONTAL;
		cPanCenterInner.weightx = 0.5;
		cPanCenterInner.weighty = 0.5;

		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = 0;
		panCenterInner.add(new Label(VegaResources.NameOfGame(false)), cPanCenterInner);
		
		cPanCenterInner.gridx = 1; cPanCenterInner.gridy = 0;
		this.tfGameName = new TextField("", Player.PLAYER_NAME_REGEX_PATTERN, 0, Game.GAME_NAME_LENGTH_MAX, null);
		panCenterInner.add(this.tfGameName, cPanCenterInner);
		
		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = 1;
		panCenterInner.add(new Label(VegaResources.Players(false)), cPanCenterInner);
		
		cPanCenterInner.gridx = 1; cPanCenterInner.gridy = 1;
		this.comboPlayers = new ComboBox(new String[0], 1, null,this);
		panCenterInner.add(this.comboPlayers, cPanCenterInner);
		
		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = 2;
		panCenterInner.add(new Label(VegaResources.Planets(false)), cPanCenterInner);

		cPanCenterInner.gridx = 1; cPanCenterInner.gridy = 2;
		this.comboPlanets = new ComboBox(new String[0], 1, null, this);
		panCenterInner.add(this.comboPlanets, cPanCenterInner);
		
		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = 3;
		this.labYear = new Label(VegaResources.Years(false));
		panCenterInner.add(this.labYear, cPanCenterInner);
		
		cPanCenterInner.gridx = 1; cPanCenterInner.gridy = 3;
		this.comboYearLast = new ComboBox(new String[0], 12, null, null);
		panCenterInner.add(this.comboYearLast, cPanCenterInner);
		
		cPanCenterInner.gridwidth = 2;
		cPanCenterInner.insets = new Insets(0, 5, 0, 5);
		this.pansPlayer = new PanelPlayer[Game.PLAYERS_COUNT_MAX];
		
		for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
		{
			cPanCenterInner.gridx = 0; cPanCenterInner.gridy = playerIndex + 4;
			this.pansPlayer[playerIndex] = new PanelPlayer(
					this,
					this,
					playerIndex);
			panCenterInner.add(this.pansPlayer[playerIndex], cPanCenterInner);
		}
		
		Panel panLabel = new Panel(new FlowLayout(FlowLayout.CENTER));
		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = Game.PLAYERS_COUNT_MAX + 5;
		this.labDateStart = new Label("");
		panLabel.add(this.labDateStart);
		panCenterInner.add(panLabel, cPanCenterInner);
		
		panLabel = new Panel(new FlowLayout(FlowLayout.CENTER));
		cPanCenterInner.gridx = 0; cPanCenterInner.gridy = Game.PLAYERS_COUNT_MAX + 6;
		this.labUpdateLast = new Label("");
		panLabel.add(this.labUpdateLast, cPanCenterInner);
		panCenterInner.add(panLabel, cPanCenterInner);
		
		panCenter.add(panCenterInner, BorderLayout.NORTH);
		
		this.addToInnerPanel(panCenter, BorderLayout.CENTER);
		// ----
		
		Panel panEast = new Panel(new BorderLayout());
		Panel panEastBoard = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanEastBoard = new GridBagConstraints();
		cPanEastBoard.insets = new Insets(5, 5, 5, 5);
		cPanEastBoard.fill = GridBagConstraints.HORIZONTAL;
		cPanEastBoard.weightx = 0.5;
		cPanEastBoard.weighty = 0.5;
		
		cPanEastBoard.gridx = 0; cPanEastBoard.gridy = 0;
		this.board = new BoardDisplay();
		panEastBoard.add(this.board, cPanEastBoard);
		
		cPanEastBoard.gridx = 0; cPanEastBoard.gridy = 1;
		this.butGameNewBoard = new Button(VegaResources.NewGameBoard(false), this);
		panEastBoard.add(this.butGameNewBoard, cPanEastBoard);
		
		cPanEastBoard.gridx = 0; cPanEastBoard.gridy = 2;
		this.butGameSubmit = new Button(VegaResources.PublishGame(false), this);
		panEastBoard.add(this.butGameSubmit, cPanEastBoard);
		
		panEast.add(panEastBoard, BorderLayout.NORTH);
		
		this.addToInnerPanel(panEast, BorderLayout.EAST);
		
		RadioButton currentRadioButton = this.getGamesByCategory(currentGameId);
		
		if (currentRadioButton != null)
		{
			currentRadioButton.setSelected(true);
			this.radioButtonSelected(currentRadioButton);
			this.listGames.setSelectedValue(currentGameId);
			this.showGameData(currentGameId);
		}
		else
		{
			this.radioButtonSelected(rbGamesWaitingForMe);
		}
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		
		this.updateRadioButtonLabels();
	}
	
	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butGameNew)
		{
			this.enableControlsForNewGame();
			this.newGame(true);
		}
		else if (source == this.butGameNewBoard)
		{
			this.newGame(false);
		}
		else if (source == this.butGameLoad)
		{
			this.selectGame(this.listGames.getSelectedValue());
		}
		else if (source == this.butGameSubmit)
		{
			this.submitGame();
		}
	}

	@Override
	public void colorChanged(int playerIndex, byte newColorIndex, byte oldColorIndex) 
	{
		for (PanelPlayer pan: this.pansPlayer)
		{
			if (pan.playerIndex != playerIndex && pan.canvasPlayerColor.colorIndex == newColorIndex)
			{
				pan.canvasPlayerColor.setColor(oldColorIndex);
				break;
			}
		}
		
	}

	@Override
	public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
	{
	}
	
	@Override
	public void comboBoxItemSelected(ComboBox source, String selectedValue)
	{
		if (source == this.comboPlayers)
		{
			this.newGame(true);
		}
		else if (source == this.comboPlanets)
		{
			this.newGame(false);
		}
	}
	
	@Override
	public void listItemSelected(List source, String selectedValue, int selectedIndex, int clickCount) 
	{
		if (clickCount == 1)
		{
			this.showGameData(selectedValue);
		}
		if (clickCount >= 2)
		{
			this.selectGame(selectedValue);
		}
	}
	
	@Override
	public void menuItemSelected(MenuItem source)
	{
		if (source == this.menuItemGameDelete)
		{
			this.deleteGame(this.listGames.getSelectedValue());
		}
		else if (source == this.menuItemGameFinalize)
		{
			this.finalizeGame(this.listGames.getSelectedValue());
		}
		else if (source == this.menuItemEvaluateYear)
		{
			this.evaluateYear(this.listGames.getSelectedValue());
		}	
	}
	
	@Override
	public void radioButtonSelected(RadioButton source)
	{
		ArrayList<String> gameNames = this.gamesByCategory.get(source);
		
		this.listGames.refreshListModel(gameNames);
		
		if (gameNames.size() > 0)
		{
			this.listGames.setSelectedIndex(0);
			this.showGameData(gameNames.get(0));
		}
		else
		{
			this.listGames.clearSelection();
			this.showGameData(null);
		}
	}

	@Override
	public int[] sortListItems(ArrayList<ListItem> listItems)
	{
		return null;
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	private void deleteGame(String gameId)
	{
		MessageBoxResult dialogResult = MessageBox.showYesNo(
				this,
			    VegaResources.DeleteGameQuestion(false, gameId),
			    VegaResources.DeleteGame(false));
		
		if (dialogResult != MessageBoxResult.YES)
			return;
		
		Vega.showWaitCursor(this);
		ResponseInfo info = this.client.deleteGame(gameId);
		Vega.showDefaultCursor(this);
		
		if (info.isSuccess())
		{
			MessageBox.showInformation(
					this, 
					VegaResources.GameDeletedSuccessfully(false, gameId), 
					VegaResources.DeleteGame(false));
			
			this.close();
		}
		else
		{
			Vega.showServerError(this, info);
		}
	}
	
	private void enableControlsForNewGame()
	{
		this.tfGameName.setEditable(true);
		this.comboPlayers.setEnabled(true);
		this.comboPlanets.setEnabled(true);
		this.comboYearLast.setEnabled(true);
		
		this.tfGameName.setText("");
		
		String[] players = new String[Game.PLAYERS_COUNT_MAX - Game.PLAYERS_COUNT_MIN + 1];
		for (int playerIndex = Game.PLAYERS_COUNT_MIN; playerIndex <= Game.PLAYERS_COUNT_MAX; playerIndex++)
			players[playerIndex-Game.PLAYERS_COUNT_MIN] = Integer.toString(playerIndex);
		
		this.comboPlayers.enableEvents(false);
		this.comboPlayers.setItems(players);
		this.comboPlayers.setSelectedItem(Integer.toString(Game.PLAYERS_COUNT_DEFAULT));
		this.comboPlayers.enableEvents(true);

		this.comboPlanets.enableEvents(false);
		this.comboPlanets.setItems(this.getPlanetComboBoxValues(Game.PLAYERS_COUNT_DEFAULT));
		this.comboPlanets.setSelectedItem(Integer.toString(Game.PLANETS_COUNT_MAX));
		this.comboPlanets.enableEvents(true);
		
		this.comboYearLast.setItems(Game.YEARS);
		this.comboYearLast.setSelectedItem(Integer.toString(Game.YEARS_COUNT_MAX_DEFAULT));
		
		this.labYear.setText(VegaResources.Years(false));
		this.labUpdateLast.setText(" ");
		this.labDateStart.setText(" ");
		
		this.butGameNew.setEnabled(false);
		this.butGameHostActions.setEnabled(false);
		this.butGameLoad.setEnabled(false);
		this.butGameSubmit.setEnabled(true);
		this.butGameNewBoard.setEnabled(true);
		
		this.listGames.clearSelection();
		
		for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
		{
			if (playerIndex == 0)
			{
				this.pansPlayer[playerIndex].comboPlayer.setItems(new String[] {client.getConfig().getUserId()});
			}
			else
			{
				this.pansPlayer[playerIndex].comboPlayer.setItems(userNames);
				this.pansPlayer[playerIndex].comboPlayer.setSelectedItem("");
			}
			
			this.pansPlayer[playerIndex].canvasPlayerColor.setEnabled(true);
			this.pansPlayer[playerIndex].canvasPlayerColor.showColor(true);
			this.pansPlayer[playerIndex].canvasPlayerColor.setColor((byte)(Colors.COLOR_OFFSET_PLAYERS + playerIndex));
			
			this.pansPlayer[playerIndex].cbEnterMovesFinished.setSelected(false);
			this.pansPlayer[playerIndex].cbEnterMovesFinished.setEnabled(false);
		}
	}

	private void evaluateYear(String gameId)
	{
		MessageBoxResult dialogResult = MessageBox.showYesNo(
				this,
			    VegaResources.EvaluateYearQuestion(
			    		false, 
			    		Integer.toString(this.gameInfoByName.get(gameId).year + 1), 
			    		gameId),
			    VegaResources.Evaluation(false));
		
		if (dialogResult != MessageBoxResult.YES)
			return;
		
		Vega.showWaitCursor(this);
		ResponseInfo info = this.client.evaluateYear(gameId);
		Vega.showDefaultCursor(this);
		
		if (info.isSuccess())
		{
			MessageBox.showInformation(
					this, 
					VegaResources.GameFinalizedSuccessfully(false, gameId), 
					VegaResources.Evaluation(false));
			
			this.close();
		}
		else
		{
			Vega.showServerError(this, info);
		}
	}
	
	private void finalizeGame(String gameId)
	{
		MessageBoxResult dialogResult = MessageBox.showYesNo(
				this,
			    VegaResources.FinalizeGameQuestion(false, gameId),
			    VegaResources.FinalizeGame(false));
		
		if (dialogResult != MessageBoxResult.YES)
			return;
		
		Vega.showWaitCursor(this);
		ResponseInfo info = this.client.finalizeGame(gameId);
		Vega.showDefaultCursor(this);
		
		if (info.isSuccess())
		{
			MessageBox.showInformation(
					this, 
					VegaResources.GameFinalizedSuccessfully(false, gameId), 
					VegaResources.FinalizeGame(false));
			
			this.close();
		}
		else
		{
			Vega.showServerError(this, info);
		}
	}
	
	private RadioButton getGamesByCategory(String currentGameId)
	{
		RadioButton rbCurrentGame = null;
		
		this.gamesByCategory = new Hashtable<RadioButton, ArrayList<String>>();
		
		this.gamesByCategory.put(this.rbGamesFinalized, new ArrayList<String>());
		this.gamesByCategory.put(this.rbGamesWaitingForMe, new ArrayList<String>());
		this.gamesByCategory.put(this.rbGamesWaitingForOthers, new ArrayList<String>());
		
		for (GameInfo gameInfo: this.gamesAndUsers.getGames())
		{
			if (gameInfo.finalized)
			{
				this.gamesByCategory.get(this.rbGamesFinalized).add(gameInfo.name);
				
				if (currentGameId != null && currentGameId.equals(gameInfo.name))
				{
					rbCurrentGame = this.rbGamesFinalized;
				}
			}
			else if (!gameInfo.finalized &&
					!gameInfo.moveEnteringFinalized.contains(this.client.getConfig().getUserId()))
			{
				this.gamesByCategory.get(this.rbGamesWaitingForMe).add(gameInfo.name);
				
				if (currentGameId != null && currentGameId.equals(gameInfo.name))
				{
					rbCurrentGame = this.rbGamesWaitingForMe;
				}
			}
			else if (!gameInfo.finalized &&
					 gameInfo.moveEnteringFinalized.contains(this.client.getConfig().getUserId()))
			{
				this.gamesByCategory.get(this.rbGamesWaitingForOthers).add(gameInfo.name);
				
				if (currentGameId != null && currentGameId.equals(gameInfo.name))
				{
					rbCurrentGame = this.rbGamesWaitingForOthers;
				}
			} 
		}
		
		Collections.sort(this.gamesByCategory.get(this.rbGamesFinalized));
		Collections.sort(this.gamesByCategory.get(this.rbGamesWaitingForMe));
		Collections.sort(this.gamesByCategory.get(this.rbGamesWaitingForOthers));
		
		return rbCurrentGame;
	}
	
	private String[] getPlanetComboBoxValues(int playersCount)
	{
		int planetCountMin = PlanetDistribution.getPlanetCountMin(playersCount);
		
		String[] planets = new String[Game.PLANETS_COUNT_MAX - planetCountMin + 1];
		for (int i = planetCountMin; i <= Game.PLANETS_COUNT_MAX; i++)
			planets[i - planetCountMin] = Integer.toString(i);

		return planets;
	}
	
	private void newGame(boolean playersCountChanged)
	{
		int playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
		
		if (playersCountChanged)
		{
			int planetCountSelected = Integer.parseInt((String)this.comboPlanets.getSelectedItem());
			String[] planetComboBoxValues = this.getPlanetComboBoxValues(playersCount);
			int minPlanetCount = Integer.parseInt(planetComboBoxValues[0]);
			this.comboPlanets.setItems(planetComboBoxValues);
			
			if (planetCountSelected < minPlanetCount)
				this.comboPlanets.setSelectedIndex(0);
			else
				this.comboPlanets.setSelectedItem(Integer.toString(planetCountSelected));
		}
		
		int planetsCount = Integer.parseInt((String)this.comboPlanets.getSelectedItem());
	
		Player[] players = new Player[playersCount];
		
		for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
		{
			if (playerIndex < playersCount)
			{
				this.pansPlayer[playerIndex].canvasPlayerColor.setEnabled(true);
				this.pansPlayer[playerIndex].canvasPlayerColor.showColor(true);
				this.pansPlayer[playerIndex].comboPlayer.setEnabled(playerIndex > 0);
				
				String name = (String)this.pansPlayer[playerIndex].comboPlayer.getSelectedItem();
				String email = 
						name.length() > 0 ?
								this.gamesAndUsers.getUsers().get(name) :
								"";
				byte colorIndex = this.pansPlayer[playerIndex].canvasPlayerColor.colorIndex;
				
				players[playerIndex] = new Player(name, email, colorIndex, false);
			}
			else
			{
				this.pansPlayer[playerIndex].canvasPlayerColor.setEnabled(false);
				this.pansPlayer[playerIndex].canvasPlayerColor.showColor(false);
				this.pansPlayer[playerIndex].comboPlayer.setEnabled(false);
				this.pansPlayer[playerIndex].comboPlayer.setSelectedItem("");
			}
		}
		
		this.game = Game.create(
				new HashSet<GameOptions>(),
				players,
				planetsCount,
				this.gamesAndUsers.getEmailGameHost(),
				30);
		
		this.gameInfo = this.game.getGameInfo();
		
		this.board.refresh(this.gameInfo.planetInfo, false);
	}

	private void selectGame(String gameId)
	{
		Vega.showWaitCursor(this);
		Response<Game> response = this.client.getGame(gameId);
		Vega.showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			this.gameLoaded = response.getPayload();
			this.close();
		}
		else
		{
			Vega.showServerError(this, response.getResponseInfo());
		}
	}
	
	private void showGameData(String gameName)
	{
		this.gameInfo = gameName != null ?
						this.gameInfoByName.get(gameName) :
						null;
		
		this.tfGameName.setEditable(false);
		this.comboPlayers.setEnabled(false);
		this.comboPlanets.setEnabled(false);
		this.comboYearLast.setEnabled(false);
		
		this.butGameNew.setEnabled(true);
		this.butGameNewBoard.setEnabled(false);
		
		if (gameInfo != null)
		{
			
			this.tfGameName.setText(gameInfo.name);
			
			this.comboPlayers.setItems(new String[] {Integer.toString(gameInfo.players.length)});
			
			this.comboPlanets.setItems(new String[] {Integer.toString(gameInfo.planetInfo.size())});
			
			this.labYear.setText(VegaResources.YearOf(false, Integer.toString(gameInfo.year + 1)));
			
			this.comboYearLast.setItems(new String[] {Integer.toString(gameInfo.yearMax)});
			
			this.labUpdateLast.setText(
					VegaResources.LastActivity(
							false, 
							VegaUtils.formatDateTimeString(
									VegaUtils.convertMillisecondsToString(this.gameInfo.dateUpdate))));
			
			this.labDateStart.setText(
					VegaResources.Start(
							false, 
							VegaUtils.formatDateTimeString(
									VegaUtils.convertMillisecondsToString(this.gameInfo.dateStart))));
			
			boolean isGameHost = this.gameInfo.players[0].getName().equals(client.getConfig().getUserId());
			
			this.butGameHostActions.setEnabled(isGameHost);
			this.menuItemEvaluateYear.setEnabled(isGameHost && !gameInfo.finalized);
			this.menuItemGameFinalize.setEnabled(isGameHost && !gameInfo.finalized);
			this.butGameLoad.setEnabled(true);
			this.butGameSubmit.setEnabled(false);
		}
		else
		{
			this.tfGameName.setText("");
			this.comboPlayers.removeAllItems();
			this.comboPlanets.removeAllItems();
			this.comboYearLast.removeAllItems();
			this.labYear.setText(VegaResources.Years(false));
			this.labDateStart.setText(" ");
			this.labUpdateLast.setText(" ");
			
			this.butGameHostActions.setEnabled(false);
			this.butGameLoad.setEnabled(false);
			this.butGameSubmit.setEnabled(false);
		}
		
		for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
		{
			this.pansPlayer[playerIndex].canvasPlayerColor.setEnabled(false);
			
			if (gameInfo != null && playerIndex < gameInfo.players.length)
			{
				this.pansPlayer[playerIndex].canvasPlayerColor.setColor(gameInfo.players[playerIndex].getColorIndex());
				this.pansPlayer[playerIndex].canvasPlayerColor.showColor(true);
				
				this.pansPlayer[playerIndex].comboPlayer.setItems(new String[] {gameInfo.players[playerIndex].getName()});
				
				this.pansPlayer[playerIndex].cbEnterMovesFinished.setSelected(
						this.gameInfo.moveEnteringFinalized.contains(gameInfo.players[playerIndex].getName()));
			}
			else
			{
				this.pansPlayer[playerIndex].canvasPlayerColor.showColor(false);
				
				this.pansPlayer[playerIndex].comboPlayer.setItems(new String[] {""});
				
				this.pansPlayer[playerIndex].cbEnterMovesFinished.setSelected(false);
			}
			
			this.pansPlayer[playerIndex].comboPlayer.setEnabled(false);
			
			this.pansPlayer[playerIndex].cbEnterMovesFinished.setEnabled(false);			
		}
		
		this.board.refresh(
				this.gameInfo != null ?
						this.gameInfo.planetInfo :
						null,
				true);
	}

	private void submitGame()
	{
		String gameName = this.tfGameName.getText().trim();
		this.tfGameName.setText(gameName);
		
		if (gameName.length() < Game.GAME_NAME_LENGTH_MIN)
		{
			MessageBox.showError(
					this,
					VegaResources.GameNameInvalid(
							false, 
							gameName, 
							Integer.toString(Game.GAME_NAME_LENGTH_MIN), 
							Integer.toString(Game.GAME_NAME_LENGTH_MAX)),
					VegaResources.Error(false));
			return;
		}
		
		this.game.setName(gameName);
		
		HashSet<String> playerNames = new HashSet<String>();
		
		for (int playerIndex = 0; playerIndex < this.game.getPlayers().length; playerIndex++)
		{
			String playerName = (String)this.pansPlayer[playerIndex].comboPlayer.getSelectedItem();
			
			if (playerName.equals(""))
			{
				MessageBox.showError(
						this,
						VegaResources.AssignUsersToAllPlayers(false),
					    VegaResources.Error(false));
				return;
			}
			else if (playerNames.contains(playerName))
			{
				MessageBox.showError(
						this,
						VegaResources.DuplicatePlayers(false, playerName),
						VegaResources.Error(false));
				return;
			}
			
			String email = this.gamesAndUsers.getUsers().get(playerName);
			
			Player player = this.game.getPlayers()[playerIndex];

			player.setEmail(email);
			player.setName(playerName);
			player.setColorIndex(this.pansPlayer[playerIndex].canvasPlayerColor.colorIndex);
			
			playerNames.add(playerName);
		}
		
		MessageBoxResult dialogResult = MessageBox.showOkCancel(
				this,
				VegaResources.PublishGameQuestion(false, game.getName()),
				VegaResources.PublishGame(false));
		
		if (dialogResult != MessageBoxResult.OK)
			return;
		
		HashSet<GameOptions> options = this.game.getOptions();
		String yearsMaxString = (String)this.comboYearLast.getSelectedItem();
		
		options.remove(GameOptions.AUTO_SAVE);
		options.remove(GameOptions.EMAIL_BASED);
		options.add(GameOptions.SERVER_BASED);
		
		this.game.setYearMax(Integer.parseInt(yearsMaxString));
		
		this.game.prepareYear();
		
		Vega.showWaitCursor(this);
		Response<String> response = this.client.postNewGame(this.game);
		Vega.showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			ArrayList<Player> playersForEmail = new ArrayList<Player>();
			
			for (Player player: this.game.getPlayers())
			{
				if (player.getName().equals(this.client.getConfig().getUserId()))
					continue;
				
				playersForEmail.add(player);
			}
			
			if (playersForEmail.size() > 0)
			{
				MessageBox.showInformation(
						this, 
						VegaResources.GameCreatedSendMail(false, response.getPayload()), 
						VegaResources.PublishGame(false));
				
				EmailCreatorJDialog dlg = new EmailCreatorJDialog(
						this, 
						game.getPlayers(),
						null,
						parent.getConfig().getEmailSeparator(),
						VegaResources.EmailSubjectInvitation(
								false, 
								this.client.getConfig().getUserId(), 
								this.game.getName()), 
						VegaResources.EmailBodyInvitation(
								false, 
								this.client.getConfig().getUserId(), 
								this.game.getName(), 
								this.client.getConfig().getUrl(), 
								Integer.toString(this.client.getConfig().getPort())));
				
				dlg.setVisible(true);
				
				if (dlg.launched)
				{
					parent.getConfig().setEmailSeparator(dlg.separatorPreset);
				}	
			}
			else
				MessageBox.showInformation(
						this, 
						VegaResources.GameCreated(false, response.getPayload()), 
						VegaResources.PublishGame(false));
			
			this.selectGame(this.game.getName());
		}
		else
		{
			Vega.showServerError(this, response.getResponseInfo());
		}
	}
	
	private void updateRadioButtonLabels()
	{
		this.rbGamesWaitingForMe.setText(
				VegaResources.PlayersAreWaiting(
						false, 
						Integer.toString(this.gamesByCategory.get(this.rbGamesWaitingForMe).size())));
		
		this.rbGamesWaitingForOthers.setText(
				VegaResources.WaitingForOtherPlayers(
						false, 
						Integer.toString(this.gamesByCategory.get(this.rbGamesWaitingForOthers).size())));
		
		this.rbGamesFinalized.setText(
				VegaResources.FinalizedGames(
						false, 
						Integer.toString(this.gamesByCategory.get(this.rbGamesFinalized).size())));
	}

	private class BoardDisplay extends JPanel
	{
		private static final int PIXEL_PER_SECTOR = 13;
		private ArrayList<PlanetInfo> planetInfo;
		private boolean showOwners;
		
		public BoardDisplay()
		{
			super();
			
			this.showOwners = true;
			
			this.setPreferredSize(new Dimension(
					(Game.BOARD_MAX_X +1) * PIXEL_PER_SECTOR, 
					(Game.BOARD_MAX_Y +1) * PIXEL_PER_SECTOR));
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = this.getSize();
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dim.width, dim.height);
			
			if (this.planetInfo == null)
				return;
			
			int offset = PIXEL_PER_SECTOR / 2;
			
			int radiusSmall = PIXEL_PER_SECTOR;
			int radiusLarge = CommonUtils.round(PIXEL_PER_SECTOR * 1.2);
			int radiusDiff = CommonUtils.round(0.5 * (radiusLarge - radiusSmall)); 
			
			for (PlanetInfo plInfo: this.planetInfo)
			{
				if (this.showOwners)					
					g.setColor(Colors.get(plInfo.colorIndex));
				else
					g.setColor(plInfo.colorIndex == Colors.NEUTRAL ? 
							Colors.get(Colors.NEUTRAL) : 
							Color.white);

				int x = CommonUtils.round(offset + plInfo.positionX * PIXEL_PER_SECTOR);
				int y = CommonUtils.round(offset + plInfo.positionY * PIXEL_PER_SECTOR);
				
				if (!this.showOwners  && plInfo.colorIndex != Colors.NEUTRAL)
				{
					g.fillOval(x - radiusDiff, y - radiusDiff, radiusLarge, radiusLarge);
				}
				else
				{
					g.fillOval(x, y, radiusSmall, radiusSmall);
				}
			}
		}
		
		public void refresh(ArrayList<PlanetInfo> planetInfo, boolean showOwners)
		{
			this.planetInfo = planetInfo;
			this.showOwners = showOwners;
			this.repaint();
		}
	}

	private class PanelPlayer extends JPanel
	{
		public PlayerColorButton 	canvasPlayerColor;
		public CheckBox 			cbEnterMovesFinished;
		public ComboBox				comboPlayer;
		
		private int					playerIndex;
		
		public PanelPlayer(
				Dialog parent,
				IColorChooserCallback callback,
				int playerIndex)
		{
			super(new FlowLayout(FlowLayout.LEFT));
			
			this.playerIndex = playerIndex;

			this.canvasPlayerColor = new PlayerColorButton(
					parent, 
					callback, 
					playerIndex, 
					(byte)(Colors.COLOR_OFFSET_PLAYERS + playerIndex));
			this.canvasPlayerColor.setPreferredSize(new Dimension(14, 14));
			
			this.add(this.canvasPlayerColor);
			
			this.comboPlayer = new ComboBox(new String[0], 22, null, null);
			this.add(this.comboPlayer);
			
			this.cbEnterMovesFinished = new CheckBox("", false, null);
			this.add(cbEnterMovesFinished);
		}
	}
}
