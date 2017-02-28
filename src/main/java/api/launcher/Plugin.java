package api.launcher;


/**
 * @author ci010
 */
public interface Plugin
{
	void preload(EventBus bus) throws Exception;

	void onLoad(ARML arml) throws Exception;
}
