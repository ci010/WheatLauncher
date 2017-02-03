package net.launcher.control.profile.base;

import javafx.scene.control.Skin;

/**
 * @author ci010
 */
public class ProfileTableSelector extends ProfileSelector
{
	@Override
	protected Skin<?> createDefaultSkin() {return new ProfileSelectorComplexSkin(this, new Behav(this));}
}
