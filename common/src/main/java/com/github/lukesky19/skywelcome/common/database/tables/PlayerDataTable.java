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
package com.github.lukesky19.skywelcome.common.database.tables;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.IntegerParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.LongParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.UUIDParameter;
import com.github.lukesky19.skywelcome.common.database.QueueManager;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.settings.ISettingsManager;
import com.github.lukesky19.skywelcome.common.util.MessageParameter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages access to the player data table in the database.
 */
public class PlayerDataTable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull ISettingsManager pluginSettings;
    private final @NonNull QueueManager queueManager;
    private final @NonNull VersionsTable versionsTable;
    private final @NonNull String tableName = "skywelcome_player_data";

    /**
     * Default Constructor.
     * You should use {@link #PlayerDataTable(ComponentLogger, ISettingsManager, QueueManager, VersionsTable)} instead.
     * @deprecated You should use {@link #PlayerDataTable(ComponentLogger, ISettingsManager, QueueManager, VersionsTable)} instead.
     */
    @Deprecated
    public PlayerDataTable() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param pluginSettings A {@link ISettingsManager} instance.
     * @param queueManager A {@link QueueManager} instance.
     * @param versionsTable A {@link VersionsTable} instance.
     */
    public PlayerDataTable(
            @NonNull ComponentLogger logger,
            @NonNull ISettingsManager pluginSettings,
            @NonNull QueueManager queueManager,
            @NonNull VersionsTable versionsTable) {
        this.logger = logger;
        this.pluginSettings = pluginSettings;
        this.queueManager = queueManager;
        this.versionsTable = versionsTable;
    }

    /**
     * Creates the table in the database if it doesn't exist and any indexes that don't exist.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "player_id LONG NOT NULL UNIQUE DEFAULT 0, " +
                "send_join INTEGER NOT NULL DEFAULT 1, " +
                "send_motd INTEGER NOT NULL DEFAULT 1, " +
                "send_leave INTEGER NOT NULL DEFAULT 1, " +
                "send_server_change INTEGER NOT NULL DEFAULT 1, " +
                "join_message TEXT NOT NULL, " +
                "leave_message TEXT NOT NULL, " +
                "server_change_message TEXT, " + // May be null due to legacy data
                "last_updated LONG NOT NULL DEFAULT 0)";
        String playerIdsIndexSql = "CREATE INDEX IF NOT EXISTS idx_" + tableName + "_player_ids ON " + tableName + "(player_id)";

        // Create the table if it doesn't exist
        return queueManager.queueBulkWriteTransaction(List.of(tableCreationSql, playerIdsIndexSql)).thenCompose(_ -> {
            // Get the table version
            return versionsTable.getVersion(tableName).thenCompose(version -> {
                // If -1, assume outdated format and data needs migrated
                if(version == -1) {
                    String temporaryTableName = "skywelcome_player_data_temp";
                    String temporaryTableCreationSql = "CREATE TABLE IF NOT EXISTS " + temporaryTableName + " (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "player_id LONG NOT NULL UNIQUE DEFAULT 0, " +
                            "send_join INTEGER NOT NULL DEFAULT 1, " +
                            "send_motd INTEGER NOT NULL DEFAULT 1, " +
                            "send_leave INTEGER NOT NULL DEFAULT 1, " +
                            "send_server_change INTEGER NOT NULL DEFAULT 1, " +
                            "join_message TEXT NOT NULL, " +
                            "leave_message TEXT NOT NULL, " +
                            "server_change_message TEXT, " + // May be null due to legacy data
                            "last_updated LONG NOT NULL DEFAULT 0)";

                    // Create a temporary table to store the original table's data in.
                    return queueManager.queueWriteTransaction(temporaryTableCreationSql).thenCompose(_ -> {
                        // Get all player data stored in the table and insert them into the new table
                        return loadLegacyPlayerData().thenCompose(list -> {
                            if(list == null) return CompletableFuture.completedFuture(null);

                            String insertOrUpdateSql = "INSERT INTO " + temporaryTableName + " (player_id, send_join, send_motd, send_leave, send_server_change, join_message, leave_message, server_change_message, last_updated) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "ON CONFLICT (player_id) DO UPDATE SET " +
                                    "send_join = ?, send_motd = ?, send_leave = ?, send_server_change = ?, join_message = ?, leave_message = ?, server_change_message = ?, last_updated = ? WHERE last_updated < ?";
                            List<List<Parameter<?>>> listOfParameterLists = new ArrayList<>();

                            list.forEach(playerData -> {
                                UUIDParameter playerIdParameter = new UUIDParameter(playerData.getPlayerId());
                                IntegerParameter sendJoinParameter = new IntegerParameter(playerData.isSendJoin() ? 1 : 0);
                                IntegerParameter sendMotdParameter = new IntegerParameter(playerData.isSendMotd() ? 1 : 0);
                                IntegerParameter sendLeaveParameter = new IntegerParameter(playerData.isSendLeave() ? 1 : 0);
                                IntegerParameter sendServerChangeParameter = new IntegerParameter(playerData.isSendServerChange() ? 1 : 0);
                                MessageParameter joinMessageParameter = new MessageParameter(playerData.getJoinMessage());
                                MessageParameter leaveMessageParameter = new MessageParameter(playerData.getLeaveMessage());
                                MessageParameter serverChangeMessageParameter = new MessageParameter(playerData.getServerChangeMessage());
                                LongParameter lastUpdatedParameter = new LongParameter(System.currentTimeMillis());

                                listOfParameterLists.add(List.of(
                                        playerIdParameter,
                                        sendJoinParameter,
                                        sendMotdParameter,
                                        sendLeaveParameter,
                                        sendServerChangeParameter,
                                        joinMessageParameter,
                                        leaveMessageParameter,
                                        serverChangeMessageParameter,
                                        lastUpdatedParameter,
                                        sendJoinParameter,
                                        sendMotdParameter,
                                        sendLeaveParameter,
                                        sendServerChangeParameter,
                                        joinMessageParameter,
                                        leaveMessageParameter,
                                        serverChangeMessageParameter,
                                        lastUpdatedParameter,
                                        lastUpdatedParameter));
                            });

                            CompletableFuture<Void> future = queueManager.queueBulkWriteTransaction(insertOrUpdateSql, listOfParameterLists).thenRun(() -> {});
                            return future.thenCompose(_ -> {
                                String dropTableSql = "DROP TABLE " + tableName;
                                // Then rename the temporary table to the old table's name.
                                return queueManager.queueWriteTransaction(dropTableSql).thenCompose(_ -> {
                                    String alterSql = "ALTER TABLE " + temporaryTableName + " RENAME TO " + tableName;

                                    // And lastly update the version
                                    return queueManager.queueWriteTransaction(alterSql).thenCompose(_ ->
                                            versionsTable.updateVersion(tableName, 1).thenCompose(_ ->
                                                    queueManager.queueWriteTransaction(playerIdsIndexSql)
                                                            .thenRun(() -> {})));
                                });
                            });
                        });
                    });
                }

                return CompletableFuture.completedFuture(null);
            });
        });
    }

    /**
     * Loads the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}. May be null.
     */
    public @NonNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NonNull UUID uuid) {
        String selectSql = "SELECT player_id, send_join, send_motd, send_leave, send_server_change, join_message, leave_message, server_change_message FROM " + tableName + " WHERE player_id = ? AND last_updated < ?";
        UUIDParameter uuidParameter = new UUIDParameter(uuid);
        LongParameter lastUpdatedParameter = new LongParameter(System.currentTimeMillis());

        return queueManager.queueReadTransaction(selectSql, List.of(uuidParameter, lastUpdatedParameter), resultSet -> {
            try {
                if(!resultSet.next()) return null;

                UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                boolean sendJoin = resultSet.getBoolean("send_join");
                boolean sendMotd = resultSet.getBoolean("send_motd");
                boolean sendLeave = resultSet.getBoolean("send_leave");
                boolean sendServerChange = resultSet.getBoolean("send_server_change");
                String joinMessage = resultSet.getString("join_message");
                String leaveMessage = resultSet.getString("leave_message");
                String serverChangeMessage = resultSet.getString("server_change_message");

                // Get default server change message if the player's isn't set
                if(serverChangeMessage == null) {
                    serverChangeMessage = pluginSettings.getDefaultServerChangeMessage();

                    if(serverChangeMessage == null) {
                        logger.error(AdventureUtility.plain("Unable to load player data because there is no default server change message configured."));
                        return null;
                    }
                }

                return new PlayerData(playerId, sendJoin, sendMotd, sendLeave, sendServerChange, joinMessage, leaveMessage, serverChangeMessage);
            } catch (SQLException e) {
                logger.error(AdventureUtility.deserialize("An error occurred while loading player data for player id " + uuid + ". Error: " + e.getMessage()));
                return null;
            }
        });
    }

    /**
     * Saves the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData} to save.
     */
    public void savePlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData) {
        String insertOrUpdateSql = "INSERT INTO " + tableName + " (player_id, send_join, send_motd, send_leave, join_message, leave_message, last_updated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (player_id) DO UPDATE SET " +
                "send_join = ?, send_motd = ?, send_leave = ?, join_message = ?, leave_message = ?, last_updated = ? WHERE last_updated < ?";

        UUIDParameter playerIdParameter = new UUIDParameter(uuid);
        IntegerParameter sendJoinParameter = new IntegerParameter(playerData.isSendJoin() ? 1 : 0);
        IntegerParameter sendMotdParameter = new IntegerParameter(playerData.isSendMotd() ? 1 : 0);
        IntegerParameter sendLeaveParameter = new IntegerParameter(playerData.isSendLeave() ? 1 : 0);
        MessageParameter joinMessageParameter = new MessageParameter(playerData.getJoinMessage());
        MessageParameter leaveMessageParameter = new MessageParameter(playerData.getLeaveMessage());
        LongParameter lastUpdatedParameter = new LongParameter(System.currentTimeMillis());

        queueManager.queueWriteTransaction(insertOrUpdateSql,
                List.of(
                        playerIdParameter,
                        sendJoinParameter,
                        sendMotdParameter,
                        sendLeaveParameter,
                        joinMessageParameter,
                        leaveMessageParameter,
                        lastUpdatedParameter,
                        sendJoinParameter,
                        sendMotdParameter,
                        sendLeaveParameter,
                        joinMessageParameter,
                        leaveMessageParameter,
                        lastUpdatedParameter,
                        lastUpdatedParameter));
    }

    /**
     * Load existing player data from the database for migration purposes.
     * @return A {@link CompletableFuture} of type {@link List} of {@link PlayerData}. The list may be null if an error occurred.
     */
    private @NonNull CompletableFuture<@Nullable List<PlayerData>> loadLegacyPlayerData() {
        String defaultServerChangeMessage = pluginSettings.getDefaultServerChangeMessage();
        if(defaultServerChangeMessage == null) {
            logger.warn(AdventureUtility.plain("Unable to load legacy player data due to a null default server change message."));
            return CompletableFuture.completedFuture(null);
        }

        String sql = "SELECT player_id, send_join, send_motd, send_leave, join_message, leave_message FROM " + tableName;
        return queueManager.queueReadTransaction(sql, resultSet -> {
            try {
                List<PlayerData> playerDataList = new ArrayList<>();
                while(resultSet.next()) {
                    UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                    boolean sendJoin = resultSet.getBoolean("send_join");
                    boolean sendMotd = resultSet.getBoolean("send_motd");
                    boolean sendLeave = resultSet.getBoolean("send_leave");
                    boolean sendServerChange = true;
                    String joinMessage = resultSet.getString("join_message");
                    String leaveMessage = resultSet.getString("leave_message");

                    playerDataList.add(new PlayerData(playerId, sendJoin, sendMotd, sendLeave, sendServerChange, joinMessage, leaveMessage, defaultServerChangeMessage));
                }

                return playerDataList;
            } catch (SQLException e) {
                logger.error(AdventureUtility.deserialize("An error occurred while loading legacy player data for migration. Error: " + e.getMessage()));
                return null;
            }
        });
    }
}