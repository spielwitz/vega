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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;

import common.EmailTransportBase;
import common.IGameThreadEventListener;
import common.KeyEventExtended;
import common.ScreenContent;
import common.ScreenUpdateEvent;
import common.Game;
import common.GameOptions;
import common.GameThread;
import common.GameThreadCommunicationStructure;
import common.Highscores;
import common.Player;
import common.MovesTransportObject;
import common.VegaResources;
import common.CommonUtils;
import commonServer.PayloadNotificationMessage;
import commonServer.PayloadNotificationNewEvaluation;
import commonServer.ResponseMessageGamesAndUsers;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import commonUi.CommonUiUtils;
import commonUi.FontHelper;
import commonUi.IPanelScreenContentCallback;
import commonUi.PanelScreenContent;
import commonUi.LanguageSelectionJDialog;
import commonUi.VegaAbout;
import commonUi.Toolbar;
import commonUi.UiConstants;
import spielwitz.biDiServer.ClientConfiguration;
import spielwitz.biDiServer.Response;
import spielwitz.biDiServer.ResponseInfo;
import spielwitz.biDiServer.Tuple;
import spielwitz.biDiServer.User;
import uiBaseControls.Frame;
import uiBaseControls.IIconLabelListener;
import uiBaseControls.IMenuItemListener;
import uiBaseControls.IconLabel;
import uiBaseControls.LookAndFeel;
import uiBaseControls.MenuItem;

