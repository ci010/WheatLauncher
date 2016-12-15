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

/**
 * @author ci010
 */
public class AuthIOGuard extends IOGuard<AuthProfile>
{
	public AuthIOGuard(Path root, IOGuardManger.IOQueue queue)
	{
		super(root, queue);
	}

	@Override
	public void forceSave(Path path) throws IOException
	{
		AuthProfile authProfile = getInstance();
		if (authProfile == null) throw new IllegalStateException();
		NBTCompound history = NBT.compound();
		authProfile.getHistoryMap().forEach((k, v) -> history.put(k, NBT.list(v)));
		Path target = getRoot().resolve("auth.dat");
		NBT.overwrite(target, NBT.compound()
				.put("auth", Authorize.getID(authProfile.getAuthorize()))
				.put("account", authProfile.getAccount())
				.put("history", history), true);
	}

	@Override
	public AuthProfile loadInstance() throws IOException
	{
		NBTCompound compound = NBT.read(getRoot().resolve("auth.dat"), true).asCompound();
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		Optional<Authorize> authorizeOptional = AuthorizeFactory.find(auth);
		Authorize authorize = authorizeOptional.orElse(AuthorizeFactory.ONLINE);
		AuthModule module = new AuthModule(account, authorize, (Map<String, List<String>>) compound.get("history").asCompound().asRaw());
		deploy();
		return module;
	}

	@Override
	protected void deploy()
	{
	}
}
