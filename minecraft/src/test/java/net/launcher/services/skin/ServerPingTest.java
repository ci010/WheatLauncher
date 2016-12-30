package net.launcher.services.skin;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.services.MinecraftServerPingService;
import net.launcher.services.MinecraftServerPingServiceBuilder;
import org.junit.Test;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Created by zekunshen on 12/30/16.
 */
public class ServerPingTest {

    @Test
    public void pingServer(){
        // arbitary server
        ServerInfo testServer = new ServerInfo("Cloudgap","mc.cloudgap.net:25565","", ServerInfo.ResourceMode.DISABLED);
        Callback<ServerInfo> testCallback = new Callback<ServerInfo>(){
            @Override
            public void failed(Throwable e){
                System.out.println("Cannot download");
            }

            @Override
            public void done(ServerInfo result) {
                System.out.println("Done");
            }

            @Override
            public void cancelled() {
                System.out.println("Canceled");
            }
        };
        MinecraftServerPingService pingService = MinecraftServerPingServiceBuilder.buildDefault();
        Future<ServerStatus> future = pingService.fetchInfo(testServer,testCallback);
        try {
            System.out.println(future.get());
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }

}
