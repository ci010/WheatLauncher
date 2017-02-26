package api.launcher;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.auth.Authorize;
import org.to2mbn.jmccc.auth.AuthInfo;

/**
 * @author ci010
 */
public interface AuthManager
{
	String getAuthorize();

	StringProperty authorizeProperty();

	void setAuthorize(String authorize);

	void setNoAuthorize();

	boolean isOffline();

	boolean registerAuth(Class<? extends Authorize> authClass);

	ObservableMap<String, Authorize> getAuthorizeMap();

	ReadOnlyObjectProperty<Authorize> authorizeInstanceProperty();

	String getAccount();

	ReadOnlyStringProperty accountProperty();

	ReadOnlyStringProperty passwordProperty();

	void setAccount(String account);

	String getPassword();

	void setPassword(String password);

	String getClientToken();

	void setClientToken(String clientToken);

	String getAccessToken();

	void setAccessToken(String accessToken);

	ObservableMap<String, ObservableList<String>> getHistoryMap();

	Authorize getAuthorizeInstance();

	void setCache(AuthInfo info);

	ObjectProperty<AuthInfo> cacheProperty();

	AuthInfo getCache();

	ObservableList<String> getHistoryList();
}
