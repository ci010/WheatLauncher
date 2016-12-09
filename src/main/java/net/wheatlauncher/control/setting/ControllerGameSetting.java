package net.wheatlauncher.control.setting;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import net.launcher.setting.GameSettingMinecraft;
import net.wheatlauncher.utils.LanguageMap;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author ci010
 */
public class ControllerGameSetting
{
	public VBox root;

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

	@PostConstruct
	public void init()
	{
		entityShadow.setOnAction(setup(entityShadow, GameSettingMinecraft.INSTANCE.ENTITY_SHADOWS));
		renderCloud.setOnAction(setup(renderCloud, GameSettingMinecraft.INSTANCE.RENDER_CLOUDS));
		enableFBO.setOnAction(setup(enableFBO, GameSettingMinecraft.INSTANCE.FBO_ENABLE));
		enableVBO.setOnAction(setup(enableVBO, GameSettingMinecraft.INSTANCE.USE_VBO));
		graphic.setOnAction(setup(graphic, GameSettingMinecraft.INSTANCE.GRAPHIC));
		mipmap.setOnAction(setup(mipmap, GameSettingMinecraft.INSTANCE.MIPMAP_LEVELS));
		particle.setOnAction(setup(particle, GameSettingMinecraft.INSTANCE.PARTICLES));
		ambientOcclusion.setOnAction(setup(ambientOcclusion, GameSettingMinecraft.INSTANCE.AMBIENT_OCCLUSION));
	}

	private EventHandler<ActionEvent> setup(Labeled labeled, GameSetting.Option<?> booleanOption)
	{
		return event ->
		{
			LaunchProfile selected = LaunchCore.getCurrentProfile(Bootstrap.getCore());
			Optional<GameSettingInstance> gameSetting = selected.getGameSetting(GameSettingMinecraft.INSTANCE);
			if (gameSetting.isPresent())
			{
				GameSettingInstance settingInstance = gameSetting.get();
				String name = booleanOption.getName();
				labeled.setText(LanguageMap.INSTANCE.translate(name) + ":" + LanguageMap.INSTANCE.translate(name + "."
						+ settingInstance.getOption(booleanOption)));
			}
		};
	}
}

