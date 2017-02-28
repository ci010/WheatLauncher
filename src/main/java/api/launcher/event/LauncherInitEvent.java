package api.launcher.event;

import api.launcher.io.IOGuard;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.Pane;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public class LauncherInitEvent extends Event
{
	public static EventType<LauncherInitEvent> LAUNCHER_INIT = new EventType<>(EventType.ROOT, "LAUNCHER_INIT");
	//	public static EventType<LauncherInitEvent> PRE = new EventType<>(LAUNCHER_INIT, "LAUNCHER_PRE_INIT");
	public static EventType<Register> REGISTER = new EventType<>(LAUNCHER_INIT, "LAUNCHER_REGISTER_INIT");
	public static EventType<Post> POST = new EventType<>(LAUNCHER_INIT, "LAUNCHER_POST_INIT");

	private Map<Class, IOGuard> registeredIO = new HashMap<>();

	public LauncherInitEvent(EventType<? extends LauncherInitEvent> type) {super(type);}

	public Map<Class, IOGuard> getRegisteredIO() {return Collections.unmodifiableMap(registeredIO);}

	public <T> boolean registerModule(Class<T> clz, IOGuard<T> guard)
	{
		Objects.requireNonNull(clz);
		Objects.requireNonNull(guard);
		if (registeredIO.containsKey(clz)) return false;
		registeredIO.put(clz, guard);
		return true;
	}

	public static class Register extends LauncherInitEvent
	{
		private Map<Class, IOGuard> registeredIO = new HashMap<>();

		public Register()
		{
			super(REGISTER);
		}

		public Map<Class, IOGuard> getRegisteredIO() {return Collections.unmodifiableMap(registeredIO);}

		public <T> boolean registerModule(Class<T> clz, IOGuard<T> guard)
		{
			Objects.requireNonNull(clz);
			Objects.requireNonNull(guard);
			if (registeredIO.containsKey(clz)) return false;
			registeredIO.put(clz, guard);
			return true;
		}
	}

	public static class Post extends LauncherInitEvent
	{
		private Pane loginPage, previewPage;

		public Post(Pane loginPage, Pane previewPage)
		{
			super(POST);
			this.loginPage = loginPage;
			this.previewPage = previewPage;
		}

		public Pane getLoginPage() {return loginPage;}

		public Pane getPreviewPage() {return previewPage;}
	}
}
