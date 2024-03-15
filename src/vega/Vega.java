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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;

import common.EmailTransportBase;
import common.IGameThreadEventListener;
import common.KeyEventExtended;
import common.ScreenContent;
import common.ScreenContentClient;
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
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.FontHelper;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
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
import uiBaseControls.IconLabel;
import uiBaseControls.LookAndFeel;

@SuppressWarnings("serial") 
public class Vega extends Frame // NO_UCD (use default)
	implements 
		IGameThreadEventListener, 
		ActionListener,
		MouseListener,
		IServerMethods,
		IHostComponentMethods,
		IIconLabelListener,
		IVegaClientCallback,
		IMessengerCallback
{
	transient private final static String FILE_SUFFIX = ".vega";
	transient private final static String FILE_SUFFIX_BACKUP = ".BAK";
	transient private final static String FILE_SUFFIX_IMPORT = ".VEG";
	
	static final String DEMO_GAME1 = "tutorial/Demo1.VEG";
	static final String DEMO_GAME2 = "tutorial/Demo2.VEG";
	static final String TUTORIAL = "/tutorial/Tutorial.json";
	
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
	
	private static void parseCommandLineArguments(String[] args)
	{
		if (args.length == 1)
		{
			VegaConfiguration.setFileName(args[0]);
		}
	}
	
	static void showDefaultCursor(Component parentComponent)
	{
		parentComponent.setCursor(Cursor.getDefaultCursor());
	}
	static void showServerError(Component parentComponent, ResponseInfo info)
	{
		DialogWindow.showError(
				parentComponent,
			    VegaResources.getString(info.getMessage()),
			    VegaResources.ConnectionError(false));
	}
	
	static void showWaitCursor(Component parentComponent)
	{
		parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	private GameThread t;
	
	private GameThreadCommunicationStructure threadCommunicationStructure;
	
	private Game gameLastRawData;
	private String fileNameLast;
	
	private VegaConfiguration config;
	private VegaDisplayFunctions serverFunctions;
	private VegaClient client;
    private boolean playersWaitingForInput;
    private JPopupMenu popupMenu;
    private JMenuItem menuTutorial;
    private JMenuItem menuDemoGame1;
    private JMenuItem menuDemoGame2;
    private JMenuItem menuNewGame;
    private JMenuItem menuLoad;
    private JMenuItem menuEmailClipboard;
    private JMenuItem menuParameters;
    private JMenuItem menuEmailSend;
    private JMenuItem menuSave;
    private JMenuItem menuServerHighscores;
    
    private JMenuItem menuServerAdmin;
    private JMenuItem menuServerGames;
    
    private JMenuItem menuServerCredentials;
    
    private JMenuItem menuLanguage;
    
    private JMenuItem menuOutputWindow;
    private JMenuItem menuServer;
	
	private JMenuItem menuWebserver;
	private JMenuItem menuHighscore;
	private JMenuItem menuQuit;
	private JMenuItem menuHelp;
	private JMenuItem menuAbout;
	private PanelScreenContent paintPanel;
	private TutorialPanel tutorialPanel;
	
	private OutputWindow outputWindow;
	private IconLabel labConnectionStatus;
	private IconLabel labGames;
	private IconLabel labMessages;
	private IconLabel labMenu;
	private ImageIcon iconConnected;
	
	private ImageIcon iconDisconnected;
	private ImageIcon iconGames;
	
	private ImageIcon iconGamesNew;
	
	private ImageIcon iconMessages;
	private ImageIcon iconMessagesNew;
	
	private boolean inputEnabled;
	
	private String currentGameId;

	private WebServer webserver;
	
	private Messages messages;
		
	private MessengerJDialog messenger;
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
				this.iconMessages, 
				this.iconMessagesNew,
				this);
		this.labMessages.setVisible(false);
		
		toolbar.addIconLabel(this.labMessages, 0);
		
		this.labGames = new IconLabel(
				this.iconGames,
				this.iconGamesNew,
				this);
		this.labGames.setVisible(false);
		
		toolbar.addIconLabel(this.labGames, 1);
		
		this.labConnectionStatus = new IconLabel(
				this.iconConnected,
				this.iconDisconnected,
				this);
		this.labConnectionStatus.setVisible(this.config.isServerCommunicationEnabled());
		
		toolbar.addIconLabel(this.labConnectionStatus, 2);
		
		this.add(toolbar, BorderLayout.WEST);
		
		this.tutorialPanel = new TutorialPanel(this);
		this.add(tutorialPanel, BorderLayout.EAST);
		this.tutorialPanel.setVisible(false);
		
		this.serverFunctions = new VegaDisplayFunctions(this.config.getMyIpAddress());
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.updateTitle();
		this.paintPanel.requestFocusInWindow();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JMenuItem JMenuItem = (JMenuItem)e.getSource();
		
		if (JMenuItem == this.menuLoad)
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
		else if (JMenuItem == this.menuEmailClipboard)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ClipboardImportJDialog<Game> dlg = 
					new ClipboardImportJDialog<Game>(this, Game.class, false);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == DialogWindowResult.OK)
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
		else if (JMenuItem == this.menuNewGame)
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
		else if (JMenuItem == this.menuParameters && this.t != null)
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
		else if (JMenuItem == this.menuEmailSend && this.t != null)
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
		else if (JMenuItem == this.menuSave && this.t != null)
		{
			this.saveGame(this.t.getGame(), false);
		}
		else if (JMenuItem == this.menuQuit)
		{
			this.close();
		}
		else if (JMenuItem == this.menuServer)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			VegaDisplaySettingsJDialog dlg = 
					new VegaDisplaySettingsJDialog(
							this, 
							this.config.getMyIpAddress(),
							this.serverFunctions);

			dlg.setVisible(true);
			
			this.config.setMyIpAddress(dlg.myIpAddress);
			
			this.updateTitle();
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (JMenuItem == this.menuServerAdmin)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			VegaServerAdminJDialog dlg = new VegaServerAdminJDialog(
					this, 
					this.config.getServerAdminCredentialFile());
			dlg.setVisible(true);
			
			this.config.setServerAdminCredentialFile(dlg.serverAdminCredentialsFile);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (JMenuItem == this.menuServerCredentials)
		{
			this.openServerCredentialsDialog();
		}
		else if (JMenuItem == this.menuServerGames)
		{
			this.openServerGamesDialog();
		}
		else if (JMenuItem == this.menuServerHighscores)
		{
			this.showServerHighscores();
		}
		else if (JMenuItem == this.menuWebserver)
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
		else if (JMenuItem == this.menuHighscore)
		{
			this.showHighscoreDialog(Highscores.getInstance());
		}
		else if (JMenuItem == this.menuHelp)
		{
			this.openManual();
		}
		else if (JMenuItem == this.menuLanguage)
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
		else if (JMenuItem == this.menuAbout)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			VegaAbout.show(this);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (JMenuItem == this.menuOutputWindow)
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
		else if (JMenuItem == this.menuTutorial)
		{
			this.loadTutorial();
		}
		else if (JMenuItem == this.menuDemoGame1)
		{
			this.loadDemoGame(DEMO_GAME1);
		}
		else if (JMenuItem == this.menuDemoGame2)
		{
			this.loadDemoGame(DEMO_GAME2);
		}
		
		this.setMenuEnabled();
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
		return this.client.getUserId();
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
			this.openServerCredentialsDialog();
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
	public boolean isMoveEnteringOpen() 
	{
		if ((this.serverFunctions != null && this.serverFunctions.isServerEnabled() && !this.areClientsInactiveWhileEnterMoves()))
		{
			return false;
		}
		
		return
				(this.outputWindow != null && this.outputWindow.isVisible()) ||
				(this.serverFunctions != null && this.serverFunctions.isServerEnabled() && this.areClientsInactiveWhileEnterMoves());
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
	public void menuKeyPressed()
	{
		Dimension dim = this.labMenu.getSize();
		this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
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
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			return this.serverFunctions.openPdf(pdfBytes, clientId);
		else
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
	public boolean rmiClientCheckRegistration(String clientId)
	{
		return this.serverFunctions.isClientRegistered(clientId);
	}
	
	@Override
	public String rmiClientConnectionRequest(
			String clientId, 
			String release, 
			String ip,
			String clientCode, 
			String clientName) 
					throws RemoteException
	{
		if (release.equals(Game.BUILD))
			return this.serverFunctions.connectClient(
					clientId, 
					ip, 
					clientCode, 
					clientName, 
					this.config.isClientsInactiveWhileEnterMoves());
		else
			return VegaResources.ClientServerDifferentBuilds(false);
	}
	
	@Override
	public void rmiClientLogoff(String clientId) throws RemoteException
	{
		this.serverFunctions.disconnectClient(clientId);
	}
	
	@Override
	public ScreenContentClient rmiGetCurrentScreenDisplayContent(String clientId)
			throws RemoteException
	{
		if (this.serverFunctions.isClientRegistered(clientId))
		{
			ScreenContentClient contentClient = new ScreenContentClient();
			contentClient.screenContent = this.paintPanel.getScreenContent();
			contentClient.inputEnabled = this.inputEnabled;
			
			return contentClient; 
		}
		else
			return null;
	}
	
	@Override
	public void rmiKeyPressed(String clientId, String languageCode, int id, long when, int modifiers, int keyCode, char keyChart) throws RemoteException
	{
		KeyEvent event = new KeyEvent(this.paintPanel, id, when, modifiers, keyCode, keyChart);
		
		if (this.serverFunctions.isClientRegistered(clientId))
			this.keyPressed(new KeyEventExtended(event, clientId, languageCode));
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
				DialogWindow.showError(
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
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
		{
			ScreenContent screenContent = null;
			
			if (this.t != null && this.t.getGame() != null)
			{
				screenContent = this.t.getGame().getScreenContentWhileMovesEntered();
			}
			
			this.serverFunctions.updateClients(
					event.getScreenContent(), 
					screenContent, 
					this.inputEnabled);
		}

		if (this.outputWindow != null && this.outputWindow.isVisible())
		{
			ScreenContent screenContent = null;
			if (this.t != null && this.t.getGame() != null)
				screenContent = this.t.getGame().getScreenContentWhileMovesEntered();
			
			this.outputWindow.redraw(
					screenContent != null ? screenContent : event.getScreenContent());
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
			
			DialogWindowResult result = DialogWindow.showYesNo(
					this, 
					VegaResources.TutorialStart(false), 
					VegaResources.TutorialStartTitle(false));
			
			if (result == DialogWindowResult.YES)
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

	boolean areClientsInactiveWhileEnterMoves() // NO_UCD (use default)
	{
		return this.config.isClientsInactiveWhileEnterMoves();
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
	
	WebServer getWebserver()
	{
		return this.webserver;
	}

	void setClientsInactiveWhileEnterMoves(boolean enabled)
	{
		this.config.setClientsInactiveWhileEnterMoves(enabled);
	}
	
	void setTutorialNextStep()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			this.t.getGame().setTutorialNextStep();
		}
	}
	
	@Override
	protected boolean confirmClose()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		DialogWindowResult result = DialogWindow.showYesNo(
				this,
				VegaResources.DoYouWantToQuitVega(false),
				VegaResources.QuitVega(false));
		
		this.inputEnabled = true;
		this.redrawScreen();
		
		if (result == DialogWindowResult.YES &&
			this.client != null)
		{
			this.disconnectClient();
		}

		return result == DialogWindowResult.YES; 
	}
	
	private void connectClient()
	{
		ClientConfiguration clientConfiguration = ClientConfiguration.readFromFile(this.config.getServerUserCredentialsFile());
		
		if (clientConfiguration == null)
		{
			this.disconnectClient();
			return;
		}
		
		this.client = new VegaClient(clientConfiguration, true, this);
		this.client.start();
		this.messages = Messages.readFromFile(this.client.getUserId());
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
		
	    this.menuAbout = new JMenuItem (VegaResources.AboutVega(false));
	    this.menuAbout.addActionListener(this);
	    popupMenu.add (this.menuAbout);	    
		
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHelp = new JMenuItem (VegaResources.Manual(false));
		    this.menuHelp.addActionListener(this);
		    popupMenu.add (this.menuHelp);
	    }
	    
	    JMenu menuDemoGames = new JMenu (VegaResources.TutorialAndDemoGames(false));
	    
	    this.menuTutorial = new JMenuItem(VegaResources.StartTutorial(false));
	    this.menuTutorial.addActionListener(this);
	    menuDemoGames.add(this.menuTutorial);
	    
	    this.menuDemoGame1 = new JMenuItem(VegaResources.LoadDemoGame1(false));
	    this.menuDemoGame1.addActionListener(this);
	    menuDemoGames.add(this.menuDemoGame1);
	    
	    this.menuDemoGame2 = new JMenuItem(VegaResources.LoadDemoGame2(false));
	    this.menuDemoGame2.addActionListener(this);
	    menuDemoGames.add(this.menuDemoGame2);
	    
	    popupMenu.add(menuDemoGames);
	    
	    popupMenu.addSeparator();
	    
	    this.menuNewGame = new JMenuItem (VegaResources.NewLocalGame(false));
	    this.menuNewGame.addActionListener(this);
	    popupMenu.add(this.menuNewGame);
	    
	    this.menuLoad = new JMenuItem (VegaResources.LoadLocalGame(false));
	    this.menuLoad.addActionListener(this);
	    popupMenu.add (menuLoad);
	    
	    this.menuSave = new JMenuItem (VegaResources.SaveLocalGameAs(false));
	    this.menuSave.addActionListener(this);
	    popupMenu.add (menuSave);
	    
	    this.menuHighscore = new JMenuItem(VegaResources.LocalHighScoreList(false));
	    this.menuHighscore.addActionListener(this);
	    popupMenu.add(this.menuHighscore);

	    popupMenu.addSeparator();
	    
	    this.menuEmailClipboard = new JMenuItem (VegaResources.ImportGameFromClipboard(false));
	    this.menuEmailClipboard.addActionListener(this);
	    popupMenu.add (menuEmailClipboard);
	    
	    popupMenu.addSeparator();

	    this.menuServerGames = new JMenuItem(VegaResources.GamesOnServer(false));
	    this.menuServerGames.addActionListener(this);
	    popupMenu.add(this.menuServerGames);
	    
	    this.menuServerHighscores = new JMenuItem(VegaResources.HighScoreListOnServer(false));
	    this.menuServerHighscores.addActionListener(this);
	    popupMenu.add(this.menuServerHighscores);
	    
	    popupMenu.addSeparator();
	    
	    this.menuEmailSend = new JMenuItem(VegaResources.WriteEmail(false));
	    this.menuEmailSend.addActionListener(this);
	    popupMenu.add(this.menuEmailSend);
	    
	    this.menuParameters = new JMenuItem (VegaResources.GameParameters(false));
	    this.menuParameters.addActionListener(this);
	    popupMenu.add (this.menuParameters);
	    
	    this.menuOutputWindow = new JMenuItem(VegaResources.OpenOutputWindow(false));
	    this.menuOutputWindow.addActionListener(this);
	    popupMenu.add(this.menuOutputWindow);
	    
	    this.menuWebserver = new JMenuItem(VegaResources.WebServer(false));
	    this.menuWebserver.addActionListener(this);
	    popupMenu.add(this.menuWebserver);
	    
	    popupMenu.addSeparator();
	    
	    JMenu menuSettings = new JMenu (VegaResources.Settings(false));
	    
	    this.menuLanguage = new JMenuItem(VegaResources.Language(false));
	    this.menuLanguage.addActionListener(this);
	    menuSettings.add(this.menuLanguage);
	    
	    this.menuServerCredentials = new JMenuItem(VegaResources.VegaServerCredentials(false));
	    this.menuServerCredentials.addActionListener(this);
	    menuSettings.add(this.menuServerCredentials);
	    
	    this.menuServerAdmin = new JMenuItem(VegaResources.AdministrateVegaServer(false));
	    this.menuServerAdmin.addActionListener(this);
	    menuSettings.add(this.menuServerAdmin);
	    
	    this.menuServer = new JMenuItem(VegaResources.Terminalserver(false));
	    this.menuServer.addActionListener(this);
	    menuSettings.add(this.menuServer);
	    
	    popupMenu.add(menuSettings);
	    
	    popupMenu.addSeparator();
	    
	    this.menuQuit = new JMenuItem (VegaResources.QuitVega(false));
	    this.menuQuit.addActionListener(this);
	    popupMenu.add (this.menuQuit);
	    
	    return popupMenu;
	}
	
	private void disconnectClient()
	{
		if (this.client != null)
		{
			this.messages.writeToFile(this.client.getUserId());
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
				DialogWindow.showError(
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
			DialogWindow.showError(
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

	private void openManual()
	{
		Desktop desktop = Desktop.getDesktop();   
	    InputStream resource = getClass().getResourceAsStream("/VegaHelp_"+
	    		VegaResources.getLocale() +
	    		".pdf");
	    try
	    {
	    	Path tempOutput = Files.createTempFile("VegaHelp", ".pdf");
	        tempOutput.toFile().deleteOnExit();
	        try
	        {
	        	Files.copy(resource,tempOutput,StandardCopyOption.REPLACE_EXISTING);
	        }
	        finally
	        {
	            resource.close();
	        }
	        desktop.open(tempOutput.toFile());   
	    }   
	    catch (Exception x)
	    {
	    	
	    }
	    finally
	    {
	    	try
	    	{
	        resource.close();
	    	}
	    	catch (Exception xx) {}
	    }
	}

	private void openServerCredentialsDialog()
	{
		ServerCredentialsJDialog dlg = new ServerCredentialsJDialog(this, this.config.getServerCredentials());
		dlg.setVisible(true);
		
//		this.inputEnabled = false;
//		this.redrawScreen();
//		
//		VegaServerCredentialsJDialog dlg = new VegaServerCredentialsJDialog(
//				this, 
//				this.config.isServerCommunicationEnabled(),
//				this.config.getServerUserCredentialsFile());
//		dlg.setVisible(true);
//		
//		if (dlg.ok)
//		{
//			this.config.setServerUserCredentialsFile(dlg.serverUserCredentialsFile);
//			this.config.setServerCommunicationEnabled(dlg.serverCommunicationEnabled);
//			
//			if (this.config.isServerCommunicationEnabled())
//			{
//				if (this.client != null)
//				{
//					this.disconnectClient();
//				}
//				
//				this.connectClient();
//				this.updateConnectionAndMessageStatus();
//			}
//			else
//			{
//				this.disconnectClient();
//			}
//			
//			this.setMenuEnabled();
//		}
//		
//		this.inputEnabled = true;
//		this.redrawScreen();

	}

	private void openServerGamesDialog()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		if (this.client == null)
		{
			DialogWindow.showError(
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
			DialogWindow.showInformation(
					this, 
					VegaResources.HighScoresNoEntries(false), 
					VegaResources.HighScoreList(false));
		}
	}

	private void stopTutorial()
	{
		this.tutorialPanel.setVisible(false);
	}

	private void updateConnectionAndMessageStatus()
	{
		this.labConnectionStatus.setVisible(this.config.isServerCommunicationEnabled());
		this.labConnectionStatus.setIcon2(!(this.client != null && this.client.isConnected()));
		
		if (this.client != null && this.client.isConnected())
		{
			this.labConnectionStatus.setToolTipText(
					VegaResources.ConnectedWithVegaServer(
							false, 
							this.client.getConfig().getUrl(), 
							Integer.toString(this.client.getConfig().getPort()),
							this.client.getConfig().getUserId()));
		}
		else
		{
			if (this.config.getServerAdminCredentialFile() == null)			
				this.labConnectionStatus.setToolTipText(
					VegaResources.ServerCredentialsNotEntered(false));
			else
			{
				ClientConfiguration clientConfiguration = ClientConfiguration.readFromFile(this.config.getServerAdminCredentialFile());
				
				if (clientConfiguration != null)
				{
					this.labConnectionStatus.setToolTipText(
						VegaResources.ConnectionToServerNotEstablished(
								false,
								clientConfiguration.getUrl()));
				}
			}
		}

		this.labGames.setVisible(this.config.isServerCommunicationEnabled() && this.client != null && this.client.isConnected());
		this.labGames.setIcon2(this.playersWaitingForInput);
		this.labGames.setToolTipText(
				this.playersWaitingForInput ?
						VegaResources.PlayersWaitingForInput(false) :
						VegaResources.GamesOnServer(false));
		
		this.labMessages.setVisible(this.config.isServerCommunicationEnabled() && this.client != null && this.client.isConnected() && this.messages != null);
		if (this.messages != null)
		{
			this.labMessages.setIcon2(this.messages.getUnreadMessages().size() > 0);
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
		
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			this.setTitle(
					VegaResources.VegaDisplayServerActive(false)+
					fileName);
		else
			this.setTitle(VegaResources.Vega(false) + fileName);
	}
	
	private class WaitThread extends Thread
	{
		private Vega parent;
		private int milliseconds;
		
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
