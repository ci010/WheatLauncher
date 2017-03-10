package net.launcher.utils.resource;

import net.launcher.utils.Patterns;

import java.io.*;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
public enum DefaultResourceType implements ResourceType
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

	public static DefaultResourceType getType(Path file)
	{
		return getType(file.toFile());
	}

	public static DefaultResourceType getType(File file)
	{
		if (DIR.match(file)) return DIR;
		if (JAR.match(file)) return JAR;
		if (ZIP.match(file)) return ZIP;
		return null;
	}
}
