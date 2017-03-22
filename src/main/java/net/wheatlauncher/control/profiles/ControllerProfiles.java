package net.wheatlauncher.control.profiles;

import api.launcher.Shell;
import api.launcher.profile.Profile;
import api.launcher.version.MinecraftVersion;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import net.launcher.control.ComboBoxDelegate;
import net.wheatlauncher.control.mics.ControllerMinecraftVersionChooserPane;
import net.wheatlauncher.control.mics.ControllerProfileChooserPane;


/**
 * @author ci010
 */
public class ControllerProfiles
{
	public ComboBoxDelegate<Profile> profile;
	public ComboBoxDelegate<MinecraftVersion> versions;

	public JFXTabPane optionsTab;

	public StackPane gameSetting;

	public StackPane languageSetting;

	public StackPane resourcePackSetting;

	public StackPane modSetting;

	public StackPane versionChooser;
	public ControllerMinecraftVersionChooserPane versionChooserController;
	public StackPane profileChooser;
	public ControllerProfileChooserPane profileChooserController;

	public void initialize()
	{
		versions.setOnShown(event -> versionChooserController.onShow());
		initVersion();
		initProfile();
		versionChooserController.initialize(versions, () -> (StackPane) versions.getScene().getRoot());
		profileChooserController.initialize(profile);
	}

	private void initVersion()
	{
		versions.setStringConverter(new StringConverter<MinecraftVersion>()
		{
			@Override
			public String toString(MinecraftVersion object)
			{
				if (object != null) return object.getVersionId();
				return "";
			}

			@Override
			public MinecraftVersion fromString(String string) {return null;}
		});
		profile.setStringConverter(new StringConverter<Profile>()
		{
			@Override
			public String toString(Profile object)
			{
				if (object != null) return object.getName();
				return "";
			}

			@Override
			public Profile fromString(String string) {return null;}
		});

		MinecraftVersion ver = Shell.instance().getProfileProxy().getVersion();
		if (ver != null)
			versions.setValue(ver);
		versions.valueProperty().addListener(observable -> Shell.instance().getProfileProxy().setVersion(versions.getValue()));
	}

	private void initProfile()
	{
		profile.valueProperty().addListener(observable ->
		{
			if (profile.getValue() != null)
				Shell.instance().getProfileProxy().load(profile.getValue());
		});
	}
}
