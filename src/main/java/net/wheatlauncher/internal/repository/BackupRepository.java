package net.wheatlauncher.internal.repository;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.wheatlauncher.Core;
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
public class BackupRepository implements ChangeListener<MinecraftDirectory>
{
	private String path;
	private File repoRoot;

	public BackupRepository(String path)
	{
		this.path = path;
		this.repoRoot = new File(Core.INSTANCE.getBackupRoot(), path);
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

	public void backup(MinecraftDirectory directory)
	{
		File root = new File(directory.getRoot(), path);
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

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		backup(newValue);
	}
}
