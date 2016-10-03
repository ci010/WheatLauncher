package net.wheatlauncher;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import net.launcher.game.setting.Option;
import net.launcher.utils.ChangeListenerCache;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class GameSettings
{
	private File root;

	private Map<Option<?>, Property<?>> cached = new HashMap<>();
	private Lock lock = new ReentrantLock();
	private Set<String> enabledSettingDomain = new TreeSet<>();

	private ChangeListenerCache<LaunchProfile, MinecraftDirectory> minLis = new ChangeListenerCache<>(
			launchProfile -> (o, old, newV) ->
			{
				File file = new File(newV.getRoot(), "options.txt");
				if (file.isFile())
				{
					try
					{
						load(file);
					}
					catch (IOException e)
					{
						//TODO store exception
					}
				}
			}
	);

	public GameSettings(File root)
	{
		this.root = root;
		Core.INSTANCE.selectedProfileProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null) oldValue.minecraftLocationProperty().removeListener(minLis.listener());
			newValue.minecraftLocationProperty().addListener(minLis.listener(newValue));
		});

		File vanilla = new File(root, "options.txt");
		if (vanilla.isFile())
			try
			{
				this.load(vanilla);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	public void enable(String domain)
	{
		enabledSettingDomain.add(domain);
	}

	public boolean isEnabled(String domain)
	{
		return enabledSettingDomain.contains(domain);
	}

	public <T> Property<T> getOption(Option<T> option)
	{
		Property<T> property = (Property<T>) cached.get(option);
		if (property == null)
			cached.put(option, property = createProperty(option, null));
		return property;
	}

	public void load(File file) throws IOException
	{
		if (file.isFile())
		{
			Properties properties = new Properties();

			lock.lock();
			try (FileInputStream in = new FileInputStream(file))
			{
				properties.load(in);
			}
			finally
			{
				lock.unlock();
			}
			properties.forEach((k, v) ->
			{
				Option option = null;
				try
				{
					option = fromString(k.toString());
				}
				catch (IllegalArgumentException ignored) {}
				if (option != null)
				{
					v = option.deserialize(v.toString());
					cached.put(option, createProperty(option, v));
				}
			});
		}
	}

	public void save(String domain) throws IOException
	{
		Map<String, String> cfg = new TreeMap<>();
		Properties properties = new Properties();
		File file = new File(root, domain);
		if (file.isFile())
		{
			String s = "";
			lock.lock();
			try
			{
				s = IOUtils.toString(file);
			}
			finally
			{
				lock.unlock();
			}
			String[] lines = s.split("\n");
			for (String line : lines)
			{
				String[] keyValue = line.split(":");
				if (keyValue.length == 2)
					cfg.put(keyValue[0], keyValue[1]);
			}
		}

//		ALL.stream().filter(option -> option.sourceFile().equals(domain) && cached.containsKey(option)
//				&& (!file.isFile() || properties.containsKey(option.getName())))
//				.forEach(option -> properties.put(option.getName(), cached.get(option).getValue()));
//		lock.lock();

		String property = System.getProperty("line.separator");
		String collect = cfg.entrySet().stream().map(pair -> pair.getKey() + ":" + pair.getValue() + property).collect(Collectors.joining("\n"));
		try (FileOutputStream out = new FileOutputStream(root))
		{
			out.write(collect.getBytes());
		}
		finally
		{
			lock.unlock();
		}
	}

	private InvalidationListener listener = o ->
	{
//		try
//		{
//			save();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	};

	private <T> Property<T> createProperty(Option<T> option, Object v)
	{
		if (v == null) v = option.defaultValue();
		SimpleObjectProperty<T> prop = new SimpleObjectProperty<>((T) v);
		prop.addListener(listener);
		return prop;
	}

	private static Option<?> fromString(String s)
	{
//		for (Option option : ALL)
//			if (option.getName().equals(s)) return option;
		return null;
	}


}
