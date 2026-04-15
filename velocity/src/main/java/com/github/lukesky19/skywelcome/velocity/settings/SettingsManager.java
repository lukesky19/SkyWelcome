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
package com.github.lukesky19.skywelcome.velocity.settings;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.common.settings.ISettingsManager;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> implements ISettingsManager {
    /**
     * Constructor
     * @param skyWelcome A {@link ISkyPlugin} instance.
     */
    public SettingsManager(@NonNull ISkyPlugin skyWelcome) {
        super(skyWelcome, Path.of(skyWelcome.getDirectoryPath() + File.separator + "settings.yml"), Settings.class);
    }

    /**
     * Proxy is always considered enabled on velocity.
     * Return false so the database is always initialized.
     * @return Always false.
     */
    @Override
    public boolean isProxyEnabled() {
        return false;
    }

    @Override
    public @Nullable List<MessageConfig> getJoinMessages() {
        if(configuration == null) return null;
        return configuration.joinMessages();
    }

    @Override
    public @Nullable List<MessageConfig> getLeaveMessages() {
        if(configuration == null) return null;
        return configuration.joinMessages();
    }

    @Override
    public @Nullable List<MessageConfig> getServerChangeMessages() {
        if(configuration == null) return null;
        return configuration.joinMessages();
    }

    /**
     * This method does nothing as it is not applicable to velocity.
     * @param joinMessages The {@link List} of {@link MessageConfig} for the join messages.
     */
    @Override
    public void setJoinMessages(@NonNull List<MessageConfig> joinMessages) {}

    /**
     * This method does nothing as it is not applicable to velocity.
     * @param leaveMessages The {@link List} of {@link MessageConfig} for the leave messages.
     */
    @Override
    public void setLeaveMessages(@NonNull List<MessageConfig> leaveMessages) {}

    /**
     * This method does nothing as it is not applicable to velocity.
     * @param serverChangeMessages The {@link List} of {@link MessageConfig} for the server change messages.
     */
    @Override
    public void setServerChangeMessages(@NonNull List<MessageConfig> serverChangeMessages) {}

    /**
     * Get the default join message.
     * @return The default join message or null.
     */
    @Override
    public @Nullable String getDefaultJoinMessage() {
        if(configuration == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default join message due to invalid plugin settings."));
            return null;
        }

        if(configuration.joinMessages().isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default join message due no join messages being configured."));
            return null;
        }

        return configuration.joinMessages().getFirst().message();
    }

    /**
     * Get the default leave message.
     * @return The default leave message or null.
     */
    @Override
    public @Nullable String getDefaultLeaveMessage() {
        if(configuration == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default leave message due to invalid plugin settings."));
            return null;
        }

        if(configuration.quitMessages().isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default leave message due no leave messages being configured."));
            return null;
        }

        return configuration.quitMessages().getFirst().message();
    }

    /**
     * Get the default server change message.
     * @return The default server change message or null.
     */
    @Override
    public @Nullable String getDefaultServerChangeMessage() {
        if(configuration == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default server change message due to invalid plugin settings."));
            return null;
        }

        if(configuration.serverChangeMessages().isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default server change message due no leave messages being configured."));
            return null;
        }

        return configuration.serverChangeMessages().getFirst().message();
    }

    @Override
    public void loadConfiguration() {
        configuration = null;

        if(configurationPath == null) return;

        saveDefaultConfiguration();

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            Settings settings = loader.load().get(Settings.class);
            if(settings == null) {
                logger.warn(AdventureUtility.deserialize("Failed to load settings.yml."));
                return;
            }

            Settings migratedConfiguration = migrateConfiguration(settings);

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtility.deserialize("Settings configuration validation failed."));
                return;
            }

            this.configuration = migratedConfiguration;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.deserialize("Failed to load configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public void saveDefaultConfiguration() {
        if(configurationPath == null) return;
        if(configurationPath.toFile().exists()) return;

        Settings defaultSettings = new Settings(
                1,
                "en_US",
                new com.github.lukesky19.skywelcome.common.settings.MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                true,
                true,
                true,
                List.of(new MessageConfig(
                        "skywelcome.join.default",
                        "<gray>[<gray><green>+</green><gray>]</gray> <papi:essentials_nickname>")),
                List.of(new MessageConfig(
                        "skywelcome.leave.default",
                        "<gray>[<gray><red>-</red><gray>]</gray> <papi:essentials_nickname>")),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")));

        saveConfiguration(defaultSettings);
    }

    /**
     * Migrate the settings configuration.
     * Currently, no migration is needed as version 1 is the only version.
     * @param settings The {@link Settings} to migrate.
     * @return The migrated settings configuration.
     */
    @Override
    public @NonNull Settings migrateConfiguration(@NonNull Settings settings) {
        return settings;
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        return settings != null;
    }
}