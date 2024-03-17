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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import common.Colors;
import common.CommonUtils;
import common.Game;
import common.GameOptions;
import common.PlanetDistribution;
import common.Player;
import common.VegaResources;
import commonUi.DialogWindow;
import spielwitz.biDiServer.User;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.Frame;
import uiBaseControls.IButtonListener;
import uiBaseControls.IComboBoxListener;
import uiBaseControls.Label;
import uiBaseControls.ListItem;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial") 
class GameParametersJDialog extends Dialog implements IButtonListener, IComboBoxListener, IColorChooserCallback
{
	private final static String ENDLESS_GAME_STRING = VegaResources.Infinite(false);
	static boolean checkEmailSettings(Component c, String emailGameHost, ArrayList<Player> players)
	{
		boolean ok = true;
		
		if (emailGameHost == null || !Pattern.matches(EmailToolkit.EMAIL_REGEX_PATTERN, emailGameHost))
		{
			DialogWindow.showError(
					c,
					VegaResources.EmailAddressGameHostInvalid(false), 
					VegaResources.Error(false));
			return false;
		}
		
		for (Player player: players)
		{
			if (player.isEmailPlayer() && !Pattern.matches(EmailToolkit.EMAIL_REGEX_PATTERN, player.getEmail()))
			{
				ok = false;
				DialogWindow.showError(
						c,
						VegaResources.EmailAddressInvalid(false, player.getName()),
						VegaResources.Error(false));
				break;
			}
		}
		
		return ok;
	}
	private boolean abort;
	private Button butCancel;
	private Button butEmailConfiguration;
	private Button butOk;
	private PlayerColorButton[] canvasPlayerColors;
	
	private Hashtable<GameOptions,CheckBox> cbOptions;
	
	private ComboBox comboPlanets;
	private ComboBox comboPlayers;
	private ComboBox comboYearLast;
	private String emailGameHost;
	private ArrayList<String> emails;
	private GameParametersDialogMode mode;
	private HashSet<GameOptions> options;
	private Panel[] panPlayers;
	private int planetsCount;
	private ArrayList<Player> players;
	private int playersCount;
	
	private TextField[] tfPlayer;
	
	private int yearMax;
	
