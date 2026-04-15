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
package com.github.lukesky19.skywelcome.paper.player;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skywelcome.common.database.DatabaseManager;
import com.github.lukesky19.skywelcome.common.database.tables.PlayerDataTable;
import com.github.lukesky19.skywelcome.common.player.AbstractPlayerDataManager;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.player.data.legacy.PlayerSettings;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.github.lukesky19.skywelcome.paper.util.PluginUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * This class manages player data.
 */
public class PlayerDataManager extends AbstractPlayerDataManager {
    private final @NonNull ISkyPlugin skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull DatabaseManager databaseManager;
    private final @NonNull SettingsManager settingsManager;

    /**
     * Constructor
     * @param skyWelcome An {@link ISkyPlugin} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public PlayerDataManager(
            @NonNull ISkyPlugin skyWelcome,
            @NonNull DatabaseManager databaseManager,
            @NonNull SettingsManager settingsManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.databaseManager = databaseManager;
        this.settingsManager = settingsManager;
    }

    /**
     * Get the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} for the player.
     * @return The {@link PlayerData}. May be null.
     */
    @Override
    public @Nullable PlayerData getPlayerData(@NonNull UUID uuid) {
        if(playerDataMap.containsKey(uuid)) return playerDataMap.get(uuid);
        if(settingsManager.isProxyEnabled()) return null;

        String joinMessage = settingsManager.getDefaultJoinMessage();
        if(joinMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default join message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first join message as the default."));
            return null;
        }

        String leaveMessage = settingsManager.getDefaultLeaveMessage();
        if(leaveMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default leave message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first leave message as the default."));
            return null;
        }

        String serverChangeMessage = settingsManager.getDefaultServerChangeMessage();
        if(serverChangeMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default server change message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first server change message as the default."));
            return null;
        }

        PlayerData newPlayerData = new PlayerData(uuid, true, true, true, true, joinMessage, leaveMessage, serverChangeMessage);

        setPlayerData(uuid, newPlayerData, true);

        return newPlayerData;
    }

