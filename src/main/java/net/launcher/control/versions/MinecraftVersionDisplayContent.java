package net.launcher.control.versions;

import com.jfoenix.controls.*;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.launcher.assets.MinecraftVersion;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.wheatlauncher.utils.LanguageMap;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;

import java.util.function.Predicate;

/**
 * @author ci010
 */
public class MinecraftVersionDisplayContent extends StackPane
{
	protected MinecraftVersionPicker picker;
	protected Node content;

	public MinecraftVersionDisplayContent(MinecraftVersionPicker picker)
	{
		this.picker = picker;

		this.setAlignment(Pos.CENTER);
		this.setStyle("-fx-padding:10;");
		this.getStyleClass().addAll("fx-mc-picker-container");
		VBox header = setupHeader();
		this.content = setupContent();
		Node foot = setupFoot();

		VBox box = new VBox(header, content, foot);
		box.setSpacing(10);
		this.getChildren().add(box);

		this.bindData();
	}

	private JFXSpinner spinner = new JFXSpinner();

	private JFXTextField filter;

	//contents
	private JFXRippler confirm, refresh;
	private JFXToggleNode showAlpha;

	private Node setupFoot()
	{
		//setup refresh
		this.refresh = new JFXRippler(new Icon("REFRESH", "2em", ";", "icon"));
		//setup onConfirm
		this.confirm = new JFXRippler(new Icon("CHECK", "2em", ";", "icon"));
		//setup showalpha
		this.showAlpha = new JFXToggleNode();
		this.showAlpha.maxHeightProperty().bind(confirm.maxHeightProperty());
		this.showAlpha.maxWidthProperty().bind(confirm.maxWidthProperty());
		this.showAlpha.prefHeightProperty().bind(confirm.prefHeightProperty());
		this.showAlpha.prefWidthProperty().bind(confirm.prefWidthProperty());
		this.showAlpha.resize(refresh.getWidth(), refresh.getHeight());
		this.showAlpha.setGraphic(new Icon("GAVEL", "2em", ";", "icon"));
		this.showAlpha.setTooltip(new Tooltip(LanguageMap.INSTANCE.translate("showAlpha")));
		//setup view
		this.filter = new JFXTextField();
		this.filter.setPromptText("Search...");
		this.filter.setFocusColor(Color.TOMATO);
		this.filter.getStyleClass().setAll("search-field");
		this.filter.setStyle("-fx-background-color:transparent;");

		BorderPane btnContainer = new BorderPane();
		btnContainer.setLeft(new HBox(refresh, showAlpha));
		btnContainer.setCenter(filter);
		btnContainer.setRight(confirm);

		return btnContainer;
	}

	private JFXTableView<MinecraftVersion> versionTable;

	private Node setupContent()
	{
		this.versionTable = buildTable();
		return versionTable;
	}

