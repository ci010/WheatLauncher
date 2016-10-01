package net.launcher.setting;

import net.launcher.io.SourceObject;

/**
 * @author ci010
 */
public class IntOption extends Option<Integer>
{
	protected int min, max;

	public IntOption(SourceObject.Prototype src, String name, int defaultV, int min, int max)
	{
		super(src, name, defaultV, i -> i < min ? min : i > max ? max : i);
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

	public int getDefaultValue()
	{
		return super.defaultValue() == null ? 0 : defaultValue();
	}

	@Override
	public Integer deserialize(String s)
	{
		return Integer.valueOf(s);
	}

	public static class Step extends IntOption
	{
		private int step = -1;
		private boolean isBound;

		public Step(SourceObject.Prototype src, String name, int defaultV, int min, int max, int step)
		{
			super(src, name, defaultV, min, max);
			this.step = step;
		}

		public Step setBound(boolean isBound)
		{
			this.isBound = isBound;
			return this;
		}

		public boolean isBound()
		{
			return isBound;
		}

		public int getStep()
		{
			return step;
		}
	}
}
