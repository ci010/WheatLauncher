package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.profile.LaunchProfile;
import net.launcher.resourcepack.ResourcePackManager;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.utils.ReloadableController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ControllerResourcePackView implements ReloadableController
{
	public StackPane root;
	public JFXTreeTableView<ResCol> availableView;
	public JFXTreeTableColumn<ResCol, StackPane> available;

	public JFXTreeTableView<ResCol> selectedView;
	public JFXTreeTableColumn<ResCol, StackPane> selected;
	//
	public JFXButton manage;

	private ResourcePackManager manager;

	@PostConstruct
	public void init()
	{
		Optional<LaunchElementManager<ResourcePack>> elementManager = Bootstrap.getCore().getElementManager(ResourcePack.class);
		if (!elementManager.isPresent()) {this.root.setDisable(true); return;}
		manager = (ResourcePackManager) elementManager.get();
		this.root.disableProperty().bind(Bindings.createBooleanBinding(() -> manager.getAllElement().isEmpty()
		));
	}

	private ObservableList<ResCol> avail = FXCollections.observableArrayList(),
			using = FXCollections.observableArrayList();

	public void refresh(ActionEvent event)
	{
		LaunchProfile selectedProfile = LaunchCore.getCurrentProfile(Bootstrap.getCore());
		List<ResourcePack> element = manager.getAllIncludedElement(selectedProfile);
		Set<ResourcePack> ava = manager.getAllElement();
		ava.removeAll(element);
		using.clear();
		using.addAll(element.stream().map(ResCol::new).collect(Collectors.toList()));
		avail.clear();
		avail.addAll(ava.stream().map(ResCol::new).collect(Collectors.toList()));
	}

	private Callback<TreeTableColumn.CellDataFeatures<ResCol, StackPane>, ObservableValue<StackPane>> createCallback
			(boolean left)
	{
		return (feature) ->
		{
			ResourcePackManager resourcePackManger = manager;

			StackPane pane = new StackPane();

			ResCol value = feature.getValue().getValue();
			HBox back = new HBox();
			{
				try
				{
					ImageView icon = new ImageView((resourcePackManger).getIcon(value.pack));
					back.getChildren().add(icon);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				Label nameL = new Label(value.pack.getPackName()), desL = new Label(value.pack.getDescription());
				back.getChildren().add(new VBox(nameL, desL));
			}
			pane.getChildren().add(back);
			pane.getChildren().add(createBtnOverlay(left));

			return new SimpleObjectProperty<>(pane);
		};
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

	@Override
	public void reload()
	{
		Logger.trace("reload");
		available.setCellValueFactory(createCallback(true));
		selected.setCellValueFactory(createCallback(false));
		refresh(null);
		availableView.setRoot(new RecursiveTreeItem<>(avail, RecursiveTreeObject::getChildren));
		selectedView.setRoot(new RecursiveTreeItem<>(using, RecursiveTreeObject::getChildren));
	}

	@Override
	public void unload()
	{

	}

	public static class ResCol extends RecursiveTreeObject<ResCol>
	{
		private ResourcePack pack;

		public ResCol(ResourcePack pack)
		{
			this.pack = pack;
		}
	}
}