    /**
     * Get the {@link PlayerData} from the database. If no data exists, then a new {@link PlayerData} record will attempt to be created.
     * If the plugin's settings are invalid, the returned {@link PlayerData} will be null.
     * @param uuid The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}, which may be null.
     */
    @Override
    public @NonNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NonNull UUID uuid) {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        if(playerDataTable == null) return CompletableFuture.completedFuture(null);

        return playerDataTable.loadPlayerData(uuid).thenApply(playerData -> {
            if(playerData == null) {
                String joinMessage = settingsManager.getDefaultJoinMessage();
                if(joinMessage == null) {
                    logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default join message."));
                    logger.error(AdventureUtility.deserialize("The plugin chooses the first join message as the default."));
                    return null;
                }

                String leaveMessage = settingsManager.getDefaultLeaveMessage();
                if(leaveMessage == null) {
                    logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default leave message."));
                    logger.error(AdventureUtility.deserialize("The plugin chooses the first leave message as the default."));
                    return null;
                }

                String serverChangeMessage = settingsManager.getDefaultServerChangeMessage();
                if(serverChangeMessage == null) {
                    logger.error(AdventureUtility.deserialize("Unable to create player data due to an invalid default server change message."));
                    logger.error(AdventureUtility.deserialize("The plugin chooses the first server change message as the default."));
                    return null;
                }

                PlayerData newPlayerData = new PlayerData(uuid, true, true, true, true, joinMessage, leaveMessage, serverChangeMessage);

                setPlayerData(uuid, newPlayerData, true);

                return newPlayerData;
            }

            setPlayerData(uuid, playerData, false);

            return playerData;
        });
    }

    /**
     * Get the {@link PlayerData} stored in memory or load it from the database.
     * If no data exists, then a new {@link PlayerData} record will attempt to be created.
     * If the plugin's settings are invalid, the returned {@link PlayerData} will be null.
     * @param playerId The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}, which may be null.
     */
    @Override
    public @NonNull CompletableFuture<@Nullable PlayerData> getOrLoadPlayerData(@NonNull UUID playerId) {
        if(playerDataMap.containsKey(playerId)) return CompletableFuture.completedFuture(playerDataMap.get(playerId));

        return loadPlayerData(playerId);
    }

    /**
     * Save the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    @Override
    public void savePlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData) {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        if(playerDataTable == null) return;

        playerDataTable.savePlayerData(uuid, playerData);
    }

    /**
     * Load all legacy player settings and migrate it to legacy player data and save the updated player data to the database.
     */
    @Override
    public void migrateLegacyPlayerSettings() {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        if(playerDataTable == null) return;

        String defaultJoinMessage = settingsManager.getDefaultJoinMessage();
        if(defaultJoinMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate legacy player data due to an invalid default join message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first join message as the default."));
            return;
        }

        String defaultLeaveMessage = settingsManager.getDefaultLeaveMessage();
        if(defaultLeaveMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate legacy player data due to an invalid default leave message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first leave message as the default."));
            return;
        }

        String defaultServerChangeMessage = settingsManager.getDefaultServerChangeMessage();
        if(defaultServerChangeMessage == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate legacy player data due to an invalid default server change message."));
            logger.error(AdventureUtility.deserialize("The plugin chooses the first leave message as the default."));
            return;
        }

        Path playerDataPath = Path.of(skyWelcome.getDirectoryFile() + File.separator + "playerdata");
        // If the path is not a directory, don't migrate any data.
        if(!Files.isDirectory(playerDataPath)) return;

        // Don't migrate player data if the path's directory doesn't exist.
        if(!Files.exists(playerDataPath)) return;

        try(Stream<Path> paths = Files.walk(playerDataPath)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        String fileName = path.toFile().getName();
                        String nameWithoutExtension = fileName.replaceAll("\\.yml$", "");
                        UUID uuid = UUID.fromString(nameWithoutExtension);

                        YamlConfigurationLoader loader = createLoader(path);
                        try {
                            PlayerSettings playerSettings = loader.load().get(PlayerSettings.class);
                            if(playerSettings != null) {
                                PlayerData playerData = migrateLegacyPlayerSettings(uuid, playerSettings, defaultJoinMessage, defaultLeaveMessage, defaultServerChangeMessage);

                                playerDataTable.savePlayerData(uuid, playerData);

                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    logger.warn(AdventureUtility.deserialize("Failed to delete legacy player data for file: " + path.toFile() + ". Error: " + e.getMessage()));
                                }
                            }
                        } catch (ConfigurateException e) {
                            logger.warn(AdventureUtility.deserialize("Failed to migrate legacy player data for file: " + path.toFile() + ". Error: " + e.getMessage()));
                        }
                    });
        } catch (IOException e) {
            logger.warn(AdventureUtility.deserialize("Failed to migrate legacy player data. Error: " + e.getMessage()));
            return;
        }

        // If the player data folder is empty, delete the directory
        try(Stream<Path> paths = Files.list(playerDataPath)) {
            int count = paths.toList().size();
            if(count == 0) {
                Files.delete(playerDataPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the {@link PlayerData}. Pre-parses the messages and updates the player's vanish status.
     * @param player The {@link Player} the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData} to update.
     */
    public void updatePlayerData(@NonNull Player player, @NonNull PlayerData playerData) {
        String joinMessage = PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getJoinMessage()));
        String leaveMessage = PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getLeaveMessage()));
        String serverChangeMessage = PaperAdventureUtility.serialize(PaperAdventureUtility.deserialize(player, playerData.getServerChangeMessage()));

        // Unescape unparsed tags
        joinMessage = joinMessage.replace("\\<", "<");
        leaveMessage = leaveMessage.replace("\\<", "<");
        serverChangeMessage = serverChangeMessage.replace("\\<", "<");

        playerData.setParsedJoinMessage(joinMessage);
        playerData.setParsedLeaveMessage(leaveMessage);
        playerData.setParsedServerChangeMessage(serverChangeMessage);

        playerData.setVanished(PluginUtils.isPlayerVanished(player));
    }
}