package net.launcher.utils.resource;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class BackupRepository implements Repository.LocalRepository<Void>
{
	private Path repoRoot;
	private Set<String> pathCache = new HashSet<>();

	public BackupRepository(Path root)
	{
		this.repoRoot = root;
		try {Files.createDirectories(repoRoot);}
		catch (IOException ignored) {}
	}

	@Override
	public Future<Boolean> containResource(String path, Callback<Boolean> callback)
	{
		try
		{
			callback.done(Files.exists(repoRoot.resolve(path)));
			pathCache.add(path);
		}
		catch (Throwable e)
		{
			callback.failed(e);
		}
	}

	@Override
	public Set<String> getAllVisiblePaths()
	{
		return pathCache;
	}

	@Override
	public void check(Path directory, Consumer<Throwable> handler)
	{
		if (!Files.exists(directory)) handler.accept(new FileNotFoundException());
		else
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
							Path relativized = directory.relativize(file);
							Path target = repoRoot.resolve(relativized);
							if (!Files.exists(target))
							{
								Files.createDirectories(target.getParent());
								Files.copy(file, target);
								pathCache.add(relativized.toString());
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
	}

	@Override
	public Delivery fetchResource(Path directory, String path, Callback<Void> callback, FetchOption option)
	{
		Path target = directory.resolve(path);
		Path src = repoRoot.resolve(path);
		if (!Files.exists(src))
			callback.failed(new FileNotFoundException("cannot find file " + path + " in this repository."));
		else
			try
			{
				pathCache.add(path);
				FetchUtils.fetch(src, target, option);
				callback.done(null);
			}
			catch (IOException e)
			{
				callback.failed(e);
			}
		return null;
	}

	@Override
	public Delivery fetchAllResources(Path directory, Callback<Void> callback, FetchOption option)
	{
		try
		{
			Files.walkFileTree(this.repoRoot, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					FileVisitResult fileVisitResult = super.visitFile(file, attrs);
					if (fileVisitResult == FileVisitResult.CONTINUE)
					{
						Path relativize = repoRoot.relativize(file);
						FetchUtils.fetch(file, directory.resolve(relativize), option);
						pathCache.add(relativize.toString());
					}
					return fileVisitResult;
				}
			});
		}
		catch (IOException e)
		{
			callback.failed(e);
		}
		callback.done(null);
		return null;
	}

	@Override
	public Future<Void> update()
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
	}
}
