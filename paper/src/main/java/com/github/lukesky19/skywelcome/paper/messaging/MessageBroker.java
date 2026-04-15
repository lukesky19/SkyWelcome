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
package com.github.lukesky19.skywelcome.paper.messaging;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.libs.rabbitmq.client.*;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.settings.MessageBrokerConfig;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.events.SkyWelcomeJoinEvent;
import com.github.lukesky19.skywelcome.paper.events.SkyWelcomeQuitEvent;
import com.github.lukesky19.skywelcome.paper.events.SkyWelcomeServerChangeEvent;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.Settings;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * This class manages sending and receiving messages to and from the proxy.
 */
public class MessageBroker {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;

    private @Nullable ConnectionFactory factory;
    private @Nullable Connection connection;
    private @Nullable Channel channel;
    private @Nullable String queueName;

    private final @NonNull String EXCHANGE_NAME = "skywelcome";

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public MessageBroker(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Attempt to initialize the factory, connection, and channel.
     */
    public void init() {
        cleanup();

        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.plain("Unable to setup message broker due to invalid plugin settings."));
            return;
        }
        MessageBrokerConfig messageBrokerConfig = settings.messageBrokerConfig();

        try {
            // Initialize connection factory
            factory = new ConnectionFactory();
            factory.setHost(messageBrokerConfig.getAddress());
            if(messageBrokerConfig.port() != null) {
                factory.setPort(messageBrokerConfig.port());
            }
            if(messageBrokerConfig.username() != null && !messageBrokerConfig.username().isEmpty()) {
                factory.setUsername(messageBrokerConfig.username());
            }
            if(messageBrokerConfig.password() != null && !messageBrokerConfig.password().isEmpty()) {
                factory.setPassword(messageBrokerConfig.password());
            }

            factory.setAutomaticRecoveryEnabled(true);
            factory.setTopologyRecoveryEnabled(true);
            factory.setRequestedHeartbeat(60);
            factory.setNetworkRecoveryInterval(5000);

            // Create connection
            connection = factory.newConnection();
            if(connection == null) {
                logger.error(AdventureUtility.plain("Failed to create connection from factory."));
                return;
            }

            // Create channel
            channel = connection.createChannel();
            if(channel == null) {
                logger.error(AdventureUtility.plain("Failed to create channel from connection."));
                return;
            }

            // Declare Exchange
            channel.exchangeDeclare("skywelcome", BuiltinExchangeType.FANOUT, true);

            // Declare a queue
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "skywelcome", "");

            // Create a callback to process incoming data
            DeliverCallback deliverCallback = (_, delivery) -> processPacket(delivery.getBody());

