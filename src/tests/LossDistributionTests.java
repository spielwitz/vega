/**	VEGA - a strategy game
    Copyright (C) 1989-2023 Michael Schweitzer, spielwitz@icloud.com

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

package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import common.CommonUtils;

public class LossDistributionTests
{
	@Test
	void LossDistributionTest1()
	{
		int[] originalCounts = new int[] {1, 1, 1};
		int lossCount = 3;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == lossCount);
		assertTrue(losses[0] == 1);
		assertTrue(losses[1] == 1);
		assertTrue(losses[2] == 1);
	}
	
	@Test
	void LossDistributionTest2()
	{
		int[] originalCounts = new int[] {1, 1, 1};
		int lossCount = 5;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == 3);
		assertTrue(losses[0] == 1);
		assertTrue(losses[1] == 1);
		assertTrue(losses[2] == 1);
	}
	
	@Test
	void LossDistributionTest3()
	{
		int[] originalCounts = new int[] {};
		int[] losses = CommonUtils.distributeLoss(originalCounts, 5, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == 0);
	}
	
	@Test
	void LossDistributionTest4()
	{
		int[] originalCounts = new int[] {0, 1, 1};
		int lossCount = 1;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == lossCount);
		assertTrue(losses[0] == 0);
		assertTrue((losses[1] == 1 && losses[2] == 0) ||
				   (losses[1] == 0 && losses[2] == 1));
	}
	
	@Test
	void LossDistributionTest5()
	{
		int[] originalCounts = new int[] {1, 1, 1};
		int lossCount = 1;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == lossCount);
		assertTrue(losses[0] == 1);
		assertTrue(losses[1] == 0);
		assertTrue(losses[2] == 0);
	}
	
	@Test
	void LossDistributionTest6()
	{
		int[] originalCounts = new int[] {33, 33, 34};
		int lossCount = 10;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == lossCount);
		assertTrue(losses[0] == 3);
		assertTrue((losses[1] == 3 && losses[2] == 4) ||
				   (losses[1] == 4 && losses[2] == 3));
	}
	
	@Test
	void LossDistributionTest7()
	{
		int[] originalCounts = new int[] {1, 1, 98};
		int lossCount = 10;
		int[] losses = CommonUtils.distributeLoss(originalCounts, lossCount, 0);
		
		assertTrue(losses.length == originalCounts.length);
		assertTrue(this.sumArray(losses) == lossCount);
		assertTrue(losses[0] == 1);
		assertTrue((losses[1] == 1 && losses[2] == 8) ||
				   (losses[1] == 0 && losses[2] == 9));
	}
	
	private int sumArray(int[] array)
	{
		int sum = 0;
		
		for (int i = 0; i < array.length; i++)
			sum+=array[i];
		
		return sum;
	}
}
