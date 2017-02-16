package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public class BuildSettingPageEvent extends Event
{
	public static EventType<BuildSettingPageEvent> TYPE = new EventType<>("BUILD_SETTING_PAGE");
	private FXMLLoader loader;

	public BuildSettingPageEvent(FXMLLoader loader)
	{
		super(TYPE);
		this.loader = loader;
	}

	private Map<String, Pane> pages = new HashMap<>();

	public FXMLLoader getLoader() {return loader;}

	public Map<String, Pane> getPages() {return Collections.unmodifiableMap(pages);}

	/**
	 * @param pageName The LOCALIZED page name. Beware that this is an localized string. Get localized bundle by
	 *                 {@link FXMLLoader#getResources()} and get string by {@link java.util.ResourceBundle#getString(String)}
	 * @param page     The page
	 * @return if this action is successful.
	 */
	public boolean registerPage(String pageName, Pane page)
	{
		Objects.requireNonNull(pageName);
		Objects.requireNonNull(page);
		if (pages.containsKey(pageName)) return false;
		pages.put(pageName, page);
		return true;
	}
}
