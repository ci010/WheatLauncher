package net.wheatlauncher.control;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.effects.JFXDepthManager;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.context.ViewContext;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.launcher.AuthProfile;
import net.launcher.Bootstrap;
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
	private StackPane rroot;
	@FXML
	private VBox leftBox;
	@FXML
	private StackPane rightBox;

	/*dialog*/
	@FXML
	private JFXDialog settingDialog;
	@FXML
	private JFXDialog loginDialog;
//	@FXMLInnerController
//	public ControllerSetting settingDialogController;

	@PostConstruct
	public void init() throws FlowException
	{
		initSkin();
		initDialog();

		JFXDepthManager.setDepth(leftBox, 3);

		profileName.textProperty().bind(Bindings.createStringBinding(() -> Bootstrap.getCore().getSelected(),
				Bootstrap.getCore().selectedProperty()));
		player.textProperty().bind(Bindings.createStringBinding(() -> Bootstrap.getCore().getAuthModule().getAccount
				(), Bootstrap.getCore().getAuthModule().accountProperty()));
	}

	@PreDestroy
	public void distroy()
	{
		settingDialog.close();
		loginDialog.close();
		animation.stop();
	}

	private void initDialog()
	{
		root.getChildren().remove(settingDialog);
		root.getChildren().remove(loginDialog);
		settingDialog.setOnDialogClosed(e ->
		{
			settingDialog.setContent(null);
			System.gc();
		});
		loginDialog.setOnDialogClosed(e ->
		{
			settingDialog.setContent(null);
			System.gc();
		});
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
			AuthProfile.State state = module.getState();
			if (state == AuthProfile.State.Logout)
			{
				defaultSkin();
				return;
			}
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
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));

		animation = new AnimationRotate(canvas);
		animation.play();
	}

	private void defaultSkin()
	{
		canvas.setSkin(SkinCanvas.STEVE, false);//TODO add random to other skin
	}

	public void popupLogin(ActionEvent event)
	{
		WindowsManager.Page thisPage = flowContext.getRegisteredObject(WindowsManager.Page.class);
		try
		{
			ViewContext<?> load = thisPage.load(ControllerLogin.class);
			loginDialog.setContent((Region) load.getRootNode());
			loginDialog.show(rroot);
		}
		catch (FxmlLoadException e)
		{
			thisPage.displayError(e);
		}
	}

	public void popupSetting(ActionEvent event)
	{
		System.out.println("popup setting");
		WindowsManager.Page thisPage = flowContext.getRegisteredObject(WindowsManager.Page.class);
		try
		{
			ViewContext<?> load = thisPage.load(ControllerSetting.class);
			settingDialog.setContent((Region) load.getRootNode());
			settingDialog.show(rroot);
		}
		catch (FxmlLoadException e)
		{
			thisPage.displayError(e);
		}
	}

	@FXML
	protected void onClick(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) root.requestFocus();}

	@FXML
	private void onClose(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) Platform.exit();}

	@Override
	public void reload()
	{

	}

	@Override
	public void unload()
	{

	}
}
