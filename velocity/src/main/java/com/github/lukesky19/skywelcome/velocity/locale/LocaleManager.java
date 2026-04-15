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
package com.github.lukesky19.skywelcome.velocity.locale;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.velocity.settings.Settings;
import com.github.lukesky19.skywelcome.velocity.settings.SettingsManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Locale DEFAULT_LOCALE = new Locale(
            1,
            "<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> ",
            "<aqua>Plugin configuration reloaded.</aqua>");

    /**
     * Constructor
     * @param skyWelcome An {@link ISkyPlugin} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(
            @NonNull ISkyPlugin skyWelcome,
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
            logger.error(AdventureUtility.deserialize("Failed to load plugin's locale due to plugin settings being null."));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtility.deserialize("Failed to load plugin's locale to use in settings.yml is null."));
            return;
        }

        saveDefaultConfiguration();
        
        this.configurationPath = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + (settings.locale() + ".yml"));
        
        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            Locale locale = loader.load().get(Locale.class);
            if(locale == null) {
                logger.warn(AdventureUtility.deserialize("Failed to load " + settings.locale() + ".yml."));
                return;
            }

            Locale migratedConfiguration = migrateConfiguration(locale);
            if(migratedConfiguration == null) {
                logger.warn(AdventureUtility.deserialize("Failed to migrate " + settings.locale() + ".yml."));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtility.deserialize("Locale " + settings.locale() + ".yml configuration validation failed."));
                return;
            }

            this.configuration = migratedConfiguration;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.deserialize("Failed to load " + settings.locale() + ".yml configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public void saveDefaultConfiguration() {
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            saveConfiguration(DEFAULT_LOCALE);
        }
    }

    /**
     * Migrate the locale configuration.
     * Currently, no migration is needed as version 1 is the only version.
     * @param locale The {@link Locale} to migrate.
     * @return The migrated locale configuration.
     */
    @Override
    public @Nullable Locale migrateConfiguration(@NonNull Locale locale) {
        return locale;
    }

    @Override
    public boolean validateConfiguration(@Nullable Locale locale) {
        if(locale == null) return false;
        if(locale.version() != 1) {
            logger.error(AdventureUtility.plain("Your locale has an unsupported version. Version: " + locale.version() + ". The default locale will be used."));
            return false;
        }

        if(locale.prefix() == null || locale.reload() == null) {
            logger.error(AdventureUtility.deserialize("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtility.deserialize("You can regenerate your locale file by deleting it or add the missing messages to resolve the issue."));
            return false;
        }

        return true;
    }
}