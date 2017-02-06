package net.launcher.utils;

import org.to2mbn.jmccc.option.WindowSize;

import java.awt.*;
import java.util.Comparator;

/**
 * @author ci010
 */
public class ResolutionUtils
{


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
