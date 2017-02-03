package net.wheatlauncher.control.profiles;

import com.jfoenix.effects.JFXDepthManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.control.MinecraftOptionButton;
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

		setup(entityShadow, GameSettingMinecraft.INSTANCE.ENTITY_SHADOWS);
		setup(renderCloud, GameSettingMinecraft.INSTANCE.RENDER_CLOUDS);
		setup(enableFBO, GameSettingMinecraft.INSTANCE.FBO_ENABLE);
		setup(enableVBO, GameSettingMinecraft.INSTANCE.USE_VBO);
		setup(graphic, GameSettingMinecraft.INSTANCE.GRAPHIC);
		setup(mipmap, GameSettingMinecraft.INSTANCE.MIPMAP_LEVELS);
		setup(particle, GameSettingMinecraft.INSTANCE.PARTICLES);
		setup(ambientOcclusion, GameSettingMinecraft.INSTANCE.AMBIENT_OCCLUSION);

//		((Pane) maxFPSBtn.getParent()).getChildren().remove(maxFPS);
//		maxFPSBtn.getParent().hoverProperty().addListener(observable ->
//		{
//			if (maxFPSBtn.getParent().isHover())
//			{
//				maxFPSBtn.setDisable(true);
//				((Pane) maxFPSBtn.getParent()).getChildren().add(maxFPS);
//			}
//			else
//			{
//				maxFPSBtn.setDisable(false);
//				((Pane) maxFPSBtn.getParent()).getChildren().remove(maxFPS);
//			}
//		});
//
//		((Pane) renderDistanceBtn.getParent()).getChildren().remove(renderDistance);
//		renderDistanceBtn.getParent().hoverProperty().addListener(observable ->
//		{
//			if (renderDistanceBtn.getParent().isHover())
//			{
//				renderDistanceBtn.setDisable(true);
//				((Pane) renderDistanceBtn.getParent()).getChildren().add(renderDistance);
//			}
//			else
//			{
//				renderDistanceBtn.setDisable(false);
//				((Pane) renderDistanceBtn.getParent()).getChildren().remove(renderDistance);
//			}
//		});
	}

	private List<String> boolOptions = Arrays.asList("true", "false");

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

