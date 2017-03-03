package net.wheatlauncher.control.mics;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTableCell;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class ControllerProfileChooserPane
{
	public JFXTableView<LaunchProfile> profileTable;
	public TableColumn<LaunchProfile, String> name;
	public TableColumn<LaunchProfile, String> mcVersion;
	public JFXTextField filter;

	public ResourceBundle resources;
	public Label profileName;
	public Label version;
	private ComboBoxBase<LaunchProfile> selector;

	public void initialize(ComboBoxBase<LaunchProfile> selector)
	{
		this.selector = selector;
		FilteredList<LaunchProfile> filteredList =
				new FilteredList<>(ARML.core().getProfileManager().getAllProfiles());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (profile) ->
		{
			String text = filter.getText();
			return text == null || text.equals("") || profile.getDisplayName().contains(text);
		}, filter.textProperty()));
		SortedList<LaunchProfile> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(profileTable.comparatorProperty());
		profileTable.setItems(sortedList);

		profileTable.getSelectionModel().selectedIndexProperty().addListener(observable ->
		{
			if (profileTable.getSelectionModel().getSelectedItem() == null) return;
			profileName.textProperty().bind(Bindings.createStringBinding(() ->
			{
				LaunchProfile value = profileTable.getSelectionModel().getSelectedItem();
				if (value != null) return value.getDisplayName();
				return "Not Selecting";
			}, profileTable.getSelectionModel().getSelectedItem().displayNameProperty()));
			version.textProperty().bind(Bindings.createStringBinding(() ->
			{
				LaunchProfile value = profileTable.getSelectionModel().getSelectedItem();
				if (value != null)
					return value.getVersion() == null ? "No Version" : value.getVersion();
				return "No Value";
			}, profileTable.getSelectionModel().getSelectedItem().versionProperty()));
		});
		profileTable.getSelectionModel().select(0);
		name.setCellFactory(param -> new GenericEditableTableCell<>(new TextFieldEditorBuilder()
		{
			@Override
			public void validateValue() throws Exception
			{
				String value = getValue();
				System.out.println("   " + value);
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
		mcVersion.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String version = param.getValue().getVersion();
			if (version == null || version.equals(""))
				version = "None";
			return version;
		}, param.getValue().versionProperty()));
	}

	public void add()
	{
		ARML.core().getProfileManager().newProfile(resources.getString("untitled"));
		profileTable.edit(profileTable.getItems().size() - 1, name);
	}

	public void delete()
	{
		ARML.taskCenter().runSimpleTask("DeleteProfile", () -> ARML.core().getProfileManager().deleteProfile(
				profileTable.getSelectionModel().getSelectedItem().getId()));
	}

	public void rename(TableColumn.CellEditEvent<LaunchProfile, String> event)
	{
		LaunchProfile value = event.getRowValue();
		if (value != null)
			if (event.getNewValue() != null && !event.getNewValue().equals(""))
				value.setDisplayName(event.getNewValue());
	}

	public void confirm()
	{
		selector.setValue(profileTable.getSelectionModel().getSelectedItem());
		selector.hide();
	}
}