	GameParametersJDialog (
			Frame parent,
			GameParametersDialogMode mode,
			Game game,
			ArrayList<String> emails)
	{
		super (parent, VegaResources.GameParameters(false), new BorderLayout(10, 10));
		
		this.abort = true;
		
		this.mode = mode;
		this.emails = emails;
		
		this.getInitialValues(game);
		
		this.panPlayers = new Panel[Game.PLAYERS_COUNT_MAX];
		this.tfPlayer = new TextField[Game.PLAYERS_COUNT_MAX];
		this.canvasPlayerColors = new PlayerColorButton[Game.PLAYERS_COUNT_MAX];
		this.cbOptions = new Hashtable<GameOptions,CheckBox>();		
		
		// ---------------
		Panel panParameters = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanParameters = new GridBagConstraints();
		
		cPanParameters.insets = new Insets(5, 5, 5, 5);
		cPanParameters.fill = GridBagConstraints.HORIZONTAL;
		cPanParameters.weightx = 0.5;
		cPanParameters.weighty = 0.5;
		
		Panel panPlanetsSub1 = new Panel(new GridBagLayout());
		
		GridBagConstraints cPanCombo = new GridBagConstraints();
		cPanCombo.insets = new Insets(0, 5, 0, 5);
		cPanCombo.fill = GridBagConstraints.HORIZONTAL;
		cPanCombo.weightx = 0.5;
		cPanCombo.weighty = 0.5;
		
		cPanCombo.gridx = 0; cPanCombo.gridy = 0;
		panPlanetsSub1.add(new Label(VegaResources.Players(false)), cPanCombo);

		String[] players = new String[Game.PLAYERS_COUNT_MAX - Game.PLAYERS_COUNT_MIN + 1];
		for (int playerIndex = Game.PLAYERS_COUNT_MIN; playerIndex <= Game.PLAYERS_COUNT_MAX; playerIndex++)
			players[playerIndex-Game.PLAYERS_COUNT_MIN] = Integer.toString(playerIndex);
		this.comboPlayers = new ComboBox(players, 1, Integer.toString(this.playersCount),this);
		
		cPanCombo.gridx = 1; cPanCombo.gridy = 0;
		panPlanetsSub1.add(this.comboPlayers, cPanCombo);
		
		cPanParameters.gridx = 0; cPanParameters.gridy = 0;
		panParameters.add(panPlanetsSub1, cPanParameters);
		
		Panel panPlanetsSub2 = new Panel(new GridBagLayout());
		
		cPanCombo.gridx = 0; cPanCombo.gridy = 0;
		panPlanetsSub2.add(new Label(VegaResources.Planets(false)), cPanCombo);

		this.comboPlanets = new ComboBox(this.getPlanetComboBoxValues(this.playersCount), 1, Integer.toString(this.planetsCount), this);
		
		cPanCombo.gridx = 1; cPanCombo.gridy = 0;
		panPlanetsSub2.add(this.comboPlanets, cPanCombo);
		
		cPanParameters.gridx = 0; cPanParameters.gridy = 1;
		panParameters.add(panPlanetsSub2, cPanParameters);
		
		cPanParameters.gridx = 0; cPanParameters.gridy = 2;
		Panel panYearMax = new Panel(new GridBagLayout());
		
		cPanCombo.gridx = 0; cPanCombo.gridy = 0;
		panYearMax.add(new Label(VegaResources.Years(false)), cPanCombo);
		
		String[] years = { ENDLESS_GAME_STRING, "15", "20", "30", "40", "50", "75", "100", "150", "200" };
		this.comboYearLast = new ComboBox(years, 12, null, null);
		
		cPanCombo.gridx = 1; cPanCombo.gridy = 0;
		panYearMax.add(this.comboYearLast, cPanCombo);
		
		panParameters.add(panYearMax, cPanParameters);

		cPanParameters.gridx = 0; cPanParameters.gridy = 3;
		CheckBox cbAutoSave = new CheckBox(VegaResources.AutomaticSave(false), true, null);
		this.cbOptions.put(GameOptions.AUTO_SAVE, cbAutoSave);
		panParameters.add(cbAutoSave, cPanParameters);
						
		cPanParameters.gridx = 0; cPanParameters.gridy = 4;
		CheckBox cbEmail = new CheckBox(VegaResources.EmailMode(false), false, null);
		this.cbOptions.put(GameOptions.EMAIL_BASED, cbEmail);
		panParameters.add(cbEmail, cPanParameters);
		
		cPanParameters.gridx = 0; cPanParameters.gridy = 5;
		this.butEmailConfiguration = new Button(VegaResources.EmailSettings(false), this);
		panParameters.add(this.butEmailConfiguration, cPanParameters);		
		
		for (int i = 0; i < Game.PLAYERS_COUNT_MAX; i++)
		{
			cPanParameters.gridx = 1;
			cPanParameters.gridy = i;
			
			panParameters.add(this.getPlayerPanel(i), cPanParameters);
		}
		
		this.addToInnerPanel(panParameters, BorderLayout.CENTER);
		
		// ----
		
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new Button(VegaResources.Cancel(false), this);
		panButtons.add(this.butCancel);
		
		this.butOk = new Button(VegaResources.OK(false), this);
		panButtons.add(this.butOk);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		
		// ---
		
		this.setInitialControlValues();
		this.setControlsEnabled();
		
		this.pack();
		this.setLocationRelativeTo(parent);	
	}
	
	@Override
	public void buttonClicked(Button button)
	{
		if (button == this.butCancel)
		{
			this.abort = true;
			this.close();
		}
		else if (button == this.butEmailConfiguration)
		{
			boolean ok = this.getPlayersFromControls(
					this.players,
					Integer.parseInt((String)this.comboPlayers.getSelectedItem()));
			
			if (ok)
			{
				EmailSettingsJDialog dlg = new EmailSettingsJDialog(
												this,
												this.emailGameHost,
												this.players,
												this.emails,
												this.mode == GameParametersDialogMode.FINALIZED_GAME ||
												this.mode == GameParametersDialogMode.EMAIL_BASED_GAME);
				
				dlg.setVisible(true);
				
				this.emailGameHost = dlg.emailGameHost;
				
				for (int playerIndex = 0; playerIndex < dlg.players.size(); playerIndex++)
				{
					this.players.get(playerIndex).setEmailPlayer(dlg.players.get(playerIndex).isEmailPlayer());
					this.players.get(playerIndex).setEmail(dlg.players.get(playerIndex).getEmail());
				}
			}
		}
		else if (button == this.butOk)
		{
			boolean ok = true;
			
			this.playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
			this.planetsCount = Integer.parseInt((String)this.comboPlanets.getSelectedItem());
			
			ok = this.getPlayersFromControls(this.players, this.playersCount);
			
			for (GameOptions option: this.cbOptions.keySet())
			{
				if (this.cbOptions.get(option).isSelected())
					this.options.add(option);
				else
					this.options.remove(option);
			}
			
			String yearMaxString = (String)this.comboYearLast.getSelectedItem();
			
			if (yearMaxString.equals(ENDLESS_GAME_STRING))
			{
				this.yearMax = 0;
				this.options.remove(GameOptions.LIMITED_NUMBER_OF_YEARS);
			}
			else
			{
				this.yearMax = Integer.parseInt(yearMaxString);
				this.options.add(GameOptions.LIMITED_NUMBER_OF_YEARS);
			}
			
			if (ok)
			{
				if (this.options.contains(GameOptions.EMAIL_BASED))
					ok = checkEmailSettings(this, this.emailGameHost, this.players);
			}
			
			if (ok)
			{
				this.abort = false;
				this.close();
			}
		}
	}
	
