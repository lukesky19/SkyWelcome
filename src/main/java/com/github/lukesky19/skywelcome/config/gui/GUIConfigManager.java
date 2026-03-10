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
package com.github.lukesky19.skywelcome.config.gui;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the gui configuration.
 */
public class GUIConfigManager {
    private final @NonNull SkyWelcome skyWelcome;
    private final @NonNull ComponentLogger logger;
    private @Nullable GUIConfig joinConfig;
    private @Nullable GUIConfig quitConfig;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     */
    public GUIConfigManager(@NonNull SkyWelcome skyWelcome) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
    }

    /**
     * Get the {@link GUIConfig} for the join gui config.
     * @return The {@link GUIConfig} or null.
     */
    public @Nullable GUIConfig getJoinGUIConfig() {
        return joinConfig;
    }

    /**
     * Get the {@link GUIConfig} for the quit gui config.
     * @return The {@link GUIConfig} or null.
     */
    public @Nullable GUIConfig getQuitGUIConfig() {
        return quitConfig;
    }

    /**
     * Reload the plugin's gui configs.
     */
    public void reload() {
        loadJoinGUIConfig();
        loadQuitGUIConfig();
    }

    /**
     * Load the {@link GUIConfig} for the join GUI config.
     */
    private void loadJoinGUIConfig() {
        joinConfig = null;

        Path joinPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "join.yml");

        if(!joinPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "join.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(joinPath);
        try {
            ConfigurationNode root = loader.load();
            migrateVersion(root);
            loader.save(root);

            joinConfig = root.get(GUIConfig.class);
            if(joinConfig == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load the join gui config."));
                return;
            }

            joinConfig = migrateConfiguration(joinConfig, joinPath);
            if(joinConfig == null) {
                logger.warn(AdventureUtil.deserialize("Failed to migrate the join gui config."));
            }
        } catch (ConfigurateException e) {
            logger.warn(AdventureUtil.deserialize("Failed to load the join gui config. Error: " + e.getMessage()));
        }
    }

    /**
     * Load the {@link GUIConfig} for the quit GUI config.
     */
    private void loadQuitGUIConfig() {
        quitConfig = null;

        Path quitPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "quit.yml");

        if(!quitPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "quit.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(quitPath);
        try {
            ConfigurationNode root = loader.load();
            migrateVersion(root);
            loader.save(root);

            quitConfig = root.get(GUIConfig.class);
            if(quitConfig == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load the quit gui config."));
                return;
            }

            quitConfig = migrateConfiguration(quitConfig, quitPath);
            if(quitConfig == null) {
                logger.warn(AdventureUtil.deserialize("Failed to migrate the quit gui config."));
            }
        } catch (ConfigurateException e) {
            logger.warn(AdventureUtil.deserialize("Failed to load the quit gui config. Error: " + e.getMessage()));
        }
    }

    /**
     * Migrate the {@link GUIConfig} provided.
     * @param guiConfig The {@link GUIConfig} to migrate.
     * @param configurationPath The {@link Path} to save migrated configuration to.
     * @return The migrated {@link GUIConfig} or null.
     */
    private @Nullable GUIConfig migrateConfiguration(@NonNull GUIConfig guiConfig, @NonNull Path configurationPath) {
        switch(guiConfig.version()) {
            case 3 -> {
                // Latest version
                return guiConfig;
            }

            case 2 -> {
                GUIConfig config = new GUIConfig(
                        3,
                        guiConfig.gui());

                saveConfiguration(config, configurationPath);

                return config;
            }

            case 1 -> {
                logger.warn(AdventureUtil.deserialize("Version 1 of the gui configuration cannot be automatically migrated."));
                return null;
            }

            default -> {
                logger.warn(AdventureUtil.deserialize("Unable to migrate the gui configuration due to an unrecognized version: " + guiConfig.version() + "."));
                return null;
            }
        }
    }

    /**
     * Save the configuration to the path.
     * @param configuration The {@link GUIConfig}.
     * @param configurationPath The {@link Path} to save to.
     */
    public void saveConfiguration(@NonNull GUIConfig configuration, @NonNull Path configurationPath) {
        try {
            YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);

            ConfigurationNode node = loader.createNode();

            node.set(GUIConfig.class, configuration);

            loader.save(node);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.deserialize("Failed to save GUI config to " + configurationPath.getFileName().toString() + ". Error: " + e.getMessage()));
        }
    }

    /**
     * Migrate the string-based version to a numeric version number.
     * @param root The root {@link ConfigurationNode}.
     */
    private void migrateVersion(@NonNull ConfigurationNode root) {
        ConfigurationNode versionNode = root.node("version");
        int version = versionNode.getInt();
        if(version > 0) return;

        ConfigurationNode legacyVersionNode = root.node("config-version");
        String legacyVersion = legacyVersionNode.virtual() ? null : legacyVersionNode.getString();
        try {
            switch(legacyVersion) {
                case "1.5.0.0" -> versionNode.set(2);

                case "1.0.0" -> versionNode.set(1);

                case null -> versionNode.set(1);

                default -> logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version due to an unrecognized version."));
            }
        } catch (SerializationException e) {
            logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version. Error: " + e.getMessage()));
        }
    }
}