package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXColorPicker;
import javafx.scene.paint.Color;

/**
 * @author ci010
 */
public class ControllerLauncherSetting
{
	public JFXColorPicker colorPicker;

	public void initialize()
	{
		colorPicker.valueProperty().addListener(observable ->
		{
			Color value = colorPicker.getValue();
			String style = "major-color:" + toRGBCode(value) + ";";
			colorPicker.getScene().getRoot().setStyle(style);// this works but still have bug...
		});
	}

	public static String toRGBCode(Color color)
	{
		return String.format("#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}
}
