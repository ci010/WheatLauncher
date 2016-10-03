package net.wheatlauncher.internal.io;

import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.util.Pair;
import net.launcher.LaunchProfile;
import net.launcher.LaunchProfileManager;
import net.launcher.LaunchProfileMangerBuilder;
import net.launcher.game.setting.Option;
import net.launcher.io.LoadHandler;
import net.launcher.io.MappedStorageType;
import net.launcher.io.SaveHandler;
import net.launcher.io.SourceObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * One kind of file system for profiles.
 * <p>-root
 * <p>--config.json
 * <p>--profiles
 * <p>---{profile name}
 * <p>----{some config files}
 * <p>----profile.json
 *
 * @author ci010
 */
public class ProfileMangerIO
{
	public static final SourceObject.Prototype
			CONFIG = new SourceObject.Prototype("config.json", MappedStorageType.JSON),
			PROFILE = new SourceObject.Prototype("profile.json", MappedStorageType.JSON);

	public static Map<SourceObject.Prototype, List<Pair<Option<?>, Property<?>>>> mapWithPrototype(Map<Option<?>, Property<?>> map)
	{
		return Collections.emptyMap();
	}

	public static Map<SourceObject.Prototype, Property<?>[]> convert(Map<Option<?>, Property<?>> map)
	{
		return Collections.emptyMap();
	}

	private ProfileManagerLoadHandler loadHandler;
	private ProfileSaveHandler profileSaveHandler;
	private ProfileMangerSaveHandler mangerSaveHandler;
	private File root;
	private WeakReference<LaunchProfileManager> profileManagerWeakReference;

	private ProfileMangerIO(File root)
	{
		this.root = root;
		loadHandler = new ProfileManagerLoadHandler(root);
		profileSaveHandler = new ProfileSaveHandler(root);
		mangerSaveHandler = new ProfileMangerSaveHandler(root);
	}


	public File getProfileDir(LaunchProfile profile)
	{
		return new File(root, "profiles/" + profile.nameProperty().getValue());
	}

	public File getProfileSettingFile(LaunchProfile profile)
	{
		return new File(getProfileDir(profile), "profile.json");
	}

	public LoadHandler<LaunchProfileManager> getLoadHandler()
	{
		return loadHandler;
	}

	public SaveHandler<LaunchProfileManager> getMangerSaveHandler()
	{
		return mangerSaveHandler;
	}

	/**
	 * Create a save/load system unitedly from a certain root file.
	 *
	 * @param root The root of the file system.
	 * @return The IO manger
	 */
	public static ProfileMangerIO create(File root, LaunchProfileManager manager)
	{
		ProfileMangerIO io = new ProfileMangerIO(root);
		io.mangerSaveHandler.add(manager);
		manager.allProfilesProperty().addListener((ListChangeListener<LaunchProfile>) c ->
		{
			for (LaunchProfile iLaunchProfile : c.getAddedSubList())
				io.profileSaveHandler.add(iLaunchProfile);
		});
		return io;
	}

	public void loadAll()
	{

	}

	public void saveAll()
	{

	}

	public LaunchProfileManager instantate() throws IOException
	{
		return loadHandler.fetch(CONFIG.create()).orElse(LaunchProfileMangerBuilder.buildDefault());
	}

	public LaunchProfileManager instantateFromData(String configFileContent)
	{
		LaunchProfileManager manager = LaunchProfileMangerBuilder.buildDefault();
		try
		{
			return loadHandler.loadFromString(manager, CONFIG.create(), configFileContent).orElse(manager);
		}
		catch (IOException e)
		{

		}
		return null;
	}
}
