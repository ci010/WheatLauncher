package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.setting.*;
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
		SettingMinecraft minecraft = ARML.core().getProfileSettingManager().getSettingMinecraft();
		Optional<Setting> optional = selecting.getGameSetting(minecraft);
		Setting setting;
		if (!optional.isPresent())
			selecting.addGameSetting(setting = minecraft.defaultInstance());// force the minecraft// setting exist
		else setting = optional.get();

		setup(maxFPS, minecraft.getMaxFPS());
		setup(renderDistance, minecraft.getRenderDistance());
		setup(entityShadow, minecraft.getEntityShadows());
		setup(renderCloud, minecraft.getRenderClouds());
		setup(enableFBO, minecraft.getFboEnable());
		setup(enableVBO, minecraft.getUseVbo());
		setup(graphic, minecraft.getGraphic());
		setup(mipmap, minecraft.getMipmapLevels(), setting);
		setup(particle, minecraft.getParticles(), setting);
		setup(ambientOcclusion, minecraft.getAmbientOcclusion(), setting);
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
		SettingMinecraft minecraft = ARML.core().getProfileSettingManager().getSettingMinecraft();
		Optional<Setting> optional = profile.getGameSetting(minecraft);
		Setting setting;
		if (!optional.isPresent())
			profile.addGameSetting(setting = minecraft.defaultInstance());// force the minecraft// setting exist
		else setting = optional.get();
		return setting;
	}

	public void createMinecraftGameSetting(ActionEvent event)
	{
//		Bootstrap.core().getProfileManager().selecting().addGameSetting(new Setting(SettingMinecraft
//				.INST));
	}
}

