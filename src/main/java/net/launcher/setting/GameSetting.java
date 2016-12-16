package net.launcher.setting;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public abstract class GameSetting
{
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ID
	{
		String value();
	}

	public abstract List<Option<?>> getAllOption();

	public abstract GameSettingInstance load(Path directory) throws IOException;

	public abstract void save(Path directory, GameSettingInstance setting) throws IOException;

	public abstract void saveTemplate(Path templateRoot, GameSettingInstance.Template instance) throws IOException;

	public abstract GameSettingInstance.Template loadTemplate(Path templateRoot, String template) throws IOException;

	public abstract static class Option<T>
	{
		private GameSetting parent;
		private String name;

		public Option(GameSetting parent, String name)
		{
			this.parent = parent;
			this.name = name;
		}

		public String getName() {return name;}

		public T valid(T v) {return v;}

		public GameSetting getParent() {return parent;}

		public abstract T getDefaultValue();

		public abstract T deserialize(String s);

		public String serialize(Object tValue)
		{
			return tValue.toString();
		}
	}
}
