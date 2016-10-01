package net.launcher.utils.resource;

import net.launcher.utils.Patterns;

import java.io.File;

/**
 * @author ci010
 */
public enum ResourceType
{
	JAR
			{
				@Override
				public String getSuffix()
				{
					return ".jar";
				}

				@Override
				public boolean match(File file)
				{
					return Patterns.JAR.matcher(file.getName()).matches();
				}
			},
	ZIP
			{
				@Override
				public String getSuffix()
				{
					return ".zip";
				}

				@Override
				public boolean match(File file)
				{
					return Patterns.ZIP.matcher(file.getName()).matches();
				}
			},
	DIR
			{
				@Override
				public String getSuffix()
				{
					return "";
				}

				@Override
				public boolean match(File file)
				{
					return file.isDirectory();
				}
			};

	public abstract String getSuffix();

	public abstract boolean match(File file);

	public static ResourceType getType(File file)
	{
		if (DIR.match(file)) return DIR;
		if (JAR.match(file)) return JAR;
		if (ZIP.match(file)) return ZIP;
		return null;
	}
}
