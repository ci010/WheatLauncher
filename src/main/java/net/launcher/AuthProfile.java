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
public class AuthProfile
{
	private StringProperty account = new SimpleStringProperty(StringUtils.EMPTY),
			password = new SimpleStringProperty(StringUtils.EMPTY);
	private String clientToken = StringUtils.EMPTY,
			accessToken = StringUtils.EMPTY;
	private ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>(AuthorizeFactory.ONLINE);
	private ObjectProperty<AuthInfo> cache = new SimpleObjectProperty<>();

	private ObservableMap<String, ObservableList<String>> historyMap = FXCollections.observableMap(new TreeMap<>());

	public AuthProfile()
	{
		AuthorizeFactory.getAuthorizeMap().forEach((k, v) ->
				historyMap.put(k, FXCollections.observableArrayList()));
	}

	public AuthProfile(String account, Authorize authorize, Map<String, List<String>> history)
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

	public ReadOnlyObjectProperty<Authorize> authorizeProperty()
	{
		return authorize;
	}

	public String getAccount() {return account.get();}

	public ReadOnlyStringProperty accountProperty()
	{
		return account;
	}

	public ReadOnlyStringProperty passwordProperty()
	{
		return password;
	}

	public void setAccount(String account)
	{
		account = account == null ? "" : account;
//		authorize.get().validateUserName(account);
		this.account.set(account);
	}

	public String getPassword() {return password.get();}

	public void setPassword(String password)
	{
		Objects.requireNonNull(password);
		authorize.get().validatePassword(password);
		this.password.set(password);
	}

	public String getClientToken() {return clientToken;}

	public void setClientToken(String clientToken)
	{
		Objects.requireNonNull(clientToken);
		this.clientToken = clientToken;
	}

	public String getAccessToken() {return accessToken;}

	public void setAccessToken(String accessToken)
	{
		Objects.requireNonNull(accessToken);
		this.accessToken = accessToken;
	}

	public ObservableMap<String, ObservableList<String>> getHistoryMap()
	{
		return historyMap;
	}

	public Authorize getAuthorize() {return authorize.get();}

	public void setAuthorize(Authorize authorize)
	{
		Objects.requireNonNull(authorize);
		this.authorize.set(authorize);
	}

	public void setCache(AuthInfo info)
	{
		this.addToHistory(info.getUsername());
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


	public ObjectProperty<AuthInfo> cacheProperty()
	{
		return cache;
	}

	public AuthInfo getCache()
	{
		return cache.get();
	}

	public ObservableList<String> getHistoryList() {return getHistoryMap().get(Authorize.getID(getAuthorize()));}
}