	private JFXTableView<MinecraftVersion> buildTable()
	{
		TableColumn<MinecraftVersion, Node> remote = new TableColumn<>("Status");
		remote.setCellValueFactory(param -> Bindings.createObjectBinding(() ->
		{
			Node node;
			switch (param.getValue().getState())
			{
				case DOWNLOADING:
					node = new JFXSpinner();
					break;
				case REMOTE:
					Icon ic = new Icon("CLOUD");
					ic.setTooltip(new Tooltip("Need to be downloaded"));
					node = ic;
					break;
				default:
				case LOCAL:
					ic = new Icon("FOLDER");
					ic.setTooltip(new Tooltip("Already in storage"));
					node = ic;
					break;
			}
			return node;
		}, param.getValue().stateProperty()));
		remote.setMaxWidth(50);

		TableColumn<MinecraftVersion, Node> version = new TableColumn<>("Version");
		version.setCellValueFactory(param ->
				Bindings.createObjectBinding(() ->
				{
					HBox parent = new HBox();
					parent.setAlignment(Pos.CENTER);
					MinecraftVersion value = param.getValue();
					String versionID = value.getVersionID();
					Object re = value.getMetadata().get("remote");
					parent.getChildren().add(new Label(versionID));
					if (re != null)
					{
						RemoteVersion remoteVersion = (RemoteVersion) re;
						String type = remoteVersion.getType();
						if (!type.equals("release"))
						{
							Icon warning = new Icon("WARNING");
							warning.setTextFill(Color.web("#D34336"));
							warning.setScaleX(0.5);
							warning.setScaleY(0.5);
							warning.setTooltip(new Tooltip("BETA!!"));
							parent.getChildren().add(warning);
						}
					}
					return parent;
				}, param.getValue().versionIDProperty())
		);
		version.setComparator((o1, o2) ->
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
		version.setSortType(TableColumn.SortType.DESCENDING);

		TableColumn<MinecraftVersion, String> updateTime = new TableColumn<>("Update Time");
		updateTime.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			RemoteVersion ver = (RemoteVersion) param.getValue().getMetadata().get("remote");
			if (ver == null) return "Unknown";
			return ver.getUploadTime().toString();
		}, param.getValue().getMetadata()));

		TableColumn<MinecraftVersion, String> releaseTime = new TableColumn<>("Release Time");
		releaseTime.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			RemoteVersion ver = (RemoteVersion) param.getValue().getMetadata().get("remote");
			if (ver == null) return "Unknown";
			return ver.getReleaseTime().toString();
		}, param.getValue().getMetadata()));

		JFXTableView<MinecraftVersion> versionTable = new JFXTableView<>();

		versionTable.setFixedSize(true);
		versionTable.setColumnsDraggable(false);
		versionTable.setEditable(true);
		versionTable.getColumns().setAll(remote, version, updateTime, releaseTime);
		for (TableColumn<MinecraftVersion, ?> column : versionTable.getColumns())
		{
			column.setEditable(false);
			column.setResizable(false);
		}
		return versionTable;
	}

	private Label version;
	private Label releaseTime = new Label();
	private Label releaseType = new Label();

	private VBox setupHeader()
	{
		//version//
		VBox header = new VBox();
		header.getStyleClass().add("jfx-layout-heading");
		header.getStyleClass().add("title");
		header.setStyle("-fx-padding:10");

		version = new Label();
		version.getStyleClass().add("spinner-label");
		version.setTextFill(Color.WHITE);
		version.setFont(Font.font("Roboto", FontWeight.BOLD, 32));

		HBox versionContainer = new HBox();
		versionContainer.getStyleClass().add("spinner");
		versionContainer.setStyle("-fx-background-color:transparent");
		versionContainer.getChildren().addAll(version);
		versionContainer.setAlignment(Pos.CENTER_LEFT);

		//release//
		releaseTime.getStyleClass().add("spinner-label");
		releaseTime.setTextFill(Color.rgb(255, 255, 255, 0.67));
		releaseTime.setFont(Font.font("Roboto", FontWeight.BOLD, 14));

		releaseType.getStyleClass().add("spinner-label");
		releaseType.setTextFill(Color.rgb(255, 255, 255, 0.67));
		releaseType.setFont(Font.font("Roboto", FontWeight.BOLD, 14));

		BorderPane releaseInfo = new BorderPane();
		releaseInfo.getStyleClass().add("spinner");
		releaseInfo.setLeft(releaseTime);
		releaseInfo.setStyle("-fx-background-color:transparent");
		releaseInfo.setRight(releaseType);

		//overall
		header.getChildren().setAll(versionContainer, releaseInfo);
		return header;
	}

	private void bindData()
	{
		FilteredList<MinecraftVersion> filteredList = new FilteredList<>(picker.getDataList());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(
				() -> (Predicate<MinecraftVersion>) version ->
				{
					if (filter.getText() != null && !filter.getText().equals(""))
						if (!version.getVersionID().contains(filter.getText())) return false;
					Object temp = version.getMetadata().get("remote");
					return temp == null || showAlpha.isSelected() || ((RemoteVersion) temp).getType().equals("release");
				},
				filter.textProperty(), showAlpha.selectedProperty()
		));
		SortedList<MinecraftVersion> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(versionTable.comparatorProperty());
		versionTable.setItems(sortedList);

		version.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null) return obj.getVersionID();
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseType.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null)
			{
				Object o = obj.getMetadata().get("remote");
				if (o != null)
				{
					RemoteVersion rv = (RemoteVersion) o;
					return rv.getType();
				}
			}
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseTime.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion obj = versionTable.getSelectionModel().getSelectedItem();
			if (obj != null)
			{
				Object o = obj.getMetadata().get("remote");
				if (o != null)
				{
					RemoteVersion rv = (RemoteVersion) o;
					return rv.getReleaseTime().toString();
				}
			}
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));

		refresh.setOnMouseReleased(event -> refresh());
		confirm.setOnMouseReleased(event -> onConfirm());

		this.picker.dataListProperty().addListener((InvalidationListener) observable ->
		{
//			if (getChildren().contains(spinner))
//				getChildren().remove(spinner);
//			if (content.isDisable())
//				this.content.setDisable(false);
		});
	}

	private JFXDialog confirmDownload;
	private JFXButton download, cancelDownload;

	private JFXDialog getConfirmDownload()
	{
		if (confirmDownload == null)
		{
			JFXDialogLayout layout = new JFXDialogLayout();
			layout.setHeading(new Label("Download Request"));
			layout.setBody(new Label("The version files doesn't appear in local storage. We need download it."));
			download = new JFXButton("download");
			cancelDownload = new JFXButton("cancel");
			layout.setActions(download, cancelDownload);
			confirmDownload = new JFXDialog(this, layout, JFXDialog.DialogTransition.CENTER);

			download.setOnAction(event ->
			{
				MinecraftVersion selectedItem = this.versionTable.getSelectionModel().getSelectedItem();
				picker.getDownloadRequest().accept(selectedItem);
				picker.setValue(selectedItem);
				picker.hide();
				confirmDownload.close();
			});
			cancelDownload.setOnAction(event -> confirmDownload.close());
		}
		return confirmDownload;
	}

	private void onConfirm()
	{
		MinecraftVersion selectedItem = this.versionTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			switch (selectedItem.getState())
			{
				case REMOTE:
					getConfirmDownload().show();
					break;
				case DOWNLOADING:
				case LOCAL:
					picker.setValue(selectedItem);
					picker.hide();
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
			MinecraftVersion value = picker.getValue();
			if (value != null)
			{
				int i = versionTable.getItems().indexOf(value);
				versionTable.getSelectionModel().select(i);
				versionTable.scrollTo(i);
			}
		}
	}

	private void refresh()
	{
//		if (!getChildren().contains(spinner))
//			getChildren().add(spinner);
//		this.content.setDisable(true);
		Runnable requestUpdate = this.picker.getRequestUpdate();
		if (requestUpdate != null) requestUpdate.run();
	}
}
