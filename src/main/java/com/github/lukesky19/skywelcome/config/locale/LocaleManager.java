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
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull Locale DEFAULT_LOCALE = new Locale(
            "1.5.0.0",
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
    private @Nullable Locale locale;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
    }

    /**
     * Get the plugin's {@link Locale} or the default {@link Locale} if the user-configured version failed to load.
     * @return The {@link Locale}.
     */
    public @NotNull Locale getLocale() {
        if(locale == null) return DEFAULT_LOCALE;

        return locale;
    }

    /**
     * Reloads the plugin's locale.
     */
    public void reload() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        locale = null;

        copyDefaultLocales();

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.error(AdventureUtil.serialize("Unable to load the plugin's locale due to invalid plugin settings. The default locale will be used."));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtil.serialize("Unable to load the plugin's locale due to a locale not being configured in settings.yml. The default locale will be used."));
            return;
        }

        Path path = Path.of(
                skyWelcome.getDataFolder()
                        + File.separator
                        + "locale"
                        + File.separator
                        + settings.locale()
                        + ".yml");
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.serialize("Failed to load the locale configuration. Error: " + e.getMessage()));
        }

        migrateLocale();
        validateLocale();
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     */
    private void copyDefaultLocales() {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyWelcome.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Validates the plugin's locale.
     */
    private void validateLocale() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(locale == null) {
            logger.warn(AdventureUtil.serialize("Unable to validate locale as the locale configuration failed to load. The default locale will be used."));
            return;
        }

        if(locale.configVersion() == null) {
            logger.warn(AdventureUtil.serialize("The locale's config version is invalid. The default locale will be used. This means your config did not migrate properly or you modified the config-version setting."));
            locale = null;
            return;
        }

        if(locale.prefix() == null) {
            logger.warn(AdventureUtil.serialize("The prefix in the locale is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        for(String msg : locale.help()) {
            if(msg == null) {
                logger.warn(AdventureUtil.serialize("A line in the help message is invalid. The default locale will be used."));
                locale = null;
                return;
            }
        }

        if(locale.reload() == null) {
            logger.warn(AdventureUtil.serialize("The reload message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.guiOpenError() == null) {
            logger.warn(AdventureUtil.serialize("The gui open error message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.joinEnabled() == null) {
            logger.warn(AdventureUtil.serialize("The join enabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.joinDisabled() == null) {
            logger.warn(AdventureUtil.serialize("The join disabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.quitEnabled() == null) {
            logger.warn(AdventureUtil.serialize("The quit enabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if (locale.quitDisabled() == null) {
            logger.warn(AdventureUtil.serialize("The quit disabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.motdEnabled() == null) {
            logger.warn(AdventureUtil.serialize("The motd enabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.motdDisabled() == null) {
            logger.warn(AdventureUtil.serialize("The motd disabled message is invalid. The default locale will be used."));
            locale = null;
            return;
        }

        if(locale.welcomeBroadcast() == null) {
            logger.warn(AdventureUtil.serialize("The welcome broadcast message is invalid. The default locale will be used."));
            locale = null;
        }
    }

    /**
     * Migrates the plugin's locale from legacy versions to the current version.
     */
    private void migrateLocale() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate the plugin's locale due to invalid plugin settings. The default locale will be used."));
            return;
        }
        if(locale == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate the plugin's locale due to invalid locale. The default locale will be used."));
            return;
        }

        switch(locale.configVersion()) {
            case "1.5.0.0" -> {
                // Latest version, do nothing.
            }

            case "1.2.0" -> {
                Locale newLocale = new Locale(
                        "1.5.0.0",
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

                Path path = Path.of(
                        skyWelcome.getDataFolder()
                                + File.separator
                                + "locale"
                                + File.separator
                                + settingsManager.getSettings().locale()
                                + ".yml");
                YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newLocale);
                    loader.save(node);
                    locale = newLocale;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            case "1.1.0", "1.0.0" -> {
                Locale newLocale = new Locale(
                        "1.5.0.0",
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

                Path path = Path.of(
                        skyWelcome.getDataFolder()
                                + File.separator
                                + "locale"
                                + File.separator
                                + settingsManager.getSettings().locale()
                                + ".yml");
                YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newLocale);
                    loader.save(node);
                    locale = newLocale;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + locale.configVersion());
        }
    }
}
