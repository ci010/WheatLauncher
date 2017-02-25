package net.launcher.assets;

import api.launcher.MinecraftWorldManager;
import api.launcher.io.IOGuard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.Image;
import net.launcher.game.WorldInfo;
import net.launcher.game.nbt.NBT;
import net.launcher.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardMinecraftWorldManager extends IOGuard<MinecraftWorldManager> implements MinecraftWorldManager
{
	private ObservableList<WorldInfo> worldInfos = FXCollections.observableArrayList();

	@Override
	protected void forceSave() throws IOException {}

	@Override
	public MinecraftWorldManager loadInstance() throws IOException
	{
		refresh().run();
		return this;
	}

	@Override
	public MinecraftWorldManager defaultInstance()
	{
		return this;
	}

	@Override
	protected void deploy() throws IOException {}

	@Override
	public ObservableList<WorldInfo> getWorldInfos()
	{
		return worldInfos;
	}

	@Override
	public Image getWorldIcon(WorldInfo worldInfo) throws IOException
	{
		return WorldInfo.getIcon(worldInfo, getContext().getRoot().resolve("saves"));
	}

	@Override
	public Task<List<WorldInfo>> refresh()
	{
		Task<List<WorldInfo>> saves = new Task<List<WorldInfo>>()
		{
			@Override
			protected List<WorldInfo> call() throws Exception
			{
				List<WorldInfo> infos = new ArrayList<>();
				for (Path save : Files.walk(getContext().getRoot().resolve("saves"), 2)
						.filter(path -> path.getFileName().toString().equals("level.dat")).collect(Collectors.toList()))
					infos.add(WorldInfo.deserialize(save));
				return infos;
			}
		};
		saves.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
		{
			Worker<List<WorldInfo>> source = event.getSource();
			worldInfos.setAll(source.getValue());
		});
		return saves;
	}

	@Override
	public Task<Void> saveMap(WorldInfo worldInfo)
	{
		return new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				Path saves = getContext().getRoot().resolve("saves").resolve(worldInfo.getFileName()).resolve("level.dat");
				if (Files.exists(saves))
					WorldInfo.WRITER.writeTo(worldInfo, NBT.read(saves, true).asCompound());
				else throw new IOException();
				return null;
			}
		};
	}

	@Override
	public Task<WorldInfo> importMap(Path path)
	{
		return new Task<WorldInfo>()
		{
			@Override
			protected WorldInfo call() throws Exception
			{
				Path resolve = path.resolve("level.dat");
				if (!Files.exists(resolve)) throw new IOException();
				WorldInfo deserialize = WorldInfo.deserialize(resolve);
				worldInfos.add(deserialize);
				return deserialize;
			}
		};
	}

	@Override
	public Task<Path> exportMap(WorldInfo worldInfo, Path target)
	{
		return new Task<Path>()
		{
			@Override
			protected Path call() throws Exception
			{
				Path saves = getContext().getRoot().resolve("saves").resolve(worldInfo.getFileName());
				DirUtils.CopyVisitor copyVisitor = new DirUtils.CopyVisitor(saves, target, null);
				Files.walkFileTree(saves, copyVisitor);
				return target;
			}
		};
	}
}
