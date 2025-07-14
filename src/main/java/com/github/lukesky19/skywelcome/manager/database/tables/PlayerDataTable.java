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
package com.github.lukesky19.skywelcome.manager.database.tables;

import com.github.lukesky19.skylib.api.database.parameter.impl.IntegerParameter;
import com.github.lukesky19.skylib.api.database.parameter.impl.LongParameter;
import com.github.lukesky19.skylib.api.database.parameter.impl.UUIDParameter;
import com.github.lukesky19.skywelcome.data.player.PlayerData;
import com.github.lukesky19.skywelcome.manager.database.QueueManager;
import com.github.lukesky19.skywelcome.util.MessageParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages access to the player data table in the database.
 */
public class PlayerDataTable {
    private final @NotNull QueueManager queueManager;
    private final @NotNull String tableName = "skywelcome_player_data";

    /**
     * Default Constructor.
     * You should use {@link #PlayerDataTable(QueueManager)} instead.
     * @deprecated You should use {@link #PlayerDataTable(QueueManager)} instead.
     */
    @Deprecated
    public PlayerDataTable() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param queueManager A {@link QueueManager} instance.
     */
    public PlayerDataTable(@NotNull QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    /**
     * Creates the table in the database if it doesn't exist and any indexes that don't exist.
     */
    public void createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "player_id LONG NOT NULL UNIQUE DEFAULT 0, " +
                "send_join INTEGER NOT NULL DEFAULT 1, " +
                "send_motd INTEGER NOT NULL DEFAULT 1, " +
                "send_leave INTEGER NOT NULL DEFAULT 1, " +
                "join_message TEXT NOT NULL, " +
                "leave_message TEXT NOT NULL, " +
                "last_updated LONG NOT NULL DEFAULT 0)";
        String playerIdsIndexSql = "CREATE INDEX IF NOT EXISTS idx_" + tableName + "_player_ids ON " + tableName + "(player_id)";

        queueManager.queueBulkWriteTransaction(List.of(tableCreationSql, playerIdsIndexSql)).thenAccept(result -> {});
    }

    /**
     * Loads the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}. May be null.
     */
    public @NotNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NotNull UUID uuid) {
        String selectSql = "SELECT send_join, send_motd, send_leave, join_message, leave_message FROM " + tableName + " WHERE player_id = ? AND last_updated < ?";
        UUIDParameter uuidParameter = new UUIDParameter(uuid);
        LongParameter lastUpdatedParameter = new LongParameter(System.currentTimeMillis());

        return queueManager.queueReadTransaction(selectSql, List.of(uuidParameter, lastUpdatedParameter), resultSet -> {
            try {
                if(!resultSet.next()) return null;

                boolean sendJoin = resultSet.getBoolean("send_join");
                boolean sendMotd = resultSet.getBoolean("send_motd");
                boolean sendLeave = resultSet.getBoolean("send_leave");
                String joinMessage = resultSet.getString("join_message");
                String leaveMessage = resultSet.getString("leave_message");

                return new PlayerData(sendJoin, sendMotd, sendLeave, joinMessage, leaveMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Saves the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData} to save.
     */
    public void savePlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
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
}
