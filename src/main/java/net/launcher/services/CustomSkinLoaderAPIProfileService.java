package net.launcher.services;

import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.URLTexture;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author ci010
 */
public class CustomSkinLoaderAPIProfileService extends SkinProfileService
{
	private String rootURL;

	public CustomSkinLoaderAPIProfileService(HttpRequester requester, String rootURL)
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
		String player_name = object.optString("player_name");
		if (player_name == null)
			player_name = object.optString("username");

		Map<String, String> map = new HashMap<>();

		JSONObject skins = object.optJSONObject("skins");
		if (skins != null)
			map.put("skins", skins.toString());
		else
		{
			String skin = object.optString("skin");
			if (skin != null)
				map.put("skin", skin);
		}

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

		String cape = properties.get("cape");
		if (cape != null)
			try
			{
				textureMap.put(TextureType.CAPE, new URLTexture(new URL(getTextureURL(cape)), Collections.emptyMap()));
			}
			catch (MalformedURLException ignored) {}

		String skinJson = properties.get("skin");
		String skin = null, skinType = null;
		if (skinJson != null)
		{
			if (skinJson.startsWith("{"))
			{
				JSONObject skinObject = new JSONObject(skinJson);
				Set<String> strings = skinObject.keySet();
				for (String s : strings)
				{
					skinType = s;
					skin = skinObject.getString(skinType);
					break;
				}
			}
			else skin = skinJson;
		}
		if (skin == null)
			return textureMap;

		Map<String, String> meta = new HashMap<>();
		if (skinType != null)
			meta.put("model", skinType);
		try {textureMap.put(TextureType.SKIN, new URLTexture(new URL(getTextureURL(skin)), meta));}
		catch (MalformedURLException ignored) {}
		return textureMap;
	}
}
