package net.launcher;

import java.util.Comparator;

/**
 * @author ci010
 */
public class ArrayUtils
{
	public static <T> int snap(T[] arr, T value, Comparator<T> comparator, boolean left)
	{
		int min = Integer.MAX_VALUE;
		int minIdx = -1;
		for (int i = 0; i < arr.length; i++)
		{
			int compare = comparator.compare(value, arr[i]);
			if (Math.abs(min) > Math.abs(compare))
			{
				min = compare;
				minIdx = i;
			}
		}

		int leftIdx, rightIdx;

		if (min > 0)
		{
			leftIdx = minIdx;
			rightIdx = minIdx + 1;
		}
		else if (min < 0)
		{
			leftIdx = minIdx - 1;
			rightIdx = minIdx;
		}
		else
		{
			leftIdx = minIdx - 1;
			rightIdx = minIdx + 1;
		}

		if (leftIdx < 0)
			leftIdx = 0;
		if (rightIdx >= arr.length)
			rightIdx = arr.length - 1;

		return left ? leftIdx : rightIdx;
	}

	public static int snap(int[] arr, int value, boolean left)
	{
		int min = Integer.MAX_VALUE;
		int minIdx = -1;
		for (int i = 0; i < arr.length; i++)
		{
			int compare = value - arr[i];
			if (Math.abs(min) > Math.abs(compare))
			{
				min = compare;
				minIdx = i;
			}
		}

		int leftIdx, rightIdx;

		if (min > 0)
		{
			leftIdx = minIdx;
			rightIdx = minIdx + 1;
		}
		else if (min < 0)
		{
			leftIdx = minIdx - 1;
			rightIdx = minIdx;
		}
		else
		{
			leftIdx = minIdx - 1;
			rightIdx = minIdx + 1;
		}

		if (leftIdx < 0)
			leftIdx = 0;
		if (rightIdx >= arr.length)
			rightIdx = arr.length - 1;

		return left ? leftIdx : rightIdx;
	}

}
