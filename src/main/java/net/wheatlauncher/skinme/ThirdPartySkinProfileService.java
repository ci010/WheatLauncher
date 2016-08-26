package net.wheatlauncher.skinme;

import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.AbstractClientService;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author ci010
 */
public class ThirdPartySkinProfileService extends AbstractClientService implements ProfileService
{
	private String rootURL;

	public ThirdPartySkinProfileService(HttpRequester requester)
	{
		super(requester);
	}

	protected GameProfile parseProfile(JSONObject object)
	{
		String name = object.getString("name");
		Map<String, String> map = new HashMap<>();
		JSONArray model_preference = object.optJSONArray("model_preference");
		String preferModel = null;
		if (model_preference != null && model_preference.length() > 0)
			preferModel = model_preference.getString(0);

		String skin;
		JSONObject skins = object.getJSONObject("skins");
		if (preferModel != null)
			skin = skins.getString(preferModel);
		else
		{
		}
		if (object.has("cape"))
			map.put("cape", object.getString("cape"));
		return new PropertiesGameProfile(UUID.fromString(name), name, map);
	}

	@Override
	public PropertiesGameProfile getGameProfile(UUID profileUUID) throws AuthenticationException
	{
		throw new UnsupportedOperationException("Skinme doesn't use UUID to specific a player. Please use username.");
	}

	@Override
	public Map<TextureType, Texture> getTextures(GameProfile profile) throws AuthenticationException
	{
		if (profile instanceof PropertiesGameProfile)
		{
			Map<String, String> properties = ((PropertiesGameProfile) profile).getProperties();
		}
		else
		{

		}
		return null;
	}

	@Override
	public GameProfile lookupGameProfile(String name) throws AuthenticationException
	{
		Objects.requireNonNull(name);
		return invokeOperation(() -> {
			try
			{
				JSONObject get = nullableJsonObject(requester.request("GET", rootURL + name + ".json"));
				if (get == null)
					return null;
				return parseProfile(get);
			}
			catch (IOException ignored) {}
			return null;
		});
	}

	@Override
	public GameProfile lookupGameProfile(String name, long timestamp) throws AuthenticationException
	{
		Objects.requireNonNull(name);
		return invokeOperation(() -> {
			Map<String, Object> arguments = new HashMap<>();
			arguments.put("at", timestamp / 1000);
			JSONObject get = nullableJsonObject(requester.request("GET", HttpUtils.withUrlArguments(rootURL + name + ".json", arguments)));
			if (get == null)
				return null;
			return parseProfile(get);
		});
	}
}
