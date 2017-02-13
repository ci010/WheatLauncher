package net.launcher.mod;

import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.setting.OptionJSONArray;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingManager;
import net.launcher.setting.SettingType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class SettingMod extends SettingType
{
	public static final SettingMod INSTANCE;

	static
	{
		SettingManager.register(SettingMod.class);
		INSTANCE = (SettingMod) SettingManager.find("forge").get();
	}

	public final Option<String[]> MODS = new OptionJSONArray(this, "mods");

	@Override
	public String getID()
	{
		return "Forge";
	}

	@Override
	public List<Option<?>> getAllOption()
	{
		return Collections.singletonList(MODS);
	}

	@Override
	public Setting load(Path minecraftFolder) throws IOException
	{
		Path path = minecraftFolder.resolve("mods.dat");
		NBT read = NBT.read(path, true);
		if (read == null) return null;
		NBTCompound nbt = read.asCompound();
		String[] mods = (String[]) nbt.get("mods").asList().toArray();
//		Setting instance = new Setting(this);
//		instance.setOption(MODS, mods);
		return null;
	}

	@Override
	public Setting defaultInstance()
	{
		return null;
	}

	@Override
	public void save(Path directory, Setting setting) throws IOException
	{
		Path path = directory.resolve("mods.dat");
//		String[] option = setting.getOption(MODS);
//		if (option != null)
//			NBT.write(path, NBT.compound().put("mods", NBT.listStr(option)), true);
	}
}
