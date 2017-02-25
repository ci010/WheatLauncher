package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.to2mbn.jmccc.option.JavaEnvironment;

import java.io.File;

/**
 * @author ci010
 */
public class ControllerCommonSetting
{
	public JFXTextField height;
	public JFXTextField width;
	//856x482|512 MC standard
	//1024x612 scale from MC standard(512)
	//1280x766 scale from MC standard(512)
	//1712x964|1024 MC double
	//1980x1184 scale from MC standard(512)

	//1024x768 common


	public JFXTextField minecraftLocation;
	public JFXButton browsMinecraft;
	public JFXTextField javaLocation;
	public JFXButton browsJava;

	public JFXTextField memory;
	public JFXSlider resolution;

	public VBox root;

	public void initialize()
	{
		LaunchProfile profile = ARML.core().getProfileManager().selecting();
		ChangeListener<String> stringChangeListener = (observable, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(newValue.replaceAll("[^\\d]", ""));
		};
		memory.textProperty().addListener(stringChangeListener);
		width.textProperty().addListener(stringChangeListener);
		height.textProperty().addListener(stringChangeListener);
//		memoryBtn.setOptions(Arrays.asList("256", "512", "1024",));
//		memory.valueProperty().set(profile.getMemory());
//		memory.valueProperty().addListener((observable, oldValue, newValue) ->
//				Bootstrap.core().getProfileManager().selecting().setMemory(newValue.intValue()));
		int value = 0;
//		for (Map.Entry<Integer, WindowSize> entry : stageToResolution.entrySet())
//			if (profile.getResolution().equals(entry.getValue()))
//				value = entry.getKey();

//		resolution.skinProperty().addListener(o ->
//		{
//			JFXSliderSkin skin = (JFXSliderSkin) resolution.getSkin();
//			try
//			{
//				Field sliderValue = JFXSliderSkin.class.getDeclaredField("sliderValue");
//				sliderValue.setAccessible(true);
//				Text text = (Text) sliderValue.get(skin);
//				text.textProperty().unbind();
//				text.setFont(Font.font(20));
//				resolution.valueProperty().addListener(obv -> text.setText(stageToResolution.get((int) resolution.getValue()).toString()));
//			}
//			catch (NoSuchFieldException | IllegalAccessException e)
//			{
//				e.printStackTrace();
//			}
//		});
//		resolution.valueProperty().set(value);
//		resolution.valueProperty().addListener((observable, oldValue, newValue) ->
//				Bootstrap.core().getProfileManager().selecting().setResolution(stageToResolution.getOrDefault(newValue, WindowSize.fullscreen())));

//		minecraftLocation.textProperty().bind(Bindings.createStringBinding(() -> Bootstrap.core().getProfileManager().selecting()
//						.getMinecraftLocation().getRoot().getAbsolutePath(),
//				Bootstrap.core().getProfileManager().selecting().minecraftLocationProperty()));
		javaLocation.textProperty().bind(Bindings.createStringBinding(() ->
						ARML.core().getProfileManager().selecting().getJavaEnvironment().getJavaPath().getAbsolutePath(),
				ARML.core().getProfileManager().selecting().javaEnvironmentProperty()));

		ARML.core().getProfileManager().selectedProfileProperty().addListener(o ->
		{
			LaunchProfile p = ARML.core().getProfileManager().selecting();

			javaLocation.textProperty().bind(Bindings.createStringBinding(() -> ARML.core().getProfileManager().selecting().
					getJavaEnvironment().getJavaPath().getAbsolutePath(), (Observable) ARML.core().getProfileManager().selecting()));
		});


//		browsMinecraft.sd

		browsJava.setOnMouseClicked(event ->
		{
			FileChooser chooser = new FileChooser();
			FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Executable files " +
					"(*.exe)", "*.exe");
			chooser.getExtensionFilters().add(extensionFilter);
			chooser.setInitialFileName("java.exe");
			chooser.setInitialDirectory(new File(System.getProperty("java.home"), "bin"));
			Stage stage = (Stage) browsJava.getScene().getWindow();
			File choose = chooser.showOpenDialog(stage);
			if (choose == null)
				return;

			if (choose.isFile() && choose.getName().equals("java.exe"))
			{
				JavaEnvironment javaEnvironment = new JavaEnvironment(choose);
				ARML.core().getProfileManager().selecting().setJavaEnvironment(javaEnvironment);
			}
		});
	}
}
