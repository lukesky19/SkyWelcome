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
package com.github.lukesky19.skywelcome.config.settings;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.legacy.LegacySettings;
import com.github.lukesky19.skywelcome.config.settings.legacy.SettingsV2ToV4;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     */
    public SettingsManager(@NonNull SkyWelcome skyWelcome) {
        super(skyWelcome, Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public void loadConfiguration() {
        configuration = null;

        if(configurationPath == null) return;

        saveBundledConfig();

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);
        try {
            ConfigurationNode root = loader.load();
            int version = getVersion(root);

            Settings settings;
            if(version >= 6) {
                settings = root.get(Settings.class);
            } else {
                settings = loadAndMigrate(version, root);
            }

            if(settings == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load and or migrate settings.yml."));
                return;
            }

            Settings migratedConfiguration = migrateConfiguration(settings);

            if(!settings.equals(migratedConfiguration)) {
                saveConfiguration(migratedConfiguration);
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtil.deserialize("Settings configuration validation failed."));
                return;
            }

            this.configuration = migratedConfiguration;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtil.deserialize("Failed to load configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    protected void saveBundledConfig() {
        if(configurationPath == null) return;

        if(!configurationPath.toFile().exists()) {
            plugin.saveResource("settings.yml", false);
        }
    }

    /**
     * Migrate the configuration from v6 and beyond.
     * Currently, version 6 is the lastest version so the passed settings are returned.
     * @param settings The {@link Settings}.
     * @return The passed {@link Settings}.
     */
    @Override
    public @NonNull Settings migrateConfiguration(@NonNull Settings settings) {
        return settings;
    }

    /**
     * Load the configuration and migrate if necessary.
     * @param version The configuration version.
     * @param root The root {@link ConfigurationNode}.
     * @return The migrated {@link Settings} or null.
     */
    private @Nullable Settings loadAndMigrate(int version, @NonNull ConfigurationNode root) {
        try {
            Settings settings = null;
            switch (version) {
                case 5 -> {
                    Settings oldSettings = root.get(Settings.class);
                    if (oldSettings == null) {
                        logger.warn(AdventureUtil.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV5ToV6(oldSettings);

                    saveConfiguration(settings);
                }

                case 4 -> {
                    try {
                        SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                        if(oldSettings == null) {
                            logger.warn(AdventureUtil.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                            return null;
                        }

                        settings = updateSettingsV4ToV6(oldSettings);

                        if(settings != null) {
                            saveConfiguration(settings);
                        }
                    } catch (ConfigurateException e) {
                        logger.error(AdventureUtil.deserialize("Failed to load legacy settings for version: " + version));
                    }
                }

                case 3 -> {
                    SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtil.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV3ToV6(oldSettings);

                    if(settings != null) {
                        saveConfiguration(settings);
                    }
                }

                case 2 -> {
                    SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtil.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV2ToV6(oldSettings);

                    saveConfiguration(settings);
                }

                case 1 -> {
                    LegacySettings oldSettings = root.get(LegacySettings.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtil.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV1ToV6(oldSettings);

                    saveConfiguration(settings);
                }

                default -> {
                    logger.warn(AdventureUtil.deserialize("Failed to load configuration file settings.yml due to an unsupported config version. Version: " + version + " Class name: " + this.getClass().getName()));
                    return null;
                }
            }

            return settings;
        } catch (SerializationException e) {
            logger.error(AdventureUtil.deserialize("Failed to load and migrate configuration file settings.yml. Error: " + e.getMessage()));
            return null;
        }
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        return settings != null;
    }

    /**
     * Migrate the settings for version V5 to V6.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @NonNull Settings updateSettingsV5ToV6(@NonNull Settings oldSettings) {
        return new Settings(
                6,
                oldSettings.locale(),
                oldSettings.globalJoinToggle(),
                oldSettings.globalQuitToggle(),
                oldSettings.globalMotdToggle(),
                oldSettings.joinMessages(),
                oldSettings.motd(),
                oldSettings.quitMessages(),
                oldSettings.welcomeRewards());
    }

    /**
     * Migrate the settings for version V4 to V6.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettingsV4ToV6(@NonNull SettingsV2ToV4 oldSettings) {
        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings v4 to v6 due to the reward item material being invalid."));
            return null;
        }

        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings v4 to v6 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }

        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings v4 to v6 due to the amount being invalid."));
            return null;
        }

        ItemStackConfig welcomeItem = new ItemStackConfig(
                itemType,
                amount,
                null,
                null,
                List.of(),
                null,
                null,
                List.of(),
                new ItemStackConfig.PotionConfig(null, List.of()),
                new ItemStackConfig.ColorConfig(false, null, null, null),
                null,
                List.of(),
                new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                new ItemStackConfig.ArmorTrimConfig(null, null),
                List.of(),
                new ItemStackConfig.OptionsConfig(null, null, null, null, null));

        return new Settings(6,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                oldSettings.join().values().stream()
                        .map(join -> new Settings.JoinMessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message()))
                        .toList(),
                new Settings.WelcomeRewards(
                        oldSettings.welcomeRewards().enabled(),
                        false,
                        oldSettings.welcomeRewards().cash(),
                        List.of(welcomeItem),
                        oldSettings.welcomeRewards().commands(),
                        oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the settings for version V3 to V6.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettingsV3ToV6(@NonNull SettingsV2ToV4 oldSettings) {
        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings V3 to V6 due to the reward item material being invalid."));
            return null;
        }
        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings V3 to V6 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }
        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtil.deserialize("Unable to migrate settings V3 to V6 due to the amount being invalid."));
            return null;
        }

        ItemStackConfig welcomeItem = new ItemStackConfig(
                itemType,
                amount,
                null,
                null,
                List.of(),
                null,
                null,
                List.of(),
                new ItemStackConfig.PotionConfig(null, List.of()),
                new ItemStackConfig.ColorConfig(false, null, null, null),
                null,
                List.of(),
                new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                new ItemStackConfig.ArmorTrimConfig(null, null),
                List.of(),
                new ItemStackConfig.OptionsConfig(null, null, null, null, null));

        return new Settings(6,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                oldSettings.join().values().stream()
                        .map(join -> new Settings.JoinMessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message()))
                        .toList(),
                new Settings.WelcomeRewards(
                        oldSettings.welcomeRewards().enabled(),
                        false,
                        oldSettings.welcomeRewards().cash(),
                        List.of(welcomeItem),
                        oldSettings.welcomeRewards().commands(),
                        oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the settings for version V2 to V6.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @NonNull Settings updateSettingsV2ToV6(@NonNull SettingsV2ToV4 oldSettings) {
        ItemStackConfig welcomeItem = new ItemStackConfig(
                ItemType.DIAMOND,
                1,
                null,
                null,
                List.of(),
                null,
                null,
                List.of(),
                new ItemStackConfig.PotionConfig(null, List.of()),
                new ItemStackConfig.ColorConfig(false, null, null, null),
                null,
                List.of(),
                new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                new ItemStackConfig.ArmorTrimConfig(null, null),
                List.of(),
                new ItemStackConfig.OptionsConfig(null, null, null, null, null));

        List<String> rewardCommands = new ArrayList<>();
        List<String> rewardMessages = new ArrayList<>();
        rewardCommands.add("give %player_name% emerald 1");
        rewardMessages.add("<aqua>Thanks for welcoming a new player. Enjoy this reward: $50</aqua>");

        return new Settings(6,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                oldSettings.join().values().stream()
                        .map(join -> new Settings.JoinMessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message()))
                        .toList(),
                new Settings.WelcomeRewards(
                        true,
                        false,
                        50.0,
                        List.of(welcomeItem),
                        rewardCommands,
                        rewardMessages));
    }

    /**
     * Migrate the legacy settings (V1) to V6.
     * @param legacySettings The {@link LegacySettings} to migrate.
     * @return The migrated {@link Settings}
     */
    private @NonNull Settings updateSettingsV1ToV6(@NonNull LegacySettings legacySettings) {
        ItemStackConfig welcomeItem = new ItemStackConfig(
                ItemType.DIAMOND,
                1,
                null,
                null,
                List.of(),
                null,
                null,
                List.of(),
                new ItemStackConfig.PotionConfig(null, List.of()),
                new ItemStackConfig.ColorConfig(false, null, null, null),
                null,
                List.of(),
                new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                new ItemStackConfig.ArmorTrimConfig(null, null),
                List.of(),
                new ItemStackConfig.OptionsConfig(null, null, null, null, null));

        List<Settings.JoinMessageConfig> joinMessageConfigList = new ArrayList<>();
        List<Settings.QuitMessageConfig> quitMessageConfigList = new ArrayList<>();
        joinMessageConfigList.add(new Settings.JoinMessageConfig(
                "skywelcome.join.default",
                migratePlaceholderAPIFormat(legacySettings.join().content())));
        quitMessageConfigList.add(new Settings.QuitMessageConfig(
                "skywelcome.quit.default",
                migratePlaceholderAPIFormat(legacySettings.quit().content())));

        List<String> rewardCommands = new ArrayList<>();
        List<String> rewardMessages = new ArrayList<>();
        rewardCommands.add("give %player_name% emerald 1");
        rewardMessages.add("<aqua>Thanks for welcoming a new player. Enjoy this reward: $50</aqua>");

        List<String> motdList = new ArrayList<>();
        for(String msg : legacySettings.motd().contents()) {
            motdList.add(migratePlaceholderAPIFormat(msg));
        }

        return new Settings(
                6,
                "en_US",
                true,
                true,
                true,
                joinMessageConfigList,
                motdList,
                quitMessageConfigList,
                new Settings.WelcomeRewards(
                        true,
                        false,
                        50.0,
                        List.of(welcomeItem),
                        rewardCommands,
                        rewardMessages));
    }

    /**
     * Converts %PLACEHOLDER_NAME% format to a <papi:PLACEHOLDER_NAME> format.
     * @param msg The String to find and convert PlaceholderAPI formats for.
     * @return A String with the new PlaceholderAPI format.
     */
    private @NonNull String migratePlaceholderAPIFormat(String msg) {
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
     * Get the version number.
     * @param root The root {@link ConfigurationNode}.
     * @return The config version.
     */
    private int getVersion(@NonNull ConfigurationNode root) {
        ConfigurationNode versionNode = root.node("version");
        int version = versionNode.getInt();
        if(version > 0) return version;

        ConfigurationNode legacyVersionNode = root.node("config-version");
        String legacyVersion = legacyVersionNode.virtual() ? null : legacyVersionNode.getString();
        try {
            switch(legacyVersion) {
                case "1.5.0.0" -> {
                    versionNode.set(5);
                    version = 5;
                }

                case "1.3.0" -> {
                    versionNode.set(4);
                    version = 4;
                }

                case "1.2.0" -> {
                    versionNode.set(3);
                    version = 3;
                }

                case "1.1.0" -> {
                    versionNode.set(2);
                    version = 2;
                }

                case null -> {
                    versionNode.set(1);
                    version = 1;
                }

                default -> {
                    logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version due to an unrecognized version."));
                    version = 0;
                }
            }
        } catch (SerializationException e) {
            logger.warn(AdventureUtil.deserialize("Failed to convert String-based version to numeric version"));
            version = 0;
        }

        return version;
    }
}