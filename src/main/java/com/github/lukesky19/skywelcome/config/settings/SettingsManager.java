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
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.legacy.LegacySettings;
import com.github.lukesky19.skywelcome.config.settings.legacy.SettingsV110ToV130;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager {
    private final @NotNull SkyWelcome skyWelcome;
    private @Nullable Settings settings;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     */
    public SettingsManager(@NotNull SkyWelcome skyWelcome) {
        this.skyWelcome = skyWelcome;
    }

    /**
     * Get the plugin's settings.
     * @return The plugin's {@link Settings}.
     */
    public @Nullable Settings getSettings() {
        return settings;
    }

    /**
     * Reload the plugin's settings.
     */
    public void reload() {
        settings = null;

        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyWelcome.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            SettingsVersionOnly settingsVersionOnly = loader.load().get(SettingsVersionOnly.class);
            if(settingsVersionOnly == null) return;
            if(settingsVersionOnly.configVersion() != null && settingsVersionOnly.configVersion().equals("1.5.0.0")) {
                settings = loader.load().get(Settings.class);
            } else {
                migrateSettings(settingsVersionOnly.configVersion());
            }
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(AdventureUtil.serialize("Failed to load plugin settings: " + e.getMessage()));
            return;
        }

        //  Validate settings
        validateSettings();
    }

    /**
     * Checks if the plugin's settings are outdated.
     */
    private void validateSettings() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(settings == null) return;

        if(settings.configVersion() == null) {
            logger.error(AdventureUtil.serialize("The config version in settings.yml is invalid."));
            settings = null;
            return;
        }

        if(!settings.configVersion().equals("1.5.0.0")) {
            logger.error(AdventureUtil.serialize("Your settings.yml configuration is outdated and needs to be updated."));
            settings = null;
        }
    }

    /**
     * Migrate legacy settings versions to the latest if possible.
     */
    private void migrateSettings(@Nullable String configVersion) {
        ComponentLogger logger = skyWelcome.getComponentLogger();

        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

        switch(configVersion) {
            case "1.5.0.0" -> {
                // Current Version, do nothing
            }

            case "1.3.0" -> {
                SettingsV110ToV130 oldSettings;
                try {
                    oldSettings = loader.load().get(SettingsV110ToV130.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                if(oldSettings == null) {
                    logger.warn(AdventureUtil.serialize("Unable to migrate settings due to the old settings failing to load."));
                    return;
                }

                Settings newSettings = updateSettings130To1500(oldSettings);
                if(newSettings == null) return;

                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newSettings);
                    loader.save(node);
                    settings = newSettings;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            case "1.2.0" -> {
                SettingsV110ToV130 oldSettings;
                try {
                    oldSettings = loader.load().get(SettingsV110ToV130.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                if(oldSettings == null) {
                    logger.warn(AdventureUtil.serialize("Unable to migrate settings due to the old settings failing to load."));
                    return;
                }

                Settings newSettings = updateSettings120To1500(oldSettings);
                if(newSettings == null) return;

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
                SettingsV110ToV130 oldSettings;
                try {
                    oldSettings = loader.load().get(SettingsV110ToV130.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                if(oldSettings == null) {
                    logger.warn(AdventureUtil.serialize("Unable to migrate settings due to the old settings failing to load."));
                    return;
                }

                Settings newSettings = updateSettings110To1500(oldSettings);
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
                LegacySettings legacySettings;
                try {
                    legacySettings = loader.load().get(LegacySettings.class);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }

                if(legacySettings == null) {
                    logger.warn(AdventureUtil.serialize("Unable to migrate settings due to the legacy settings failing to load."));
                    return;
                }
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

            default -> throw new IllegalStateException("Unexpected value: " + configVersion);
        }
    }

    /**
     * Migrate the {@link SettingsV110ToV130} for version 1.3.0 to {@link Settings} 1.5.0.0.
     * @param oldSettings The {@link SettingsV110ToV130}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettings130To1500(@NotNull SettingsV110ToV130 oldSettings) {
        ComponentLogger logger = skyWelcome.getComponentLogger();

        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.3.0 to 1.5.0.0 due to the reward item material being invalid."));
            return null;
        }
        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.3.0 to 1.5.0.0 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }
        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.3.0 to 1.5.0.0 due to the amount being invalid."));
            return null;
        }

        ItemStackConfig welcomeItem = new ItemStackConfig(
                itemType.getKey().getKey(),
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

        return new Settings("1.5.0.0", oldSettings.options().locale(), oldSettings.options().joins(),
                oldSettings.options().motd(), oldSettings.options().quits(),
                oldSettings.join().values().stream().map(join -> new Settings.JoinMessageConfig(join.permission(), join.message())).toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream().map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message())).toList(),
                new Settings.WelcomeRewards(oldSettings.welcomeRewards().enabled(),
                        false, oldSettings.welcomeRewards().cash(),
                        List.of(welcomeItem), oldSettings.welcomeRewards().commands(),
                        oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the {@link SettingsV110ToV130} for version 1.2.0 to {@link Settings} 1.5.0.0.
     * @param oldSettings The {@link SettingsV110ToV130}
     * @return The updated {@link Settings}.
     */
    private @Nullable Settings updateSettings120To1500(@NotNull SettingsV110ToV130 oldSettings) {
        ComponentLogger logger = skyWelcome.getComponentLogger();

        Material material = Material.getMaterial(oldSettings.welcomeRewards().item().material());
        if(material == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.2.0 to 1.5.0.0 due to the reward item material being invalid."));
            return null;
        }
        ItemType itemType = material.asItemType();
        if(itemType == null) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.2.0 to 1.5.0.0 due to being unable to find the ItemType that corresponds to the Material."));
            return null;
        }
        Integer amount = oldSettings.welcomeRewards().item().amount();
        if(amount == null || amount <= 0) {
            logger.error(AdventureUtil.serialize("Unable to migrate settings 1.2.0 to 1.5.0.0 due to the amount being invalid."));
            return null;
        }

        ItemStackConfig welcomeItem = new ItemStackConfig(
                itemType.getKey().getKey(),
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

        return new Settings("1.5.0.0", oldSettings.options().locale(), oldSettings.options().joins(),
                oldSettings.options().motd(), oldSettings.options().quits(),
                oldSettings.join().values().stream().map(join -> new Settings.JoinMessageConfig(join.permission(), join.message())).toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream().map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message())).toList(),
                new Settings.WelcomeRewards(oldSettings.welcomeRewards().enabled(),
                false, oldSettings.welcomeRewards().cash(),
                List.of(welcomeItem), oldSettings.welcomeRewards().commands(),
                oldSettings.welcomeRewards().messages()));
    }

    /**
     * Migrate the {@link SettingsV110ToV130} for version 1.1.0 to {@link Settings} 1.5.0.0.
     * @param oldSettings The {@link SettingsV110ToV130}
     * @return The updated {@link Settings}.
     */
    private @NotNull Settings updateSettings110To1500(@NotNull SettingsV110ToV130 oldSettings) {
        ItemStackConfig welcomeItem = new ItemStackConfig(
                ItemType.DIAMOND.getKey().getKey(),
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

        return new Settings("1.5.0.0", oldSettings.options().locale(), oldSettings.options().joins(),
                oldSettings.options().motd(), oldSettings.options().quits(),
                oldSettings.join().values().stream().map(join -> new Settings.JoinMessageConfig(join.permission(), join.message())).toList(),
                oldSettings.motd().contents(),
                oldSettings.quit().values().stream().map(quit -> new Settings.QuitMessageConfig(quit.permission(), quit.message())).toList(),
                new Settings.WelcomeRewards(true, false,
                50.0, List.of(welcomeItem), rewardCommands, rewardMessages));
    }

    private @NotNull Settings migrateLegacySettings(@NotNull LegacySettings legacySettings) {
        ItemStackConfig welcomeItem = new ItemStackConfig(
                ItemType.DIAMOND.getKey().getKey(),
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
        joinMessageConfigList.add(new Settings.JoinMessageConfig("skywelcome.join.default", migratePlaceholderAPIFormat(legacySettings.join().content())));
        quitMessageConfigList.add(new Settings.QuitMessageConfig("skywelcome.quit.default", migratePlaceholderAPIFormat(legacySettings.quit().content())));

        List<String> rewardCommands = new ArrayList<>();
        List<String> rewardMessages = new ArrayList<>();
        rewardCommands.add("give %player_name% emerald 1");
        rewardMessages.add("<aqua>Thanks for welcoming a new player. Enjoy this reward: $50</aqua>");

        List<String> motdList = new ArrayList<>();
        for(String msg : legacySettings.motd().contents()) {
            motdList.add(migratePlaceholderAPIFormat(msg));
        }

        return new Settings(
                "1.5.0.0",
                "en_US",
                true,
                true,
                true,
                joinMessageConfigList,
                motdList,
                quitMessageConfigList,
                new Settings.WelcomeRewards(true, false,50.0, List.of(welcomeItem), rewardCommands, rewardMessages));
    }

    /**
     * Converts %PLACEHOLDER_NAME% format to a <papi:PLACEHOLDER_NAME> format.
     * @param msg The String to find and convert PlaceholderAPI formats for.
     * @return A String with the new PlaceholderAPI format.
     */
    private String migratePlaceholderAPIFormat(String msg) {
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
}
