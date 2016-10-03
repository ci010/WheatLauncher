package net.launcher.utils.resource;

import net.launcher.utils.Patterns;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

				@Override
				public InputStream openStream(File file, String path) throws IOException
				{
					JarFile jar = new JarFile(file);
					ZipEntry entry = jar.getEntry(path);
					if (entry != null)
						return jar.getInputStream(entry);
					return null;
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

				@Override
				public InputStream openStream(File file, String path) throws IOException
				{
					ZipFile zipFile = new ZipFile(file);
					ZipEntry entry = zipFile.getEntry(path);
					if (entry != null)
						return zipFile.getInputStream(entry);
					return null;
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

				public InputStream openStream(File file, String path) throws FileNotFoundException
				{
					return new FileInputStream(new File(file, path));
				}
			};

	public abstract String getSuffix();

	public abstract boolean match(File file);

	public abstract InputStream openStream(File file, String path) throws IOException;

	public static ResourceType getType(File file)
	{
		if (DIR.match(file)) return DIR;
		if (JAR.match(file)) return JAR;
		if (ZIP.match(file)) return ZIP;
		return null;
	}
}
