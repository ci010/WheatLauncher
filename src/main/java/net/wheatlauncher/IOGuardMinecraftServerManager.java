package net.wheatlauncher;

import api.launcher.MinecraftServerManager;
import api.launcher.io.IOGuard;
import javafx.collections.FXCollections;
import net.launcher.MinecraftServerManagerImpl;
import net.launcher.game.ServerInfo;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardMinecraftServerManager extends IOGuard<MinecraftServerManager>
{
	@Override
	protected void forceSave() throws IOException
	{

	}

	@Override
	public MinecraftServerManager loadInstance() throws IOException
	{
		Path resolve = getContext().getRoot().resolve("servers.dat");
		NBTCompound read = NBT.read(resolve, false).asCompound();
		if (read.isEmpty()) return defaultInstance();
		BiSerializer<ServerInfo, NBTCompound> serializer = ServerInfo.serializer();
		return new MinecraftServerManagerImpl(FXCollections.observableArrayList(read.get("servers").asList().stream().map(NBT::asCompound).map(serializer::deserialize).collect(Collectors
				.toList())));
	}

	@Override
	public MinecraftServerManager defaultInstance()
	{
		return new MinecraftServerManagerImpl();
	}

	@Override
	protected void deploy() throws IOException
	{

	}
}
