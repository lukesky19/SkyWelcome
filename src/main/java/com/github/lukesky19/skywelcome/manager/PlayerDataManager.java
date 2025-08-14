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
package com.github.lukesky19.skywelcome.manager;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.data.player.PlayerData;
import com.github.lukesky19.skywelcome.data.player.legacy.PlayerSettings;
import com.github.lukesky19.skywelcome.manager.database.DatabaseManager;
import com.github.lukesky19.skywelcome.manager.database.tables.PlayerDataTable;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * This class manages player data.
 */
public class PlayerDataManager {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull DatabaseManager databaseManager;

    private final @NotNull Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public PlayerDataManager(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager,
            @NotNull DatabaseManager databaseManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Get the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} for the player.
     * @return The {@link PlayerData}. May be null.
     */
    public @Nullable PlayerData getPlayerData(@NotNull UUID uuid) {
        if(playerDataMap.containsKey(uuid)) return playerDataMap.get(uuid);

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.error(AdventureUtil.serialize("Unable to create player data due to invalid plugin settings."));
            return null;
        }

        if(settings.joinMessages().isEmpty()) {
            logger.error(AdventureUtil.serialize("Unable to create player data due to no join messages being configured."));
            return null;
        }

        if(settings.quitMessages().isEmpty()) {
            logger.error(AdventureUtil.serialize("Unable to create player data due to no leave messages being configured."));
            return null;
        }

        @Nullable String joinMessage = settings.joinMessages().getFirst().message();
        if(joinMessage == null) {
            logger.error(AdventureUtil.serialize("Unable to create player data due to an invalid default join message."));
            logger.error(AdventureUtil.serialize("The plugin chooses the first join message as the default."));
            return null;
        }

        @Nullable String leaveMessage = settings.quitMessages().getFirst().message();
        if(leaveMessage == null) {
            logger.error(AdventureUtil.serialize("Unable to create player data due to an invalid default leave message."));
            logger.error(AdventureUtil.serialize("The plugin chooses the first leave message as the default."));
            return null;
        }

        PlayerData newPlayerData = new PlayerData(true, true, true, joinMessage, leaveMessage);

        playerDataMap.put(uuid, newPlayerData);

        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        playerDataTable.savePlayerData(uuid, newPlayerData);

