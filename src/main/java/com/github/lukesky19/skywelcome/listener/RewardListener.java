package com.github.lukesky19.skywelcome.listener;

import com.github.lukesky19.skywelcome.manager.RewardManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RewardListener implements Listener {
    private final RewardManager rewardManager;
    private boolean reward = false;

    public RewardListener(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if(reward) {
            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
            if(message.contains("welcome")) {
                reward = false;
                rewardManager.giveReward(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onNewPlayer(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore()) {
            reward = true;
        }
    }
}
