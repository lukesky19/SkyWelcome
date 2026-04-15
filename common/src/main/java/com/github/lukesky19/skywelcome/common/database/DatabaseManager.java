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
package com.github.lukesky19.skywelcome.common.database;

import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skywelcome.common.database.tables.PlayerDataTable;
import com.github.lukesky19.skywelcome.common.database.tables.VersionsTable;
import com.github.lukesky19.skywelcome.common.settings.ISettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This class manages access to the database table classes.
 */
public class DatabaseManager {
    private final @NonNull ISkyPlugin skyPlugin;
    private final @NonNull ComponentLogger logger;
    private final @NonNull ISettingsManager pluginSettings;

    private @Nullable ConnectionManager connectionManager;
    private @Nullable QueueManager queueManager;
    private @Nullable PlayerDataTable playerDataTable;

    /**
     * Constructor
     * @param skyPlugin A {@link ISkyPlugin} instance.
     * @param pluginSettings A {@link ISettingsManager} instance.
     */
    public DatabaseManager(
            @NonNull ISkyPlugin skyPlugin,
            @NonNull ISettingsManager pluginSettings) {
        this.skyPlugin = skyPlugin;
        this.logger = skyPlugin.getComponentLogger();
        this.pluginSettings = pluginSettings;
    }

    /**
     * Initialize the {@link ConnectionManager}, {@link QueueManager}, {@link VersionsTable}, and {@link PlayerDataTable}.
     */
    public void init() {
        if(connectionManager != null || queueManager != null || playerDataTable != null) shutdownDatabase();

        if(pluginSettings.isProxyEnabled()) return;

        this.connectionManager = new ConnectionManager(skyPlugin);
        this.queueManager = new QueueManager(connectionManager);

        VersionsTable versionsTable = new VersionsTable(queueManager);
        playerDataTable = new PlayerDataTable(logger, pluginSettings, queueManager, versionsTable);

        versionsTable.createTable().thenCompose(_ -> playerDataTable.createTable()).join();
    }

    /**
     * Get the {@link PlayerDataTable}.
     * @return The {@link PlayerDataTable} or null.
     */
    public @Nullable PlayerDataTable getPlayerDataTable() {
        return playerDataTable;
    }

    /**
     * Shutdown the database queue and connections.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> shutdownDatabase() {
        playerDataTable = null;

        if(queueManager != null && connectionManager != null) {
            return this.queueManager.shutdownQueue().thenAccept(_ -> this.connectionManager.closeConnections());
        } else if(queueManager != null) {
            return this.queueManager.shutdownQueue();
        } else if(connectionManager != null) {
            this.connectionManager.closeConnections();
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.completedFuture(null);
    }
}