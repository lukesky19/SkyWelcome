package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    final SkyWelcome skyWelcome;
    final PlayerManager playerManager;
    final SettingsManager settingsManager;
    final LocaleManager localeManager;

    public JoinListener(
            SkyWelcome skyWelcome,
            PlayerManager playerManager,
            SettingsManager settingsManager,
            LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Settings settings = settingsManager.getSettings();
        playerManager.createPlayerSettings(player);

        if(settings.options().motd()) {
            if(playerManager.getPlayerSettings(player).motd()) {
                localeManager.sendMotd(player);
            }
        }

        if(settings.options().joins()) {
            if(playerManager.getPlayerSettings(player).joinMessage()) {
                localeManager.sendJoinMessage(player);
            }
        }
    }
}
