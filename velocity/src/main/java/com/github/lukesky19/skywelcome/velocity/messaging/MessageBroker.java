package com.github.lukesky19.skywelcome.velocity.messaging;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.libs.rabbitmq.client.*;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.settings.MessageBrokerConfig;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import com.github.lukesky19.skywelcome.velocity.SkyWelcomeVelocity;
import com.github.lukesky19.skywelcome.velocity.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.velocity.settings.Settings;
import com.github.lukesky19.skywelcome.velocity.settings.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * This class manages sending and receiving messages to and from the proxy.
 */
public class MessageBroker {
    private final @NonNull SkyWelcomeVelocity skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;

    private @Nullable ConnectionFactory factory;
    private @Nullable Connection connection;
    private @Nullable Channel channel;

    private final @NonNull String EXCHANGE_NAME = "skywelcome";

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomeVelocity} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public MessageBroker(
            @NonNull SkyWelcomeVelocity skyWelcome,
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

            logger.info(AdventureUtility.plain("Factory initialized"));

            // Create connection
            connection = factory.newConnection();
            if(connection == null) {
                logger.error(AdventureUtility.plain("Failed to create connection from factory."));
                return;
            }

            logger.info(AdventureUtility.plain("Connection initialized"));

            // Create channel
            channel = connection.createChannel();
            if(channel == null) {
                logger.error(AdventureUtility.plain("Failed to create channel from connection."));
                return;
            }

            // Declare Exchange
            channel.exchangeDeclare("skywelcome", BuiltinExchangeType.FANOUT, true);

            // Declare a queue
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "skywelcome", "");

            // Create a callback to process incoming data
            DeliverCallback deliverCallback = (_, delivery) -> processPacket(delivery.getBody(), delivery.getProperties().getReplyTo());

            // Register the consumer
            channel.basicConsume(queueName, true, deliverCallback, _ -> {});
        } catch (IOException | TimeoutException e) {
            logger.error(AdventureUtility.plain("Message broker error: " + e));

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
     * Deserialize the binary array into usable data.
     * @param data The binary array.
     * @param replyTo The queue name to reply to, if any.
     */
    public void processPacket(byte[] data, @Nullable String replyTo) {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);

        // The code defines what the raw data is
        int code = in.readShort();
        switch(code) {
            // Code 1 means to save the player data sent from the backend server
            case 1 -> {
                UUID playerId = UUID.fromString(in.readUTF());

                byte[] playerBytes = new byte[in.readInt()];
                in.readFully(playerBytes);

                PlayerData playerData = playerDataManager.deserialize(playerBytes);

                playerDataManager.setPlayerData(playerId, playerData, true);
            }

            // Code 3 is a response to the request to pre-process the player data on join.
            // So after getting the player data, the join message is sent if valid.
            case 3 -> {
                UUID playerId = UUID.fromString(in.readUTF());

                byte[] playerBytes = new byte[in.readInt()];
                in.readFully(playerBytes);

                PlayerData playerData = playerDataManager.deserialize(playerBytes);

                playerDataManager.setPlayerData(playerId, playerData, true);

                if(playerData.isVanished() || !playerData.isSendJoin()) return;

                if(playerData.getParsedJoinMessage() == null) {
                    logger.warn(AdventureUtility.plain("Unable to send a player's join message because their join message has not been pre-processed."));
                    return;
                }

                String message = playerData.getParsedJoinMessage();
                Component componentMessage = AdventureUtility.deserialize(message);
                String rawMessage = PlainTextComponentSerializer.plainText().serialize(componentMessage);

                skyWelcome.getServer().sendMessage(componentMessage);

                sendJoinNotice(playerId, message, rawMessage);
            }

            // Code 5 is a response to the request to pre-process the player data on server change.
            // So after getting the player data, the server change message is sent if valid.
            case 5 -> {
                UUID playerId = UUID.fromString(in.readUTF());

                byte[] playerBytes = new byte[in.readInt()];
                in.readFully(playerBytes);
                PlayerData playerData = playerDataManager.deserialize(playerBytes);

                playerDataManager.setPlayerData(playerId, playerData, true);
            }

            // Code 7 is a notice that a backend server was reloaded and to send all player data and plugin's settings to the backend server(s).
            case 7 -> {
                // Reload the plugin on the proxy
                skyWelcome.reload();

                // Send join/leave/server-change messages to backend server(s)
                Settings settings = settingsManager.getConfiguration();
                if(settings != null) {
                    sendSettings(replyTo, settings);
                }

                // Send player data to backend server(s)
                skyWelcome.getServer().getAllPlayers().forEach(player -> {
                    UUID playerId = player.getUniqueId();
                    PlayerData playerData = playerDataManager.getPlayerData(playerId);
                    if(playerData != null) {
                        sendPlayerData(playerId, playerData, replyTo);
                    }
                });
            }
        }
    }

    /**
     * Send a message to store the player data on the backend server.
     * The backend server will pre-parse any placeholders in messages and update the vanish status as necessary.
     * It will then send the changes back to the proxy.
     * @param playerId The {@link UUID} of the player the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData}.
     * @param replyTo The queue to reply to, if any.
     */
    public void sendPlayerData(@NonNull UUID playerId, @NonNull PlayerData playerData, @Nullable String replyTo) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send player data to a backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(0);

        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);
        out.writeInt(playerBytes.length);
        out.write(playerBytes);

        try {
            if(replyTo != null) {
                channel.basicPublish("", replyTo, null, out.toByteArray());
            } else {
                channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
            }
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send player data to the backend server: " + e.getMessage()));
        }
    }

    /**
     * Send a message to update the player data and send it back to the proxy.
     * The backend server will pre-parse any placeholders in messages and update the vanish status.
     * Once returned, the player's join message will be sent.
     * @param playerId The {@link UUID} of the player the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData}.
     */
    public void onPlayerJoin(@NonNull UUID playerId, @NonNull PlayerData playerData) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to request a player data update from a backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(2);
        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);
        out.writeInt(playerBytes.length);
        out.write(playerBytes);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send player data for update to the backend server: " + e.getMessage()));
        }
    }

    /**
     * Send a message to update the player data and send it back to the proxy.
     * The backend server will pre-parse any placeholders in messages and update the vanish status.
     * @param playerId The {@link UUID} of the player the {@link PlayerData} belongs to.
     * @param playerData The {@link PlayerData}.
     */
    public void onServerChange(
            @NonNull UUID playerId,
            @NonNull PlayerData playerData) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to request a player data update from a backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(4);
        out.writeUTF(playerId.toString());

        byte[] playerBytes = playerDataManager.serialize(playerData);
        out.writeInt(playerBytes.length);
        out.write(playerBytes);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send player data for update to the backend server: " + e.getMessage()));
        }
    }

    /**
     * Send a join notice to the backend server.
     * @param playerId The {@link UUID} of the player.
     * @param message The message with all placeholders resolved.
     * @param rawMessage The raw message with all placeholders resolved and no formatting tags.
     */
    public void sendJoinNotice(@NonNull UUID playerId, @NonNull String message, @NonNull String rawMessage) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send a join notice to backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(6);

        out.writeUTF(playerId.toString());
        out.writeUTF(message);
        out.writeUTF(rawMessage);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send a join notice to backend server. Error: " + e.getMessage()));
        }
    }

    /**
     * Send a leave notice to the backend server.
     * @param playerId The {@link UUID} of the player.
     * @param message The message with all placeholders resolved.
     * @param rawMessage The raw message with all placeholders resolved and no formatting tags.
     */
    public void sendLeaveNotice(@NonNull UUID playerId, @NonNull String message, @NonNull String rawMessage) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send a leave notice to backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(8);

        out.writeUTF(playerId.toString());
        out.writeUTF(message);
        out.writeUTF(rawMessage);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send a leave notice to backend server. Error: " + e.getMessage()));
        }
    }

    /**
     * Send a server change notice to the backend server.
     * @param playerId The {@link UUID} of the player.
     * @param message The message with all placeholders resolved.
     * @param rawMessage The raw message with all placeholders resolved and no formatting tags.
     * @param previousServer The previous server name.
     * @param currentServer The current server name.
     */
    public void sendServerChangeNotice(
            @NonNull UUID playerId,
            @NonNull String message,
            @NonNull String rawMessage,
            @NonNull String previousServer,
            @NonNull String currentServer) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to send a server change notice to backend server as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(10);

        out.writeUTF(playerId.toString());

        out.writeUTF(previousServer);
        out.writeUTF(currentServer);

        out.writeUTF(message);
        out.writeUTF(rawMessage);

        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, out.toByteArray());
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send a server change notice to backend server. Error: " + e.getMessage()));
        }
    }

    /**
     * Send the plugin's join/leave/server-change messages to the backend server.
     * @param replyTo The queue to send the settings to.
     * @param settings The plugin's {@link Settings}.
     */
    public void sendSettings(@Nullable String replyTo, @NonNull Settings settings) {
        if(channel == null || !channel.isOpen()) {
            logger.error(AdventureUtility.plain("Failed to sync plugin settings backend servers as the message broker has become invalid."));
            return;
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(12);

        serializeMessages(output, settings.joinMessages());
        serializeMessages(output, settings.quitMessages());
        serializeMessages(output, settings.serverChangeMessages());

        try {
            if(replyTo != null) {
                channel.basicPublish("", replyTo, null, output.toByteArray());
            } else {
                channel.basicPublish(EXCHANGE_NAME, "", null, output.toByteArray());
            }
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Failed to send a sync plugin settings to the backend servers. Error: " + e.getMessage()));
        }
    }

    /**
     * Serialize the {@link List} of {@link MessageConfig}s to the {@link ByteArrayDataOutput}.
     * @param output The {@link ByteArrayDataOutput}.
     * @param messageConfigList The {@link List} of {@link MessageConfig}s.
     */
    private void serializeMessages(@NonNull ByteArrayDataOutput output, @NonNull List<MessageConfig> messageConfigList) {
        List<MessageConfig> cleanMessageList = messageConfigList.stream()
                .filter(messageConfig -> messageConfig.permission() != null && messageConfig.message() != null)
                .toList();

        output.writeInt(cleanMessageList.size());

        cleanMessageList.forEach(messageConfig -> {
            assert messageConfig.permission() != null;
            assert messageConfig.message() != null;
            output.writeUTF(messageConfig.permission());
            output.writeUTF(messageConfig.message());
        });
    }
}