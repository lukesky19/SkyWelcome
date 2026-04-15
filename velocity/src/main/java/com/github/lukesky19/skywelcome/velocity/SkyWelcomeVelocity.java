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
package com.github.lukesky19.skywelcome.velocity;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skywelcome.common.database.DatabaseManager;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.velocity.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.velocity.locale.LocaleManager;
import com.github.lukesky19.skywelcome.velocity.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.velocity.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.velocity.settings.Settings;
import com.github.lukesky19.skywelcome.velocity.settings.SettingsManager;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class is the entry point for the velocity portion of the plugin.
 */
@Plugin(
    id = "skywelcome",
    name = "SkyWelcome",
    version = "2.0.0.0",
    authors = {"lukeskywlker19"},
    dependencies = {
        @Dependency(id = "skylib")
    }
)
public class SkyWelcomeVelocity implements ISkyPlugin {
    private final @NonNull ProxyServer server;
    private final @NonNull ComponentLogger logger;
    private final @NonNull Path dataDirectory;

    private SettingsManager settingsManager;
    private LocaleManager localeManager;

    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;
    private MessageBroker messageBroker;

    /**
     * Constructor
     * @param server A {@link ProxyServer}.
     * @param logger A {@link ComponentLogger}.
     * @param dataDirectory The plugin's {@link Path} to the data directory.
     */
    @Inject
    public SkyWelcomeVelocity(
            @NonNull ProxyServer server,
            @NonNull ComponentLogger logger,
            @DataDirectory @NonNull Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    /**
     * Listens for proxy initialization to initialize plugin data.
     * @param proxyInitializeEvent A {@link ProxyInitializeEvent}.
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent proxyInitializeEvent) {
        // Log a message that the plugin is being initialized
        logger.info(AdventureUtility.plain("Initializing SkyWelcome"));

        settingsManager = new SettingsManager(this);
        // Load settings so the database can access the default server change message during migration
        settingsManager.loadConfiguration();

        localeManager = new LocaleManager(this, settingsManager);

        databaseManager = new DatabaseManager(this, settingsManager);
        databaseManager.init();

        playerDataManager = new PlayerDataManager(this, databaseManager, settingsManager);

        messageBroker = new MessageBroker(this, settingsManager, playerDataManager);

        // Register the command
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("skywelcome-velocity")
                .aliases("vwelcome")
                .plugin(this)
                .build();
        BrigadierCommand skyWelcomeCommand = new SkyWelcomeCommand(this, localeManager).createCommand();
        commandManager.register(commandMeta, skyWelcomeCommand);

        reload();
    }

    /**
     * Cleans up any plugin data on proxy shutdown.
     * @param proxyShutdownEvent A {@link ProxyShutdownEvent}.
     */
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent proxyShutdownEvent) {
        if(databaseManager != null) databaseManager.shutdownDatabase();

        if(playerDataManager != null) playerDataManager.clearPlayerData();

        if(messageBroker != null) messageBroker.cleanup();
    }

    /**
     * Listens for when a player finishes connecting to a server and either sends their join message or server change message.
     * @param serverPostConnectEvent A {@link ServerPostConnectEvent}.
     */
    @Subscribe
    public void onServerConnect(ServerPostConnectEvent serverPostConnectEvent) {
        Player player = serverPostConnectEvent.getPlayer();
        UUID playerId = player.getUniqueId();

        CompletableFuture<PlayerData> future = playerDataManager.getOrLoadPlayerData(player.getUniqueId());
        future.thenAccept(playerData -> {
            if(playerData == null) return;

            ServerConnection playerConnection = player.getCurrentServer().orElse(null);
            if(playerConnection == null) return;

            RegisteredServer previousServer = serverPostConnectEvent.getPreviousServer();
            RegisteredServer currentServer = playerConnection.getServer();

            if(previousServer != null && currentServer != null) {
               if(playerData.getParsedServerChangeMessage() != null) {
                   String previousServerName = previousServer.getServerInfo().getName();
                   String currentServerName = currentServer.getServerInfo().getName();

                   Component componentMessage = AdventureUtility.deserialize(
                           playerData.getParsedServerChangeMessage(),
                           List.of(Placeholder.parsed("previous_server", previousServerName),
                                   Placeholder.parsed("current_server", currentServerName)));
                   String rawMessage = AdventureUtility.serialize(componentMessage);
                   String plainTextMessage = PlainTextComponentSerializer.plainText().serialize(componentMessage);

                   server.sendMessage(componentMessage);

                   messageBroker.onServerChange(playerId, playerData);

                   messageBroker.sendServerChangeNotice(playerId, rawMessage, plainTextMessage, previousServerName, currentServerName);
               } else {
                   logger.warn(AdventureUtility.plain("Unable to send a player's server change message due to the server change message not being pre-parsed."));
               }
            } else if(currentServer != null) {
                // Send request to update player data
                messageBroker.onPlayerJoin(playerId, playerData);
            } else {
                logger.error(AdventureUtility.plain("Player " + player.getUsername() + " connected but has no previous or current server."));
            }
        });
    }

    /**
     * Listens for when a player disconnects from the proxy and sends their leave message.
     * It then unloads their player data.
     * @param disconnectEvent A {@link DisconnectEvent}.
     */
    @Subscribe
    public void onPlayerLeave(DisconnectEvent disconnectEvent) {
        Player player = disconnectEvent.getPlayer();
        UUID playerId = player.getUniqueId();

        PlayerData playerData = playerDataManager.getPlayerData(playerId);
        if(playerData == null) {
            logger.info(AdventureUtility.plain("No player data found on player disconnect."));
            return;
        }

        if(playerData.getParsedLeaveMessage() != null) {
            if(playerData.isVanished() || !playerData.isSendLeave()) return;

            String message = playerData.getParsedLeaveMessage();
            String plainMessage = PlainTextComponentSerializer.plainText().serialize(AdventureUtility.deserialize(message));

            server.sendMessage(AdventureUtility.deserialize(playerData.getParsedLeaveMessage()));

            messageBroker.sendLeaveNotice(playerId, message, plainMessage);
        } else {
            logger.warn(AdventureUtility.plain("Unable to send a player's leave message due to the leave message not being pre-parsed."));
        }

        playerDataManager.unloadPlayerData(playerId);
    }

    /**
     * Reload the plugin.
     */
    @Override
    public void reload() {
        settingsManager.loadConfiguration();
        localeManager.loadConfiguration();
        messageBroker.cleanup();

        Settings settings = settingsManager.getConfiguration();
        if(settings != null) {
            messageBroker.init();

            messageBroker.sendSettings("global", settings);
        }
    }

    /**
     * Get the {@link ProxyServer}.
     * @return The {@link ProxyServer}.
     */
    public @NonNull ProxyServer getServer() {
        return server;
    }

    /**
     * Get the plugin's {@link ComponentLogger}.
     * @return A {@link ComponentLogger}
     */
    @Override
    public @NonNull ComponentLogger getComponentLogger() {
        return logger;
    }

    /**
     * Get the plugin's data directory as a {@link File}.
     * @return The plugin's data directory as a {@link File}.
     */
    @Override
    public @NonNull File getDirectoryFile() {
        return dataDirectory.toFile();
    }

    /**
     * Get the plugin's data directory as a {@link Path}.
     * @return The plugin's data directory as a {@link Path}.
     */
    @Override
    public @NonNull Path getDirectoryPath() {
        return dataDirectory;
    }
}