	@Override
	public void colorChanged(int playerIndex, byte newColorIndex, byte oldColorIndex)
	{
		for (PlayerColorButton c: this.canvasPlayerColors)
		{
			if (c.playerIndex != playerIndex && c.colorIndex == newColorIndex)
			{
				c.setColor(oldColorIndex);
				break;
			}
		}
	}
	
	@Override
	public void comboBoxItemSelected(ComboBox source, String selectedValue)
	{
		if (source == this.comboPlayers)
		{
			this.playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
			this.comboPlanets.setItems(this.getPlanetComboBoxValues(this.playersCount));
			this.comboPlanets.setSelectedIndex(0);
			
			this.setControlsEnabled();
			this.pack();
		}
		else if (source == this.comboPlanets)
		{
			this.planetsCount = Integer.parseInt((String)this.comboPlanets.getSelectedItem());
			
			this.setControlsEnabled();
		}
	}
	
	@Override
	public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
	{
	}
	
	public String getEmailGameHost()
	{
		return this.emailGameHost;
	}
	
	public HashSet<GameOptions> getOptions() {
		return options;
	}

	public int getPlanetsCount()
	{
		return this.planetsCount;
	}
	
	public ArrayList<Player> getPlayers()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			players.add((Player)CommonUtils.klon(this.players.get(playerIndex)));
			
