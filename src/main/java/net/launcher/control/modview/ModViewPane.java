package net.launcher.control.modview;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import net.launcher.services.curseforge.CurseForgeService;

import java.util.Objects;

/**
 * @author ci010
 */
public class ModViewPane extends StackPane
{
	private ScrollPane scrollControl;
	private JFXMasonryPane container;

	private ObjectProperty<CurseForgeService> service = new SimpleObjectProperty<>();

	public CurseForgeService getService() {return service.get();}

	public ObjectProperty<CurseForgeService> serviceProperty() {return service;}

	public void setService(CurseForgeService service)
	{
		Objects.requireNonNull(service);
		this.service.set(service);
	}

	protected void init()
	{
		scrollControl = new ScrollPane(container = new JFXMasonryPane());
	}

}
