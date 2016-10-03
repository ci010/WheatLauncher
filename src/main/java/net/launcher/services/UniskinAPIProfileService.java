package net.launcher.services;

import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.URLTexture;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONException;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ci010
 */
public class UniskinAPIProfileService extends SkinProfileService
{
	private String rootURL;

	public UniskinAPIProfileService(HttpRequester requester, String rootURL)
	{
		super(requester);
		if (!rootURL.endsWith("/"))
			rootURL += "/";
		this.rootURL = rootURL;
	}

	@Override
	protected String getProfileJsonURL(String name)
	{
		return rootURL + name + ".json";
	}

	@Override
	protected String getTextureURL(String hash)
	{
		return rootURL + "textures/" + hash;
	}

	@Override
	protected PropertiesGameProfile parse(JSONObject object)
	{
		String player_name = object.getString("player_name");

		Map<String, String> map = new HashMap<>();

		JSONArray model_preference = object.optJSONArray("model_preference");
		if (model_preference != null)
			map.put("model_preference", model_preference.toString());

		map.put("last_update", String.valueOf(object.optInt("last_update")));
		JSONObject skins = object.optJSONObject("skins");
		if (skins != null)
			map.put("skins", skins.toString());

		String cape = object.optString("cape");
		if (cape != null)
			map.put("cape", cape);
		return new PropertiesGameProfile(UUID.fromString(player_name), player_name, map);
	}

	@Override
	protected Map<TextureType, Texture> handleTexture(PropertiesGameProfile profile)
	{
		Map<TextureType, Texture> textureMap = new HashMap<>();
		Map<String, String> properties = profile.getProperties();

		String legacy_cape = properties.get("cape");

		String skins = properties.get("skins");
		if (skins == null)
		{
			if (legacy_cape != null)
				try
				{
					textureMap.put(TextureType.CAPE, new URLTexture(new URL(getTextureURL(legacy_cape)), Collections.emptyMap()));
				}
				catch (MalformedURLException ignored) {}
			return textureMap;
		}

		String defaultSkinID = "default";
		String model_preference = properties.get("model_preference");
		if (model_preference != null)
		{
			JSONArray arr = null;
			try {arr = new JSONArray(model_preference);}
			catch (JSONException ignored) {}
			if (arr != null && arr.length() > 0)
				for (int i = 0; i < arr.length(); i++)
				{
					String s = arr.optString(i);
					if (s != null && (s.equals("slim") || s.equals("default")))
					{
						defaultSkinID = s;
						break;
					}
				}
		}

		JSONObject skinsObject = new JSONObject(skins);

		String skin = skinsObject.optString(defaultSkinID);
		if (skin != null)
		{
			Map<String, String> meta = new HashMap<>();
			meta.put("model", defaultSkinID);
			try {textureMap.put(TextureType.SKIN, new URLTexture(new URL(getTextureURL(skin)), meta));}
			catch (MalformedURLException ignored) {}
		}

		String capeHash = skinsObject.optString("cape");
		if (capeHash == null)
			try
			{
				textureMap.put(TextureType.CAPE, new URLTexture(new URL(getTextureURL(legacy_cape)), Collections.emptyMap()));
			}
			catch (MalformedURLException ignored) {}
		else
		{
			try
			{
				textureMap.put(TextureType.CAPE, new URLTexture(new URL(getTextureURL(capeHash)), Collections.emptyMap()));
			}
			catch (MalformedURLException ignored) {}
		}

		String elytron = skinsObject.optString("elytron");
		if (elytron != null)
			try
			{
				textureMap.put(TextureType.ELYTRA, new URLTexture(new URL(getTextureURL(elytron)), Collections.emptyMap()));
			}
			catch (MalformedURLException ignored) {}

		return textureMap;
	}
}
