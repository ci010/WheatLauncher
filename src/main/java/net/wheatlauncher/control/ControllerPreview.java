package net.wheatlauncher.control;

import api.launcher.Shell;
import api.launcher.event.LoginedEvent;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import moe.mickey.minecraft.skin.fx.animation.SkinAniRunning;
import net.wheatlauncher.control.utils.AnimationRotate;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class ControllerPreview
{
	private Map<String, JFXDialog> dialogs = new TreeMap<>();

	public Pane root;

	public Label useSever;

	public Label player;
	public SkinCanvas canvas;
	private Animation animation;

	public JFXButton profileName;

	public VBox leftBox;

	public void initialize() throws Exception
	{
		JFXDepthManager.setDepth(leftBox, 3);
		animation = new AnimationRotate(canvas);
		canvas.getAnimationPlayer().addSkinAnimation(new SkinAniRunning(100, 100, 30, canvas));
		JFXDepthManager.setDepth(player.getParent(), 2);
		profileName.textProperty().bind(Bindings.createStringBinding(() -> Shell.instance().getProfileProxy().getName(),
				Shell.instance().getProfileProxy().idProperty()));
		player.textProperty().bind(Bindings.createStringBinding(() -> Shell.instance().getAuthorizeProxy().getAccount(),
				Shell.instance().getAuthorizeProxy().idProperty()));

		Shell.bus().addEventHandler(LoginedEvent.TYPE, event ->
		{
			Task<Pair<Image, Boolean>> getSkin = Shell.instance().buildAndExecute("skin");
			getSkin.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e ->
			{
				Pair<Image, Boolean> value = (Pair<Image, Boolean>) (e.getSource().getValue());
				canvas.setSkin(value.getKey(), value.getValue());
				animation.play();
			});
			getSkin.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, e -> defaultSkin());
		});
//			try
//			{
//
//				ProfileService profileService = authorize.createProfileService();
//				Map<TextureType, Texture> textures = profileService.getTextures(
//						profileService.getGameProfile(UUIDUtils.toUUID(auth.getUUID())));
//				if (textures.isEmpty()) defaultSkin();
//				else
//				{
//					Texture texture = textures.get(TextureType.SKIN);
//
//					(new Task<Pair<Image, Boolean>>()
//					{
//						@Override
//						protected Pair<Image, Boolean> call() throws Exception
//						{
//							return new Pair<>(Tasks.optional(() -> new Image(texture.openStream())).orElse(SkinCanvas.STEVE),
//									Optional.ofNullable(texture.getMetadata().get("model")).orElse("steve").equals
//											("slim"));
//						}
//					}).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
//					{
//						Pair<Image, Boolean> value = (Pair<Image, Boolean>) event.getSource().getValue();
//						canvas.setSkin(value.getKey(), value.getValue());
//						animation.play();
//					});
//				}
//			}
//			catch (Exception e) {defaultSkin();}

		for (Node node : root.getChildren())
			if (node instanceof JFXDialog)
			{
				JFXDialog dialog = (JFXDialog) node;
				dialog.setDialogContainer((StackPane) root.getParent());
				dialog.setContentHolderBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null,
						null)));
				dialogs.put(node.getId(), dialog);
			}
		root.getChildren().removeAll(dialogs.values());
	}

	private void defaultSkin()
	{
		canvas.setSkin(SkinCanvas.STEVE, false);//TODO add random to other skin
		animation.play();
	}

	public void launch() throws Exception
	{
		Shell.instance().buildAndExecute("launch");
//		((LaunchCore) ARML.core()).launch();
	}

	public void switchPage(ActionEvent event)
	{
		Node source = (Node) event.getSource();
		((Consumer) root.getScene().getUserData()).accept(source.getId());
	}

	public void popupDialog(ActionEvent event)
	{
		String id = ((JFXButton) event.getSource()).getId();
		JFXDialog jfxDialog = dialogs.get(id);
		if (jfxDialog != null)
			jfxDialog.show((StackPane) root.getParent());
	}
}
