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
package com.github.lukesky19.skywelcome.listener;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.data.player.PlayerData;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import com.github.lukesky19.skywelcome.util.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Listens to when a player disconnects from the server and sends their leave message if appropriate.
 */
public class QuitListener implements Listener {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull ComponentLogger logger;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull SettingsManager settingsManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public QuitListener(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager,
        @NotNull PlayerDataManager playerDataManager) {
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
        UUID uuid = player.getUniqueId();

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a leave message to players due to invalid plugin settings."));
            return;
        }

        if(settings.globalQuitToggle() == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a leave message to players due to an invalid global quit toggle setting."));
            return;
        }

        // Don't send a leave message if the player is vanished
        if(PluginUtils.isPlayerVanished(player)) return;

        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a leave message to players due due to no player data retrieved."));
            return;
        }

        if(settings.globalQuitToggle() && playerData.isSendLeave()) {
            Component joinMessage = AdventureUtil.serialize(player, playerData.getJoinMessage());
            skyWelcome.getServer().getOnlinePlayers()
                    .forEach(onlinePlayer -> onlinePlayer.sendMessage(joinMessage));
        }
    }
}
