package net.launcher.profile;

import api.launcher.LaunchProfile;
import api.launcher.LaunchProfileManager;
import javafx.util.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class LaunchProfileManagerBuilder implements Builder<LaunchProfileManager>
{
	public static LaunchProfileManagerBuilder create() {return new LaunchProfileManagerBuilder();}

	public static Object buildDefault() {return create().build();}

	public static Consumer<LaunchProfile> defaultProfileFactory()
	{
		return (profile) ->
		{
		};
	}

	public static BiConsumer<LaunchProfile, LaunchProfile> defaultCopyGuard()
	{
		return (s, n) ->
		{
		};
	}

	public static Consumer<LaunchProfile> defaultDeleteGuard()
	{
		return (p) ->
		{
		};
	}


	public LaunchProfileManagerBuilder setCreateGuard(Consumer<LaunchProfile> factory)
	{
		Objects.requireNonNull(factory);
		this.createGuard = factory;
		return this;
	}

	public LaunchProfileManagerBuilder setInitState(List<LaunchProfile> profiles)
	{
		Objects.requireNonNull(profiles);
		this.profiles = profiles;
		return this;
	}

	public BiConsumer<LaunchProfile, LaunchProfile> getCopyGuard() {return copyGuard;}

	public LaunchProfileManagerBuilder setCopyGuard(BiConsumer<LaunchProfile, LaunchProfile> copyGuard)
	{
		Objects.requireNonNull(copyGuard);
		this.copyGuard = copyGuard;
		return this;
	}

	public Consumer<LaunchProfile> getDeleteGuard()
	{
		return deleteGuard;
	}

	public Consumer<LaunchProfile> getCreateGuard()
	{
		return createGuard;
	}

	public LaunchProfileManagerBuilder setDeleteGuard(Consumer<LaunchProfile> deleteGuard)
	{
		Objects.requireNonNull(deleteGuard);
		this.deleteGuard = deleteGuard;
		return this;
	}

	private List<LaunchProfile> profiles = new ArrayList<>();
	private Consumer<LaunchProfile> deleteGuard = defaultDeleteGuard();
	private Consumer<LaunchProfile> createGuard = defaultProfileFactory();
	private BiConsumer<LaunchProfile, LaunchProfile> copyGuard = defaultCopyGuard();

	@Override
	public LaunchProfileManager build()
	{
		return new LaunchProfileManagerImpl(profiles, createGuard, deleteGuard, copyGuard);
	}

	private LaunchProfileManagerBuilder() {}
}