        return newPlayerData;
    }

    /**
     * Get the {@link PlayerData} from the database. If no data exists, then a new {@link PlayerData} record will attempt to be created.
     * If the plugin's settings are invalid, the returned {@link PlayerData} will be null.
     * @param uuid The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}, which may be null.
     */
    public @NotNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NotNull UUID uuid) {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();

        return playerDataTable.loadPlayerData(uuid).thenApply(playerData -> {
            if(playerData == null) {
                Settings settings = settingsManager.getSettings();
                if(settings == null) {
                    logger.error(AdventureUtil.serialize("Unable to create player data due to invalid plugin settings."));
                    return null;
                }

                if(settings.joinMessages().isEmpty()) {
                    logger.error(AdventureUtil.serialize("Unable to create player data due to no join messages being configured."));
                    return null;
                }

                if(settings.quitMessages().isEmpty()) {
                    logger.error(AdventureUtil.serialize("Unable to create player data due to no leave messages being configured."));
                    return null;
                }

                @Nullable String joinMessage = settings.joinMessages().getFirst().message();
                if(joinMessage == null) {
                    logger.error(AdventureUtil.serialize("Unable to create player data due to an invalid default join message."));
                    logger.error(AdventureUtil.serialize("The plugin chooses the first join message as the default."));
                    return null;
                }

                @Nullable String leaveMessage = settings.quitMessages().getFirst().message();
                if(leaveMessage == null) {
                    logger.error(AdventureUtil.serialize("Unable to create player data due to an invalid default leave message."));
                    logger.error(AdventureUtil.serialize("The plugin chooses the first leave message as the default."));
                    return null;
                }

                PlayerData newPlayerData = new PlayerData(true, true, true, joinMessage, leaveMessage);

                playerDataMap.put(uuid, newPlayerData);

                playerDataTable.savePlayerData(uuid, newPlayerData);

                return newPlayerData;
            }

            playerDataMap.put(uuid, playerData);

            return playerData;
        });
    }

    /**
     * Save the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    public void savePlayerData(@NotNull UUID uuid, PlayerData playerData) {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        playerDataTable.savePlayerData(uuid, playerData);
    }

    /**
     * Load all legacy player settings and migrate it to legacy player data and save the updated player data to the database.
     */
    public void migrateLegacyPlayerSettings() {
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate legacy player data due to invalid plugin settings."));
            return;
        }

        if(settings.joinMessages().isEmpty()) {
            logger.error(AdventureUtil.serialize("Unable to migrate legacy player data due to no join messages being configured."));
            return;
        }

        if(settings.quitMessages().isEmpty()) {
            logger.error(AdventureUtil.serialize("Unable to migrate legacy player data due to no leave messages being configured."));
            return;
        }

        @Nullable String defaultJoinMessage = settings.joinMessages().getFirst().message();
        if(defaultJoinMessage == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate legacy player data due to an invalid default join message."));
            logger.error(AdventureUtil.serialize("The plugin chooses the first join message as the default."));
            return;
        }

        @Nullable String defaultLeaveMessage = settings.quitMessages().getFirst().message();
        if(defaultLeaveMessage == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate legacy player data due to an invalid default leave message."));
            logger.error(AdventureUtil.serialize("The plugin chooses the first leave message as the default."));
            return;
        }

        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();

        Path playerDataPath = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata");
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

                        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
                        try {
                            PlayerSettings playerSettings = loader.load().get(PlayerSettings.class);
                            if(playerSettings != null) {
                                PlayerData playerData = migrateLegacyPlayerSettings(defaultJoinMessage, defaultLeaveMessage, playerSettings);

                                playerDataTable.savePlayerData(uuid, playerData);

                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    logger.warn(AdventureUtil.serialize("Failed to delete legacy player data for file: " + path.toFile() + ". Error: " + e.getMessage()));
                                }
                            }
                        } catch (ConfigurateException e) {
                            logger.warn(AdventureUtil.serialize("Failed to migrate legacy player data for file: " + path.toFile() + ". Error: " + e.getMessage()));
                        }
                    });
        } catch (IOException e) {
            logger.warn(AdventureUtil.serialize("Failed to migrate legacy player data. Error: " + e.getMessage()));
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
     * Migrate the legacy player data.
     * @param defaultJoinMessage The default join message.
     * @param defaultLeaveMessage The default leave message.
     * @param playerSettings The legacy {@link PlayerSettings}.
     * @return The migrated {@link PlayerData}.
     */
    private @NotNull PlayerData migrateLegacyPlayerSettings(@NotNull String defaultJoinMessage, @NotNull String defaultLeaveMessage, PlayerSettings playerSettings) {
        boolean sendJoin = true;
        boolean sendMotd = true;
        boolean sendLeave = true;
        String joinMessage = defaultJoinMessage;
        String leaveMessage = defaultLeaveMessage;

        if(playerSettings.joinMessage() != null) sendJoin = playerSettings.joinMessage();
        if(playerSettings.motd() != null) sendMotd = playerSettings.motd();
        if(playerSettings.leaveMessage() != null) sendLeave = playerSettings.leaveMessage();
        if(playerSettings.selectedJoinMessage() != null) joinMessage = playerSettings.selectedJoinMessage();
        if(playerSettings.selectedLeaveMessage() != null) leaveMessage = playerSettings.selectedLeaveMessage();

        return new PlayerData(sendJoin, sendMotd, sendLeave, joinMessage, leaveMessage);
    }
}
