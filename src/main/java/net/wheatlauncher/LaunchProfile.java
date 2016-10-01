package net.wheatlauncher;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import net.launcher.utils.*;
import net.wheatlauncher.utils.Condition;
import net.wheatlauncher.utils.EventListenerUtils;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.Authenticator;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.io.File;

/**
 * @author ci010
 */
public class LaunchProfile
{
	public LaunchProfile()
	{
		this("default");
	}

	public LaunchProfile(String name)
	{
		this.name.set(name);
		setupAuth(this.conditionAuth);
		this.version.addListener((observable, oldValue, newValue) -> Logger.trace("change " + newValue));
	}

	private StringProperty name = new SimpleStringProperty();
	private StrictProperty<JavaEnvironment> javaEnvironmentSimpleStrictProperty =
			new WrappedStrictProperty<>(new SimpleObjectProperty<>(this, "java.environment", JavaEnvironment.current()))
					.withValidator((stateHandler, v) ->
					{
						if (v.getJavaPath().getName().equals("java.exe"))
							stateHandler.setValue(State.of(State.Values.PASS));
						else stateHandler.setValue(State.of(State.Values.FAIL, "invalid"));
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
			((stateHandler, v) ->
			{
				if (v == null)
					stateHandler.setValue(State.of(State.Values.FAIL, "null"));
				else if (v.intValue() < 256) stateHandler.setValue(
						State.of(State.Values.FAIL, "too-small"));
				else stateHandler.setValue(State.of(State.Values.PASS));
			})
	);
	private StrictProperty<String> version = new SimpleStrictProperty<>(this, "minecraft.version");
	private ConditionAuth conditionAuth = new ConditionAuth();
	private Condition conditionLaunch = new Condition().add(javaEnvironmentSimpleStrictProperty,
			minecraftLocationProperty, version, memory).add(conditionAuth);
	private StrictProperty<WindowSize> windowSize = new SimpleStrictProperty<>(new WindowSize(856, 482));
	private GameSettings gameSettings = new GameSettings(new File(Core.INSTANCE.getProfilesRoot(),
			this.nameProperty().get() + "/options.txt"));

	protected void setupAuth(ConditionAuth auth)
	{
		Logger.trace("setup Auth");
		EventListenerUtils.addListenerAndNotify(conditionAuth.onlineMode(), (observable, oldValue, newValue) ->
		{
			Logger.trace("Online mode change to " + newValue);
			if (newValue)
				auth.apply(DefaultAuthSetting.ONLINE);
			else
				auth.apply(DefaultAuthSetting.OFFLINE);
		});
	}

	public void onApply()
	{
		System.out.println("[LaunchProfile]onApply");
	}

	public void onUnapply()
	{
		System.out.println("unapplied");
	}

	public StringProperty nameProperty() {return name;}

	public StrictProperty<WindowSize> windowSizeProperty() {return windowSize;}

	public StrictProperty<String> versionProperty() {return version;}

	public StrictProperty<Number> memoryProperty() {return memory;}

	public StrictProperty<MinecraftDirectory> minecraftLocationProperty() {return this.minecraftLocationProperty;}

	public StrictProperty<JavaEnvironment> javaLocationProperty() {return javaEnvironmentSimpleStrictProperty;}

	public BooleanProperty onlineModeProperty() {return conditionAuth.onlineMode();}

	public StrictProperty<String> accountProperty() {return conditionAuth.account();}

	public StrictProperty<String> passwordProperty() {return conditionAuth.password();}

	public ObservableValueBase<State.Values> launchState() {return conditionLaunch;}

	public ObservableValueBase<State.Values> loginState() {return conditionAuth;}

	public ReadOnlyObjectProperty<AuthInfo> authInfoProperty()
	{
		return conditionAuth.authInfoProperty();
	}

	public ObservableValue<String> settingName() {return conditionAuth.settingName();}

	public boolean isPasswordEnable() {return conditionAuth.isPasswordEnable();}

	public GameSettings getGameSettings()
	{
		return gameSettings;
	}

	public Authenticator getAuth() {return conditionAuth;}

	public static final JsonSerializer<LaunchProfile> SERIALIZER = new JsonSerializer<LaunchProfile>()
	{
		@Override
		public LaunchProfile deserialize(JSONObject jsonObject)
		{
			String name = jsonObject.getString("nameProperty");
			LaunchProfile profile = new LaunchProfile(name);
			profile.javaLocationProperty().setValue(new JavaEnvironment(new File(
					jsonObject.optString("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath()))));
			profile.memoryProperty().setValue(jsonObject.optInt("memory", 512));

			profile.minecraftLocationProperty().setValue(new MinecraftDirectory(new File(
					jsonObject.optString("minecraft", ".minecraft"))));

			profile.onlineModeProperty().setValue(jsonObject.optBoolean("online-mode"));
			profile.accountProperty().setValue(jsonObject.optString("account"));
			try
			{
				String winSize = jsonObject.getString("windows-size");
				if (winSize.equals("Fullscreen"))
					profile.windowSize.setValue(WindowSize.fullscreen());
				else
				{
					String[] xes = winSize.split("x");
					profile.windowSize.setValue(new WindowSize(Integer.valueOf(xes[0]), Integer.valueOf(xes[1])));
				}
			}
			catch (Exception ignored)
			{
				profile.windowSize.setValue(new WindowSize(856, 482));
			}
			if (jsonObject.has("version"))
				profile.versionProperty().setValue(jsonObject.getString("version"));
			return profile;
		}

		@Override
		public JSONObject serialize(LaunchProfile data)
		{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("nameProperty", data.nameProperty().getValue());
			jsonObject.put("java", data.javaLocationProperty().getValue().getJavaPath().getAbsolutePath());
			jsonObject.put("memory", data.memoryProperty().getValue());
			jsonObject.put("minecraft", data.minecraftLocationProperty().getValue().getAbsolutePath());
			jsonObject.put("version", data.versionProperty().getValue());
			jsonObject.put("online-mode", data.onlineModeProperty().get());
			jsonObject.put("account", data.accountProperty().getValue());
			jsonObject.put("windows-size", data.windowSizeProperty().getValue().toString());
			return jsonObject;
		}
	};

	@Override
	public String toString()
	{
		return name.get();
	}
}
