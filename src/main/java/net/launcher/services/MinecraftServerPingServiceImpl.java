package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.utils.Callbacks1;
import net.launcher.utils.MessageUtils;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callbacks;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author ci010
 */
class MinecraftServerPingServiceImpl implements MinecraftServerPingService
{
	private ExecutorService service;
	private boolean isShutDown;

	MinecraftServerPingServiceImpl(ExecutorService service)
	{
		this.service = service;
	}

	private class Task extends CallbackAdapter<ServerStatus>
	{
		Callback<ServerInfo> callback;
		SocketChannel channel;
		boolean waitPing;
		private ServerInfo info;

		Task(Callback<ServerInfo> callback, SocketChannel open, boolean waitPing, ServerInfo info)
		{
			this.callback = callback;
			this.channel = open;
			this.waitPing = waitPing;
			this.info = info;
		}

		@Override
		public void done(ServerStatus result)
		{
			if (!waitPing)
			{
				callback.done(info);
				service.submit(new PingTask(info, Callbacks.whatever(this::close), channel));
			}
			else
				service.submit(new PingTask(info, Callbacks.group(callback, Callbacks.whatever(this::close)), channel));
		}

		void close()
		{
			try {channel.close();}
			catch (IOException ignored) {}
		}

		@Override
		public void failed(Throwable t)
		{
			callback.failed(t);
			close();
		}
	}

	@Override
	public Future<ServerStatus> fetchInfo(final ServerInfo info, final Callback<ServerInfo> callback)
	{
		return fetchInfo0(info, callback, false);
	}

	@Override
	public Future<ServerStatus> fetchInfoAndWaitPing(ServerInfo info, Callback<ServerInfo> callback)
	{
		return fetchInfo0(info, callback, true);
	}

	private Future<ServerStatus> fetchInfo0(final ServerInfo info, final Callback<ServerInfo> callback, boolean waitPing)
	{
		info.setStatus(ServerStatus.pinging());
		try
		{
			SocketChannel open = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
			CallbackAdapter<ServerStatus> adapter = new Task(callback, open, waitPing, info);
			FutureTask<ServerStatus> future = Callbacks1.createWithFallback(new HandshakeTask(info, open), new
					HandshakeTaskLegacy(info, open), adapter);
			service.submit(future);
			return future;
		}
		catch (IOException e)
		{
			callback.failed(e);
			return CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public synchronized void shutdown()
	{
		if (isShutDown) return;
		service.shutdownNow();
		isShutDown = true;
		service = null;
	}

	@Override
	public boolean isShutdown()
	{
		return isShutDown;
	}

}
