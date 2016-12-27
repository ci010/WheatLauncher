package net.launcher.control.versions;

import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class MCVersionPicker extends JFXVersionPicker<RemoteVersion, RemoteVersionList>
{
	@Override
	public void onUpdate(Callback<RemoteVersionList> callback)
	{
		MinecraftDownloaderBuilder.buildDefault().fetchRemoteVersionList(new CallbackAdapter<RemoteVersionList>()
		{
			@Override
			public void done(RemoteVersionList result)
			{
				Platform.runLater(() ->
				{
					dataListProperty().set(result);
					callback.done(result);
				});
			}

			@Override
			public void failed(Throwable e)
			{
				Platform.runLater(() -> callback.failed(e));
			}

			@Override
			public void cancelled()
			{
				Platform.runLater(callback::cancelled);
			}
		});
	}

	@Override
	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new MCSkin(this);
	}

	static class MCSkin extends Skin<RemoteVersion, RemoteVersionList>
	{
		public MCSkin(JFXVersionPicker<RemoteVersion, RemoteVersionList> parent)
		{
			super(parent);
		}

		@Override
		protected Node defaultLabel()
		{
			return new Label("");
		}

		@Override
		protected VersionDisplayContent<RemoteVersion, RemoteVersionList> defaultContent() {return new MCVersionDisplayContent(this.parent);}

		@Override
		protected StringConverter<RemoteVersion> getConverter()
		{
			return new StringConverter<RemoteVersion>()
			{
				@Override
				public String toString(RemoteVersion object)
				{
					if (object != null) return object.getVersion();
					return "";
				}

				@Override
				public RemoteVersion fromString(String string)
				{
					if (string != null) return parent.dataListProperty().get().getVersions().get(string);
					return null;
				}
			};
		}
	}

	static class MCVersionDisplayContent extends VersionDisplayContent<RemoteVersion, RemoteVersionList>
	{
		//header labels
		private Label version;
		private Label releaseTime;
		private Label releaseType;

		public MCVersionDisplayContent(JFXVersionPicker<RemoteVersion, RemoteVersionList> picker)
		{
			super(picker);

		}

		@Override
		protected JFXTableView<?> buildTable()
		{
			TableColumn<MCVersionObj, Icon> releaseTypeIcon = new TableColumn<>("Type");
			releaseTypeIcon.setCellValueFactory(param -> param.getValue().versionIcon);
			releaseTypeIcon.setEditable(false);
			releaseTypeIcon.setResizable(false);
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
			version.setEditable(false);
			version.setResizable(false);
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
			updateTime.setEditable(false);
			updateTime.setResizable(false);
			updateTime.setContextMenu(null);
			TableColumn<MCVersionObj, String> releaseTime = new TableColumn<>("Release Time");
			releaseTime.setCellValueFactory(param -> param.getValue().releaseTime);
			releaseTime.setEditable(false);
			releaseTime.setResizable(false);


			JFXTableView<MCVersionObj> versionTable = new JFXTableView<>();
			this.picker.dataListProperty().addListener(o ->
					versionTable.getItems().setAll(this.picker.getDataList().getVersions().values().stream().map
							(MCVersionObj::new).collect(Collectors.toList())));
			versionTable.getItems().setAll(this.picker.getDataList().getVersions().values().stream().map(MCVersionObj::new)
					.collect(Collectors.toList()));
			versionTable.setFixedSize(true);
			versionTable.setColumnsDraggable(false);
			versionTable.setEditable(false);
			versionTable.getColumns().setAll(releaseTypeIcon, version, updateTime, releaseTime);
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
					return obj.getVersion().getVersion();
				return "";
			}, versionTable.getSelectionModel().selectedIndexProperty()));
			releaseType.textProperty().bind(Bindings.createStringBinding(() ->
			{
				MCVersionObj obj = (MCVersionObj) versionTable.getSelectionModel().getSelectedItem();
				if (obj != null)
					return obj.getVersion().getType();
				return "";
			}, versionTable.getSelectionModel().selectedIndexProperty()));
			releaseTime.textProperty().bind(Bindings.createStringBinding(() ->
			{
				MCVersionObj selectedItem = (MCVersionObj) versionTable.getSelectionModel().getSelectedItem();
				if (selectedItem != null)
					return DateFormat.getInstance().format((selectedItem).getVersion()
							.getReleaseTime());
				return "";
			}, versionTable.getSelectionModel().selectedIndexProperty()));
		}

		@Override
		protected void onConfirm()
		{
			MCVersionObj selectedItem = (MCVersionObj) this.versionTable.getSelectionModel().getSelectedItem();
			if (selectedItem != null)
				picker.setValue(selectedItem.getVersion());
		}

		public static class MCVersionObj extends RecursiveTreeObject<MCVersionObj>
		{
			private StringProperty version, type, releaseTime, updateTime;
			private ObjectProperty<Icon> versionIcon;
			private ObjectProperty<RemoteVersion> versionObjectProperty;

			public MCVersionObj(RemoteVersion version)
			{
				this.versionObjectProperty = new SimpleObjectProperty<>(version);
				this.version = new SimpleStringProperty("");
				this.version.bind(Bindings.createStringBinding(versionObjectProperty.get()::getVersion, versionObjectProperty));
				this.type = new SimpleStringProperty("");
				this.type.bind(Bindings.createStringBinding(versionObjectProperty.get()::getType, versionObjectProperty));
				this.releaseTime = new SimpleStringProperty("");
				this.releaseTime.bind(Bindings.createStringBinding(() ->
				{
					Date releaseTime = versionObjectProperty.get().getReleaseTime();
					if (releaseTime != null)
						return DateFormat.getInstance().format
								(releaseTime);
					return "Unknown";
				}, versionObjectProperty));
				this.updateTime = new SimpleStringProperty("");
				this.updateTime.bind(Bindings.createObjectBinding(() ->
				{
					Date uploadTime = versionObjectProperty.get().getUploadTime();
					if (uploadTime != null)
						return DateFormat.getInstance().format(uploadTime);
					return "Unknown";
				}, versionObjectProperty));
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

			public void setVersion(RemoteVersion version) {versionObjectProperty.set(version);}

			public RemoteVersion getVersion() {return versionObjectProperty.get();}
		}
	}
}
