package net.wheatlauncher.internal.repository;

import javafx.beans.value.ObservableValue;
import net.wheatlauncher.MinecraftRepository;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Library;

import java.util.Set;

/**
 * @author ci010
 */
public class LibRepository implements MinecraftRepository<Library>
{
	@Override
	public Set<String> getAllKey()
	{
		return null;
	}

	@Override
	public Library get(String key)
	{
		return null;
	}

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		newValue.getLibraries();
	}
}
