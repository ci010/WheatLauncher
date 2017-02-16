package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.LaunchElementManager;
import net.launcher.Logger;
import net.launcher.game.ResourcePack;
import net.launcher.profile.LaunchProfile;
import net.launcher.resourcepack.ResourcePackManager;
import net.launcher.utils.Tasks;
import net.wheatlauncher.MainApplication;
import net.wheatlauncher.control.utils.ReloadableController;

import java.util.Optional;

/**
 * @author ci010
 */
public class ControllerResourcePackView implements ReloadableController
{
	public StackPane root;
	public JFXListView<ResourcePack> availableView;
	public JFXListView<ResourcePack> selectedView;
	//
	public JFXButton manage;
	public JFXTextField searchAvailable;
	public JFXTextField searchSelecting;

	private ResourcePackManager manager;

	public void initialize()
	{
		Optional<LaunchElementManager<ResourcePack>> elementManager = MainApplication.getCore().getElementManager(ResourcePack.class);
		if (!elementManager.isPresent()) {this.root.setDisable(true); return;}
		manager = (ResourcePackManager) elementManager.get();

		for (Node node : availableView.getParent().getChildrenUnmodifiable())
			if (node != availableView)
				JFXDepthManager.setDepth(node, 1);

		for (Node node : selectedView.getParent().getChildrenUnmodifiable())
			if (node != selectedView)
				JFXDepthManager.setDepth(node, 1);

//		ObservableList<ResourcePack> enabledResourcePacks = manager.getEnabledResourcePacks();
//		selectedView.setItems(manager.getEnabledResourcePacks());
//		availableView.setItems(new FilteredList<>(manager.getEnabledResourcePacks(),
//				resourcePack -> manager.getEnabledResourcePacks().contains(resourcePack)));
		availableView.getParent().disableProperty().bind(Bindings.createBooleanBinding(() -> availableView.getItems().isEmpty(),
				availableView.getItems()));
		selectedView.getParent().disableProperty().bind(Bindings.createBooleanBinding(() -> selectedView.getItems().isEmpty(),
				selectedView.getItems()));
		availableView.setCellFactory(param ->
				new ListCell<ResourcePack>()
				{
					@Override
					protected void updateItem(ResourcePack item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!empty && item != null)
							Tasks.optional(() -> manager.getIcon(item)).ifPresent(img -> setGraphic(createPane(item, img, true)));
					}
				});
		selectedView.setCellFactory(param ->
				new ListCell<ResourcePack>()
				{
					@Override
					protected void updateItem(ResourcePack item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!empty && item != null)
							Tasks.optional(() -> manager.getIcon(item)).ifPresent(img -> setGraphic(createPane(item,
									img, false)));
					}
				});
	}

	public void refresh(ActionEvent event)
	{
		LaunchProfile selectedProfile = MainApplication.getCore().getProfileManager().selecting();
//		List<ResourcePack> selected = manager.getAllIncludedElement(selectedProfile);
//		Set<ResourcePack> remain = manager.getAllElement();
//		remain.removeAll(selected);
//		selectedView.getItems().setAll(selected);
//		availableView.getItems().setAll(remain);
	}


	@Override
	public void reload()
	{
		Logger.trace("reload");
//		available.setCellValueFactory(createCallback(true));
//		selected.setCellValueFactory(createCallback(false));
//		refresh(null);
//		availableView.setRoot(new RecursiveTreeItem<>(avail, RecursiveTreeObject::getChildren));
//		selectedView.setRoot(new RecursiveTreeItem<>(using, RecursiveTreeObject::getChildren));
	}

	@Override
	public void unload()
	{

	}

	private StackPane createPane(ResourcePack resourcePack, Image icon, boolean left)
	{
		StackPane root = new StackPane();
		StackPane imgContainer = new StackPane();
		ImageView view = new ImageView(icon);
		HBox btnOverlay = createBtnOverlay(left);
		imgContainer.getChildren().addAll(view, btnOverlay);
		HBox box = new HBox();
		VBox content = new VBox(new Label(resourcePack.getPackName()), new Label(resourcePack.getDescription()));
		box.getChildren().addAll(imgContainer, content);
		root.getChildren().addAll(box);
		return root;
	}

	private HBox createBtnOverlay(boolean left)
	{
		HBox btnRoot = new HBox();

		JFXButton choose = new JFXButton();
		if (left) choose.setGraphic(new Icon("caret-right"));
		else choose.setGraphic(new Icon("caret-left"));

		VBox moveBtnPanel = new VBox();
		JFXButton moveUp = new JFXButton(), moveDown = new JFXButton();
		moveUp.setGraphic(new Icon("caret-up"));
		moveDown.setGraphic(new Icon("caret-down"));

		moveBtnPanel.getChildren().add(moveUp);
		moveBtnPanel.getChildren().add(moveDown);

		if (left)
		{
			btnRoot.getChildren().add(moveBtnPanel);
			btnRoot.getChildren().add(choose);
		}
		else
		{
			btnRoot.getChildren().add(choose);
			btnRoot.getChildren().add(moveBtnPanel);
		}
		return btnRoot;
	}
}