           // Register the consumer
            channel.basicConsume(queueName, true, deliverCallback, _ -> {});
        } catch (IOException | TimeoutException e) {
            logger.error(AdventureUtility.plain("Message broker error: " + e.getMessage()));

            cleanup();
        }
    }

    /**
     * Cleanup the factory, connection, and channel.
     */
    public void cleanup() {
        if(channel != null) {
            if(channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException ex) {
                    logger.error(AdventureUtility.plain("Failed to properly close the channel. Error: " + ex.getMessage()));
                }
            }

            channel = null;
        }

        if(connection != null) {
            if(connection.isOpen()) {
                try {
                    connection.close();
                } catch (IOException ex) {
                    logger.error(AdventureUtility.plain("Failed to properly close the connection. Error: " + ex.getMessage()));
                }
            }

            connection = null;
        }

        if(factory != null) {
            factory = null;
        }
    }

    /**
     * Process incoming data.
     * @param data The binary data array.
     */
    private void processPacket(byte[] data) {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);

        try {
            // The code defines what the binary data is
            int code = in.readShort();
            switch (code) {
                // Code 0 means to process the player's messages to resolve PlaceholderAPI placeholders and update the player's vanish status.
                // Then store the data locally and send the updated player data back to the proxy.
                case 0 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);

                    // Ignore invalid player (data is only meant for the server the player is connected to)
                    if(targetPlayer == null || !targetPlayer.isOnline() || !targetPlayer.isConnected()) return;

                    byte[] originalPlayerBytes = new byte[in.readInt()];
                    in.readFully(originalPlayerBytes);
                    PlayerData playerData = playerDataManager.deserialize(originalPlayerBytes);

                    playerDataManager.updatePlayerData(targetPlayer, playerData);

                    playerDataManager.setPlayerData(playerId, playerData, false);

                    sendPlayerData(playerId, playerData);
                }

                // Code 2 means to pre-process the player's messages to resolve PlaceholderAPI placeholders and update the player's vanish status.
                // This is the initial pre-process on player join. The proxy will send a join message once it receives a response if applicable.
                // The server's MotD will be sent if applicable as well.
                case 2 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);

                    // Ignore invalid player
                    if(targetPlayer == null || !targetPlayer.isOnline() || !targetPlayer.isConnected()) return;

                    byte[] originalPlayerBytes = new byte[in.readInt()];
                    in.readFully(originalPlayerBytes);
                    PlayerData playerData = playerDataManager.deserialize(originalPlayerBytes);

                    playerDataManager.updatePlayerData(targetPlayer, playerData);

                    playerDataManager.setPlayerData(playerId, playerData, false);

                    sendUpdatedPlayerDataServerJoin(playerId, playerData);

                    Settings settings = settingsManager.getConfiguration();
                    if(settings == null) {
                        logger.warn(AdventureUtility.deserialize("Unable to send the MotD to player " + targetPlayer.getName() + " due to invalid plugin settings."));
                        return;
                    }

                    if(settings.globalMotdToggle() && playerData.isSendMotd()) {
                        settings.motd().forEach(message -> targetPlayer.sendMessage(PaperAdventureUtility.deserialize(targetPlayer, message)));
                    }
                }

                // Code 4 means to pre-process the player's messages to resolve PlaceholderAPI placeholders and update the player's vanish status.
                // This is the initial pre-process on player server change. The proxy will send a server change message once it receives a response if applicable.
                case 4 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);

                    // Ignore invalid player
                    if(targetPlayer == null || !targetPlayer.isOnline() || !targetPlayer.isConnected()) return;

                    byte[] originalPlayerBytes = new byte[in.readInt()];
                    in.readFully(originalPlayerBytes);
                    PlayerData playerData = playerDataManager.deserialize(originalPlayerBytes);

                    playerDataManager.updatePlayerData(targetPlayer, playerData);

                    playerDataManager.setPlayerData(playerId, playerData, false);

                    sendUpdatedPlayerDataServerChange(playerId, playerData);
                }

                // Code 6 means a join message on the proxy was sent and to call a SkyWelcomeJoinEvent
                case 6 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);

                    // Ignore invalid player
                    if(targetPlayer == null || !targetPlayer.isOnline() || !targetPlayer.isConnected()) return;

                    String message = in.readUTF();
                    String rawMessage = in.readUTF();
                    Component componentMessage = PaperAdventureUtility.deserialize(targetPlayer, message);

                    skyWelcome.getServer().getScheduler().runTask(skyWelcome, () ->
                            skyWelcome.getServer().getPluginManager().callEvent(new SkyWelcomeJoinEvent(targetPlayer, componentMessage, rawMessage)));
                }

                // Code 8 means a leave message on the proxy was sent and to call a SkyWelcomeQuitEvent
                case 8 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);
                    if(targetPlayer == null) return;

                    String message = in.readUTF();
                    String rawMessage = in.readUTF();
                    Component componentMessage = PaperAdventureUtility.deserialize(targetPlayer, message);

                    skyWelcome.getServer().getScheduler().runTask(skyWelcome, () ->
                            skyWelcome.getServer().getPluginManager().callEvent(new SkyWelcomeQuitEvent(targetPlayer, componentMessage, rawMessage)));
                }

                // Code 10 means a server change message on the proxy was sent and to call a SkyWelcomeServerChangeEvent
                case 10 -> {
                    UUID playerId = UUID.fromString(in.readUTF());
                    Player targetPlayer = skyWelcome.getServer().getPlayer(playerId);

                    // Ignore invalid player
                    if(targetPlayer == null || !targetPlayer.isOnline() || !targetPlayer.isConnected()) return;

                    String previousServer = in.readUTF();
                    String currentServer = in.readUTF();

                    String message = in.readUTF();
                    String rawMessage = in.readUTF();
                    Component componentMessage = PaperAdventureUtility.deserialize(targetPlayer, message);

                    skyWelcome.getServer().getScheduler().runTask(skyWelcome, () ->
                            skyWelcome.getServer().getPluginManager().callEvent(new SkyWelcomeServerChangeEvent(targetPlayer, componentMessage, rawMessage, previousServer, currentServer)));
                }

                // Code 12 has plugin settings to sync to the backend server (the join/leave/server-change messages)
                case 12 -> {
                    List<MessageConfig> joinMessages = new ArrayList<>();
                    List<MessageConfig> leaveMessages = new ArrayList<>();
                    List<MessageConfig> serverChangeMessages = new ArrayList<>();

                    int joinCount = in.readInt();
                    if(joinCount > 0) {
                        for(int i = 0; i < joinCount; i++) {
                            String permission = in.readUTF();
                            String message = in.readUTF();
                            joinMessages.add(new MessageConfig(permission, message));
                        }
                    }

                    int leaveCount = in.readInt();
                    if(leaveCount > 0) {
                        for(int i = 0; i < leaveCount; i++) {
                            String permission = in.readUTF();
                            String message = in.readUTF();
                            leaveMessages.add(new MessageConfig(permission, message));
                        }
                    }

                    int serverChangeCount = in.readInt();
                    if(serverChangeCount > 0) {
                        for(int i = 0; i < serverChangeCount; i++) {
                            String permission = in.readUTF();
                            String message = in.readUTF();
                            serverChangeMessages.add(new MessageConfig(permission, message));
                        }
                    }

                    settingsManager.setJoinMessages(joinMessages);
                    settingsManager.setLeaveMessages(leaveMessages);
                    settingsManager.setServerChangeMessages(serverChangeMessages);
                }
            }
        } catch (RuntimeException e) {
            logger.error(AdventureUtility.plain("An error occurred while processing incoming packet with code. Error: " + e));
        }
    }

    /**
     * Send a message to update the player data on the proxy.
     * @param playerId The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    public void sendPlayerData(@NonNull UUID playerId, @NonNull PlayerData playerData) {
        if(channel == null || !channel.isOpen()) return;
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(1);
        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);
        out.writeInt(playerBytes.length);
        out.write(playerBytes);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send player data. Error: " + e.getMessage()));
        }
    }

    /**
     * Send the updated player data to the proxy that was requested.
     * @apiNote This is a different code because the proxy will send the join message if applicable.
     * @param playerId The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    public void sendUpdatedPlayerDataServerJoin(@NonNull UUID playerId, @NonNull PlayerData playerData) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send a message with requested player data from the proxy due to the message broker being invalid. Was is closed?"));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(3);
        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);

        out.writeInt(playerBytes.length);

        out.write(playerBytes);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send response to a request to update player data on server join. Error: " + e.getMessage()));
        }
    }

    /**
     * Send the updated player data to the proxy that was requested.
     * @apiNote This is a different code because this is in response to a requested update on server change.
     * @param playerId The {@link UUID} of the player.
     * @param playerData The {@link PlayerData}.
     */
    public void sendUpdatedPlayerDataServerChange(
            @NonNull UUID playerId,
            @NonNull PlayerData playerData) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send a message with requested player data from the proxy due to the message broker being invalid. Was is closed?"));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(5);
        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);

        out.writeInt(playerBytes.length);
        out.write(playerBytes);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send response to a request to update player data on server change. Error: " + e.getMessage()));
        }
    }

    /**
     * Notify the proxy of a reload. The proxy will return the player data for all players and the plugin settings.
     */
    public void sendReloadNotice() {
        if(channel == null || !channel.isOpen() || queueName == null) {
            logger.error(AdventureUtility.plain("Failed to send a message to notify the proxy the backend server was reloaded due to the message broker being invalid. Was is closed?"));
            return;
        }
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.plain("Unable to send a message to notify the proxy the backend server was reloaded due to invalid plugin settings."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(7);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", new AMQP.BasicProperties.Builder().replyTo(queueName).build(), out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send reload notice to the proxy. Error: " + e.getMessage()));
        }
    }
}