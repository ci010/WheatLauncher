package net.launcher.version;

import net.launcher.AuthProfile;
import net.launcher.LaunchElementManager;
import net.launcher.LaunchOptionBuilder;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.resource.ArchiveRepository;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.version.Version;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class VersionMananger implements LaunchElementManager<Version>, LaunchOptionBuilder
{
	private ArchiveRepository<Version> versionArchiveRepository;

	@Override
	public Set<Version> getAllElement()
	{
		return null;
	}

	@Override
	public List<Version> getAllIncludedElement(LaunchProfile profile)
	{
		return null;
	}

	@Override
	public void manipulateIncludeElement(LaunchProfile profile, Consumer<List<Version>> manipulator)
	{

	}

	@Override
	public void onLaunch(LaunchOption option, LaunchProfile profile)
	{

	}

	@Override
	public void onClose(LaunchOption option, LaunchProfile profile)
	{

	}

	@Override
	public LaunchOption build(LaunchProfile launchProfile, AuthProfile authProfile)
	{
		return null;
	}
}
