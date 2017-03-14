package net.launcher.model.fx.auth;

import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.model.Authorize;

/**
 * @author ci010
 */
public interface Authorizer extends Authorize
{
	void load(Authorize authorize);

	ReadOnlyStringProperty idProperty();

	ReadOnlyStringProperty accountProperty();
}
