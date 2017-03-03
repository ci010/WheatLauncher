package net.launcher;

import net.launcher.game.forge.ForgeMod;
import net.launcher.game.forge.ForgeModParser;
import net.launcher.utils.resource.FetchUtils;
import net.launcher.utils.serial.Deserializer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author ci010
 */
public class TestModParse
{
	@Test
	public void testLink() throws IOException, InterruptedException
	{
		Path path = Paths.get("C:\\Users\\cijhn\\AppData\\Roaming\\" +
				".launcher\\resourcepacks\\DCBDECE40CBF5387C731D288AA02313B.zip");
		Path another = Paths.get("C:\\Users\\cijhn\\AppData\\Roaming\\.launcher\\resourcepacks\\willpack2.0.zip");
		FetchUtils.fetch(path, another, null);
	}

	@Test
	public void testMove() throws IOException
	{
		Path desktop = Paths.get("D:\\Storage\\Desktop").resolve("a");
		Files.delete(desktop);
//
//		Path path = desktop.resolve("a");
//		Path another = Paths.get("D:\\a");
//		Files.copy(path, another);
	}


	@Test
	public void test()
	{
		Deserializer<ForgeMod[], Path> pathDeserializer = ForgeModParser.defaultModDeserializer();
		ForgeModParser forgeModParser = ForgeModParser.create();
		ForgeMod[] forgeMods = forgeModParser.parseFile(Paths.get("D:\\Storage\\Download\\mobends-0.22.5_for_MC-1.10.2.jar"));
		System.out.println(Arrays.toString(forgeMods));
	}
}
