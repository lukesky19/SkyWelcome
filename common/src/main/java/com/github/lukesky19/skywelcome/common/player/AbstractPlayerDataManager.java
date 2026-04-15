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
package com.github.lukesky19.skywelcome.common.player;

import com.github.lukesky19.skylib.libs.configurate.yaml.NodeStyle;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.player.data.legacy.PlayerSettings;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class can be extended to create a player data manager class.
 */
public abstract class AbstractPlayerDataManager {
    /**
     * Maps {@link UUID} to {@link PlayerData}.
     */
    protected final @NonNull Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Constructor
     */
    public AbstractPlayerDataManager() {}

    /**
     * Set the player data stored in the map and optionally save it to the database.
     * @param playerId The player's {@link UUID}.
     * @param playerData The {@link PlayerData}.
     * @param save Whether to save the data or not.
     */
    public void setPlayerData(@NonNull UUID playerId, @NonNull PlayerData playerData, boolean save) {
        playerDataMap.put(playerId, playerData);

        if(save) {
            savePlayerData(playerId, playerData);
        }
    }

    /**
     * Get the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} for the player.
     * @return The {@link PlayerData}. May be null.
     */
    public abstract @Nullable PlayerData getPlayerData(@NonNull UUID uuid);

    /**
     * Get the {@link PlayerData} from the database. If no data exists, then a new {@link PlayerData} record will attempt to be created.
     * If the plugin's settings are invalid, the returned {@link PlayerData} will be null.
     * @param uuid The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}, which may be null.
     */
    public abstract @NonNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NonNull UUID uuid);

    /**
     * Get the {@link PlayerData} stored in memory or load it from the database.
     * If no data exists, then a new {@link PlayerData} record will attempt to be created.
     * If the plugin's settings are invalid, the returned {@link PlayerData} will be null.
     * @param playerId The {@link UUID} of the player.
     * @return A {@link CompletableFuture} containing {@link PlayerData}, which may be null.
     */
    public abstract @NonNull CompletableFuture<@Nullable PlayerData> getOrLoadPlayerData(@NonNull UUID playerId);

    /**
     * Save the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    public abstract void savePlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData);

    /**
     * Clear the player data for the {@link UUID} provided.
     * @param playerId The player's {@link UUID}.
     */
    public void unloadPlayerData(@NonNull UUID playerId) {
        playerDataMap.remove(playerId);
    }

    /**
     * Clear any stored player data.
     */
    public void clearPlayerData() {
        playerDataMap.clear();
    }

    /**
     * Serialize the player data into a byte array.
     * @param playerData The {@link PlayerData}.
     * @return A byte array.
     */
    public byte[] serialize(@NonNull PlayerData playerData) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(playerData.getPlayerId().toString());
        out.writeBoolean(playerData.isSendJoin());
        out.writeBoolean(playerData.isSendMotd());
        out.writeBoolean(playerData.isSendLeave());
        out.writeBoolean(playerData.isSendServerChange());

        out.writeUTF(playerData.getJoinMessage());
        out.writeUTF(playerData.getLeaveMessage());
        out.writeUTF(playerData.getServerChangeMessage());

        if(playerData.getParsedJoinMessage() != null) {
            out.writeBoolean(true);
            out.writeUTF(playerData.getParsedJoinMessage());
        } else {
            out.writeBoolean(false);
        }

        if(playerData.getParsedLeaveMessage() != null) {
            out.writeBoolean(true);
            out.writeUTF(playerData.getParsedLeaveMessage());
        } else {
            out.writeBoolean(false);
        }

        if(playerData.getParsedServerChangeMessage() != null) {
            out.writeBoolean(true);
            out.writeUTF(playerData.getParsedServerChangeMessage());
        } else {
            out.writeBoolean(false);
        }

        out.writeBoolean(playerData.isVanished());

        return out.toByteArray();
    }

    /**
     * Deserialize the byte array into {@link PlayerData}.
     * @param data The byte array.
     * @return The {@link PlayerData}.
     */
    public @NonNull PlayerData deserialize(byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);

        UUID playerId = UUID.fromString(input.readUTF());
        boolean sendJoin = input.readBoolean();
        boolean sendMotd = input.readBoolean();
        boolean sendLeave = input.readBoolean();
        boolean sendServerChange = input.readBoolean();

        String joinMessage = input.readUTF();
        String leaveMessage = input.readUTF();
        String serverChangeMessage = input.readUTF();

        boolean hasParsedJoinMessage = input.readBoolean();
        String parsedJoinMessage = null;
        if(hasParsedJoinMessage) {
            parsedJoinMessage = input.readUTF();
        }

        boolean hasParsedLeaveMessage = input.readBoolean();
        String parsedLeaveMessage = null;
        if(hasParsedLeaveMessage) {
            parsedLeaveMessage = input.readUTF();
        }

        boolean hasParsedServerChangeMessage = input.readBoolean();
        String parsedServerChangeMessage = null;
        if(hasParsedServerChangeMessage) {
            parsedServerChangeMessage = input.readUTF();
        }

        boolean vanished = input.readBoolean();

        return new PlayerData(
                playerId,
                sendJoin, sendMotd, sendLeave, sendServerChange,
                joinMessage, leaveMessage, serverChangeMessage,
                parsedJoinMessage, parsedLeaveMessage, parsedServerChangeMessage,
                vanished);
    }

    /**
     * Load all legacy player settings and migrate it to legacy player data and save the updated player data to the database.
     */
    public abstract void migrateLegacyPlayerSettings();

    /**
     * Migrate the legacy player data.
     * @param playerId The {@link UUID} of the player.
     * @param playerSettings The legacy {@link PlayerSettings}.
     * @param defaultJoinMessage The default join message.
     * @param defaultLeaveMessage The default leave message.
     * @param defaultServerChangeMessage The default server change message.
     * @return The migrated {@link PlayerData}.
     */
    protected @NonNull PlayerData migrateLegacyPlayerSettings(
            @NonNull UUID playerId,
            @NonNull PlayerSettings playerSettings,
            @NonNull String defaultJoinMessage,
            @NonNull String defaultLeaveMessage,
            @NonNull String defaultServerChangeMessage) {
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

        return new PlayerData(playerId, sendJoin, sendMotd, sendLeave, true, joinMessage, leaveMessage, defaultServerChangeMessage);
    }

    /**
     * Create the {@link YamlConfigurationLoader} to load the legacy player settings.
     * @param path The {@link Path} to the file.
     * @return A {@link YamlConfigurationLoader}.
     */
    protected @NonNull YamlConfigurationLoader createLoader(@NonNull Path path) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(4)
                .build();
    }
}