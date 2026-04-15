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
package com.github.lukesky19.skywelcome.paper.integration.hooks;

import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import de.myzelyam.api.vanish.PostPlayerHideEvent;
import de.myzelyam.api.vanish.PostPlayerShowEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * This hook manages interfacing with SuperVanish and PremiumVanish.
 */
public class SuperVanishHook implements Hook, Listener {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull MessageBroker messageBroker;
    private boolean hooked = false;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     */
    public SuperVanishHook(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull MessageBroker messageBroker) {
        this.skyWelcome = skyWelcome;
        this.playerDataManager = playerDataManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Check if SuperVanish or PremiumVanish is enabled.
     */
    @Override
    public void initialize() {
        PluginManager pluginManager = skyWelcome.getServer().getPluginManager();
        hooked = pluginManager.isPluginEnabled("SuperVanish") || pluginManager.isPluginEnabled("PremiumVanish");

        if(hooked) {
            pluginManager.registerEvents(this, skyWelcome);
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return hooked;
    }

    /**
     * Update the stored vanish status in player data on vanish.
     * @param playerHideEvent A {@link PostPlayerHideEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(PostPlayerHideEvent playerHideEvent) {
        Player player = playerHideEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) return;

        playerData.setVanished(true);

        messageBroker.sendPlayerData(playerId, playerData);
    }

    /**
     * Update the stored vanish status in player data on un-vanish.
     * @param playerShowEvent A {@link PostPlayerShowEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnVanish(PostPlayerShowEvent playerShowEvent) {
        Player player = playerShowEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(playerId);
        if(playerData == null) return;

        playerData.setVanished(false);

        messageBroker.sendPlayerData(playerId, playerData);
    }
}