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

class CirclePlacing
{
	private final static int MAX_TRY_COUNT = 100;

	Circle[] circles;

	CirclePlacing(int circlesCount, int radius, Point posMax)
	{
		do 
		{
			this.circles = new Circle[circlesCount];

			for (int i = 0; i < circlesCount; i++)
			{
				this.circles[i] = new Circle(radius, posMax);
			}

			int count = 0;

			boolean ok = true;

			while (count < MAX_TRY_COUNT)
			{
				ok = true;

				for (int i = 0; i < circlesCount - 1; i++)
				{
					for (int j = i + 1; j < circlesCount; j++)
					{
						if (this.circles[i].intersectsWith(this.circles[j]))
						{
							this.circles[j].setRandomPosition();
							ok = false;
							count++;
							break;
						}
					}

					if (!ok)
						break;
				}

				if (ok)
					break;
			}

			if (ok)
				break;

		} while(true);
	}
}
