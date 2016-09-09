package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.effects.JFXDepthManager;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.wheatlauncher.Core;
import net.wheatlauncher.launch.LaunchProfile;
import net.wheatlauncher.utils.ControlUtils;
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
	private Animation animation;

	@FXML
	private StackPane rightBox;

	@FXML
	private ColorTransitionButton launch, profileName;

	@FXML
	private JFXDialog settingDialog;

	@FXMLInnerController
	public ControllerSetting settingDialogController;

	@PostConstruct
	public void init() throws FlowException
	{
		root.getChildren().remove(settingDialog);
		settingDialog.setDialogContainer(flowContext.getRegisteredObject(StackPane.class));

		JFXDepthManager.setDepth(leftBox, 3);
		animation = new AnimationRotate(canvas);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));
		profileName.setOnAction(event -> openDialog());

		profileName.setTooltip(new Tooltip("Switch profile"));
		setting.setTooltip(new Tooltip("Setting"));
		switchPlayer.setTooltip(new Tooltip("Switch account"));
		switchPlayer.setOnAction(action -> flowContext.getRegisteredObject(PageManager.class).switchToQuite("login"));
	}

	private boolean firstOpen = true;

	private void openDialog()
	{
		if (firstOpen)
		{
			firstOpen = false;
			ControlUtils.setDialogHolderBackground(settingDialog, new Background(new BackgroundFill(Color.TRANSPARENT, null,
					null)));
			settingDialog.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), null, null)));
		}
		System.out.println(settingDialog);
		settingDialog.show();
	}


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
		profileName.setText(currentProfile.getName());
		player.setText(currentProfile.getUserName());
	}

	@Override
	public void unload()
	{
		animation.stop();
	}
}
