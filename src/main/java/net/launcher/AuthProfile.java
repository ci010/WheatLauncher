package net.launcher;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.auth.Authorize;
import org.to2mbn.jmccc.auth.AuthInfo;

/**
 * @author ci010
 */
public interface AuthProfile
{
	ReadOnlyStringProperty accountProperty();

	ReadOnlyStringProperty passwordProperty();

	ReadOnlyObjectProperty<Authorize> authorizeProperty();

	String getAccount();

	void setAccount(String name);

	String getPassword();

	void setPassword(String password);

	Authorize getAuthorize();

	void setAuthorize(Authorize authorize);

	String getClientToken();

	void setClientToken(String clientToken);

	String getAccessToken();

	void setAccessToken(String accessToken);

	ObservableMap<String, ObservableList<String>> getHistoryMap();

	default ObservableList<String> getHistoryList() {return getHistoryMap().get(Authorize.getID(getAuthorize()));}

	void setCache(AuthInfo info);

	ObjectProperty<AuthInfo> cacheProperty();

	AuthInfo getCache();
}
