package net.launcher.fx.auth;

import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.model.Authorize;

import java.util.List;

/**
 * @author ci010
 */
public interface Authorizer extends Authorize
{
	void load(Authorize authorize);

	ReadOnlyStringProperty idProperty();

	ReadOnlyStringProperty accountProperty();

	List<String> getAccountHistory();
}
