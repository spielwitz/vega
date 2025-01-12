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
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import common.Game;
import common.VegaResources;
import common.CommonUtils;
import commonUi.FontHelper;
import commonUi.UiConstants;
import uiBaseControls.Button;
import uiBaseControls.CheckBox;
import uiBaseControls.ComboBox;
import uiBaseControls.Dialog;
import uiBaseControls.HyperlinkLabel;
import uiBaseControls.IButtonListener;
import uiBaseControls.ICheckBoxListener;
import uiBaseControls.IComboBoxListener;
import uiBaseControls.ITextFieldListener;
import uiBaseControls.Label;
import uiBaseControls.ListItem;
import uiBaseControls.Panel;
import uiBaseControls.TextField;
import vega.qrCode.*;

@SuppressWarnings("serial") class WebServerConfigJDialog extends Dialog implements IButtonListener, ICheckBoxListener, IComboBoxListener, ITextFieldListener
{
	String ipAddress;
	int port;
	
	private Button butClose;
	
	private Button butGetIp;
	private CheckBox cbServerEnabled;
	private ComboBox comboViews;
	
	private HyperlinkLabel labUrl;
	
	private QrCodePanel	panQrCode;
	
	private TextField tfIpAddress;
	private TextField tfPort;
	private ArrayList<String[]> urls;
	
	private Vega vega;
	
	WebServerConfigJDialog(Vega vega, String myIpAddress, int port)
	{
		super (vega, VegaResources.WebServer(false), new BorderLayout());
		
		this.vega = vega;
		this.ipAddress = myIpAddress;
		this.port = port;
		
		int columnsTextField = 40;
		int qrCodeSize = 230;
		
		this.cbServerEnabled = new CheckBox(
				VegaResources.ActivateServer(false), 
				vega.getWebserver() != null, this);
		this.tfIpAddress = new TextField(
				myIpAddress == null || myIpAddress.length() == 0 ?
						CommonUtils.getMyIPAddress() :
						myIpAddress, 
				null, 
				columnsTextField, 
				0, 
				this);
		this.tfPort = new TextField(Integer.toString(port), "[0-9]*", columnsTextField, 5, this);
		this.butGetIp = new Button(VegaResources.GetIp(false) , this);
		this.butClose = new Button(VegaResources.Close(false), this);
		
		this.comboViews = new ComboBox(new String[0], 0, null, this);
		
		this.panQrCode = new QrCodePanel();
		this.panQrCode.setPreferredSize(new Dimension(qrCodeSize, qrCodeSize));
		
		this.labUrl = new HyperlinkLabel("");
		this.labUrl.setHorizontalAlignment(SwingConstants.CENTER);
		
		// --------------
		Panel panConfigOuter = new Panel(VegaResources.WebServerConfiguration(false), new BorderLayout(10, 10));
		
		Panel panConfigParameters = new Panel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(
				5, 
				10, 
				5, 
				10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
		panConfigParameters.add(new Label(VegaResources.ServerIp(false)), c);
		
		c.gridx = 1; c.gridy = 0; c.gridwidth = 2;
		panConfigParameters.add(this.tfIpAddress, c);
		
		c.gridx = 3; c.gridy = 0; c.gridwidth = 1;
		panConfigParameters.add(this.butGetIp, c);
		
		c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
		panConfigParameters.add(new Label(VegaResources.ServerPort(false)), c);
		
		c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
		panConfigParameters.add(this.tfPort, c);
		
		c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
		panConfigParameters.add(this.cbServerEnabled, c);
		
		c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
		panConfigParameters.add(this.comboViews, c);
		
		panConfigOuter.add(panConfigParameters, BorderLayout.NORTH);
		
		Panel panQrOuter = new Panel(new BorderLayout());
		
		Panel panQr = new Panel(new FlowLayout(FlowLayout.CENTER));
		panQr.add(this.panQrCode);
		
		panQrOuter.add(panQr, BorderLayout.CENTER);
		
		Panel panQrUrl = new Panel(new FlowLayout(FlowLayout.CENTER));
		panQrUrl.setPreferredSize(new Dimension(qrCodeSize, 30));
		panQrUrl.add(this.labUrl);
		panQrOuter.add(panQrUrl, BorderLayout.SOUTH);
		
		panConfigOuter.add(panQrOuter, BorderLayout.CENTER);
		
		this.addToInnerPanel(panConfigOuter, BorderLayout.CENTER);

		// --------------
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		panButtons.add(this.butClose);
		this.setDefaultButton(this.butClose);
		
		this.addToInnerPanel(panButtons, BorderLayout.SOUTH);
		// --------------
		
		this.pack();
		this.setLocationRelativeTo(vega);
		
		this.collectUrls();
		this.setControlsEnabled();
	}
	
	@Override
	public void buttonClicked(uiBaseControls.Button source)
	{
		if (source == this.butClose)
		{
			this.close();
		}
		else if (source == this.butGetIp)
		{
			this.tfIpAddress.setText(CommonUtils.getMyIPAddress());
		}
	}
	
	@Override
	public void checkBoxValueChanged(CheckBox source, boolean newValue)
	{
		if (source == this.cbServerEnabled)
		{
			this.vega.activateWebServer(newValue, port);
			
			if (newValue == true)
			{
				this.collectUrls();
			}
			
			this.setControlsEnabled();
		}
	}

	@Override
	public void comboBoxItemSelected(ComboBox source, String selectedValue)
	{
		this.setControlsEnabled();
	}
	
	@Override
	public void comboBoxItemSelected(ComboBox source, ListItem selectedListItem)
	{
	}
	
	@Override
	public void textChanged(TextField source)
	{
	}
	
	@Override
	public void textFieldFocusLost(TextField source)
	{
		if (source == this.tfIpAddress)
		{
			this.ipAddress = this.tfIpAddress.getText();
		}
		else if (source == this.tfPort)
		{
			this.port = Integer.parseInt(this.tfPort.getText());
		}
	}
	
	protected void close()
	{
		if (this.vega.getWebserver() != null)
		{
			this.port = Integer.parseInt(this.tfPort.getText());
			this.ipAddress = this.tfIpAddress.getText().trim();
		}
		
		super.close();
	}

	@Override
	protected boolean confirmClose()
	{
		return true;
	}
	
	private void collectUrls()
	{
		this.urls = this.generateWebInventoryKeys();
		String[] comboValues = new String[urls.size()];
		for (int i = 0; i < urls.size(); i++)
		{
			comboValues[i] = urls.get(i)[0];
		}
		
		this.comboViews.setItems(comboValues);
	}

	private ArrayList<String[]> generateWebInventoryKeys()
	{
		ArrayList<String[]> urls = new ArrayList<String[]>(); 
		
		if (this.vega.getWebserver() == null)
		{
			return urls;
		}

		urls.add (new String[] {VegaResources.Board(false), ""});
		urls.add(new String[] {VegaResources.Manual(false), "/" + WebServer.MANUAL_URL});
		
		String distanceMatrixKey = this.vega.getWebserver().getDistanceMatrixKey();
		
		if (distanceMatrixKey != null)
		{
			urls.add (new String[] {VegaResources.DistanceMatrix(false), "/" + distanceMatrixKey});
		}
		
		String[] webserverInventoryKeys = this.vega.getWebserver().getWebInventoryKeys();
		
		if (webserverInventoryKeys != null)
		{
			Game game = this.vega.getGame();
			
			for (int playerIndex = 0; playerIndex < webserverInventoryKeys.length; playerIndex++)
			{
				if (webserverInventoryKeys[playerIndex] != null)
				{
					urls.add(
							new String[] {
									VegaResources.WebServerInventory(false, game.getPlayers()[playerIndex].getName()),
									"/" + webserverInventoryKeys[playerIndex]
							});
				}
			}
		}
		
		return urls;
	}
	
	private void setControlsEnabled()
	{
		this.tfIpAddress.setEditable(!this.cbServerEnabled.isSelected());
		this.tfPort.setEditable(!this.cbServerEnabled.isSelected());
		this.butGetIp.setEnabled(!this.cbServerEnabled.isSelected());
		this.comboViews.setEnabled(this.cbServerEnabled.isSelected());
		
		if (this.cbServerEnabled.isSelected())
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("http://");
			sb.append(this.tfIpAddress.getText());
			sb.append(":");
			sb.append(this.tfPort.getText());
			sb.append(urls.get(this.comboViews.getSelectedIndex())[1]);
			
			this.labUrl.updateText(sb.toString());
			this.panQrCode.updateContent(sb.toString());
		}
		else
		{
			this.labUrl.updateText("");
			this.panQrCode.updateContent("");
			this.comboViews.setItems(new String[0]);
		}
	}
	
