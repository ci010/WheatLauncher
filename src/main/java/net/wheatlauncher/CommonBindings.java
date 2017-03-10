package net.wheatlauncher;

import api.launcher.ARML;
import api.launcher.event.ProfileEvent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import net.launcher.assets.MinecraftVersion;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ci010
 */
public class CommonBindings
{
	public static final Observable VERSION = new Observable()
	{
		private Set<InvalidationListener> listeners = new HashSet<>();

		{
			ARML.bus().addEventHandler(ProfileEvent.VERSION_CHANGE, event ->
			{
				if (ARML.core().getProfileManager().selecting() == event.getProfile())
					listeners.forEach(invalidationListener ->
							invalidationListener.invalidated(this));
			});
		}

		@Override
		public void addListener(InvalidationListener listener) {listeners.add(listener);}

		@Override
		public void removeListener(InvalidationListener listener) {listeners.remove(listener);}
	};

	public static final ObjectBinding<MinecraftVersion> CURRENT_VERSION_BINDING = Bindings.createObjectBinding(() ->
					ARML.core().getProfileManager().selecting().getMcVersion(),
			ARML.core().getProfileManager().selectedProfileProperty(), VERSION
	);
}
