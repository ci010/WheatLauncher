package net.wheatlauncher.control.mics;

import api.launcher.Shell;
import api.launcher.version.MinecraftVersion;
import com.jfoenix.controls.*;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.ComparableVersion;

import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author ci010
 */
public class ControllerMinecraftVersionChooserPane
{
	public Label version;
	public Label releaseTime;
	public Label releaseType;
	public JFXTableView<MinecraftVersion> versionTable;
	public TableColumn<MinecraftVersion, Node> remote;
	public TableColumn<MinecraftVersion, Node> versionCol;
	public TableColumn<MinecraftVersion, String> updateTime;
	public TableColumn<MinecraftVersion, String> releaseTimeCol;

	public ResourceBundle resources;
	public JFXTextField filter;
	public JFXToggleNode showAlpha;
	public JFXDialog confirmDownload;
	public StackPane root;

	public Supplier<StackPane> outerRoot;

	private ComboBoxBase<MinecraftVersion> comboBox;

	public void initialize()
	{
		root.getChildren().remove(confirmDownload);
	}

	public void initialize(ComboBoxBase<MinecraftVersion> picker, Supplier<StackPane> scene)
	{
		this.comboBox = picker;
		this.outerRoot = scene;

		FilteredList<MinecraftVersion> filteredList = new FilteredList<>(Shell.instance().getAllVersions());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(
				() -> (Predicate<MinecraftVersion>) version ->
				{
					if (filter.getText() != null && !filter.getText().equals(""))
						if (!version.getVersionId().contains(filter.getText())) return false;
					return showAlpha.isSelected() || "release".equals(version.getMetadata().get("type"));
				},
				filter.textProperty(), showAlpha.selectedProperty()
		));
		SortedList<MinecraftVersion> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(versionTable.comparatorProperty());
		versionTable.setItems(sortedList);

		version.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null) return obj.getVersionId();
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseType.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null) return obj.getMetadata().get("state");
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseTime.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null) return obj.getMetadata().get("releaseTime");
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));

		setupTable();

//		ARML.bus().addEventHandler(ProfileEvent.CREATE, event ->
//		{
//			LaunchProfile profile = event.getProfile();
//			profile.setVersion(versionTable.getItems().get(0).getVersionID());
//		});
	}

	private void setupTable()
	{
		remote.setCellValueFactory(param -> Bindings.createObjectBinding(() ->
		{
			Node node;
			switch (param.getValue().getMetadata().getOrDefault("state", "remote"))
			{
				case "downloading":
					node = new JFXSpinner();
					break;
				case "remote":
					Icon ic = new Icon("CLOUD");
					ic.setTooltip(new Tooltip(resources.getString("version.remote")));
					node = ic;
					break;
				default:
				case "local":
					ic = new Icon("FOLDER");
					ic.setTooltip(new Tooltip(resources.getString("version.local")));
					node = ic;
					break;
			}
			return node;
		}, ((FXVersionWrapper) param.getValue()).map));//TODO need to be observable
		remote.setMaxWidth(50);

		versionCol.setCellValueFactory(param ->
				Bindings.createObjectBinding(() ->
				{
					HBox parent = new HBox();
					parent.setAlignment(Pos.CENTER);
					MinecraftVersion value = param.getValue();
					String versionID = value.getVersionId();
					parent.getChildren().add(new Label(versionID));
					String type = value.getMetadata().get("type");
					if (!"release".equals(type))
					{
						Icon warning = new Icon("WARNING");
						warning.setTextFill(Color.web("#D34336"));
						warning.setScaleX(0.5);
						warning.setScaleY(0.5);
						warning.setTooltip(new Tooltip("BETA!!"));
						parent.getChildren().add(warning);
					}
					return parent;
				}, ((FXVersionWrapper) param.getValue()).map)
		);
		versionCol.setComparator((o1, o2) ->
		{
			String v1 = ((Label) ((HBox) o1).getChildren().get(0)).getText(), v2 = ((Label) ((HBox) o2).getChildren().get(0))
					.getText();
			if (!Character.isDigit(v1.charAt(0)))
				if (!Character.isDigit(v2.charAt(0))) return v1.compareTo(v2);
				else return -1;
			if (!Character.isDigit(v1.charAt(2)))
				if (!Character.isDigit(v2.charAt(2))) return v1.compareTo(v2);
				else return -1;
			return new ComparableVersion(v1).compareTo(new ComparableVersion(v2));
		});
		versionCol.setSortType(TableColumn.SortType.DESCENDING);

		updateTime.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String ver = param.getValue().getMetadata().get("remote");
			if (ver == null) return resources.getString("unknown");
			return ver;
		}, ((FXVersionWrapper) param.getValue()).map));//TODO need to be observable

		releaseTimeCol.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String ver = param.getValue().getMetadata().get("remote");
			if (ver == null) return resources.getString("unknown");
			return ver;
		}, ((FXVersionWrapper) param.getValue()).map));//TODO need to be observable
	}

	public void onConfirm()
	{
		MinecraftVersion selectedItem = this.versionTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			switch (selectedItem.getMetadata().getOrDefault("state", "remote"))
			{
				case "remote":
					confirmDownload.show(outerRoot.get());
					comboBox.hide();
					break;
				case "downloading":
				case "local":
					comboBox.setValue(selectedItem);
					comboBox.hide();
					break;
			}
	}

	public void onShow()
	{
		if (!versionTable.getItems().isEmpty() && versionTable.getSelectionModel().isEmpty())
		{
			versionTable.getSelectionModel().select(0);
			versionTable.sort();
		}
		else
		{
			MinecraftVersion value = comboBox.getValue();
			if (value != null)
			{
				int i = versionTable.getItems().indexOf(value);
				versionTable.getSelectionModel().select(i);
				versionTable.scrollTo(i);
			}
		}
	}

	public void refresh() {Shell.instance().buildAndExecute("version.refresh");}

	public void requestDownload()
	{
		MinecraftVersion selectedItem = this.versionTable.getSelectionModel().getSelectedItem();
		Shell.instance().buildAndExecute("version.fetch", selectedItem.getVersionId());
		comboBox.setValue(selectedItem);
		comboBox.hide();
		confirmDownload.close();
	}

	public void cancelDownload()
	{
		confirmDownload.close();
	}
}
