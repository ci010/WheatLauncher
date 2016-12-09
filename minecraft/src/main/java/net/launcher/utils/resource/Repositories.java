package net.launcher.utils.resource;


import javafx.util.Builder;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;

import java.net.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author ci010
 */
public class Repositories
{
	public static Repository<Void> newBackupRepository(Path root, ExecutorService service)
	{
		return new BackupRepository(root, service);
	}

	public static <T> ArchiveRepositoryBuilder<T> newArchiveRepositoryBuilder
			(Path path, ExecutorService service, BiSerializer<T, NBTCompound> archiveSerializer, Deserializer<T, Path> parser)
	{
		return new ArchiveRepositoryBuilder<>(path, service, archiveSerializer, parser);
	}

	private Repositories() {}


	public static class ArchiveRepositoryBuilder<T> implements Builder<ArchiveRepository<T>>
	{
		private List<ArchiveRepository.Remote> remotePorts = new ArrayList<>();
		private Path path;
		private ExecutorService service;
		private BiSerializer<T, NBTCompound> archiveSerializer;
		private Proxy proxy;
		private Deserializer<T, Path> parser;

		private ArchiveRepositoryBuilder(Path path, ExecutorService service,
										 BiSerializer<T, NBTCompound> archiveSerializer,
										 Deserializer<T, Path> parser)
		{
			this.path = path;
			this.service = service;
			this.archiveSerializer = archiveSerializer;
			this.parser = parser;
		}

		public ArchiveRepositoryBuilder<T> registerRemote(ArchiveRepository.Remote remote)
		{
			remotePorts.add(remote);
			return this;
		}

		public ArchiveRepositoryBuilder<T> setProxy(Proxy proxy)
		{
			this.proxy = proxy;
			return this;
		}

		@Override
		public ArchiveRepository<T> build()
		{
			ArchiveRepositoryBase<T> repositoryBase = new ArchiveRepositoryBase<>(path, service,
					remotePorts.toArray(new ArchiveRepository.Remote[remotePorts.size()]),
					parser, archiveSerializer);
			repositoryBase.setProxy(proxy == null ? Proxy.NO_PROXY : proxy);
			return repositoryBase;
		}
	}
}
