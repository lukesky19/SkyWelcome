/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024  lukeskywlker19

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
package com.github.lukesky19.skywelcome.config.settings;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsManager {
    final SkyWelcome skyWelcome;
    final ConfigurationUtility configurationUtility;
    Settings settings;

    public SettingsManager(SkyWelcome skyWelcome, ConfigurationUtility configurationUtility) {
        this.skyWelcome = skyWelcome;
        this.configurationUtility = configurationUtility;
    }

    public Settings getSettings() {
        return settings;
    }

    public void reload() {
        settings = null;

        copyDefaultLocales();

        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyWelcome.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            settings = loader.load().get(Settings.class);
        } catch (ConfigurateException ignored) {}

        migrateSettings();
        validateSettings();
    }

    private void validateSettings() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(settings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load <yellow>settings.yml</yellow>.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(settings.configVersion() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>config-version</yellow> setting in <yellow>settings.yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the config-version setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        Settings.Options options = settings.options();
        if(options == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The options configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.locale() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>locale</yellow> setting in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        Path path = Path.of(
                skyWelcome.getDataFolder()
                        + File.separator
                        + "locale"
                        + File.separator
                        + options.locale()
                        + ".yml");
        if(Files.notExists(path)) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>locale</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.joins() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>joins</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.quits() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>quits</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.motd() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>motd</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(Map.Entry<String, Settings.Join> entry : settings.join().entrySet()) {
            String id = entry.getKey();
            Settings.Join joinSettings = entry.getValue();

            if(joinSettings == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The join configuration of id <yellow> " + id + " </yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(joinSettings.permission() == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>permission</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(joinSettings.message() == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>message</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        Settings.Motd motdSettings = settings.motd();
        if(motdSettings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The motd configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(String string : motdSettings.contents()) {
            if(string == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>A String in the <yellow>contents</yellow> of the motd configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        for(Map.Entry<String, Settings.Quit> entry : settings.quit().entrySet()) {
            String id = entry.getKey();
            Settings.Quit quitSettings = entry.getValue();

            if(quitSettings == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The quit configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(quitSettings.permission() == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>permission</yellow> for the quit configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(quitSettings.message() == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>message</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }
    }

    private void migrateSettings() {
        if(settings.configVersion() == null) {
            com.github.lukesky19.skywelcome.config.settings.legacy.Settings legacySettings;
            Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
            YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
            try {
                legacySettings = loader.load().get(com.github.lukesky19.skywelcome.config.settings.legacy.Settings.class);
            } catch (ConfigurateException e) {
                throw new RuntimeException(e);
            }

            assert legacySettings != null;
            Settings newSettings = createNewSettings(legacySettings);

            CommentedConfigurationNode node = loader.createNode();
            try {
                node.set(newSettings);
                loader.save(node);
                settings = newSettings;
            } catch (ConfigurateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static @NotNull Settings createNewSettings(com.github.lukesky19.skywelcome.config.settings.legacy.Settings legacySettings) {
        LinkedHashMap<String, Settings.Join> joinMap = new LinkedHashMap<>();
        LinkedHashMap<String, Settings.Quit> quitMap = new LinkedHashMap<>();
        joinMap.put("0", new Settings.Join("skywelcome.join.default", migratePlaceholderAPIFormat(legacySettings.join().content())));
        quitMap.put("0", new Settings.Quit("skywelcome.quit.default", migratePlaceholderAPIFormat(legacySettings.quit().content())));

        List<String> motdList = new ArrayList<>();
        for(String msg : legacySettings.motd().contents()) {
            motdList.add(migratePlaceholderAPIFormat(msg));
        }
        Settings.Motd motdSettings = new Settings.Motd(motdList);

        return new Settings("1.1.0", new Settings.Options("en_US", true, true, true), joinMap, motdSettings, quitMap);
    }

    /**
     * Converts %PLACEHOLDER_NAME% format to a <papi:PLACEHOLDER_NAME> format.
     * @param msg The String to find and convert PlaceholderAPI formats for.
     * @return A String with the new PlaceholderAPI format.
     */
    private static String migratePlaceholderAPIFormat(String msg) {
        Pattern pattern = Pattern.compile("%([^%]+)%");
        Matcher matcher = pattern.matcher(msg);

        if(matcher.find()) {
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "<papi:" + matcher.group(1) + ">");
            }
            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return msg;
        }
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     * It is located here instead of LocaleManager because part of the Settings validation is making sure the locale file exists on disk.
     */
    private void copyDefaultLocales() {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyWelcome.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }
}
