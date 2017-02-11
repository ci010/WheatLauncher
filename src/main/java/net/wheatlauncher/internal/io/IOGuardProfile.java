package net.wheatlauncher.internal.io;

import javafx.collections.MapChangeListener;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.profile.LaunchProfileManagerBuilder;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingManager;
import net.launcher.setting.GameSettingType;
import net.launcher.utils.DirUtils;
import org.to2mbn.jmccc.option.JavaEnvironment;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardProfile extends IOGuard<LaunchProfileManager>
{
	private Path getProfilesRoot() {return getContext().getRoot().resolve("profiles");}

	private Path getProfileDir(String name) {return getProfilesRoot().resolve(name);}

	private NBT serialize(LaunchProfile profile)
	{
		return NBT.compound().put("name", profile.getDisplayName()).put("id", profile.getId()).put("memory", profile
				.getMemory()).put("java", profile.getJavaEnvironment().getJavaPath().getAbsolutePath())
				.put("resolution", profile.getResolution().toString())
				.put("version", profile.getVersion());
	}

	private LaunchProfile deserialize(NBT nbt)
	{
		NBTCompound compound = nbt.asCompound();
		LaunchProfile launchProfile = new LaunchProfile(compound.get("id").asString());
		launchProfile.setDisplayName(compound.get("name").asString());
		launchProfile.setJavaEnvironment(new JavaEnvironment(new File(compound.get("java").asString())));
		launchProfile.setMemory(compound.get("memory").asInt());
		launchProfile.setVersion(compound.get("version").asString());
		return launchProfile;
	}

	private void onNewProfile(LaunchProfile profile)
	{
		Path profileRoot = getProfileDir(profile.getId());
		try {Files.createDirectories(profileRoot);}
		catch (IOException e) {throw new IllegalArgumentException(e);}
	}

	private void onDeleteProfile(LaunchProfile profile)
	{
		Path profileRoot = getProfileDir(profile.getId());
		try
		{
			DirUtils.deleteContent(profileRoot);
			Files.delete(profileRoot);
		}
		catch (IOException e)
		{
			//TODO add suppressed
		}
	}

	private void onCopyProfile(LaunchProfile profile, LaunchProfile copy)
	{
		Path profileRoot = getProfileDir(profile.getId());
		Path copyRoot = getProfileDir(copy.getId());


	}

	@Override
	public void forceSave() throws IOException
	{
		Path path = this.getContext().getRoot().resolve("profiles.dat");
		LaunchProfileManager instance = this.getInstance();
		if (instance == null) throw new IllegalStateException();

		NBTCompound compound = NBT.compound();
		compound.put("selecting", instance.getSelectedProfile());
		compound.put("profiles", NBT.listStr(instance.getAllProfiles().stream().map(LaunchProfile::getId).collect(Collectors.toList())));
		NBT.write(path, compound, false);
	}

	@Override
	public LaunchProfileManager loadInstance() throws IOException
	{
		Path path = this.getContext().getRoot().resolve("profiles.dat");
		List<String> profilesRecord = null;
		String selecting = null;

		if (Files.exists(path))
		{
			NBTCompound read = NBT.read(path, false).asCompound();
			selecting = read.get("selecting").asString();
			profilesRecord = read.get("profiles").asList().stream().map(NBT::asString).collect(Collectors.toList());
		}

		ArrayList<GameSettingType> list = new ArrayList<>(GameSettingManager.getAllSetting().values());
		List<LaunchProfile> profilesList = new ArrayList<>();
		Files.walkFileTree(getProfilesRoot(), Collections.emptySet(), 1, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				Path prof = dir.resolve("profile.dat");
				if (!Files.exists(prof)) return super.preVisitDirectory(dir, attrs);
				LaunchProfile deserialize = deserialize(NBT.read(prof, false));
				profilesList.add(deserialize);
				for (GameSettingType gameSettingType : list)
				{
					GameSetting load = gameSettingType.load(dir);
					if (load != null) deserialize.addGameSetting(load);
				}
				return super.preVisitDirectory(dir, attrs);
			}
		});

		if (profilesRecord != null)
		{
			for (LaunchProfile profile : profilesList)
				profilesRecord.remove(profile.getId());
			if (!profilesRecord.isEmpty())
			{
				//TODO bad record
			}
		}

		if (profilesList.isEmpty())
			throw new IOException("profile.load.fail");

		LaunchProfileManager build = LaunchProfileManagerBuilder.create()
				.setInitState(profilesList)
				.setCreateGuard(this::onNewProfile)
				.setDeleteGuard(this::onDeleteProfile)
				.setCopyGuard(this::onCopyProfile)
				.build();
		if (selecting == null) selecting = profilesList.get(0).getId();
		build.setSelectedProfile(selecting);

		return build;
	}

	@Override
	public LaunchProfileManager defaultInstance()
	{
		LaunchProfileManager manager = LaunchProfileManagerBuilder.create()
				.setCreateGuard(this::onNewProfile)
				.setDeleteGuard(this::onDeleteProfile)
				.setCopyGuard(this::onCopyProfile)
				.build();
		manager.newProfile("default");
		manager.setSelectedProfile("default");
		return manager;
	}

	@Override
	protected void deploy() throws IOException
	{
		LaunchProfileManager instance = this.getInstance();
		Save save = new Save();
		this.getContext().registerSaveTask(save, instance.selectedProfileProperty(),
				instance.getAllProfiles());
		for (LaunchProfile profile : instance.getAllProfiles())
			getContext().registerSaveTask(new SaveProfile(profile), profile.displayNameProperty(),
					profile.javaEnvironmentProperty(),
					profile.memoryProperty(),
					profile.versionProperty(),
					profile.resolutionProperty());
		instance.getProfilesMap().addListener((MapChangeListener<String, LaunchProfile>) change ->
		{
			LaunchProfile profile = change.getValueAdded();
			if (profile != null)
			{
				getContext().registerSaveTask(new SaveProfile(profile), profile.displayNameProperty(),
						profile.javaEnvironmentProperty(),
						profile.memoryProperty(),
						profile.versionProperty(),
						profile.resolutionProperty());
			}
		});
	}

	class SaveProfile implements IOGuardContext.IOTask
	{
		private LaunchProfile profile;

		SaveProfile(LaunchProfile profile) {this.profile = profile;}

		@Override
		public void performance(Path root) throws Exception
		{
			Path profileDir = getProfileDir(profile.getId());
			Path resolve = profileDir.resolve("profile.dat");
			NBT.write(resolve, serialize(profile).asCompound(), false);
		}

		@Override
		public boolean canMerge(IOGuardContext.IOTask task)
		{
			return task == this ||
					(task instanceof SaveProfile && Objects.equals(((SaveProfile) task).profile, profile));
		}
	}

	class Save implements IOGuardContext.IOTask
	{
		@Override
		public void performance(Path root) throws Exception
		{
			forceSave();
		}

		@Override
		public boolean canMerge(IOGuardContext.IOTask task)
		{
			return task == this || task instanceof Save;
		}
	}
}