@SuppressWarnings("serial") 
public class Vega extends Frame // NO_UCD (use default)
	implements 
		IGameThreadEventListener, 
		MouseListener,
		IPanelScreenContentCallback,
		IIconLabelListener,
		IVegaClientCallback,
		IMenuItemListener,
		IMessengerCallback
{
	static final String DEMO_GAME1 = "tutorial/Demo1.VEG";
	static final String DEMO_GAME2 = "tutorial/Demo2.VEG";
	static final String TUTORIAL = "/tutorial/Tutorial.json";
	
	transient private final static String FILE_SUFFIX = ".vega";
	transient private final static String FILE_SUFFIX_BACKUP = ".BAK";
	transient private final static String FILE_SUFFIX_IMPORT = ".VEG";
	
	static
	{
		FontHelper.initialize(UiConstants.FONT_NAME);
		
		LookAndFeel.set(
				new FlatDarkLaf(),
				FontHelper.getFont(UiConstants.FONT_DIALOG_SIZE), 
				10);
	}
	public static void main(String[] args)
	{
		parseCommandLineArguments(args);
		new Vega();
	}
	
	static void showDefaultCursor(Component parentComponent)
	{
		parentComponent.setCursor(Cursor.getDefaultCursor());
	}
	
	static void showServerError(Component parentComponent, ResponseInfo info)
	{
		MessageBox.showError(
				parentComponent,
			    VegaResources.getString(info.getMessage()),
			    VegaResources.ConnectionError(false));
	}
	static void showWaitCursor(Component parentComponent)
	{
		parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	private static void parseCommandLineArguments(String[] args)
	{
		if (args.length == 1)
		{
			VegaConfiguration.setFileName(args[0]);
		}
	}
	
	private VegaClient client;
	
	private VegaConfiguration config;
	
	private String currentGameId;
	private VegaDisplayServer displayServer;
	
	private String fileNameLast;
	private Game gameLastRawData;
	private ImageIcon iconConnected;
    private ImageIcon iconCredentialsLocked;
    private ImageIcon iconDisconnected;
    private ImageIcon iconGames;
    private ImageIcon iconGamesNew;
    private ImageIcon iconMessages;
    private ImageIcon iconMessagesNew;
    private boolean inputEnabled;
    private IconLabel labConnectionStatus;
    private IconLabel labGames;
    private IconLabel labMenu;
    private IconLabel labMessages;
    private MenuItem menuAbout;
    
    private MenuItem menuDemoGame1;
    private MenuItem menuDemoGame2;
    
    private MenuItem menuEmailClipboard;
    
    private MenuItem menuEmailSend;
    private MenuItem menuHelp;
	
	private MenuItem menuHighscore;
	private MenuItem menuLanguage;
	private MenuItem menuLoad;
	private MenuItem menuNewGame;
	private MenuItem menuOutputWindow;
	private MenuItem menuParameters;
	private MenuItem menuQuit;
	
	private MenuItem menuSave;
	private MenuItem menuServer;
	private MenuItem menuServerGames;
	private MenuItem menuServerHighscores;
	private MenuItem menuServerSettings;

	private MenuItem menuTutorial;
	private MenuItem menuWebserver;
	private Messages messages;
	private MessengerJDialog messenger;
	private OutputWindow outputWindow;
	private PanelScreenContent paintPanel;
	private boolean playersWaitingForInput;
	
	private JPopupMenu popupMenu;
	
	private GameThread t;

	private GameThreadCommunicationStructure threadCommunicationStructure;
	
	private TutorialPanel tutorialPanel;
		
	private WebServer webserver;
	private Vega()
	{
		super("", new BorderLayout());
		
		this.config = VegaConfiguration.get();
		
		if (this.config.isFirstTimeStart())
		{
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					null, 
					VegaResources.getLocale(),
					false);
			dlg.setVisible(true);
			this.config.setLocale(dlg.languageCode);
			VegaResources.setLocale(dlg.languageCode);
		}
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.popupMenu = definePopupMenu();
		this.setMenuEnabled();
		
		this.paintPanel = new PanelScreenContent(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		this.iconCredentialsLocked = new ImageIcon (ClassLoader.getSystemResource("credentialsLocked.png"));
		this.iconConnected = new ImageIcon (ClassLoader.getSystemResource("connected.png"));
		this.iconDisconnected = new ImageIcon (ClassLoader.getSystemResource("disconnected.png"));
		this.iconGames = new ImageIcon (ClassLoader.getSystemResource("games.png"));
		this.iconGamesNew = new ImageIcon (ClassLoader.getSystemResource("gamesNew.png"));
		this.iconMessages = new ImageIcon (ClassLoader.getSystemResource("mail.png"));
		this.iconMessagesNew = new ImageIcon (ClassLoader.getSystemResource("mailNew.png"));
		ImageIcon iconMenu = new ImageIcon (ClassLoader.getSystemResource("ic_menu.png"));
		
		this.labMenu = new IconLabel(
				iconMenu, 
				this);
		this.labMenu.setToolTipText(VegaResources.Menu(false));
		
		Toolbar toolbar = new Toolbar(this.labMenu);
		
		this.labMessages = new IconLabel(
				new ImageIcon[] {
						this.iconMessages, 
						this.iconMessagesNew},
				this);
		this.labMessages.setVisible(false);
		
		toolbar.addIconLabel(this.labMessages, 0);
		
		this.labGames = new IconLabel(
				new ImageIcon[] {
						this.iconGames,
						this.iconGamesNew},
				this);
		this.labGames.setVisible(false);
		
		toolbar.addIconLabel(this.labGames, 1);
		
		this.labConnectionStatus = new IconLabel(
				new ImageIcon[] {
						this.iconCredentialsLocked,
						this.iconConnected,
						this.iconDisconnected},
				this);
		this.labConnectionStatus.setVisible(this.config.isServerCommunicationEnabled());
		
		toolbar.addIconLabel(this.labConnectionStatus, 2);
		
		this.add(toolbar, BorderLayout.WEST);
		
		this.tutorialPanel = new TutorialPanel(this);
		this.add(tutorialPanel, BorderLayout.EAST);
		this.tutorialPanel.setVisible(false);
		
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.updateTitle();
		this.paintPanel.requestFocusInWindow();
	}

	@Override
	public void addRecipientsString(String recipientsString)
	{
		synchronized(this.messages)
		{
			if (!this.messages.getMessagesByRecipients().containsKey(recipientsString))
			{
				this.messages.addRecipientsString(recipientsString, new ArrayList<Message>());
			}
		}
	}
	
	@Override
	public void checkMenuEnabled() {
		this.setMenuEnabled();
	}

	@Override
	public void endTutorial()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			this.t.getGame().endTutorial();
		}
		this.stopTutorial();
	}
	
	@Override
	public String getClientUserIdForMessenger()
	{
		return this.messages.getUserId();
	}

	@Override
	public ArrayList<Message> getMessagesByRecipientsString(String recipientsString)
	{
		synchronized(this.messages)
		{
			return this.messages.getMessagesByRecipients().get(recipientsString);
		}
	}
	
	@Override
	public PrivateKey getUserPrivateKey()
	{
		if (this.client != null)
			return this.client.getConfig().getUserPrivateKeyObject();
		else
			return null;
	}
	
	@Override
	public ArrayList<User> getUsersForMessenger()
	{
		if (this.client != null)
		{
			return this.client.getUsers().getPayload().getUsers();
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean hasUnreadMessages(String recipientsString)
	{
		synchronized(this.messages)
		{
			return this.messages.getUnreadMessages().contains(recipientsString);
		}
	}

	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
		this.keyPressed(new KeyEventExtended(arg0, null, languageCode));
	}

	@Override
	public void iconLabelClicked(IconLabel source)
	{
		if (source == this.labMenu)
		{
			Dimension dim = this.labMenu.getSize();
			this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
		}
		else if (source == this.labGames)
		{
			this.openServerGamesDialog();
		}
		else if (source == this.labMessages)
		{
			if (this.messenger == null ||
				!this.messenger.isVisible())
			{
				synchronized(this.messages)
				{
					this.messenger = new MessengerJDialog(this.messages, this);
					this.messenger.setVisible(true);
				}
			}
		}
		else if (source == this.labConnectionStatus)
		{
			ServerCredentials serverCredentials = this.config.getServerCredentials();
			if (serverCredentials.areCredentialsLocked())
			{
				this.unlockServerCredentials();
			}
			else
			{
				this.openServerSettingsDialog();
			}
		}
	}

	@Override
	public MovesTransportObject importMovesFromEmail()
	{
		this.inputEnabled = false;
		this.redrawScreen();

		// ---------
		ClipboardImportJDialog<MovesTransportObject> dlg = 
				new ClipboardImportJDialog<MovesTransportObject>(
						this, MovesTransportObject.class, false);
		
		dlg.setVisible(true);
		// ---------
		
		MovesTransportObject movesTransportObject = (MovesTransportObject)dlg.obj;
		
		if (movesTransportObject != null)
		{
			if (!RequiredBuildChecker.doCheck(this, movesTransportObject.getBuildRequired()))
				movesTransportObject = null;
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
		this.updateTitle();

		return movesTransportObject;
	}
	
	@Override
	public boolean launchEmailClient(String recipient, String subject, String bodyText, EmailTransportBase obj)
	{
		return EmailToolkit.launchEmailClient(
				this,
				recipient, 
				subject, 
				bodyText, 
				null, 
				obj);
	}

	@Override
	public void menuItemSelected(MenuItem source)
	{
		if (source == this.menuLoad)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.loadGame();
			if (game != null)
			{
				this.stopTutorial();
				this.setNewGame(game, false);
			}
						
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuEmailClipboard)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ClipboardImportJDialog<Game> dlg = 
					new ClipboardImportJDialog<Game>(this, Game.class, false);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == MessageBoxResult.OK)
			{
				Game game = (Game)dlg.obj;
				
				if (game != null)
				{
					if (!RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
						game = null;
				}
				
				if (game != null)
				{
					this.stopTutorial();
					this.setNewGame(game, true);
				}
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuNewGame)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ArrayList<String> emailAddresses = this.config.getEmailAddresses();
			
			GameParametersJDialog dlg = 
					new GameParametersJDialog(
							this,
							GameParametersDialogMode.NEW_GAME,
							this.gameLastRawData,
							emailAddresses);
			
			dlg.setVisible(true);
			this.config.setEmailAddresses(emailAddresses);
			
			if (!dlg.isAbort())
			{
				Object[] playersArray = dlg.getPlayers().toArray();
				Player[] players =  Arrays.copyOf(playersArray,playersArray.length,Player[].class);

				Game game = new Game(
						dlg.getOptions(), 
						players, 
						dlg.getPlanetsCount(),
						dlg.getEmailGameHost(), 
						dlg.getYearMax());
				this.gameLastRawData = (Game)CommonUtils.klon(game);
				this.fileNameLast = "";
				this.stopTutorial();
				this.setNewGame(game, false);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuParameters && this.t != null)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.t.getGame();
			
			ArrayList<String> emailAddresses = this.config.getEmailAddresses();
			
			GameParametersJDialog dlg = new GameParametersJDialog(
					this, 
					game.isSoloPlayer() ?
							GameParametersDialogMode.EMAIL_BASED_GAME :
							game.isFinalized() ?
									GameParametersDialogMode.FINALIZED_GAME :
									GameParametersDialogMode.ACTIVE_GAME,
					(Game)CommonUtils.klon(game),
					emailAddresses);
			
			dlg.setVisible(true);
			this.config.setEmailAddresses(emailAddresses);
			
			if (!dlg.isAbort())
				game.changeParameters(dlg.getOptions(), dlg.getYearMax(), dlg.getEmailGameHost(), dlg.getPlayers());
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuEmailSend && this.t != null)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.t.getGame();
			
			EmailCreatorJDialog dlg = new EmailCreatorJDialog(
					this, 
					game.getPlayers(),
					game.getEmailAddressGameHost(),
					this.config.getEmailSeparator(),
					"[VEGA] " + game.getName(),
					"");
			
			dlg.setVisible(true);
			
			if (dlg.launched)
			{
				this.config.setEmailSeparator(dlg.separatorPreset);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuSave && this.t != null)
		{
			this.saveGame(this.t.getGame(), false);
		}
		else if (source == this.menuQuit)
		{
			this.close();
		}
		else if (source == this.menuServer)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			VegaDisplayServerSettingsJDialog dlg = 
					new VegaDisplayServerSettingsJDialog(
							this, 
							this.config.getMyIpAddress(),
							this.config.getDisplayServerPort());

			dlg.setVisible(true);
			
			this.config.setMyIpAddress(dlg.myIpAddress);
			this.config.setDisplayServerPort(dlg.serverPort);
			
			this.updateTitle();
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuServerSettings)
		{
			this.openServerSettingsDialog();
		}
		else if (source == this.menuServerGames)
		{
			this.openServerGamesDialog();
		}
		else if (source == this.menuServerHighscores)
		{
			this.showServerHighscores();
		}
		else if (source == this.menuWebserver)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			WebServerConfigJDialog dlg = 
					new WebServerConfigJDialog(
							this, 
							this.config.getMyIpAddress(), 
							this.config.getWebserverPort() == 0 ?
									WebServer.PORT :
									this.config.getWebserverPort());
			
			dlg.setVisible(true);
			
			if (this.webserver != null)
			{
				this.config.setMyIpAddress(dlg.ipAddress);
				this.config.setWebserverPort(dlg.port);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();

		}
		else if (source == this.menuHighscore)
		{
			this.showHighscoreDialog(Highscores.getInstance());
		}
		else if (source == this.menuHelp)
		{
			CommonUiUtils.showManual(this);
		}
		else if (source == this.menuLanguage)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					this, 
					VegaResources.getLocale(),
					true);
			dlg.setVisible(true);
			
			if (dlg.ok)
			{
				VegaResources.setLocale(dlg.languageCode);
				
				this.config.setLocale(dlg.languageCode);
				
				System.exit(0);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuAbout)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			VegaAbout.show(this);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (source == this.menuOutputWindow)
		{
			if (this.outputWindow == null || !this.outputWindow.isVisible())
			{
				Point windowLocation = this.getLocation();
				Dimension windowSize = this.getSize();
				
				this.outputWindow = new OutputWindow(windowLocation.x + 20, windowLocation.y + 20, windowSize.width/2, windowSize.height/2);
				this.outputWindow.setVisible(true);
				this.redrawScreen();
			}
		}
		else if (source == this.menuTutorial)
		{
			this.loadTutorial();
		}
		else if (source == this.menuDemoGame1)
		{
			this.loadDemoGame(DEMO_GAME1);
		}
		else if (source == this.menuDemoGame2)
		{
			this.loadDemoGame(DEMO_GAME2);
		}
		
		this.setMenuEnabled();
	}
	
	@Override
	public void menuKeyPressed()
	{
		Dimension dim = this.labMenu.getSize();
		this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
	}
	
	@Override
	public void messengerClosed()
	{
		this.config.setMessages(messages);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void onConnectionStatusChanged(boolean connected)
	{
		if (connected)
			this.getGamesWaitingForInput();
		
		this.updateConnectionAndMessageStatus();
	}

	@Override
	public void onMessageReceived(String sender, ArrayList<String> recipients, long dateCreated, String text)
	{
		synchronized(this.messages)
		{
			Tuple<String,Message> messageTuple = this.messages.addMessage(sender, recipients, dateCreated, text);
		
			this.updateConnectionAndMessageStatus();
			
			if (this.messenger != null && this.messenger.isVisible())
			{
				this.messenger.onNewMessageReceived(messageTuple);
			}
		}
	}
	
	@Override
	public void onNewEvaluationAvailable(PayloadNotificationNewEvaluation payload)
	{
		this.playersWaitingForInput = true;
		this.updateConnectionAndMessageStatus();
		
		if (this.currentGameId != null &&
			this.currentGameId.equals(payload.getGameId()))
		{
			this.reloadCurrentGame();
		}
	}
	
	@Override
	public boolean openPdf(byte[] pdfBytes, String clientId)
	{
		return false;
	}

	@Override
	public void pause(int milliseconds)
	{
		this.inputEnabled = false;
		WaitThread t = new WaitThread(this, milliseconds);
		t.start();
	}

	@Override
	public boolean postMovesToServer(String gameId, String playerName, MovesTransportObject movesTransportObject)
	{
		if (this.client != null &&
			this.client.getConfig().getUserId().equals(playerName))
		{		
			showWaitCursor(this);
			ResponseInfo info = this.client.postMoves(gameId, movesTransportObject);
			showDefaultCursor(this);
			
			if (info.isSuccess())
			{
				this.getGamesWaitingForInput();
			}
			else
			{
				showServerError(this, info);
			}
			
			return info.isSuccess();
		}
		else
			return false;
	}

	@Override
	public void pushNotificationFromMessenger(ArrayList<String> recipients, Message message)
	{
		if (this.client != null)
		{
			synchronized(this.messages)
			{
				this.messages.addMessage(
						this.client.getUserId(), 
						recipients, 
						message.getDateCreated(), 
						message.getText());
			}
			
			this.client.pushNotification(recipients, new PayloadNotificationMessage(message.getText()));
		}
	}

	@Override
	public void removeRecipientsString(String recipientsString)
	{
		synchronized(this.messages)
		{
			this.messages.removeRecipientsString(recipientsString);
		}
	}
	
	@Override
	public void saveGame(Game game, boolean autoSave)
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		String fileName ="";
		String directoryName = "";
		
		if (autoSave && this.fileNameLast != null && !this.fileNameLast.isEmpty() &&
				        this.config.getDirectoryNameLast() != null && !this.config.getDirectoryNameLast().isEmpty())
		{
			fileName = this.fileNameLast;
			directoryName = this.config.getDirectoryNameLast();
			
			try {
				this.createBackup(new File(directoryName,fileName).getPath());
			} catch (IOException e) {}
		}
		else
		{
			fileName = null;
			
			do
			{
				JFileChooser fc = new JFileChooser(this.config.getDirectoryNameLast());
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        VegaResources.FileFilterDescription(false), 
				        FILE_SUFFIX.substring(1));
				
				fc.setFileFilter(filter);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setDialogTitle(VegaResources.SaveGame(false));
				
				fc.setSelectedFile(new File(game.getName() + FILE_SUFFIX));

				int returnVal = fc.showSaveDialog(this);
				
				if(returnVal != JFileChooser.APPROVE_OPTION)
				{
					this.inputEnabled = true;
					this.redrawScreen();
					return;
				}
				
				File file = fc.getSelectedFile();
				
				fileName = file.getName();
				directoryName = file.getParent();
				
				if (fileName == null || fileName.equals(""))
				{
					break;
				}
				
				if (fileName.toLowerCase().endsWith(FILE_SUFFIX))
				{
					fileName = fileName.substring(0, fileName.indexOf(FILE_SUFFIX));
				}
				
				if (fileName.length() > 0)
				{
					game.setName(fileName);
					fileName = fileName + FILE_SUFFIX;
					break;
				}
				
			} while (true);
		}
		
		if (fileName != null)
		{
			game.setBuildRequired(Game.BUILD_COMPATIBLE);
				
			File file = new File(directoryName,fileName);
			
			String errorText = game.toFile(file);
			
			if (errorText != null)
				MessageBox.showError(
						this,
					    errorText,
					    "");
			else
			{
				this.config.setDirectoryNameLast(directoryName);
				this.fileNameLast = fileName;
			}
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
		this.updateTitle();
	}

	@Override
	public void setMessagesByRecipientsRead(String recipientsString, boolean read)
	{
		synchronized(this.messages)
		{
			if (read)
				this.messages.getUnreadMessages().remove(recipientsString);
			else
				this.messages.getUnreadMessages().add(recipientsString);
		
			this.updateConnectionAndMessageStatus();
		}
	}
	
	@Override
	public void updateDisplay(ScreenUpdateEvent event)
	{
		if (this.isVegaDisplayServerEnabled())
		{
			ScreenContent screenContent = getScreenContentForOutputWindow();
			if (screenContent == null) screenContent = event.getScreenContent();
			this.displayServer.updateScreen(screenContent);
		}

		if (this.outputWindow != null && this.outputWindow.isVisible())
		{
			ScreenContent screenContent = getScreenContentForOutputWindow();
			if (screenContent == null) screenContent = event.getScreenContent();
			this.outputWindow.redraw(screenContent);
		}

		this.paintPanel.redraw(event.getScreenContent(), this.inputEnabled, false);
	}
	
	@Override
	public void updateTutorialPanel(String text, int currentStep, int totalSteps, boolean enableNextButton)
	{
		this.tutorialPanel.setText(text, currentStep, totalSteps, enableNextButton);
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		if (this.config.isServerCommunicationEnabled())
		{
			this.onConnectionStatusChanged(false);
			this.connectClient();
		}
		
		if (this.config.isFirstTimeStart())
		{
			this.config.setFirstTimeStart(false);
			
			MessageBoxResult result = MessageBox.showYesNo(
					this, 
					VegaResources.TutorialStart(false), 
					VegaResources.TutorialStartTitle(false));
			
			if (result == MessageBoxResult.YES)
			{
				this.loadTutorial();
			}
		}
	}

	void activateWebServer(boolean enabled, int port)
	{
		if (enabled)
		{
			this.webserver = new WebServer(this);
			this.webserver.start(port);
		}
		else if (this.webserver != null)
		{
			this.webserver.stop();
			this.webserver = null;
		}
	}

	VegaConfiguration getConfig()
	{
		return this.config;
	}

	Game getGame()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			return this.t.getGame();
		}
		else
		{
			return null;
		}
	}
	
	ScreenContent getInitialScreenContentForVegaDisplayClient()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			ScreenContent screenContent = this.t.getGame().getScreenContentWhileMovesEntered();
			if (screenContent == null) screenContent = this.paintPanel.getScreenContent();
			return screenContent;
		}
		else
		{
			return null;
		}
	}
	
	byte[] getInventoryPdfBytes(int playerCode)
	{
		if (this.t != null && this.t.getGame() != null)
		{
			return this.t.getGame().getInventoryPdfBytes(playerCode);
		}
		else
		{
			return null;
		}
	}
	
	ScreenContent getScreenContentForOutputWindow()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			return this.t.getGame().getScreenContentWhileMovesEntered();
		}
		else
		{
			return null;
		}
	}
	
	ScreenContent getScreenContentStartOfYear()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			return this.t.getGame().getScreenContentStartOfYear();
		}
		else
		{
			return null;
		}
	}

	String getVegaDisplaySecurityCode()
	{
		return this.displayServer != null ? this.displayServer.getSecurityCode() : "";
	}
	
	ArrayList<String> getVegaDisplayServerConnectedClients()
	{
		if (!this.isVegaDisplayServerEnabled()) return new ArrayList<String>();
		return this.displayServer.getConnectedClients();
	}
	
	WebServer getWebserver()
	{
		return this.webserver;
	}

	boolean isVegaDisplayServerEnabled()
	{
		return this.displayServer != null && this.displayServer.isEnabled();
	}
	
	void setTutorialNextStep()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			this.t.getGame().setTutorialNextStep();
		}
	}
	
	void startVegaDisplayServer(int port)
	{
		this.displayServer = new VegaDisplayServer(this, port);
		this.displayServer.start();
	}

	void stopVegaDisplayServer()
	{
		if (this.displayServer == null) return;
		
		this.displayServer.shutdown();
		this.displayServer = null;
	}

	@Override
	protected boolean confirmClose()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		MessageBoxResult result = MessageBox.showYesNo(
				this,
				VegaResources.DoYouWantToQuitVega(false),
				VegaResources.QuitVega(false));
		
		this.inputEnabled = true;
		this.redrawScreen();
		
		if (result == MessageBoxResult.YES &&
			this.client != null)
		{
			this.disconnectClient();
		}

		return result == MessageBoxResult.YES; 
	}

	private void connectClient()
	{
		ClientConfiguration clientConfiguration = this.config.getClientConfiguration();
		
		if (clientConfiguration == null)
		{
			this.disconnectClient();
			return;
		}
		
		this.client = new VegaClient(clientConfiguration, true, this);
		this.client.start();
		this.messages = this.config.getMessages();
	}

	private void connectDisconnectClient()
	{		
		if (this.config.isServerCommunicationEnabled())
		{
			if (this.client != null)
			{
				this.disconnectClient();
			}
			
			this.connectClient();
		}
		else
		{
			this.disconnectClient();
		}

		this.updateConnectionAndMessageStatus();
		this.setMenuEnabled();
	}

	private void createBackup (String fileName) throws IOException
	{
		InputStream in = null;
		OutputStream out = null; 
		
		if (!new File(fileName).exists())
			return;
		
		String fileNameBackup = fileName + FILE_SUFFIX_BACKUP;
		
		byte[] buffer = new byte[1000000];
		
		try {
			in = new FileInputStream(fileName);
			out = new FileOutputStream(fileNameBackup);
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead); 
				}
			} 
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private JPopupMenu definePopupMenu()
	{
		JPopupMenu popupMenu = new JPopupMenu ();
		
	    this.menuAbout = new MenuItem (VegaResources.AboutVega(false), this);
	    popupMenu.add (this.menuAbout);	    
		
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHelp = new MenuItem (VegaResources.Manual(false), this);
		    popupMenu.add (this.menuHelp);
	    }
	    
	    JMenu menuDemoGames = new JMenu (VegaResources.TutorialAndDemoGames(false));
	    
	    this.menuTutorial = new MenuItem(VegaResources.StartTutorial(false), this);
	    menuDemoGames.add(this.menuTutorial);
	    
	    this.menuDemoGame1 = new MenuItem(VegaResources.LoadDemoGame1(false), this);
	    menuDemoGames.add(this.menuDemoGame1);
	    
	    this.menuDemoGame2 = new MenuItem(VegaResources.LoadDemoGame2(false), this);
	    menuDemoGames.add(this.menuDemoGame2);
	    
	    popupMenu.add(menuDemoGames);
	    
	    popupMenu.addSeparator();
	    
	    this.menuNewGame = new MenuItem (VegaResources.NewLocalGame(false), this);
	    popupMenu.add(this.menuNewGame);
	    
	    this.menuLoad = new MenuItem (VegaResources.LoadLocalGame(false), this);
	    popupMenu.add (menuLoad);
	    
	    this.menuSave = new MenuItem (VegaResources.SaveLocalGameAs(false), this);
	    popupMenu.add (menuSave);
	    
	    this.menuHighscore = new MenuItem(VegaResources.LocalHighScoreList(false), this);
	    popupMenu.add(this.menuHighscore);

	    popupMenu.addSeparator();
	    
	    this.menuEmailClipboard = new MenuItem (VegaResources.ImportGameFromClipboard(false), this);
	    popupMenu.add (menuEmailClipboard);
	    
	    popupMenu.addSeparator();

	    this.menuServerGames = new MenuItem(VegaResources.GamesOnServer(false), this);
	    popupMenu.add(this.menuServerGames);
	    
	    this.menuServerHighscores = new MenuItem(VegaResources.HighScoreListOnServer(false), this);
	    popupMenu.add(this.menuServerHighscores);
	    
	    popupMenu.addSeparator();
	    
	    this.menuEmailSend = new MenuItem(VegaResources.WriteEmail(false), this);
	    popupMenu.add(this.menuEmailSend);
	    
	    this.menuParameters = new MenuItem (VegaResources.GameParameters(false), this);
	    popupMenu.add (this.menuParameters);
	    
	    this.menuOutputWindow = new MenuItem(VegaResources.OpenOutputWindow(false), this);
	    popupMenu.add(this.menuOutputWindow);
	    
	    this.menuWebserver = new MenuItem(VegaResources.WebServer(false), this);
	    popupMenu.add(this.menuWebserver);
	    
	    popupMenu.addSeparator();
	    
	    JMenu menuSettings = new JMenu (VegaResources.Settings(false));
	    
	    this.menuLanguage = new MenuItem(VegaResources.Language(false), this);
	    menuSettings.add(this.menuLanguage);
	    
	    this.menuServerSettings = new MenuItem(VegaResources.ServerSettings(false), this);
	    menuSettings.add(this.menuServerSettings);
	    
	    this.menuServer = new MenuItem(VegaResources.DisplayServer(false), this);
	    menuSettings.add(this.menuServer);
	    
	    popupMenu.add(menuSettings);
	    
	    popupMenu.addSeparator();
	    
	    this.menuQuit = new MenuItem (VegaResources.QuitVega(false), this);
	    popupMenu.add (this.menuQuit);
	    
	    return popupMenu;
	}

	private void disconnectClient()
	{
		if (this.client != null)
		{
			this.config.setMessages(messages);
			this.client.disconnect();
		}
		
		this.client = null;
		this.messages = null;
		
		if (this.messenger != null)
		{
			this.messenger.setVisible(false);
			this.messenger = null;
		}
		
		this.updateConnectionAndMessageStatus();
	}

	private void getGamesWaitingForInput()
	{
		if (this.client == null)
			return;
		
		Response<Boolean> response = this.client.getGamesWaitingForInput();
		
		if (response.getResponseInfo().isSuccess())
		{
			this.playersWaitingForInput = response.getPayload();
			this.updateConnectionAndMessageStatus();
		}
	}

	private Game importGame(String filePath)
	{
		byte[] bytes = null;
		Game game = null;
		
		try {
			Path path = Paths.get(filePath);
			String gameName = path.getFileName().toString();
			if (gameName.contains("."))
				gameName = gameName.substring(0, gameName.indexOf("."));
			
			 bytes = Files.readAllBytes(path);
			 game = Game.importFromVega(gameName, bytes);
		} catch (IOException e)
		{
			game = null;
		}
		
		return game;

	}
	
	private void keyPressed(KeyEventExtended event)
	{
		if (this.inputEnabled && 
			this.threadCommunicationStructure != null&&
			this.t != null &&
			this.t.isAlive())
		{
			synchronized(this.threadCommunicationStructure)
			{
				this.threadCommunicationStructure.keyEvent = event;
				this.threadCommunicationStructure.notify();
			}
		}
	}

	private void loadDemoGame(String gameName)
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
	    InputStream resource = getClass().getResourceAsStream("/"+gameName);
	    
	    byte[] bytes = null;
		try
		{
			bytes = VegaUtils.readAllBytes(resource);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Game game = Game.importFromVega(gameName, bytes);
		this.stopTutorial();
		this.setNewGame(game, false);
					
		this.inputEnabled = true;
		this.redrawScreen();
	}
	
	private Game loadGame()
	{
		Game game = null;
		
		JFileChooser fc = new JFileChooser(this.config.getDirectoryNameLast());
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        VegaResources.FileFilterDescription(false), 
		        FILE_SUFFIX.substring(1),
		        FILE_SUFFIX_BACKUP.substring(1),
		        FILE_SUFFIX_IMPORT.substring(1));
		
		fc.setFileFilter(filter);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle(VegaResources.LoadGame(false));
		
		int returnVal = fc.showOpenDialog(this);
		
		if(returnVal != JFileChooser.APPROVE_OPTION)
		{
			return null;
		}
		
		File file = fc.getSelectedFile();
		
		if (file.exists())
		{
			boolean error = false;
			String	errorText = "";
			boolean importFromOldVega = false;
			
			game = Game.fromFile(file);
			
			if (game == null)
			{
				game = this.importGame(file.getPath());
				
				if (game == null)
				{						
					errorText = VegaResources.FileNotValid(false);
					error = true;
				}
				else
					importFromOldVega = true;
			}
			
			if (error == true)
			{
				MessageBox.showError(
						this,
					    errorText,
					    VegaResources.LoadError(false));
				
				game = null;
			}
			else if (!importFromOldVega)
			{
				if (!RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
					game = null;
			}
			
			if (game != null)
			{
				this.config.setDirectoryNameLast(file.getParent());

				if (!importFromOldVega)
					this.fileNameLast = file.getName();
				
				game.getOptions().remove(GameOptions.SERVER_BASED);
			}
		}
		else
			MessageBox.showError(
					this, 
					VegaResources.FileNotExists(false), 
					VegaResources.LoadError(false));
		
		return game;
	}
	
	private void loadTutorial()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
	    InputStream resource = getClass().getResourceAsStream(TUTORIAL);
	    
	    byte[] bytes = null;
		try
		{
			bytes = VegaUtils.readAllBytes(resource);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	    
	    Game game = Game.fromJson(new String(bytes));	
		this.setNewGame(game, false);
	    game.initializeTutorial();
					
		this.inputEnabled = true;
		this.redrawScreen();
		
		this.tutorialPanel.setVisible(true);
	}
	
	private void openServerGamesDialog()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		if (this.client == null)
		{
			MessageBox.showError(
					this,
				    VegaResources.ServerCredentialsNotEntered(false),
				    VegaResources.Error(false));
		}
		else
		{
			showWaitCursor(this);
			Response<ResponseMessageGamesAndUsers> response = this.client.getGamesAndUsers(this.client.getConfig().getUserId());
			showDefaultCursor(this);
			
			if (response.getResponseInfo().isSuccess())
			{
				ServerGamesJDialog dlg = new ServerGamesJDialog(
						this, 
						this.currentGameId,
						this.client,
						response.getPayload());
				dlg.setVisible(true);
				
				if (dlg.gameLoaded != null)
				{
					if (RequiredBuildChecker.doCheck(this, dlg.gameLoaded.getBuildRequired()))
					{
						this.stopTutorial();
						this.setNewGame(dlg.gameLoaded, true);
					}
				}
				
				this.getGamesWaitingForInput();
			}
			else
			{
				showServerError(this, response.getResponseInfo());
			}
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

	}
	
	private void openServerSettingsDialog()
	{
		if (this.config.getServerCredentials().areCredentialsLocked())
		{
			if (!this.unlockServerCredentials())
			{
				return;
			}
		}
		
		this.inputEnabled = false;
		this.redrawScreen();
		
		if (!this.config.getServerCredentials().containsCredentials())
		{
			ServerCredentialsPasswordJDialog dlg = 
					new ServerCredentialsPasswordJDialog(
							this, 
							this.config.getServerCredentials(),
							ServerCredentialsPasswordJDialogMode.ENTER_PASSWORD_FIRST_TIME);
				
			dlg.setVisible(true);
			
			if (dlg.result != MessageBoxResult.CANCEL)
			{
				this.config.setServerCredentials(dlg.getServerCredentials());
			}
			if (dlg.result != MessageBoxResult.OK)
			{
				this.inputEnabled = true;
				this.redrawScreen();
				return;
			}
		}

		ServerSettingsJDialog dlg = new ServerSettingsJDialog(this, this.config.getServerCredentials());
		dlg.setVisible(true);
		
		if (dlg.ok)
		{
			this.config.setServerCredentials(dlg.getServerCredentials());
			this.connectDisconnectClient();
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
	}
	
	private void redrawScreen ()
	{
		this.updateDisplay(new ScreenUpdateEvent(this, this.paintPanel.getScreenContent()));
	}
	
	private void reloadCurrentGame()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		showWaitCursor(this);
		Response<Game> response = this.client.getGame(this.currentGameId);
		showDefaultCursor(this);
		
		if (response.getResponseInfo().isSuccess())
		{
			Game game = response.getPayload(); 
			
			if (RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
			{
				this.stopTutorial();
				this.setNewGame(game, true);
			}		
		}
		else
		{
			showServerError(this, response.getResponseInfo());
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
	}

	private void resumeAfterPause()
	{
		this.inputEnabled = true;
		synchronized(this.threadCommunicationStructure)
		{
			this.threadCommunicationStructure.notify();
		}
	}

	private void setMenuEnabled()
	{
		if (this.t != null && this.t.getGame() != null && !this.t.getGame().isInitial())
		{
			Game game = this.t.getGame();
			this.menuParameters.setEnabled(
					game.isParameterChangeEnabled() &&
					!game.isTutorial());
			this.menuSave.setEnabled(
					game.isParameterChangeEnabled() && 
					!game.isSoloPlayer() &&
					!game.isTutorial());
			this.menuEmailSend.setEnabled(game.getOptions().contains(
					GameOptions.EMAIL_BASED) ||
					game.getOptions().contains(GameOptions.SERVER_BASED));
		}
		else
		{
			this.menuParameters.setEnabled(false);
			this.menuSave.setEnabled(false);
			this.menuEmailSend.setEnabled(false);
		}
		
		this.menuServerGames.setEnabled(
				this.config.isServerCommunicationEnabled() &&
				this.client != null &&
				!this.client.getConfig().getUserId().equals(User.ADMIN_USER_ID));
		
		this.menuServerHighscores.setEnabled(this.config.isServerCommunicationEnabled());
		
		this.updateTitle();
	}
	
	private void setNewGame(Game game, boolean isSoloPlayer)
	{
		this.inputEnabled = true;
		this.currentGameId = game.getName();
		
		if (this.t == null)
		{
			this.threadCommunicationStructure = new GameThreadCommunicationStructure();
			this.threadCommunicationStructure.isSoloPlayer = isSoloPlayer;
			this.t = new GameThread(this.threadCommunicationStructure, this, game);
			this.t.start();
		}
		else
		{
			synchronized(this.threadCommunicationStructure)
			{
				this.threadCommunicationStructure.gameNew = game;
				this.threadCommunicationStructure.isSoloPlayer = isSoloPlayer;
				this.threadCommunicationStructure.notify();
			}
		}
	}
	
	private void showHighscoreDialog(Highscores highscores)
	{
		if (highscores.getEntries().size() > 0)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			HighscoreJDialog dlg = new HighscoreJDialog(this, true, highscores);
			dlg.setVisible(true);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else
		{
			MessageBox.showInformation(
					this, 
					VegaResources.HighScoresNoEntries(false), 
					VegaResources.HighScoreList(false));
		}
	}

	private void showServerHighscores()
	{
		if (this.client == null)
			return;
		
		Response<Highscores> response = this.client.getHighscores();
		
		if (response.getResponseInfo().isSuccess())
		{
			this.showHighscoreDialog(response.getPayload());
		}
		else
		{
			showServerError(this, response.getResponseInfo());
		}
	}
	
	private void stopTutorial()
	{
		this.tutorialPanel.setVisible(false);
	}
	
	private boolean unlockServerCredentials()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		ServerCredentialsPasswordJDialog dlg = 
				new ServerCredentialsPasswordJDialog(
						this, 
						this.config.getServerCredentials(),
						ServerCredentialsPasswordJDialogMode.UNLOCK_CREDENTIALS);
			
		dlg.setVisible(true);
		
		if (dlg.result != MessageBoxResult.CANCEL)
		{
			this.config.setServerCredentials(dlg.getServerCredentials());
			this.connectDisconnectClient();
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

		return dlg.result == MessageBoxResult.OK;
	}
	
	private void updateConnectionAndMessageStatus()
	{
		this.labConnectionStatus.setVisible(this.config.isServerCommunicationEnabled());
		
		if (this.config.isServerCommunicationEnabled())
		{
			if (this.config.getServerCredentials().areCredentialsLocked())
			{
				this.labConnectionStatus.setIconIndex(0);
				
				this.labConnectionStatus.setToolTipText(
						VegaResources.ServerCredentialsLocked(false));
				
			}
			else if (this.client != null && this.client.isConnected())
			{
				this.labConnectionStatus.setIconIndex(1);
				
				this.labConnectionStatus.setToolTipText(
						VegaResources.ConnectedWithVegaServer(
								false, 
								ServerCredentials.getCredentialsDisplayName(this.client.getConfig())));
			}
			else
			{
				this.labConnectionStatus.setIconIndex(2);
				
				this.labConnectionStatus.setToolTipText(
						VegaResources.ConnectionToServerNotEstablished(false));
			}
		}
				
		this.labGames.setVisible(this.config.isServerCommunicationEnabled() && this.client != null && this.client.isConnected());
		this.labGames.setIconIndex(this.playersWaitingForInput ? 1 : 0);
		this.labGames.setToolTipText(
				this.playersWaitingForInput ?
						VegaResources.PlayersWaitingForInput(false) :
						VegaResources.GamesOnServer(false));
		
		this.labMessages.setVisible(this.config.isServerCommunicationEnabled() && this.client != null && this.client.isConnected() && this.messages != null);
		if (this.messages != null)
		{
			this.labMessages.setIconIndex(this.messages.getUnreadMessages().size() > 0 ? 1 : 0);
			this.labMessages.setToolTipText(
					this.messages.getUnreadMessages().size() > 0 ?
							VegaResources.MessagesUnread(false) :
							VegaResources.Messages(false));
		}
	}
	
	private void updateTitle()
	{
		String fileName = (this.t != null && 
						   this.t.getGame() != null && 
						   this.t.getGame().getName() != null &&
						   this.t.getGame().getName().length() > 0) ?
							" <" + this.t.getGame().getName() + ">" :
							"";
		
		this.setTitle(VegaResources.Vega(false) + fileName);
	}
	
	private class WaitThread extends Thread
	{
		private int milliseconds;
		private Vega parent;
		
		public WaitThread(Vega parent, int milliseconds)
		{
			this.parent = parent;
			this.milliseconds = milliseconds;
		}
		
		@Override
		public void run()
		{
			super.run();
			
			try {
				Thread.sleep(this.milliseconds);
			} catch (InterruptedException e) {
			}
			
			this.parent.resumeAfterPause();
		}
	}
}
