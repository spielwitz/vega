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

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.URI;
import java.net.URLEncoder;

import common.EmailTransportBase;
import common.Game;
import common.VegaResources;
import commonUi.MessageBox;

class EmailToolkit
{
	static final String EMAIL_REGEX_PATTERN = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
	private static final String BASE64_START = "----------begin:\n";
	private static final String BASE64_END = "\n:end----------";
	
	static String getClipboardContent()
	{
		String retval = "";
		
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipData = clip.getContents(clip);
		
		if (clipData != null)
		{
			try
			{
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					retval = (String)(clipData.getTransferData(
							DataFlavor.stringFlavor));
				}
			}
			catch (Exception e) {
			}
		}			

		return retval;
	}
	
	static String getEmailObjectPayload(
			String password, 
			EmailTransportBase obj)
	{
		obj.className = obj.getClass().getName();
		obj.build = Game.BUILD;
		
		String base64 = VegaUtils.convertToBase64(obj, password);
		
		return BASE64_START + base64 + BASE64_END;
	}
	
	static boolean launchEmailClient(
			Component parent,
			String recipient, 
			String subject, 
			String bodyText, 
			String password, 
			EmailTransportBase obj)
	{
		boolean ok = true;
		
		String uriStr = null;
		
		if (obj != null)
		{
			uriStr = String.format("mailto:%s?subject=%s&body=%s",
		            recipient,
		            urlEncode(subject),
		            urlEncode(bodyText + "\n\n" + getEmailObjectPayload(password, obj)));
		}
		else
			uriStr = String.format("mailto:%s?subject=%s&body=%s",
		            recipient,
		            urlEncode(subject),
		            urlEncode(bodyText));
		
		try
		{
			Desktop.getDesktop().browse(new URI(uriStr));
		}
		catch (Exception x)
		{
			MessageBox.showError(
					parent, 
					VegaResources.EmailOpenError(false, x.getMessage()), 
					VegaResources.Error(false));
			ok = false;
		}
		
		return ok;
	}
	
	static <T> EmailTransportBase parseEmail(String body, Class<T> expectedClass, String password)
	{
		body = body.replace(" ", "");
		
		int posStart = body.indexOf(BASE64_START);
		if (posStart == -1)
			return null;
		
		int posEnd = body.indexOf(BASE64_END);
		if (posEnd == -1 || posEnd < posStart)
			return null;
		
		String base64 = body.substring(posStart + BASE64_START.length(), posEnd);
		
		EmailTransportBase obj = null;
		
		try
		{
			obj = (EmailTransportBase)VegaUtils.convertFromBase64(base64, expectedClass, password);
			
			if (obj != null && !obj.className.equals(expectedClass.getName()))
				obj = null;
		}
		catch (Exception x)
		{
			obj = null;
		}
		
		return obj;
	}
	
	private static final String urlEncode(String str) {
	    try {
	        return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
	    } catch (Exception e) {
	        return "";
	    }
	}
}
