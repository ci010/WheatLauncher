package net.launcher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

	private ObservableMap<String, ObservableList<String>> historyMap = FXCollections.observableMap(new TreeMap<>());

	public AuthModule()
	{
		AuthorizeFactory.getAuthorizeMap().forEach((k, v) ->
				historyMap.put(k, FXCollections.observableArrayList()));
	}

	public AuthModule(String account, Authorize authorize, Map<String, List<String>> history)
	{
		this();
		this.account.set(account);
		this.authorize.set(authorize);
		history.forEach((s, l) ->
		{
			ObservableList<String> limitList = FXCollections.observableArrayList();
			limitList.addAll(l);
			this.historyMap.put(s, limitList);
		});
	}

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
		account = account == null ? "" : account;
//		authorize.get().validateUserName(account);
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
	public ObservableMap<String, ObservableList<String>> getHistoryMap()
	{
		return historyMap;
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
		addToHistory(info.getUsername());
		this.cache.set(info);
	}

	private void addToHistory(String account)
	{
		String id = Authorize.getID(getAuthorize());
		if (!historyMap.containsKey(id))
			historyMap.put(id, FXCollections.observableArrayList());
		ObservableList<String> strings = historyMap.get(id);
		if (!strings.contains(account))
		{
			if (strings.size() > 5) strings.remove(strings.size() - 1);
			strings.add(0, account);
		}
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
