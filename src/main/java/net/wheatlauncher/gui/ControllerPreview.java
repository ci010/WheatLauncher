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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import java.lang.reflect.Field;
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

	private JFXDialog dialog;
	@FXML
	private ColorTransitionButton launch, profileName;

	@PostConstruct
	public void init() throws FlowException
	{
		animation = new AnimationRotate(canvas);
//				new Timeline(
//						new KeyFrame(Duration.ZERO,
//								new KeyValue(canvas.getYRotate().angleProperty(), 180, Interpolator.LINEAR)),
//						new KeyFrame(Duration.millis(8000),
//								new KeyValue(canvas.getYRotate().angleProperty(), 540, Interpolator.LINEAR)),
//				new KeyFrame(Duration.millis(0),
//						new KeyValue(canvas.getYRotate().angleProperty(), 0, Interpolator.EASE_BOTH)));
//		animation.setCycleCount(Animation.INDEFINITE);
		JFXDepthManager.setDepth(leftBox, 3);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));
		profileName.setOnAction(event -> openDialog());
		profileName.setTooltip(new Tooltip("Switch profile"));
		setting.setTooltip(new Tooltip("Setting"));
		switchPlayer.setTooltip(new Tooltip("Switch account"));
		switchPlayer.setOnAction(action -> flowContext.getRegisteredObject(PageSwitcher.class).switchToQuite("login"));
	}

	private void openDialog()
	{
		if (dialog == null)
		{
			dialog = new JFXDialog();
			try
			{
				FXMLLoader fxmlLoader = new FXMLLoader(ControllerPreview.class.getResource("/fxml/Setting" + ".fxml"));
				Region load = fxmlLoader.load();
				ControllerSetting controllerSetting = fxmlLoader.getController();
				controllerSetting.close = dialog::close;
				controllerSetting.setup();
				dialog.setContent(load);
				dialog.setDialogContainer(flowContext.getRegisteredObject(StackPane.class));
				dialog.setOnDialogOpened(event -> controllerSetting.reload());
				dialog.setOnDialogClosed(event -> controllerSetting.unload());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				Field contentHolder = JFXDialog.class.getDeclaredField("contentHolder");
				if (!contentHolder.isAccessible())
					contentHolder.setAccessible(true);
				StackPane holder = (StackPane) contentHolder.get(dialog);
				holder.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			}
			catch (NoSuchFieldException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
			dialog.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null)));
		}
		dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		dialog.show();
	}

	private void tryLoad()
	{
		try
		{
			Object load = FXMLLoader.load(ControllerPreview.class.getResource("/fxml/Setting.fxml"));
			System.out.println(load);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		profileName.setText(currentProfile.getName());
		player.setText(currentProfile.getUserName());
	}

	@Override
	public void unload()
	{
		animation.stop();
	}
}
