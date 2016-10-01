package net.wheatlauncher.gui.setting;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import net.launcher.utils.ChangeListenerCache;
import net.launcher.utils.Logger;
import net.wheatlauncher.Core;
import net.wheatlauncher.LaunchProfile;
import net.wheatlauncher.gui.FXMLInnerController;
import net.wheatlauncher.gui.ReloadableController;
import net.wheatlauncher.utils.EventListenerUtils;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author ci010
 */
public class ControllerSetting implements ReloadableController
{
	public ValidationFacade validProfile;
	public ValidationFacade validVersion;

	public Label profileLabel;

	@FXMLInnerController(true)
	public ControllerSettingProfile profilePopupController;
	public JFXPopup profilePopup;
	public JFXRippler editProfileRegion;

	public JFXComboBox<String> profile;
	public JFXComboBox<String> versions;
	public JFXListView<Label> options;

	@FXMLInnerController
	public ControllerCommonSetting commonSettingController;
	public VBox commonSetting;

	@FXMLInnerController
	public ControllerGameSetting gameSettingController;
	public VBox gameSetting;

	public JFXTabPane optionsTest;

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private FlowPane root;

	public JFXDialog rootDialog;

	private ChangeListenerCache<LaunchProfile, MinecraftDirectory> versionCache = new ChangeListenerCache<>(
			prf -> (observable, oldValue, newValue) ->
			{
				Set<String> versions = Versions.getVersions(newValue);
				ControllerSetting.this.versions.getItems().clear();
				ControllerSetting.this.versions.getItems().addAll(versions);
				ControllerSetting.this.versions.selectionModelProperty().get().select(prf.versionProperty().getValue());
			}
	);

	private EventHandler<ActionEvent> eventHandler;

	private EventHandler<ActionEvent> eventHandler(EventHandler<ActionEvent> oldValue)
	{
		return eventHandler = event ->
		{
			oldValue.handle(event);
			rootDialog.setOverlayClose(true);
		};
	}

	@PostConstruct
	public void setup()
	{
		Logger.trace("init");
		rootDialog.setOverlayClose(true);
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


		Core.INSTANCE.selectedProfileProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null)
				oldValue.minecraftLocationProperty().removeListener(versionCache.listener());

			profile.valueProperty().bind(Bindings.createObjectBinding(() ->
							Core.INSTANCE.selectedProfileProperty().get().nameProperty().get(),
					Core.INSTANCE.selectedProfileProperty().get().nameProperty()));

			profile.itemsProperty().bind(Bindings.createObjectBinding(() ->
					{
						ListProperty<String> objects = new SimpleListProperty<>(FXCollections.observableArrayList());
						objects.addAll(Core.INSTANCE.launchProfileListProperty()
								.stream()
								.map(prof -> (prof.nameProperty().get()))
								.collect(Collectors.toList()));
						return objects;
					}, Core.INSTANCE.launchProfileListProperty(),
					Core.INSTANCE.selectedProfileProperty().get().nameProperty()));

			EventListenerUtils.addListenerAndNotify(newValue.minecraftLocationProperty(), versionCache.listener(newValue));
		});
		profile.selectionModelProperty().get().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> Core.INSTANCE.selectProfile(newValue));
	}


//	private Background small = new Background(new BackgroundFill(new Color(Color.WHEAT.getRed(), Color.WHEAT.getGreen(),
//			Color.WHEAT.getBlue(), 0.7), new CornerRadii(5),
//			new Insets(0, 0, 0, 0)));

	@Override
	public void reload()
	{
		Logger.trace("reload");
//		options.setExpanded(true);
	}

	@Override
	public void unload()
	{
		Logger.trace("unload");
		profilePopup.close();
//		options.setExpanded(false);
	}


//	Color freshColor = new Color(Color.BROWN.getRed(), Color.BROWN.getRed(), Color.BROWN.getRed(), 1);
//	Background brown = new Background(
//			new BackgroundFill(new Color(Color.BROWN.getRed(), Color.BROWN.getGreen(), Color.BROWN.getBlue(), 0.7),
//					new CornerRadii(5),
//					new Insets(-2, -2, -2, -2)));

}
