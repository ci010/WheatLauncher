package net.launcher.control.versions;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.provider.DownloadProviderChain;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeDownloadProvider;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersion;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersionList;
import org.to2mbn.jmccc.mcdownloader.provider.liteloader.LiteloaderDownloadProvider;
import org.to2mbn.jmccc.mcdownloader.provider.liteloader.LiteloaderVersion;
import org.to2mbn.jmccc.mcdownloader.provider.liteloader.LiteloaderVersionList;

/**
 * @author ci010
 */
public class MinecraftVersionHandler
{
	private MinecraftDownloader downloader;
	private ForgeDownloadProvider forge;
	private LiteloaderDownloadProvider liteloader;

	private ObjectProperty<RemoteVersionList> mcVersionList;
	private ObjectProperty<ForgeVersionList> forgeVersionList;
	private ObjectProperty<LiteloaderVersionList> liteloaderVersionList;


	private ObjectProperty<RemoteVersion> selectedMC;
	private ObjectProperty<ForgeVersion> selectedForge;
	private ObjectProperty<LiteloaderVersion> selectedLiteLoader;

	public MinecraftVersionHandler()
	{
		forge = new ForgeDownloadProvider();
		liteloader = new LiteloaderDownloadProvider();
		downloader = MinecraftDownloaderBuilder.create()
				.providerChain(DownloadProviderChain.create().addProvider(forge).addProvider(liteloader))
				.build();

		mcVersionList = new SimpleObjectProperty<>();
		forgeVersionList = new SimpleObjectProperty<>();
		liteloaderVersionList = new SimpleObjectProperty<>();
	}

	public void handle()
	{
		RemoteVersion selectedMC = getSelectedMC();
		if (selectedMC == null) throw new IllegalArgumentException();
		ForgeVersion selectedForge = getSelectedForge();

	}

	public RemoteVersion getSelectedMC()
	{
		return selectedMC.get();
	}

	public ObjectProperty<RemoteVersion> selectedMCProperty()
	{
		return selectedMC;
	}

	public void setSelectedMC(RemoteVersion selectedMC)
	{
		this.selectedMC.set(selectedMC);
	}

	public ForgeVersion getSelectedForge()
	{
		return selectedForge.get();
	}

	public ObjectProperty<ForgeVersion> selectedForgeProperty()
	{
		return selectedForge;
	}

	public void setSelectedForge(ForgeVersion selectedForge)
	{
		this.selectedForge.set(selectedForge);
	}

	public LiteloaderVersion getSelectedLiteLoader()
	{
		return selectedLiteLoader.get();
	}

	public ObjectProperty<LiteloaderVersion> selectedLiteLoaderProperty()
	{
		return selectedLiteLoader;
	}

	public void setSelectedLiteLoader(LiteloaderVersion selectedLiteLoader)
	{
		this.selectedLiteLoader.set(selectedLiteLoader);
	}

	public RemoteVersionList getMcVersionList()
	{
		return mcVersionList.get();
	}

	public ObjectProperty<RemoteVersionList> mcVersionListProperty()
	{
		return mcVersionList;
	}

	public ForgeVersionList getForgeVersionList()
	{
		return forgeVersionList.get();
	}

	public ObjectProperty<ForgeVersionList> forgeVersionListProperty()
	{
		return forgeVersionList;
	}

	public LiteloaderVersionList getLiteloaderVersionList()
	{
		return liteloaderVersionList.get();
	}

	public ObjectProperty<LiteloaderVersionList> liteloaderVersionListProperty()
	{
		return liteloaderVersionList;
	}

	public void refreshAll()
	{
		refreshMC();
		refreshForge();
		refreshLiteLoader();
	}

	public void refreshMC()
	{
		downloader.fetchRemoteVersionList(new CallbackAdapter<RemoteVersionList>()
		{
			@Override
			public void done(RemoteVersionList result) {mcVersionList.set(result);}
		});
	}

	public void refreshForge()
	{
		downloader.download(forge.forgeVersionList(), new CallbackAdapter<ForgeVersionList>()
		{
			@Override
			public void done(ForgeVersionList result) {forgeVersionList.set(result);}
		});
	}

	public void refreshLiteLoader()
	{
		downloader.download(liteloader.liteloaderVersionList(), new CallbackAdapter<LiteloaderVersionList>()
		{
			@Override
			public void done(LiteloaderVersionList result) {liteloaderVersionList.set(result);}
		});
	}
}
