package net.launcher.utils;

/**
 * @author ci010
 */
public class StringUtils
{
	public static final String EMPTY = "";

	public static boolean isEmpty(String s)
	{
		return s == null || s.equals(EMPTY);
	}

	public static boolean isNotEmpty(String s)
	{
		return !isEmpty(s);
	}

	public static String validate(String s)
	{
		return isEmpty(s) ? "" : s;
	}

	public static String invalid(String s)
	{
		return isEmpty(s) ? null : s;
	}
}
