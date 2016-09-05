package net.wheatlauncher;

import javafx.beans.value.ChangeListener;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.util.Set;

/**
 * @author ci010
 */
public interface MinecraftRepository<T> extends ChangeListener<MinecraftDirectory>
{
	Set<String> getAllKey();

	T get(String key);
}
