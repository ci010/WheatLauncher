package net.wheatlauncher.internal.io;

import net.launcher.AuthModule;
import net.launcher.AuthProfile;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class AuthIOGuard extends IOGuard<AuthProfile>
{
	public AuthIOGuard(Path root, ExecutorService service)
	{
		super(root, service);
	}

	Consumer<Consumer<Path>> queue;

	@Override
	public AuthProfile loadInstance() throws IOException
	{
		NBTCompound compound = NBT.read(getRoot().resolve("auth.dat"), true).asCompound();
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		Optional<Authorize> authorizeOptional = AuthorizeFactory.find(auth);
		Authorize authorize = authorizeOptional.orElse(AuthorizeFactory.ONLINE);
		AuthModule module = new AuthModule(account, authorize, (Map<String, List<String>>) compound.get("history").asCompound().asRaw());
		watch(module);
		return module;
	}

	private void watch(AuthProfile profile)
	{
	}
}
