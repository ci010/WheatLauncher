package net.wheatlauncher.utils;

/**
 * @author ci010
 */
public class ValidatorAuth extends ValidatorInContext
{
	private String type;
	private String onlineType;

	public ValidatorAuth(String type)
	{
		this.type = type;
	}

	public void setOnlineType(String t)
	{
		onlineType = t;
	}

	@Override
	protected String getContext()
	{
		return onlineType + "." + type;
	}
}
