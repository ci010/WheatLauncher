package net.launcher.impl.core;

import api.launcher.*;
import api.launcher.module.ComponentProvider;
import api.launcher.module.GlobalModule;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.FXServerInfo;
import net.launcher.game.*;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.model.MinecraftVersion;
import net.launcher.services.HandshakeTask;
import net.launcher.services.HandshakeTaskLegacy;
import net.launcher.services.PingTask;
import net.launcher.utils.MessageUtils;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.Resource;

import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author ci010
 */
public class ModuleAssets extends GlobalModule
{
	private View<ResourcePack> resourcePacks;
	private ArchiveRepository<ResourcePack> resourcePackRepository;

	private ArchiveRepository<ModContainer<?>[]> modRepository;
	private View<ModContainer<?>> mods;

	private View<ServerInfo> serverInfos;
	private ObservableList<ServerInfo> serverInfosList;

	private View<WorldInfo> worldInfos;
	private ObservableList<WorldInfo> worldInfoList;

	private ObservableList<MinecraftVersion> versionsList;
	private View<MinecraftVersion> versions;
	private String lastModified;

	@Override
	protected void onInit() throws Exception
	{
		serverInfosList = FXCollections.observableList(CoreAlgHelper.loadServer(getRoot()));
		serverInfos = Views.create(server -> server.getHostName() + "@" + server.hashCode(), serverInfosList);

		worldInfoList = FXCollections.observableArrayList(CoreAlgHelper.loadWorld(getRoot()));
		worldInfos = Views.create(WorldInfo::getDisplayName, worldInfoList);

		resourcePackRepository = RepostiroyCreator.resourcePack(getRoot());
		resourcePacks = Views.create(ResourcePack::getPackName, Resource::getContainData, resourcePackRepository
				.getResourceMap());

		ObservableList<ModContainer<?>> list = FXCollections.observableArrayList();
		modRepository = RepostiroyCreator.modContainer(getRoot());
		ObservableMap<String, Resource<ModContainer<?>[]>> map = modRepository.getResourceMap();
		map.addListener((MapChangeListener<String, Resource<ModContainer<?>[]>>) change ->
		{
			if (change.wasAdded())
				list.addAll(change.getValueAdded().getContainData());
			else if (change.wasRemoved())
				list.removeAll(change.getValueRemoved().getContainData());
		});
		mods = new Views.ViewImpl<>(mod -> mod.getId() + ":" + mod.getVersion().getVersionString(), list);

		NBTCompound compound = NBT.read(getRoot().resolve("versions.dat"), false).asCompound();
		lastModified = compound.getOption("lastModified").map(NBT::asString).orElse(null);
		versionsList = CoreAlgHelper.loadVersions(compound);
		versions = Views.create(MinecraftVersion::getVersionId, versionsList);
		lastModified = CoreAlgHelper.updateVersion(getRoot(), lastModified, versions, versionsList);
	}

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return Arrays.asList(new TaskProviderBase("server.add", (args) -> new Task<ServerInfo>()
				{
					@Override
					protected ServerInfo call() throws Exception
					{
						String string = Shell.instance().getLanguageBundle().getString("server.new");
						int i = 1;
						while (serverInfos.getByKey(string) != null)
							string = string.concat((i++) + "");
						serverInfosList.add(new FXServerInfo(new ServerInfoBase(string, args[0])));
						return null;
					}
				}),
				new TaskProviderBase("server.remove", (args) -> new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						ServerInfo byKey = serverInfos.getByKey(args[0]);
						serverInfosList.remove(byKey);
						return null;
					}
				}),
				new TaskProviderBase("server.ping", (args) -> new Task<ServerStatus>()
				{
					@Override
					protected ServerStatus call() throws Exception
					{
						ServerInfo info = serverInfos.getByKey(args[args.length - 1]);
						boolean wait = false;
						if (args.length > 1 && args[0].equals("-wait"))
							wait = true;
						SocketChannel open = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
						//@formatter:off
						if (!wait)
							try {return new HandshakeTask(info, open).call();}
							catch (Exception e)
							{
								try {return new HandshakeTaskLegacy(info, open).call();}
								catch (Exception e1) {e1.addSuppressed(e);throw e1;}
							}
						else
							try {return new PingTask(info, new HandshakeTask(info, open).call(), open).call();}
							catch (Exception e)
							{
								try {return new PingTask(info, new HandshakeTaskLegacy(info, open).call(), open).call();}
								catch (Exception e1) {e1.addSuppressed(e);throw e1;}
							}
						//@formatter:on
					}
				}),
				new TaskProviderBase("minecraft.import", (args) -> null),
				new TaskProviderBase("world.refresh", (args) -> new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						worldInfoList.setAll(CoreAlgHelper.loadWorld(getRoot()));
						return null;
					}
				}),
				new TaskProviderBase("world.import", (args) -> new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						String world = args[0];
						WorldInfo info = worldInfos.getByKey(world);
						if (info == null) throw new IllegalArgumentException("Cannot find world file named " + world);
						String target = args[1];
						Path path = Paths.get(target);
						Files.createDirectories(path);
						NIOUtils.copyDirectory(getRoot().resolve(world), path);
						return null;
					}
				}),
				new TaskProviderBase("version.refresh", (args) ->
						new Task<Void>()
						{
							@Override
							protected Void call() throws Exception
							{
								CoreAlgHelper.updateVersion(getRoot(), lastModified, versions, versionsList);
								return null;
							}
						}),
				new TaskProviderBase("version.fetch", (args) ->
				{

					return null;
				}),
				new TaskProviderBase("version.export", (args) -> null),

				new TaskProviderBase("resourcepacks.import", (args) -> resourcePackRepository.importResource(Paths
						.get(args[0]))),
				new TaskProviderBase("resourcepacks.export", (args) -> resourcePackRepository.fetchAllResource(
						Paths.get(args[0]), Arrays.asList(Arrays.copyOfRange(args, 1, args.length)),
						FetchOption.SYMBOL_LINK
				)),
				new TaskProviderBase("resourcepacks.icon", (args) -> new Task<Image>()
				{
					@Override
					protected Image call() throws Exception
					{
						Resource<ResourcePack> packResource = resourcePackRepository.getResourceMap().get(args[0]);
						if (packResource != null)
							try (InputStream stream = resourcePackRepository.openStream(packResource, "pack.png"))
							{
								return new Image(stream);
							}
							catch (Exception e) {return MinecraftIcons.UNKNOWN;}
						return MinecraftIcons.UNKNOWN;
					}
				}));
	}

	@Override
	public ComponentProvider createComponentProvider()
	{
		return new ComponentProvider()
		{
			@Override
			public List<Class<?>> getAllComponentTypes()
			{
				return Arrays.asList(
						ResourcePack.class,
						ModContainer.class,
						MinecraftVersion.class,
						WorldInfo.class,
						ServerInfo.class);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> View<T> getComponent(Class<T> type)
			{
				if (type.equals(WorldInfo.class))
					return (View<T>) worldInfos;
				else if (type.equals(ServerInfo.class))
					return (View<T>) (serverInfos);
				else if (type.equals(ResourcePack.class))
					return (View<T>) resourcePacks;
				else if (type.equals(ModContainer.class))
					return (View<T>) mods;
				else if (type.equals(MinecraftVersion.class))
					return (View<T>) versions;
				return null;
			}

			@Override
			public boolean saveComponent(View<?> o) throws Exception
			{
				return false;
			}
		};
	}
}
