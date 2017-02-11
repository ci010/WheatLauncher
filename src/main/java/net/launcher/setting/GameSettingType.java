package net.launcher.setting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public abstract class GameSettingType
{
	public abstract String getID();

	public abstract List<Option<?>> getAllOption();

	public abstract GameSetting load(Path minecraftFolder) throws IOException;

	public abstract GameSetting defaultInstance();

	public abstract void save(Path minecraftFolder, GameSetting setting) throws IOException;

	public abstract static class Option<T>
	{
		private GameSettingType parent;
		private String name;

		public Option(GameSettingType parent, String name)
		{
			this.parent = parent;
			this.name = name;
		}

		public String getName() {return name;}

		public GameSettingType getParent() {return parent;}

		public abstract GameSettingProperty<T> getDefaultValue(GameSetting gameSetting);

		public abstract T deserialize(GameSetting gameSetting, String s);

		public String serialize(Object tValue) {return tValue.toString();}
	}
}
