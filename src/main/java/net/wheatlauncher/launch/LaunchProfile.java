package net.wheatlauncher.launch;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.JsonSerializer;
import net.wheatlauncher.utils.SimpleStrictProperty;
import net.wheatlauncher.utils.StrictProperty;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.launch.LaunchException;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.File;
import java.io.IOException;

/**
 * @author ci010
 */
public class LaunchProfile
{
	public LaunchProfile(String name)
	{
		this(null, name);
	}

	public LaunchProfile(LaunchProfile parent, String name)
	{
		this.name = name;
		if (parent.parent == this)
			throw new IllegalArgumentException();
		this.parent = parent;
	}

	private final String name;

	private LaunchProfile parent;

	private StrictProperty<JavaEnvironment> javaEnvironmentSimpleStrictProperty = new SimpleStrictProperty<>
			(this, "java.environment", JavaEnvironment.current()).setValidator(
			(stateHandler, v) -> {
				if (v.getJavaPath().getName().equals("java.exe"))
					stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
				else stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "invalid"));
			});
	private StrictProperty<MinecraftDirectory> minecraftLocationProperty = new SimpleStrictProperty<>
			(this, "minecraft.location", new MinecraftDirectory(new File(".minecraft")));
	private StrictProperty<Number> memory = new SimpleStrictProperty<Number>(this, "java.memory", 512)
	{
		@Override
		public void set(Number newValue)
		{
			int value = newValue.intValue();
			int prevTick = (value - 256) / 256;
			double less = (prevTick) * 256 + 256;
			double more = (prevTick + 1) * 256 + 256;
			double lessDiff = value - less;
			double moreDiff = more - value;
			value = (int) (lessDiff < moreDiff ? less : more);
			super.set(value);
		}
	}.setValidator(
			((stateHandler, v) -> {
				if (v == null)
					stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "null"));
				else if (v.intValue() < 256) stateHandler.setValue(
						StrictProperty.State.of(StrictProperty.EnumState.FAIL, "too-small"));
				else stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
			})
	);
	private StrictProperty<String> version = new SimpleStrictProperty<>(this, "minecraft.version");
	private ConditionAuth conditionAuth = new ConditionAuth();
	private ListProperty<String> versionList = new SimpleListProperty<String>();

	protected void setupAuth(ConditionAuth auth)
	{
		conditionAuth.onlineMode().addListener((observable, oldValue, newValue) -> {
			System.out.println("online change " + newValue);
			if (newValue)
				auth.apply(DefaultAuthSetting.ONLINE);
			else
				auth.apply(DefaultAuthSetting.OFFLINE);
		});
	}

	public void init()
	{
		System.out.println("launch profile init");
		setupAuth(this.conditionAuth);
		versionList.addAll(Versions.getVersions(this.minecraftLocationProperty().getValue()));
		minecraftLocationProperty().addListener(observable -> {
			versionList.clear();
			versionList.addAll(Versions.getVersions(minecraftLocationProperty().getValue()));
		});
	}

	public String getName()
	{
		return name;
	}

	public YggdrasilAuthenticator.PasswordProvider getPasswordProvider()
	{
		return conditionAuth;
	}

	public ReadOnlyListProperty<String> versionList() {return this.versionList;}

	public StrictProperty<String> versionProperty() {return version;}

	public StrictProperty<Number> memoryProperty() {return memory;}

	public StrictProperty<MinecraftDirectory> minecraftLocationProperty() {return this.minecraftLocationProperty;}

	public StrictProperty<JavaEnvironment> javaLocationProperty() {return javaEnvironmentSimpleStrictProperty;}

	public BooleanProperty onlineModeProperty() {return conditionAuth.onlineMode();}

	public StrictProperty<String> accountProperty() {return conditionAuth.account();}

	public StrictProperty<String> passwordProperty() {return conditionAuth.password();}

	public ObservableValueBase<StrictProperty.EnumState> launchState() {return conditionAuth;}

	public ObservableValue<String> settingName() {return conditionAuth.settingName();}

	public static final JsonSerializer<LaunchProfile> SERIALIZER = new JsonSerializer<LaunchProfile>()
	{
		@Override
		public LaunchProfile deserialize(JSONObject jsonObject)
		{
			LaunchProfile profile;
			boolean hasParent;
			String name = jsonObject.getString("name");
			if (hasParent = jsonObject.has("parent"))
			{
				String parentName = jsonObject.getString("parent");
				LaunchProfile parent = Core.INSTANCE.profileMapProperty().get().get(parentName);
				profile = new LaunchProfile(parent, name);
			}
			else profile = new LaunchProfile(name);
			profile.javaLocationProperty().setValue(new JavaEnvironment(new File(jsonObject.getString("java"))));
			profile.memoryProperty().setValue(jsonObject.optInt("memory", 512));
			profile.minecraftLocationProperty().setValue(new MinecraftDirectory(new File(jsonObject.getString("minecraft"))));
			profile.onlineModeProperty().setValue(jsonObject.getString("online-mode").equals("enable"));
			profile.accountProperty().setValue(jsonObject.getString("account"));
			if (jsonObject.has("version"))
			{
				String version = jsonObject.getString("version");
			}
			return null;
		}

		@Override
		public JSONObject serialize(LaunchProfile data)
		{
			return null;
		}
	};

	public void launch()
	{
		Launcher launcher = LauncherBuilder.buildDefault();
		try
		{
			LaunchOption option = new LaunchOption(this.version.getValue(), conditionAuth,
					this.minecraftLocationProperty.getValue());
			launcher.launch(option);
		}
		catch (LaunchException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
