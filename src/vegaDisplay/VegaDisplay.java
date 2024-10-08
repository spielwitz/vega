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

package vegaDisplay;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.FlatDarkLaf;

import common.PdfLauncher;
import common.ScreenContent;
import common.ScreenContentClient;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import commonUi.CommonUiUtils;
import commonUi.FontHelper;
import commonUi.IVegaDisplayMethods;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
import commonUi.PanelScreenContent;
import commonUi.LanguageSelectionJDialog;
import commonUi.VegaAbout;
import commonUi.Toolbar;
import commonUi.UiConstants;
import uiBaseControls.Frame;
import uiBaseControls.IIconLabelListener;
import uiBaseControls.IconLabel;
import uiBaseControls.LookAndFeel;
import common.VegaResources;
import common.CommonUtils;

@SuppressWarnings("serial") 
public class VegaDisplay extends Frame // NO_UCD (use default)
	implements 
		ActionListener,
		IVegaDisplayMethods,
		IHostComponentMethods,
		IIconLabelListener
{
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
		new VegaDisplay();
	}
	
	boolean connected = false;
	
	private PanelScreenContent paintPanel;
    private VegaDisplayConfiguration config;  
    private JPopupMenu popupMenu;
    
    private JMenuItem menuConnectionSettings;
    private JMenuItem menuQuit;
    private JMenuItem menuHelp;
    
    private JMenuItem menuAbout;

	private JMenuItem menuLanguage;
	
	private IconLabel labMenu;
	
	private VegaDisplay()
	{
		super(VegaResources.VegaDisplay(false), new BorderLayout());
		
		ImageIcon iconMenu = new ImageIcon (ClassLoader.getSystemResource("ic_menu.png"));
		
		this.config = VegaDisplayConfiguration.get();
		
		if (this.config.isFirstTimeStart())
		{
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					null, 
					VegaResources.getLocale(),
					false);
			dlg.setVisible(true);
			this.config.setLocale(dlg.languageCode);
			VegaResources.setLocale(dlg.languageCode);
			this.config.setFirstTimeStart(false);
		}
		
		if (config.getMyIpAddress() == null || config.getMyIpAddress().equals(""))
			config.setMyIpAddress(CommonUtils.getMyIPAddress());
		
		if (config.getServerIpAddress() == null || config.getServerIpAddress().equals(""))
			config.setServerIpAddress(config.getMyIpAddress());
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.popupMenu = this.definePopupMenu();
		
		this.labMenu = new IconLabel(
				iconMenu, 
				this);
		this.labMenu.setToolTipText(VegaResources.Menu(false));
		
		Toolbar toolbar = new Toolbar(this.labMenu);
		
		this.add(toolbar, BorderLayout.WEST);
		
		this.paintPanel = new PanelScreenContent(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT    );
		}
		catch ( RemoteException e ) 
		{}

		IVegaDisplayMethods stub;
		try {
			stub = (IVegaDisplayMethods) UnicastRemoteObject.exportObject( this, 0 );
			Registry registry = LocateRegistry.getRegistry(this.config.getMyIpAddress());
			registry.rebind( this.config.getClientId(), stub );			
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
				
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.paintPanel.requestFocusInWindow();
		
		ActionEvent e = new ActionEvent(
				this.menuConnectionSettings, 
				ActionEvent.ACTION_PERFORMED,
				"Connection settings", 
				System.currentTimeMillis(), 
				0);
		
		this.actionPerformed(e);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JMenuItem JMenuItem = (JMenuItem)e.getSource();
		
		if (JMenuItem == this.menuQuit)
		{
			this.close();
		}
		else if (JMenuItem == this.menuConnectionSettings)
		{
			VegaDisplaySettingsJDialog dlg = new VegaDisplaySettingsJDialog(
												this, 
												VegaResources.ConnectionSettings(false),
												true,
												this.config);
			dlg.setVisible(true);
			
			this.updateScreenDisplayContent();
		}
		else if (JMenuItem == this.menuHelp)
		{
			CommonUiUtils.showManual(this);
		}
		else if (JMenuItem == this.menuLanguage)
		{
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					this, 
					VegaResources.getLocale(),
					true);
			dlg.setVisible(true);
			
			if (dlg.ok)
			{
				VegaResources.setLocale(dlg.languageCode);
				
				this.config.setLocale(dlg.languageCode);
				
				this.logoff();
				
				System.exit(0);
			}
		}
		else if (JMenuItem == this.menuAbout)
		{
			VegaAbout.show(this);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
		if (this.connected)
		{
			try {
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.config.getServerIpAddress());
				rmiServer = (IServerMethods) registry.lookup( CommonUtils.RMI_REGISTRATION_NAME_SERVER );
				rmiServer.rmiKeyPressed(
						this.config.getClientId(), 
						languageCode,
						arg0.getID(), 
						arg0.getWhen(), 
						arg0.getModifiers(), 
						arg0.getKeyCode(), 
						arg0.getKeyChar());
			}
			catch (Exception e) {
			}
		}
	}
	
	@Override
	public void iconLabelClicked(IconLabel source)
	{
		if (source == this.labMenu)
		{
			Dimension dim = this.labMenu.getSize();
			this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
		}
	}
	
	@Override
	public void menuKeyPressed()
	{
		Dimension dim = this.labMenu.getSize();
		this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
	}

	@Override
	public boolean openPdf(byte[] pdfBytes) throws RemoteException
	{
		return PdfLauncher.showPdf(pdfBytes);
	}
	
	@Override
	public void updateScreen(
			ScreenContent screenContent, 
			boolean inputEnabled,
			boolean showInputDisabled)
			throws RemoteException
	{
		this.paintPanel.redraw(screenContent, inputEnabled, showInputDisabled);
	}
	
	@Override
	protected boolean confirmClose()
	{
		MessageBoxResult result = MessageBox.showYesNo(
				this,
				VegaResources.QuitVegaDisplayQuestion(false),
				VegaResources.QuitVegaDisplay(false));

		if (result == MessageBoxResult.YES)
		{
			this.logoff();
			return true;
		}
		else
		{
			return false;
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
	    
	    popupMenu.addSeparator();
	    
	    this.menuConnectionSettings = new JMenuItem(VegaResources.ConnectionSettings(false));
	    this.menuConnectionSettings.addActionListener(this);
	    popupMenu.add(this.menuConnectionSettings);
	    
	    this.menuLanguage = new JMenuItem(VegaResources.Language(false));
	    this.menuLanguage.addActionListener(this);
	    popupMenu.add(this.menuLanguage);
	    
	    popupMenu.addSeparator();
	    
	    this.menuQuit = new JMenuItem (VegaResources.QuitVegaDisplay(false));
	    this.menuQuit.addActionListener(this);
	    popupMenu.add (this.menuQuit);
	    
	    return popupMenu;
	}

	private void logoff()
	{
		if (!this.connected)
			return;
		
		try {
			IServerMethods rmiServer;
			Registry registry = LocateRegistry.getRegistry(this.config.getServerIpAddress());
			rmiServer = (IServerMethods) registry.lookup( CommonUtils.RMI_REGISTRATION_NAME_SERVER );
			rmiServer.rmiClientLogoff(this.config.getClientId());
		}
		catch (Exception e) {}
	}

	private void updateScreenDisplayContent()
	{
		ScreenContentClient screenContentClient = null;
		
		if (this.connected)
		{		
			try {
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.config.getServerIpAddress());
				rmiServer = (IServerMethods) registry.lookup( CommonUtils.RMI_REGISTRATION_NAME_SERVER );
				screenContentClient = rmiServer.rmiGetCurrentScreenDisplayContent(this.config.getClientId());
			}
			catch (Exception e) {
			}
		}
	
		if (screenContentClient != null)
			this.paintPanel.redraw(
					screenContentClient.screenContent, 
					screenContentClient.inputEnabled,
					screenContentClient.showInputDisabled);
		else
			this.paintPanel.redraw(null, false, true);
	}
}
