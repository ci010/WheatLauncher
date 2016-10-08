package net.launcher.services;

import net.launcher.game.ServerInfo;
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

	private abstract class Task extends CallbackAdapter<ServerInfo>
	{
		FutureTask<ServerInfo> future;
		Callback<ServerInfo> callback;
		SocketChannel open;
		boolean waitPing;

		Task(FutureTask<ServerInfo> future, Callback<ServerInfo> callback, SocketChannel open, boolean waitPing)
		{
			this.future = future;
			this.callback = callback;
			this.open = open;
			this.waitPing = waitPing;
		}

		@Override
		public void done(ServerInfo result)
		{
			if (!waitPing)
			{
				future.run();
				callback.done(result);
				service.submit(new PingTask(result, Callbacks.whatever(this::close), open));
			}
			else
				service.submit(new PingTask(result, new CallbackAdapter<ServerInfo>()
				{
					@Override
					public void done(ServerInfo result)
					{
						future.run();
						callback.done(result);
						close();
					}

					@Override
					public void failed(Throwable e) {Task.this.failed(e);}
				}, open));
		}

		protected void close()
		{
			try {open.close();}
			catch (IOException ignored) {}
		}

		@Override
		public void failed(Throwable t)
		{
			callback.failed(t);
			future.cancel(false);
			close();
		}
	}

	@Override
	public Future<ServerInfo> fetchInfo(final ServerInfo info, final Callback<ServerInfo> callback)
	{
		return fetchInfo0(info, callback, false);
	}

	private Future<ServerInfo> fetchInfo0(final ServerInfo info, final Callback<ServerInfo> callback, boolean waitPing)
	{
		info.setServerMOTD("Pinging...");
		info.setPingToServer(-1L);
		info.setPlayerList(null);
		try
		{
			SocketChannel open = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
			FutureTask<ServerInfo> future = new FutureTask<>(() -> info);
			CallbackAdapter<ServerInfo> adapter = new Task(future, callback, open, false)
			{
				@Override
				public void failed(Throwable e)
				{
					service.submit(new HandshakeTaskLegacy(info, new Task(future, callback, open, false)
					{
						@Override
						public void failed(Throwable t)
						{
							t.addSuppressed(e);
							super.failed(t);
						}
					}, open));
				}
			};
			service.submit(new HandshakeTask(info, adapter, open), info);
			return future;
		}
		catch (IOException e)
		{
			callback.failed(e);
			return CompletableFuture.completedFuture(info);
		}
	}


	@Override
	public Future<ServerInfo> fetchInfoAndWaitPing(ServerInfo info, Callback<ServerInfo> callback)
	{
		return fetchInfo0(info, callback, true);
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
