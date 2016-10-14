# WheatLauncher
An easy minecraft launcher (in the future)

Require Java8 & javaFX.

#Setup
gradlew setup

Recommend to work with IDEA.

#Module(WIP)
The model that is raised up to solve the launch and Minecraft problems.
###Launcher Base(net.launcher)
> The basic model for Launching, `LaunchProfile`, and profile managing, `LaunchProfileManager`.

> I wasn't fully understand the design of javafx when I start on it. So the solution might looks strange.(hope not)

###Game(net.launcher.game)
> This part try to deal with the problem in Minecraft.

> It support with Minecraft `GameType`, `ServerInfo` and `WorldInfo`, including the (de)serialization.

> Overall, it could view/manipulate minecraft's data and used in launcher for the preview of world and server.

###NBT(net.launcher.nbt)
> The support of NBT manipulation and IO.

###Mod(net.launcher.mod)
> Basic model for MinecraftForge Mods.

> It groups the `Mod` by modid. The real mod specified by modid and version instance is called `Mod.Release` here.
> The `ModManager` provides the ability of read the `Mod` from Mod file(jar or folder) and serializes the `Mod` into the disk in order.

###Resource Pack(net.launcher.resourcepack)
> Basic model for Minecraft resource packs.

> Support to read resource pack information and icon.

###Game Settings(net.launcher.setting)
> This module could manipulate Minecraft's game settings.

> It has certain flexibility and extendability to support other settings, like Optifine, ShaderMod, and customized settings.

> Currently, Mod configs are not supported, but I plan to do it in the future.

###Auth(net.launcher.auth)
> An dependency module of LauncherBase. Required by `LaunchProfile`, and used to deal with the online/offline/customized login.

###Server Handshake Service(net.launcher.services)
> This module make it possible to handshake with a Minecraft server and ping its status.

> It supports MinecraftForge server, parsing the forge's mod lists. Therefore we could prepare mods to launch by this.

###Skin Profile Services(net.launcher.service.skin)
> Provides profile service for `UniskinAPI`, `CustomSkinLoaderAPI`, and custom skin service. (Mostly based on JMCCC)

###Skin Query Services(net.launcher.service.skin.query)
> This module provides fetching information of skin from skin sites, like SkinMe, MinecraftSkins, and Seuscraft. Basically, you could search and download the skin you want.
