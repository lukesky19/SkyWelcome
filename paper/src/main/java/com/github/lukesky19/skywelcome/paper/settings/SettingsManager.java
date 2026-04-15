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
package com.github.lukesky19.skywelcome.paper.settings;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skywelcome.common.settings.ISettingsManager;
import com.github.lukesky19.skywelcome.common.settings.MessageBrokerConfig;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import com.github.lukesky19.skywelcome.paper.settings.legacy.LegacySettings;
import com.github.lukesky19.skywelcome.paper.settings.legacy.SettingsV2ToV4;
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
public class SettingsManager extends SimpleConfigManager<Settings> implements ISettingsManager {
    private @Nullable List<MessageConfig> joinMessages;
    private @Nullable List<MessageConfig> leaveMessages;
    private @Nullable List<MessageConfig> serverChangeMessages;
    
    /**
     * Constructor
     * @param skyWelcome A {@link ISkyPlugin} instance.
     */
    public SettingsManager(@NonNull ISkyPlugin skyWelcome) {
        super(skyWelcome, Path.of(skyWelcome.getDirectoryFile() + File.separator + "settings.yml"), Settings.class);
    }

    /**
     * Is the plugin in proxy mode or local mode?
     * @return true for proxy mode, or false for local mode.
     */
    @Override
    public boolean isProxyEnabled() {
        if(configuration == null) {
            logger.warn(AdventureUtility.plain("The plugin settings are invalid, the plugin will assume proxy support is not enabled."));
            return false;
        }

        return configuration.enableProxy();
    }

    /**
     * Get the configured {@link List} of {@link MessageConfig} for join messages.
     * @return A {@link List} of {@link MessageConfig} for join messages or null.
     */
    @Override
    public @Nullable List<MessageConfig> getJoinMessages() {
        return joinMessages;
    }

    /**
     * Set the configured join messages.
     * @param messageConfigList A {@link List} of {@link MessageConfig}.
     */
    @Override
    public void setJoinMessages(@NonNull List<MessageConfig> messageConfigList) {
        this.joinMessages = messageConfigList;
    }

    /**
     * Get the configured {@link List} of {@link MessageConfig} for leave messages.
     * @return A {@link List} of {@link MessageConfig} for leave messages or null.
     */
    @Override
    public @Nullable List<MessageConfig> getLeaveMessages() {
        return leaveMessages;
    }

    /**
     * Set the configured leave messages.
     * @param messageConfigList A {@link List} of {@link MessageConfig}.
     */
    @Override
    public void setLeaveMessages(@NonNull List<MessageConfig> messageConfigList) {
        this.leaveMessages = messageConfigList;
    }

    /**
     * Get the configured {@link List} of {@link MessageConfig} for server change messages.
     * @return A {@link List} of {@link MessageConfig} for server change messages or null.
     */
    @Override
    public @Nullable List<MessageConfig> getServerChangeMessages() {
        return serverChangeMessages;
    }

    /**
     * Set the configured server change messages.
     * @param messageConfigList A {@link List} of {@link MessageConfig}.
     */
    @Override
    public void setServerChangeMessages(@NonNull List<MessageConfig> messageConfigList) {
        this.serverChangeMessages = messageConfigList;
    }

    /**
     * Get the default join message. This will be the first one in the list.
     * @return The default join message or null if the settings is null or no join messages are configured.
     */
    @Override
    public @Nullable String getDefaultJoinMessage() {
        if(joinMessages == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default join message due to invalid plugin settings."));
            return null;
        }

        if(joinMessages.isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default join message due no join messages being configured."));
            return null;
        }

        return joinMessages.getFirst().message();
    }

    /**
     * Get the default leave message. This will be the first one in the list.
     * @return The default leave message or null if the settings is null or no leave messages are configured.
     */
    @Override
    public @Nullable String getDefaultLeaveMessage() {
        if(leaveMessages == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default leave message due to invalid plugin settings."));
            return null;
        }

        if(leaveMessages.isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default leave message due no leave messages being configured."));
            return null;
        }

        return leaveMessages.getFirst().message();
    }

    /**
     * Get the default server change message. This will be the first one in the list.
     * @return The default server change message or null if the settings is null or no server change messages are configured.
     */
    @Override
    public @Nullable String getDefaultServerChangeMessage() {
        if(serverChangeMessages == null) {
            logger.error(AdventureUtility.deserialize("Unable to get the default server change message due to invalid plugin settings."));
            return null;
        }

        if(serverChangeMessages.isEmpty()) {
            logger.error(AdventureUtility.deserialize("Unable to get the default server change message due no leave messages being configured."));
            return null;
        }

        return serverChangeMessages.getFirst().message();
    }

