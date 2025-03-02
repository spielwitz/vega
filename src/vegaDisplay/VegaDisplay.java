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

package vegaDisplay;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.FlatDarkLaf;

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
import uiBaseControls.IMenuItemListener;
import uiBaseControls.IconLabel;
import uiBaseControls.LookAndFeel;
import uiBaseControls.MenuItem;
import common.VegaResources;
import common.CommonUtils;

@SuppressWarnings("serial") 
public class VegaDisplay extends Frame // NO_UCD (use default)
	implements 
		IMenuItemListener,
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
	
	private VegaDisplayConfiguration config;
    private ConnectionCheckThread connectionCheckThread;
    private VegaDisplayClient displayClient;
    
    private ImageIcon iconConnected;
    private ImageIcon iconDisconnected;
    private IconLabel labConnectionStatus;
    private IconLabel labMenu;
    
    private MenuItem menuAbout;
    private MenuItem menuConnectionSettings;
    private MenuItem menuHelp;
    
    private MenuItem menuLanguage;

	private MenuItem menuQuit;
	
	private PanelScreenContent paintPanel;
	
	private JPopupMenu popupMenu;
	
	private VegaDisplay()
	{
		super(VegaResources.VegaDisplay(false), new BorderLayout());
		
		ImageIcon iconMenu = new ImageIcon (ClassLoader.getSystemResource("ic_menu.png"));
	    this.iconConnected = new ImageIcon (ClassLoader.getSystemResource("connected.png"));
		this.iconDisconnected = new ImageIcon (ClassLoader.getSystemResource("disconnected.png"));
		
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
		
		if (config.getServerIpAddress() == null || config.getServerIpAddress().equals(""))
			config.setServerIpAddress(CommonUtils.getMyIPAddress());
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.popupMenu = this.definePopupMenu();
		
		this.labMenu = new IconLabel(
				iconMenu, 
				this);
		this.labMenu.setToolTipText(VegaResources.Menu(false));
		
		Toolbar toolbar = new Toolbar(this.labMenu);
		
		this.labConnectionStatus = new IconLabel(
				new ImageIcon[] {
						this.iconDisconnected,
						this.iconConnected
						},
				this);
		
		toolbar.addIconLabel(this.labConnectionStatus, 2);
		
		this.add(toolbar, BorderLayout.WEST);
		
		this.paintPanel = new PanelScreenContent(null);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.paintPanel.requestFocusInWindow();
		
		this.connectionCheckThread = new ConnectionCheckThread();
		this.connectionCheckThread.start();
		
		this.menuItemSelected(this.menuConnectionSettings);
	}

	@Override
	public void iconLabelClicked(IconLabel source)
	{
		if (source == this.labMenu)
		{
			Dimension dim = this.labMenu.getSize();
			this.popupMenu.show(this.labMenu, dim.width / 2, dim.height / 2);
		}
		else if (source == this.labConnectionStatus)
		{
			this.openDisplaySettings();
		}
	}

	@Override
	public void menuItemSelected(MenuItem source)
	{
		if (source == this.menuQuit)
		{
			this.close();
		}
		else if (source == this.menuConnectionSettings)
		{
			this.openDisplaySettings();
		}
		else if (source == this.menuHelp)
		{
			CommonUiUtils.showManual(this);
		}
		else if (source == this.menuLanguage)
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
		else if (source == this.menuAbout)
		{
			VegaAbout.show(this);
		}
	}
	
	boolean isDisplayClientEnabled()
	{
		return this.displayClient != null && this.displayClient.isEnabled();
	}
	
	VegaDisplayClientStartResult startDisplayClient(VegaDisplayConfiguration config)
	{
		this.displayClient = new VegaDisplayClient(this, config);
		return this.displayClient.init();
	}

	void updateScreen(ScreenContent screenContent)
	{
		this.paintPanel.redraw(screenContent, false, false);
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
			this.stopDisplayClient();
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

	    this.menuAbout = new MenuItem (VegaResources.AboutVega(false), this);
	    popupMenu.add (this.menuAbout);
	    
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHelp = new MenuItem (VegaResources.Manual(false), this);
		    popupMenu.add (this.menuHelp);
	    }
	    
	    popupMenu.addSeparator();
	    
	    this.menuConnectionSettings = new MenuItem(VegaResources.ConnectionSettings(false), this);
	    popupMenu.add(this.menuConnectionSettings);
	    
	    this.menuLanguage = new MenuItem(VegaResources.Language(false), this);
	    popupMenu.add(this.menuLanguage);
	    
	    popupMenu.addSeparator();
	    
	    this.menuQuit = new MenuItem (VegaResources.QuitVegaDisplay(false), this);
	    popupMenu.add (this.menuQuit);
	    
	    return popupMenu;
	}
	
	private void openDisplaySettings()
	{
		VegaDisplaySettingsJDialog dlg = new VegaDisplaySettingsJDialog(
				this, 
				VegaResources.ConnectionSettings(false),
				true,
				this.config);
		dlg.setVisible(true);
	}
	
	private void stopDisplayClient()
	{
		if (this.displayClient == null) return;
		
		if (this.connectionCheckThread != null)
		{
			try
			{
				this.connectionCheckThread.interrupt();
			}
			catch (Exception x) {}
		}
		
		try
		{
			this.displayClient.interrupt();
		}
		catch (Exception x) {}
		
		this.displayClient = null;
	}
	
	// ------------------
	
	private class ConnectionCheckThread extends Thread
	{
		public void run()
		{
			do
			{
				boolean connected = isDisplayClientEnabled(); 
					
				if (connected)
				{
					labConnectionStatus.setIconIndex(1);
					labConnectionStatus.setToolTipText(
							VegaResources.ConnectedToDisplayServer(
									false, 
									displayClient.getServerIpAddress()+":"+displayClient.getServerPort()));
					
				}
				else
				{
					labConnectionStatus.setIconIndex(0);
					labConnectionStatus.setToolTipText(VegaResources.NotConnected(false));
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e)
				{
					break;
				}
				
			} while (true);
		}
	}
}
