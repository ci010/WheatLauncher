package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.layout.StackPane;
import net.launcher.Logger;
import net.launcher.assets.MinecraftVersion;
import net.launcher.control.profile.base.ProfileTableSelector;
import net.launcher.control.versions.MinecraftVersionPicker;
import net.launcher.profile.LaunchProfile;
import net.wheatlauncher.MainApplication;

import javax.annotation.PreDestroy;


/**
 * @author ci010
 */
public class ControllerProfiles
{
	public ProfileTableSelector profile;

	public MinecraftVersionPicker versions;

	public JFXTabPane optionsTab;

	public StackPane gameSetting;

	public StackPane languageSetting;

	public StackPane resourcePackSetting;

	public StackPane modSetting;

	public JFXDialog rootDialog;

	public void initialize()
	{
		Logger.trace("init");
		rootDialog.setOverlayClose(true);
		initVersion();
		initProfile();
	}

	@PreDestroy
	public void distroy()
	{
		Logger.trace("destroy");
	}

	private void initVersion()
	{
		versions.setDataList(MainApplication.getCore().getAssetsManager().getVersions());
		versions.setDownloadRequest((version1) -> MainApplication.getCore().getAssetsManager().getRepository().fetchVersion(version1));
		versions.setRequestUpdate(() -> MainApplication.getCore().getTaskCenter().runTask(MainApplication.getCore().getAssetsManager().getRepository().refreshVersion()));

		MainApplication.getCore().getProfileManager().selectedProfileProperty().addListener(observable ->
		{
			String version = MainApplication.getCore().getProfileManager().selecting().getVersion();
			if (version != null) versions.setValue(MainApplication.getCore().getAssetsManager().getVersion(version));
		});
		LaunchProfile selecting = MainApplication.getCore().getProfileManager().selecting();
		if (selecting != null)
		{
			String version = selecting.getVersion();
			if (version != null)
				versions.setValue(MainApplication.getCore().getAssetsManager().getVersion(version));
		}
		versions.valueProperty().addListener(observable ->
		{
			MinecraftVersion value = versions.getValue();
			if (value != null)
				MainApplication.getCore().getProfileManager().selecting().setVersion(value.getVersionID());
		});
	}

	private void initProfile()
	{
		profile.setProfiles(MainApplication.getCore().getProfileManager().getAllProfiles());
		profile.setProfileFactory(param -> MainApplication.getCore().getProfileManager().newProfile(param));
		profile.setRemoveCallback(param ->
		{
			MainApplication.getCore().getProfileManager().deleteProfile(param.getId());
			return null;
		});
		LaunchProfile selecting = MainApplication.getCore().getProfileManager().selecting();
		if (selecting != null)
			profile.setValue(selecting);
		profile.valueProperty().addListener(observable ->
				MainApplication.getCore().getProfileManager().setSelectedProfile(profile.getValue().getId()));
	}
}
