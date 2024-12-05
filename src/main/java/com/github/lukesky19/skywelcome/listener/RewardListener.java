package com.github.lukesky19.skywelcome.listener;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.manager.RewardManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class RewardListener implements Listener {
    private final SkyWelcome skyWelcome;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;
    private final RewardManager rewardManager;
    private boolean reward = false;
    private String newPlayerName;

    public RewardListener(SkyWelcome skyWelcome, SettingsManager settingsManager, LocaleManager localeManager, RewardManager rewardManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.rewardManager = rewardManager;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if(skyWelcome.isPluginDisabled()) return;
        if(!settingsManager.getSettings().welcomeRewards().enabled()) return;

        if(reward) {
            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
            if(message.contains("welcome")) {
                List<TagResolver.Single> placeholders = List.of(
                        Placeholder.parsed("welcome_player", event.getPlayer().getName()),
                        Placeholder.parsed("new_player", newPlayerName));

                reward = false;
                newPlayerName = null;

                rewardManager.giveReward(event.getPlayer());

                Locale locale = localeManager.getLocale();
                for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                    if(player.isOnline() && player.isConnected()) {
                        player.sendMessage(FormatUtil.format(player, locale.prefix() + locale.welcomeBroadcast(), placeholders));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onNewPlayerJoin(PlayerJoinEvent event) {
        if(skyWelcome.isPluginDisabled()) return;
        if(!settingsManager.getSettings().welcomeRewards().enabled()) return;

        if(!event.getPlayer().hasPlayedBefore()) {
            reward = true;
            newPlayerName = event.getPlayer().getName();
        }
    }

    @EventHandler
    public void onNewPlayerQuit(PlayerQuitEvent event) {
        if(!settingsManager.getSettings().welcomeRewards().rewardOfflineJoins()) {
            reward = false;
            newPlayerName = null;
        }
    }
}
