package net.launcher;

import api.launcher.EventBus;
import api.launcher.event.LauncherInitEvent;
import org.junit.Test;

/**
 * @author ci010
 */
public class FXEventBusTest
{
	@Test
	public void postEvent() throws Exception
	{
		EventBus bus = new FXEventBus();
		bus.addEventHandler(LauncherInitEvent.LAUNCHER_INIT, event ->
		{
			System.out.println("event!");
		});
	}

}
