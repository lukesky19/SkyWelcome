package com.github.lukesky19.skywelcome.listeners;

import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.managers.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    final PlayerManager playerManager;
    final MessageManager messageManager;

    public QuitListener(PlayerManager playerManager, MessageManager messageManager) {
        this.playerManager = playerManager;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(playerManager.getPlayerSettings(event.getPlayer()).joinMessage()) {
            messageManager.sendLeaveMessage(event.getPlayer());
        }
    }
}
