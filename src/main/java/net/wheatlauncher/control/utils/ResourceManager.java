package net.wheatlauncher.control.utils;

import io.datafx.controller.ViewConfiguration;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import net.wheatlauncher.MainApplication;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class ResourceManager
{
	private static Font reg, alt;

	static
	{
		reg = Font.loadFont(ResourceManager.class.getResourceAsStream("/fonts/Minecrafter.Alt.ttf"), 120);
		System.out.println(reg.getFamily());
		alt = Font.loadFont(ResourceManager.class.getResourceAsStream("/fonts/Minecrafter.Reg.ttf"), 120);
		System.out.println(alt.getFamily());
	}

	public static void setupGlobalConfig(ViewConfiguration viewConfiguration)
	{
		ResourceBundle lang;
		try
		{
			lang = ResourceBundle.getBundle("lang", Locale.getDefault());
		}
		catch (Exception e)
		{
			lang = ResourceBundle.getBundle("lang", Locale.ENGLISH);
		}
		if (lang == null)
			throw new IllegalStateException();
		viewConfiguration.setResources(lang);
	}


	public static void setupScene(Scene scene)
	{
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
//		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-components.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
	}
}
