package net.launcher.mod;

import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingFactory;
import net.launcher.setting.GameSettingInstance;
import net.launcher.setting.StringArrayOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
@GameSetting.ID("mod")
public class GameSettingMod extends GameSetting
{
	public static final GameSettingMod INSTANCE;

	static
	{
		GameSettingFactory.register(GameSettingMod.class);
		INSTANCE = (GameSettingMod) GameSettingFactory.find("mod").get();
	}

	public final Option<String[]> MODS = new StringArrayOption(this, "mods");

	@Override
	public List<Option<?>> getAllOption()
	{
		return Collections.singletonList(MODS);
	}

	@Override
	public GameSettingInstance load(MinecraftDirectory directory) throws IOException
	{
		File file = new File(directory.getRoot(), "mods.dat");
		NBT read = NBT.read(file, true);
		if (read == null) return null;
		NBTCompound nbt = read.asCompound();
		String[] mods = (String[]) nbt.get("mods").asList().toArray();
		GameSettingInstance instance = new GameSettingInstance(this);
		instance.setOption(MODS, mods);
		return instance;
	}

	@Override
	public void save(MinecraftDirectory directory, GameSettingInstance setting) throws IOException
	{
		Path path = directory.getRoot().toPath().resolve("mods.dat");
		String[] option = setting.getOption(MODS);
		if (option != null)
			NBT.write(path, NBT.compound().put("mods", NBT.list(option)), true);
	}

	@Override
	public void saveTemplate(Path templateRoot, GameSettingInstance.Template instance) throws IOException
	{

	}

	@Override
	public GameSettingInstance.Template loadTemplate(Path templateRoot, String template) throws IOException
	{
		return null;
	}
}