package net.wheatlauncher.control.mics;

import api.launcher.Shell;
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
import net.launcher.model.MinecraftVersion;
import net.launcher.model.Profile;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class ControllerProfileChooserPane
{
	public JFXTableView<Profile> profileTable;
	public TableColumn<Profile, String> name;
	public TableColumn<Profile, String> mcVersion;
	public JFXTextField filter;

	public ResourceBundle resources;
	public Label profileName;
	public Label version;
	private ComboBoxBase<Profile> selector;

	private class ProfileWrapper implements Profile
	{
		Profile profile;

		public ProfileWrapper(Profile profile) {this.profile = profile;}

		//@formatter:off
		public String getId() {return profile.getId();}
		public int getMaxMemory() {return profile.getMaxMemory();}
		public void setMaxMemory(int maxMemory) {profile.setMaxMemory(maxMemory);}
		public int getMinMemory() {return profile.getMinMemory();}
		public void setMinMemory(int minMemory) {profile.setMinMemory(minMemory);}
		public MinecraftVersion getVersion() {return profile.getVersion();}
		public void setVersion(MinecraftVersion version) {profile.setVersion(version);}
		public String getName() {return profile.getName();}
		public void setName(String name) {profile.setName(name);}
		public WindowSize getResolution() {return profile.getResolution();}
		public void setResolution(WindowSize resolution) {profile.setResolution(resolution);}
		public JavaEnvironment getJavaLocation() {return profile.getJavaLocation();}
		public void setJavaLocation(JavaEnvironment javaLocation) {profile.setJavaLocation(javaLocation);}
		//@formatter:on
	}

	public void initialize(ComboBoxBase<Profile> selector)
	{
		this.selector = selector;
		FilteredList<Profile> filteredList = new FilteredList<>(Shell.instance().getAllProfile());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (profile) ->
		{
			String text = filter.getText();
			return text == null || text.equals("") || profile.getName().contains(text);
		}, filter.textProperty()));
		SortedList<Profile> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(profileTable.comparatorProperty());
		profileTable.setItems(sortedList);

		profileName.textProperty().bind(Bindings.createStringBinding(() ->
				Shell.instance().getProfileProxy().getName(), Shell.instance().getProfileProxy().idProperty()));
		version.textProperty().bind(Bindings.createStringBinding(() -> Shell.instance().getProfileProxy().getVersion().getVersionId(),
				Shell.instance().getProfileProxy().idProperty()));
		profileTable.getSelectionModel().select(0);
		name.setCellFactory(param -> new GenericEditableTableCell<Profile, String>(new TextFieldEditorBuilder()
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
		})
		{
			@Override
			public void commitEdit(String newValue)
			{
				super.commitEdit(newValue);
				Profile item = this.getTableView().getSelectionModel().getSelectedItem();
				item.setName(newValue);
			}
		});

		name.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String name = param.getValue().getName();
			if (name == null || name.equals(""))
				name = "Unknown";
			return name;
		}));

		mcVersion.setCellValueFactory(param -> Bindings.createStringBinding(() ->
		{
			String version = param.getValue().getVersion().getVersionId();
			if (version == null || version.equals(""))
				version = "None";
			return version;
		}));
	}

	public void add()
	{
		Shell.instance().buildAndExecuteImmediately("profile.add", resources.getString("untitled"));
		profileTable.edit(profileTable.getItems().size() - 1, name);
	}

	public void delete()
	{
		Shell.instance().buildAndExecute("profile.delete", profileTable.getSelectionModel().getSelectedItem().getId());
	}

	public void rename(TableColumn.CellEditEvent<Profile, String> event)
	{
		Profile value = event.getRowValue();
		if (value != null)
			if (event.getNewValue() != null && !event.getNewValue().equals(""))
				value.setName(event.getNewValue());
	}

	public void confirm()
	{
		selector.setValue(profileTable.getSelectionModel().getSelectedItem());
		selector.hide();
	}
}
