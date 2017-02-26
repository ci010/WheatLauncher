package net.launcher.game.text.event;

/**
 * @author ci010
 */
public class EventBase<T> implements Event<T>
{
	private T value;
	private Action action;

	public EventBase(T value, Action action)
	{
		this.value = value;
		this.action = action;
	}

	@Override
	public T getValue() {return value;}

	@Override
	public Action getAction() {return action;}
}
