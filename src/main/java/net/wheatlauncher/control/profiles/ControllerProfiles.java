package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import net.launcher.api.ARML;
import net.launcher.assets.MinecraftVersion;
import net.launcher.control.ComboBoxDelegate;
import net.launcher.profile.LaunchProfile;
import net.wheatlauncher.control.mics.ControllerMinecraftVersionChooserPane;
import net.wheatlauncher.control.mics.ControllerProfileChooserPane;


/**
 * @author ci010
 */
public class ControllerProfiles
{
	public ComboBoxDelegate<LaunchProfile> profile;
	public ComboBoxDelegate<MinecraftVersion> versions;

	public JFXTabPane optionsTab;

	public StackPane gameSetting;

	public StackPane languageSetting;

	public StackPane resourcePackSetting;

	public StackPane modSetting;

	public JFXDialog rootDialog;
	public StackPane versionChooser;
	public ControllerMinecraftVersionChooserPane versionChooserController;
	public StackPane profileChooser;
	public ControllerProfileChooserPane profileChooserController;

	public void initialize()
	{
		rootDialog.setOverlayClose(true);
		versions.setOnShown(event -> versionChooserController.onShow());
		initVersion();
		initProfile();
		versionChooserController.initialize(versions, () -> (StackPane) rootDialog.getScene().getRoot());
		profileChooserController.initialize(profile);
	}

	private void initVersion()
	{
		versions.setStringConverter(new StringConverter<MinecraftVersion>()
		{
			@Override
			public String toString(MinecraftVersion object)
			{
				if (object != null)
					return object.toString();
				return "";
			}

			@Override
			public MinecraftVersion fromString(String string)
			{
				return null;
			}
		});
		profile.setStringConverter(new StringConverter<LaunchProfile>()
		{
			@Override
			public String toString(LaunchProfile object)
			{
				if (object != null)
					return object.getDisplayName();
				return "";
			}

			@Override
			public LaunchProfile fromString(String string)
			{
				return null;
			}
		});
		ARML.core().getProfileManager().selectedProfileProperty().addListener(observable ->
		{
			String version = ARML.core().getProfileManager().selecting().getVersion();
			if (version != null) versions.setValue(ARML.core().getAssetsManager().getVersion(version));
		});
		LaunchProfile selecting = ARML.core().getProfileManager().selecting();
		if (selecting != null)
		{
			MinecraftVersion mcVersion = selecting.getMcVersion();
			if (mcVersion != null)
				versions.setValue(mcVersion);
		}
		versions.valueProperty().addListener(observable ->
		{
			MinecraftVersion value = versions.getValue();
			if (value != null)
				ARML.core().getProfileManager().selecting().setVersion(value.getVersionID());
		});
	}

	private void initProfile()
	{
		LaunchProfile selecting = ARML.core().getProfileManager().selecting();
		if (selecting != null) profile.setValue(selecting);
		profile.valueProperty().addListener(observable ->
		{
			if (profile.getValue() != null)
				ARML.core().getProfileManager().setSelectedProfile(profile.getValue().getId());
		});
	}
}
