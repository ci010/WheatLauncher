package net.launcher.mod;

import api.launcher.setting.Setting;
import api.launcher.setting.SettingProperty;
import api.launcher.setting.SettingType;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.launcher.game.ServerStatus;
import net.launcher.utils.NIOUtils;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class SettingMod extends SettingType
{
	public static SettingMod INSTANCE;

	static
	{
//		SettingManager.register(SettingMod.class);
//		INSTANCE = (SettingMod) SettingManager.find("Forge").get();
	}

	public final Option<ServerStatus.ModInfo> MODS = new Option<ServerStatus.ModInfo>(this, "mods")
	{
		@Override
		public SettingProperty<ServerStatus.ModInfo> getDefaultValue(Setting setting)
		{
			return new SettingProperty<ServerStatus.ModInfo>()
			{
				private ObjectProperty<ServerStatus.ModInfo> property = new SimpleObjectProperty<>();

				@Override
				public Setting getBean()
				{
					return setting;
				}

				@Override
				public Option<ServerStatus.ModInfo> getOption()
				{
					return MODS;
				}

				@Override
				public String getName()
				{
					return "Mods";
				}

				public void setValue(ServerStatus.ModInfo v) {property.setValue(v);}

				public void bindBidirectional(Property other) {property.bindBidirectional(other);}

				public void unbindBidirectional(Property other) {property.unbindBidirectional(other);}

				@Override
				public String toString() {return property.toString();}

				@Override
				public ServerStatus.ModInfo getValue() {return property.getValue();}

				public void addListener(ChangeListener listener) {property.addListener(listener);}

				public void removeListener(ChangeListener listener) {property.removeListener(listener);}

				@Override
				public void addListener(InvalidationListener listener) {property.addListener(listener);}

				@Override
				public void removeListener(InvalidationListener listener) {property.removeListener(listener);}

				public void bind(ObservableValue observable) {property.bind(observable);}

				@Override
				public void unbind() {property.unbind();}

				@Override
				public boolean isBound() {return property.isBound();}
			};
		}

		@Override
		public String serialize(Object tValue)
		{
			return ServerStatus.modInfoSerializer().serialize((ServerStatus.ModInfo) tValue).toString();
		}

		@Override
		public ServerStatus.ModInfo deserialize(String s)
		{
			return ServerStatus.modInfoSerializer().deserialize(new JSONObject(s));
		}
	};

	@Override
	public String getID()
	{
		return "Forge";
	}

	@Override
	public List<Option<?>> getAllOption()
	{
		return Collections.singletonList(MODS);
	}

	@Override
	public Setting load(Path minecraftFolder) throws IOException
	{
		Path path = minecraftFolder.resolve("mods.json");
		if (Files.exists(path))
		{
			String s = NIOUtils.readToString(path);
			ServerStatus.ModInfo stat = MODS.deserialize(s);
			Setting of = Setting.of(this);
			of.getOption(MODS).setValue(stat);
			return of;
		}
		return null;
	}

	@Override
	public Setting defaultInstance()
	{
		return Setting.of(this);
	}

	@Override
	public void save(Path directory, Setting setting) throws IOException
	{
		Path path = directory.resolve("mods.json");
		ServerStatus.ModInfo value = setting.getOption(MODS).getValue();
		if (value != null) NIOUtils.writeString(path, MODS.serialize(value));
	}
}
