package net.launcher.version;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;

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

	@Override
	public void start(Stage primaryStage) throws Exception
	{

	}
}
