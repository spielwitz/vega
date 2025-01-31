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

package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils
{
	private static Object cloneLock = new Object();
	
	private final static String	DATA_FOLDER = "data"; 
	
	public static boolean areBuildsCompatible(String otherBuild)
	{
		if (otherBuild == null || otherBuild.compareTo(Game.BUILD_COMPATIBLE) < 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public static int[] distributeLoss(int[] originalCounts, int lossCount, int indexPreferred)
	{
		int sumOriginalCounts = 0;
		
		for (int i = 0; i < originalCounts.length; i++)
			sumOriginalCounts += originalCounts[i];
		
		if (lossCount <= 0 || sumOriginalCounts <= 0)
			return new int[originalCounts.length];
		
		if (lossCount > sumOriginalCounts)
			lossCount = sumOriginalCounts;

		int[] losses = new int[originalCounts.length];
		
		int rest = distributeLossInternal(
				indexPreferred, 
				originalCounts,
				sumOriginalCounts,
				losses, 
				lossCount, 
				lossCount, 
				true);
		
		if (rest > 0)
		{
			int[] seq = CommonUtils.getRandomList(originalCounts.length);
			
			for (int round = 0; round < 2; round++)
			{
				for (int i = 0; i < originalCounts.length; i++)
				{
					int index = seq[i];
					
					if (originalCounts[index] == 0 || index == indexPreferred)
						continue;

					rest = distributeLossInternal(
							index, 
							originalCounts,
							sumOriginalCounts,
							losses, 
							lossCount, 
							rest, 
							(i==1));
					
					if (rest <= 0)
						break;
				}
				
				if (rest <= 0)
					break;
			}
		}
		
		return losses;
	}
	public static String getHomeDir()
	{
		File dir = Paths.get(System.getProperty("user.dir"), DATA_FOLDER).toFile();
		
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		return dir.getAbsolutePath();
	}
	
	public static String getMyIPAddress()
	{
		String meineIP = null;
		try {
			InetAddress myAddr = InetAddress.getLocalHost();
			meineIP = myAddr.getHostAddress();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
		
		return meineIP;
	}
	
	public static int getRandomInteger(int valueMax)
	{
		return ThreadLocalRandom.current().nextInt(valueMax);
	}
	
	public static Object klon(Object obj)
	{
		if (obj == null)
			return null;

		synchronized(cloneLock)
		{
			Object retval = null;
			
			try
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(obj);
				os.flush();
				os.close();

				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
				ObjectInputStream is = new ObjectInputStream(in);
				retval = (Object)is.readObject();
				is.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				retval = null;
			}

			return retval;
		}
	}
	
	public static int round (double arg)
	{
		return (int)Math.round(arg);		
	}
	
	public static int[] sortList (String values[], boolean descending)
	{
		int sequence[] = new int[values.length];
		int swap;
		boolean ok = false;
		
		for (int t = 0; t < values.length; t++)
			sequence[t] = t;
		
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < values.length - 1; t++)
			{
				if (descending == false && values[sequence[t]].compareTo(values[sequence[t+1]]) > 0)
					ok = false;
				else if (descending == true && values[sequence[t]].compareTo(values[sequence[t+1]]) < 0)
					ok = false;
				
				if (ok == false)
				{
					swap = sequence[t];
					sequence[t] = sequence[t+1];
					sequence[t+1] = swap;
				}
			}
		}
		
		return sequence;
	}
	
	static <T> T ArrayListGetLast(ArrayList<T> list)
	{
		if (list == null || list.size() == 0) return null;
		return list.get(list.size() - 1);
	}
	
	static <T> void ArrayListRemoveLast(ArrayList<T> list)
	{
		if (list == null || list.size() == 0) return;
		list.remove(list.size() - 1);
	}
	
	static String convertDateToString(long dateLong)
	{
		Date date = new Date(dateLong);
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM); 
		return df.format(date);
	}
	
	static String convertToString(int value)
	{
		if (value == 0)
			return "";
		else
			return Integer.toString(value);
	}
		
	static int[] getRandomList(int elementsCount)
	{
		return getRandomList(elementsCount, elementsCount);
	}
	
	static int[] getSequentialList(int elementsCount)
	{
		int retval[] = new int[elementsCount];
		
		for (int t = 0; t < elementsCount; t++)
			retval[t] = t;
		
		return retval;
	}
	
	static String getStringWithGivenLength(char c, int stringLength)
	{
		if (stringLength <= 0)
			return "";
		else
		{
			char[] chars = new char[stringLength];
			Arrays.fill(chars, c);
			return new String(chars);
		}
	}
	
	static String padString(int value, int stringLength)
	{
		return padString(Integer.toString(value), stringLength);
	}

	static String padString(String text, int stringLength)
	{
		StringBuilder sb = null;
		String stringTemplate = new String(new char[stringLength]).replace('\0', ' ');
		
		sb = new StringBuilder(stringTemplate);
		sb.append(text);
		
		return sb.substring(sb.length()-stringLength, sb.length());
	}
	
	static String padStringLeft(String text, int stringLength)
	{
		return (text + getStringWithGivenLength(' ', stringLength)).substring(0, stringLength);
	}
	
	static int[] sortValues (int values[], boolean descending)
	{
		int sequence[] = new int[values.length];
		int swap;
		boolean ok = false;
		
		for (int t = 0; t < values.length; t++)
			sequence[t] = t;
		
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < values.length - 1; t++)
			{
				if (descending == false && values[sequence[t]] > values[sequence[t+1]])
					ok = false;
				else if (descending == true && values[sequence[t]] < values[sequence[t+1]])
					ok = false;
				
				if (ok == false)
				{
					swap = sequence[t];
					sequence[t] = sequence[t+1];
					sequence[t+1] = swap;
				}
			}
		}
		
		return sequence;
	}
	
	private static int distributeLossInternal(
			int index,
			int[] originalCounts,
			int sumOriginalCounts,
			int[] losses, 
			int lossCount, 
			int rest, 
			boolean radikal)
	{
		if (originalCounts[index] > 0)
		{
			int reduction = CommonUtils.round((originalCounts[index] / (double)sumOriginalCounts) * (double)lossCount);
			
			if (radikal && reduction == 0)
				reduction = 1;
			
			if (reduction > rest)
				reduction = rest;

			losses[index] += reduction;
			
			rest -= reduction;
		}
		
		return rest;
	}
	
	private static int[] getRandomList(int valueMax, int elementsCount)
	{
		int retval[] = new int[elementsCount];
		int t, tt;

		BitSet b = new BitSet(valueMax);

		for (t = 0; t < elementsCount; t++)
		{
			tt = CommonUtils.getRandomInteger(valueMax);

			while (b.get(tt) == true)
				tt = (tt + 1) % valueMax;

			retval[t] = tt;
			b.set(tt);
		}

		b = null;

		return retval;
	}
}
