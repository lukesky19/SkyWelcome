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

import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.enums.RewardType;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsManager {
    private final SkyWelcome skyWelcome;
    private Settings settings;

    public SettingsManager(SkyWelcome skyWelcome) {
        this.skyWelcome = skyWelcome;
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

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            settings = loader.load().get(Settings.class);
        } catch (ConfigurateException ignored) {}

        migrateSettings();
        validateSettings();
    }

    private void validateSettings() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(settings == null) {
            logger.error(FormatUtil.format("<red>Failed to load <yellow>settings.yml</yellow>.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(settings.configVersion() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>config-version</yellow> setting in <yellow>settings.yml</yellow> does exist.</red>"));
            logger.error(FormatUtil.format("<red>This means your config did not migrate properly or you modified the config-version setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        Settings.Options options = settings.options();
        if(options == null) {
            logger.error(FormatUtil.format("<red>The options configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.locale() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>locale</yellow> setting in <yellow>settings.yml</yellow> is invalid.</red>"));
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
            logger.error(FormatUtil.format("<red>The <yellow>locale</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.joins() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>joins</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.quits() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>quits</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(options.motd() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>motd</yellow> setting under <yellow>options</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(Map.Entry<String, Settings.Join> entry : settings.join().entrySet()) {
            String id = entry.getKey();
            Settings.Join joinSettings = entry.getValue();

            if(joinSettings == null) {
                logger.error(FormatUtil.format("<red>The join configuration of id <yellow> " + id + " </yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(joinSettings.permission() == null) {
                logger.error(FormatUtil.format("<red>The <yellow>permission</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(joinSettings.message() == null) {
                logger.error(FormatUtil.format("<red>The <yellow>message</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        Settings.Motd motdSettings = settings.motd();
        if(motdSettings == null) {
            logger.error(FormatUtil.format("<red>The motd configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(String string : motdSettings.contents()) {
            if(string == null) {
                logger.error(FormatUtil.format("<red>A String in the <yellow>contents</yellow> of the motd configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        for(Map.Entry<String, Settings.Quit> entry : settings.quit().entrySet()) {
            String id = entry.getKey();
            Settings.Quit quitSettings = entry.getValue();

            if(quitSettings == null) {
                logger.error(FormatUtil.format("<red>The quit configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(quitSettings.permission() == null) {
                logger.error(FormatUtil.format("<red>The <yellow>permission</yellow> for the quit configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }

            if(quitSettings.message() == null) {
                logger.error(FormatUtil.format("<red>The <yellow>message</yellow> for the join configuration of id <yellow>" + id + "</yellow> in <yellow>settings.yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        if(settings.welcomeRewards() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>welcome-rewards</yellow> configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        RewardType type = RewardType.getType(settings.welcomeRewards().type());
        if(type == null) {
            logger.error(FormatUtil.format("<red>The <yellow>type</yellow> for the <yellow>welcome-rewards</yellow> configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(type.equals(RewardType.ITEM) && settings.welcomeRewards().item() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>cash</yellow> for the <yellow>welcome-rewards</yellow> configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(type.equals(RewardType.CASH) && settings.welcomeRewards().cash() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>cash</yellow> for the <yellow>welcome-rewards</yellow> configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(type.equals(RewardType.COMMANDS) && settings.welcomeRewards().commands() == null) {
            logger.error(FormatUtil.format("<red>The <yellow>commands</yellow> for the <yellow>welcome-rewards</yellow> configuration in <yellow>settings.yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
        }
    }

    private void migrateSettings() {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

        switch(settings.configVersion()) {
            case "1.3.0" -> {
                // Current Version, do nothing
            }

            case "1.2.0" -> {
                Settings oldSettings;
                try {
                    oldSettings = loader.load().get(Settings.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                Settings newSettings = updateSettings120To130(Objects.requireNonNull(oldSettings));
                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newSettings);
                    loader.save(node);
                    settings = newSettings;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            case "1.1.0" -> {
                Settings oldSettings;
                try {
                    oldSettings = loader.load().get(Settings.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                Settings newSettings = updateSettings110To130(Objects.requireNonNull(oldSettings));
                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newSettings);
                    loader.save(node);
                    settings = newSettings;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            case null -> {
                com.github.lukesky19.skywelcome.config.settings.legacy.Settings legacySettings;
                try {
                    legacySettings = loader.load().get(com.github.lukesky19.skywelcome.config.settings.legacy.Settings.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                assert legacySettings != null;
                Settings newSettings = migrateLegacySettings(legacySettings);

                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newSettings);
                    loader.save(node);
                    settings = newSettings;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + settings.configVersion());
        }
    }

    private static @NotNull Settings updateSettings120To130(Settings oldSettings) {
        return new Settings("1.3.0", oldSettings.options(), oldSettings.join(), oldSettings.motd(),
                oldSettings.quit(), new Settings.WelcomeRewards(oldSettings.welcomeRewards().enabled(),
                false, oldSettings.welcomeRewards().type(), oldSettings.welcomeRewards().cash(),
                oldSettings.welcomeRewards().item(), oldSettings.welcomeRewards().commands(),
                oldSettings.welcomeRewards().messages()));
    }


    private static @NotNull Settings updateSettings110To130(Settings oldSettings) {
        ArrayList<String> rewardCommands = new ArrayList<>();
        ArrayList<String> rewardMessages = new ArrayList<>();
        rewardCommands.add("give %player_name% emerald 1");
        rewardMessages.add("<aqua>Thanks for welcoming a new player. Enjoy this reward: $50</aqua>");

        return new Settings("1.2.0", oldSettings.options(), oldSettings.join(), oldSettings.motd(),
                oldSettings.quit(), new Settings.WelcomeRewards(true, false,"CASH",
                50.0, new Settings.Item("DIAMOND", 1), rewardCommands, rewardMessages));
    }

    private static @NotNull Settings migrateLegacySettings(com.github.lukesky19.skywelcome.config.settings.legacy.Settings legacySettings) {
        LinkedHashMap<String, Settings.Join> joinMap = new LinkedHashMap<>();
        LinkedHashMap<String, Settings.Quit> quitMap = new LinkedHashMap<>();
        joinMap.put("0", new Settings.Join("skywelcome.join.default", migratePlaceholderAPIFormat(legacySettings.join().content())));
        quitMap.put("0", new Settings.Quit("skywelcome.quit.default", migratePlaceholderAPIFormat(legacySettings.quit().content())));

        ArrayList<String> rewardCommands = new ArrayList<>();
        ArrayList<String> rewardMessages = new ArrayList<>();
        rewardCommands.add("give %player_name% emerald 1");
        rewardMessages.add("<aqua>Thanks for welcoming a new player. Enjoy this reward: $50</aqua>");

        List<String> motdList = new ArrayList<>();
        for(String msg : legacySettings.motd().contents()) {
            motdList.add(migratePlaceholderAPIFormat(msg));
        }

        Settings.Motd motdSettings = new Settings.Motd(motdList);

        return new Settings("1.3.0", new Settings.Options("en_US", true, true, true), joinMap, motdSettings, quitMap, new Settings.WelcomeRewards(true, false,"CASH", 50.0, new Settings.Item("DIAMOND", 1), rewardCommands, rewardMessages));
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
