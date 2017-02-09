package net.wheatlauncher.internal.io;

import javafx.collections.MapChangeListener;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.game.nbt.NBTList;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.profile.LaunchProfileManagerBuilder;
import net.launcher.utils.DirUtils;
import org.to2mbn.jmccc.option.JavaEnvironment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
			e.printStackTrace();
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
		System.out.println("force save");

		Path path = this.getContext().getRoot().resolve("profiles.dat");
		LaunchProfileManager instance = this.getInstance();
		if (instance == null) throw new IllegalStateException();

		NBTCompound compound = NBT.compound();
		compound.put("selecting", instance.getSelectedProfile());
		compound.put("profiles", NBT.list(instance.getAllProfiles().stream().map(this::serialize).collect(Collectors.toList())));
		NBT.write(path, compound, false);

		System.out.println("force save done");
	}

	@Override
	public LaunchProfileManager loadInstance() throws IOException
	{
		Path path = this.getContext().getRoot().resolve("profiles.dat");
		if (Files.exists(path))
		{
			NBTCompound read = NBT.read(path, false).asCompound();
			String selecting = read.get("selecting").asString("");
			NBTList profiles = read.get("profiles").asList();
			List<LaunchProfile> collect = profiles.stream().map(this::deserialize).collect(Collectors.toList());
			LaunchProfileManager build = LaunchProfileManagerBuilder.create()
					.setInitState(collect)
					.setCreateGuard(this::onNewProfile)
					.setDeleteGuard(this::onDeleteProfile)
					.setCopyGuard(this::onCopyProfile)
					.build();
			build.setSelectedProfile(selecting);
			return build;
		}
		throw new IOException("profile.load.fail");
	}

	@Override
	public LaunchProfileManager defaultInstance()
	{
		LaunchProfileManager manager = LaunchProfileManagerBuilder.buildDefault();
		manager.newProfile("default");
		manager.setSelectedProfile("default");
		return manager;
	}

	@Override
	protected void deploy()
	{
		LaunchProfileManager instance = this.getInstance();
		Save save = new Save();
		this.getContext().registerSaveTask(save, instance.selectedProfileProperty(),
				instance.getAllProfiles());
		for (LaunchProfile profile : instance.getAllProfiles())
			getContext().registerSaveTask(save, profile.displayNameProperty(),
					profile.javaEnvironmentProperty(),
					profile.memoryProperty(),
					profile.versionProperty(),
					profile.resolutionProperty());
		instance.getProfilesMap().addListener((MapChangeListener<String, LaunchProfile>) change ->
		{
			LaunchProfile profile = change.getValueAdded();
			if (profile != null)
			{
				getContext().registerSaveTask(save, profile.displayNameProperty(),
						profile.javaEnvironmentProperty(),
						profile.memoryProperty(),
						profile.versionProperty(),
						profile.resolutionProperty());
			}
		});
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
