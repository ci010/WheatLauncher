package net.wheatlauncher.internal.io;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.AuthProfile;
import net.launcher.Logger;
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
public class IOGuardAuth extends IOGuard<AuthProfile>
{
	@Override
	public void forceSave() throws IOException
	{
		AuthProfile authProfile = getInstance();
		if (authProfile == null) throw new IllegalStateException();
		NBTCompound history = NBT.compound();
		authProfile.getHistoryMap().forEach((k, v) -> history.put(k, NBT.listStr(v)));
		Path target = getContext().getRoot().resolve("auth.dat");
		NBT.write(target, NBT.compound()
				.put("auth", Authorize.getID(authProfile.getAuthorize()))
				.put("account", authProfile.getAccount())
				.put("history", history), false);
	}

	@Override
	public AuthProfile loadInstance() throws IOException
	{
		Logger.trace("start to load auth instance");
		Path path = getContext().getRoot().resolve("auth.dat");
		NBTCompound compound = NBT.read(path, false).asCompound();
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		Optional<Authorize> authorizeOptional = AuthorizeFactory.find(auth);
		Authorize authorize = authorizeOptional.orElse(AuthorizeFactory.ONLINE);
		Logger.trace("loaded auth instance");
		return new AuthProfile(account, authorize, (Map<String, List<String>>) compound.get("history").asCompound().asRaw());
	}

	@Override
	public AuthProfile defaultInstance() {return new AuthProfile();}

	@Override
	protected void deploy()
	{
		AuthProfile instance = getInstance();
		ObservableMap<String, ObservableList<String>> historyMap = instance.getHistoryMap();
		Save save = new Save();
		historyMap.addListener((MapChangeListener<String, ObservableList<String>>) change ->
		{
			ObservableList<String> valueAdded = change.getValueAdded();
			getContext().registerSaveTask(save, valueAdded);
		});
		for (ObservableList<String> strings : historyMap.values())
			getContext().registerSaveTask(save, strings);
		getContext().registerSaveTask(save,
				instance.accountProperty(), instance.authorizeProperty(), historyMap);
	}

	private class Save implements IOGuardContext.IOTask
	{
		@Override
		public void performance(Path root) throws Exception {forceSave();}

		@Override
		public boolean isEquivalence(IOGuardContext.IOTask task)
		{
			return task == this || task instanceof Save;
		}
	}
}
