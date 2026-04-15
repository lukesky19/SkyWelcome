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
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.events.SkyWelcomeQuitEvent;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.Settings;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.github.lukesky19.skywelcome.paper.util.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * Listens to when a player disconnects from the server and sends their leave message if appropriate.
 */
public class QuitListener implements Listener {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull SettingsManager settingsManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public QuitListener(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.playerDataManager = playerDataManager;
        this.settingsManager = settingsManager;
    }

    /**
     * Listens for a {@link PlayerQuitEvent} and sends the player's leave message if appropriate.
     * @param playerQuitEvent A {@link PlayerQuitEvent}
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        UUID playerId = player.getUniqueId();

        if(!settingsManager.isProxyEnabled()) {
            Settings settings = settingsManager.getConfiguration();
            if(settings == null) {
                logger.warn(AdventureUtility.deserialize("Unable to send a leave message to players due to invalid plugin settings."));
                return;
            }

            // Don't send a leave message if the player is vanished
            if(PluginUtils.isPlayerVanished(player)) return;

            PlayerData playerData = playerDataManager.getPlayerData(playerId);
            if(playerData == null) {
                logger.warn(AdventureUtility.deserialize("Unable to send a leave message to players due due to no player data retrieved."));
                return;
            }

            if(settings.globalQuitToggle() && playerData.isSendLeave()) {
                Component leaveMessage = PaperAdventureUtility.deserialize(player, playerData.getLeaveMessage());

                logger.info(leaveMessage);

                skyWelcome.getServer().getPluginManager().callEvent(new SkyWelcomeQuitEvent(
                        player, leaveMessage, PlainTextComponentSerializer.plainText().serialize(leaveMessage)));

                skyWelcome.getServer().getOnlinePlayers().forEach(onlinePlayer ->
                        onlinePlayer.sendMessage(leaveMessage));
            }
        }

        playerDataManager.unloadPlayerData(playerId);
    }
}