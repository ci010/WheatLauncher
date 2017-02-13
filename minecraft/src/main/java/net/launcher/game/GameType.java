package net.launcher.game;

/**
 * @author ci010
 */
public enum GameType
{
	NON,
	SURVIVAL,
	CREATIVE,
	ADVENTURE,
	SPECTATOR;

	public int getId() {return this.ordinal() - 1;}

	public static GameType getByID(int id)
	{
		return values()[id + 1];
	}
}
