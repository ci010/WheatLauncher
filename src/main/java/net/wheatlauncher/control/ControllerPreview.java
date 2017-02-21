package net.wheatlauncher.control;

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
import net.launcher.auth.AuthManager;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.StringUtils;
import net.launcher.utils.Tasks;
import net.wheatlauncher.MainApplication;
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
	public JFXDialog rootDialog;
	public Parent window;

	/*controls*/
	@FXML
	private JFXButton setting, switchPlayer;
	@FXML
	private JFXButton launch, profileName;
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

	public JFXDialog settingDialog;

	public void initialize() throws Exception
	{
		JFXDepthManager.setDepth(leftBox, 3);
		animation = new AnimationRotate(canvas);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));
		JFXDepthManager.setDepth(player.getParent(), 2);
		JFXDepthManager.setDepth(window, 2);
		LaunchProfile selecting = MainApplication.getCore().getProfileManager().selecting();
		if (selecting != null)
			profileName.setText(selecting.getDisplayName());
		MainApplication.getCore().getProfileManager().selectedProfileProperty().addListener(observable ->
				profileName.textProperty().bind(Bindings.createStringBinding(() ->
								MainApplication.getCore().getProfileManager().selecting().getDisplayName(),
						MainApplication.getCore().getProfileManager().selectedProfileProperty())));
		player.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (MainApplication.getCore().getAuthManager().getCache() != null)
						return MainApplication.getCore().getAuthManager().getCache().getUsername();
					return StringUtils.EMPTY;
				},
				MainApplication.getCore().getAuthManager().cacheProperty()));
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
		MainApplication.getCore().getAuthManager().cacheProperty().addListener(observable ->
		{
			try
			{
				AuthManager module = MainApplication.getCore().getAuthManager();
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
					MainApplication.getCore().getTaskCenter().runTask(new Task<Void>()
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
		AuthManager module = MainApplication.getCore().getAuthManager();
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
		MainApplication.getCore().launch();
	}
}
