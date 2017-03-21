package api.launcher.auth;

import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.model.Authorize;

import java.util.List;

/**
 * @author ci010
 */
public interface AuthorizeProxy extends Authorize
{
	void load(Authorize authorize);

	ReadOnlyStringProperty idProperty();

	ReadOnlyStringProperty accountProperty();

	List<String> getAccountHistory();
}
