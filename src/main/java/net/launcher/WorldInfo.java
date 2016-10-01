package net.launcher;

import net.launcher.utils.nbt.NBT;
import net.launcher.utils.nbt.NBTCompound;
import net.launcher.utils.serial.Deserializer;

/**
 * @author ci010
 */
public class WorldInfo
{
	private String fileName, displayName;
	private long size, lastPlayed;
	private GameType gameType;
	private boolean isHardCore, enabledCheat;

	private int spawnX, spawnY, spawnZ;

	public WorldInfo(String fileName, String displayName, long size, long lastPlayed, GameType gameType, boolean isHardCore, boolean enabledCheat, int spawnX, int spawnY, int spawnZ)
	{
		this.fileName = fileName;
		this.displayName = displayName;
		this.size = size;
		this.lastPlayed = lastPlayed;
		this.gameType = gameType;
		this.isHardCore = isHardCore;
		this.enabledCheat = enabledCheat;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
	}

	public int getSpawnX()
	{
		return spawnX;
	}

	public int getSpawnY()
	{
		return spawnY;
	}

	public int getSpawnZ()
	{
		return spawnZ;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public long getSize()
	{
		return size;
	}

	public long getLastPlayed()
	{
		return lastPlayed;
	}

	public GameType getGameType()
	{
		return gameType;
	}

	public boolean isHardCore()
	{
		return isHardCore;
	}

	public boolean isEnabledCheat()
	{
		return enabledCheat;
	}

	public static Deserializer<WorldInfo, NBTCompound> SERIALIZER = (compound, context) ->
	{
		long lastPlayed1 = compound.get("LastPlayed").getAsPrimitive().getAsLong();
		long sizeOnDisk = compound.get("SizeOnDisk").getAsPrimitive().getAsLong();
		String levelName = compound.get("LevelName").getAsPrimitive().getAsString();
		boolean hardcore = compound.get("hardcore").getAsPrimitive().getAsBoolean();
		GameType gameType1 = GameType.getByID(compound.get("GameType").getAsPrimitive().getAsInt());
		boolean allowCommands = compound.option("allowCommands").orElse(NBT.bool(gameType1 == GameType.CREATIVE)).getAsPrimitive().getAsBoolean();
		int spawnX1 = compound.get("SpawnX").getAsPrimitive().getAsInt();
		int spawnY1 = compound.get("SpawnY").getAsPrimitive().getAsInt();
		int spawnZ1 = compound.get("SpawnZ").getAsPrimitive().getAsInt();
		String fileName1 = (String) context.get("fileName");
		return new WorldInfo(fileName1, levelName, sizeOnDisk, lastPlayed1, gameType1, hardcore, allowCommands, spawnX1,
				spawnY1, spawnZ1);
	};
}
