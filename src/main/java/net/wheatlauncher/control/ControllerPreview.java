package net.wheatlauncher.control;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.effects.JFXDepthManager;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.launcher.AuthProfile;
import net.launcher.Bootstrap;
import net.launcher.utils.StringUtils;
import net.wheatlauncher.control.setting.ControllerSetting;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.util.UUIDUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;

/**
 * @author ci010
 */
@FXMLController("/fxml/Preview.fxml")
public class ControllerPreview implements ReloadableController
{
	public JFXDialog rootDialog;
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	/*controls*/
	@FXML
	private ColorTransitionButton setting, switchPlayer;
	@FXML
	private ColorTransitionButton launch, profileName;
	@FXML
	private Label player;
	private Animation animation;
	@FXML
	private SkinCanvas canvas;

	/*controls parent*/
	@FXML
	private Pane root;

	@FXML
	private VBox leftBox;
	@FXML
	private StackPane rightBox;

	/*dialog*/
	public JFXDialog profileSettingDialog;
	@FXMLInnerController
	public ControllerSetting profileSettingDialogController;


	@PostConstruct
	public void init() throws FlowException
	{
		JFXDepthManager.setDepth(leftBox, 3);
		animation = new AnimationRotate(canvas);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));

		profileName.textProperty().bind(Bindings.createStringBinding(() -> Bootstrap.getCore().getSelected(),
				Bootstrap.getCore().selectedProperty()));
		player.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (Bootstrap.getCore().getAuthModule().getCache() != null)
						return Bootstrap.getCore().getAuthModule().getCache().getUsername();
					return StringUtils.EMPTY;
				},
				Bootstrap.getCore().getAuthModule().cacheProperty()));
	}

	@PreDestroy
	public void distroy()
	{
		profileSettingDialog.close();
		animation.stop();
	}

	private void initDialog()
	{
		root.getChildren().remove(profileName);
		profileSettingDialog.setDialogContainer(flowContext.getRegisteredObject(StackPane.class));
//		settingDialog.setPrefSize(500,400);
//		settingDialog.getContent().setPrefSize(500,400);
//		settingDialog.setDialogContainer(flowContext.getRegisteredObject(StackPane.class));
//		settingDialog.setOverlayClose(true);
//		settingDialog.setOnDialogClosed(event -> settingDialogController.unload());
//		ControlUtils.setDialogHolderBackground(settingDialog, new Background(new BackgroundFill(Color.TRANSPARENT, null,
//				null)));
	}

	private void initSkin()
	{
		try
		{
			AuthProfile module = Bootstrap.getCore().getAuthModule();
			AuthInfo auth = module.getCache();
			if (auth == null)
				throw new IllegalStateException();
			ProfileService profileService = module.getAuthorize().createProfileService();
			Map<TextureType, Texture> textures = profileService.getTextures(
					profileService.getGameProfile(UUIDUtils.toUUID(auth.getUUID())));
			if (textures.isEmpty())
				defaultSkin();
			else
			{
				Texture texture = textures.get(TextureType.SKIN);
				Bootstrap.getCore().getService().submit(() ->
				{
					try
					{
						String model = texture.getMetadata().get("model");
						Image image = new Image(texture.openStream());
						Platform.runLater(() -> canvas.setSkin(image, model != null && model.equals("slim")));
					}
					catch (IOException e) {defaultSkin();}
				});
			}
		}
		catch (AuthenticationException e)
		{
			defaultSkin();
		}

		animation.play();
	}

	private void defaultSkin()
	{
		canvas.setSkin(SkinCanvas.STEVE, false);//TODO add random to other skin
	}

	public void onSwitchPlayer(ActionEvent event)
	{
		flowContext.getRegisteredObject(WindowsManager.Page.class).switchPage(ControllerLogin.class);
	}

	public void popupProfileSetting(ActionEvent event)
	{
		profileSettingDialog.show(flowContext.getRegisteredObject(StackPane.class));
	}

	public void popupSetting(ActionEvent event)
	{

	}

	@FXML
	protected void onClick(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) root.requestFocus();}

	@FXML
	private void onClose(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) Platform.exit();}

	@Override
	public void reload()
	{
		initSkin();
		initDialog();
	}

	@Override
	public void unload()
	{
		profileSettingDialog.close();
		animation.stop();
	}
}