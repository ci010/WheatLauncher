package net.wheatlauncher.utils;

/**
 * @author ci010
 */
public enum DataSizeUnit
{
	KB(1000, "kb", "kilobyte"),
	MB(1000000, "mb", "megabyte"),
	GB(1000000000, "gb", "gigabyte");

	DataSizeUnit(int scaleToByte, String... unitsName)
	{
		this.scaleToByte = scaleToByte;
		this.unitsName = unitsName;
	}

	private int scaleToByte;
	private String[] unitsName;

	public long toByte(Number number)
	{
		return (long) (number.doubleValue() * scaleToByte);
	}

	public double fromByte(long bytes)
	{
		return bytes / (double) scaleToByte;
	}

	public double fromString(String s)
	{
		s = s.toLowerCase();
		String unitName = getUnitName(s);
		return Double.parseDouble(s.replace(unitName, ""));
	}

	public String getUnitName(String s)
	{
		s = s.toLowerCase();
		for (String unit : unitsName)
			if (s.endsWith(unit)) return unit;
		return "";
	}

	public static DataSizeUnit of(String s)
	{
		for (DataSizeUnit dataSizeUnit : values())
		{
			String unitName = dataSizeUnit.getUnitName(s);
			if (!unitName.equals(""))
				return dataSizeUnit;
		}
		return null;
	}
}
