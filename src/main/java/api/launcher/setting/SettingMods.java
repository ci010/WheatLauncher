package api.launcher.setting;

import net.launcher.game.ModManifest;

/**
 * @author ci010
 */
public abstract class SettingMods extends SettingType
{
	public abstract Option<ModManifest> getMods();
}
