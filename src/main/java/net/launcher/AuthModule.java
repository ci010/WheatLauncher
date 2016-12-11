package net.launcher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthInfo;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author ci010
 */
public class AuthModule implements AuthProfile
{
	private StringProperty account = new SimpleStringProperty(StringUtils.EMPTY),
			password = new SimpleStringProperty(StringUtils.EMPTY);
	private String
			clientToken = StringUtils.EMPTY,
			accessToken = StringUtils.EMPTY;
	private ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>(AuthorizeFactory.ONLINE);
	private ObjectProperty<AuthInfo> cache = new SimpleObjectProperty<>();

	private ObservableList<String> history = FXCollections.observableArrayList();

	@Override
	public ReadOnlyObjectProperty<Authorize> authorizeProperty()
	{
		return authorize;
	}

	@Override
	public String getAccount() {return account.get();}

	@Override
	public ReadOnlyStringProperty accountProperty()
	{
		return account;
	}

	@Override
	public ReadOnlyStringProperty passwordProperty()
	{
		return password;
	}

	@Override
	public void setAccount(String account)
	{
		Objects.requireNonNull(account);
		authorize.get().validateUserName(account);
		this.account.set(account);
	}

	@Override
	public String getPassword() {return password.get();}

	@Override
	public void setPassword(String password)
	{
		Objects.requireNonNull(password);
		authorize.get().validatePassword(password);
		this.password.set(password);
	}

	@Override
	public String getClientToken() {return clientToken;}

	@Override
	public void setClientToken(String clientToken)
	{
		Objects.requireNonNull(clientToken);
		this.clientToken = clientToken;
	}


	@Override
	public String getAccessToken() {return accessToken;}

	@Override
	public void setAccessToken(String accessToken)
	{
		Objects.requireNonNull(accessToken);
		this.accessToken = accessToken;
	}

	@Override
	public Authorize getAuthorize() {return authorize.get();}

	@Override
	public void setAuthorize(Authorize authorize)
	{
		Objects.requireNonNull(authorize);
		this.authorize.set(authorize);
	}

	@Override
	public void setCache(AuthInfo info)
	{
		history.add(account.getValue());
		this.cache.set(info);
	}

	@Override
	public ObservableList<String> getHistory()
	{
		return history;
	}

	@Override
	public ObjectProperty<AuthInfo> cacheProperty()
	{
		return cache;
	}

	@Override
	public AuthInfo getCache()
	{
		return cache.get();
	}
}
