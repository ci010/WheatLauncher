package net.wheatlauncher.utils;

import java.util.regex.Pattern;

/**
 * @author ci010
 */
public interface Patterns
{
	Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern
			.CASE_INSENSITIVE);
	Pattern zipJarPattern = Pattern.compile("(.+).(zip|jar)$");
}
