package net.launcher.mod;

import api.launcher.setting.Setting;
import api.launcher.setting.SettingMods;
import api.launcher.setting.SettingProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.launcher.game.ModManifest;
import net.launcher.utils.NIOUtils;
import org.to2mbn.jmccc.internal.org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class SettingMod extends SettingMods
{
	public static SettingMod INSTANCE;

	public final Option<ModManifest> MODS = new Option<ModManifest>(this, "mods")
	{
		@Override
		public SettingProperty<ModManifest> getDefaultValue(Setting setting)
		{
			return new SettingProperty<ModManifest>()
			{
				private ObjectProperty<ModManifest> property = new SimpleObjectProperty<>();

				@Override
				public Setting getBean()
				{
					return setting;
				}

				@Override
				public Option<ModManifest> getOption()
				{
					return MODS;
				}

				@Override
				public String getName()
				{
					return "Mods";
				}

				public void setValue(ModManifest v) {property.setValue(v);}

				public void bindBidirectional(Property other) {property.bindBidirectional(other);}

				public void unbindBidirectional(Property other) {property.unbindBidirectional(other);}

				@Override
				public String toString() {return property.toString();}

				@Override
				public ModManifest getValue() {return property.getValue();}

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
			return ModManifest.serializer().serialize((ModManifest) tValue).toString();
		}

		@Override
		public ModManifest deserialize(String s)
		{
			return ModManifest.serializer().deserialize(new JSONArray(s));
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
			ModManifest stat = MODS.deserialize(s);
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
		ModManifest value = setting.getOption(MODS).getValue();
		if (value != null) NIOUtils.writeString(path, MODS.serialize(value));
	}

	@Override
	public Option<ModManifest> getMods()
	{
		return MODS;
	}
}
