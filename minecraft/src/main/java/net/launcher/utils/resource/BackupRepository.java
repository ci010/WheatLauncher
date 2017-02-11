package net.launcher.utils.resource;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class BackupRepository implements Repository<Void>
{
	private Path repoRoot;
	private ExecutorService service;
	private Set<String> pathCache = new TreeSet<>();

	BackupRepository(Path repoRoot, ExecutorService service)
	{
		this.repoRoot = repoRoot;
		this.service = service;
		try {Files.createDirectories(repoRoot);}
		catch (IOException ignored) {}
	}

	@Override
	public Future<Boolean> containResource(String path, Callback<Boolean> callback)
	{
		Objects.requireNonNull(path);

		return service.submit(() -> Files.exists(repoRoot.resolve(path)));
	}

	@Override
	public Set<String> getAllVisiblePaths()
	{
		return pathCache;
	}

	@Override
	public void check(Path directory, Consumer<Throwable> handler)
	{
		Objects.requireNonNull(directory);
		if (!Files.exists(directory)) if (handler != null) handler.accept(new FileNotFoundException());
		else
			service.submit(() ->
			{
				try
				{
					Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							FileVisitResult fileVisitResult = super.visitFile(file, attrs);
							if (fileVisitResult == FileVisitResult.CONTINUE)
							{
								Path relative = directory.relativize(file);
								Path target = repoRoot.resolve(relative);
								if (!Files.exists(target))
								{
									Files.createDirectories(target.getParent());
									Files.copy(file, target);
									pathCache.add(relative.toString());
								}
								else
								{
									FileTime lastModifiedTime = Files.getLastModifiedTime(file);
									FileTime fileTime = Files.getLastModifiedTime(target);
									if (lastModifiedTime.compareTo(fileTime) > 0)
										Files.copy(file, target);
								}
							}
							return fileVisitResult;
						}
					});
				}
				catch (IOException e)
				{
					if (handler != null)
						handler.accept(e);
				}
			});
	}

	@Override
	public Delivery<Void> fetchResource(Path directory, String path, FetchOption option)
	{
		Objects.requireNonNull(directory);
		Objects.requireNonNull(path);

		Path target = directory.resolve(path);
		return new DeliveryImpl<>(service.submit(() ->
		{
			Path src = repoRoot.resolve(path);
			FetchUtils.fetch(src, target, option);
			pathCache.add(path);
			return null;
		}), Collections.singleton(target), service);
	}

	@Override
	public Delivery<Void> fetchAllResources(Path directory, Collection<String> paths, FetchOption option)
	{
		return null;
	}

	@Override
	public Delivery<Void> fetchAllResources(Path directory, FetchOption option)
	{
		Objects.requireNonNull(directory);

		Set<Path> collect = pathCache.stream().map(s -> repoRoot.resolve(s)).collect(Collectors.toSet());
		return new DeliveryImpl<>(service.submit(() ->
		{
			for (Path path : collect)
				FetchUtils.fetch(path, directory.resolve(repoRoot.relativize(path)), option);
			return null;
		}), collect, service);
	}

	@Override
	public Future<Void> update()
	{
		return service.submit(() ->
		{
			Files.walkFileTree(this.repoRoot, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					FileVisitResult result = super.visitFile(file, attrs);
					if (result == FileVisitResult.CONTINUE) pathCache.add(repoRoot.relativize(file).toString());
					return result;
				}
			});
			return null;
		});
	}
}
