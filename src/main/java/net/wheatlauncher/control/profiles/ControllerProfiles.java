package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import net.launcher.Bootstrap;
import net.launcher.Logger;
import net.launcher.control.profile.base.ProfileTableSelector;
import net.launcher.control.versions.MinecraftVersionPicker;
import net.launcher.profile.LaunchProfile;
import net.launcher.version.MinecraftVersion;
import net.wheatlauncher.control.utils.FXMLInnerController;
import net.wheatlauncher.control.utils.ReloadableController;
import net.wheatlauncher.control.utils.WindowsManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;


/**
 * @author ci010
 */
@FXMLController("/fxml/profiles/Profiles.fxml")
public class ControllerProfiles implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	public ProfileTableSelector profile;

	/*Version*/
	public MinecraftVersionPicker versions;

	/*Sub-settings*/
	public JFXTabPane optionsTab;

	@FXMLInnerController
	public ControllerGameSetting gameSettingController;
	public StackPane gameSetting;

	@FXMLInnerController
	public ControllerLanguages languageSettingController;
	public StackPane languageSetting;

	@FXMLInnerController
	public ControllerResourcePackView resourcePackSettingController;
	public StackPane resourcePackSetting;

	@FXMLInnerController
	public ControllerModView modSettingController;
	public StackPane modSetting;

	/*root*/
	public JFXDialog rootDialog;

	@PostConstruct
	public void setup()
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
		versions.setDataList(Bootstrap.getCore().getAssetsManager().getVersions());
		versions.setDownloadRequest((version1) -> Bootstrap.getCore().getAssetsManager().getRepository().fetchVersion(version1));
		versions.setRequestUpdate(() ->
				Bootstrap.getCore().getService().submit(() ->
				{
					try {Bootstrap.getCore().getAssetsManager().getRepository().update();}
					catch (IOException e)
					{
						Platform.runLater(() -> flowContext.getRegisteredObject(WindowsManager.Page.class).displayError(e));
					}
				}));
		Bootstrap.getCore().getProfileManager().selectedProfileProperty().addListener(observable ->
		{
			String version = Bootstrap.getCore().getProfileManager().selecting().getVersion();
			if (version != null) versions.setValue(Bootstrap.getCore().getAssetsManager().getVersion(version));
		});
		LaunchProfile selecting = Bootstrap.getCore().getProfileManager().selecting();
		if (selecting != null)
		{
			String version = selecting.getVersion();
			if (version != null)
				versions.setValue(Bootstrap.getCore().getAssetsManager().getVersion(version));
		}
		versions.valueProperty().addListener(observable ->
		{
			MinecraftVersion value = versions.getValue();
			if (value != null)
				Bootstrap.getCore().getProfileManager().selecting().setVersion(value.getVersionID());
		});
	}

	private void initProfile()
	{
		profile.setProfiles(Bootstrap.getCore().getProfileManager().getAllProfiles());
		profile.setProfileFactory(param -> Bootstrap.getCore().getProfileManager().newProfile(param));
		profile.setRemoveCallback(param ->
		{
			Bootstrap.getCore().getProfileManager().deleteProfile(param.getId());
			return null;
		});
		LaunchProfile selecting = Bootstrap.getCore().getProfileManager().selecting();
		if (selecting != null)
			profile.setValue(selecting);
		profile.valueProperty().addListener(observable ->
				Bootstrap.getCore().getProfileManager().setSelectedProfile(profile.getValue().getId()));
	}

	@Override
	public void reload()
	{
		Logger.trace("reload");
	}

	@Override
	public void unload()
	{
	}
}
