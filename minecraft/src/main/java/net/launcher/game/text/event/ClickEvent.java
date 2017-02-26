package net.launcher.game.text.event;


/**
 * @author ci010
 */
public class ClickEvent extends EventBase<String> implements Event<String>
{
	public ClickEvent(String value, Action action)
	{
		super(value, action);
	}
}
