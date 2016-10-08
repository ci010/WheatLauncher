package net.launcher.services;
/**
 * @author ci010
 */

import javafx.util.Builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinecraftServerPingServiceBuilder implements Builder<MinecraftServerPingService>
{
	public static MinecraftServerPingServiceBuilder create() {return new MinecraftServerPingServiceBuilder();}

	public static MinecraftServerPingService buildDefault() {return create().build();}

	@Override
	public MinecraftServerPingService build()
	{
		return new MinecraftServerPingServiceImpl(service == null ? Executors.newFixedThreadPool(5) : service);
	}

	private ExecutorService service;

	public MinecraftServerPingServiceBuilder setExecutor(ExecutorService service)
	{
		this.service = service;
		return this;
	}

	private MinecraftServerPingServiceBuilder() {}
}
