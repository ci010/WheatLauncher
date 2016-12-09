package net.launcher.auth;

import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

/**
 * @author ci010
 */

public interface Authorize
{
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface ID
	{
		String value();
	}

	void validateUserName(String name);

	void validatePassword(String password);

	AuthInfo auth(String account, String password) throws AuthenticationException;

	ProfileService createProfileService();

	static String getID(Authorize authorize)
	{
		Objects.requireNonNull(authorize);
		ID annotation = authorize.getClass().getAnnotation(ID.class);
		Objects.requireNonNull(annotation);
		return annotation.value();
	}
}
