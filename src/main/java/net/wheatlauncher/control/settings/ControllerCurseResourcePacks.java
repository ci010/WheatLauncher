package net.wheatlauncher.control.settings;

import api.launcher.ARML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.ScrollPane;
import net.launcher.control.modview.CurseForgeViewSlider;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.services.curseforge.CurseForgeServices;

/**
 * @author ci010
 */
public class ControllerCurseResourcePacks
{
	//	public JFXListView<CurseForgeProject> resourcePacks;
	public JFXTextField searchField;
	public JFXButton searchBtn;
	public ScrollPane root;
	public CurseForgeViewSlider slider;


	private CurseForgeService service;

	public void initialize()
	{
//		resourcePacks.setCellFactory(param -> new JFXListCell<CurseForgeProject>()
//		{
//			@Override
//			public void updateItem(CurseForgeProject item, boolean empty)
//			{
//				super.updateItem(item, empty);
//				if (item == null || empty) setGraphic(null);
//				else
//				{
//					setGraphic(new CurseForgeProjectCard(item));
//				}
//			}
//		});
		ARML.taskCenter().runTask(new Task<CurseForgeService.Cache<CurseForgeProject>>()
		{
			@Override
			protected CurseForgeService.Cache<CurseForgeProject> call() throws Exception
			{
				service = CurseForgeServices.newService
						(CurseForgeProjectType.TexturePacks);
				return service.view(null);
			}
		}).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
		{
			CurseForgeService.Cache<CurseForgeProject> cache = (CurseForgeService.Cache<CurseForgeProject>) event
					.getSource().getValue();
			slider.setProjectCache(cache);
			System.out.println("set cache");
		});
	}

	public void search()
	{
//		String text = searchField.getText();
	}
}
