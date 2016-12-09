package net.wheatlauncher.control.setting;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.FXMLInnerController;
import net.wheatlauncher.control.ReloadableController;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;


/**
 * @author ci010
 */
@FXMLController("/fxml/Setting.fxml")
public class ControllerSetting implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	/*Profile*/
	public Label profileLabel;

	public ValidationFacade validProfile;
	@FXMLInnerController(true)
	public ControllerSettingProfile profilePopupController;
	public JFXPopup profilePopup;
	public JFXRippler editProfileRegion;

	public JFXComboBox<String> profile;

	/*Version*/
	public JFXComboBox<String> versions;

	public ValidationFacade validVersion;

	/*Sub-settings*/
	public JFXListView<Label> options;
	public JFXTabPane optionsTest;

	@FXMLInnerController
	public ControllerCommonSetting commonSettingController;
	public VBox commonSetting;

	@FXMLInnerController
	public ControllerGameSetting gameSettingController;
	public VBox gameSetting;

	/*root*/
	@FXML
	private FlowPane root;

	public JFXDialog rootDialog;


	@PostConstruct
	public void setup()
	{
		Logger.trace("init");
		rootDialog.setOverlayClose(true);
		initProfilePopupMenu();
		initProfile();
		System.out.println(commonSettingController);
		System.out.println(gameSettingController);
	}

	@PreDestroy
	public void distroy()
	{
		Logger.trace("destroy");
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
			MinecraftDirectory minecraftLocation = LaunchCore.getCurrentProfile(Bootstrap.getCore()).getMinecraftLocation();

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

	private ObservableList<String> versionList;

	private void updateVersionList()
	{
		versionList.clear();
		versionList.addAll(Versions.getVersions(LaunchCore.getCurrentProfile(Bootstrap.getCore())
				.getMinecraftLocation()));
	}

	private void initVersion()
	{
		Bootstrap.getCore().getService().scheduleAtFixedRate(new SimpleFileWatcher(Paths.get("versions"),
						() -> Platform.runLater(ControllerSetting.this::updateVersionList)), 5, 5,
				TimeUnit.SECONDS);
		versionList = FXCollections.observableArrayList(Versions.getVersions(LaunchCore.getCurrentProfile(Bootstrap.getCore())
				.getMinecraftLocation()));
		versions.itemsProperty().bind(Bindings.createObjectBinding(() ->
				{
					updateVersionList();
					return versionList;
				},
				Bootstrap.getCore().selectedProperty()));
		versions.valueProperty().bind(Bindings.createStringBinding(() ->
				LaunchCore.getCurrentProfile(Bootstrap.getCore()).getVersion(), Bootstrap.getCore().selectedProperty()));
		versions.getJFXEditor().textProperty().bind(
				Bindings.createStringBinding(() -> LaunchCore.getCurrentProfile(Bootstrap.getCore()).getVersion(), Bootstrap.getCore()
						.selectedProperty()));
	}

	private void initProfile()
	{
		profile.valueProperty().bind(Bindings.createObjectBinding(() ->
				Bootstrap.getCore().getSelected(), Bootstrap.getCore().selectedProperty()));
		profile.itemsProperty().bind(Bindings.createObjectBinding(() ->
						FXCollections.observableArrayList(Bootstrap.getCore().getProfileManager().getAllProfiles().keySet()),
				Bootstrap.getCore().getProfileManager().getAllProfiles()));
		profile.selectionModelProperty().get().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> Bootstrap.getCore().setSelected(newValue));
	}

	private void initProfilePopupMenu()
	{
		rootDialog.getChildren().remove(profilePopup);
		profilePopup.setPopupContainer(rootDialog);

		try//fix the wrong close method call when pop up is showing.
		{
			Field f = JFXPopup.class.getDeclaredField("animation");
			f.setAccessible(true);
			Animation o = (Animation) f.get(profilePopup);
			o.onFinishedProperty().addListener((observable, oldValue, newValue) ->
			{
				if (o.getRate() < 0 && newValue != eventHandler)
					o.setOnFinished(eventHandler(newValue));
			});
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		profilePopup.setSource(editProfileRegion);
		editProfileRegion.setOnMouseClicked(event ->
		{
			rootDialog.setOverlayClose(false);
			profilePopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 50, 37);
		});

	}

	private EventHandler<ActionEvent> eventHandler;

	private EventHandler<ActionEvent> eventHandler(EventHandler<ActionEvent> oldValue)
	{
		return eventHandler = event ->
		{
			oldValue.handle(event);
			rootDialog.setOverlayClose(true);
		};
	}

	@Override
	public void reload()
	{
		Logger.trace("reload");
		options.setExpanded(true);
	}

	@Override
	public void unload()
	{
		Logger.trace("unload");
		profilePopup.close();
		options.setExpanded(false);
	}

	@Override
	protected void finalize() throws Throwable
	{
		System.out.println("finalize the setting");
		super.finalize();
	}
}
