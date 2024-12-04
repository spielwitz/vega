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

package common;

import java.util.ArrayList;

public class PlanetDistribution
{
	final static int NEARBY_PLANETS_COUNT = 5;
	private final static double BOARD_RATIO = (double)Game.BOARD_MAX_X / (double)Game.BOARD_MAX_Y;
	private static final double BOARD_SECTORS_PER_PLANET = 8.57;

	private final static int NEARBY_PLANETS_RADIUS = 4;
	private final static double PLANETS_PER_PLAYER = 7;

	public static int getPlanetCountMin(int playerCount)
	{
		return (int) Math.ceil(playerCount * PLANETS_PER_PLAYER);
	}

	private boolean[][] blockedSectors;
	private Point boardSize;
	private int[] homePlanetIndices;
	private int[][] nearbyPlanetIndicesPerPlayer;
	private Point[] positions;
	private int startIndexRegularPlanets;

	private Statistics statistics;

	public PlanetDistribution(int playerCount, int planetCount)
	{
		int planetsCount = Math.max(getPlanetCountMin(playerCount), planetCount);

		this.statistics = new Statistics(playerCount);

		double h = Math.sqrt(BOARD_SECTORS_PER_PLANET * (double) planetsCount / BOARD_RATIO);
		double w = BOARD_RATIO * h;
		this.boardSize = new Point(CommonUtils.round(w), CommonUtils.round(h));

		boolean ok = false;
		
		while (!ok)
		{
			this.positions = new Point[planetsCount];
			this.homePlanetIndices = new int[playerCount];
			this.nearbyPlanetIndicesPerPlayer = new int[playerCount][NEARBY_PLANETS_COUNT];
			this.blockedSectors = new boolean[(int) this.boardSize.x][(int) this.boardSize.y];

			if (!this.placeHomePlanets()) continue;
			if (!this.placeNearbyPlanets()) continue;
			if (!this.placeRegularPlanets()) continue;
			
			ok = true;
		}
	}

	public Statistics getStatistics()
	{
		return statistics;
	}

	Point getBoardSize()
	{
		return boardSize;
	}

	int[] getHomePlanetIndices()
	{
		return homePlanetIndices;
	}

	int[][] getNearbyPlanetIndicesPerPlayer()
	{
		return nearbyPlanetIndicesPerPlayer;
	}

	Point[] getPositions()
	{
		return positions;
	}

	int getStartIndexRegularPlanets()
	{
		return startIndexRegularPlanets;
	}

	private void blockSectorsAroundPlanet(Point pos)
	{
		for (int x = (int) Math.max(pos.x - 1, 0); x <= Math.min(pos.x + 1, boardSize.x - 1); x++)
		{
			for (int y = (int) Math.max(pos.y - 1, 0); y <= Math.min(pos.y + 1, boardSize.y - 1); y++)
			{
				this.blockedSectors[x][y] = true;
			}
		}
	}

	private ArrayList<String> cloneList(ArrayList<String> list)
	{
		ArrayList<String> cloneList = new ArrayList<String>();
		
		for (String line: list)
		{
			cloneList.add(line);
		}
		
		return cloneList;
	}

	private boolean placeHomePlanets()
	{
		Circle[] circles = new Circle[this.homePlanetIndices.length];
		boolean ok = true;

		do
		{
			this.statistics.loopsHomePlanets++;

			ArrayList<String> potentialSectors = new ArrayList<String>((int) (this.boardSize.x * this.boardSize.y));

			for (int y = 0; y < this.boardSize.y; y++)
			{
				for (int x = 0; x < this.boardSize.x; x++)
				{
					potentialSectors.add(x + ";" + y);
				}
			}

			ok = true;

			for (int playerIndex = 0; playerIndex < this.homePlanetIndices.length; playerIndex++)
			{
				if (potentialSectors.size() == 0)
				{
					ok = false;
					break;
				}

				circles[playerIndex] = new Circle(potentialSectors);

				for (String blockedSector : circles[playerIndex].blockedSectors)
				{
					potentialSectors.remove(blockedSector);
				}
			}
		} while (!ok);

		for (int playerIndex = 0; playerIndex < this.homePlanetIndices.length; playerIndex++)
		{
			int homePlanetIndex = playerIndex * (NEARBY_PLANETS_COUNT + 1);
			this.homePlanetIndices[playerIndex] = homePlanetIndex;
			this.positions[homePlanetIndex] = circles[playerIndex].pos;
			this.blockSectorsAroundPlanet(this.positions[homePlanetIndex]);
		}
		
		return ok;
	}

