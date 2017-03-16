package net.launcher.fx.impl;

import api.launcher.event.RegisterAuthEvent;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.fx.TaskProvider;
import net.launcher.fx.View;
import net.launcher.fx.Views;
import net.launcher.fx.auth.AuthorizeMojang;
import net.launcher.fx.auth.Authorizer;
import net.launcher.fx.module.ComponentProvider;
import net.launcher.fx.module.GlobalModule;
import net.launcher.fx.module.InstanceProvider;
import net.launcher.fx.profile.LaunchProfiler;
import net.launcher.game.ResourcePack;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.WorldInfo;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.model.Authorize;
import net.launcher.model.Profile;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Resource;
import net.launcher.utils.serial.BiSerializer;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ModuleCore extends GlobalModule
{
	private ArchiveRepository<ModContainer<?>[]> modRepository;
	private ArchiveRepository<ResourcePack> resourcePackRepository;

	private View<ServerInfo> serverInfos;
	private View<WorldInfo> worldInfos;
	private View<Profile> profiles;
	private View<Authorize> authorizes;
	private View<ResourcePack> resourcePacks;
	private View<ModContainer<?>> mods;

	private Authorizer authorizer;
	private LaunchProfiler profiler;

	@Override
	protected void onInit() throws Exception
	{
		NBTCompound read = NBT.read(getRoot().resolve("servers.dat"), false).asCompound();
		BiSerializer<ServerInfo, NBTCompound> serializer = ServerInfoBase.serializer();
		serverInfos = Views.create(ServerInfo::getName, FXCollections.observableList(read.get("servers").asList().stream().map(NBT::asCompound)
				.map(serializer::deserialize).collect(Collectors.toList())));

		List<WorldInfo> infos = new ArrayList<>();
		for (Path save : Files.walk(getRoot().resolve("saves"), 2)
				.filter(path -> path.getFileName().toString().equals("level.dat")).collect(Collectors.toList()))
			infos.add(WorldInfo.deserialize(save));
		worldInfos = Views.create(WorldInfo::getDisplayName, FXCollections.observableArrayList(infos));

		resourcePackRepository = RepostiroyCreator.resourcePack(getRoot());
		resourcePacks = Views.create(ResourcePack::getPackName, Resource::getContainData, resourcePackRepository
				.getResourceMap());

		modRepository = RepostiroyCreator.modContainer(getRoot());
		ObservableList<ModContainer<?>> list = FXCollections.observableArrayList();
		ObservableMap<String, Resource<ModContainer<?>[]>> map = modRepository.getResourceMap();
		map.addListener((MapChangeListener<String, Resource<ModContainer<?>[]>>) change ->
		{
			if (change.wasAdded())
				list.addAll(change.getValueAdded().getContainData());
			else if (change.wasRemoved())
				list.removeAll(change.getValueRemoved().getContainData());
		});
		mods = new Views.ViewImpl<>(mod -> mod.getId() + ":" + mod.getVersion()
				.getVersionString(), list);

		RegisterAuthEvent event = new RegisterAuthEvent();
		event.register(new AuthorizeMojang());
		//post event
		authorizes = Views.create(Authorize::getId, FXCollections.observableArrayList(event.getRegistered().values()));

		authorizer = new AuthorizerImpl();
		Path path = getRoot().resolve("auth.dat");
		NBTCompound compound = NBT.read(path, false).asCompound();
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		authorizer.setAccount(account);
		authorizer.load(event.getRegistered().get(auth));

		ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(getRoot().resolve("profile.dat")));
		String selecting = (String) stream.readObject();

	}

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return null;
	}

	@Override
	public ComponentProvider createComponentProvider()
	{
		return new ComponentProvider()
		{
			@Override
			public List<Class<?>> getAllComponentTypes()
			{
				return Arrays.asList(Authorize.class,
						Profile.class,
						ResourcePack.class,
						ModContainer.class,
						WorldInfo.class,
						ServerInfo.class);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> View<T> getComponent(Class<T> type)
			{
				if (type.equals(WorldInfo.class))
					return (View<T>) worldInfos;
				else if (type.equals(Authorize.class))
					return (View<T>) (authorizes);
				else if (type.equals(Profile.class))
					return (View<T>) profiles;
				else if (type.equals(ServerInfo.class))
					return (View<T>) (serverInfos);
				else if (type.equals(ResourcePack.class))
					return (View<T>) resourcePacks;
				else if (type.equals(ModContainer.class))
					return (View<T>) mods;
				return null;
			}

			@Override
			public boolean saveComponent(View<?> o) throws Exception
			{
				return false;
			}
		};
	}

	@Override
	public InstanceProvider createInstanceProvider()
	{
		return new InstanceProvider()
		{
			@Override
			public List<Class<?>> getAllInstanceBuilderType()
			{
				return Arrays.asList(
						Authorizer.class,
						LaunchProfiler.class);
			}

			@Override
			public Object getInstance(Class<?> type)
			{
				if (type.equals(Authorizer.class))
					return authorizer;
				else if (type.equals(LaunchProfiler.class))
					return profiler;
				return null;
			}

			@Override
			public boolean saveInstance(Object o) throws Exception
			{
				if (!(o instanceof Authorizer))
					return false;

				return true;
			}
		};
	}
}
