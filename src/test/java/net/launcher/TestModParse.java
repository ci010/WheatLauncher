package net.launcher;

import net.launcher.game.forge.ForgeMod;
import net.launcher.game.forge.ForgeModParser;
import net.launcher.utils.serial.Deserializer;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author ci010
 */
public class TestModParse
{
	@Test
	public void test()
	{
		Deserializer<ForgeMod[], Path> pathDeserializer = ForgeModParser.defaultModDeserializer();
		ForgeModParser forgeModParser = ForgeModParser.create();
		ForgeMod[] forgeMods = forgeModParser.parseFile(Paths.get("D:\\Storage\\Download\\mobends-0.22.5_for_MC-1.10.2.jar"));
		System.out.println(Arrays.toString(forgeMods));
	}
}
