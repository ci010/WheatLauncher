package net.wheatlauncher.resourcepack;

import net.wheatlauncher.Core;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.FileUtils;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
public abstract class ResourcePackRepository
{
	private Map<String, ResourcePack> nameToPack = new HashMap<String, ResourcePack>();

	public ResourcePack getResourcePack(String name)
	{
		return nameToPack.get(name);
	}

	public abstract File getRoot();

	public void tryRegisterPack(final File file) throws FileNotFoundException
	{
		String[] split = file.getName().split(".");
		final String name = split[0];
		if (file.isDirectory() && validateDir(file))
			Core.INSTANCE.getIOService().submit(() -> {
				boolean mkdir = new File(getRoot(), file.getName()).mkdir();
				File[] files = file.listFiles();
				while (files != null)
				{
					for (File f : files)
					{
//							if (f.isFile())

					}
				}
			});
		else if (file.isFile() && file.getName().endsWith(".zip") && validateZip(file))
			Core.INSTANCE.getIOService().submit(() -> {
				try
				{
					FileUtils.copyFile(file, new File(getRoot(), name + ".zip"));
					register0(file, name, true);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
	}


	private boolean validateDir(File file)
	{
		File meta = new File(file, "pack.mcmeta");
		if (meta.isFile())
		{
			try
			{
				JSONObject jsonObject = IOUtils.toJson(meta);
				JSONObject pack = jsonObject.getJSONObject("pack");
				if (pack.has("pack_format"))
					return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				return false;
			}
		}
		return false;
	}

	private boolean validateZip(File file)
	{
		try
		{
			ZipFile zipFile = new ZipFile(file);
			ZipEntry entry = zipFile.getEntry("pack.mcmeta");
			if (entry != null)
			{
				InputStream inputStream = zipFile.getInputStream(entry);
				JSONObject jsonObject = new JSONObject(IOUtils.toString(inputStream));
				JSONObject pack = jsonObject.getJSONObject("pack");
				if (pack.has("pack_format"))
					return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			return false;
		}
		return false;
	}

	private void register0(File file, String name, boolean isZip)
	{
		try
		{
			byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md5Value = md5.digest(bytes);
			long length = file.length();
			ResourcePack resourcePack = new ResourcePack(name, md5Value, length, isZip);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
