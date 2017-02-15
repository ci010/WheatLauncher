package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.Logger;
import net.launcher.control.ImageCell;
import net.launcher.game.WorldInfo;
import net.wheatlauncher.control.utils.WindowsManager;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerMap
{
	public JFXListView<WorldInfo> maps;
	public ResourceBundle resources;
	public JFXTextField search;
	public JFXButton exportBtn, importBtn;

	public void initialize()
	{
		Logger.trace("init map");
		FilteredList<WorldInfo> filteredList = new FilteredList<>(Bootstrap.getCore().getAssetsManager().getWorldInfos());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Predicate<WorldInfo>) worldInfo ->
				worldInfo.getFileName().contains(search.getText()) ||
						worldInfo.getDisplayName().contains(search.getText()), search.textProperty()));
		maps.setItems(filteredList);

		maps.setCellFactory(param -> new ListCell<WorldInfo>()
		{
			@Override
			protected void updateItem(WorldInfo item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) return;
				WorldInfoCell worldInfoCell = new WorldInfoCell(item);
				try {worldInfoCell.setImage(Bootstrap.getCore().getAssetsManager().getRepository().getIcon(item));}
				catch (Exception e) { WindowsManager.displayError(maps.getScene(), e);}
				this.setGraphic(worldInfoCell);
			}
		});
	}

	public class WorldInfoCell extends ImageCell<WorldInfo> implements Observable
	{
		private WorldInfo worldInfo;
		private List<InvalidationListener> list;

		public WorldInfoCell(WorldInfo worldInfo)
		{
			this.worldInfo = worldInfo;
			change();
		}

		@Override
		protected void init()
		{
			list = FXCollections.observableList(new ArrayList<>(3));
			super.init();
		}

		void change()
		{
			for (InvalidationListener listener : list)
				listener.invalidated(this);
		}

		@Override
		protected Node buildContent()
		{
			VBox box = new VBox();
			box.setSpacing(10);
			Label name = new Label(), fileInfo = new Label(), mode = new Label();
			box.getChildren().setAll(name, fileInfo, mode);
			name.setStyle("-fx-font-weight:BOLD; -fx-font-size:14px;-fx-text-fill:BLACK;");

			name.textProperty().bind(Bindings.createStringBinding(() ->
					{
						if (worldInfo == null) return "";
						return worldInfo.getDisplayName();
					}, this
			));
			fileInfo.textProperty().bind(Bindings.createStringBinding(() ->
			{
				if (worldInfo == null) return "";
				return worldInfo.getFileName() +
						" (" + new Date(worldInfo.getLastPlayed()) + ")";
			}, this));
			mode.textProperty().bind(Bindings.createStringBinding(() ->
			{
				WorldInfo value = worldInfo;
				if (value == null) return "";
				StringJoiner joiner = new StringJoiner(", ");
				joiner.add(value.getGameType().name());
				if (value.isEnabledCheat()) joiner.add("Cheat");
				if (value.isHardCore()) joiner.add("Hardcore");
				return joiner.toString();
			}, this));
			return box;
		}

		@Override
		public void addListener(InvalidationListener listener)
		{
			list.add(listener);
		}

		@Override
		public void removeListener(InvalidationListener listener) {}
	}
}
