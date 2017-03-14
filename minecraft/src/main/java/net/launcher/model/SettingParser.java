package net.launcher.model;

import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.IOException;

/**
 * @author ci010
 */
public interface SettingParser
{
	Setting parse(MinecraftDirectory minecraftDirectory) throws IOException;

	void save(MinecraftDirectory minecraftDirectory, Setting setting) throws IOException;
}
