package net.wheatlauncher.control;

import api.launcher.ARML;
import api.launcher.AuthManager;
import api.launcher.LaunchProfile;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.launcher.utils.StringUtils;
import net.launcher.utils.Tasks;
import net.wheatlauncher.control.utils.AnimationRotate;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.util.UUIDUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class ControllerPreview
{
	public Parent window;

	/*controls*/
	@FXML
	private JFXButton profileName;
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

	/*dialog*/
	public JFXDialog profileSettingDialog;

	public JFXDialog settingDialog;

	public void initialize() throws Exception
	{
		JFXDepthManager.setDepth(leftBox, 3);
		animation = new AnimationRotate(canvas);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));
		JFXDepthManager.setDepth(player.getParent(), 2);
		JFXDepthManager.setDepth(window, 2);
		LaunchProfile selecting = ARML.core().getProfileManager().selecting();
		if (selecting != null)
			profileName.setText(selecting.getDisplayName());
		ARML.core().getProfileManager().selectedProfileProperty().addListener(observable ->
				profileName.textProperty().bind(Bindings.createStringBinding(() ->
								ARML.core().getProfileManager().selecting().getDisplayName(),
						ARML.core().getProfileManager().selectedProfileProperty())));
		player.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (ARML.core().getAuthManager().getCache() != null)
						return ARML.core().getAuthManager().getCache().getUsername();
					return StringUtils.EMPTY;
				},
				ARML.core().getAuthManager().cacheProperty()));
		initDialog();
		initSkin();
	}

	private void initDialog()
	{
		root.getChildren().remove(profileSettingDialog);
		root.getChildren().remove(settingDialog);

		profileSettingDialog.setDialogContainer(((StackPane) root.getParent()));
		profileSettingDialog.setContentHolderBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null,
				null)));

		settingDialog.setDialogContainer((StackPane) root.getParent());
		settingDialog.setContentHolderBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null,
				null)));
	}

	private void initSkin()
	{
		ARML.core().getAuthManager().cacheProperty().addListener(observable ->
		{
			try
			{
				AuthManager module = ARML.core().getAuthManager();
				AuthInfo auth = module.getCache();
				if (auth == null)
				{
					defaultSkin();
					return;
				}
				ProfileService profileService = module.getAuthorizeInstance().createProfileService();
				Map<TextureType, Texture> textures = profileService.getTextures(
						profileService.getGameProfile(UUIDUtils.toUUID(auth.getUUID())));
				if (textures.isEmpty()) defaultSkin();
				else
				{
					Texture texture = textures.get(TextureType.SKIN);
					ARML.core().getTaskCenter().runTask(new Task<Void>()
					{
						@Override
						protected Void call() throws Exception
						{
							canvas.setSkin(Tasks.optional(() -> new Image(texture.openStream())).orElse(SkinCanvas.STEVE),
									Optional.ofNullable(texture.getMetadata().get("model")).orElse("steve").equals("slim"));
							animation.play();
							return null;
						}
					});
				}
			}
			catch (Exception e) {defaultSkin();}
		});

	}

	private void defaultSkin()
	{
		canvas.setSkin(SkinCanvas.STEVE, false);//TODO add random to other skin
		animation.play();
	}

	public void onSwitchPlayer(ActionEvent event)
	{
		((Consumer) root.getScene().getUserData()).accept("LOGIN");
	}

	public void popupProfileSetting(ActionEvent event) {profileSettingDialog.show(((StackPane) root.getParent()));}

	public void popupSetting(ActionEvent event)
	{
		settingDialog.show(((StackPane) root.getParent()));
	}

	@FXML
	protected void onClick(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) root.requestFocus();}

	@FXML
	private void onClose(MouseEvent event) {if (event.getButton() == MouseButton.PRIMARY) Platform.exit();}

	public void reload()
	{
		AuthManager module = ARML.core().getAuthManager();
		assert module.getCache() != null;
		assert module.getAuthorizeInstance() != null;
		assert module.getAccount() != null;
		initSkin();
		initDialog();
	}

	public void unload()
	{
		profileSettingDialog.close();
		animation.stop();
	}

	public void launch(ActionEvent actionEvent) throws Exception
	{
//		ARML.core().launch();
	}
}
