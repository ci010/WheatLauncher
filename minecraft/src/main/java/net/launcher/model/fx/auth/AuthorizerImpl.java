package net.launcher.model.fx.auth;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import net.launcher.model.Authorize;
import net.launcher.model.AuthorizeBase;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.Authenticator;
import org.to2mbn.jmccc.auth.OfflineAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.PropertiesGameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author ci010
 */
public class AuthorizerImpl extends AuthorizeBase implements Authorizer
{
	private ObjectProperty<Authorize> delegate = new SimpleObjectProperty<>();

	private StringProperty id = new SimpleStringProperty();
	private StringProperty account = new SimpleStringProperty();

	AuthorizerImpl(Authorize authorize)
	{
		this.load(authorize);
	}

	@Override
	public String getId()
	{
		if (delegate.get() != null)
			return delegate.get().getId();
		return id.get();
	}

	@Override
	public void setAccount(String account)
	{
		if (delegate.get() != null)
			delegate.get().setAccount(account);
		else if (StringUtils.isEmpty(account))
			throw new IllegalArgumentException("offline.account.null");
		this.account.set(account);
	}

	@Override
	public String getAccount()
	{
		if (delegate.get() != null)
			return delegate.get().getAccount();
		return account.get();
	}

	@Override
	public void updatePassword(String password)
	{
		if (delegate.get() != null)
			delegate.get().updatePassword(password);
	}

	@Override
	public List<String> getAccountHistory()
	{
		if (delegate.get() != null)
			return delegate.get().getAccountHistory();
		return super.getAccountHistory();
	}

	@Override
	public Authenticator buildAuthenticator()
	{
		if (delegate.get() != null)
			return delegate.get().buildAuthenticator();
		return new AuthenticatorWrapper(new OfflineAuthenticator(account.get()));
	}

	@Override
	public ProfileService createProfileService()
	{
		if (delegate.get() != null)
			return delegate.get().createProfileService();
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

	@Override
	public void load(Authorize authorize)
	{
		if (authorize == null || authorize == this)
		{
			id.unbind();
			id.setValue("offline");
			account.unbind();
			account.setValue(getAccountHistory().get(0));
		}
		else
		{
			delegate.set(authorize);
			id.bind(Bindings.createStringBinding(() -> delegate.get().getId(), delegate));
			account.bind(Bindings.createStringBinding(() -> delegate.get().getAccount(), delegate));
		}
	}

	@Override
	public ReadOnlyStringProperty idProperty()
	{
		return id;
	}

	@Override
	public ReadOnlyStringProperty accountProperty()
	{
		return account;
	}
}
