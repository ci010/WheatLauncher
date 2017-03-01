package net.launcher.setting;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author ci010
 */
public class OptionBoolean extends SettingType.Option<Boolean>
{
	private boolean defValue;

	public OptionBoolean(SettingType parent, String name, boolean defValue)
	{
		super(parent, name);
		this.defValue = defValue;
	}

	@Override
	public Boolean deserialize(String s)
	{
		return Boolean.valueOf(s);
	}

	@Override
	public SettingProperty<Boolean> getDefaultValue(Setting setting)
	{
		return new PropertyBool(setting, this.getName(), defValue, this);
	}

	public static class PropertyBool extends SimpleBooleanProperty implements SettingProperty.Limited<Boolean>
	{
		private SettingType.Option<Boolean> booleanOption;

		public PropertyBool(Setting bean, String name, boolean initialValue, SettingType.Option<Boolean> booleanOption)
		{
			super(bean, name, initialValue);
			this.booleanOption = booleanOption;
		}

		@Override
		public Setting getBean() {return (Setting) super.getBean();}

		@Override
		public SettingType.Option<Boolean> getOption()
		{
			return booleanOption;
		}

		@Override
		public boolean hasNext()
		{
			return true;
		}

		@Override
		public Boolean next()
		{
			boolean next = !get();
			set(next);
			return next;
		}
	}
}
