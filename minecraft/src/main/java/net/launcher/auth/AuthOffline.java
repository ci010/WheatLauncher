package net.launcher.auth;

import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.OfflineAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @author ci010
 */
@Authorize.ID("offline")
public class AuthOffline implements Authorize
{
	@Override
	public void validateUserName(String name)
	{
		if (StringUtils.isEmpty(name))
			throw new IllegalArgumentException("offline.account.null");
	}

	@Override
	public void validatePassword(String password)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AuthInfo auth(String account, String password) throws AuthenticationException
	{
		return new OfflineAuthenticator(account).auth();
	}

	@Override
	public ProfileService createProfileService()
	{
		return new ProfileService()
		{
			@Override
			public PropertiesGameProfile getGameProfile(UUID profileUUID) throws AuthenticationException {return new PropertiesGameProfile(profileUUID, profileUUID.toString(), Collections.emptyMap());}

			@Override
			public Map<TextureType, Texture> getTextures(GameProfile profile) throws AuthenticationException {return Collections.emptyMap();}

			@Override
			public GameProfile lookupGameProfile(String name) throws AuthenticationException {return null;}

			@Override
			public GameProfile lookupGameProfile(String name, long timestamp) throws AuthenticationException {return null;}
		};
	}
}
