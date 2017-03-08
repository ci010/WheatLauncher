package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.setting.OptionInt;
import api.launcher.setting.Setting;
import api.launcher.setting.SettingProperty;
import api.launcher.setting.SettingType;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.effects.JFXDepthManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.launcher.control.MinecraftOptionButton;
import net.launcher.control.MinecraftOptionMemory;
import net.launcher.control.MinecraftOptionResolution;
import net.launcher.control.MinecraftSlider;
import net.wheatlauncher.SettingMinecraftImpl;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class ControllerGameSetting
{
	public MinecraftOptionButton graphic;
	public MinecraftOptionButton ambientOcclusion;

	public MinecraftOptionButton mipmap;
	public MinecraftOptionButton particle;

	public MinecraftOptionButton entityShadow;
	public MinecraftOptionButton renderCloud;

	public MinecraftOptionButton enableFBO;
	public MinecraftOptionButton enableVBO;

	public MinecraftSlider maxFPS;
	public MinecraftSlider renderDistance;

	public MinecraftOptionMemory memory;
	public MinecraftOptionResolution resolution;

	public VBox container;
	public VBox missingFileIndicator;
	public Label missingFileIndicatorText;

	public ResourceBundle resources;

	public void initialize()
	{
		JFXDepthManager.setDepth(missingFileIndicatorText, 3);
		missingFileIndicator.setVisible(false);

		LaunchProfile selecting = ARML.core().getProfileManager().selecting();
		Optional<Setting> optional = selecting.getGameSetting(SettingMinecraftImpl.INSTANCE);
		Setting setting;
		if (!optional.isPresent())
			selecting.addGameSetting(setting = SettingMinecraftImpl.INSTANCE.defaultInstance());// force the minecraft// setting exist
		else setting = optional.get();

		setup(maxFPS, SettingMinecraftImpl.INSTANCE.MAXFPS);
		setup(renderDistance, SettingMinecraftImpl.INSTANCE.RENDER_DISTANCE);
		setup(entityShadow, SettingMinecraftImpl.INSTANCE.ENTITY_SHADOWS);
		setup(renderCloud, SettingMinecraftImpl.INSTANCE.RENDER_CLOUDS);
		setup(enableFBO, SettingMinecraftImpl.INSTANCE.FBO_ENABLE);
		setup(enableVBO, SettingMinecraftImpl.INSTANCE.USE_VBO);
		setup(graphic, SettingMinecraftImpl.INSTANCE.GRAPHIC);
		setup(mipmap, SettingMinecraftImpl.INSTANCE.MIPMAP_LEVELS, setting);
		setup(particle, SettingMinecraftImpl.INSTANCE.PARTICLES, setting);
		setup(ambientOcclusion, SettingMinecraftImpl.INSTANCE.AMBIENT_OCCLUSION, setting);
	}

	private void setup(MinecraftSlider slider, OptionInt option)
	{
		JFXSlider s = slider.getSlider();
		s.setMin(option.getMin());
		s.setMax(option.getMax());
		s.setMajorTickUnit(option.getStep());
		s.setSnapToTicks(true);
		slider.setUserData(resources);
		slider.setPropertyBinding(Bindings.createObjectBinding(() ->
		{
			Setting set = ensureSetting(ARML.core().getProfileManager().selecting());
			return (SettingProperty.Limited<Number>) set.getOption(option);
		}, ARML.core().getProfileManager().selectedProfileProperty()));
	}

	private void setup(MinecraftOptionButton<Boolean> button, SettingType.Option<Boolean> option)
	{
		button.setUserData(resources);
		button.setPropertyBinding(Bindings.createObjectBinding(() ->
		{
			Setting set = ensureSetting(ARML.core().getProfileManager().selecting());
			return (SettingProperty.Limited<Boolean>) set.getOption(option);
		}, ARML.core().getProfileManager().selectedProfileProperty()));
	}

	private void setup(MinecraftOptionButton<Number> button, OptionInt option, Setting setting)
	{
		button.setUserData(resources);
		button.setPropertyBinding(Bindings.createObjectBinding(() ->
		{
			Setting set = ensureSetting(ARML.core().getProfileManager().selecting());
			return (SettingProperty.Limited<Number>) set.getOption(option);
		}, ARML.core().getProfileManager().selectedProfileProperty()));
	}

	private Setting ensureSetting(LaunchProfile profile)
	{
		Optional<Setting> optional = profile.getGameSetting(SettingMinecraftImpl.INSTANCE);
		Setting setting;
		if (!optional.isPresent())
			profile.addGameSetting(setting = SettingMinecraftImpl.INSTANCE.defaultInstance());// force the minecraft// setting exist
		else setting = optional.get();
		return setting;
	}

	public void createMinecraftGameSetting(ActionEvent event)
	{
//		Bootstrap.core().getProfileManager().selecting().addGameSetting(new Setting(SettingMinecraft
//				.INST));
	}
}

