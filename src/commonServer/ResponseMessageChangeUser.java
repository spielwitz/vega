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

package commonServer;

import java.io.Serializable;

import common.EmailTransportBase;
import spielwitz.biDiServer.PayloadResponseMessageChangeUser;

@SuppressWarnings("serial")
public class ResponseMessageChangeUser extends EmailTransportBase implements Serializable
{
	public static ResponseMessageChangeUser GetInstance(PayloadResponseMessageChangeUser payload)
	{
		ResponseMessageChangeUser retval = new ResponseMessageChangeUser();
		
		retval.userId = payload.getUserId();
		retval.activationCode = payload.getActivationCode();
		retval.serverUrl = payload.getServerUrl();
		retval.serverPort = payload.getServerPort();
		retval.adminEmail = payload.getAdminEmail();
		retval.serverPublicKey = payload.getServerPublicKey();
		
		return retval;
	}
	public String userId;
	
	public String activationCode;
	public String serverUrl;
	public int 	  serverPort;
	
	public String adminEmail;
	
	public String serverPublicKey;
}
