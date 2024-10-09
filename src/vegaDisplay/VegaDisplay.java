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
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.Gson;

import common.ScreenContent;
import commonUi.MessageBox;
import commonUi.MessageBoxResult;
import commonUi.CommonUiUtils;
import commonUi.FontHelper;
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
	
	private static final int SOCKET_TIMEOUT = 10000;

	private ServerSocketThread socketThread;
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
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.popupMenu = this.definePopupMenu();
		
		this.labMenu = new IconLabel(
				iconMenu, 
				this);
		this.labMenu.setToolTipText(VegaResources.Menu(false));
		
		Toolbar toolbar = new Toolbar(this.labMenu);
		
		this.add(toolbar, BorderLayout.WEST);
		
		this.paintPanel = new PanelScreenContent(null);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
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
			
			//this.updateScreenDisplayContent();
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
				
				System.exit(0);
			}
		}
		else if (JMenuItem == this.menuAbout)
		{
			VegaAbout.show(this);
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
	
	public void menuKeyPressed()
	{
		Dimension dim = this.labMenu.getSize();
		this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
	}
	
	void startServer(VegaDisplayConfiguration config)
	{
		if (this.socketThread != null && this.socketThread.isAlive()) return;
		
		this.socketThread = null;
		this.socketThread = new ServerSocketThread(config);
		this.socketThread.start();
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
	
	private class ServerSocketThread extends Thread
	{
		private ServerSocket serverSocket;
		private Gson serializer = new Gson();
		VegaDisplayConfiguration config;
		
		ServerSocketThread(VegaDisplayConfiguration config)
		{
			this.config = config;
		}
		
		public void run()
		{
			try
			{
				this.serverSocket = new ServerSocket(config.getPort());
			} catch (Exception e)
			{
				// TODO Write error message
				e.printStackTrace();
				return;
			}
			
			while (true)
			{
				try
				{
				    Socket clientSocket = this.serverSocket.accept();
				    clientSocket.setSoTimeout(SOCKET_TIMEOUT);

				    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				    
				    byte[] lengthBytes = new byte[4];
				    in.readFully(lengthBytes);
				    
				    int length = this.convertByteArrayToInt(lengthBytes);
				    
				    byte[] bytes = new byte[length];
				    in.readFully(bytes);
				    
				    String jsonStringScreenContent = new String(bytes);
				    ScreenContent screenContent = serializer.fromJson(jsonStringScreenContent, ScreenContent.class);
				    
				    paintPanel.redraw(screenContent, false, false);
				}
				catch (Exception x)
				{
				}
			}
		}
		
		private int convertByteArrayToInt(byte[] b)
		{
			return   b[3] & 0xFF |
		            (b[2] & 0xFF) << 8 |
		            (b[1] & 0xFF) << 16 |
		            (b[0] & 0xFF) << 24; 
		}
	}
}
