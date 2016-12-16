package net.wheatlauncher.internal.io;

import net.launcher.AuthModule;
import net.launcher.AuthProfile;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.Logger;

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
	@Override
	public void forceSave() throws IOException
	{
		AuthProfile authProfile = getInstance();
		if (authProfile == null) throw new IllegalStateException();
		NBTCompound history = NBT.compound();
		authProfile.getHistoryMap().forEach((k, v) -> history.put(k, NBT.list(v)));
		Path target = getContext().getRoot().resolve("auth.dat");
		NBT.overwrite(target, NBT.compound()
				.put("auth", Authorize.getID(authProfile.getAuthorize()))
				.put("account", authProfile.getAccount())
				.put("history", history), true);
	}

	@Override
	public AuthProfile loadInstance() throws IOException
	{
		Logger.trace("start to load auth instance");
		Path path = getContext().getRoot().resolve("auth.dat");
		NBTCompound compound = NBT.read(path, true).asCompound();
		System.out.println(compound);
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		Optional<Authorize> authorizeOptional = AuthorizeFactory.find(auth);
		Authorize authorize = authorizeOptional.orElse(AuthorizeFactory.ONLINE);
		Logger.trace("loaded auth instance");
		return new AuthModule(account, authorize, (Map<String, List<String>>) compound.get("history").asCompound().asRaw());
	}

	@Override
	public AuthProfile defaultInstance() {return new AuthModule();}

	@Override
	protected void deploy()
	{
//		AuthProfile instance = getInstance();
//		List<Observable> observables = new ArrayList<>();
//		observables.add(instance.accountProperty());
//		observables.add(instance.authorizeProperty());
//		for (ObservableList<String> strings : instance.getHistoryMap().values())
//			observables.add(strings);
//		getContext().registerSaveTask(observables.toArray(new Observable[observables.size()]), this::forceSave);
	}
}
