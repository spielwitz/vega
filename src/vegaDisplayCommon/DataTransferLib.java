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

package vegaDisplayCommon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

public class DataTransferLib 
{
	private static final Gson serializer = new Gson();
	private static final String STRING_ENCODING = "UTF-8"; 

	public static <T> Object receiveObjectAesEncrypted(
			DataInputStream in,
			String password,
			Class<T> expectedClass)
	{
		try
		{
			byte[] lengthBytes = new byte[4];
		    in.readFully(lengthBytes);
		    
		    int length = DataTransferLib.convertByteArrayToInt(lengthBytes);
		    
		    byte[] bytesEncrypted = new byte[length];
		    in.readFully(bytesEncrypted);
		    
		    String json = new String(
		    		decompress(
		    				aesDecrypt(
		    						bytesEncrypted, 
		    						password.getBytes(STRING_ENCODING))), 
		    		STRING_ENCODING);
		    
			return serializer.fromJson(json, expectedClass);
		}
		catch (Exception x)
		{
			return null;
		}
	}
	
	public static boolean sendObjectAesEncrypted(
			OutputStream out,
			Object object,
			String password)
	{
		try
		{
			byte[] bytesStringEncrypted = aesEncrypt(
					compress(
							serializer.toJson(object)
							.getBytes(STRING_ENCODING)), 
					password.getBytes(STRING_ENCODING));
			
			byte[] byteStringLength = DataTransferLib.convertIntToByteArray(bytesStringEncrypted.length);
	
			out.write(byteStringLength);
			out.write(bytesStringEncrypted);
			
			return true;
		}
		catch (Exception x)
		{
			return false;
		}
	}
	
	private static byte[] aesDecrypt(byte[] encryptedBytes, byte[] passwordBytes) throws Exception
	{
		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, aesGetKey(passwordBytes));
		byte[] decrypted = cipher2.doFinal(encryptedBytes);

		return decrypted;
	}
	
	private static byte[] aesEncrypt(byte[] unencryptedBytes, byte[] passwordBytes) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesGetKey(passwordBytes));
		byte[] encrypted = cipher.doFinal(unencryptedBytes);

		return encrypted;
	}
	
	private static SecretKeySpec aesGetKey(byte[] passwordBytes) throws Exception
	{
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = sha.digest(passwordBytes);
		key = Arrays.copyOf(key, 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		return secretKeySpec;
	}
	
	private static byte[] compress(byte[] content){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(content);
			gzipOutputStream.close();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	private static int convertByteArrayToInt(byte[] b)
	{
		return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24; 
	}

	private static byte[] convertIntToByteArray(int a)
	{
		return new byte[] {
		        (byte) ((a >> 24) & 0xFF),
		        (byte) ((a >> 16) & 0xFF),   
		        (byte) ((a >> 8) & 0xFF),   
		        (byte) (a & 0xFF)
		    };
	}
	
	private static byte[] decompress(byte[] contentBytes)
	{
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(contentBytes);
			GZIPInputStream gzipper = new GZIPInputStream(bin);

			int len;
			while ((len = gzipper.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			gzipper.close();
			out.close();
		}
		catch (Exception x)
		{
			return null;
		}
		return out.toByteArray();
	}
}
