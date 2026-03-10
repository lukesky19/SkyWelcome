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
package com.github.lukesky19.skywelcome.config.locale;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Locale DEFAULT_LOCALE = new Locale(
            6,
            "<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> ",
            List.of(
                    "<aqua>SkyWelcome is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>",
                    "<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>",
                    " ",
                    "<aqua><bold>List of Commands:</bold></aqua>",
                    "<white>/<aqua>skywelcome <yellow>help</yellow></white>",
                    "<white>/<aqua>skywelcome <yellow>reload</yellow></white>",
                    "<white>/<aqua>skywelcome <yellow>toggle</yellow> <yellow><join | leave | quit | motd></yellow></white>",
                    "<white>/<aqua>skywelcome <yellow>gui</yellow> <yellow><join | leave | quit></yellow></white>"),
            "<aqua>Plugin configuration reloaded.</aqua>",
            "<red>Unable to open this GUI because of a configuration error.</red>",
            "<aqua>You have enabled your join message.</aqua>",
            "<aqua>You have disabled your join message.</aqua>",
            "<aqua>You have enabled your leave message.</aqua>",
            "<aqua>You have disabled your leave message.</aqua>",
            "<aqua>You have enabled your MOTD message.</aqua>",
            "<aqua>You have disabled your MOTD message.</aqua>",
            "<aqua><white><welcome_player></white> welcomed <white><new_player></white> to the server!</aqua>");

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(
            @NonNull SkyWelcome skyWelcome,
            @NonNull SettingsManager settingsManager) {
        super(skyWelcome, Locale.class);
        this.settingsManager = settingsManager;
    }

    @Override
    public @NonNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        configuration = null;
        
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtil.deserialize("Failed to load plugin's locale due to plugin settings being null."));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtil.deserialize("Failed to load plugin's locale to use in settings.yml is null."));
            return;
        }

        saveBundledConfig();
        
        this.configurationPath = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + (settings.locale() + ".yml"));
        
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);
        try {
            ConfigurationNode root = loader.load();
            migrateVersion(root);
            loader.save(root);

            Locale locale = root.get(Locale.class);
            if(locale == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load " + settings.locale() + ".yml."));
                return;
            }

            Locale migratedConfiguration = migrateConfiguration(locale);
            if(migratedConfiguration == null) {
                logger.warn(AdventureUtil.deserialize("Failed to migrate " + settings.locale() + ".yml."));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtil.deserialize("Locale " + settings.locale() + ".yml configuration validation failed."));
                return;
            }

            this.configuration = migratedConfiguration;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtil.deserialize("Failed to load " + settings.locale() + ".yml configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public void saveBundledConfig() {
        Path path = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    @Override
    public @Nullable Locale migrateConfiguration(@NonNull Locale locale) {
        switch(locale.version()) {
            case 5 -> {
                // Latest version, do nothing
                return locale;
            }
            
            case 4 -> {
                Locale newLocale = new Locale(
                        5,
                        locale.prefix(),
                        locale.help(),
                        locale.reload(),
                        locale.guiOpenError(),
                        locale.joinEnabled(),
                        locale.joinDisabled(),
                        locale.quitEnabled(),
                        locale.quitDisabled(),
                        locale.motdEnabled(),
                        locale.motdDisabled(),
                        locale.welcomeBroadcast());

                saveConfiguration(newLocale);

                return newLocale;
            }
            
            case 3 -> {
                Locale newLocale = new Locale(
                        5,
                        locale.prefix(),
                        locale.help(),
                        locale.reload(),
                        "<red>Unable to open this GUI because of a configuration error.</red>",
                        locale.joinEnabled(),
                        locale.joinDisabled(),
                        locale.quitEnabled(),
                        locale.quitDisabled(),
                        locale.motdEnabled(),
                        locale.motdDisabled(),
                        locale.welcomeBroadcast());
                
                saveConfiguration(newLocale);
                
                return newLocale;
            }
            
            case 2, 1 -> {
                Locale newLocale = new Locale(
                        5,
                        locale.prefix(),
                        locale.help(),
                        locale.reload(),
                        "<red>Unable to open this GUI because of a configuration error.</red>",
                        locale.joinEnabled(),
                        locale.joinDisabled(),
                        locale.quitEnabled(),
                        locale.quitDisabled(),
                        locale.motdEnabled(),
                        locale.motdDisabled(),
                        "<aqua><white><welcome_player></white> welcomed <white><new_player></white> to the server!</aqua>");

                saveConfiguration(newLocale);

                return newLocale;
            }
            
            default -> {
                logger.warn(AdventureUtil.deserialize("Unable to migrate locale configuration for version " + locale.version()));
                return null;
            }
        }
    }

    @Override
    public boolean validateConfiguration(@Nullable Locale locale) {
        if(locale == null) return false;

        if(locale.prefix() == null
                || locale.reload() == null
                || locale.guiOpenError() == null
                || locale.joinEnabled() == null
                || locale.joinDisabled() == null
                || locale.quitEnabled() == null
                || locale.quitDisabled() == null
                || locale.motdEnabled() == null
                || locale.motdDisabled() == null
                || locale.welcomeBroadcast() == null) {
            logger.error(AdventureUtil.deserialize("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtil.deserialize("You can regenerate your locale file by deleting it or adding the missing messages to resolve the issue."));
            return false;
        }

        return true;
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
                case "1.5.0.0" -> versionNode.set(4);

                case "1.2.0" -> versionNode.set(3);

                case "1.1.0" -> versionNode.set(2);

                case "1.0.0" -> versionNode.set(1);

                case null, default -> logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version due to an unrecognized version."));
            }
        } catch (SerializationException e) {
            logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version. Error: " + e.getMessage()));
        }
    }
}