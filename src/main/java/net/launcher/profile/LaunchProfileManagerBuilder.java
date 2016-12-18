package net.launcher.profile;

import javafx.util.Builder;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ci010
 */
public class LaunchProfileManagerBuilder implements Builder<LaunchProfileManager>
{
	public static LaunchProfileManagerBuilder create() {return new LaunchProfileManagerBuilder();}

	public static LaunchProfileManager buildDefault() {return create().build();}

	public static Function<String, LaunchProfile> defaultProfileFactory() {return s -> new LaunchProfile();}

	public static BiConsumer<String, String> defaultRenameGuard()
	{
		return (s, n) ->
		{
		};
	}

	public static Consumer<String> defaultDeleteGuard()
	{
		return (p) ->
		{
		};
	}

	public LaunchProfileManagerBuilder setProfileFactory(Function<String, LaunchProfile> factory)
	{
		Objects.requireNonNull(factory);
		this.factory = factory;
		return this;
	}

	public LaunchProfileManagerBuilder setInitState(Map<String, LaunchProfile> profileMap)
	{
		this.loaded = profileMap;
		return this;
	}

	public Consumer<String> getDeleteGuard()
	{
		return deleteGuard;
	}

	public BiConsumer<String, String> getRenameGuard()
	{
		return renameGuard;
	}

	public Function<String, LaunchProfile> getFactory()
	{
		return factory;
	}

	public LaunchProfileManagerBuilder setRenameGuard(BiConsumer<String, String> renameGuard)
	{
		this.renameGuard = renameGuard;
		return this;
	}

	public LaunchProfileManagerBuilder setDeleteGuard(Consumer<String> deleteGuard)
	{
		Objects.requireNonNull(deleteGuard);
		this.deleteGuard = deleteGuard;
		return this;
	}

	private Map<String, LaunchProfile> loaded = Collections.emptyMap();
	private BiConsumer<String, String> renameGuard = defaultRenameGuard();
	private Consumer<String> deleteGuard = defaultDeleteGuard();
	private Function<String, LaunchProfile> factory = defaultProfileFactory();

	@Override
	public LaunchProfileManager build()
	{
		return new LaunchProfileManager(loaded, factory, renameGuard, deleteGuard);
	}

	private LaunchProfileManagerBuilder() {}
}
