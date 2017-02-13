package net.wheatlauncher.utils;

import java.util.StringJoiner;

/**
 * @author ci010
 */
public class State
{
	private Values state;
	private String cause;

	public static State of(Values state, String... cause)
	{
		if (cause != null && cause.length > 0)
		{
			StringJoiner stringJoiner = new StringJoiner(".");
			for (String s : cause)
				stringJoiner.add(s);
			return new State(state, stringJoiner.toString());
		}
		return new State(state, "");
	}

	private State(Values state, String cause)
	{
		this.state = state;
		this.cause = cause;
	}

	public Values getState() {return state;}

	public String getCause() {return cause;}

	/**
	 * @author ci010
	 */
	public static enum Values
	{
		FAIL, PENDING, PASS;

		public boolean isPass() {return this == PASS;}

		public Values and(Values state)
		{
			return this.ordinal() < state.ordinal() ? this : state;
		}
	}
}
