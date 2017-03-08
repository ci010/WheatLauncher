package api.launcher.setting;

import java.util.List;
import java.util.Optional;

/**
 * @author ci010
 */
public interface SettingManager
{
	SettingMinecraft getSettingMinecraft();

	SettingMods getSettingMods();

	<T extends SettingType> Optional<T> find(Class<T> clz);

	List<SettingType> getAllSettingType();
}
