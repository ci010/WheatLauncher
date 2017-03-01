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
		ForgeMod[] forgeMods = forgeModParser.parseFile(Paths.get("D:\\Storage\\Desktop\\Floricraft-1.11.2-2.0.1.jar"));
		System.out.println(Arrays.toString(forgeMods));
	}
}
