/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skywelcome.paper.listener;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.locale.Locale;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.reward.RewardManager;
import com.github.lukesky19.skywelcome.paper.settings.Settings;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * This class listens to when a new player joins and when a player says welcome to distribute rewards.
 */
public class RewardListener implements Listener {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull RewardManager rewardManager;
    private boolean reward = false;
    private String newPlayerName;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param rewardManager A {@link RewardManager} instance.
     */
    public RewardListener(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull RewardManager rewardManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.rewardManager = rewardManager;
    }

    /**
     * Listens to an {@link AsyncChatEvent} for when a player says welcome for a new player and distributes rewards.
     * @param asyncChatEvent An {@link AsyncChatEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatEvent asyncChatEvent) {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.deserialize("Unable to process welcome rewards for a new player due to invalid plugin settings."));
            return;
        }
        if(settings.welcomeRewards().enabled() == null) {
            logger.error(AdventureUtility.deserialize("Unable to process welcome reward for new player due to invalid plugin settings. The boolean whether welcome rewards should be enabled is not configured."));
            return;
        }

        if(reward) {
            String message = PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()).toLowerCase();
            if(message.contains("welcome")) {
                List<TagResolver.Single> placeholders = List.of(
                        Placeholder.parsed("welcome_player", asyncChatEvent.getPlayer().getName()),
                        Placeholder.parsed("new_player", newPlayerName));

                reward = false;
                newPlayerName = null;

                rewardManager.giveReward(asyncChatEvent.getPlayer());

                Locale locale = localeManager.getConfiguration();
                for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                    if(player.isOnline() && player.isConnected()) {
                        player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.welcomeBroadcast(), placeholders));
                    }
                }
            }
        }
    }

    /**
     * Listens to a {@link PlayerJoinEvent} for when a new player joins.
     * @param playerJoinEvent A {@link PlayerJoinEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNewPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.deserialize("Unable to process new player due to invalid plugin settings."));
            return;
        }
        if(settings.welcomeRewards().enabled() == null) {
            logger.error(AdventureUtility.deserialize("Unable to process new player due to invalid plugin settings. The boolean whether welcome rewards should be enabled is not configured."));
            return;
        }

        if(!playerJoinEvent.getPlayer().hasPlayedBefore()) {
            reward = true;
            newPlayerName = playerJoinEvent.getPlayer().getName();
        }
    }

    /**
     * Listens to a {@link PlayerQuitEvent} for when a new player disconnects.
     * @param playerQuitEvent A {@link PlayerQuitEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNewPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if(!playerQuitEvent.getPlayer().getName().equals(newPlayerName)) return;

        Settings settings = settingsManager.getConfiguration();
        if(settings == null) return;
        if(settings.welcomeRewards().enabled() == null) return;

        reward = false;
        newPlayerName = null;
    }
}