	private class QrCodePanel extends JPanel
	{
		private String content;

		public void paint(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			Dimension dim = this.getSize();
			
			if (this.content != null && this.content.length() > 0)
			{
				g2.setColor(Color.white);
				g2.fillRect(0, 0, dim.width, dim.height);
				
				vega.qrCode.QrCode.Ecc errCorLvl = QrCode.Ecc.MEDIUM;
				QrCode qr = QrCode.encodeText(this.content, errCorLvl);
				BufferedImage img = qr.toImage(10, 2);
				g2.drawImage(img, 0, 0, dim.width, dim.height, null);
			}
			else
			{
				g2.setRenderingHint(
				        RenderingHints.KEY_TEXT_ANTIALIASING,
				        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
				
				g2.setColor(getBackground());
				g2.fillRect(0, 0, dim.width, dim.height);
				
				g2.setColor(getForeground());
				g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
				
				g2.setFont(FontHelper.getFont(UiConstants.FONT_DIALOG_SIZE));
				
				String s = VegaResources.WebServerInactive(false);
				
				g2.drawString(
						s, 
						(this.getWidth() - g2.getFontMetrics().stringWidth(s)) / 2, 
						(this.getHeight() - g2.getFontMetrics().getHeight()) / 2);
			}
		}
		
		public void updateContent(String content)
		{
			this.content = content;
			this.repaint();
		}
	}
}
