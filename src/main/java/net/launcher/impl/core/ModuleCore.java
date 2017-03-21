package net.launcher.impl.core;

import api.launcher.*;
import api.launcher.auth.AuthorizeMojang;
import api.launcher.auth.AuthorizeProxy;
import api.launcher.event.RegisterAuthEvent;
import api.launcher.module.ComponentProvider;
import api.launcher.module.GlobalModule;
import api.launcher.module.InstanceProvider;
import api.launcher.profile.ProfileProxy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.util.Pair;
import moe.mickey.minecraft.skin.fx.SkinCanvas;
import net.launcher.model.Authorize;
import net.launcher.model.Profile;
import net.launcher.model.ProfileBase;
import net.launcher.utils.Tasks;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.util.UUIDUtils;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * @author ci010
 */
public class ModuleCore extends GlobalModule
{
	private View<Profile> profiles;
	private ObservableList<Profile> profilesList;
	private View<Authorize> authorizes;

	private AuthorizeProxy authorizeProxy;
	private ProfileProxy profiler;

	private AuthInfo cache;

	@Override
	protected void onInit() throws Exception
	{
		RegisterAuthEvent event = new RegisterAuthEvent();
		event.register(new AuthorizeMojang());
		Shell.bus().postEvent(event);
		authorizes = Views.create(Authorize::getId, FXCollections.observableArrayList(event.getRegistered().values()));
		authorizeProxy = Tasks.optional(() -> CoreAlgHelper.loadProxy(getRoot(), event), AuthorizeProxyImpl::new);

		profilesList = FXCollections.observableList(Tasks.optional(() -> CoreAlgHelper.updateLocalProf(getRoot()),
				Collections::emptyList));
		String selecting = Tasks.optional(() -> new ObjectInputStream(Files.newInputStream(getRoot().resolve("profile.dat")))
				.readObject().toString()).orElse(null);
		profiles = Views.create(Profile::getId, profilesList);
		profiler = new ProfilerImpl();
		Profile byKey = profiles.getByKey(selecting);
		if (byKey != null) profiler.load(byKey);
		else if (profilesList.isEmpty())
		{
			profilesList.add(new ProfileBase());
			profiler.load(profilesList.get(0));
		}
		else profiler.load(profilesList.get(0));
	}

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return Arrays.asList(
				new TaskProviderBase("login", (args) -> new Task<AuthInfo>()
				{
					@Override
					protected AuthInfo call() throws Exception
					{
						return cache = authorizeProxy.buildAuthenticator().auth();
					}
				}),
				new TaskProviderBase("launch", (args) -> new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						return null;
					}
				}),
				new TaskProviderBase("skin", (args) -> new Task<Pair<Image, Boolean>>()
				{
					@Override
					protected Pair<Image, Boolean> call() throws Exception
					{
						if (cache == null) return new Pair<>(SkinCanvas.STEVE, false);
						ProfileService profileService = authorizeProxy.createProfileService();
						Map<TextureType, Texture> textures = profileService.getTextures(
								profileService.getGameProfile(UUIDUtils.toUUID(cache.getUUID())));
						if (textures == null) return new Pair<>(SkinCanvas.STEVE, false);
						Texture texture = textures.get(TextureType.SKIN);
						return new Pair<>(Tasks.optional(() -> new Image(texture.openStream())).orElse(SkinCanvas.STEVE),
								Optional.ofNullable(texture.getMetadata().get("model")).orElse("steve").equals
										("slim"));
					}
				}),
				new TaskProviderBase("profile.add", (args) -> new Task<Profile>()
				{
					@Override
					protected Profile call() throws Exception
					{
						String name = "Default";
						if (args.length >= 1) name = args[0];
						ProfileBase profile = new ProfileBase(name);
						profilesList.add(profile);
						return profile;
					}
				}),
				new TaskProviderBase("profile.delete", (args) -> new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						if (args.length != 1) throw new IllegalArgumentException("delete profile need target id!!");
						String id = args[0];
						Profile byKey = profiles.getByKey(id);
						profiles.remove(byKey);
						return null;
					}
				})
		);
	}

	@Override
	public ComponentProvider createComponentProvider()
	{
		return new ComponentProvider()
		{
			@Override
			public List<Class<?>> getAllComponentTypes()
			{
				return Arrays.asList(Authorize.class, Profile.class);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> View<T> getComponent(Class<T> type)
			{
				if (type.equals(Authorize.class))
					return (View<T>) (authorizes);
				else if (type.equals(Profile.class))
					return (View<T>) profiles;
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
						AuthorizeProxy.class,
						ProfileProxy.class,
						AuthInfo.class);
			}

			@Override
			public Object getInstance(Class<?> type)
			{
				if (type.equals(AuthorizeProxy.class)) return authorizeProxy;
				else if (type.equals(ProfileProxy.class)) return profiler;
				else if (type.equals(AuthInfo.class)) return cache;
				return null;
			}

			@Override
			public boolean saveInstance(Object o) throws Exception
			{
				if (!(o instanceof AuthorizeProxy)) return false;

				return true;
			}
		};
	}
}
