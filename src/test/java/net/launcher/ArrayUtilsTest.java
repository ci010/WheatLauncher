package net.launcher;

import org.junit.Test;

/**
 * @author ci010
 */
public class ArrayUtilsTest
{
	@Test
	public void snap() throws Exception
	{
		Double[] arr = new Double[]{1D, 3D, 4D, 5D, 6D, 7D, 9D};
		int snap;
		snap = ArrayUtils.snap(arr, 2.5, Double::compareTo, true);
		assert snap == 0;

		snap = ArrayUtils.snap(arr, 2.5, Double::compareTo, false);
		assert snap == 1;

		snap = ArrayUtils.snap(arr, 1D, Double::compareTo, true);

		assert snap == 0;

		snap = ArrayUtils.snap(arr, 1D, Double::compareTo, true);
		assert snap == 0;

		snap = ArrayUtils.snap(arr, 1D, Double::compareTo, false);
		assert snap == 1;

		snap = ArrayUtils.snap(arr, 3D, Double::compareTo, false);
		assert snap == 2;

		snap = ArrayUtils.snap(arr, 3D, Double::compareTo, true);
		assert snap == 0;

		snap = ArrayUtils.snap(arr, 3.5D, (o1, o2) -> (int) ((o1 - o2) * 10), true);
		assert snap == 1;

		snap = ArrayUtils.snap(arr, 3.5D, (o1, o2) -> (int) ((o1 - o2) * 10), false);
		assert snap == 2;

	}

}
