package com.gamerduck.betterjoin.commands;

import com.gamerduck.betterjoin.api.Config;
import com.gamerduck.betterjoin.api.Colors;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ReloadCommand extends CommandBase {
    private final String PERMISSION = "betterjoin.reload";
    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        super("betterjoin", "Better Join command", false);
        this.plugin = plugin;
    }

    protected void executeSync(@Nonnull CommandContext commandContext) {
        if (!commandContext.sender().hasPermission(PERMISSION)) {
            commandContext.sender().sendMessage(Colors.formatColorCodes(Config.getConfig().getNoPermission()));
        } else {
            try {
                Config.reloadConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            commandContext.sender().sendMessage(Colors.formatColorCodes(Config.getConfig().getMessageReloaded()));
        }
    }
}
