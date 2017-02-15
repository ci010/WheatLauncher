package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.effects.JFXDepthManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.control.MinecraftOptionButton;
import net.launcher.control.MinecraftOptionMemory;
import net.launcher.control.MinecraftOptionResolution;
import net.launcher.control.MinecraftSlider;
import net.launcher.setting.OptionInt;
import net.launcher.setting.SettingMinecraft;
import net.launcher.setting.SettingType;

import java.util.Arrays;
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

		setup(maxFPS, SettingMinecraft.INSTANCE.MAXFPS);
		setup(renderDistance, SettingMinecraft.INSTANCE.RENDER_DISTANCE);
		setup(entityShadow, SettingMinecraft.INSTANCE.ENTITY_SHADOWS);
		setup(renderCloud, SettingMinecraft.INSTANCE.RENDER_CLOUDS);
		setup(enableFBO, SettingMinecraft.INSTANCE.FBO_ENABLE);
		setup(enableVBO, SettingMinecraft.INSTANCE.USE_VBO);
		setup(graphic, SettingMinecraft.INSTANCE.GRAPHIC);
		setup(mipmap, SettingMinecraft.INSTANCE.MIPMAP_LEVELS);
		setup(particle, SettingMinecraft.INSTANCE.PARTICLES);
		setup(ambientOcclusion, SettingMinecraft.INSTANCE.AMBIENT_OCCLUSION);

//		Bootstrap.getCore().getProfileManager().selecting().setMemory(memory.memoryProperty().get());
	}

	private void setup(MinecraftSlider slider, OptionInt option)
	{
		JFXSlider s = slider.getSlider();
		s.setMin(option.getMin());
		s.setMax(option.getMax());
		s.setMajorTickUnit(option.getStep());
		s.setSnapToTicks(true);
	}

	private void setup(MinecraftOptionButton button, SettingType.Option<Boolean> option)
	{
		button.setOptions(Arrays.asList(resources.getString(button.getId() + ".true"),
				resources.getString(button.getId() + ".false")));
		button.valueProperty().addListener((observable, oldValue, newValue) ->
				Bootstrap.getCore().getProfileManager().selecting().getGameSetting(SettingMinecraft.INSTANCE)
						.ifPresent(gameSettingInstance -> gameSettingInstance.getOption(option).setValue(Boolean.valueOf(newValue))));
	}

	private void setup(MinecraftOptionButton button, OptionInt option)
	{
		String[] arr = new String[option.getMax() - option.getMin() + 1];
		for (int i = 0; i < arr.length; i++)
			arr[i] = resources.getString(button.getId() + "." + String.valueOf((option.getMin() + i)));
		button.setOptions(Arrays.asList(arr));
		button.valueProperty().addListener((observable, oldValue, newValue) ->
				Bootstrap.getCore().getProfileManager().selecting().getGameSetting(SettingMinecraft.INSTANCE)
						.ifPresent(gameSettingInstance -> gameSettingInstance.getOption(option).setValue(Integer.valueOf(newValue))));
	}

	public void createMinecraftGameSetting(ActionEvent event)
	{
//		Bootstrap.getCore().getProfileManager().selecting().addGameSetting(new Setting(SettingMinecraft
//				.INSTANCE));
	}
}

