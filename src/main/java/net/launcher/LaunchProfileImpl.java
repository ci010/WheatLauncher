package net.launcher;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import net.launcher.auth.AuthenticationIndicator;
import net.launcher.setting.Option;
import net.launcher.utils.*;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;
import org.to2mbn.jmccc.util.UUIDUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ci010
 */
class LaunchProfileImpl implements ILaunchProfile
{
	private StringProperty name = new SimpleStringProperty("default");
	private StrictProperty<WindowSize> winSize = new WrappedStrictProperty<>(new SimpleStrictProperty<>(new WindowSize(856, 482)));
	private StrictProperty<String> version = new WrappedStrictProperty<>(new SimpleStrictProperty<>());
	private StrictProperty<MinecraftDirectory> mcLoc = new WrappedStrictProperty<>(
			new SimpleStrictProperty<>(this, "minecraft.location", new MinecraftDirectory(new File(".minecraft"))));
	private StrictProperty<JavaEnvironment> javaloc = new WrappedStrictProperty<>(
			new SimpleStrictProperty<>(this, "java", JavaEnvironment.current()))
			.withValidator((stateHandler, v) ->
			{
				if (v.getJavaPath().getName().equals("java.exe"))
					stateHandler.setValue(State.of(State.Values.PASS));
				else stateHandler.setValue(State.of(State.Values.FAIL, "invalid"));
			});
	private StrictProperty<String> username = new WrappedStrictProperty<>(new SimpleStringProperty()),
			password = new WrappedStrictProperty<>(new SimpleStringProperty());
	private StrictProperty<Number> memo = new WrappedStrictProperty<>(new SimpleIntegerProperty())
			.withValidator((stateHandler, v) ->
			{
				if (v == null)
					stateHandler.setValue(State.of(State.Values.FAIL, "null"));
				else if (v.intValue() < 256)
					stateHandler.setValue(State.of(State.Values.FAIL, "too-small"));
				else stateHandler.setValue(State.of(State.Values.PASS));
			});
	private StringProperty
			clientToken = new SimpleStringProperty(UUIDUtils.randomUnsignedUUID()),
			accessToken = new SimpleStringProperty();
	private StrictProperty<AuthenticationIndicator> indic = new WrappedStrictProperty<>(new SimpleStrictProperty<>());

	{
		indic.addListener((observable, oldV, newV) ->
		{
			if (oldV != null) oldV.unWatch(this);
			newV.watch(this);
		});
	}

	private Map<Option<?>, Property<?>> controlledOption = new TreeMap<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
	private Map<Option<?>, Property<?>> view = Collections.unmodifiableMap(controlledOption);

	@Override
	public Property<String> nameProperty()
	{
		return name;
	}

	@Override
	public StatedProperty<WindowSize> resolutionProperty()
	{
		return winSize;
	}

	@Override
	public StatedProperty<String> versionProperty()
	{
		return version;
	}

	@Override
	public StatedProperty<Number> memoryProperty()
	{
		return memo;
	}

	@Override
	public StatedProperty<MinecraftDirectory> minecraftProperty()
	{
		return mcLoc;
	}

	@Override
	public StatedProperty<JavaEnvironment> javaProperty()
	{
		return javaloc;
	}

	@Override
	public StrictProperty<String> accountProperty()
	{
		return username;
	}

	@Override
	public StrictProperty<String> passwordProperty()
	{
		return password;
	}

	@Override
	public Property<String> clientToken()
	{
		return clientToken;
	}

	@Override
	public Property<String> accessToken()
	{
		return accessToken;
	}

	@Override
	public <T> Property<T> createSettingIfAbsent(Option<T> option)
	{
		Optional<Property<T>> setting = getSetting(option);
		if (setting.isPresent())
			return setting.get();
		Property<T> prop = new SimpleObjectProperty<T>(this, option.getName(), option.defaultValue())
		{
			@Override
			public void set(T newValue)
			{
				super.set(option.setFunction().apply(newValue));
			}
		};

		controlledOption.put(option, prop);
		return prop;
	}

	@Override
	public <T> Optional<Property<T>> getSetting(Option<T> option)
	{
		return Optional.ofNullable((Property<T>) controlledOption.get(option));
	}

	@Override
	public Map<Option<?>, Property<?>> getAllOption()
	{
		return view;
	}

	@Override
	public Property<AuthenticationIndicator> authProperty()
	{
		return this.indic;
	}

	@Override
	public void onApply()
	{

	}

	@Override
	public void onDispose()
	{

	}

	@Override
	public LaunchOption build()
	{
		LaunchOption launchOption = null;
		try
		{
			launchOption = new LaunchOption(version.getValue(), () -> indic.getValue().authProperty().getValue(), minecraftProperty().getValue());
			launchOption.setJavaEnvironment(javaProperty().getValue());
			launchOption.setWindowSize(this.winSize.getValue());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return launchOption;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LaunchProfileImpl profile = (LaunchProfileImpl) o;

		return name.get().equals(profile.name.get());
	}

	@Override
	public int hashCode()
	{
		return name.get().hashCode();
	}

	private List<InvalidationListener> listeners = new ArrayList<>();

	@Override
	public void addListener(InvalidationListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener)
	{
		listeners.remove(listener);
	}
}
