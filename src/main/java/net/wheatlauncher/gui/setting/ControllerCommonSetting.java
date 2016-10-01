package net.wheatlauncher.gui.setting;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.skins.JFXSliderSkin;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.launcher.utils.ChangeListenerCache;
import net.launcher.utils.Logger;
import net.wheatlauncher.Core;
import net.wheatlauncher.LaunchProfile;
import net.wheatlauncher.gui.ColorTransitionButton;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public class ControllerCommonSetting
{
	//856x482|512 MC standard
	//1024x612 scale from MC standard(512)
	//1280x766 scale from MC standard(512)
	//1712x964|1024 MC double
	//1980x1184 scale from MC standard(512)

	//1024x768 common
	private Map<Integer, WindowSize> stageToResolution = new HashMap<>(6);

	{
		stageToResolution.put(0, new WindowSize(856, 482));
		stageToResolution.put(1, new WindowSize(1024, 612));
		stageToResolution.put(2, new WindowSize(1280, 766));
		stageToResolution.put(3, new WindowSize(1712, 1024));
		stageToResolution.put(4, new WindowSize(1980, 1184));
		stageToResolution.put(5, WindowSize.fullscreen());
	}

	public Label minecraftLocation;
	public ColorTransitionButton browsMinecraft;
	public Label javaLocation;
	public ColorTransitionButton browsJava;

	public JFXSlider memory;
	public JFXSlider resolution;

	public VBox root;

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	private ChangeListenerCache<LaunchProfile, Number> winSize = new ChangeListenerCache<>(
			profile -> (o, old, newV) -> profile.windowSizeProperty().setValue(stageToResolution.get(old.intValue()))
	);

	@PostConstruct
	public void init()
	{
		Logger.trace("init");


		Core.INSTANCE.selectedProfileProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null)
			{
				oldValue.memoryProperty().unbindBidirectional(memory.valueProperty());
			}
			newValue.memoryProperty().bindBidirectional(memory.valueProperty());
			minecraftLocation.textProperty().bind(Bindings.createStringBinding(() -> newValue.minecraftLocationProperty().getValue().getAbsolutePath(),
					newValue.minecraftLocationProperty()));
			javaLocation.textProperty().bind(Bindings.createStringBinding(() -> newValue.javaLocationProperty().getValue().getJavaPath().getAbsolutePath(),
					newValue.javaLocationProperty()));
			ChangeListener<Number> listener = winSize.listener();
			if (listener != null)
				resolution.valueProperty().removeListener(winSize.listener());
			resolution.valueProperty().addListener(winSize.listener(newValue));
		});

		resolution.skinProperty().addListener(o ->
		{
			JFXSliderSkin skin = (JFXSliderSkin) resolution.getSkin();
			try
			{
				Field sliderValue = JFXSliderSkin.class.getDeclaredField("sliderValue");
				sliderValue.setAccessible(true);
				Text text = (Text) sliderValue.get(skin);
				text.textProperty().unbind();
				text.setFont(Font.font(20));
				resolution.valueProperty().addListener(obv -> text.setText(stageToResolution.get((int) resolution.getValue()).toString()));
			}
			catch (NoSuchFieldException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		});


		browsMinecraft.setOnMouseClicked(event ->
		{
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(new File("").getAbsoluteFile());
			chooser.setTitle("Choose Minecraft root(.minecraft)");
			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
			File choose = chooser.showDialog(stage);
			if (choose == null)
				return;
			if (choose.isDirectory())
			{
				MinecraftDirectory directory = new MinecraftDirectory(choose);
				Core.INSTANCE.getCurrentProfile().minecraftLocationProperty().setValue(directory);
			}
		});

		browsJava.setOnMouseClicked(event ->
		{
			FileChooser chooser = new FileChooser();
			FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Executable files " +
					"(*.exe)", "*.exe");
			chooser.getExtensionFilters().add(extensionFilter);
			chooser.setInitialFileName("java.exe");
			chooser.setInitialDirectory(new File(System.getProperty("java.home"), "bin"));
			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
			File choose = chooser.showOpenDialog(stage);
			if (choose == null)
				return;

			if (choose.isFile() && choose.getName().equals("java.exe"))
			{
				JavaEnvironment javaEnvironment = new JavaEnvironment(choose);
				Core.INSTANCE.getCurrentProfile().javaLocationProperty().setValue(javaEnvironment);
			}
		});
	}
}