	private boolean placeNearbyPlanets()
	{
		for (int playerIndex = 0; playerIndex < this.homePlanetIndices.length; playerIndex++)
		{
			ArrayList<String> potentialSectorsMaster = new ArrayList<String>();
			int homePlanetIndex = this.homePlanetIndices[playerIndex];
			Point homePlanetPos = this.positions[homePlanetIndex];

			for (int x = (int) Math.max(homePlanetPos.x - NEARBY_PLANETS_RADIUS, 0); x <= Math
					.min(homePlanetPos.x + NEARBY_PLANETS_RADIUS, boardSize.x - 1); x++)
			{
				for (int y = (int) Math.max(homePlanetPos.y - NEARBY_PLANETS_RADIUS, 0); y <= Math
						.min(homePlanetPos.y + NEARBY_PLANETS_RADIUS, boardSize.y - 1); y++)
				{
					Point pos = new Point(x, y);

					if (homePlanetPos.distance(pos) < 2 || homePlanetPos.distance(pos) > NEARBY_PLANETS_RADIUS
							|| this.blockedSectors[x][y])
					{
						continue;
					}

					potentialSectorsMaster.add(x + ";" + y);
				}
			}

			boolean ok;
			Point[] planetPositions;
			int numberTries = 0;

			do
			{
				this.statistics.loopNearbyPlanetsByPlayer[playerIndex]++;
				ok = true;

				ArrayList<String> potentialSectors = this.cloneList(potentialSectorsMaster);
				planetPositions = new Point[NEARBY_PLANETS_COUNT];

				for (int i = 0; i < NEARBY_PLANETS_COUNT; i++)
				{
					if (potentialSectors.size() == 0)
					{
						ok = false;
						break;
					}

					planetPositions[i] = this.placePlanet(potentialSectors);
				}
				numberTries++;
				
				if (numberTries > 1000) return false;
				
			} while (!ok);

			for (int i = 0; i < NEARBY_PLANETS_COUNT; i++)
			{
				int planetIndex = homePlanetIndex + i + 1;
				this.positions[planetIndex] = planetPositions[i];
				this.nearbyPlanetIndicesPerPlayer[playerIndex][i] = planetIndex;
				this.blockSectorsAroundPlanet(this.positions[planetIndex]);
			}
		}
		
		return true;
	}

	private Point placePlanet(ArrayList<String> potentialSectors)
	{
		String[] posCoordinatesString = potentialSectors.get(CommonUtils.getRandomInteger(potentialSectors.size()))
				.split(";");
		int posX = Integer.parseInt(posCoordinatesString[0]);
		int posY = Integer.parseInt(posCoordinatesString[1]);

		for (int x = Math.max(posX - 1, 0); x <= Math.min(posX + 1, boardSize.x - 1); x++)
		{
			for (int y = Math.max(posY - 1, 0); y <= Math.min(posY + 1, boardSize.y - 1); y++)
			{
				potentialSectors.remove(x + ";" + y);
			}
		}

		return new Point(posX, posY);
	}
	
	private boolean placeRegularPlanets()
	{
		ArrayList<String> potentialSectorsMaster = new ArrayList<String>((int) (this.boardSize.x * this.boardSize.y));

		for (int y = 0; y < this.boardSize.y; y++)
		{
			for (int x = 0; x < this.boardSize.x; x++)
			{
				if (!this.blockedSectors[x][y])
				{
					Point pos = new Point(x, y);
					boolean ok = true;

					for (int playerIndex = 0; playerIndex < this.homePlanetIndices.length; playerIndex++)
					{
						if (pos.distance(this.positions[this.homePlanetIndices[playerIndex]]) <= NEARBY_PLANETS_RADIUS)
						{
							ok = false;
							break;
						}
					}

					if (ok)
					{
						potentialSectorsMaster.add(x + ";" + y);
					}
				}
			}
		}

		this.startIndexRegularPlanets = this.homePlanetIndices.length * (NEARBY_PLANETS_COUNT + 1);

		boolean ok;
		int numberTries = 0;

		do
		{
			this.statistics.loopsRegularPlanets++;
			ok = true;

			ArrayList<String> potentialSectors = this.cloneList(potentialSectorsMaster);

			for (int planetIndex = startIndexRegularPlanets; planetIndex < positions.length; planetIndex++)
			{
				if (potentialSectors.size() == 0)
				{
					ok = false;
					break;
				}

				this.positions[planetIndex] = this.placePlanet(potentialSectors);
			}
			
			numberTries++;
			
			if (numberTries > 1000) return false;
			
		} while (!ok);
		
		return true;
	}

	public class Statistics
	{
		public int[] loopNearbyPlanetsByPlayer;
		public int loopsHomePlanets;
		public int loopsRegularPlanets;

		private Statistics(int playerCount)
		{
			loopNearbyPlanetsByPlayer = new int[playerCount];
		}
	}

	private class Circle
	{
		private ArrayList<String> blockedSectors;
		private Point pos;

		Circle(ArrayList<String> potentialSectors)
		{
			String[] posCoordinatesString = potentialSectors.get(CommonUtils.getRandomInteger(potentialSectors.size()))
					.split(";");
			int posX = Integer.parseInt(posCoordinatesString[0]);
			int posY = Integer.parseInt(posCoordinatesString[1]);

			this.pos = new Point(posX, posY);

			this.blockedSectors = new ArrayList<String>();

			for (int x = Math.max(posX - 2 * NEARBY_PLANETS_RADIUS, 0); x <= Math
					.min(posX + 2 * NEARBY_PLANETS_RADIUS, boardSize.x-1); x++)
			{
				for (int y = Math.max(posY - 2 * NEARBY_PLANETS_RADIUS, 0); y <= Math
						.min(posY + 2 * NEARBY_PLANETS_RADIUS, boardSize.y-1); y++)
				{
					if (this.pos.distance(new Point(x, y)) <= 2 * NEARBY_PLANETS_RADIUS)
					{
						blockedSectors.add(x + ";" + y);
					}
				}
			}
		}
	}
}
