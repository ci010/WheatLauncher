package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.ResourcePackManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import net.launcher.control.MovableResourcePackCell;
import net.launcher.game.ResourcePack;
import net.wheatlauncher.control.utils.ReloadableController;

/**
 * @author ci010
 */
public class ControllerProfileResourcePack implements ReloadableController
{
	public StackPane root;
	public JFXListView<ResourcePack> availableView;
	public JFXListView<ResourcePack> selectedView;
	//
	public JFXButton manage;
	public JFXTextField searchSelecting;

	private ResourcePackManager manager;

	private ListProperty<ResourcePack> selected = new SimpleListProperty<>();
//
//	private Predicate<ResourcePack> resourcePackPredicate = resourcePack -> resourcePack.getPackName().contains(searchSelecting.getText()) || resourcePack.getDescription()
//			.contains(searchSelecting.getText());

	public void initialize()
	{
		manager = ARML.core().getResourcePackManager();

		for (Node node : availableView.getParent().getChildrenUnmodifiable())
			if (node != availableView)
				JFXDepthManager.setDepth(node, 1);

		for (Node node : selectedView.getParent().getChildrenUnmodifiable())
			if (node != selectedView)
				JFXDepthManager.setDepth(node, 1);

		selected.bind(Bindings.createObjectBinding(() ->
						manager.getIncludeElementContainer(ARML.core().getProfileManager().selecting()),
				ARML.core().getProfileManager().selectedProfileProperty()));

		FilteredList<ResourcePack> selectedRes = new FilteredList<>(selected);
//		selectedRes.predicateProperty().bind(Bindings.createObjectBinding(() -> resourcePackPredicate, searchSelecting.textProperty()));
		selectedView.setItems(selectedRes);

		FilteredList<ResourcePack> allRes = new FilteredList<>(manager.getAllElement());
		availableView.setItems(allRes);
		allRes.predicateProperty().bind(Bindings.createObjectBinding(() ->
				resourcePack -> !selected.contains(resourcePack), selected));

		availableView.setFocusTraversable(false);
		selectedView.setFocusTraversable(false);

		availableView.getParent().disableProperty().bind(Bindings.createBooleanBinding(() -> availableView.getItems().isEmpty(),
				availableView.getItems()));
		selectedView.getParent().disableProperty().bind(Bindings.createBooleanBinding(() -> selectedView.getItems().isEmpty(),
				selectedView.getItems()));
		availableView.setCellFactory(param ->
				new JFXListCell<ResourcePack>()
				{
					@Override
					public void updateItem(ResourcePack item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!empty && item != null)
							setGraphic(new MovableResourcePackCell(item, manager.getIcon(item), true));
						else
							setGraphic(null);
					}
				});
		availableView.addEventHandler(MovableResourcePackCell.MOVE_RESOURCE_PACK_EVENT, event ->
		{
			if (event.getType() == MovableResourcePackCell.MoveResourcePackEvent.Type.SWITCH)
				if (!selected.get().contains(event.getResourcePack()))
					selected.get().add(event.getResourcePack());
		});
		selectedView.addEventHandler(MovableResourcePackCell.MOVE_RESOURCE_PACK_EVENT, event ->
		{
			if (event.getType() == MovableResourcePackCell.MoveResourcePackEvent.Type.SWITCH)
				selected.get().remove(event.getResourcePack());
			if (event.getType() == MovableResourcePackCell.MoveResourcePackEvent.Type.UP)
			{
				if (selected.size() <= 1) return;
				int i = selected.get().indexOf(event.getResourcePack()) - 1;
				selected.remove(event.getResourcePack());
				selected.add(i, event.getResourcePack());
			}
			else if (event.getType() == MovableResourcePackCell.MoveResourcePackEvent.Type.DOWN)
			{
				if (selected.size() <= 1) return;
				int i = selected.get().indexOf(event.getResourcePack()) + 1;
				selected.remove(event.getResourcePack());
				selected.add(i, event.getResourcePack());
			}
		});
		selectedView.setCellFactory(param ->
				new JFXListCell<ResourcePack>()
				{
					@Override
					public void updateItem(ResourcePack item, boolean empty)
					{
						super.updateItem(item, empty);
						if (!empty && item != null)
							setGraphic(new MovableResourcePackCell(item, manager.getIcon(item), false));
						else
							setGraphic(null);
					}
				});
	}

	public void refresh(ActionEvent event)
	{
		LaunchProfile selectedProfile = ARML.core().getProfileManager().selecting();
//		List<ResourcePack> selected = manager.getAllIncludedElement(selectedProfile);
//		Set<ResourcePack> remain = manager.getAllElement();
//		remain.removeAll(selected);
//		selectedView.getItems().setAll(selected);
//		availableView.getItems().setAll(remain);
	}


	@Override
	public void reload()
	{
		ARML.logger().info("reload");
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
}
