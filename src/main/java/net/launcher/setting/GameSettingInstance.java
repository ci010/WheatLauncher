package net.launcher.setting;

import org.to2mbn.jmccc.util.Builder;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class GameSettingInstance
{
	private Map<String, Object> optionObjectMap = new TreeMap<>();
	private GameSetting type;

	public GameSettingInstance(GameSetting type)
	{
		this.type = type;
	}

	public GameSetting getGameSettingType()
	{
		return type;
	}

	public void loadFrom(GameSettingInstance settingInstance)
	{
		Objects.requireNonNull(settingInstance);
		if (settingInstance.getGameSettingType() != type) throw new IllegalArgumentException("wrong type!");
		this.optionObjectMap.clear();
		this.optionObjectMap.putAll(settingInstance.optionObjectMap);
	}

	public <V> V getOption(GameSetting.Option<V> s)
	{
		return (V) optionObjectMap.get(s.getName());
	}

	public <V> void setOption(GameSetting.Option<V> s, V o)
	{
		Objects.requireNonNull(o);
		V valid = s.valid(o);
		if (valid.equals(optionObjectMap.get(s.getName()))) return;
		optionObjectMap.put(s.getName(), valid);
	}

	public static class Template extends GameSettingInstance implements Builder<GameSettingInstance>
	{
		private String name;

		public Template(String name, GameSettingInstance instance)
		{
			super(instance.type);
			this.name = name;
		}

		public String getName() {return name;}

		@Override
		public void loadFrom(GameSettingInstance settingInstance)
		{
			super.loadFrom(settingInstance);
		}

		@Override
		public <V> void setOption(GameSetting.Option<V> s, V o)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public GameSettingInstance build()
		{
			GameSettingInstance gameSettingInstance = new GameSettingInstance(this.getGameSettingType());
			gameSettingInstance.loadFrom(this);
			return gameSettingInstance;
		}
	}
}
