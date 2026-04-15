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
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import net.ess3.api.events.NickChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * This class manages interfacing with EssentialsX.
 */
public class EssentialsHook implements Hook, Listener {
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
    public EssentialsHook(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull MessageBroker messageBroker) {
        this.skyWelcome = skyWelcome;
        this.playerDataManager = playerDataManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Check if EssentialsX is enabled.
     */
    @Override
    public void initialize() {
        PluginManager pluginManager = skyWelcome.getServer().getPluginManager();
        hooked = pluginManager.isPluginEnabled("Essentials");

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
     * Update the stored vanish status in player data on vanish/unvanish.
     * @param vanishStatusChangeEvent A {@link VanishStatusChangeEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanishChange(VanishStatusChangeEvent vanishStatusChangeEvent) {
        Player player = vanishStatusChangeEvent.getAffected().getBase();
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) return;

        playerData.setVanished(vanishStatusChangeEvent.getValue());

        messageBroker.sendPlayerData(playerId, playerData);
    }

    /**
     * Reparse the join, leave, and server change message on nick change.
     * @param nickChangeEvent A {@link NickChangeEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNickChange(NickChangeEvent nickChangeEvent) {
        Player player = nickChangeEvent.getAffected().getBase();
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(playerId);
        if(playerData == null) return;

        skyWelcome.getServer().getScheduler().runTask(skyWelcome, () -> {
            playerData.setParsedJoinMessage(PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getJoinMessage())));

            playerData.setParsedLeaveMessage(PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getLeaveMessage())));

            playerData.setParsedServerChangeMessage(PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getServerChangeMessage())));

            messageBroker.sendPlayerData(playerId, playerData);
        });
    }
}