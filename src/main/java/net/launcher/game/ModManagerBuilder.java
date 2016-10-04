package net.launcher.game;

import net.wheatlauncher.utils.DirUtils;
import org.to2mbn.jmccc.util.Builder;

import java.io.File;

/**
 * @author ci010
 */
public class ModManagerBuilder implements Builder<ModManager>
{
	public static ModManagerBuilder create(File root) {return new ModManagerBuilder();}

	public static ModManager buildDefault() {return create(DirUtils.getAvailableWorkDir()).build();}

	private File root;

	public ModManagerBuilder setRoot(File file)
	{
		this.root = file;
		return this;
	}

	@Override
	public ModManager build()
	{
		return new ModMangerImpl(root);
	}

	private ModManagerBuilder() {}
}
