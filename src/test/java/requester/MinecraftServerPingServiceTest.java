package requester;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.services.MinecraftServerPingService;
import net.launcher.services.MinecraftServerPingServiceBuilder;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public class MinecraftServerPingServiceTest extends Application
{
	@Test
	public void pingLocal() throws Exception
	{
//		MinecraftServerPingService service = MinecraftServerPingServiceBuilder.buildDefault();
//		Future<ServerStatus> localhost = service.fetchInfo(new ServerInfo("", "localhost"), null);
//		ServerStatus serverStatus = localhost.get();
//		System.out.println(serverStatus);
	}


	@Test
	public void pingCrafter() throws ExecutionException, InterruptedException
	{
		MinecraftServerPingService service = MinecraftServerPingServiceBuilder.buildDefault();
		Future<ServerStatus> infoFuture = service.fetchInfo(new ServerInfo("", "crafter.me"), null);
		ServerStatus serverStatus = infoFuture.get();

		System.out.println(serverStatus);
	}

	@Test
	public void pingCloudGap() throws ExecutionException, InterruptedException
	{
		MinecraftServerPingService service = MinecraftServerPingServiceBuilder.buildDefault();
		Future<ServerStatus> infoFuture = service.fetchInfo(new ServerInfo("", "mc.cloudgap.net"), null);
		ServerStatus serverStatus = infoFuture.get();
		System.out.println(serverStatus);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
//		ServerInfo info = new ServerInfo("", "crafter.me");
		ServerInfo info = new ServerInfo("", "mc.cloudgap.net");
		MinecraftServerPingService service = MinecraftServerPingServiceBuilder.buildDefault();
		Future<ServerStatus> infoFuture = service.fetchInfo(info, null);
		ServerStatus serverStatus = infoFuture.get();

		System.out.println(serverStatus);

		ImageView imageView = new ImageView(ServerInfo.createServerIcon(info));
		Scene scene = new Scene(new StackPane(imageView));
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
