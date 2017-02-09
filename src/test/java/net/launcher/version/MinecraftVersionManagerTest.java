package net.launcher.version;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.wheatlauncher.MainApplication;
import org.junit.Test;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ci010
 */
public class MinecraftVersionManagerTest extends Application
{
	private String lastModified;

	@Test
	public void updateRemoteVersion() throws Exception
	{
		HttpRequester requester = new HttpRequester();
		lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).format(Calendar.getInstance()
				.getTime());
		System.out.println(lastModified);
		Map<String, String> header;
		header = Collections.singletonMap("If-Modified-Since", lastModified);
		String get = requester.request("GET", "https://launchermeta.mojang.com/mc/game/version_manifest.json",
				header);
		System.out.println(get);
//		RemoteVersionList remoteVersionList = RemoteVersionList.fromJson(new JSONObject(get));
//
//
//		System.out.println(remoteVersionList);
	}


	@Test
	public void testSortversion() throws IOException
	{

		HttpRequester requester = new HttpRequester();
		String get = requester.request("GET", "https://launchermeta.mojang.com/mc/game/version_manifest.json");
		RemoteVersionList remoteVersionList = RemoteVersionList.fromJson(new JSONObject(get));
		for (RemoteVersion remoteVersion : remoteVersionList.getVersions().values())
			System.out.println(remoteVersion);
		System.out.println();

		ArrayList<RemoteVersion> list = new ArrayList<>(remoteVersionList.getVersions().values());
		ArrayList<RemoteVersion> list1 = new ArrayList<>(list);
		Collections.shuffle(list);
		list.sort((o1, o2) -> o2.getReleaseTime().compareTo(o1.getReleaseTime()));

		for (RemoteVersion remoteVersion : list)
			System.out.println(remoteVersion);
		for (int i = 0; i < list.size(); i++)
		{
			assert list.get(i).equals(list1.get(i));
		}

	}

	public static void main(String[] ars)
	{
		launch(ars);
	}

	class TaskWrapper
	{
		String url;
		LongProperty total;
		LongProperty progress;
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		ScrollPane root = new ScrollPane();

		VBox container = new VBox();
		JFXButton button = new JFXButton("START");
		JFXTextField jfxTextField = new JFXTextField();
		container.getChildren().add(new HBox(jfxTextField, button));
		root.setContent(container);
		Scene scene = new Scene(root, 512, 380);
//		primaryStage.initStyle(StageStyle.TRANSPARENT);
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		JFXTableView<TaskWrapper> taskJFXTableView = new JFXTableView<>();
		taskJFXTableView.setMaxHeight(300);
		taskJFXTableView.setMinWidth(300);
		TableColumn<TaskWrapper, String> url = new TableColumn<>("URL");
		url.setCellValueFactory(task -> Bindings.createStringBinding(() -> task.getValue().url));
		TableColumn<TaskWrapper, String> progress = new TableColumn<>("PROGRESS");
		progress.setCellValueFactory(task -> Bindings.createStringBinding(() ->
		{
			TaskWrapper value = task.getValue();
			double prog = value.progress.get();
			prog = prog / value.total.get() * 100D;
			return String.format("%.3f", prog) + "%";
		}, task.getValue().total, task.getValue().progress));
		taskJFXTableView.getColumns().addAll(url, progress);

		ObservableList<TaskWrapper> list = FXCollections.observableList(new LinkedList<>());

		taskJFXTableView.setItems(list);

		container.getChildren().addAll(taskJFXTableView);

		button.setOnAction(event ->
		{
			button.setDisable(true);
			System.out.println("start");
			File file = new File("D:\\Storage\\Desktop\\testminecraft");
			MinecraftDirectory directory = new MinecraftDirectory(file);
			MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();
			System.out.println("begin");
			downloader.downloadIncrementally(directory, jfxTextField.getText(), new CombinedDownloadCallback<Version>()
			{
				@Override
				public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
				{
					LongProperty progress = new SimpleLongProperty(),
							totalP = new SimpleLongProperty();
					Platform.runLater(() ->
					{
						TaskWrapper wrapper = new TaskWrapper();
						wrapper.url = task.getURI().toString();
						wrapper.total = totalP;
						wrapper.progress = progress;
						list.add(0, wrapper);
					});
					return new DownloadCallback<R>()
					{
						@Override
						public void updateProgress(long done, long total)
						{
							Platform.runLater(() ->
							{
								progress.set(done);
								totalP.set(total);
							});
						}

						@Override
						public void retry(Throwable e, int current, int max) {}

						@Override
						public void done(R result) {}

						@Override
						public void failed(Throwable e) {}

						@Override
						public void cancelled() {}
					};
				}

				@Override
				public void done(Version result)
				{
					Platform.runLater(() ->
					{
						button.setDisable(false);
					});
				}

				@Override
				public void failed(Throwable e) {}

				@Override
				public void cancelled() {}
			});
			System.out.println("end");
		});

	}
}
