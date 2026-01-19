package com.gamerduck.betterjoin;

import com.gamerduck.betterjoin.api.Config;
import com.gamerduck.betterjoin.commands.ReloadCommand;
import com.gamerduck.betterjoin.api.Colors;
import com.gamerduck.betterjoin.early.TransformerCompiler;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.EventTitleUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class BetterJoinPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final Path playersData = Path.of("universe").resolve("players");
    private final Path earlyPlugins = Path.of("earlyplugins");

    public BetterJoinPlugin(@Nonnull JavaPluginInit init) throws IOException {
        super(init);
            Config.initialize(this, this.withConfig("config", Config.CODEC))
                    .thenRun(() -> {
                        // TODO - Make the disabled jar a configuration value, for now have people manually rename
//                        if (Config.getConfig().isDisableLeaveMessages()
//                                && Files.exists(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar.disabled"))) {
//                            File file = new File(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar.disabled").toUri());
//                            file.renameTo(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar").toFile());
//                            file.delete();
//                        } else if (Files.exists(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar"))) {
//                            File file = new File(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar").toUri());
//                            file.renameTo(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar.disabled").toFile());
//                            file.delete();
//                        }
                    });

        if (Files.notExists(earlyPlugins)) {
            Files.createDirectories(earlyPlugins);
        }

        if (Files.notExists(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar"))
                || Files.notExists(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar.disabled"))) {
            try {
                TransformerCompiler compiler = new TransformerCompiler();

                compiler.createJarFromCompiledClass("earlyplugins/BetterJoinEarlyPlugin.jar");

                File file = new File(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar").toUri());
                file.renameTo(earlyPlugins.resolve("BetterJoinEarlyPlugin.jar.disabled").toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ReloadCommand(this));

        this.getEventRegistry().registerGlobal(PlayerSetupConnectEvent.class, this::onPlayerFirstJoin);
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, e -> e.setBroadcastJoinMessage(!Config.getConfig().isDisableJoinMessages()));
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);

    }

    private void onPlayerFirstJoin(PlayerSetupConnectEvent e) {
        Path playerPath = playersData.resolve(e.getUuid().toString() + ".json");
        if (Files.notExists(playerPath)) {
            String message = Config.getConfig().getWelcomeMessage();
            if (Config.getConfig().isUseTitles()) {
                Iterator<String> iterator = message.replace("{player}", e.getUsername()).replaceAll("[&§]([0-9a-fk-or])", "").lines().iterator();
                Message topLine = Message.raw(iterator.next());
                StringBuilder rest = new StringBuilder();
                iterator.forEachRemaining(s -> {
                    rest.append(s);
                    if (iterator.hasNext()) rest.append("\n");
                });
                Message restLines = Message.raw(rest.toString());
                Universe.get().getPlayers().forEach((playerRef) -> {
                    if (playerRef.getWorldUuid() != null) {
                        World world = Universe.get().getWorld(playerRef.getWorldUuid());
                        world.execute(() -> {
                            EventTitleUtil.showEventTitleToPlayer(playerRef, restLines, topLine, true, null, 5, 1, 1);
                        });
                    }
                });
            } else {
                Universe.get().sendMessage(Colors.formatColorCodes(message.replace("{player}", e.getUsername())));
            }
        }
    }

    private void onPlayerConnect(PlayerConnectEvent e) {
        String message = Config.getConfig().getJoinMessage();
        if (Config.getConfig().isUseTitles()) {
            Iterator<String> iterator = message.replace("{player}", e.getPlayerRef().getUsername()).replaceAll("[&§]([0-9a-fk-or])", "").lines().iterator();
            Message topLine = Message.raw(iterator.next());
            StringBuilder rest = new StringBuilder();
            iterator.forEachRemaining(s -> {
                rest.append(s);
                if (iterator.hasNext()) rest.append("\n");
            });
            Message restLines = Message.raw(rest.toString());
            Universe.get().getPlayers().forEach((playerRef) -> {
                if (playerRef.getWorldUuid() != null) {
                    World world = Universe.get().getWorld(playerRef.getWorldUuid());
                    world.execute(() -> {
                        EventTitleUtil.showEventTitleToPlayer(playerRef, restLines, topLine, false, null, 5, 1, 1);
                    });
                }
            });
        } else {
            Universe.get().sendMessage(Colors.formatColorCodes(message.replace("{player}", e.getPlayerRef().getUsername())));
        }
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent e) {
        String message = Config.getConfig().getLeaveMessage();
        if (Config.getConfig().isUseTitles()) {
            Iterator<String> iterator = message.replace("{player}", e.getPlayerRef().getUsername()).replaceAll("[&§]([0-9a-fk-or])", "").lines().iterator();
            Message topLine = Message.raw(iterator.next());
            StringBuilder rest = new StringBuilder();
            iterator.forEachRemaining(s -> {
                rest.append(s);
                if (iterator.hasNext()) rest.append("\n");
            });
            Message restLines = Message.raw(rest.toString());
            Universe.get().getPlayers().forEach((playerRef) -> {
                if (playerRef.getWorldUuid() != null) {
                    World world = Universe.get().getWorld(playerRef.getWorldUuid());
                    world.execute(() -> {
                        EventTitleUtil.showEventTitleToPlayer(playerRef, restLines, topLine, false, null, 5, 1, 1);
                    });
                }
            });
        } else {
            Universe.get().sendMessage(Colors.formatColorCodes(message.replace("{player}", e.getPlayerRef().getUsername())));
        }
    }

}