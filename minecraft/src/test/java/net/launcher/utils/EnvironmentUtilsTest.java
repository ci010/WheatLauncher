package net.launcher.utils;

import org.junit.Test;

/**
 * @author ci010
 */
public class EnvironmentUtilsTest
{
	@Test
	public void getAvailableMemory() throws Exception
	{
		int[] availableMemory = EnvironmentUtils.getAvailableMemory();
		for (int i : availableMemory)
		{
			System.out.println(i);
		}
	}

}
