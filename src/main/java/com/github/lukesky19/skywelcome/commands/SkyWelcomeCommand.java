package com.github.lukesky19.skywelcome.commands;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.util.FormatUtil;
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
    final LocaleManager localeManager;
    final JoinGUI joinGUI;
    final QuitGUI quitGUI;

    public SkyWelcomeCommand(
            SkyWelcome skyWelcome,
            PlayerManager playerManager,
            LocaleManager localeManager,
            JoinGUI joinGUI,
            QuitGUI quitGUI) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.localeManager = localeManager;
        this.joinGUI = joinGUI;
        this.quitGUI = quitGUI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Locale locale = localeManager.getLocale();

        switch(args[0]) {
            case "reload" -> {
                if(sender instanceof Player) {
                    if(sender.hasPermission("skywelcome.command." + args[0])) {
                        skyWelcome.reload();
                        sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.reload()));
                        return true;
                    } else {
                        sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                        return false;
                    }
                } else {
                    skyWelcome.reload();
                    skyWelcome.getComponentLogger().info(FormatUtil.format(locale.prefix() + locale.reload()));
                    return true;
                }
            }

            case "help" -> {
                if(sender.hasPermission("skywelcome.command." + args[0])) {
                    for(String str : locale.help()) {
                        sender.sendMessage(FormatUtil.format((Player) sender, str));
                    }
                    return true;
                } else {
                    sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                    return false;
                }
            }

            case "gui" -> {
                if(!(sender instanceof Player)) {
                    skyWelcome.getComponentLogger().info(FormatUtil.format(locale.prefix() + locale.playerOnly()));
                    return false;
                }

                switch (args[1].toLowerCase()) {
                    case "join" -> {
                        if(sender.hasPermission("skywelcome.command.gui." + args[1].toLowerCase())) {
                            joinGUI.createGUI((Player) sender);
                            joinGUI.openGUI((Player) sender);
                            return true;
                        } else {
                            sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                            return false;
                        }
                    }

                    case "leave", "quit" -> {
                        if(sender.hasPermission("skywelcome.command.gui." + args[1].toLowerCase())) {
                            quitGUI.createGUI((Player) sender);
                            quitGUI.openGUI((Player) sender);
                            return true;
                        } else {
                            sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                            return false;
                        }
                    }
                }
            }

            case "toggle" -> {
                if(!(sender instanceof Player)) {
                    skyWelcome.getComponentLogger().info(FormatUtil.format(locale.prefix() + locale.playerOnly()));
                    return false;
                }

                switch (args[1].toLowerCase()) {
                    case "join" -> {
                        if(sender.hasPermission("skywelcome.command.toggle." + args[1].toLowerCase())) {
                            playerManager.toggleJoin((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).joinMessage()) {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.joinEnabled()));
                            } else {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.joinDisabled()));
                            }
                            return true;
                        } else {
                            sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                            return false;
                        }
                    }

                    case "leave", "quit" -> {
                        if(sender.hasPermission("skywelcome.command.toggle." + args[1].toLowerCase())) {
                            playerManager.toggleLeave((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).leaveMessage()) {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.quitEnabled()));
                            } else {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.quitDisabled()));
                            }
                            return true;
                        } else {
                            sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                            return false;
                        }
                    }

                    case "motd" -> {
                        if(sender.hasPermission("skywelcome.command.toggle." + args[1].toLowerCase())) {
                            playerManager.toggleMotd((Player) sender);
                            if(playerManager.getPlayerSettings((Player) sender).motd()) {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.motdEnabled()));
                            } else {
                                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.motdDisabled()));
                            }
                            return true;
                        } else {
                            sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.noPermission()));
                            return false;
                        }
                    }
                }
            }

            default -> {
                sender.sendMessage(FormatUtil.format((Player) sender, locale.prefix() + locale.unknownCommand()));
                return false;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch(args.length) {
            case 1 -> {
                return List.of("help", "gui", "toggle", "reload");
            }

            case 2 -> {
                switch(args[0].toLowerCase()) {
                    case "gui" -> {
                        return List.of("join", "leave", "quit");
                    }

                    case "toggle" -> {
                        return List.of("join", "leave", "quit", "motd");
                    }
                }
            }
        }

        return List.of();
    }
}
