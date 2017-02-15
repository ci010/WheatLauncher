package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.services.curseforge.CurseForgeServices;

import java.io.IOException;

/**
 * @author ci010
 */
public class ControllerCurseResourcePacks
{
	public JFXListView resourcePacks;
	public JFXTextField searchField;
	public JFXButton searchBtn;

	public void initialize() throws IOException
	{
		CurseForgeService curseForgeService = CurseForgeServices.newService(CurseForgeProjectType.TexturePacks);
	}
}
