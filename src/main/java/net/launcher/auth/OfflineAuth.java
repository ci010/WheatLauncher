package net.launcher.auth;

import javafx.beans.value.WritableValue;
import net.launcher.utils.Logger;
import net.launcher.utils.State;
import net.launcher.utils.StrictProperty;
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
class OfflineAuth extends AuthenticationIndicator
{
	@Override
	public ProfileService createProfileService()
	{
		return new ProfileService()
		{
			@Override
			public PropertiesGameProfile getGameProfile(UUID profileUUID) throws AuthenticationException {return new PropertiesGameProfile(profileUUID, profileUUID.toString(), Collections.emptyMap());}

			@Override
			public Map<TextureType, Texture> getTextures(GameProfile profile) throws AuthenticationException
			{
				return Collections.emptyMap();
			}

			@Override
			public GameProfile lookupGameProfile(String name) throws AuthenticationException {return null;}

			@Override
			public GameProfile lookupGameProfile(String name, long timestamp) throws AuthenticationException {return null;}
		};
	}

	public String id()
	{
		return "offline";
	}

	@Override
	protected void auth(String validAccount, String validPassword, WritableValue<State>
			handler, WritableValue<AuthInfo> out)
	{
		try
		{
			Logger.trace("try offline auth " + validAccount);
			AuthInfo auth = new OfflineAuthenticator(validAccount).auth();
			if (auth != null)
			{
				Logger.trace("offline auth passed!");
				out.setValue(auth);
				handler.setValue(State.of(State.Values.PASS));
			}
			else
			{
				Logger.trace("auth fail");
				handler.setValue(State.of(State.Values.FAIL, "fail"));
			}
		}
		catch (AuthenticationException e)
		{
			Logger.trace("auth fail");
			handler.setValue(State.of(State.Values.FAIL, "fail"));
		}
	}

	@Override
	public StrictProperty.Validator<String> accountValid()
	{
		return (stateHandler, v) ->
		{
			if (v == null || v.isEmpty())
				stateHandler.setValue(State.of(State.Values.FAIL, "null"));
			else stateHandler.setValue(StrictProperty.PASS);
		};
	}

	@Override
	public StrictProperty.Validator<String> passwordValid()
	{
		return null;
	}

}
