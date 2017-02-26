package net.launcher.game.text.event;

import net.launcher.game.text.TextComponent;

/**
 * @author ci010
 */
public class HoverEvent extends EventBase<TextComponent>
{
	public HoverEvent(TextComponent value, Action action)
	{
		super(value, action);
	}
}
