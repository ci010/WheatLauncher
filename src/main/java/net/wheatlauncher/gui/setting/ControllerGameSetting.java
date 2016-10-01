package net.wheatlauncher.gui.setting;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import net.launcher.GameSetting;
import net.launcher.setting.IntOption;
import net.launcher.setting.Option;
import net.wheatlauncher.Core;
import net.wheatlauncher.LaunchProfile;
import net.wheatlauncher.utils.LanguageMap;

import javax.annotation.PostConstruct;

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
		entityShadow.setOnAction(setupBool(entityShadow, GameSetting.ENTITY_SHADOWS));
		renderCloud.setOnAction(setupBool(renderCloud, GameSetting.RENDER_CLOUDS));
		enableFBO.setOnAction(setupBool(enableFBO, GameSetting.FBO_ENABLE));
		enableVBO.setOnAction(setupBool(enableVBO, GameSetting.USE_VBO));
		graphic.setOnAction(setupBool(graphic, GameSetting.GRAPHIC));
		mipmap.setOnAction(setupStep(mipmap, GameSetting.MIPMAP_LEVELS));
		particle.setOnAction(setupStep(particle, GameSetting.PARTICLES));
		ambientOcclusion.setOnAction(setupStep(ambientOcclusion, GameSetting.AMBIENT_OCCLUSION));
	}

	private EventHandler<ActionEvent> setupStep(Labeled labeled, IntOption.Step step)
	{
		return event ->
		{
			LaunchProfile currentProfile = Core.INSTANCE.getCurrentProfile();
			Property<Integer> option = currentProfile.getGameSettings().getOption(step);
			Integer value = option.getValue();
			if (value == null) value = step.defaultValue();
			else
			{
				value += 1;
				if (value > step.getMax() && step.isBound())
					value = value - step.getMax() - 1;
			}
			option.setValue(value);
			labeled.setText(step.getName() + " : " + LanguageMap.INSTANCE.translate(step.getName() + "." + option
					.getValue()));
		};
	}

	private EventHandler<ActionEvent> setupBool(Labeled labeled, Option<Boolean> booleanOption)
	{
		return event ->
		{
			LaunchProfile currentProfile = Core.INSTANCE.getCurrentProfile();
			Property<Boolean> grap = currentProfile.getGameSettings().getOption(booleanOption);
			Boolean b = grap.getValue();
			if (b == null) b = false;
			grap.setValue(!b);
			if (labeled != null)
				labeled.setText(booleanOption.getName() + " : " + LanguageMap.INSTANCE.translate(booleanOption
						.getName() + "." + grap.getValue()));
		};
	}
}

