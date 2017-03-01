package net.launcher.setting;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author ci010
 */
public class OptionInt extends SettingType.Option<Number>
{
	private int min, max;
	private int defValue;
	private int step = 1;

	public OptionInt(SettingType parent, String name, int defaultV, int min, int max)
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

	public SettingProperty<Number> getDefaultValue(Setting setting)
	{
		return new PropertyInt(setting, this.getName(), defValue, this);
	}

	@Override
	public Number deserialize(String s)
	{
		return Integer.valueOf(s);
	}

	public static class PropertyInt extends SimpleIntegerProperty implements SettingProperty.Limited<Number>
	{
		private OptionInt option;

		public PropertyInt(Setting bean, String name, int initialValue, OptionInt optionInt)
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
		public Setting getBean()
		{
			return (Setting) super.getBean();
		}

		@Override
		public SettingType.Option<Number> getOption() {return option;}

		@Override
		public boolean hasNext()
		{
			return true;
		}

		@Override
		public Number next()
		{
			int next = get() + option.getStep();
			if (next > option.getMax())
				next = option.getMin();
			this.set(next);
			return next;
		}
	}

}
