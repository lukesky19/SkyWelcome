package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.managers.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    final PlayerManager playerManager;
    final MessageManager messageManager;
    final SettingsManager settingsManager;

    public QuitListener(
            PlayerManager playerManager,
            MessageManager messageManager,
            SettingsManager settingsManager) {
        this.playerManager = playerManager;
        this.messageManager = messageManager;
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Settings settings = settingsManager.getSettings();

        if(settings.options().quits()) {
            if(playerManager.getPlayerSettings(event.getPlayer()).leaveMessage()) {
                messageManager.sendLeaveMessage(event.getPlayer());
            }
        }
    }
}
