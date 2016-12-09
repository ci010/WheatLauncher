package net.launcher;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
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

	ReadOnlyObjectProperty<State> stateProperty();

	State getState();

	void setCache(AuthInfo info);

	AuthInfo getCache();

	enum State
	{
		Login, Logout, Pending
	}
}
