package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXTableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import net.launcher.DownloadCenter;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
public class ControllerDownload
{
	public TableColumn downloadURL;
	public TableColumn downloadProgress;
	public JFXTableView<DownloadCenter.TaskInfo> taskTable;
	public Label downloadID;

	@PostConstruct
	public void init()
	{

	}
}
