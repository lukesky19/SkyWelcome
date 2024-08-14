package com.github.lukesky19.skywelcome.managers;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.PlaceholderAPIUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.*;

public class MessageManager {
    final SkyWelcome skyWelcome;
    final SettingsManager settingsManager;
    final PlayerManager playerManager;

    final Map<String, String> codeConversion = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("§0", "<black>"),
            new AbstractMap.SimpleEntry<>("§1", "<dark_blue>"),
            new AbstractMap.SimpleEntry<>("§2", "<dark_green>"),
            new AbstractMap.SimpleEntry<>("§3", "<dark_aqua>"),
            new AbstractMap.SimpleEntry<>("§4", "<dark_red>"),
            new AbstractMap.SimpleEntry<>("§5", "<dark_purple>"),
            new AbstractMap.SimpleEntry<>("§6", "<gold>"),
            new AbstractMap.SimpleEntry<>("§7", "<gray>"),
            new AbstractMap.SimpleEntry<>("§8", "<dark_gray>"),
            new AbstractMap.SimpleEntry<>("§9", "<blue>"),
            new AbstractMap.SimpleEntry<>("§a", "<green>"),
            new AbstractMap.SimpleEntry<>("§b", "<aqua>"),
            new AbstractMap.SimpleEntry<>("§c", "<red>"),
            new AbstractMap.SimpleEntry<>("§d", "<light_purple>"),
            new AbstractMap.SimpleEntry<>("§e", "<yellow>"),
            new AbstractMap.SimpleEntry<>("§f", "<white>"),
            new AbstractMap.SimpleEntry<>("§k", "<obfuscated>"),
            new AbstractMap.SimpleEntry<>("§l", "<bold>"),
            new AbstractMap.SimpleEntry<>("§m", "<strikethrough>"),
            new AbstractMap.SimpleEntry<>("§n", "<underlined>"),
            new AbstractMap.SimpleEntry<>("§o", "<italic>"),
            new AbstractMap.SimpleEntry<>("§r", "<reset>")
    );

    public MessageManager(SkyWelcome skyWelcome, SettingsManager settingsManager, PlayerManager playerManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
    }

    public void sendJoinMessage(Player joiningPlayer) {
        if(playerManager.getPlayerSettings(joiningPlayer).joinMessage()) {
            Settings.Join join = settingsManager.getSettings().join();
            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(format(joiningPlayer, join.content()));
                }
            }
        }
    }

    public void sendLeaveMessage(Player leavingPlayer) {
        if(playerManager.getPlayerSettings(leavingPlayer).leaveMessage()) {
            Settings.Quit quit = settingsManager.getSettings().quit();

            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(format(leavingPlayer, quit.content()));
                }
            }
        }
    }

    public void sendMotd(Player player) {
        if(playerManager.getPlayerSettings(player).motd()) {
            Settings.Motd motd = settingsManager.getSettings().motd();
            for(String message : motd.contents()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPIUtil.parsePlaceholders(player, message)));
            }
        }
    }

    /**
     * Converts a String to a Component.
     * First, parses PlaceholderAPI placeholders.
     * Then, converts legacy §/& codes into a component format using the handleLegacyCodes(String message).
     * Lastly, converts the new String into a Component using MiniMessage.
     * @param player A player that placeholders will parse for.
     * @param message A string to format.
     * @return A formatted Component.
     */
    private Component format(Player player, String message) {
        return MiniMessage.miniMessage().deserialize(
                handleLegacyCodes(
                        PlaceholderAPIUtil.parsePlaceholders(player, message)));
    }

    private String handleLegacyCodes(String message) {
        StringBuilder builder = new StringBuilder(message);

        for(Map.Entry<String, String> codeEntry : codeConversion.entrySet()) {
            String target = codeEntry.getKey();
            String replacement = codeEntry.getValue();

            while(builder.toString().contains(target)) {
                int startIndex = builder.toString().indexOf(target);
                int stopIndex = startIndex + target.length();

                builder.replace(startIndex, stopIndex, replacement);
            }
        }

        return builder.toString();

    }
}

