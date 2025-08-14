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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class listens to when a player joins the server and sends the join message and motd depending on server and player settings.
 */
public class JoinListener implements Listener {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public JoinListener(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager,
            @NotNull PlayerDataManager playerDataManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Listens to when a player joins the server and sends the join message and motd depending on server and player settings.
     * @param playerJoinEvent A {@link PlayerJoinEvent}
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a join message to online players and the motd to player " + player.getName() + " due to invalid plugin settings."));
            return;
        }

        if(settings.globalJoinToggle() == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a join message to online players and the motd to player " + player.getName() + " due to an invalid global join toggle setting."));
            return;
        }

        if(settings.globalMotdToggle() == null) {
            logger.warn(AdventureUtil.serialize("Unable to send a join message to online players and the motd to player " + player.getName() + " due to an invalid global motd toggle setting."));
            return;
        }

        @NotNull CompletableFuture<@Nullable PlayerData> future = playerDataManager.loadPlayerData(uuid);
        future.thenAccept(playerData -> {
            if(playerData == null) {
                logger.warn(AdventureUtil.serialize("Unable to send a join message to online players and the motd to player " + player.getName() + " due to no player data retrieved."));
                return;
            }

            skyWelcome.getServer().getScheduler().runTask(skyWelcome, () -> {
                if(settings.globalJoinToggle() && playerData.isSendJoin()) {
                    skyWelcome.getServer().getOnlinePlayers().forEach(onlinePlayer ->
                            onlinePlayer.sendMessage(AdventureUtil.serialize(player, playerData.getJoinMessage())));
                }

                if(settings.globalMotdToggle() && playerData.isSendMotd()) {
                    settings.motd().forEach(message -> player.sendMessage(AdventureUtil.serialize(player, message)));
                }
            });
        });
    }
}
