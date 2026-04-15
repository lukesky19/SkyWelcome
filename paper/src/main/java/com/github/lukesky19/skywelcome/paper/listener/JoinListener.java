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
import com.github.lukesky19.skywelcome.paper.events.SkyWelcomeJoinEvent;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class listens to when a player joins the server and sends the join message and MotD depending on server and player settings.
 */
public class JoinListener implements Listener {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public JoinListener(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Listens to when a player joins the server and sends the join message and MotD depending on server and player settings.
     * @param playerJoinEvent A {@link PlayerJoinEvent}
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent playerJoinEvent) {
        if(settingsManager.isProxyEnabled()) return;
        Player player = playerJoinEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        Settings settings = settingsManager.getConfiguration();

        CompletableFuture<@Nullable PlayerData> future = playerDataManager.loadPlayerData(playerId);
        future.thenAccept(playerData -> {
            if(playerData == null) {
                logger.warn(AdventureUtility.deserialize("Unable to send a join message to online players and the MotD to player " + player.getName() + " due to no player data loaded."));
                return;
            }

            if(settings == null) {
                logger.warn(AdventureUtility.deserialize("Unable to send a join message to online players and the MotD to player " + player.getName() + " due to invalid plugin settings."));
                return;
            }

            skyWelcome.getServer().getScheduler().runTask(skyWelcome, () -> {
                if(!player.isOnline() || !player.isConnected()) {
                    logger.warn(AdventureUtility.plain("Aborting sending of the join message online players and the MotD to player " + player.getName() + " as the joining player is no longer online or connected."));
                    return;
                }

                if(settings.globalJoinToggle() && playerData.isSendJoin() && !PluginUtils.isPlayerVanished(player)) {
                    Component joinMessage = PaperAdventureUtility.deserialize(player, playerData.getJoinMessage());

                    logger.info(joinMessage);

                    skyWelcome.getServer().getPluginManager().callEvent(new SkyWelcomeJoinEvent(
                            player, joinMessage, PlainTextComponentSerializer.plainText().serialize(joinMessage)));

                    skyWelcome.getServer().getOnlinePlayers().forEach(onlinePlayer ->
                            onlinePlayer.sendMessage(joinMessage));
                }

                if(settings.globalMotdToggle() && playerData.isSendMotd()) {
                    settings.motd().forEach(message -> player.sendMessage(PaperAdventureUtility.deserialize(player, message)));
                }
            });
        });
    }
}