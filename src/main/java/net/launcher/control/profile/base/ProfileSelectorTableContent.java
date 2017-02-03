package net.launcher.control.profile.base;

import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTableCell;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileSelectorTableContent extends StackPane
{
	private ProfileSelector selector;

	private VBox header;
	private VBox body;

	public ProfileSelectorTableContent(ProfileSelector skinnable)
	{
		this.selector = skinnable;
		this.header = createHeader();
		this.body = createBody();
		this.getChildren().addAll(new VBox(header, body));
		this.setOnMouseClicked(Event::consume);
		this.getStyleClass().addAll("fx-mc-picker-container");
		this.bindData();
	}

	private void bindData()
	{
		ObservableList<LaunchProfile> profiles = selector.getProfiles();
		this.profileTable.getItems().addAll(profiles);
		profiles.addListener((ListChangeListener<LaunchProfile>) c ->
		{
			while (c.next())
			{
				for (LaunchProfile added : c.getAddedSubList())
					this.profileTable.getItems().add(added);
				for (LaunchProfile removed : c.getRemoved())
					this.profileTable.getItems().remove(removed);
			}
		});
		profileName.textProperty().bind(Bindings.createStringBinding(() ->
		{
			LaunchProfile value = selectionModel.getSelectedItem();
			if (value != null) return value.getDisplayName();
			return "Not Selecting";
		}, selectionModel.selectedIndexProperty()));
		version.textProperty().bind(Bindings.createStringBinding(() ->
		{
			LaunchProfile value = selectionModel.getSelectedItem();
			if (value != null)
				return value.getVersion() == null ? "No Version" : value.getVersion();
			return "No Value";
		}, selectionModel.selectedIndexProperty()));

		name.setOnEditCommit(event ->
		{
			LaunchProfile value = selector.getValue();
			if (value != null)
				if (event.getNewValue() != null && event.getNewValue().equals(""))
					value.setDisplayName(event.getNewValue());
		});

		confirm.setOnMouseReleased(event ->
		{
			selector.setValue(selectionModel.getSelectedItem());
			selector.hide();
		});
	}

	private Label profileName, createDate, version;

	private VBox createHeader()
	{
		VBox header = new VBox();
		header.getStyleClass().add("jfx-layout-heading");
		header.getStyleClass().add("title");
		header.setStyle("-fx-padding:10");

		profileName = new Label();
		createDate = new Label();
		version = new Label();
		profileName.getStyleClass().add("header-major");
		createDate.getStyleClass().add("header-minor");
		version.getStyleClass().add("header-minor");

		BorderPane pane = new BorderPane();
		pane.setLeft(createDate);
		pane.setRight(version);

		header.getChildren().addAll(profileName, pane);

		return header;
	}

	private JFXTableView<LaunchProfile> profileTable;
	private Node foot;
	private TableColumn<LaunchProfile, String> name;
	private TableView.TableViewSelectionModel<LaunchProfile> selectionModel;

	private VBox createBody()
	{
		VBox body = new VBox();
		body.setPadding(new Insets(10));

		this.profileTable = createTable();
		this.foot = createFoot();

		body.getChildren().addAll(profileTable, foot);

		return body;
	}

	private JFXTableView<LaunchProfile> createTable()
	{
		JFXTableView<LaunchProfile> profileTable = new JFXTableView<>();
		selectionModel = profileTable.getSelectionModel();
		profileTable.setEditable(true);
		profileTable.setMaxHeight(300);
		profileTable.setFixedSize(true);
		profileTable.setColumnsDraggable(false);

		name = new TableColumn<>("Name");
		name.setEditable(true);
		name.setResizable(false);
		name.setCellFactory(param -> new GenericEditableTableCell<>(new TextFieldEditorBuilder()
		{
			@Override
			public void validateValue() throws Exception
			{
				String value = getValue();
				if (value == null || value.equals(""))
					throw new IllegalArgumentException("Value cannot be null");
				boolean valid = false;
				for (int i = 0; i < value.length(); i++)
					if (value.charAt(i) != ' ')
						valid = true;
				if (!valid)
					throw new IllegalArgumentException("Value cannot be empty!");
			}
		}));
		name.setCellValueFactory(param ->
				param.getValue().displayNameProperty());

		TableColumn<LaunchProfile, String> mcVersion = new TableColumn<>("Version");
		mcVersion.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String version = param.getValue().getVersion();
			if (version == null || version.equals(""))
				version = "None";
			return version;
		}, param.getValue().versionProperty()));
		mcVersion.setEditable(true);
		mcVersion.setResizable(false);

		profileTable.getColumns().addAll(name, mcVersion);

		return profileTable;
	}

	private JFXRippler delete;
	private JFXTextField filter;
	private JFXRippler confirm;
	private JFXRippler add;

	private JFXNodesList more;

	private Node createFoot()
	{
		BorderPane pane = new BorderPane();


		delete = new JFXRippler(new Icon("CLOSE", "2em", ";", "icon"));
		add = new JFXRippler(new Icon("PLUS", "2em", ";", "icon"));
		HBox left = new HBox(delete, add);

		pane.setLeft(left);

		filter = new JFXTextField();
		filter.setPromptText("Search...");
		filter.setFocusColor(Color.ROSYBROWN);
		filter.getStyleClass().setAll("search-field");
		filter.setStyle("-fx-background-color:transparent;");
		pane.setCenter(filter);

		confirm = new JFXRippler(new Icon("CHECK", "2em", ";", "icon"));
		pane.setRight(confirm);

		return pane;
	}


}
