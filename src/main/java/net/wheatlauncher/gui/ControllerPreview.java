package net.wheatlauncher.gui;

import com.jfoenix.effects.JFXDepthManager;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.wheatlauncher.Core;
import net.wheatlauncher.launch.LaunchProfile;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.yggdrasil.YggdrasilProfileServiceBuilder;
import org.to2mbn.jmccc.util.UUIDUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * @author ci010
 */
@FXMLController("/fxml/Preview.fxml")
public class ControllerPreview implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private Pane root;

	@FXML
	private ColorTransitionButton setting, switchPlayer;

	@FXML
	private VBox leftBox;
	@FXML
	private Label player;
	@FXML
	private SkinCanvas canvas;

	@FXML
	private StackPane rightBox;

	@FXML
	private ColorTransitionButton launch;

	@PostConstruct
	public void init()
	{
		animation = //new CachedTransition(canvas,
				new Timeline(
						new KeyFrame(Duration.ZERO,
								new KeyValue(canvas.getYRotate().angleProperty(), 180, Interpolator.LINEAR)),
						new KeyFrame(Duration.millis(8000),
								new KeyValue(canvas.getYRotate().angleProperty(), 540, Interpolator.LINEAR))/*,
				new KeyFrame(Duration.millis(0),
						new KeyValue(canvas.getYRotate().angleProperty(), 0, Interpolator.EASE_BOTH))*/);
		animation.setCycleCount(Animation.INDEFINITE);
		JFXDepthManager.setDepth(canvas, 4);
		JFXDepthManager.setDepth(leftBox, 3);
		canvas.getSubScene().setHeight(250);
		canvas.getSubScene().setWidth(150);
		canvas.getAnimationplayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));

		setting.setTooltip(new Tooltip("Setting"));
		switchPlayer.setTooltip(new Tooltip("Switch account"));
		switchPlayer.setOnAction(action -> flowContext.getRegisteredObject(PageSwitcher.class).switchToQuite("login"));
	}

	private Animation animation;

	@Override
	public void reload()
	{
		LaunchProfile currentProfile = Core.INSTANCE.getCurrentProfile();
		if (currentProfile.onlineModeProperty().get())
		{
			ProfileService profileService = YggdrasilProfileServiceBuilder.buildDefault();
			AuthInfo authInfo = currentProfile.authInfoProperty().get();
			if (authInfo == null)
				throw new IllegalStateException();
			try
			{
				PropertiesGameProfile gameProfile = profileService.getGameProfile(UUIDUtils.toUUID(authInfo.getUUID()));
				Map<TextureType, Texture> textures = profileService.getTextures(gameProfile);
				Texture texture = textures.get(TextureType.SKIN);
				String model = texture.getMetadata().get("model");
				Core.INSTANCE.getService().submit(() -> {
					try
					{
						Image image = new Image(texture.openStream());
						Platform.runLater(() -> canvas.setSkin(image, model != null && model.equals("slim")));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				});
			}
			catch (AuthenticationException e)
			{
				e.printStackTrace();
			}
		}
		animation.play();
		player.setText(currentProfile.getUserName());
	}

	@Override
	public void unload()
	{
		animation.stop();
	}
}
