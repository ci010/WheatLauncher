package net.launcher.control.versions;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.launcher.utils.Tasks;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.wheatlauncher.utils.LanguageMap;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;

import java.text.DateFormat;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class MinecraftVersionDisplayContent extends Region
{
	protected MinecraftVersionPicker picker;

	protected StackPane root;
	protected HBox containerParent;
	protected VBox bookMarkContainer;
	protected VBox mainDisplayContainer;
	protected VBox header;
	protected VBox content;

	protected JFXTextField filter;

	private JFXSpinner spinner;

	//contents
	protected JFXTableView<?> versionTable;
	protected JFXRippler confirm, refresh;
	protected JFXToggleNode showAlpha;
	//header labels
	private Label version;
	private Label releaseTime;
	private Label releaseType;

	public MinecraftVersionDisplayContent(MinecraftVersionPicker picker)
	{
		this.picker = picker;

		this.root = new StackPane();
		this.root.setOnMouseClicked(Event::consume);
		this.root.setAlignment(Pos.CENTER);
		this.getChildren().add(root);

		this.containerParent = new HBox();
		this.mainDisplayContainer = new VBox();
		this.bookMarkContainer = new VBox();
		this.header = new VBox();
		this.content = new VBox();
		this.content.setAlignment(Pos.CENTER);

		this.mainDisplayContainer.getChildren().add(header);
		this.mainDisplayContainer.getChildren().add(content);
		this.mainDisplayContainer.setAlignment(Pos.CENTER);
		this.mainDisplayContainer.setStyle("-fx-padding:5; -fx-background-color:#009688");

		this.containerParent.getChildren().add(mainDisplayContainer);

		this.spinner = new JFXSpinner();

		this.root.getChildren().add(mainDisplayContainer);

		header.getStyleClass().add("jfx-layout-heading");
		header.getStyleClass().add("title");
		header.setStyle("-fx-padding:10");

		setupHeader();
		setupContent();
		bindData();

		refresh.setOnMouseClicked(event -> refresh());
		confirm.setOnMouseClicked(event -> onConfirm());
	}


	private void refresh()
	{
		if (!root.getChildren().contains(spinner))
			root.getChildren().add(spinner);
		this.mainDisplayContainer.setDisable(true);
		this.picker.onUpdate(Tasks.whatever(
				() ->
				{
					root.getChildren().remove(spinner);
					mainDisplayContainer.setDisable(false);
				}));
	}

	protected void setupContent()
	{
		this.versionTable = buildTable();
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
		this.filter.setFocusColor(Color.ROSYBROWN);
		this.filter.getStyleClass().setAll("search-field");
		this.filter.setStyle("-fx-background-color:transparent;");
		BorderPane btnContainer = new BorderPane();
		btnContainer.setLeft(new HBox(refresh, showAlpha));
		btnContainer.setCenter(filter);
		btnContainer.setRight(confirm);

//		setupTabs();
		this.content.getChildren().setAll(versionTable, btnContainer);
	}

	protected JFXTableView<?> buildTable()
	{
		TableColumn<MCVersionObj, Icon> releaseTypeIcon = new TableColumn<>("Type");
		releaseTypeIcon.setCellValueFactory(param -> param.getValue().versionIcon);
		releaseTypeIcon.setComparator((o1, o2) ->
		{
			String a = o1.getTooltip().getText();
			String b = o2.getTooltip().getText();
			if (a.equals("release"))
				if (b.equals("release")) return 0;
				else return 1;
			return a.compareTo(b);
		});
		releaseTypeIcon.setMaxWidth(50);
		TableColumn<MCVersionObj, String> version = new TableColumn<>("Version");
		version.setCellValueFactory(param ->
				param.getValue().version);
		version.setComparator((o1, o2) ->
		{
			if (!Character.isDigit(o1.charAt(0)))
				if (!Character.isDigit(o2.charAt(0))) return o1.compareTo(o2);
				else return -1;
			if (!Character.isDigit(o1.charAt(2)))
				if (!Character.isDigit(o2.charAt(2))) return o1.compareTo(o2);
				else return -1;
			return new ComparableVersion(o1).compareTo(new ComparableVersion(o2));

		});
		version.setSortType(TableColumn.SortType.DESCENDING);

		TableColumn<MCVersionObj, String> updateTime = new TableColumn<>("Update Time");
		updateTime.setCellValueFactory(param -> param.getValue().updateTime);
		TableColumn<MCVersionObj, String> releaseTime = new TableColumn<>("Release Time");
		releaseTime.setCellValueFactory(param -> param.getValue().releaseTime);

		TableColumn<MCVersionObj, Icon> remote = new TableColumn<>("Status");
		remote.setCellValueFactory(param ->
				{
					Icon icon;
					if (param.getValue().isRemote.getValue())
					{
						icon = new Icon("DOWNLOAD");
						Tooltip tooltip = new Tooltip("Need to be downloaded");
						icon.setTooltip(tooltip);
					}
					else
					{
						icon = new Icon("DRAWER");
						icon.setTooltip(new Tooltip("Already in storage"));
					}
					return new SimpleObjectProperty<>(icon);
				}
		);

		JFXTableView<MCVersionObj> versionTable = new JFXTableView<>();
		this.picker.dataListProperty().addListener(o ->
		{
			RemoteVersionList dataList = this.picker.getDataList();
			if (dataList != null)
				versionTable.setItems(FXCollections.observableArrayList(this.picker.getDataList().getVersions().values().stream().map
						(MCVersionObj::new)
						.collect(Collectors.toList())));
		});
		RemoteVersionList dataList = this.picker.getDataList();
		if (dataList != null)
			versionTable.setItems(FXCollections.observableArrayList(this.picker.getDataList().getVersions().values()
					.stream().map
							(MCVersionObj::new)
					.collect(Collectors.toList())));
		versionTable.setFixedSize(true);
		versionTable.setColumnsDraggable(false);
		versionTable.setEditable(false);
		versionTable.getColumns().setAll(releaseTypeIcon, version, updateTime, releaseTime, remote);
		for (TableColumn<MCVersionObj, ?> column : versionTable.getColumns())
		{
			column.setEditable(false);
			column.setResizable(false);
		}
		return versionTable;
	}

	protected void setupHeader()
	{
		//version//
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
		releaseTime = new Label();
		releaseTime.getStyleClass().add("spinner-label");
		releaseTime.setTextFill(Color.rgb(255, 255, 255, 0.67));
		releaseTime.setFont(Font.font("Roboto", FontWeight.BOLD, 14));

		releaseType = new Label();
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
	}

	protected void bindData()
	{
		version.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MCVersionObj obj = (MCVersionObj) versionTable.getSelectionModel().getSelectedItem();
			if (obj != null)
				return obj.version.get();
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseType.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MCVersionObj obj = (MCVersionObj) versionTable.getSelectionModel().getSelectedItem();
			if (obj != null)
				return obj.type.get();
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));
		releaseTime.textProperty().bind(Bindings.createStringBinding(() ->
		{
			MCVersionObj selectedItem = (MCVersionObj) versionTable.getSelectionModel().getSelectedItem();
			if (selectedItem != null)
				return selectedItem.releaseTime.get();
			return "";
		}, versionTable.getSelectionModel().selectedIndexProperty()));

		filter.textProperty().addListener((o, oldV, newV) ->
		{
			if (newV == null || "".equals(newV))
				versionTable.getFilterMap().remove("key");
			else
				versionTable.getFilterMap().put("key", obj ->
				{
					MCVersionObj versionObj = (MCVersionObj) obj;
					return versionObj.type.get().contains(newV) || versionObj.version.get().contains(newV);
				});
		});
		InvalidationListener listener = o ->
		{
			if (!showAlpha.isSelected())
				versionTable.getFilterMap().put("type", p ->
				{
					MCVersionObj versionObj = (MCVersionObj) p;
					return versionObj.type.get().equals("release");
				});
			else
				versionTable.getFilterMap().remove("type");
		};
		showAlpha.selectedProperty().addListener(listener);
		listener.invalidated(null);
	}

	protected void onConfirm()
	{
		MCVersionObj selectedItem = (MCVersionObj) this.versionTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			picker.setValue(selectedItem.getVersion());
	}

	public static class MCVersionObj
	{
		private StringProperty version, type, releaseTime, updateTime;
		private ObjectProperty<Icon> versionIcon;
		private ObjectProperty<RemoteVersion> versionObject;
		private BooleanProperty isRemote;

		public MCVersionObj(RemoteVersion version) {this(version, true);}

		public MCVersionObj(RemoteVersion version, boolean isRemote)
		{
			this.isRemote = new SimpleBooleanProperty(isRemote);
			this.versionObject = new SimpleObjectProperty<>(version);
			this.version = new SimpleStringProperty("");
			this.version.bind(Bindings.createStringBinding(versionObject.get()::getVersion, versionObject));
			this.type = new SimpleStringProperty("");
			this.type.bind(Bindings.createStringBinding(versionObject.get()::getType, versionObject));
			this.releaseTime = new SimpleStringProperty("");
			this.releaseTime.bind(Bindings.createStringBinding(() ->
			{
				Date releaseTime = versionObject.get().getReleaseTime();
				if (releaseTime != null)
					return DateFormat.getInstance().format
							(releaseTime);
				return "Unknown";
			}, versionObject));
			this.updateTime = new SimpleStringProperty("");
			this.updateTime.bind(Bindings.createObjectBinding(() ->
			{
				Date uploadTime = versionObject.get().getUploadTime();
				if (uploadTime != null)
					return DateFormat.getInstance().format(uploadTime);
				return "Unknown";
			}, versionObject));
			this.versionIcon = new SimpleObjectProperty<>();
			this.versionIcon.bind(Bindings.createObjectBinding(() ->
			{
				String value = type.getValue();
				Icon icon = new Icon("CIRCLE");
				JFXDepthManager.setDepth(icon, 1);
				if (value.equals("release")) icon.setTextFill(Color.valueOf("#01A05E"));
				else icon.setTextFill(Color.DARKRED);
				icon.setTooltip(new Tooltip(value));
				return icon;
			}, type));
		}

		public RemoteVersion getVersion() {return versionObject.get();}
	}
}
