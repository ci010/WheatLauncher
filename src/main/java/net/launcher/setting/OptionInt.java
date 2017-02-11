package net.launcher.setting;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author ci010
 */
public class OptionInt extends GameSettingType.Option<Number>
{
	private int min, max;
	private int defValue;
	private int step = 1;

	public OptionInt(GameSettingType parent, String name, int defaultV, int min, int max)
	{
		super(parent, name);
		this.defValue = defaultV;
		this.min = min;
		this.max = max;
	}

	public int getMin()
	{
		return min;
	}

	public int getMax()
	{
		return max;
	}

	public int getStep() {return step;}

	public OptionInt setStep(int step)
	{
		this.step = step;
		return this;
	}

	public GameSettingProperty<Number> getDefaultValue(GameSetting gameSetting)
	{
		return new PropertyInt(gameSetting, this.getName(), defValue, this);
	}

	@Override
	public Number deserialize(GameSetting gameSetting, String s)
	{
		return Integer.valueOf(s);
	}

	public static class PropertyInt extends SimpleIntegerProperty implements GameSettingProperty<Number>
	{
		private OptionInt option;

		public PropertyInt(GameSetting bean, String name, int initialValue, OptionInt optionInt)
		{
			super(bean, name, initialValue);
			this.option = optionInt;
		}

		@Override
		public void set(int newValue)
		{
			int max = option.getMax();
			int min = option.getMin();
			super.set(newValue < min ? min : newValue > max ? max : newValue);
		}

		@Override
		public GameSetting getBean()
		{
			return (GameSetting) super.getBean();
		}

		@Override
		public GameSettingType.Option<Number> getOption() {return option;}
	}

}
