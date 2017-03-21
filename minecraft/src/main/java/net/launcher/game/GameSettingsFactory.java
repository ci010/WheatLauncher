package net.launcher.game;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public abstract class GameSettingsFactory
{
	//@formatter:off
	public abstract GameSettings.Option buildChatLinksPrompt(boolean value);
	public abstract GameSettings.Option buildUseNativeTransport(boolean value);
	public abstract GameSettings.Option buildChatScale(double value);
	public abstract GameSettings.Option buildUseVbo(boolean value);
	public abstract GameSettings.Option buildFancyGraphics(boolean value);
	public abstract GameSettings.Option buildEnableVsync(boolean value);
	public abstract GameSettings.Option buildShowInventoryAchievementHint(boolean value);
	public abstract GameSettings.Option buildParticles(int value);
	public abstract GameSettings.Option buildMouseSensitivity(double value);
	public abstract GameSettings.Option buildAdvancedItemTooltips(boolean value);
	public abstract GameSettings.Option buildTouchscreen(boolean value);
	public abstract GameSettings.Option buildEntityShadows(boolean value);
	public abstract GameSettings.Option buildIncompatibleResourcePacks(String[] value);
	public abstract GameSettings.Option buildVersion(int value);
	public abstract GameSettings.Option buildAo(int value);
	public abstract GameSettings.Option buildMipmapLevels(int value);
	public abstract GameSettings.Option buildAnaglyph3d(boolean value);
	public abstract GameSettings.Option buildRenderDistance(int value);
	public abstract GameSettings.Option buildFullscreen(boolean value);
	public abstract GameSettings.Option buildChatWidth(double value);
	public abstract GameSettings.Option buildChatHeightUnfocused(double value);
	public abstract GameSettings.Option buildRealmsNotifications(boolean value);
	public abstract GameSettings.Option buildShowSubtitles(boolean value);
	public abstract GameSettings.Option buildChatHeightFocused(double value);
	public abstract GameSettings.Option buildHideServerAddress(boolean value);
	public abstract GameSettings.Option buildMaxFps(int value);
	public abstract GameSettings.Option buildRenderClouds(boolean value);
	public abstract GameSettings.Option buildGuiScale(int value);
	public abstract GameSettings.Option buildForceUnicodeFont(boolean value);
	public abstract GameSettings.Option buildChatOpacity(double value);
	public abstract GameSettings.Option buildGamma(double value);
	public abstract GameSettings.Option buildResourcePacks(String[] value);
	public abstract GameSettings.Option buildSaturation(double value);
	public abstract GameSettings.Option buildChatColors(boolean value);
	public abstract GameSettings.Option buildHeldItemTooltips(boolean value);
	public abstract GameSettings.Option buildDifficulty(int value);
	public abstract GameSettings.Option buildMainHand(String value);
	public abstract GameSettings.Option buildOverrideHeight(int value);
	public abstract GameSettings.Option buildChatLinks(boolean value);
	public abstract GameSettings.Option buildAutoJump(boolean value);
	public abstract GameSettings.Option buildInvertYMouse(boolean value);
	public abstract GameSettings.Option buildReducedDebugInfo(boolean value);
	public abstract GameSettings.Option buildFov(double value);
	public abstract GameSettings.Option buildAttackIndicator(int value);
	public abstract GameSettings.Option buildSnooperEnabled(boolean value);
	public abstract GameSettings.Option buildFboEnable(boolean value);
	public abstract GameSettings.Option buildPauseOnLostFocus(boolean value);
	public abstract GameSettings.Option buildLang(String value);
	public abstract GameSettings.Option buildEnableWeakAttacks(boolean value);
	public abstract GameSettings.Option buildBobView(boolean value);
	public abstract GameSettings.Option buildOverrideWidth(int value);
	public abstract GameSettings.Option buildChatVisibility(int value);
	//@formatter:on

	public abstract GameSettings parse(String content);

	public abstract String toString(GameSettings settings);

	public static Map<String, String> toMap(String content)
	{
		String property = System.getProperty("line.separator");
		Map<String, String> map = new HashMap<>();
		String[] split = content.split(property);
		for (String s : split)
		{
			String[] keyVPair = s.split(":");
			if (keyVPair.length == 2)
				map.put(keyVPair[0], keyVPair[1]);
		}
		return map;
	}

	public static String fromMap(Map<String, String> map)
	{
		String property = System.getProperty("line.separator");
		String collect = map.entrySet().stream()
				.map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(property));
		collect = collect.concat(property);
		return collect;
	}
}
