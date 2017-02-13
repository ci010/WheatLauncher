package net.launcher.utils;

import java.util.regex.Pattern;

/**
 * @author ci010
 */
public interface Patterns
{
	Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	Pattern ZIP = Pattern.compile("(.+).(zip)$");
	Pattern JAR = Pattern.compile("(.+).(jar)$");

	Pattern ZIP_JAR = Pattern.compile("(.+).(zip|jar)$");
	Pattern CLASS_FILE = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");


}
