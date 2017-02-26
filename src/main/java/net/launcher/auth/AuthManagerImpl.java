package net.launcher.auth;

import api.launcher.AuthManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author ci010
 */
public class AuthManagerImpl implements AuthManager
{
	private final Authorize OFFLINE;

	private StringProperty account = new SimpleStringProperty(StringUtils.EMPTY),
			password = new SimpleStringProperty(StringUtils.EMPTY);
	private String clientToken = StringUtils.EMPTY,
			accessToken = StringUtils.EMPTY;
	private ObjectProperty<AuthInfo> cache = new SimpleObjectProperty<>();

	private ObservableMap<String, ObservableList<String>> historyMap = FXCollections.observableMap(new TreeMap<>());
	private ObservableMap<String, Authorize> authorizeMap = FXCollections.observableMap(new TreeMap<>());

	private StringProperty authorize = new SimpleStringProperty();
	private ObjectProperty<Authorize> authorized = new SimpleObjectProperty<>();

	{
		OFFLINE = new AuthOffline();
		registerAuth(AuthOnline.class);
	}

	AuthManagerImpl()
	{
		this("", "offline", Collections.emptyMap());
	}

	@Override
	public String getAuthorize()
	{
		return authorize.get();
	}

	@Override
	public StringProperty authorizeProperty()
	{
		return authorize;
	}

	@Override
	public void setAuthorize(String authorize)
	{
		this.authorize.set(authorize);
	}

	@Override
	public void setNoAuthorize()
	{
		this.authorize.set("offline");
	}

	@Override
	public boolean isOffline() {return getAuthorize().equals("offline");}

	AuthManagerImpl(String account, String authorize, Map<String, List<String>> history)
	{
		this.account.set(account);
		this.authorize.set(authorize);
		this.authorized.bind(Bindings.createObjectBinding(() ->
				authorizeMap.getOrDefault(getAuthorize(), OFFLINE), this.authorize, authorizeMap));
		history.forEach((s, l) -> this.historyMap.put(s, FXCollections.observableArrayList(l)));
	}

	@Override
	public boolean registerAuth(Class<? extends Authorize> authClass)
	{
		Authorize.ID annotation = authClass.getAnnotation(Authorize.ID.class);
		if (annotation == null)
			return false;
		if (authorizeMap.containsKey(annotation.value()))
			return false;
		try
		{
			authorizeMap.put(annotation.value(), authClass.getDeclaredConstructor().newInstance());
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public ObservableMap<String, Authorize> getAuthorizeMap()
	{
		return FXCollections.unmodifiableObservableMap(authorizeMap);
	}

	@Override
	public ReadOnlyObjectProperty<Authorize> authorizeInstanceProperty()
	{
		return authorized;
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
		this.account.set(account);
	}

	@Override
	public String getPassword() {return password.get();}

	@Override
	public void setPassword(String password)
	{
		Objects.requireNonNull(password);
		authorized.get().validatePassword(password);
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
	public Authorize getAuthorizeInstance() {return authorized.get();}

	@Override
	public void setCache(AuthInfo info)
	{
		this.addToHistory(info.getUsername());
		this.cache.set(info);
	}

	private void addToHistory(String account)
	{
		String id = Authorize.getID(getAuthorizeInstance());
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

	@Override
	public ObservableList<String> getHistoryList()
	{
		String id = Authorize.getID(getAuthorizeInstance());
		ObservableList<String> strings = getHistoryMap().get(id);
		if (strings == null)
			historyMap.put(id, strings = FXCollections.observableArrayList());
		return strings;
	}
}
