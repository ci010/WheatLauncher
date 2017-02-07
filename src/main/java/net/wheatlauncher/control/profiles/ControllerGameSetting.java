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
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import net.launcher.setting.GameSettingMinecraft;
import net.launcher.setting.IntOption;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

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

	@PostConstruct
	public void init()
	{
		JFXDepthManager.setDepth(missingFileIndicatorText, 3);
		missingFileIndicator.setVisible(false);
//		container.disableProperty().bind(Bindings.createBooleanBinding(
//				() ->
//				{
//					LaunchProfile profile = Bootstrap.getCore().getProfileManager().selecting();
//					return !profile.getGameSetting(GameSettingMinecraft.INSTANCE).isPresent();
//				}
//				, Bindings.createObjectBinding(() -> Bootstrap.getCore().getProfileManager().selecting().versionProperty(),
//						Bootstrap.getCore().getProfileManager().selectedProfileProperty())));
//		missingFileIndicator.visibleProperty().bind(Bindings.createBooleanBinding(() -> container.isDisabled(),
//				container.disabledProperty()));
//		missingFileIndicator.disableProperty().bind(Bindings.createBooleanBinding(() -> !container.isDisabled(),
//				container.disabledProperty()));

		setup(maxFPS, GameSettingMinecraft.INSTANCE.MAXFPS);
		setup(renderDistance, GameSettingMinecraft.INSTANCE.RENDER_DISTANCE);
		setup(entityShadow, GameSettingMinecraft.INSTANCE.ENTITY_SHADOWS);
		setup(renderCloud, GameSettingMinecraft.INSTANCE.RENDER_CLOUDS);
		setup(enableFBO, GameSettingMinecraft.INSTANCE.FBO_ENABLE);
		setup(enableVBO, GameSettingMinecraft.INSTANCE.USE_VBO);
		setup(graphic, GameSettingMinecraft.INSTANCE.GRAPHIC);
		setup(mipmap, GameSettingMinecraft.INSTANCE.MIPMAP_LEVELS);
		setup(particle, GameSettingMinecraft.INSTANCE.PARTICLES);
		setup(ambientOcclusion, GameSettingMinecraft.INSTANCE.AMBIENT_OCCLUSION);

//		Bootstrap.getCore().getProfileManager().selecting().setMemory(memory.memoryProperty().get());
	}

	private List<String> boolOptions = Arrays.asList("true", "false");

	private void setup(MinecraftSlider slider, IntOption option)
	{
		JFXSlider s = slider.getSlider();
		s.setMin(option.getMin());
		s.setMax(option.getMax());
		s.setMajorTickUnit(option.getStep());
		s.setSnapToTicks(true);
	}

	private void setup(MinecraftOptionButton button, GameSetting.Option<Boolean> option)
	{
		button.setOptions(boolOptions);
		button.valueProperty().addListener((observable, oldValue, newValue) ->
				Bootstrap.getCore().getProfileManager().selecting().getGameSetting(GameSettingMinecraft.INSTANCE)
						.ifPresent(gameSettingInstance -> gameSettingInstance.setOption(option, Boolean.valueOf(newValue))));
	}

	private void setup(MinecraftOptionButton button, IntOption option)
	{
		String[] arr = new String[option.getMax() - option.getMin() + 1];
		for (int i = 0; i < arr.length; i++)
			arr[i] = String.valueOf((option.getMin() + i));
		button.setOptions(Arrays.asList(arr));
		button.valueProperty().addListener((observable, oldValue, newValue) ->
				Bootstrap.getCore().getProfileManager().selecting().getGameSetting(GameSettingMinecraft.INSTANCE)
						.ifPresent(gameSettingInstance -> gameSettingInstance.setOption(option, Integer.valueOf(newValue))));
	}

	public void createMinecraftGameSetting(ActionEvent event)
	{
		Bootstrap.getCore().getProfileManager().selecting().addGameSetting(new GameSettingInstance(GameSettingMinecraft
				.INSTANCE));
	}
}

