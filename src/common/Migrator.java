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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Migrator
{
	private static final String PROP_BUILD = "build";
	
	public static void migrate(JsonObject jobj)
	{
		String build =
				jobj.has(PROP_BUILD) ?
						jobj.get(PROP_BUILD).getAsString() :
						"0000";
		
		if (build.compareTo("0004") < 0)
		{
			JsonObject jsonObjectEditorPrices = (JsonObject)jobj.get("editorPrices");
			
			if (jsonObjectEditorPrices != null)
			{
				int patrolPrice =
						CommonUtils.getRandomInteger(
								Planet.PRICES_MIN_MAX.get(ShipType.PATROL).getMax() -
								Planet.PRICES_MIN_MAX.get(ShipType.PATROL).getMin() + 1) +
						Planet.PRICES_MIN_MAX.get(ShipType.PATROL).getMin();
				
				jsonObjectEditorPrices.add("PATROL", new JsonPrimitive(patrolPrice));
			}
			
			jobj.addProperty(PROP_BUILD, "0004");
			migrate(jobj);
		}
		else if (build.compareTo("0012") < 0)
		{
			JsonArray jsonArrayOptions = (JsonArray)jobj.get("options");
			
			boolean updateOptions = false;
			
			for (int i = jsonArrayOptions.size() - 1; i >= 0; i--)
			{
				String option = jsonArrayOptions.get(i).getAsString();
				
				if (option.equals("LIMITED_NUMBER_OF_YEARS"))
				{
					jsonArrayOptions.remove(i);
					updateOptions = true;
				}
			}
			
			if (updateOptions)
			{
				jobj.add("options", jsonArrayOptions);
			}
			
			int yearMax = jobj.get("yearMax").getAsInt();
			
			if (yearMax <= 0)
			{
				jobj.addProperty("yearMax", Integer.parseInt(Game.YEARS[Game.YEARS.length - 1]));
			}
			
			jobj.addProperty(PROP_BUILD, "0012");
			migrate(jobj);
		}
		else
		{
			jobj.addProperty(PROP_BUILD, Game.BUILD);
			return;
		}
	}
}
