package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import org.junit.Test;

import java.util.concurrent.Future;

/**
 * @author ci010
 */
public class MinecraftServerPingServiceTest
{
	@Test
	public void fetchInfo() throws Exception
	{
		MinecraftServerPingService service = MinecraftServerPingServiceBuilder.buildDefault();
		Future<ServerStatus> localhost = service.fetchInfo(new ServerInfo("", "localhost"), null);
		ServerStatus serverStatus = localhost.get();
		System.out.println(serverStatus);
	}

}