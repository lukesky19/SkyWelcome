package com.github.lukesky19.skywelcome.commands;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkyWelcomeCommand implements CommandExecutor, TabCompleter {
    final SkyWelcome skyWelcome;
    final PlayerManager playerManager;

    public SkyWelcomeCommand(SkyWelcome skyWelcome, PlayerManager playerManager) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            skyWelcome.getComponentLogger().info(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>This command is only available in-game.</red>"));
            return false;
        }

        switch(args[0]) {
            case "reload" -> {
                if(sender.hasPermission("skywelcome.reload")) {
                    skyWelcome.reload();
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>Plugin configuration reloaded.</aqua>"));
                    return true;
                }
            }

            case "toggle" -> {
                switch (args[1]) {
                    case "join" -> {
                        if (sender.hasPermission("skywelcome.command.toggle.join")) {
                            playerManager.toggleJoin((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).joinMessage()) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have enabled your join message.</aqua>"));
                            } else {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have disabled your join message.</aqua>"));
                            }
                            return true;
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>You do not have permission for this command.</red>"));
                            return false;
                        }
                    }

                    case "leave", "quit" -> {
                        if (sender.hasPermission("skywelcome.command.toggle.leave") || sender.hasPermission("skywelcome.command.toggle.quit")) {
                            playerManager.toggleLeave((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).leaveMessage()) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have enabled your leave message.</aqua>"));
                            } else {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have disabled your leave message.</aqua>"));
                            }
                            return true;
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>You do not have permission for this command.</red>"));
                            return false;
                        }
                    }

                    case "motd" -> {
                        if(sender.hasPermission("skywelcome.command.toggle.motd")) {
                            playerManager.toggleMotd((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).motd()) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have enabled your MOTD message.</aqua>"));
                            } else {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <aqua>You have disabled your MOTD message.</aqua>"));
                            }
                            return true;
                        }
                    }
                }
            }

            default -> {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>Unknown command.</red>"));
                return false;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("toggle");
        }

        if(args.length == 2) {
            return List.of("join", "leave", "quit", "motd");
        }

        return List.of();
    }
}