		return players;
	}

	public int getYearMax() {
		return yearMax;
	}
	
	public boolean isAbort() {
		return abort;
	}
	
	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void getInitialValues(Game game)
	{
		if (game == null)
		{
			this.players = Game.getPlayersDefault();
			this.options = Game.getOptionsDefault();
			
			this.emailGameHost = "";
			
			this.playersCount = Game.PLAYERS_COUNT_DEFAULT;
			this.planetsCount = Game.PLANETS_COUNT_MAX;
			this.yearMax = Game.YEARS_COUNT_MAX_DEFAULT;
		}
		else
		{
			this.playersCount = game.getPlayersCount();
			this.planetsCount = game.getPlanetsCount();
			
			this.emailGameHost = game.getEmailAddressGameHost();
			
			this.options = (HashSet<GameOptions>)CommonUtils.klon(game.getOptions());
			
			if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			{
				this.yearMax = game.getYearMax();
			}
			else
				this.yearMax = Game.YEARS_COUNT_MAX_DEFAULT;
			
			this.players = new ArrayList<Player>();
			ArrayList<Player> playersDefault = Game.getPlayersDefault();
			
			boolean[] colorIndicesUsed = new boolean[Game.PLAYERS_COUNT_MAX  + Colors.COLOR_OFFSET_PLAYERS];
			
			for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
			{
				if (playerIndex < this.playersCount)
				{
					colorIndicesUsed[game.getPlayers()[playerIndex].getColorIndex()] = true;
					players.add((Player)CommonUtils.klon(game.getPlayers()[playerIndex]));
				}
				else
				{
					byte colorIndexTemp = playersDefault.get(playerIndex).getColorIndex();
					
					for (int playerIndex2 = 0; playerIndex2 < Game.PLAYERS_COUNT_MAX; playerIndex2++)
					{
						if (!colorIndicesUsed[colorIndexTemp])
						{
							colorIndicesUsed[colorIndexTemp] = true;
							players.add(new Player("", "", colorIndexTemp, false));
							break;
						}
						colorIndexTemp = (byte)((colorIndexTemp + 1) % Game.PLAYERS_COUNT_MAX);
					}
				}
			}
		}		
	}
	
	private String[] getPlanetComboBoxValues(int playersCount)
	{
		int planetCountMin = 
				this.mode == GameParametersDialogMode.NEW_GAME ?
						PlanetDistribution.getPlanetCountMin(playersCount) :
						Game.PLAYERS_COUNT_MAX;
		
		String[] planets = new String[Game.PLANETS_COUNT_MAX - planetCountMin + 1];
		for (int i = planetCountMin; i <= Game.PLANETS_COUNT_MAX; i++)
			planets[i - planetCountMin] = Integer.toString(i);

		return planets;
	}

	private Panel getPlayerPanel(int playerIndex)
	{
		this.panPlayers[playerIndex] = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		canvasPlayerColors[playerIndex] = new PlayerColorButton(
				this, 
				this,
				playerIndex, 
				this.players.get(playerIndex).getColorIndex());
		canvasPlayerColors[playerIndex].setPreferredSize(new Dimension(14,14));
		this.panPlayers[playerIndex].add(canvasPlayerColors[playerIndex]);
		
		this.tfPlayer[playerIndex] = new TextField(
				this.players.get(playerIndex).getName(), 
				Player.PLAYER_NAME_REGEX_PATTERN,
				20,
				Player.PLAYER_NAME_LENGTH_MAX,
				null);
		
		this.panPlayers[playerIndex].add(this.tfPlayer[playerIndex]);
				
		return this.panPlayers[playerIndex];
	}

	private boolean getPlayersFromControls(ArrayList<Player> players, int playersCount)
	{
		if (players == null)
			players = new ArrayList<Player>();
		
		for (int playerIndex = players.size() - 1; playerIndex >= playersCount; playerIndex--)
			players.remove(playerIndex);
		
		boolean ok = true;
		
		for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
		{						
			this.tfPlayer[playerIndex].setText(this.tfPlayer[playerIndex].getText().trim());
			
			Player player = null;
			
			if (playerIndex < players.size())
			{
				player = players.get(playerIndex);
				
				player.setName(this.tfPlayer[playerIndex].getText());
				player.setColorIndex(this.canvasPlayerColors[playerIndex].colorIndex);
			}
			else
			{
				player = new Player(this.tfPlayer[playerIndex].getText(), "", 
						this.canvasPlayerColors[playerIndex].colorIndex, false);
				
				players.add(player);
			}
			
			boolean isUserNameAllowed = 
					(player.getName().length() >= Player.PLAYER_NAME_LENGTH_MIN &&
					player.getName().length() <= Player.PLAYER_NAME_LENGTH_MAX &&
					!player.getName().toLowerCase().equals(User.ACTIVATION_USER_ID.toLowerCase()) &&
					Pattern.matches(Player.PLAYER_NAME_REGEX_PATTERN, player.getName())
					);
			
			if (this.tfPlayer[playerIndex].isEditable() && !isUserNameAllowed)
			{
				DialogWindow.showError(
						this,
						VegaResources.UserNameInvalid(
								false, 
								player.getName(), 
								Integer.toString(Player.PLAYER_NAME_LENGTH_MIN), 
								Integer.toString(Player.PLAYER_NAME_LENGTH_MAX)),
						VegaResources.Error(false));
				ok = false;
				break;
			}
		}

		return ok;
	}
	
	private void setControlsEnabled()
	{
		boolean enabled = (this.mode != GameParametersDialogMode.FINALIZED_GAME &&
				   		   this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);

		for (GameOptions option: this.cbOptions.keySet())
		{
			if (option == GameOptions.AUTO_SAVE)
				this.cbOptions.get(option).setEnabled(enabled);
			else if (option == GameOptions.EMAIL_BASED)
				this.cbOptions.get(option).setEnabled(enabled);			
		}
		
		this.comboYearLast.setEnabled(enabled);
		this.comboPlayers.setEnabled(this.mode == GameParametersDialogMode.NEW_GAME);
		this.comboPlanets.setEnabled(this.mode == GameParametersDialogMode.NEW_GAME);
		
		for (int playerIndex = 0; playerIndex < Game.PLAYERS_COUNT_MAX; playerIndex++)
		{
			if (playerIndex < this.playersCount)
			{
				this.panPlayers[playerIndex].setEnabled(true);
				this.canvasPlayerColors[playerIndex].showColor(true);
				this.canvasPlayerColors[playerIndex].setEnabled(enabled);
				this.tfPlayer[playerIndex].setEnabled(enabled);
				
				if (this.tfPlayer[playerIndex].getText().length() == 0)
				{
					this.tfPlayer[playerIndex].setText(VegaResources.Player(false)+(playerIndex+1));
				}
			}
			else
			{
				this.panPlayers[playerIndex].setEnabled(false);
				this.canvasPlayerColors[playerIndex].showColor(false);
				this.canvasPlayerColors[playerIndex].setEnabled(false);
				this.tfPlayer[playerIndex].setEnabled(false);
				this.tfPlayer[playerIndex].setText("");
			}
		}
	}
	
	private void setInitialControlValues()
	{
		for (GameOptions option: this.cbOptions.keySet())
			this.cbOptions.get(option).setSelected(this.options.contains(option));

		if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			this.comboYearLast.setSelectedItem(Integer.toString(this.yearMax));
		else
			this.comboYearLast.setSelectedItem(ENDLESS_GAME_STRING);
		
	}
}
