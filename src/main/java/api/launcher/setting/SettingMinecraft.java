package api.launcher.setting;

/**
 * @author ci010
 */
public abstract class SettingMinecraft extends SettingType
{
	public abstract OptionInt getFOV();

	public abstract OptionInt getMaxFPS();

	public abstract OptionInt getRenderDistance();

	public abstract OptionInt getMipmapLevels();

	public abstract OptionInt getParticles();

	public abstract OptionInt getAmbientOcclusion();

	public abstract SettingType.Option<Boolean> getAnaglyph3D();

	public abstract SettingType.Option<Boolean> getEnableVsync();

	public abstract SettingType.Option<Boolean> getFboEnable();

	public abstract SettingType.Option<Boolean> getRenderClouds();

	public abstract SettingType.Option<Boolean> getUseVbo();

	public abstract SettingType.Option<Boolean> getGraphic();

	public abstract SettingType.Option<Boolean> getEntityShadows();

	public abstract SettingType.Option<Boolean> getForceUnicode();

	public abstract SettingType.Option<String> getLanguage();

	public abstract SettingType.Option<String[]> getResourcePace();
}
