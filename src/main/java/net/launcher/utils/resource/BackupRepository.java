package net.launcher.utils.resource;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author ci010
 */
public class BackupRepository
{
	private String path;
	private File repoRoot;

	public BackupRepository(File parent, String path)
	{
		this.path = path;
		this.repoRoot = new File(parent, path);
		this.repoRoot.mkdirs();
	}

	public File getRoot()
	{
		return repoRoot;
	}

	public boolean contains(String path)
	{
		return getPath(path).isFile();
	}

	public File getPath(String path)
	{
		return new File(repoRoot, path);
	}

	public void bind(ObservableValue<MinecraftDirectory> fileObservableValue)
	{
		fileObservableValue.addListener(dirListener);
	}

	public void unbind(ObservableValue<MinecraftDirectory> fileObservableValue)
	{
		fileObservableValue.removeListener(dirListener);
	}

	public void backup(File directory)
	{
		File root = new File(directory, path);
		if (!root.exists()) return;
		try
		{
			Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					Path relativized = root.toPath().relativize(file);
					Path back = repoRoot.toPath().resolve(relativized);
					if (!back.toFile().isFile())
					{
						back.toFile().getParentFile().mkdirs();
						Files.copy(file, back);
					}
					return super.visitFile(file, attrs);
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private ChangeListener<MinecraftDirectory> dirListener = (observable, oldValue, newValue) -> backup(newValue.getRoot());
}
