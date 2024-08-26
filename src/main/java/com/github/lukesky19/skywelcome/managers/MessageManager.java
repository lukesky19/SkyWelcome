package com.github.lukesky19.skywelcome.managers;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.FormatUtil;
import com.github.lukesky19.skywelcome.util.PlaceholderAPIUtil;
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
        PlayerSettings playerSettings = playerManager.getPlayerSettings(joiningPlayer);

        if(playerSettings.joinMessage()) {
            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(FormatUtil.format(joiningPlayer, playerSettings.selectedJoinMessage()));
                }
            }
        }
    }

    public void sendLeaveMessage(Player leavingPlayer) {
        PlayerSettings playerSettings = playerManager.getPlayerSettings(leavingPlayer);

        if(playerSettings.leaveMessage()) {
            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(FormatUtil.format(leavingPlayer, playerSettings.selectedLeaveMessage()));
                }
            }
        }
    }

    public void sendMotd(Player player) {
        if(playerManager.getPlayerSettings(player).motd()) {
            Settings.Motd motd = settingsManager.getSettings().motd();
            for(String message : motd.contents()) {
                player.sendMessage(FormatUtil.format(player, PlaceholderAPIUtil.parsePlaceholders(player, message)));
            }
        }
    }
}

