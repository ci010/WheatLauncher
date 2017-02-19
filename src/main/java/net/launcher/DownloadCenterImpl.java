package net.launcher;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import org.to2mbn.jmccc.mcdownloader.CacheOption;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloadOption;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.combine.CombinedDownloadTask;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.mcdownloader.provider.MinecraftDownloadProvider;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.util.TreeMap;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public class DownloadCenterImpl implements DownloadCenter
{
	private ObservableMap<String, ObservableList<TaskInfo>> downloadMap = FXCollections.observableMap(new TreeMap<>());

	class DownloaderWrapper implements MinecraftDownloader
	{
		private String name;
		private MinecraftDownloader delegate;
		private ObservableList<TaskInfo> list;

		DownloaderWrapper(String name, MinecraftDownloader delegate, ObservableList<TaskInfo> list)
		{
			this.name = name;
			this.delegate = delegate;
			this.list = list;
		}

		@Override
		public Future<Version> downloadIncrementally(MinecraftDirectory dir, String version, CombinedDownloadCallback<Version> callback, MinecraftDownloadOption... options)
		{
			return delegate.downloadIncrementally(dir, version, new CallbackAdapter<Version>()
			{
				@Override
				public void done(Version result)
				{
					if (callback != null)
						callback.done(result);
				}

				@Override
				public void failed(Throwable e)
				{
					if (callback != null)
						callback.failed(e);
				}

				@Override
				public void cancelled()
				{
					if (callback != null)
						callback.cancelled();
				}

				@Override
				public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
				{
					if (callback != null)
						return wrap(task.getURI().toString(), callback.taskStart(task));
					return null;
				}
			}, options);
		}

		@Override
		public Future<RemoteVersionList> fetchRemoteVersionList(CombinedDownloadCallback<RemoteVersionList> callback, CacheOption... options)
		{
			return delegate.fetchRemoteVersionList(new CallbackAdapter<RemoteVersionList>()
			{
				@Override
				public void done(RemoteVersionList result)
				{
					if (callback != null)
						callback.done(result);
				}

				@Override
				public void failed(Throwable e)
				{
					if (callback != null)
						callback.failed(e);
				}

				@Override
				public void cancelled()
				{
					if (callback != null)
						callback.cancelled();
				}

				@Override
				public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
				{
					if (callback != null)
						return wrap(task.getURI().toString(), callback.taskStart(task));
					return null;
				}
			}, options);
		}

		@Override
		public MinecraftDownloadProvider getProvider() {return delegate.getProvider();}

		@Override
		public <T> Future<T> download(CombinedDownloadTask<T> task, CombinedDownloadCallback<T> callback)
		{
			return delegate.download(task, new CallbackAdapter<T>()
			{
				@Override
				public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
				{
					return wrap(task.getURI().toString(), callback.taskStart(task));
				}
			});
		}

		@Override
		public <T> Future<T> download(CombinedDownloadTask<T> task, CombinedDownloadCallback<T> callback, int tries)
		{
			return delegate.download(task, new CallbackAdapter<T>()
			{
				@Override
				public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
				{
					return wrap(task.getURI().toString(), callback.taskStart(task));
				}
			}, tries);
		}

		@Override
		public <T> Future<T> download(DownloadTask<T> task, DownloadCallback<T> callback)
		{
			return delegate.download(task, wrap(task.getURI().toString(), callback));
		}

		@Override
		public <T> Future<T> download(DownloadTask<T> task, DownloadCallback<T> callback, int tries) {return delegate.download(task, wrap(task.getURI().toString(), callback), tries);}

		@Override
		public void shutdown()
		{
			delegate.shutdown();
			Platform.runLater(() -> downloadMap.remove(name));
		}

		@Override
		protected void finalize() throws Throwable
		{
			shutdown();
			super.finalize();
		}

		@Override
		public boolean isShutdown() {return delegate.isShutdown();}

		private <T> DownloadCallback<T> wrap(String url, DownloadCallback<T> downloadCallback)
		{
			if (downloadCallback == null) return null;
			TaskImpl<T> tTask = new TaskImpl<>(url, downloadCallback);
			list.add(tTask);
			return tTask;
		}
	}

	@Override
	public MinecraftDownloader listenDownloader(String name, MinecraftDownloader downloader)
	{
		ObservableList<TaskInfo> objects = FXCollections.observableArrayList();
		downloadMap.put(name, objects);
		return new DownloaderWrapper(name, downloader, objects);
	}

	@Override
	public Task<Version> downloadVersion(MinecraftDirectory dir, String version, MinecraftDownloadOption... options)
	{
		return null;
	}

	@Override
	public <T> Task<T> download(CombinedDownloadTask<T> task)
	{
		return null;
	}

	@Override
	public <T> Task<T> download(CombinedDownloadTask<T> task, int tries)
	{
		return null;
	}

	@Override
	public <T> Task<T> download(DownloadTask<T> task)
	{
		return null;
	}

	@Override
	public <T> Task<T> download(DownloadTask<T> task, int tries)
	{
		return null;
	}

	@Override
	public ObservableMap<String, ObservableList<TaskInfo>> getListenedDownloaderMap()
	{
		return downloadMap;
	}

	public static class TaskImpl<T> implements TaskInfo, DownloadCallback<T>
	{
		private String url;
		private DownloadCallback<T> downloadCallback;

		private LongProperty progress = new SimpleLongProperty(), total = new SimpleLongProperty();
		private IntegerProperty retry = new SimpleIntegerProperty(), maxRetry = new SimpleIntegerProperty();
		private BooleanProperty isDown = new SimpleBooleanProperty();
		private ObservableList<Throwable> errors;

		public TaskImpl(String url, DownloadCallback<T> downloadCallback)
		{
			this.url = url;
			this.downloadCallback = downloadCallback;
		}

		@Override
		public String getURL() {return url;}

		@Override
		public LongProperty getProgress() {return progress;}

		@Override
		public LongProperty getTotal() {return total;}

		@Override
		public IntegerProperty retryCount() {return retry;}

		@Override
		public IntegerProperty maxRetryCount() {return maxRetry;}

		@Override
		public BooleanProperty isDone() {return isDown;}

		@Override
		public ObservableList<Throwable> getErrors() {return errors;}

		private void addError(Throwable throwable)
		{
			if (errors == null) errors = FXCollections.observableArrayList();
			errors.add(throwable);
		}

		public void updateProgress(long done, long total)
		{
			Platform.runLater(() ->
			{
				this.total.set(total);
				this.progress.set(done);
				if (downloadCallback != null)
					downloadCallback.updateProgress(done, total);
			});
		}

		public void retry(Throwable e, int current, int max)
		{
			Platform.runLater(() ->
			{
				this.total.set(max);
				this.retry.set(current);
				addError(e);
				if (downloadCallback != null)
					downloadCallback.retry(e, current, max);
			});
		}

		@Override
		public void done(T result)
		{
			Platform.runLater(
					() ->
					{
						isDown.set(true);
						if (downloadCallback != null)
							downloadCallback.done(result);
					}
			);
		}

		public void failed(Throwable e)
		{
			Platform.runLater(
					() ->
					{
						isDown.set(true);
						addError(e);
						if (downloadCallback != null)
							downloadCallback.failed(e);
					}
			);
		}

		public void cancelled()
		{
			Platform.runLater(
					() ->
					{
						isDown.set(true);
						addError(new IllegalStateException("cancelled"));
						if (downloadCallback != null)
							downloadCallback.cancelled();
					}
			);
		}
	}
}
