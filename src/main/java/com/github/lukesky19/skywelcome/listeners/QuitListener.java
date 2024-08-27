package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    final PlayerManager playerManager;
    final SettingsManager settingsManager;
    final LocaleManager localeManager;

    public QuitListener(
            PlayerManager playerManager,
            SettingsManager settingsManager,
            LocaleManager localeManager) {
        this.playerManager = playerManager;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Settings settings = settingsManager.getSettings();

        if(settings.options().quits()) {
            if(playerManager.getPlayerSettings(event.getPlayer()).leaveMessage()) {
                localeManager.sendLeaveMessage(event.getPlayer());
            }
        }
    }
}
