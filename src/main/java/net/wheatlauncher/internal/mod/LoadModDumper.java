package net.wheatlauncher.internal.mod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author ci010
 */
class LoadModDumper
{
	static String loadJson()
	{
		URL[] urls = {LoadModDumper.class.getResource("dumpmc/DumpMod.class")};
		URLClassLoader urlClassLoader = new URLClassLoader(urls);
		try
		{
			Class<?> aClass = urlClassLoader.loadClass("dumpmc.DumpMod");
			Method dump = aClass.getMethod("dump");
			return (String) dump.invoke(null);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
