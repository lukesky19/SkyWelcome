package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.managers.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    final SkyWelcome skyWelcome;
    final PlayerManager playerManager;
    final MessageManager messageManager;

    public JoinListener(
            SkyWelcome skyWelcome,
            PlayerManager playerManager,
            MessageManager messageManager) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerManager.createPlayerSettings(player);

        if(playerManager.getPlayerSettings(player).motd()) {
            messageManager.sendMotd(player);
        }

        if(playerManager.getPlayerSettings(player).joinMessage()) {
            messageManager.sendJoinMessage(player);
        }
    }
}
