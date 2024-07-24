package com.github.lukesky19.skywelcome.managers;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.PlaceholderAPIUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class MessageManager {
    final SkyWelcome skyWelcome;
    final SettingsManager settingsManager;
    final PlayerManager playerManager;

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
                    player.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPIUtil.parsePlaceholders(joiningPlayer, join.content())));
                }
            }
        }
    }

    public void sendLeaveMessage(Player leavingPlayer) {
        if(playerManager.getPlayerSettings(leavingPlayer).leaveMessage()) {
            Settings.Quit quit = settingsManager.getSettings().quit();

            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPIUtil.parsePlaceholders(leavingPlayer, quit.content())));
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
}

