package net.launcher.utils;

import org.to2mbn.jmccc.option.WindowSize;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author ci010
 */
public class EnvironmentUtils
{
	public static int getFreePhysicalMemory()
	{
		return (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / 1024 / 1024);
	}

	public static int getTotalPhysicalMemory()
	{
		return (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1024 / 1024);
	}

	private static int[] availableMemory;

	public static int[] getAvailableMemory()
	{
		if (availableMemory != null) return availableMemory;
		int[] arr = new int[32];//notice that this is hard code 32... which mean the max is 1024*(2^32)
		int maxMemory = getTotalPhysicalMemory();

		arr[0] = 512;
		int base = 1024;
		int i = 1;
		while (base < maxMemory)
		{
			arr[i++] = base;//it doesn't check bound...
			base += 1024;
		}
		return availableMemory = Arrays.copyOf(arr, i);
	}

	public static WindowSize getScreenSize()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		return new WindowSize((int) width, (int) height);
	}

	public static Comparator<WindowSize> getComparator()
	{
		return (o1, o2) ->
				o1.isFullScreen() ? o2.isFullScreen() ? 0 : Integer.MAX_VALUE :
						o2.isFullScreen() ? Integer.MIN_VALUE :
								(o1.getWidth() - o2.getWidth()) + (o1.getHeight() - o2.getHeight());
	}

}