    @Override
    public void loadConfiguration() {
        configuration = null;
        joinMessages = null;
        leaveMessages = null;
        serverChangeMessages = null;

        if(configurationPath == null) return;

        saveDefaultConfiguration();

        YamlConfigurationLoader loader = createLoader(configurationPath);
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
                logger.warn(AdventureUtility.deserialize("Failed to load and or migrate settings.yml."));
                return;
            }

            Settings migratedConfiguration = migrateConfiguration(settings);

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtility.deserialize("Settings configuration validation failed."));
                return;
            }

            assert migratedConfiguration != null;
            this.configuration = migratedConfiguration;

            if(!configuration.enableProxy()) {
                this.joinMessages = configuration.joinMessages();
                this.leaveMessages = configuration.quitMessages();
                this.serverChangeMessages = configuration.serverChangeMessages();
            }
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.deserialize("Failed to load configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public void saveDefaultConfiguration() {
        if(configurationPath == null) return;
        if(configurationPath.toFile().exists()) return;

        Settings defaultSettings = new Settings(
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                "en_US",
                true,
                true,
                true,
                true,
                List.of(new MessageConfig(
                        "skywelcome.join.default",
                        "<gray>[<gray><green>+</green><gray>]</gray> <papi:essentials_nickname>")),
                List.of("Welcome to the server, <papi:essentials_nickname>!"),
                List.of(new MessageConfig(
                        "skywelcome.leave.default",
                        "<gray>[<gray><red>-</red><gray>]</gray> <papi:essentials_nickname>")),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                new WelcomeRewards(
                        true,
                        false,
                        50.0,
                        List.of(new ItemStackConfig(
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
                                new ItemStackConfig.OptionsConfig(null, null, null, null, null))),
                        List.of("give %player_name% emerald 1"),
                        List.of("<aqua>Thanks for welcoming a new player. Enjoy this reward: <green>$50</green></aqua>")));

        saveConfiguration(defaultSettings);
    }

    /**
     * Migrate the configuration from v6 and beyond. See {@link #loadAndMigrate(int, ConfigurationNode)} for v5 and below.
     * @param settings The {@link Settings} to migrate.
     * @return The migrated {@link Settings} or null.
     */
    @Override
    public @Nullable Settings migrateConfiguration(@NonNull Settings settings) {
        switch(settings.version()) {
            case 7 -> {
                // Latest version, do nothing.
                return settings;
            }

            case 6 -> {
                Settings newSettings = new Settings(
                        7,
                        new MessageBrokerConfig(
                                "localhost",
                                5672,
                                "",
                                ""),
                        false,
                        settings.locale(),
                        settings.globalJoinToggle(),
                        settings.globalQuitToggle(),
                        settings.globalMotdToggle(),
                        true,
                        settings.joinMessages(),
                        settings.motd(),
                        settings.quitMessages(),
                        List.of(new MessageConfig(
                                "skywelcome.change.default",
                                "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                        settings.welcomeRewards());

                saveConfiguration(newSettings);

                return newSettings;
            }

            case 1, 2, 3, 4, 5 -> {
                logger.error(AdventureUtility.plain("The migrate configuration method was passed settings with version: " + settings.version() + ". This should not occur as the configuration should of been migrated for these versions before the method was called."));
                return null;
            }

            default -> {
                logger.warn(AdventureUtility.deserialize("Failed to migrate the settings configuration due to an unsupported config version. Version: " + settings.version() + " Class name: " + this.getClass().getName()));
                return null;
            }
        }
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        return settings != null;
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
                        logger.warn(AdventureUtility.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV5ToV7(oldSettings);

                    saveConfiguration(settings);
                }

                case 4 -> {
                    try {
                        SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                        if(oldSettings == null) {
                            logger.warn(AdventureUtility.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                            return null;
                        }

                        settings = updateSettingsV4ToV7(oldSettings);

                        if(settings != null) {
                            saveConfiguration(settings);
                        }
                    } catch (ConfigurateException e) {
                        logger.error(AdventureUtility.deserialize("Failed to load legacy settings for version: " + version));
                    }
                }

                case 3 -> {
                    SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtility.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV3ToV7(oldSettings);

                    if(settings != null) {
                        saveConfiguration(settings);
                    }
                }

                case 2 -> {
                    SettingsV2ToV4 oldSettings = root.get(SettingsV2ToV4.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtility.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV2ToV7(oldSettings);

                    saveConfiguration(settings);
                }

                case 1 -> {
                    LegacySettings oldSettings = root.get(LegacySettings.class);
                    if(oldSettings == null) {
                        logger.warn(AdventureUtility.deserialize("Unable to migrate settings due to the old settings failing to load for version: " + version));
                        return null;
                    }

                    settings = updateSettingsV1ToV7(oldSettings);

                    saveConfiguration(settings);
                }

                default -> {
                    logger.warn(AdventureUtility.deserialize("Failed to load configuration file settings.yml due to an unsupported config version. Version: " + version + " Class name: " + this.getClass().getName()));
                    return null;
                }
            }

            return settings;
        } catch (SerializationException e) {
            logger.error(AdventureUtility.deserialize("Failed to load and migrate configuration file settings.yml. Error: " + e.getMessage()));
            return null;
        }
    }

    /**
     * Migrate the settings for version V5 to V7.
     * @param oldSettings The {@link Settings}
     * @return The updated {@link Settings}.
     */
    private @NonNull Settings updateSettingsV5ToV7(@NonNull Settings oldSettings) {
        return new Settings(
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                oldSettings.locale(),
                oldSettings.globalJoinToggle(),
                oldSettings.globalQuitToggle(),
                oldSettings.globalMotdToggle(),
                true,
                oldSettings.joinMessages(),
                oldSettings.motd(),
                oldSettings.quitMessages(),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                oldSettings.welcomeRewards());
    }

    /**
     * Migrate the settings for version V4 to V7.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettingsV4ToV7(@NonNull SettingsV2ToV4 oldSettings) {
        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings v4 to v6 due to the reward item material being invalid."));
            return null;
        }

        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings v4 to v6 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }

        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings v4 to v6 due to the amount being invalid."));
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

        return new Settings(
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                true,
                oldSettings.join().values().stream()
                        .map(join -> new MessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new MessageConfig(quit.permission(), quit.message()))
                        .toList(),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                new WelcomeRewards(
                        oldSettings.welcomeRewards().enabled(),
                        false,
                        oldSettings.welcomeRewards().cash(),
                        List.of(welcomeItem),
                        oldSettings.welcomeRewards().commands(),
                        oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the settings for version V3 to V7.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettingsV3ToV7(@NonNull SettingsV2ToV4 oldSettings) {
        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings V3 to V6 due to the reward item material being invalid."));
            return null;
        }
        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings V3 to V6 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }
        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtility.deserialize("Unable to migrate settings V3 to V6 due to the amount being invalid."));
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

        return new Settings(
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                true,
                oldSettings.join().values().stream()
                        .map(join -> new MessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new MessageConfig(quit.permission(), quit.message()))
                        .toList(),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                new WelcomeRewards(
                        oldSettings.welcomeRewards().enabled(),
                        false,
                        oldSettings.welcomeRewards().cash(),
                        List.of(welcomeItem),
                        oldSettings.welcomeRewards().commands(),
                        oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the settings for version V2 to V7.
     * @param oldSettings The {@link SettingsV2ToV4}
     * @return The updated {@link Settings}.
     */
    private @NonNull Settings updateSettingsV2ToV7(@NonNull SettingsV2ToV4 oldSettings) {
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

        return new Settings(
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                oldSettings.options().locale(),
                oldSettings.options().joins(),
                oldSettings.options().motd(),
                oldSettings.options().quits(),
                true,
                oldSettings.join().values().stream()
                        .map(join -> new MessageConfig(join.permission(), join.message()))
                        .toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream()
                        .map(quit -> new MessageConfig(quit.permission(), quit.message()))
                        .toList(),
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                new WelcomeRewards(
                        true,
                        false,
                        50.0,
                        List.of(welcomeItem),
                        rewardCommands,
                        rewardMessages));
    }

    /**
     * Migrate the legacy settings (V1) to V7.
     * @param legacySettings The {@link LegacySettings} to migrate.
     * @return The migrated {@link Settings}
     */
    private @NonNull Settings updateSettingsV1ToV7(@NonNull LegacySettings legacySettings) {
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

        List<MessageConfig> joinMessageConfigList = new ArrayList<>();
        List<MessageConfig> quitMessageConfigList = new ArrayList<>();
        joinMessageConfigList.add(new MessageConfig(
                "skywelcome.join.default",
                migratePlaceholderAPIFormat(legacySettings.join().content())));
        quitMessageConfigList.add(new MessageConfig(
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
                7,
                new MessageBrokerConfig(
                        "localhost",
                        5672,
                        "",
                        ""),
                false,
                "en_US",
                true,
                true,
                true,
                true,
                joinMessageConfigList,
                motdList,
                quitMessageConfigList,
                List.of(new MessageConfig(
                        "skywelcome.change.default",
                        "<gray>[</gray><red><previous_server></red> <white>-></white> <green><current_server></green><gray>]</gray> <papi:essentials_nickname>")),
                new WelcomeRewards(
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
                    logger.warn(AdventureUtility.deserialize("Failed to convert String-based version to numeric version due to an unrecognized version."));
                    version = 0;
                }
            }
        } catch (SerializationException e) {
            logger.warn(AdventureUtility.deserialize("Failed to convert String-based version to numeric version"));
            version = 0;
        }

        return version;
    }
}