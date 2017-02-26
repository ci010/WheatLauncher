package net.launcher.game.text.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public enum DefaultAction implements Action
{
	SHOW_TEXT("show_text", true),
	SHOW_ACHIEVEMENT("show_achievement", true),
	SHOW_ITEM("show_item", true),
	SHOW_ENTITY("show_entity", true),
	OPEN_URL("open_url", true),
	OPEN_FILE("open_file", false),
	RUN_COMMAND("run_command", true),
	SUGGEST_COMMAND("suggest_command", true),
	CHANGE_PAGE("change_page", true);

	DefaultAction(String canonicalNameIn, boolean allowedInChatIn)
	{
		this.canonicalName = canonicalNameIn;
		this.allowedInChat = allowedInChatIn;
	}

	private final boolean allowedInChat;
	private final String canonicalName;

	public boolean shouldAllowInChat()
	{
		return this.allowedInChat;
	}

	public String getCanonicalName()
	{
		return this.canonicalName;
	}

	private static final Map<String, Action> NAME_MAPPING = new HashMap<>();

	static
	{
		for (Action action : values()) NAME_MAPPING.put(action.getCanonicalName(), action);
	}

	public static Action getValueByCanonicalName(String canonicalNameIn)
	{
		return NAME_MAPPING.get(canonicalNameIn);
	}

}
