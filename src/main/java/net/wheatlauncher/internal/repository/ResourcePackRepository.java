package net.wheatlauncher.internal.repository;

import javafx.beans.value.ObservableValue;
import net.wheatlauncher.Core;
import net.wheatlauncher.MinecraftRepository;
import net.wheatlauncher.ResourcePack;
import net.wheatlauncher.internal.ResourcePackImpl;
import net.wheatlauncher.utils.DirUtils;
import net.wheatlauncher.utils.MD5;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.util.FileUtils;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
public abstract class ResourcePackRepository implements MinecraftRepository<ResourcePack>
{
	private Map<String, ResourcePack> nameToPack = new HashMap<>();

	public ResourcePack getResourcePack(String name)
	{
		return nameToPack.get(name);
	}

	protected abstract File getRoot();

	private ResourcePack searchDummy(byte[] dummy)
	{
		return new ResourcePackImpl("dummy", dummy, 0, true);
	}

	private void tryRegisterPack(final File file)
	{
		try
		{
			ResourcePack resourcePack = searchDummy(MD5.getMD5(file));
			if (nameToPack.containsValue(resourcePack))
				return;
		}
		catch (IOException ignored) {}
		String[] split = file.getName().split(".");
		final String name = split[0];
		if (file.isDirectory() && validateDir(file))
			try
			{
				File target = new File(getRoot(), name);
				DirUtils.copy(file, target);
				register0(target, name, true);
			}
			catch (IOException e) {}
		else if (file.isFile() && file.getName().endsWith(".zip") && validateZip(file))
			try
			{
				File target = new File(getRoot(), name + ".zip");
				FileUtils.copyFile(file, target);
				register0(target, name, true);
			}
			catch (IOException e) {}
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
			catch (IOException | NullPointerException ignored) {}
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
		catch (IOException | NullPointerException ignored) {}
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
			ResourcePack resourcePack = new ResourcePackImpl(name, md5Value, length, isZip);
			nameToPack.put(name, resourcePack);
		}
		catch (NoSuchAlgorithmException | IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getAllKey()
	{
		return nameToPack.keySet();
	}

	@Override
	public ResourcePack get(String key)
	{
		return nameToPack.get(key);
	}

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		File root = newValue.getRoot();
		File resourcepacks = new File(root, "resourcepacks");
		if (resourcepacks.isDirectory())
		{
			File[] files = resourcepacks.listFiles();
			if (files != null)
				for (File file : files)
					Core.INSTANCE.getService().submit(() -> tryRegisterPack(file));
		}
	}
}
