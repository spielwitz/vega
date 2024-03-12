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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

import common.VegaResources;

class VegaUtils
{
	static <T> Object convertFromBase64(String base64, Class<T> expectedClass, String password)
	{
		Object retval = null;
		Gson serializer = new Gson();
		
		try {
			byte[] byteArray = Base64.getMimeDecoder().decode(base64.getBytes());
			
			if (password != null)
				byteArray = aesDecrypt(byteArray, password);
			
			ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
			GZIPInputStream zipin = new GZIPInputStream (in);
			ObjectInputStream iis = new ObjectInputStream(zipin);
			String json = (String) iis.readObject();
			retval = serializer.fromJson(json, expectedClass);
			in.close();
		}
		catch (Exception e) {
			retval = null;
		}

		return retval;
	}

	static String convertMillisecondsToString(long dateLong)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		
		Instant instant = Instant.ofEpochMilli(dateLong);
	    LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	    
	    return date.format(formatter);
	}
	
	static String convertToBase64(Object obj, String password)
	{
		String outString = "";
		
		Gson serializer = new Gson();
		String json = serializer.toJson(obj);
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream zipout = new GZIPOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(zipout);
			oos.writeObject(json);
			oos.close();
			
			byte[] byteArray = out.toByteArray();
			
			if (password != null)
				byteArray = aesEncrypt(byteArray, password);
			
			outString = Base64.getMimeEncoder().encodeToString(byteArray);
		}
			catch (Exception e) {
		}

		return outString;
	}
		
	static String formatDateTimeString(String unformattedString)
	{
		String jahr = unformattedString.substring(0, 4);
		String monat = unformattedString.substring(4, 6);
		String tag = unformattedString.substring(6, 8);
		
		String stunde = unformattedString.substring(8, 10);
		String minute = unformattedString.substring(10, 12);
		
		return VegaResources.ReleaseFormatted(
				false, tag, monat, jahr, stunde, minute);
	}
	
	static String formatDateString(String unformattedString)
	{
		String jahr = unformattedString.substring(0, 4);
		String monat = unformattedString.substring(4, 6);
		String tag = unformattedString.substring(6, 8);
		
		return VegaResources.DateFormatted(false, tag, monat, jahr);
	}
	
	static byte[] readAllBytes(InputStream inputStream) throws IOException 
	{
		int bufLen = 4000;
		byte[] buf = new byte[bufLen];
		int readLen;
		IOException exception = null;

		try 
		{
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
			{
				while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
					outputStream.write(buf, 0, readLen);

				return outputStream.toByteArray();
			}
	    } 
		catch (IOException e)
		{
			exception = e;
			throw e;
		} 
		finally 
		{
			if (exception == null)
				inputStream.close();
			else
			{
				try 
				{
					inputStream.close();
				}
				catch (IOException e)
				{
					exception.addSuppressed(e);
				}
			}
		}
	}
	
	private static byte[] aesDecrypt(byte[] encryptedBytes, String password) throws Exception
	{
		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, aesGetKey(password));
		byte[] decrypted = cipher2.doFinal(encryptedBytes);
		
		return decrypted;
	}

	private static byte[] aesEncrypt(byte[] unencryptedBytes, String password) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesGetKey(password));
		byte[] encrypted = cipher.doFinal(unencryptedBytes);
		
		return encrypted;
	}

	private static SecretKeySpec aesGetKey(String password) throws Exception
	{
		byte[] key = password.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); 
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		
		return secretKeySpec;
	}	
}
