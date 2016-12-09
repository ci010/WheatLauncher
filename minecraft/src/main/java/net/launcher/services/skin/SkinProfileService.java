package net.launcher.services.skin;

import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.AbstractClientService;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author ci010
 */
public abstract class SkinProfileService extends AbstractClientService implements ProfileService
{
	public SkinProfileService(HttpRequester requester)
	{
		super(requester);
	}

	protected abstract String getProfileJsonURL(String name);

	protected abstract String getTextureURL(String hash);

	protected abstract PropertiesGameProfile parse(JSONObject object);

	protected abstract Map<TextureType, Texture> handleTexture(PropertiesGameProfile profile);

	@Override
	public PropertiesGameProfile getGameProfile(UUID profileUUID) throws AuthenticationException
	{
		return lookupGameProfile(profileUUID.toString());
	}

	@Override
	public Map<TextureType, Texture> getTextures(GameProfile profile) throws AuthenticationException
	{
		if (!(profile instanceof PropertiesGameProfile))
			profile = lookupGameProfile(profile.getName());
		return handleTexture((PropertiesGameProfile) profile);
	}

	@Override
	public PropertiesGameProfile lookupGameProfile(String name) throws AuthenticationException
	{
		Objects.requireNonNull(name);
		return invokeOperation(() ->
		{
			try
			{
				JSONObject get = nullableJsonObject(requester.request("GET", getProfileJsonURL(name)));
				if (get == null)
					return null;
				get.put("$fallback_name", name);
				return parse(get);
			}
			catch (IOException ignored) {}
			return null;
		});
	}

	@Override
	public PropertiesGameProfile lookupGameProfile(String name, long timestamp) throws AuthenticationException
	{
		Objects.requireNonNull(name);
		return invokeOperation(() ->
		{
			Map<String, Object> arguments = new HashMap<>();
			arguments.put("at", timestamp / 1000);
			JSONObject get = nullableJsonObject(requester.request("GET", HttpUtils.withUrlArguments(getProfileJsonURL(name), arguments)));
			if (get == null)
				return null;
			get.put("$fallback_name", name);
			return parse(get);
		});
	}
}
