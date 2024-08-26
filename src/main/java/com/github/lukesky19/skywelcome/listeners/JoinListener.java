package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.managers.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    final SkyWelcome skyWelcome;
    final PlayerManager playerManager;
    final MessageManager messageManager;
    final SettingsManager settingsManager;

    public JoinListener(
            SkyWelcome skyWelcome,
            PlayerManager playerManager,
            MessageManager messageManager,
            SettingsManager settingsManager) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.messageManager = messageManager;
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Settings settings = settingsManager.getSettings();
        playerManager.createPlayerSettings(player);

        if(settings.options().motd()) {
            if(playerManager.getPlayerSettings(player).motd()) {
                messageManager.sendMotd(player);
            }
        }

        if(settings.options().joins()) {
            if(playerManager.getPlayerSettings(player).joinMessage()) {
                messageManager.sendJoinMessage(player);
            }
        }
    }
}
