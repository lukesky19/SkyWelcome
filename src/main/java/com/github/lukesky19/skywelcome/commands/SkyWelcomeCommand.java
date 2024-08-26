package com.github.lukesky19.skywelcome.commands;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.gui.JoinMessageGUI;
import com.github.lukesky19.skywelcome.gui.QuitMessageGUI;
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
    final JoinMessageGUI joinMessageGUI;
    final QuitMessageGUI quitMessageGUI;

    public SkyWelcomeCommand(SkyWelcome skyWelcome, PlayerManager playerManager, JoinMessageGUI joinMessageGUI, QuitMessageGUI quitMessageGUI) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.joinMessageGUI = joinMessageGUI;
        this.quitMessageGUI = quitMessageGUI;
    }

    // TODO Configurable Messages
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

            case "gui" -> {
                switch(args[1]) {
                    case "join" -> {
                        if(sender.hasPermission("skywelcome.command.gui.join")) {
                            joinMessageGUI.createGUI((Player) sender);
                            joinMessageGUI.openGUI((Player) sender);
                            return true;
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>You do not have permission for this command.</red>"));
                            return false;
                        }
                    }
                    case "leave", "quit" -> {
                        if(sender.hasPermission("skywelcome.command.gui.quit") || sender.hasPermission("skywelcome.command.gui.leave")) {
                            quitMessageGUI.createGUI((Player) sender);
                            quitMessageGUI.openGUI((Player) sender);
                            return true;
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> <red>You do not have permission for this command.</red>"));
                            return false;
                        }
                    }
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
        switch(args.length) {
            case 1 -> {
                // TODO Permission Checks
                return List.of("gui", "toggle", "reload");
            }

            case 2 -> {
                switch(args[0].toLowerCase()) {
                    case "gui" -> {
                        // TODO Permission Checks
                        return List.of("join", "leave", "quit");
                    }

                    case "toggle" -> {
                        // TODO Permission Checks
                        return List.of("join", "leave", "quit", "motd");
                    }
                }
            }
        }

        return List.of();
    }
}
