package net.wheatlauncher.control;

import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * @author ci010
 */
public class ControllerMain
{
	public void initialize() {}

	public void onClose(ActionEvent event) {Platform.exit();}
}
