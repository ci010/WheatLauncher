package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.Logger;
import net.launcher.control.profile.base.ProfileTableSelector;
import net.launcher.control.versions.MinecraftVersionPicker;
import net.launcher.profile.LaunchProfile;
import net.launcher.version.MinecraftVersion;
import net.wheatlauncher.control.utils.FXMLInnerController;
import net.wheatlauncher.control.utils.ReloadableController;
import net.wheatlauncher.control.utils.WindowsManager;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;


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
	@FXML
	private VBox root;

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
		versions.setDataList(Bootstrap.getCore().getVersionManager().getVersions());
		versions.setDownloadRequest(Bootstrap.getCore().getVersionManager().getRepository()::fetchVersion);
		versions.setRequestUpdate(() ->
		{
			try {Bootstrap.getCore().getVersionManager().getRepository().update();}
			catch (IOException e) {flowContext.getRegisteredObject(WindowsManager.Page.class).displayError(e);}
		});
		Bootstrap.getCore().getProfileManager().selectedProfileProperty().addListener(observable ->
		{
			String version = Bootstrap.getCore().getProfileManager().selecting().getVersion();
			if (version != null) versions.setValue(Bootstrap.getCore().getVersionManager().getVersion(version));
		});
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

	private class SimpleFileWatcher implements Runnable
	{
		private FileTime last;
		private Path sub;
		private Runnable runnable;

		public SimpleFileWatcher(Path sub, Runnable runnable)
		{
			this.sub = sub;
			this.runnable = runnable;
		}

		@Override
		public void run()
		{
			MinecraftDirectory minecraftLocation = Bootstrap.getCore().getProfileManager().selecting().getMinecraftLocation();

			try
			{
				FileTime lastModifiedTime = Files.getLastModifiedTime(minecraftLocation.getRoot().toPath().resolve(sub));
				if (last == null) last = lastModifiedTime;
				else
				{
					if (last.compareTo(lastModifiedTime) < 0)
						runnable.run();
				}
			}
			catch (IOException e) {}
		}
	}
}
