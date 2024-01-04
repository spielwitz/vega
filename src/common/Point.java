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

import java.awt.geom.Point2D;
import java.io.Serializable;

@SuppressWarnings("serial") 
class Point extends Point2D.Double implements Serializable
{
	private static final 	Point zeroPoint = new Point(0, 0);
	static final 			double PRECISION = 0.000001;

	Point(double x, double y)
	{
		super(x, y);
	}

	public boolean equals(Object obj)
	{
	  if (this==obj) {
	     return true;
	  }
	  if (obj==null) {
	     return false;
	  }
	  if (!(obj instanceof Point ))
	  {
	     return false;
	  }
	  Point other = (Point) obj;
	  
	  if (this.distance(other) < PRECISION)
		  return true;
	  else
		  return false;
	}
	
	int getAngleBetweenVectors(Point otherVector)
	{
		// Returns the angle between this point (vector) and another vector in integer degrees (absolute value)
		if (Point.zeroPoint.distance(this) <= PRECISION ||
			Point.zeroPoint.distance(otherVector) <= PRECISION)
		{
			return 0;
		}
		
		double angle1 = Math.atan2(this.getY(), this.getX());
		double angle2 = Math.atan2(otherVector.getY(), otherVector.getX());

		double angle = Math.abs(angle2 - angle1);

		int angleDegrees = CommonUtils.round(180 * (angle / Math.PI));
		
		if (angleDegrees > 180)
		{
			return 360 - angleDegrees;
		}
		else
		{
			return angleDegrees;
		}
	}
	
	Point getSector()
	{
		double fractionX = Math.abs(this.x - (double)((int)this.x) - 0.5);
		double fractionY = Math.abs(this.y - (double)((int)this.y) - 0.5);
		
		if (fractionX < Point.PRECISION || fractionY < Point.PRECISION)
		{
			// When exactly in between two sectors, return null.
			return null;
		}
		else
		{
			return new Point(
					CommonUtils.round(this.x),
					CommonUtils.round(this.y));
		}
	}
	
	String getString()
	{
		return this.x + ";" + this.y;
	}
	
	Point klon()
	{
		return new Point(this.x, this.y);
	}	
	
	Point subtract(Point pt)
	{
		return new Point(
				this.getX() - pt.getX(),
				this.getY() - pt.getY());
	}
}
