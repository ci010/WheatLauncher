package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.effects.JFXDepthManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import net.launcher.setting.GameSettingMinecraft;
import net.wheatlauncher.utils.LanguageMap;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
public class ControllerGameSetting
{
	public JFXButton graphic;
	public JFXButton ambientOcclusion;

	public JFXButton mipmap;
	public JFXButton particle;

	public JFXButton entityShadow;
	public JFXButton renderCloud;

	public JFXButton enableFBO;
	public JFXButton enableVBO;

	public JFXSlider renderDistance;
	public JFXSlider maxFPS;

	public VBox container;
	public VBox missingFileIndicator;
	public Label missingFileIndicatorText;

	@PostConstruct
	public void init()
	{
		JFXDepthManager.setDepth(missingFileIndicatorText, 3);

		container.disableProperty().bind(Bindings.createBooleanBinding(
				() ->
				{
					LaunchProfile profile = Bootstrap.getCore().getProfileManager().selecting();
					return !profile.getGameSetting(GameSettingMinecraft.INSTANCE).isPresent();
				}
				, Bindings.createObjectBinding(() -> Bootstrap.getCore().getProfileManager().selecting().versionProperty(),
						Bootstrap.getCore().getProfileManager().selectedProfileProperty())));

//		BooleanBinding missingBoolean = Bindings.createBooleanBinding(
//				() -> Bootstrap.getCore().getProfileManager().selecting().getGameSetting(GameSettingMinecraft.INSTANCE).isPresent()
//				, Bindings.createObjectBinding(() -> Bootstrap.getCore().getProfileManager().selecting().gameSettingsProperty(),
//						Bootstrap.getCore().selectedProperty()));
		missingFileIndicator.visibleProperty().bind(Bindings.createBooleanBinding(() -> container.isDisabled(),
				container.disabledProperty()));
		missingFileIndicator.disableProperty().bind(Bindings.createBooleanBinding(() -> !container.isDisabled(),
				container.disabledProperty()));


		entityShadow.setOnAction(setup(entityShadow, GameSettingMinecraft.INSTANCE.ENTITY_SHADOWS));
		renderCloud.setOnAction(setup(renderCloud, GameSettingMinecraft.INSTANCE.RENDER_CLOUDS));
		enableFBO.setOnAction(setup(enableFBO, GameSettingMinecraft.INSTANCE.FBO_ENABLE));
		enableVBO.setOnAction(setup(enableVBO, GameSettingMinecraft.INSTANCE.USE_VBO));
		graphic.setOnAction(setup(graphic, GameSettingMinecraft.INSTANCE.GRAPHIC));
		mipmap.setOnAction(setup(mipmap, GameSettingMinecraft.INSTANCE.MIPMAP_LEVELS));
		particle.setOnAction(setup(particle, GameSettingMinecraft.INSTANCE.PARTICLES));
		ambientOcclusion.setOnAction(setup(ambientOcclusion, GameSettingMinecraft.INSTANCE.AMBIENT_OCCLUSION));
	}

	private EventHandler<ActionEvent> setup(Labeled labeled, GameSetting.Option<?> option)
	{
		labeled.setText(getLocalizedText(option));
		return event ->
				Bootstrap.getCore().getProfileManager().selecting().getGameSetting(GameSettingMinecraft.INSTANCE).ifPresent(gameSettingInstance ->
						labeled.setText(getLocalizedText(option, gameSettingInstance.getOption(option))));
	}

	private String getLocalizedText(GameSetting.Option<?> option)
	{
		String name = option.getName();
		return LanguageMap.INSTANCE.translate(name) + ":" +
				LanguageMap.INSTANCE.translate(name + "." + "null");
	}

	private String getLocalizedText(GameSetting.Option<?> option, Object value)
	{
		String name = option.getName();
		return LanguageMap.INSTANCE.translate(name) + ":" +
				LanguageMap.INSTANCE.translate(name + "." + value);
	}

	public void createMinecraftGameSetting(ActionEvent event)
	{
		Bootstrap.getCore().getProfileManager().selecting().addGameSetting(new GameSettingInstance(GameSettingMinecraft
				.INSTANCE));
	}
}

