import net.launcher.game.GameSettingsFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
public class TestClassGen
{
	public static void main(String[] args)
	{
		Map<String, String> setting = GameSettingsFactory.toMap(content);
		Map<String, List<String>> fieldMap = new HashMap<>();
		for (Map.Entry<String, String> entry : setting.entrySet())
		{
			String type = null;
			if (Character.isDigit(entry.getValue().charAt(0)))
			{
				type = "int";
				if (entry.getValue().contains("."))
					type = "double";
			}
			else if (entry.getValue().equals("true") || entry.getValue().equals("false"))
			{
				type = "boolean";
			}
			else if (entry.getValue().startsWith("["))
			{
				type = "String[]";
			}
			List<String> strings = fieldMap.get(type);
			if (strings == null)
				fieldMap.put(type, strings = new ArrayList<>());
			strings.add(entry.getKey());
		}
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, List<String>> stringListEntry : fieldMap.entrySet())
		{
			builder.append("private ").append(stringListEntry.getKey()).append(" ");
			for (String s : stringListEntry.getValue())
				builder.append(s).append(",");
			builder.deleteCharAt(builder.length() - 1);
			builder.append(";");
		}
		System.out.println(builder);
	}

	private static String content = "version:922\n" +
			"invertYMouse:false\n" +
			"mouseSensitivity:0.5\n" +
			"fov:0.0\n" +
			"gamma:0.45070422\n" +
			"saturation:0.0\n" +
			"renderDistance:12\n" +
			"guiScale:0\n" +
			"particles:0\n" +
			"bobView:true\n" +
			"anaglyph3d:false\n" +
			"maxFps:120\n" +
			"fboEnable:true\n" +
			"difficulty:1\n" +
			"fancyGraphics:false\n" +
			"ao:1\n" +
			"renderClouds:false\n" +
			"resourcePacks:[\"SE-Soundfix-v0.1.zip\"]\n" +
			"incompatibleResourcePacks:[]\n" +
			"lastServer:\n" +
			"lang:zh_cn\n" +
			"chatVisibility:0\n" +
			"chatColors:true\n" +
			"chatLinks:true\n" +
			"chatLinksPrompt:true\n" +
			"chatOpacity:1.0\n" +
			"snooperEnabled:true\n" +
			"fullscreen:true\n" +
			"enableVsync:false\n" +
			"useVbo:true\n" +
			"hideServerAddress:false\n" +
			"advancedItemTooltips:false\n" +
			"pauseOnLostFocus:true\n" +
			"touchscreen:false\n" +
			"overrideWidth:0\n" +
			"overrideHeight:0\n" +
			"heldItemTooltips:true\n" +
			"chatHeightFocused:1.0\n" +
			"chatHeightUnfocused:0.44366196\n" +
			"chatScale:1.0\n" +
			"chatWidth:1.0\n" +
			"showInventoryAchievementHint:false\n" +
			"mipmapLevels:4\n" +
			"forceUnicodeFont:false\n" +
			"reducedDebugInfo:false\n" +
			"useNativeTransport:true\n" +
			"entityShadows:true\n" +
			"mainHand:right\n" +
			"attackIndicator:1\n" +
			"showSubtitles:false\n" +
			"realmsNotifications:true\n" +
			"enableWeakAttacks:false\n" +
			"autoJump:false\n" +
			"key_key.attack:-100\n" +
			"key_key.use:-99\n" +
			"key_key.forward:17\n" +
			"key_key.left:30\n" +
			"key_key.back:31\n" +
			"key_key.right:32\n" +
			"key_key.jump:57\n" +
			"key_key.sneak:42\n" +
			"key_key.sprint:29\n" +
			"key_key.drop:16\n" +
			"key_key.inventory:18\n" +
			"key_key.chat:20\n" +
			"key_key.playerlist:15\n" +
			"key_key.pickItem:-98\n" +
			"key_key.command:53\n" +
			"key_key.screenshot:60\n" +
			"key_key.togglePerspective:63\n" +
			"key_key.smoothCamera:0\n" +
			"key_key.fullscreen:87\n" +
			"key_key.spectatorOutlines:0\n" +
			"key_key.swapHands:33\n" +
			"key_key.hotbar.1:2\n" +
			"key_key.hotbar.2:3\n" +
			"key_key.hotbar.3:4\n" +
			"key_key.hotbar.4:5\n" +
			"key_key.hotbar.5:6\n" +
			"key_key.hotbar.6:7\n" +
			"key_key.hotbar.7:8\n" +
			"key_key.hotbar.8:9\n" +
			"key_key.hotbar.9:10\n" +
			"key_of.key.zoom:46\n" +
			"soundCategory_master:1.0\n" +
			"soundCategory_music:1.0\n" +
			"soundCategory_record:1.0\n" +
			"soundCategory_weather:1.0\n" +
			"soundCategory_block:1.0\n" +
			"soundCategory_hostile:1.0\n" +
			"soundCategory_neutral:1.0\n" +
			"soundCategory_player:1.0\n" +
			"soundCategory_ambient:1.0\n" +
			"soundCategory_voice:1.0\n" +
			"modelPart_cape:true\n" +
			"modelPart_jacket:true\n" +
			"modelPart_left_sleeve:true\n" +
			"modelPart_right_sleeve:true\n" +
			"modelPart_left_pants_leg:true\n" +
			"modelPart_right_pants_leg:true\n" +
			"modelPart_hat:true\n";
}
