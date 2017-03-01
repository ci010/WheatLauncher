package net.launcher.setting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public abstract class SettingType
{
	public abstract String getID();

	public abstract List<Option<?>> getAllOption();

	public abstract Setting load(Path minecraftFolder) throws IOException;

	public abstract Setting defaultInstance();

	public abstract void save(Path minecraftFolder, Setting setting) throws IOException;

	public abstract static class Option<T>
	{
		private SettingType parent;
		private String name;

		public Option(SettingType parent, String name)
		{
			this.parent = parent;
			this.name = name;
		}

		public String getName() {return name;}

		public SettingType getParent() {return parent;}

		public abstract SettingProperty<T> getDefaultValue(Setting setting);

		public abstract T deserialize(String s);

		public String serialize(Object tValue) {return tValue.toString();}
	}
}
