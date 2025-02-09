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

package common;

import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class Game extends EmailTransportBase implements Serializable
{
	// Game board dimensions 
	public static final int 		BOARD_MAX_X = 20;
	
	public static final int 		BOARD_MAX_Y = 18;

	/// The current build
	public static final String		BUILD = "0014";
	// Minimum required build version when reading games or when exchanging data
	// with the VEGA server to avoid incompatibilities and advantages caused
	// by program errors.
	public static final String 		BUILD_COMPATIBLE = "0014";
	
	public static final int 		GAME_NAME_LENGTH_MAX = 18;
	public static final int 		GAME_NAME_LENGTH_MIN = 3;
	public static final int 		PLANETS_COUNT_MAX = 42;
	// Default values for a new game
	public static final int 		PLAYERS_COUNT_DEFAULT = 6;
	public static final int 		PLAYERS_COUNT_MAX = 6;
	public static final int 		PLAYERS_COUNT_MIN = 2;
	public static final String[] 	YEARS = { "15", "20", "30", "40", "50", "75", "100", "150", "200", "500", "999" };
	public static final int 		YEARS_COUNT_MAX_DEFAULT = 50;
	static final int 				BATTLESHIPS_COUNT_INITIAL_NEUTRAL_MAX = 10;
	static final int 				BATTLESHIPS_COUNT_INITIAL_PLAYERS = 350;
	static final double 			BLACK_HOLE_RANGE = 0.5;
	static final int 				DAYS_OF_YEAR_COUNT = 365;
	static final int				DEFENSIVE_BATTLESHIPS_BUY_SELL = 450;
	static final int 				DEFENSIVE_BATTLESHIPS_COUNT_INITIAL_PLAYERS = 450;
	static final int				DEFENSIVE_BATTLESHIPSS_COUNT_MAX = 900;
	static final int 				MONEY_PRODUCTION_INITIAL_NEUTRAL = 10;
	static final int 				MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA = 5;
	static final int 				MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W1 = 15;
	static final int 				MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W2 = 200;
	static final int 				MONEY_PRODUCTION_INITIAL_PLAYERS = 10;
	static final int 				MONEY_PRODUCTION_MAX = 100;
	static final int				MONEY_PRODUCTION_NEARBY_PLANETS = 25;
	static final int 				MONEY_PRODUCTION_PURCHASE = 5;
	static final int 				MONEY_SUPPLY_INITIAL_NEUTRAL_MAX = 5;
	static final int 				MONEY_SUPPLY_INITIAL_PLAYERS = 30;
	
	static final int 				PATROL_CAPUTURES_BATTLESHIPS_COUNT_MAX = 5;
	static final double 			PATROL_RADAR_RANGE = 1.5;
	static final int 				PLANET_NAME_LENGTH_MAX = 2;
	static final int 				TRANSPORT_MONEY_MAX = 30;
	private static final int 		BLACK_HOLE_FIRST_YEAR = 7;
	private static final double 	BLACK_HOLE_MOVE_DISTANCE = 2; 
	private static final int 		BLACK_HOLE_YEARS_OFF_MAX = 4;
	private static final int 		BLACK_HOLE_YEARS_OFF_MIN = 1;
	private static final int 		BLACK_HOLE_YEARS_ON_MAX = 9;
	
	private static final int 		BLACK_HOLE_YEARS_ON_MIN = 2;
	private static final int		NEUTRAL_FLEET_FIRST_YEAR = 9;
	private static final int 		NEUTRAL_FLEET_YEAR_INTERVAL = 5;
	
	public static Game create(HashSet<GameOptions> options,
			Player[] players,
			int planetsCount,
			String emailAddressGameHost,
			int yearMax)
	{
		Game game = new Game(options, players, planetsCount, emailAddressGameHost, yearMax);
		game.createBoard();
		
		return game;
	}
	public static Game fromFile(File file)
	{
		Game game = null;
		
		try {
			FileInputStream fs = new FileInputStream(file.getPath());
			GZIPInputStream zipin = new GZIPInputStream (fs);
			ObjectInputStream is = new ObjectInputStream(zipin);
			String gameJson = (String)is.readObject();
			is.close();
			
			game = fromJson(gameJson);

		} catch (Exception e){}		
		
		return game;
	}

	public static Game fromJson(String gameJson)
	{
		JsonObject jobj = new Gson().fromJson(gameJson, JsonObject.class);
		Migrator.migrate(jobj);
		
		return new Gson().fromJson(jobj, Game.class);
	}
	
	public static HashSet<GameOptions> getOptionsDefault()
	{
		HashSet<GameOptions> options = new HashSet<GameOptions>();
		
		options.add(GameOptions.AUTO_SAVE);
		
		return options;
	}
	
	public static ArrayList<Player> getPlayersDefault()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (int playerIndex = 0; playerIndex < PLAYERS_COUNT_MAX; playerIndex++)
			players.add(
					new Player(
							VegaResources.Player(false)+(playerIndex+1), 
							"", 
							(byte)(playerIndex+Colors.COLOR_OFFSET_PLAYERS), 
							false));
		
		return players;
	}
	public static Game importFromVega(String name, byte[] bytes)
	{
		Import imp = new Import(bytes);
		return imp.start(name);
	}
	static String getSectorNameFromPositionStatic(Point pt)
	{
		return Character.toString((char)(65+(CommonUtils.round(pt.getY())))) +
			   Character.toString((char)(65+(CommonUtils.round(pt.getX()))));
		
	}
	private Hashtable<Integer,Archive> archive;
			  
	private int boardHeight;
	private int boardWidth;
	private int boardXOffset;
	private int boardYOffset;
	private String buildRequired;
	transient private Console console;
	private long dateStart;
	private long dateUpdate;
	
	transient private ShipTravelTime[][] distanceMatrix;
	
	transient private int[][] distanceMatrixYears;
	private Hashtable<ShipType, Integer> editorPrices;
	
	private String emailAddressGameHost;
	
	transient private boolean enableParameterChange;
	private boolean finalized;
	transient private Game gameStartOfYear;
	transient private GameThread gameThread;
	transient private boolean goToReplay;
	
	private UUID id;
	transient private boolean initial;
	transient private Hashtable<Integer,String> mapPlanetIndexToName;
	transient private Hashtable<String,Integer> mapPlanetNameToIndex;
	
	private Hashtable<String,Mine> mines;
	private Hashtable<Integer,ArrayList<Move>> moves;
	private String name;
	private HashSet<GameOptions> options;
	transient private int[] planetIndicesSorted;
	
	transient private int planetListContentAllShipsPageCounter = 0;
	transient private int planetListContentStateOrdinal = 0;
	
	private Planet[] planets; 
	transient private Hashtable<String,Integer> planetsByPosition;
	
	private int planetsCount;
	transient private int	  playerIndexEnteringMoves = Player.NEUTRAL;
	
	private UUID[] playerReferenceCodes;
	private Player[] players;
	private int playersCount;
	private ArrayList<ScreenContent> replayLast;
	transient private ScreenContent screenContent;
	transient private ScreenContent screenContentWhileMovesEntered;
	private ArrayList<Ship> ships;
	transient private HashSet<Integer> shipsOfPlayerHidden;

	transient private boolean soloPlayer;
	
	private Tutorial tutorial;
	
	private int year;
	
	private int yearBlackHoleSwap;
	
	private int yearMax;
	
	@SuppressWarnings("unchecked")
	public Game(HashSet<GameOptions> options,
			Player[] players,
			int planetsCount,
			String emailAddressGameHost,
			int yearMax)
	{
		this.id = UUID.randomUUID();
		this.options = (HashSet<GameOptions>)CommonUtils.klon(options);
		this.players = (Player[])CommonUtils.klon(players);
		this.emailAddressGameHost = emailAddressGameHost;

		this.planetsCount = planetsCount;
		this.playersCount = players.length;
		this.yearMax = yearMax;
		
		this.build = BUILD;
		
		this.initial = true;
	}
	
	protected Game() {}
	
	@SuppressWarnings("unchecked")
	public void changeParameters(
			HashSet<GameOptions> options,
			int yearMax,
			String emailAddressGameHost,
			ArrayList<Player> players)
	{
		this.options = (HashSet<GameOptions>) CommonUtils.klon(options);
		
		for (int playerIndex = 0; playerIndex < players.size(); playerIndex++)
		{
			this.players[playerIndex].setName(players.get(playerIndex).getName());
			this.players[playerIndex].setColorIndex(players.get(playerIndex).getColorIndex());
			this.players[playerIndex].setEmailPlayer(players.get(playerIndex).isEmailPlayer());
			this.players[playerIndex].setEmail(players.get(playerIndex).getEmail());
		}
		
		this.emailAddressGameHost = emailAddressGameHost;
		this.yearMax = yearMax;
		
		this.updateBoard();
		this.updatePlanetList(false);
	}
	
	public Game createCopyForPlayer(int playerIndex)
	{
		Game gameClone = (Game)CommonUtils.klon(this);
		
		gameClone.setBuildRequired(BUILD_COMPATIBLE);
		
		if (gameClone.options.contains(GameOptions.SERVER_BASED))
		{
			gameClone.emailAddressGameHost = gameClone.players[0].getEmail();
		}
		
		if (gameClone.finalized)
			return gameClone;
		
		for (int playerIndex2 = 0; playerIndex2 < this.playersCount; playerIndex2++)
		{
			if (playerIndex2 != playerIndex)
			{
				gameClone.moves.put(playerIndex2, new ArrayList<Move>());
				gameClone.playerReferenceCodes[playerIndex2] = null;
			}
		}
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			Planet plClone = this.planets[planetIndex].createCopyForPlayer(
					playerIndex);
			
			gameClone.planets[planetIndex] = plClone;
		}
		
		return gameClone;
	}
	
	public void endTutorial()
	{
		this.tutorial = null;
	}
	
	public void finalizeGameServer()
	{
		this.moves = new Hashtable<Integer,ArrayList<Move>>();
		this.yearMax = this.year;
		this.finalized = true;
		this.setDateUpdate();
		
		Highscores.getInstance().add(this.archive.get(this.year), players);
	}
	
	public String getBuildRequired()
	{
		return this.buildRequired;
	}
	
	public long getDateUpdate()
	{
		return this.dateUpdate;
	}
	
	public ShipTravelTime[][] getDistanceMatrix()
	{
		if (this.distanceMatrix == null)
		{
			this.distanceMatrix = new ShipTravelTime[planetsCount][planetsCount];
			this.distanceMatrixYears = new int[planetsCount][planetsCount];
			
			for (int planetIndex = 0; planetIndex < planetsCount - 1; planetIndex++)
			{
				for (int planetIndex2 = planetIndex + 1; planetIndex2 < planetsCount; planetIndex2++)
				{
					this.distanceMatrix[planetIndex][planetIndex2] = Ship.getTravelTime(
							ShipType.BATTLESHIPS,
							false,
							this.planets[planetIndex].getPosition(),
							this.planets[planetIndex2].getPosition());
					
					this.distanceMatrix[planetIndex2][planetIndex] = this.distanceMatrix[planetIndex][planetIndex2];
					
					this.distanceMatrixYears[planetIndex][planetIndex2] = this.distanceMatrix[planetIndex][planetIndex2].year;
					this.distanceMatrixYears[planetIndex2][planetIndex] = this.distanceMatrixYears[planetIndex][planetIndex2];
				}
			}  			
		}
		
		return this.distanceMatrix;
	}
	
	public String getEmailAddressGameHost()
	{
		return this.emailAddressGameHost;
	}
	
	public GameInfo getGameInfo()
	{
		GameInfo info = new GameInfo();
		
		info.finalized = this.finalized;
		info.year = this.year;
		info.yearMax = this.yearMax;
		info.name = this.name;
		info.players = this.players;
		info.dateStart = this.dateStart;
		info.dateUpdate = this.dateUpdate;
		info.planetInfo = this.getPlanetInfo();
		
		info.moveEnteringFinalized = new HashSet<String>();
		
		if (this.moves != null)
		{
			for (Integer playerIndex: this.moves.keySet())
				info.moveEnteringFinalized.add(this.players[playerIndex].getName());
		}
		
		return info;
	}

	public Game getGameStartOfYear()
	{
		return this.gameStartOfYear;
	}
	
	public UUID getId()
	{
		return this.id;
	}
	
	public byte[] getInventoryPdfBytes(int playerIndex)
	{
		if (playerIndex >= 0)
		{
			Inventory inventory = new Inventory(
					this.gameStartOfYear != null ?
							this.gameStartOfYear :
							this,
							playerIndex);
			return inventory.create(VegaResources.getLocale());
		}
		else
		{
			DistanceMatrix dm = new DistanceMatrix(this.gameStartOfYear != null ?
					this.gameStartOfYear :
					this);
			
			return dm.create(VegaResources.getLocale());
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public HashSet<GameOptions> getOptions()
	{
		return this.options;
	}
	
	public ArrayList<PlanetInfo> getPlanetInfo()
	{
		ArrayList<PlanetInfo> retval = new ArrayList<PlanetInfo>(this.planetsCount);
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			Planet planet = this.planets[planetIndex];
			
			retval.add(new PlanetInfo(
					(int)planet.getPosition().getX(), 
					(int)planet.getPosition().getY(), 
					planet.getOwnerColorIndex(this)));
		}
		
		return retval;
	}
	
	public int getPlanetsCount()
	{
		return this.planetsCount;
	}
	
	public Player[] getPlayers()
	{
		return this.players;
	}
	
	public int getPlayersCount()
	{
		return this.playersCount;
	}
	
	public Hashtable<String,UUID> getPlayersMovesNotEntered()
	{
		Hashtable<String,UUID> playerInfos = new Hashtable<String,UUID>();
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
  		{
  			if (!this.moves.containsKey(playerIndex))
  			{
  				playerInfos.put(this.players[playerIndex].getName(), this.playerReferenceCodes[playerIndex]);
  			}
  		}
		
		return playerInfos;
	}
	
	public ScreenContent getScreenContentStartOfYear()
	{
		if (this.gameStartOfYear == null || this.gameStartOfYear.screenContent == null)
		{
			return null;
		}
		else
		{
			return this.gameStartOfYear.screenContent;
		}
	}
	
	public ScreenContent getScreenContentWhileMovesEntered()
	{
		return this.screenContentWhileMovesEntered;
	}
	
	public int getYear()
	{
		return this.year;
	}
	
	public int getYearMax()
	{
		return this.yearMax;
	}
	
	public int importMovesFromEmail(MovesTransportObject movesTransportObject)
  	{
		int playerIndex = -1;
		
		for (int i = 0; i < this.playersCount; i++)
		{
			if (this.playerReferenceCodes[i].equals(movesTransportObject.getPlayerReferenceCode()))
			{
				playerIndex = i;
				
				this.moves.put(playerIndex, movesTransportObject.getMoves());
				this.setDateUpdate();
				break;
			}
		}
				
		return playerIndex;
  	}
	
	public void initializeTutorial()
	{
		this.tutorial = new Tutorial(this);
	}
	
	public boolean isFinalized()
	{
		return this.finalized;
	}
	
	public boolean isInitial()
	{
		return this.initial;
	}
	
	public boolean isParameterChangeEnabled()
  	{
  		return this.enableParameterChange;
  	}
	
	public boolean isPlayerEmail(int playerIndex)
	{
		if (this.options.contains(GameOptions.EMAIL_BASED))
			return this.players[playerIndex].isEmailPlayer();
		else
			return false;
	}
	
	public boolean isSoloPlayer()
	{
		return this.soloPlayer;
	}
	
	public boolean isTutorial()
	{
		return this.tutorial != null;
	}
	
	public void prepareYear()
	{
		this.moves = new Hashtable<Integer, ArrayList<Move>> ();
		
		this.playerReferenceCodes = new UUID[this.playersCount];
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			if (this.players[playerIndex].isDead())
			{
				this.moves.put(playerIndex, new ArrayList<Move>());
			}
				
			this.playerReferenceCodes[playerIndex] = UUID.randomUUID();
		}
		
		this.editorPrices = new Hashtable<ShipType, Integer>();
		
		this.prepareYearSetPrice(ShipType.SPY);
		this.prepareYearSetPrice(ShipType.MONEY_PRODUCTION);
		this.prepareYearSetPrice(ShipType.BATTLESHIP_PRODUCTION);
		this.prepareYearSetPrice(ShipType.DEFENSIVE_BATTLESHIPS);
		this.prepareYearSetPrice(ShipType.BONUS);
		this.prepareYearSetPrice(ShipType.MINE50);
		this.prepareYearSetPrice(ShipType.MINE100);
		this.prepareYearSetPrice(ShipType.MINE250);
		this.prepareYearSetPrice(ShipType.MINE500);
		this.prepareYearSetPrice(ShipType.MINESWEEPER);
		this.prepareYearSetPrice(ShipType.PATROL);
		this.prepareYearSetPrice(ShipType.TRANSPORT);
	}
	
	public void removePlayerFromServerGame(String userId)
  	{
  		int playerIndex = this.getPlayerIndexByName(userId);
  		
		this.players[playerIndex].setName(Player.PLAYER_DELETED_NAME);
		this.players[playerIndex].setEmail("");
  		  		
  		if (!this.finalized)
  		{
  			for (int i = this.ships.size() - 1; i >= 0; i--)
  			{
  				Ship ship = this.ships.get(i);
  				
  				if (ship.getOwner() == playerIndex && ship.getStopLabel() != null)
  					this.ships.remove(i);
  			}
  			
			Ship ship = new Ship(
					0,
					0,
					null,
					null,
					ShipType.CAPITULATION,
					1,
					playerIndex,
					false,
					true,
					null,
					0); 				

  			ArrayList<Move> moves = new ArrayList<Move>();
  	
			moves.add(
					new Move(
							0,
							ship,
							null));

			this.moves.put(playerIndex, moves);
  			
  			this.startEvaluationServer();
  		}
  	}
	
	public void setBuildRequired(String buildRequired)
	{
		this.buildRequired = buildRequired;
	}
	
	public void setDateUpdate()
	{
		this.dateUpdate = System.currentTimeMillis();
	}
	
	public void setEnableParameterChange(boolean enabled)
  	{
  		this.enableParameterChange = enabled;
  		this.gameThread.checkMenueEnabled();
  	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setOptions(HashSet<GameOptions> options)
	{
		this.options = options;
	}
	
	public void setPlayers(Player[] players)
	{
		this.players = players;
	}
	
	public void setSoloPlayer(boolean v)
	{
		this.soloPlayer = v;
	}
	
	public void setTutorialNextStep()
	{
		this.tutorial.nextStep();
	}
	
	public void setYearMax(int yearMax)
	{
		this.yearMax = yearMax;
	}
	
	public boolean startEvaluationServer()
  	{
		boolean allPlayersHaveEnteredMoves = this.getPlayersMovesNotEntered().size() == 0;
		
  		if (allPlayersHaveEnteredMoves)
  		{
  			this.console = new Console(this, true);
  			
			this.updateBoard();
			this.updatePlanetList(false);
  			
	  		new Evaluation(this);
	  		
	  		this.checkIsGameFinalized(true);
  		}
  		
  		this.setDateUpdate();
  		
  		return allPlayersHaveEnteredMoves;
  	}
	
	public String toFile(File file)
	{
		String gameJson = new Gson().toJson(this);
		
		String errorText = null;
		
		try {
			FileOutputStream fs = new FileOutputStream(file.getPath());
			GZIPOutputStream zipout = new GZIPOutputStream(fs);
			ObjectOutputStream os = new ObjectOutputStream(zipout);
			os.writeObject(gameJson);
			os.close();
		} catch (Exception e) {
			errorText = e.toString();
		}
		
		return errorText;
	}
	
	void autosave()
	{
		if (this.options.contains(GameOptions.AUTO_SAVE))
			this.save(true);
	}
	
	void calculateScores()
	{
		int battleshipsCount[] = new int[this.playersCount];
		int planetsCount[] = new int[this.playersCount];
		int moneyProductions[] = new int[this.playersCount];
		
		for (Planet planet: this.planets)
		{
			int owner = planet.getOwner();
			
			if (planet.getOwner() == Player.NEUTRAL)
				continue;
			
			planetsCount[owner]++;
			
			moneyProductions[owner] += planet.getMoneyProduction();
			
			for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			{
				battleshipsCount[playerIndex]+=planet.getBattleshipsCount(playerIndex);
			}
		}
		
		for (Ship ship: this.ships)
		{
			int owner = ship.getOwner();
			
			if (owner == Player.NEUTRAL)
				continue;
			
			if (ship.getType() == ShipType.BATTLESHIPS)
			{
				for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
				{
					battleshipsCount[playerIndex]+=ship.getBattleshipsCount(playerIndex);;
				}				
			}
		}
		
		this.archive.put(this.year, new Archive(battleshipsCount, planetsCount, moneyProductions));
	}
	
	
	void createScreenContentWhileMovesEntered()
	{
		this.screenContentWhileMovesEntered = 
				(ScreenContent)CommonUtils.klon(this.screenContent);
		
		ScreenContentConsole cons = this.screenContentWhileMovesEntered.getConsole();
		
		String[] textLines = cons.getTextLines();
		textLines[Console.TEXT_LINES_COUNT_MAX - 1] = 
				VegaResources.PlayerEnteringMovesInputDisabled(true);
		
		cons = new ScreenContentConsole(
						textLines, 
						cons.getLineColors(),
						new ArrayList<ConsoleKey>(), 
						cons.getHeaderText(),
						cons.getHeaderCol(), 
						0, 
						false,
						cons.getProgressBarDay());
		
		this.screenContentWhileMovesEntered.setConsole(cons);
	}
	
	boolean evaluationExists()
  	{
  		return (this.replayLast != null && this.replayLast.size() > 0);
  	}
	
	Hashtable<Integer,Archive> getArchive()
	{
		return this.archive;
	}
	
	Console getConsole()
	{
		return this.console;
	}
	
	long getDateStart()
	{
		return this.dateStart;
	}
	
	Hashtable<ShipType, Integer> getEditorPrices()
	{
		return this.editorPrices;
	}
	
	// Getters and setters
	GameThread getGameThread()
	{
		return this.gameThread;
	}
	
	Hashtable<String,Mine> getMines()
	{
		return this.mines;
	}
	
	Hashtable<Integer,ArrayList<Move>> getMoves()
	{
		return this.moves;
	}
	
	int getPlanetIndexFromName(String planetName)
	{
		if (this.mapPlanetNameToIndex == null)
			this.buildPlanetMap();
		
		Integer index = this.mapPlanetNameToIndex.get(planetName.toUpperCase());
		
		if (index == null)
			return Planet.NO_PLANET;
		else
			return index;
	}
	
	int getPlanetIndexFromPosition(Point position)
	{
		if (this.planetsByPosition == null)
			this.buildPlanetMap();
		
		String positionString = Integer.toString((int)(position.getX())) + ";" + Integer.toString((int)(position.getY())); 
		
		Integer planetIndex = this.planetsByPosition.get(positionString);
		
		if (planetIndex == null)
			return -1;
		else
			return planetIndex.intValue();
	}
	
	PlanetInputStruct getPlanetInput(String label, int allowedInput)
  	{
  		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();			  		
  		
  		do
  		{
	  		this.console.appendText(label+": ");
	
			ConsoleInput input = this.console.waitForTextEntered(PLANET_NAME_LENGTH_MAX, allowedKeys, true);
	
			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.console.outAbort();
				return null;
			}
	
			if (allowedInput == PlanetInputStruct.ALLOWED_INPUT_SECTOR)
			{
				Point positionDestination = this.getPositionFromSectorName(input.getInputText());
				
				if (positionDestination != null)
				{
					return new PlanetInputStruct(positionDestination, this.getPlanetIndexFromName(input.getInputText()));
				}
			}
			
			if (allowedInput == PlanetInputStruct.ALLOWED_INPUT_PLANET)
			{
				int planetIndexDestination = this.getPlanetIndexFromName(input.getInputText());
				
				if (planetIndexDestination != Planet.NO_PLANET)
				{
					return new PlanetInputStruct(planetIndexDestination);
				}
			}
			
			this.console.outInvalidInput();

		}
  		
		while (true);

  	}
	
	String getPlanetNameFromIndex(int planetIndex)
	{
		if (this.mapPlanetIndexToName == null)
			this.buildPlanetMap();
		
		return this.mapPlanetIndexToName.get(planetIndex);
	}
	
	Planet[] getPlanets()
	{
		return this.planets;
	}
	
	int[] getPlanetsSorted()
	{
		if (this.planetIndicesSorted == null)
			this.buildPlanetMap();
		
		return this.planetIndicesSorted;
	}
	
	UUID[] getPlayerReferenceCodes()
	{
		return this.playerReferenceCodes;
	}
	
	int getPriceBuy(ShipType shipType)
	{
		return this.editorPrices.get(shipType);
	}
	
	int getPriceSell(ShipType shipType)
	{
		return (int)Math.round((double)this.getPriceBuy(shipType) * Planet.PRICE_RATIO_BUY_SELL);
	}
	
	ArrayList<ScreenContent> getReplayLast()
	{
		return this.replayLast;
	}
	
	ScreenContent getScreenContent()
	{
		return this.screenContent;
	}
	
	String getSectorNameFromPosition(Point position)
	{
		int plIndex = this.getPlanetIndexFromPosition(position);
		
		if (plIndex != -1)
			return this.getPlanetNameFromIndex(plIndex);
		else
			return Game.getSectorNameFromPositionStatic(position);
		
	}
	
	ArrayList<Ship> getShips()
	{
		return this.ships;
	}
	
	HashSet<Integer> getShipsOfPlayerHidden()
	{
		return this.shipsOfPlayerHidden;
	}
	Hashtable<Integer,ArrayList<Byte>> getSimpleFrameObjekt(int planetIndex, byte colorIndex)
	{
		ArrayList<Byte> frameCol = new ArrayList<Byte>();
		frameCol.add(colorIndex);
		
		Hashtable<Integer,ArrayList<Byte>> frames = new Hashtable<Integer,ArrayList<Byte>>();
		frames.put(planetIndex, frameCol);
		
		return frames;
	}
	
	ArrayList<Point> getSimpleMarkedPosition(Point position)
	{
		ArrayList<Point> markedPositions = new ArrayList<Point>();
		markedPositions.add(position);
		
		return markedPositions;
	}
	Tutorial getTutorial()
	{
		return this.tutorial;
	}
	
	void incYear()
	{
		this.year++;
	}
	
	void initAfterLoad(GameThread gameThread)
	{
		this.gameThread = gameThread;
		this.console = new Console(this, false);
		
		this.gameThread.checkMenueEnabled();
		this.goToReplay = this.soloPlayer;
		
		this.mainLoop();
	}
	
	void initNewGame(
			GameThread gameThread)
			
	{
		this.gameThread = gameThread;
		this.console = new Console(this, false);
		
		do
		{
			this.createBoard();
			
			this.gameThread.checkMenueEnabled();
			
			this.updateBoardNewGame();
			this.updatePlanetList(true);
			
			this.console.clear();
			this.console.appendText(
					VegaResources.AgreeWithGameBoardQuestion(true) + " ");
			String input = this.console.waitForKeyPressedYesNo().getInputText().toUpperCase();
			
			if (input.equals(Console.KEY_YES))
				break;
			
		} while (true);
		
		this.initial = false;
		this.gameThread.checkMenueEnabled();
		
		this.prepareYear();
		this.mainLoop();
	}
	
	boolean isGoToReplay()
	{
		return this.goToReplay;
	}
	
	void launchNeutralFleet()
	{
		if (this.year < NEUTRAL_FLEET_FIRST_YEAR ||
			(this.year + 1) % NEUTRAL_FLEET_YEAR_INTERVAL != 0)
		{
			return;
		}
		
		int destinationPlanetIndex = CommonUtils.getRandomInteger(this.planetsCount);
		Planet destinationPlanet = this.planets[destinationPlanetIndex];
		
		Point pointStart = null;
		
		while (pointStart == null)
		{
			pointStart = new Point(
							this.boardXOffset + CommonUtils.getRandomInteger(this.boardWidth),
							this.boardYOffset + CommonUtils.getRandomInteger(this.boardHeight));
			
			for (Planet planet: this.planets)
			{
				if (pointStart.equals(planet.getPosition()))
				{
					pointStart = null;
					break;
				}
			}
		}
		
		int count = CommonUtils.round(Math.log(this.year) / Math.log(1.02));
		count = count + CommonUtils.round(count * (double)(CommonUtils.getRandomInteger(160) - 80) / 100);
		
		this.ships.add(
				new Ship(
						Planet.NO_PLANET, 
						destinationPlanetIndex,
						pointStart,
						destinationPlanet.getPosition(),
						0,
						ShipType.BATTLESHIPS,
						count,
						Player.NEUTRAL,
						false,
						null));
	}
	
	String mainMenuGetYearDisplayText()
	{
		if (this.finalized)
		{
			return VegaResources.FinalizedGameInYear(true, Integer.toString(this.year+1));
		}
		else
		{
			return VegaResources.YearOf(
						true, 
						Integer.toString(this.year+1), 
						Integer.toString(this.yearMax));
		}
	}
		
	void pause(int milliseconds)
	{
		if (!this.console.isBackground())
			this.gameThread.pause(milliseconds);
	}
	
	void printAllianceInfo(int planetIndex)
	{
		Planet planet = this.planets[planetIndex];
		
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			if (!planet.isAllianceMember(playerIndex))
				continue;
			
			if (counter >= 3)
			{
				this.console.appendText(sb.toString());
				this.console.lineBreak();
				sb = new StringBuilder();
				counter = 0;
			}
			
			if (sb.length() > 0)
				sb.append(", ");
			
			sb.append(this.players[playerIndex].getName());
			sb.append(" (");
			sb.append(planet.getShipsCount(ShipType.BATTLESHIPS, playerIndex));
			sb.append(" "+VegaResources.Battleships(true)+")");
				
			counter++; 							
		}
		
		this.console.appendText(sb.toString());
	}
	
	Ship setBlackHoleDirection()
  	{
		if (this.year < BLACK_HOLE_FIRST_YEAR)
			return null;
		
		Ship blackHole = null;
		
		Optional<Ship> shipOptional = this.ships.stream().filter(s -> s.getType() == ShipType.BLACK_HOLE).findFirst();
		
		if (shipOptional.isPresent())
		{
			blackHole = shipOptional.get();
			
			if (this.year == this.yearBlackHoleSwap)
			{
				this.ships.remove(blackHole);
				
				this.yearBlackHoleSwap = 
						this.year +
						BLACK_HOLE_YEARS_OFF_MIN +
						CommonUtils.getRandomInteger(BLACK_HOLE_YEARS_OFF_MAX - BLACK_HOLE_YEARS_OFF_MIN);
				
				return null;
			}
			
			blackHole.setPositionStart(blackHole.getPositionDestination());
		}
		else
		{
			if (this.year < this.yearBlackHoleSwap)
				return null;
			
			Point currentPosition = null;
			boolean ok = false;
			
			while (!ok)
			{
				currentPosition = 
						new Point(
								this.boardXOffset + CommonUtils.getRandomInteger(this.boardWidth),
								this.boardYOffset + CommonUtils.getRandomInteger(this.boardHeight));
				
				ok = true;
				
				for (Planet planet: this.planets)
				{
					if (planet.getPosition().getSector().equals(currentPosition.getSector()))
					{
						ok = false;
						break;
					}
				}
			}
			
			blackHole = new Ship(
					Planet.NO_PLANET,
					Planet.NO_PLANET,
					currentPosition,
					currentPosition,
					0,
					ShipType.BLACK_HOLE,
					1,
					Player.NEUTRAL,
					false,
					null);
			
			this.ships.add(blackHole);
			
			this.yearBlackHoleSwap = 
					this.year +
					BLACK_HOLE_YEARS_ON_MIN +
					CommonUtils.getRandomInteger(BLACK_HOLE_YEARS_ON_MAX - BLACK_HOLE_YEARS_ON_MIN);
		}
		
		Point destinationPosition = null;
		
		while (destinationPosition == null)
		{
			double angle = Math.PI * CommonUtils.getRandomInteger(360) / (double)180;
			
			destinationPosition =
					new Point(
							blackHole.getPositionStart().x + BLACK_HOLE_MOVE_DISTANCE * Math.cos(angle),
							blackHole.getPositionStart().y + BLACK_HOLE_MOVE_DISTANCE * Math.sin(angle));
			
			if (destinationPosition.x < this.boardXOffset ||
				destinationPosition.x > this.boardXOffset + this.boardWidth - 1 ||
				destinationPosition.y < this.boardYOffset ||
				destinationPosition.y > this.boardYOffset + this.boardHeight - 1)
			{
				destinationPosition = null;
				continue;
			}
			
			for (Planet planet: this.planets)
			{
				if (planet.getPosition().getSector().equals(destinationPosition.getSector()))
				{
					destinationPosition = null;
					break;
				}
			}
		}
		
		blackHole.resetYearCount();
		blackHole.setPositionDestination(destinationPosition);
		
		return blackHole;
  	}
	
	void setGoToReplay(boolean goToReplay)
	{
		this.goToReplay = goToReplay;
	}
	
	void setInitial()
	{
		this.initial = true;
	}
	
	void setMines(Hashtable<String,Mine> mines)
	{
		this.mines = mines;
	}
	
	void setMoves(Hashtable<Integer,ArrayList<Move>> moves)
	{
		this.moves = moves;
	}
	
	void setPlanets(Planet[] planets)
	{
		this.planets = planets;
	}
	
	void setPlayerIndexEnteringMoves(int playerIndex)
	{
		this.playerIndexEnteringMoves = playerIndex;
	}
	
	void setReplayLast(ArrayList<ScreenContent> screenContent)
	{
		this.replayLast = screenContent;
	}
	
	void setScreenContent(ScreenContent screenContent)
	{
		this.screenContent = screenContent;
	}
	
	void setScreenContentMode (int mode)
	{
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setMode(mode);
	}
	
	void setScreenContentWhileMovesEntered(ScreenContent screenContent)
	{
		this.screenContentWhileMovesEntered = screenContent;
	}
	
	void setShips(ArrayList<Ship> ships)
	{
		this.ships = ships;
	}
	
	void setShipsOfPlayerHidden(HashSet<Integer> players)
	{
		this.shipsOfPlayerHidden = players;
	}
	
	void togglePlanetListContent(int keyCode)
	{
		if (this.planetListContentStateOrdinal == 1 &&
				(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN))
		{
			if (keyCode == KeyEvent.VK_UP && this.planetListContentAllShipsPageCounter > 0)
			{
				this.planetListContentAllShipsPageCounter--;
				this.updatePlanetList(
						this.getCurrentPlayerIndex(), 
						false);
				return;
			}
			else if (keyCode == KeyEvent.VK_DOWN)
			{
				this.planetListContentAllShipsPageCounter++;
				this.updatePlanetList(
						this.getCurrentPlayerIndex(), 
						false);
				return;
			}
		}
		
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
		{
			PlanetListContent[] contentTypes = PlanetListContent.values();
			int currentPlayerIndex = this.getCurrentPlayerIndex();
			
			int maxIndex = 
					currentPlayerIndex == Player.NEUTRAL ?
							3 : contentTypes.length;
			
			if (keyCode == KeyEvent.VK_LEFT)
			{
				this.planetListContentStateOrdinal--;
				this.planetListContentStateOrdinal += maxIndex;
			}
			else
			{
				this.planetListContentStateOrdinal++;
			}
			
			this.planetListContentStateOrdinal = this.planetListContentStateOrdinal % maxIndex;
			
			this.updatePlanetList(
					this.getCurrentPlayerIndex(), 
					false);
		}		
	}
	
	void togglePlanetListContentReset()
	{
		this.planetListContentStateOrdinal = 0;
		this.planetListContentStateOrdinal = 0;
		this.updatePlanetList(Player.NEUTRAL, false);
	}
	
	void updateBoard()
	{
		this.updateBoard(null, null, 0);
	}
	
	void updateBoard (
			ArrayList<Point> positionsMarked, 
			int day)
	{
		this.updateBoard(null, positionsMarked, day);
	}
	
	void updateBoard (
			Hashtable<Integer, ArrayList<Byte>> frames, 
			ArrayList<Point> positionsMarked, 
			int radarShipHashCode,
			int playerIndexDisplayedShips,
			int day)
	{
		ArrayList<ScreenContentBoardPlanet> plData = new ArrayList<ScreenContentBoardPlanet>(this.planetsCount);
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			ArrayList<Byte> frameCol = null;
			
			if (frames != null)
				frameCol = frames.get(planetIndex);
			
			plData.add(new ScreenContentBoardPlanet(
					this.getPlanetNameFromIndex(planetIndex),
					this.planets[planetIndex].getPosition(),
					this.planets[planetIndex].getOwnerColorIndex(this),
					frameCol));
		}
		
		ArrayList<ScreenContentBoardObject> objects = new ArrayList<ScreenContentBoardObject>(); 

		for (Ship ship: this.ships)
		{
			if (ship.isToBeDeleted()
				|| ship.getType() == ShipType.CAPITULATION)
				continue;
			
			if (this.shipsOfPlayerHidden != null &&
				ship.getType() != ShipType.BLACK_HOLE &&
				this.shipsOfPlayerHidden.contains(ship.getOwner()))
			{
				continue;
			}
			
			Point shipPosition = ship.getPositionOnDay(day);
			
			boolean drawSymbol = 
					ship.wasStoppedBefore() ||
					!(ship.getOwner() == playerIndexDisplayedShips && ship.isStartedRecently());
			
			ScreenContentBoardRadar radar = null;
			
			if (ship.getType() == ShipType.PATROL && !ship.isTransfer())
				radar = new ScreenContentBoardRadar(
						ship.hashCode() == radarShipHashCode,
						false,
						Game.PATROL_RADAR_RANGE);
			else if (ship.getType() == ShipType.BLACK_HOLE)
				radar = new ScreenContentBoardRadar(
						ship.hashCode() == radarShipHashCode,
						true,
						Game.BLACK_HOLE_RANGE);
			
			boolean drawLine =
					(ship.getOwner() == playerIndexDisplayedShips && ship.isStartedRecently()) ||
					(!ship.wasStoppedBefore() &&
					!ship.isStopped());
			
			ScreenContentBoardObject object =
					new ScreenContentBoardObject(
							ship.hashCode(),
							shipPosition,
							drawLine ? ship.getPositionDestination() : null,
							drawSymbol ? ship.getScreenDisplaySymbol() : (byte)-1,
							ship.getOwnerColorIndex(this),
							drawSymbol,
							drawLine && ship.getPlanetIndexDestination() == Planet.NO_PLANET,
							radar);
			
			objects.add(object);
		}
		
		ArrayList<ScreenContentBoardMine> mines = new ArrayList<ScreenContentBoardMine>();
		
		for (Mine mine: this.mines.values())
		{
			mines.add(
					new ScreenContentBoardMine(
							mine.getPositionX(), 
							mine.getPositionY(), 
							mine.getStrength()));
		}
		
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setBoard(
				new ScreenContentBoard(
						plData,
						positionsMarked,
						objects,
						mines));
		
		this.screenContent.setEventDay(day);
		
		if (!this.console.isBackground())
			this.gameThread.updateDisplay(this.screenContent);
	}
	
	void updateBoard (
			Hashtable<Integer,ArrayList<Byte>> frames, 
			int day)
	{
		this.updateBoard(frames, null, day);
	}
	
	void updateBoard (int day)
	{
		this.updateBoard(null, null, day);
	}
	
	void updateConsole(ScreenContentConsole contentConsole, boolean isBackground)
	{
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setConsole(contentConsole);
		
		if (this.gameThread != null && !isBackground)
			this.gameThread.updateDisplay(this.screenContent);
	}

	void updatePlanetList(boolean newGame)
	{
		this.updatePlanetList(Player.NEUTRAL, newGame);
	}
		
	void updatePlanetList (int playerIndexEnterMoves, boolean newGame)
	{
		PlanetListContent planetListContent = 
				PlanetListContent.values()[this.planetListContentStateOrdinal];
		
		if (planetListContent == PlanetListContent.ALL_SHIPS)
		{
			this.updatePlanetListAllShips(playerIndexEnterMoves);
		}
		else
		{
			ShipType shipTypeDisplay = 
					Enum.valueOf(ShipType.class, planetListContent.toString());
			this.updatePlanetListByShipTypes(shipTypeDisplay, playerIndexEnterMoves, newGame);
		}
	}
	
	KeyEventExtended waitForKeyInput()
	{
		if (!this.console.isBackground())
			return this.gameThread.waitForKeyInput();
		else
			return new KeyEventExtended(
					new KeyEvent(
							new Panel(), 
							0, 
							0, 
							0,
		                    KeyEvent.VK_TAB,
		                    '\t'), 
						"",
						"");
	}
	
	private void askFinalizeGame()
	{
		this.console.appendText(VegaResources.FinalizeGameQuestion(true) + " ");
		String input = this.console.waitForKeyPressedYesNo().getInputText().toUpperCase();
		
		if (input.equals(Console.KEY_YES))
			this.finalizeGame(false);
		else
			this.console.outAbort();		
	}
	
	private void buildPlanetMap()
	{
		this.mapPlanetIndexToName = new Hashtable<Integer,String>();
		this.mapPlanetNameToIndex = new Hashtable<String,Integer>();
		this.planetsByPosition = new Hashtable<String,Integer>();
		
		String[] planetNamesUnsorted = new String[this.planetsCount];
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			Planet planet = this.planets[planetIndex];
			String positionString = Integer.toString(
					(int)(planet.getPosition().getX())) + ";" + Integer.toString((int)(planet.getPosition().getY()));

			String planetName = this.getSectorNameFromPosition(planet.getPosition());
			
			this.mapPlanetIndexToName.put(planetIndex, planetName);
			this.mapPlanetNameToIndex.put(planetName, planetIndex);
			
			planetNamesUnsorted[planetIndex] = planetName;
			
			this.planetsByPosition.put(positionString, planetIndex);
		}
		
		this.planetIndicesSorted = CommonUtils.sortList(planetNamesUnsorted, false);
	}
	
  	private void checkIsGameFinalized(boolean background)
	{
		if (!this.finalized)
		{
			if (this.year >= this.yearMax)
				this.finalizeGame(background);
			else
			{
				int playersRemainingCount = 0;
				
				for (Player player: this.players)
				{
					if (!player.isDead())
					{
						playersRemainingCount++;
					}
				}
				
				if (playersRemainingCount <= 1)
					this.finalizeGame(background);
			}
		}
	}
  	
  	private void createBoard()
	{
		this.name = "";
		this.buildRequired = BUILD_COMPATIBLE;
		this.finalized = false;
		this.initial = true;
		this.archive = new Hashtable<Integer,Archive>();
		this.replayLast = new ArrayList<ScreenContent>();
		this.year = 0;
		this.mines = new Hashtable<String,Mine>();
		this.ships = new ArrayList<Ship>();
		this.planets = new Planet[planetsCount];
		this.screenContent = null;
		this.dateStart = System.currentTimeMillis();
		
		PlanetDistribution planetDistribution = new PlanetDistribution(players.length, planetsCount);
		
		this.boardWidth = (int) planetDistribution.getBoardSize().x;
		this.boardHeight = (int) planetDistribution.getBoardSize().y;
		
		this.boardXOffset = (int)((double)(BOARD_MAX_X - this.boardWidth) / 2.);
		this.boardYOffset = (int)((double)(BOARD_MAX_Y - this.boardHeight) / 2.);
		
		Point ptOffset = new Point(this.boardXOffset, this.boardYOffset);
		
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
		{
			int homePlanetIndex = planetDistribution.getHomePlanetIndices()[playerIndex];
			
			this.planets[homePlanetIndex] = new Planet(
					planetDistribution.getPositions()[homePlanetIndex].add(ptOffset),
					null,
					new Hashtable<ShipType, Integer>(){{put(ShipType.BATTLESHIPS, BATTLESHIPS_COUNT_INITIAL_PLAYERS);}},
					playerIndex, 
					DEFENSIVE_BATTLESHIPS_COUNT_INITIAL_PLAYERS,
					MONEY_SUPPLY_INITIAL_PLAYERS,
					MONEY_PRODUCTION_INITIAL_PLAYERS,
					MONEY_PRODUCTION_INITIAL_PLAYERS);
			
			int[] moneyProductionsNearbyPlanets = this.getMoneyProductionsOfNearbyPlanets(PlanetDistribution.NEARBY_PLANETS_COUNT);
			
			for (int i = 0; i < planetDistribution.getNearbyPlanetIndicesPerPlayer()[playerIndex].length; i++)
			{
				int nearbyPlanetIndex = planetDistribution.getNearbyPlanetIndicesPerPlayer()[playerIndex][i];
				
				this.planets[nearbyPlanetIndex] = new Planet(
						planetDistribution.getPositions()[nearbyPlanetIndex].add(ptOffset),
						null,
						new Hashtable<ShipType, Integer>(){{put(ShipType.BATTLESHIPS, CommonUtils.getRandomInteger(BATTLESHIPS_COUNT_INITIAL_NEUTRAL_MAX + 1));}},
						Player.NEUTRAL,
						0,
						CommonUtils.getRandomInteger(MONEY_SUPPLY_INITIAL_NEUTRAL_MAX + 1),
						moneyProductionsNearbyPlanets[i],
						moneyProductionsNearbyPlanets[i]);
			}
		}
		
		for (int planetIndex = planetDistribution.getStartIndexRegularPlanets(); planetIndex < this.planets.length; planetIndex++)
		{
			int moneyProduction = getRandomProductionOfNeutralPlanet();
			
			this.planets[planetIndex] = new Planet(
					planetDistribution.getPositions()[planetIndex].add(ptOffset),
					null,
					new Hashtable<ShipType, Integer>(){{put(ShipType.BATTLESHIPS, CommonUtils.getRandomInteger(BATTLESHIPS_COUNT_INITIAL_NEUTRAL_MAX + 1));}},
					Player.NEUTRAL,
					0,
					CommonUtils.getRandomInteger(MONEY_SUPPLY_INITIAL_NEUTRAL_MAX + 1),
					moneyProduction,
					moneyProduction);
		}
	
				
		this.buildPlanetMap();
		this.calculateScores();	
	}
  	
  	private void emailMenu()
	{
		this.console.setHeaderText(
			this.mainMenuGetYearDisplayText() + " -> "+VegaResources.EnterMoves(true)+" -> "+VegaResources.EmailActions(true), Colors.NEUTRAL);
	
		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
		allowedKeys.add(new ConsoleKey("1",VegaResources.SendGameToAllPlayers(true)));
		allowedKeys.add(new ConsoleKey("2",VegaResources.ImportMovesOfPlayer(true)));
		allowedKeys.add(new ConsoleKey("3",VegaResources.WhoIsMissing(true)));
		allowedKeys.add(new ConsoleKey("ESC",VegaResources.Back(true)));
		
		do
		{
			ConsoleInput consoleInput = this.console.waitForKeyPressed(allowedKeys);
			String input = consoleInput.getInputText().toUpperCase();
			
			if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				break;
			else if (input.equals("1"))
			{
				this.autosave();
				
				this.sendGameToEmailPlayer();
				break;
			}
			else if (input.equals("2"))
			{
				MovesTransportObject movesTransportObject = this.gameThread.importMovesFromEmail();
				
				if (movesTransportObject != null)
				{
					int playerIndex = this.importMovesFromEmail(movesTransportObject);
					
					if (playerIndex >= 0)
					{
						this.console.setLineColor(this.players[playerIndex].getColorIndex());
						this.console.appendText(
								VegaResources.MovesSuccessfullyImported(true, this.players[playerIndex].getName()));
						this.console.lineBreak();
						this.console.setLineColor(Colors.WHITE);
						
						this.autosave();
					}
					else
					{
						this.console.appendText(VegaResources.MovesDoNotBelongToThisYear(true));
						this.console.lineBreak();
					}
				}
				else
				{
					this.console.appendText(VegaResources.MovesNotImported(true));
					this.console.lineBreak();
				}
			}
			else if (input.equals("3"))
			{
				StringBuilder sb = new StringBuilder();
				
				for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
				{
					Player player = this.players[playerIndex];
					
					if (player.isEmailPlayer() && !this.moves.containsKey(playerIndex))
					{
						if (sb.length() > 0)
							sb.append(" ");
						
						sb.append(player.getName());
					}
				}
				
				if (sb.length() == 0)
					this.console.appendText(VegaResources.AllEmailPlayerMovesImported(true));
				else
				{
					this.console.appendText(VegaResources.MovesOfEmailPlayersNotYetImported(true));
					this.console.lineBreak();
					this.console.appendText(sb.toString());
				}
				
				this.console.lineBreak();
			}
			else
				this.console.outInvalidInput();
				
			
		} while (true);
	}
  	
	private void finalizeGame(boolean background)
	{
		this.finalized = true;
		
		this.performAwardCeremony();
		
		if (!background)
		{
			this.console.setLineColor(Colors.WHITE);
			this.console.appendText(VegaResources.AddToHighScoreListQuestion(true) + " ");
			
			String input = this.console.waitForKeyPressedYesNo().getInputText().toUpperCase();
			if (input.equals(Console.KEY_YES))
			{
				Highscores.getInstance().add(this.archive.get(this.year), players);
			}
				
			this.console.clear();
			new Statistics(this, true);
			
			if (this.options.contains(GameOptions.AUTO_SAVE))
			{
				this.options.remove(GameOptions.AUTO_SAVE);
				this.save(true);
			}
		}
		
		this.console.clear();
	}
  	
  	private int getCurrentPlayerIndex()
  	{
  		if (this.isSoloPlayer())
  		{
	  		int playerIndex = -1;
			
			for (int i = 0; i < this.playersCount; i++)
			{
				if (this.playerReferenceCodes[i] != null)
				{
					playerIndex = i;
					break;
				}
			}
			
			return playerIndex;
  		}
  		else
  		{
  			return this.playerIndexEnteringMoves;
  		}
  	}
  	
  	private int[] getMoneyProductionsOfNearbyPlanets(int planetsNearbyCount)
	{
		int[] result = new int[planetsNearbyCount];
		
		int sum = 0;
		
		for (int i = 0; i < planetsNearbyCount; i++)
		{
			result[i] = this.getRandomProductionOfNeutralPlanet();
			sum += result[i]; 
		}
		
		while (sum != MONEY_PRODUCTION_NEARBY_PLANETS)
		{
			int indexToChange = CommonUtils.getRandomInteger(planetsNearbyCount);
			
			if (sum < MONEY_PRODUCTION_NEARBY_PLANETS &&
					result[indexToChange] < MONEY_PRODUCTION_INITIAL_NEUTRAL + MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA)
			{
				result[indexToChange]++;
				sum++;
			}
			else if (sum > MONEY_PRODUCTION_NEARBY_PLANETS &&
					result[indexToChange] > 1)
			{
				result[indexToChange]--;
				sum--;
			}
		}
		
		return result;
	}
  	
  	private byte getPlanetColorIndex(int planetIndex)
	{
		if (planetIndex == Planet.NO_PLANET)
		{
			return Colors.WHITE;
		}
		else
		{
			return this.planets[planetIndex].getOwnerColorIndex(this);
		}
	}
  	
  	private int getPlayerIndexByName(String userId)
  	{
  		int playerIndex = 1;
  		
  		for (int i = 0; i < this.playersCount; i++)
  		{
  			if (this.players[i].getName().equals(userId))
  			{
  				playerIndex = i;
  				
  				break;
  			}
  		}
  		
  		return playerIndex;
  	}
  	
  	private Point getPositionFromSectorName(String sectorName)
	{
		if (sectorName.length() != 2)
			return null;
		
		int y = -1;
		
		try
		{
			y = sectorName.toUpperCase().codePointAt(0) - 65;
		}
		catch (Exception ex) {}
		
		int x = -1;
		
		try
		{
			x = sectorName.toUpperCase().codePointAt(1) - 65;
		}
		catch (Exception ex) {}
		
		if (x >= 0 && x < BOARD_MAX_X && y >= 0 && y < BOARD_MAX_Y)
			return new Point(x, y);
		else
			return null;
	}
  	
  	private int getRandomProductionOfNeutralPlanet()
  	{
  		int moneyProduction = CommonUtils.getRandomInteger(MONEY_PRODUCTION_INITIAL_NEUTRAL) + 1;
		if (CommonUtils.getRandomInteger(MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W2) < MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W1)
			moneyProduction += (CommonUtils.getRandomInteger(MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA)+1);
		
		return moneyProduction;
  	}
  			
	private void mainLoop()
	{
		if (this.moves == null)
			this.moves = new Hashtable<Integer, ArrayList<Move>>();
		
		if (this.isTutorial())
			this.tutorial.start();
		
		do
		{
			this.console.enablePlanetListContentToggle(true);
			this.updateBoard();
			this.updatePlanetList(false);
			
			this.console.clear();
			
			this.checkIsGameFinalized(false);
			
			this.gameStartOfYear = (Game)CommonUtils.klon(this);
			this.gameStartOfYear.screenContent = (ScreenContent)CommonUtils.klon(this.screenContent);
			
			this.mainMenu();
			
			this.console.setBackground(true);
			new Evaluation(this);
			this.console.setBackground(false);
			
			this.autosave();
			
			this.goToReplay = true;
			new Replay(this);
			
		} while (true);
	}
	
	private void mainMenu()
	{
		if (this.isTutorial())
		{
			this.tutorial.setOpponentsMoves();
		}
		
		do
		{
			boolean readyForEvaluation = false;
			this.setPlayerIndexEnteringMoves(Player.NEUTRAL);

			this.setEnableParameterChange(true);
			
			if (this.goToReplay)
			{
				new Replay(this);
				continue;
			}
			
			ArrayList<Integer> playersAllowedToEnterMoves = new ArrayList<Integer>();
			
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			
			if (this.finalized)
			{
				readyForEvaluation = false;
			}
			else if (this.isSoloPlayer())
			{
				readyForEvaluation = false;
				int soloPlayerIndex = this.getCurrentPlayerIndex();
				
				if (!this.moves.containsKey(soloPlayerIndex))
				{
					playersAllowedToEnterMoves.add(soloPlayerIndex);
				}
			}
			else
			{
				readyForEvaluation = true;

				for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
				{
					Player player = this.players[playerIndex];
					
					if (player.isDead())
					{
						continue;
					}

					if (this.moves.containsKey(playerIndex) == false)
					{
						readyForEvaluation = false;
											
						if (!this.isPlayerEmail(playerIndex))
						{
							playersAllowedToEnterMoves.add(playerIndex);
						}
					}
				}
			}
			
			this.console.setHeaderText(
					this.mainMenuGetYearDisplayText() + " -> "+VegaResources.MainMenu(true), Colors.NEUTRAL);
			
			if (!playersAllowedToEnterMoves.isEmpty())
			{
				if (this.isSoloPlayer())
				{
					allowedKeys.add(new ConsoleKey("TAB",VegaResources.EnterMoves(true)));
				}
				else
				{
					for (int i = 0; i < playersAllowedToEnterMoves.size(); i++)
					{
						int playerIndex = playersAllowedToEnterMoves.get(i);
						
						allowedKeys.add(
								new ConsoleKey(
										Integer.toString(playerIndex + 1), 
										this.players[playerIndex].getName()));
					}
					
					allowedKeys.add(new ConsoleKey("TAB",VegaResources.Random(true)));
				}
			}
			else if (readyForEvaluation)
			{
				allowedKeys.add(new ConsoleKey("TAB",VegaResources.Evaluation(true)));
			}
			
			if (this.evaluationExists())
				allowedKeys.add(new ConsoleKey("7",VegaResources.Replay(true)));
			if (this.year > 0)
				allowedKeys.add(new ConsoleKey("8",VegaResources.Statistics(true)));
			if ((!this.soloPlayer && !isTutorial()) || this.finalized)
				allowedKeys.add(new ConsoleKey("9",VegaResources.GameInfo(true)));
			if (!this.soloPlayer && this.options.contains(GameOptions.EMAIL_BASED) && !this.finalized)
				allowedKeys.add(
						new ConsoleKey("0",VegaResources.EmailActions(true)));
			if (!this.soloPlayer && this.year > 0 && !this.finalized)
				allowedKeys.add(new ConsoleKey("-",VegaResources.Finalize(true)));
			
			ConsoleInput consoleInput = this.console.waitForKeyPressed(allowedKeys);
			
			String input = consoleInput.getInputText().toUpperCase();
			
			if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				this.console.clear();
			else if (!this.finalized && input.equals("\t"))
			{
				if (readyForEvaluation)
				{
					break;
				}
				else if (!playersAllowedToEnterMoves.isEmpty())
				{
					if (this.soloPlayer)
					{
						new EnterMoves(this, this.getCurrentPlayerIndex());
					}
					else
					{
						int randomPlayerIndex = 
								playersAllowedToEnterMoves.get(
										CommonUtils.getRandomInteger(playersAllowedToEnterMoves.size()));
						
						if (!this.isPlayerEmail(randomPlayerIndex))
						{
							new EnterMoves(this, randomPlayerIndex);
						}
					}
				}
				else
				{
					console.appendText(VegaResources.InvalidInput(true));
					console.lineBreak();
				}
			}
			else if (!this.soloPlayer && this.year > 0 && !this.finalized && input.equals("-"))
			{
				this.askFinalizeGame();
			}
			else if (this.evaluationExists() && input.equals("7"))
			{
				new Replay(this);
			}
			else if (input.equals("8") && this.year > 0)
			{
				this.console.clear();
				new Statistics(this, this.finalized || !this.soloPlayer);
			}
			else if (((!this.soloPlayer && !isTutorial()) || this.finalized) && input.equals("9"))
			{
				this.console.clear();
				new GameInformation(this);
			}
			else if (!this.soloPlayer && input.equals("0") && this.options.contains(GameOptions.EMAIL_BASED) && !this.finalized)
			{
				this.console.clear();
				this.emailMenu();
			}
			else
			{
				boolean error = false;
				
				if (!this.soloPlayer)
				{
					try
					{
						int playerIndex = Integer.parseInt(input) - 1;
						
						if (playerIndex >= 0 &&
							playerIndex < this.playersCount &&
							playersAllowedToEnterMoves.contains(playerIndex))
						{
							if (this.isPlayerEmail(playerIndex))
							{
								error = true;
							}
							else
							{
								new EnterMoves(this, playerIndex);
							}
						}
						else
						{
							error = true;
						}
					}
					catch (Exception x)
					{
						error = true;
					}
				}
				else
				{
					error = true;
				}
				
				if (error)
				{
					console.appendText(VegaResources.InvalidInput(true));
					console.lineBreak();
				}
			}
		}   while (true);
		
	}
 		
	private void performAwardCeremony()
	{
		this.console.clear();
		
		int seqArchive[] = CommonUtils.sortValues(this.archive.get(this.year).getScore(), true);		
		
		int position = 1;
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			this.console.setLineColor(this.players[seqArchive[playerIndex]].getColorIndex());
			
			StringBuilder sb = new StringBuilder();
			
			if (playerIndex > 0 && this.archive.get(this.year).getScore()[seqArchive[playerIndex]] < this.archive.get(this.year).getScore()[seqArchive[playerIndex-1]])
				position++;
			
			sb.append(
					VegaResources.FinalizedGamePosition(true, Integer.toString(position)) + " ");

			sb.append(CommonUtils.padStringLeft(this.players[seqArchive[playerIndex]].getName(), Player.PLAYER_NAME_LENGTH_MAX));
			
			sb.append(" " + CommonUtils.padString(this.archive.get(this.year).getPlanetsCount()[seqArchive[playerIndex]], 2) + " "+VegaResources.Planets(true)+" ");
			sb.append(CommonUtils.padString(this.archive.get(this.year).getBattleships()[seqArchive[playerIndex]], 5) + " "+VegaResources.Battleships(true)+" ");
			sb.append(CommonUtils.padString(this.archive.get(this.year).getMoneyProduction()[seqArchive[playerIndex]], 4) + " "+VegaResources.FinalizedGameMoneyProduction(true)+" ");
			sb.append(CommonUtils.padString(this.archive.get(this.year).getScore()[seqArchive[playerIndex]], 5) + " "+VegaResources.Points(true)+".");
			
			this.console.appendText(sb.toString());
			
			if (playerIndex == (Console.TEXT_LINES_COUNT_MAX - 1))
				this.console.waitForKeyPressed();
			else if (playerIndex < playersCount-1)
				this.console.lineBreak();
		}
		
		this.console.waitForKeyPressed();
		this.console.clear();
	}
	
	private void prepareYearSetPrice(ShipType shipType)
	{
		int price = CommonUtils.getRandomInteger((int)(
				Planet.PRICES_MIN_MAX.get(shipType).getMax() - 
				Planet.PRICES_MIN_MAX.get(shipType).getMin() + 1)) + 
				(int)Planet.PRICES_MIN_MAX.get(shipType).getMin();
		
		this.editorPrices.put(shipType, price);
	}
	
	private void save(boolean autoSave)
	{
		this.gameThread.saveGame(this, autoSave);
	}
	
	private void sendGameToEmailPlayer()
	{
		int emailsCount = 0;
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			Player player = this.players[playerIndex];
			if (!this.isPlayerEmail(playerIndex))
				continue;
			
			Game gameCopy = this.createCopyForPlayer(playerIndex);
			
			String subject = "[Vega] " + gameCopy.name;
			
			String bodyText = 
					VegaResources.EmailGameEmailBody(
							false,
							gameCopy.name,
							Integer.toString(gameCopy.year + 1),
							BUILD,
							player.getName());
			
			this.gameThread.launchEmail(
					player.getEmail(), 
					subject, 
					bodyText, 
					gameCopy);
			
			emailsCount++;
		}
		
		if (emailsCount > 0)
		{
			this.console.appendText(
					VegaResources.EmailsWereCreated(true,
						Integer.toString(emailsCount)));
			this.console.lineBreak();
			this.console.appendText(VegaResources.SendEmailToPlayers(true));
			this.console.lineBreak();
		}
	}
	
	private void updateBoard (
			Hashtable<Integer, ArrayList<Byte>> frames, 
			ArrayList<Point> positionsMarked, 
			int day)
	{
		this.updateBoard(frames, positionsMarked, 0, Player.NEUTRAL, day);
	}
	
	private void updateBoardNewGame()
	{
		ArrayList<ScreenContentBoardPlanet> plData = new ArrayList<ScreenContentBoardPlanet>(this.planetsCount);
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			if (this.planets[planetIndex].isNeutral())
				plData.add(new ScreenContentBoardPlanet(
						this.getPlanetNameFromIndex(planetIndex),
						this.planets[planetIndex].getPosition(),
						Colors.NEUTRAL,
						null));
			else
				plData.add(new ScreenContentBoardPlanet(
						this.getPlanetNameFromIndex(planetIndex),
						this.planets[planetIndex].getPosition(),
						Colors.WHITE,
						null));
		}
		
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setBoard(
				new ScreenContentBoard(
						plData,
						null,
						null,
						null));
		
		this.gameThread.updateDisplay(this.screenContent);
	}
	
	private void updatePlanetListAllShips (int playerIndexEnterMoves)
	{
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		ArrayList<ScreenContentPlanetsColoredListHeaderColumn> headers = new ArrayList<ScreenContentPlanetsColoredListHeaderColumn>();
		
		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.FromShort(true),
						2,
						false));
		
		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.CountShort(true),
						5,
						true));
		
		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.TypeShort(true),
						1,
						false,
						true));
		
		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.ToShort(true),
						2,
						false));
		
		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.Arrival(true),
						9,
						false));

		headers.add(
				new ScreenContentPlanetsColoredListHeaderColumn(
						VegaResources.Attributes(true),
						10,
						false));
		
		ArrayList<ShipTravelTime> travelTimes = new ArrayList<ShipTravelTime>();
		
		for (Ship ship: this.ships)
		{
			if (ship.getType() == ShipType.CAPITULATION ||
				ship.getType() == ShipType.BLACK_HOLE)
			{
				continue;
			}
			
			ShipTravelTime travelTime = ship.getTravelTimeRemaining();
			travelTime.ship = ship;
			travelTimes.add(travelTime);
		}
		
		Collections.sort(travelTimes, new ShipTravelTime());
		ArrayList<ScreenContentPlanetsColoredListCellValue[]> lines = new ArrayList<ScreenContentPlanetsColoredListCellValue[]>();
		
		for (ShipTravelTime travelTime: travelTimes)
		{
			Ship ship = travelTime.ship;
			
			ScreenContentPlanetsColoredListCellValue[] values = new ScreenContentPlanetsColoredListCellValue[headers.size()];
			
			values[0] = 
					new ScreenContentPlanetsColoredListCellValue(
							this.getPlanetColorIndex(ship.getPlanetIndexStart()),
							this.getSectorNameFromPosition(ship.getPositionStart()));
			

			values[1] =  
					new ScreenContentPlanetsColoredListCellValue(
							ship.getOwnerColorIndex(this),
							ship.getType() == ShipType.BATTLESHIPS ?
									Integer.toString(ship.getCount()) :
									"1");
			
			values[2] = 
					new ScreenContentPlanetsColoredListCellValue(
							ship.getOwnerColorIndex(this),
							Byte.toString(ship.getScreenDisplaySymbol()));
			
			if (!ship.isStopped())
			{
				values[3] =
						new ScreenContentPlanetsColoredListCellValue(
								this.getPlanetColorIndex(ship.getPlanetIndexDestination()),
								this.getSectorNameFromPosition(ship.getPositionDestination()));
				values[4] = 
						new ScreenContentPlanetsColoredListCellValue(
								Colors.NEUTRAL,
								travelTime.toOutputStringForPlanetList(this.year, true));
			}

			if (ship.getType() == ShipType.BATTLESHIPS && ship.isAlliance())
			{						
				StringBuilder sb = new StringBuilder();
				
				for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
				{
					if (ship.isAllianceMember(playerIndex))
					{
						sb.append(Integer.toString(playerIndex+1));
					}
				}
				
				values[5] =
						new ScreenContentPlanetsColoredListCellValue(
								Colors.NEUTRAL,
								VegaResources.Allied(true, sb.toString()));

			}
			else if (ship.getType() == ShipType.TRANSPORT)
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this),
								VegaResources.MoneyFreight(
									true, 
									Integer.toString(ship.getCount())));
			}
			else if (ship.getType() == ShipType.MINE50)
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this),						
								VegaResources.Mine50Short(true));
			}
			else if (ship.getType() == ShipType.MINE100)
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this), 
								VegaResources.Mine100Short(true));
			}
			else if (ship.getType() == ShipType.MINE250)
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this), 
								VegaResources.Mine250Short(true));
			}
			else if (ship.getType() == ShipType.MINE500)
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this), 
								VegaResources.Mine500Short(true));
			}
			else if ((ship.getType() == ShipType.PATROL || 
					  ship.getType() == ShipType.MINESWEEPER ||
					  ship.getType() == ShipType.SPY) &&
					  ship.isTransfer())
			{
				values[5] = 
						new ScreenContentPlanetsColoredListCellValue(
								ship.getOwnerColorIndex(this), 
								VegaResources.Transfer(true));
			}
			
			lines.add(values);
		}
		
		int pageLength = BOARD_MAX_Y - 2;
		boolean showScrollUp = false;
		boolean showScrollDown = false;
		
		while (planetListContentAllShipsPageCounter > 0 && lines.size() <= pageLength * this.planetListContentAllShipsPageCounter)
		{
			this.planetListContentAllShipsPageCounter--;
		}
		
		showScrollUp = this.planetListContentAllShipsPageCounter > 0;
		showScrollDown = false;
		
		ArrayList<ScreenContentPlanetsColoredListCellValue[]> linesVisible = new ArrayList<ScreenContentPlanetsColoredListCellValue[]>();
		
		if (lines.size() > 0)
		{
			for (int i = planetListContentAllShipsPageCounter * pageLength; i < lines.size(); i++)
			{
				linesVisible.add(lines.get(i));
				
				if (linesVisible.size() >= pageLength)
				{
					break;
				}
			}
			
			showScrollDown = lines.size() > (planetListContentAllShipsPageCounter+1) * pageLength;
		}
		
		ScreenContentPlanetsColoredList coloredList = 
				new ScreenContentPlanetsColoredList(
						showScrollUp,
						showScrollDown,
						Colors.NEUTRAL,
						headers,
						linesVisible);
						
		this.screenContent.setPlanets(
				new ScreenContentPlanets(
						VegaResources.Spaceships(true),
						Colors.NEUTRAL,
						this.console.isEnablePlanetListContentToggle(),
						coloredList));
		
		if (!this.console.isBackground())
			this.gameThread.updateDisplay(this.screenContent);
	}
	
	private void updatePlanetListByShipTypes (ShipType shipTypeDisplay, int playerIndexEnterMoves, boolean newGame)
	{
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Byte> textCol = new ArrayList<Byte>();
		
		int[] sequenceByScore = this.archive.get(this.year) != null
				    ? CommonUtils.sortValues(this.archive.get(this.year).getScore(), true)
				    : CommonUtils.getSequentialList(this.playersCount);
		
	    for (int i = Player.NEUTRAL; i < this.playersCount; i++)
		{
			int playerIndex = i == Player.NEUTRAL ? Player.NEUTRAL : sequenceByScore[i];
			boolean isFirstLine = true;
			
			for (int index = 0; index < this.planetsCount; index++)
			{
				int planetIndex = this.getPlanetsSorted()[index];
				Planet planet = this.planets[planetIndex];
				
				if (planet.getOwner() != playerIndex)
					continue;
				
				if (newGame && planet.getOwner() == Player.NEUTRAL)
					continue;
				
				if (isFirstLine)
				{
					if (playerIndex != Player.NEUTRAL)
					{
						if (shipTypeDisplay == ShipType.ALLIANCES ||
							shipTypeDisplay == ShipType.ACTIVE_SPIES)
						{
							text.add("["+(playerIndex+1)+"]" + this.players[playerIndex].getName());
						}
						else
						{
							text.add(this.players[playerIndex].getName());
						}

						textCol.add(this.players[playerIndex].getColorIndex());
					}
					isFirstLine = false;
				}
				
				String planetName = newGame ?
						" ??" :
						" " + this.getPlanetNameFromIndex(planetIndex);
				
				String shipCount = "";
				int pad = 5;
				
				if (shipTypeDisplay == ShipType.BATTLESHIPS)
				{
					shipCount = Integer.toString(this.planets[planetIndex].getShipsCount(ShipType.BATTLESHIPS));
				}
				else if (shipTypeDisplay == ShipType.ACTIVE_SPIES)
				{
					StringBuilder playerIndices = new StringBuilder();
					
					for (int playerIndex2 = 0; playerIndex2 < this.playersCount; playerIndex2++)
					{
						if (this.planets[planetIndex].hasRadioStation(playerIndex2))
						{
							playerIndices.append(playerIndex2 + 1);
						}
					}
					
					if (playerIndices.length() == 0)
						continue;
					else
						shipCount = playerIndices.toString();
				}
				else if (!planet.areDetailsVisibleForPlayer(playerIndexEnterMoves))
				{
					continue;
				}
				else if (shipTypeDisplay == ShipType.ALLIANCES)
				{
					if (planet.allianceExists())
					{
						boolean[] allianceMembers = planet.getAllianceMembers();
						StringBuilder sbAlliance = new StringBuilder();
						
						for (int playerIndex2 = 0; playerIndex2 < this.playersCount; playerIndex2++)
						{
							if (allianceMembers[playerIndex2] && playerIndex2 != planet.getOwner())
							{
								sbAlliance.append(Integer.toString(playerIndex2 + 1));
							}
						}
						
						shipCount = sbAlliance.toString();
					}
					else
					{
						shipCount = "-";
					}
				}
				else if (shipTypeDisplay == ShipType.DEFENSIVE_BATTLESHIPS)
				{
					shipCount = planet.getDefensiveBattleshipCombatStrengthConcatenated();
					pad = 8;
				}
				else if (shipTypeDisplay == ShipType.MONEY_PRODUCTION)
				{
					shipCount = Integer.toString(planet.getMoneyProduction());
				}
				else if (shipTypeDisplay == ShipType.BATTLESHIP_PRODUCTION)
				{
					shipCount = Integer.toString(planet.getBattleshipProduction());
				}
				else if (shipTypeDisplay == ShipType.MONEY_SUPPLY)
				{
					shipCount = Integer.toString(planet.getMoneySupply());
				}
				else
				{
					shipCount = Integer.toString(this.planets[planetIndex].getShipsCount(shipTypeDisplay));
				}
								
				shipCount = CommonUtils.padString(shipCount, pad);
				
				text.add(planetName.substring(planetName.length()-2, planetName.length()) + 
						":" +
						shipCount);
				
				textCol.add(
						playerIndex == Player.NEUTRAL ?
								Colors.NEUTRAL :
								this.players[playerIndex].getColorIndex());
			}
		}
		
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		String title = "";
		
		switch (shipTypeDisplay)
		{
		case BATTLESHIPS:
			title = VegaResources.Battleships(true);
			break;
		case SPY:
			title = VegaResources.Spies(true);
			break;
		case ACTIVE_SPIES:
			title = VegaResources.ActiveSpies(true);
			break;
		case PATROL:
			title = VegaResources.Patrols(true);
			break;
		case MINE50:
			title = VegaResources.Mine50Plural(true);
			break;
		case MINE100:
			title = VegaResources.Mine100Plural(true);
			break;
		case MINE250:
			title = VegaResources.Mine250Plural(true);
			break;
		case MINE500:
			title = VegaResources.Mine500Plural(true);
			break;
		case TRANSPORT:
			title = VegaResources.Transporters(true);
			break;
		case MINESWEEPER:
			title = VegaResources.Minesweepers(true);
			break;
		case DEFENSIVE_BATTLESHIPS:
			title = VegaResources.DefensiveBattleships(true);
			break;
		case ALLIANCES:
			title = VegaResources.Alliances(true);
			break;
		case BATTLESHIP_PRODUCTION:
			title = VegaResources.BattleshipProduction(true);
			break;
		case MONEY_PRODUCTION:
			title = VegaResources.MoneyProduction(true);
			break;
		case MONEY_SUPPLY:
			title = VegaResources.MoneySupply(true);
			break;
		default:
			title = "";
		}
		
		this.screenContent.setPlanets(
				new ScreenContentPlanets(
						title,
						Colors.NEUTRAL,
						this.console.isEnablePlanetListContentToggle(),
						text, 
						textCol));
		
		if (!this.console.isBackground())
			this.gameThread.updateDisplay(this.screenContent);
	}
	
 	private static class Import
	{
		private static long getDateFromOldVega(String dateString)
		{
			int year = Integer.parseInt(dateString.substring(0, 4)); // - 1900, 
			int month = Integer.parseInt(dateString.substring(4, 6)) -1; 
			int day = Integer.parseInt(dateString.substring(6, 8));

			Date date = new GregorianCalendar(year, month, day).getTime();
			
			return date.getTime();
		}
		// Import games from Vega (Windows 3.11)
		private byte[] bytes;
		
		private int pos;
		
		private Import(byte[] bytes)
		{
			this.pos = 0;
			this.bytes = bytes;
		}
		
		private short getByteValue()
		{
			short b = (short)this.bytes[this.pos];
			if (b < 0)
				b += 256;
			
			this.pos++;
			
			return b;
		}
		private String getDate(short val)
		{
			String day = "0" + Integer.toString(((val + 32767) % (31*100))/100 + 1);
			String month = "0" + Integer.toString((val + 32767) / (100*31) + 1);
			String year = Integer.toString((val + 32767) % 100 + 1980);
			
			return year + month.substring(month.length()-2, month.length()) + day.substring(day.length()-2, day.length()); 
		}
		
		private short inasc()
		{
			return this.getByteValue();
		}
		
		private char inchar()
		{
			return (char)this.getByteValue();
		}
		
		private short inmki()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			
			return (short)(byte0 + byte1 * 256 - 65536);
		}
		
		private int inmkl()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			short byte2 = this.getByteValue();
			short byte3 = this.getByteValue();
			
			return (int)(byte0 + byte1 * 256 + byte2 * 256 * 256 + byte3 * 256 * 256 * 256);
		}
		
		private String instring()
		{
			String retval = "";
			
			while ((char)this.bytes[this.pos] != '\r')
				retval = retval + this.inchar();
			
			this.pos += 2; // because of \r\n
			
			return retval;
		}
		
		private Game start(String gameName)
		{
			Game game = new Game();
			
			game.id = UUID.randomUUID();
			game.name = gameName;
			game.build = BUILD;
			
			if (this.inchar() != 'V')
				return null;
			
			if (this.inasc() != 6)
				return null;
			
			game.initial = false;
			
			game.boardXOffset = 0;
			game.boardYOffset = 0;
			game.boardWidth = BOARD_MAX_X;
			game.boardHeight = BOARD_MAX_Y;
			
			@SuppressWarnings("unused")
			short ladecode = this.inmki();
			
			game.dateStart = getDateFromOldVega(this.getDate(this.inmki()));
			this.inmkl(); // Game duration in seconds -> ignore
			
			short setup = this.inmki();
			this.inmki(); // Battleships at beginning -> ignore
			this.inmki(); // Number of minutes used for entering moves -> ignore
			game.yearMax = this.inmki();
			game.year = this.inmki()-1;
			
			boolean blackHole = false;
			game.options = new HashSet<GameOptions>();
			if ((setup & 8) > 0)
				blackHole = true;
			if (!((setup & 4) > 0 && game.yearMax > 0))
			{
				// Endless game
				game.yearMax = Integer.parseInt(Game.YEARS[Game.YEARS.length - 1]);
			}
			
			game.playersCount = this.inasc();
			game.players = new Player[game.playersCount];
			
			for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
			{
				String playerName = this.instring();
				
				if ((setup & 64) > 0)
					this.inmkl(); // Password -> ignore
				
				if ((setup & 32) > 0)
					this.inasc(); // Former command rooms -> ignore
				
				this.inmkl();
				
				game.players[playerIndex] = new Player(
												playerName, 
												"", 
												(byte)(playerIndex+Colors.COLOR_OFFSET_PLAYERS), 
												false);
			}
			
			game.planetsCount = this.inasc() + 1;
			game.planets = new Planet[game.planetsCount];
			
			for (int planetIndex = 0; planetIndex < game.planetsCount; planetIndex++)
			{
				Hashtable<ShipType, Integer> ships = new Hashtable<ShipType, Integer>();
				
				short battleshipsCount = this.inmki();
				if (battleshipsCount > 0)
					ships.put(ShipType.BATTLESHIPS, (int)battleshipsCount);
				
			    short spiesCount = this.inmki();
			    if (spiesCount > 0)
					ships.put(ShipType.SPY, (int)spiesCount);
			    
				short patrolsCount = this.inmki();
				if (patrolsCount > 0)
					ships.put(ShipType.PATROL, (int)patrolsCount);
				
				short mine50Count = this.inmki();
				if (mine50Count > 0)
					ships.put(ShipType.MINE50, (int)mine50Count);
				
				short mine100Count = this.inmki();
				if (mine100Count > 0)
					ships.put(ShipType.MINE100, (int)mine100Count);
				
				short mine250Count = this.inmki();
				if (mine250Count > 0)
					ships.put(ShipType.MINE250, (int)mine250Count);
				
				short mine500Count = this.inmki();
				if (mine500Count > 0)
					ships.put(ShipType.MINE50, (int)mine500Count);
				
				short transportsCount = this.inmki();
				if (transportsCount > 0)
					ships.put(ShipType.TRANSPORT, (int)transportsCount);
				
				short minesweepersCount = this.inmki();
				if (minesweepersCount > 0)
					ships.put(ShipType.MINESWEEPER, (int)minesweepersCount);
				
				short xpos = this.inasc();
				short ypos = this.inasc();
				
				Point position = new Point(xpos, ypos);
				
				short owner = (short)(this.inasc() - 1);
				
				double defenseShielCount = this.inasc();
				int defensiveBattleshipsCount = 0;
				if (defenseShielCount > 0)
				{
					double intactPercent = this.inasc();
					defensiveBattleshipsCount = 
							Math.max(
									1,
									CommonUtils.round(
											(defenseShielCount * intactPercent / 100) * (double)Game.DEFENSIVE_BATTLESHIPS_BUY_SELL));
				}
				
				short moneySupply = this.inmki();
				short moneyProduction = this.inasc();
				short battleshipProduction = this.inasc();
				
				Alliance alliance = null;
				
				short isAlliance = this.inasc();
				if (isAlliance > 0)
				{
					alliance = new Alliance(game.playersCount);
					
					for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
					{
						int all_pl = this.inmkl();

						if (all_pl >= 0)
							alliance.addBattleshipsCount(playerIndex, all_pl);
					}
				}
			    
			    if (alliance!= null)
			    {
			    	ships.put(ShipType.BATTLESHIPS, alliance.getBattleshipsCount());
			    }
			    
			    game.planets[planetIndex] = 
			    		new Planet(
			    				position, 
			    				alliance, 
			    				ships, 
			    				owner, 
			    				defensiveBattleshipsCount, 
			    				moneySupply, 
			    				moneyProduction, 
			    				battleshipProduction);
			}
			
			short shipsCount = this.inmki();
			game.ships = new ArrayList<Ship>(shipsCount);
			
			if (shipsCount > 0)
			{
				for (int t = 0; t < shipsCount; t++)
				{
					short planetIndexStart = this.inasc();
					short planetIndexDestination = this.inasc();
					short positionStartX = this.inasc();
					short positionStartY = this.inasc();
					Point positionStart = new Point(positionStartX, positionStartY);
					short positionDestinationX = this.inasc();
					short positionDestinationY = this.inasc();
					Point positionDestination = new Point(positionDestinationX, positionDestinationY);
					
					if (!positionStart.equals(game.planets[planetIndexStart].getPosition()))
						planetIndexStart = Planet.NO_PLANET;
					
					if (!positionDestination.equals(game.planets[planetIndexDestination].getPosition()))
						planetIndexDestination = Planet.NO_PLANET;
					
					short yearCount = this.inasc();
					short typeValue = this.inasc();
					
					ShipType type = ShipType.BATTLESHIPS;
					boolean transfer = false;
					
					switch (typeValue)
					{
					case 1:		type = ShipType.BATTLESHIPS;
								transfer = false;
								break;
					case 2:		type = ShipType.SPY;
								transfer = false;
								break;
					case 3:		type = ShipType.PATROL;
								transfer = false;
								break;
					case 4:		type = ShipType.PATROL;
								transfer = true;
								break;
					case 5:		type = ShipType.MINE50;
								transfer = false;
								break;
					case 6:		type = ShipType.MINE100;
								transfer = false;
								break;
					case 7:		type = ShipType.MINE250;
								transfer = false;
								break;
					case 8:		type = ShipType.MINE500;
								transfer = false;
								break;
					case 9:		type = ShipType.MINE50;
								transfer = true;
								break;
					case 10:	type = ShipType.MINE100;
								transfer = true;
								break;
					case 11:	type = ShipType.MINE250;
								transfer = true;
								break;
					case 12:	type = ShipType.MINE500;
								transfer = true;
								break;
					case 13:	type = ShipType.BATTLESHIPS;
								transfer = false;
								break;
					case 14:	type = ShipType.TRANSPORT;
								transfer = false;
								break;
					case 15:	type = ShipType.MINESWEEPER;
								transfer = false;
								break;
					case 16:	type = ShipType.MINESWEEPER;
								transfer = true;
								break;
					case 17:	// Former "black hole" -> ignored
								continue;
					}
					
					short count = this.inmki();
					
					if (type == ShipType.TRANSPORT && count > 90) // Former command room
						count = 0;
					
					Alliance alliance = null;
					if (typeValue == 13) // Allied battleships
					{
						alliance = new Alliance(game.playersCount);
						
						for (int playerIndex = 0; playerIndex < 6; playerIndex++)
						{
							int alliedBattleshipsOfPlayerCount = this.inmkl();
							if (alliedBattleshipsOfPlayerCount >= 0 && playerIndex < game.playersCount)
								alliance.addBattleshipsCount(playerIndex, alliedBattleshipsOfPlayerCount);
						}
					}
					short owner = (short)(this.inasc() - 1);
					if (owner >= game.playersCount)
						owner = Player.NEUTRAL;
					
					this.inmki(); // Former x position -> ignored
					this.inmki(); // Former y position -> ignored
					this.inasc(); // Flag "landed" -> ignored
					
					Ship objekt = new Ship(
							planetIndexStart, 
							planetIndexDestination, 
							positionStart,
							positionDestination,
							yearCount, 
							type,
							count, 
							owner, 
							transfer,
							alliance);
					
					game.ships.add(objekt);
				}
			}
			
			if (blackHole)
			{
				game.yearBlackHoleSwap = this.inmki();
				short loch = this.inasc();
				
				if (loch > 0)
				{
					short blackHoleX = this.inasc();
					short blackHoleY = this.inasc();
					
					game.ships.add(new Ship(
							Planet.NO_PLANET,
							Planet.NO_PLANET,
							new Point(blackHoleX, blackHoleY),
							new Point(blackHoleX, blackHoleY),
							0,
							ShipType.BLACK_HOLE,
							1,
							Player.NEUTRAL,
							false,
							null));
				}
			}
			
			game.archive = new Hashtable<Integer,Archive>();
			
			for (int year = 0; year <= game.year; year++)
			{
				int[] battleshipsCount = new int[game.playersCount];
				int[] planetsCount = new int[game.playersCount];
				int[] moneyProductions = new int[game.playersCount];

				for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
				{
					this.inmkl(); // Scores
				    battleshipsCount[playerIndex] = this.inmkl();
				    planetsCount[playerIndex] = this.inasc();
				    moneyProductions[playerIndex] = this.inmki();
				}
								
				if (year < game.year)
					game.archive.put(year, new Archive(battleshipsCount, planetsCount, moneyProductions));
			}
			
			game.mines = new Hashtable<String,Mine>();
			
			short minesCount = this.inmki();
			if (minesCount > 0)
			{
				for (int t = 0; t < minesCount; t++)
				{
					this.inmki(); // Former mine "history" -> ignore
				}
			}
			
			char dummy = this.inchar();
			
			if (dummy == '1')
			{
				while (this.pos < this.bytes.length)
				{
					short positionX = this.inasc();
					short positionY = this.inasc();
					short strength = this.inmki();
					
					Point position = new Point(positionX,positionY);
					
					Mine mine = game.mines.get(position.getString());
					if (mine == null)
					{
						mine = new Mine((int)position.x, (int)position.y, strength);
						game.mines.put(position.getString(), mine);
					}
					else
						mine.setStrength(strength);
				}
			}
			
			if (game.year >= game.yearMax)
				game.finalized = true;
			
	  		game.replayLast = new ArrayList<ScreenContent>();
	  		
			game.buildPlanetMap();
			game.calculateScores();
			game.prepareYear();
			
			return game;
		}
	}
}
