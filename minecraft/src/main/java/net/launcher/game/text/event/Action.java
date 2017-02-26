package net.launcher.game.text.event;

/**
 * @author ci010
 */
public interface Action
{
	String getCanonicalName();

	boolean shouldAllowInChat();
}
