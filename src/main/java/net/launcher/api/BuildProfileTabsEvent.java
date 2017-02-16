package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ci010
 */
public class BuildProfileTabsEvent extends Event
{
	public static final EventType<BuildProfileTabsEvent> TYPE = new EventType<>(EventType.ROOT, "BUILD_PAGE");
	private List<Tab> additionalTabs = new ArrayList<>();
	private FXMLLoader loader;

	public BuildProfileTabsEvent(FXMLLoader loader)
	{
		super(TYPE);
		this.loader = loader;
	}

	public void addTab(Tab tab)
	{
		Objects.requireNonNull(tab);
		additionalTabs.add(tab);
	}

	public FXMLLoader getLoader() {return loader;}

	public List<Tab> getAdditionalTabs() {return Collections.unmodifiableList(additionalTabs);}
}
