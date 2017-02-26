package net.launcher.game.text.event;

/**
 * @author ci010
 */
public interface Event<T>
{
	Action getAction();

	T getValue();
}
