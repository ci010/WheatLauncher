package net.launcher.setting;

/**
 * @author ci010
 */
public class IntOption extends GameSetting.Option<Integer>
{
	private int min, max;
	private int defValue;

	public IntOption(GameSetting parent, String name, int defaultV, int min, int max)
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

	@Override
	public Integer valid(Integer i)
	{
		return i < min ? min : i > max ? max : i;
	}

	public Integer getDefaultValue()
	{
		return defValue;
	}

	@Override
	public Integer deserialize(String s)
	{
		return Integer.valueOf(s);
	}

}
