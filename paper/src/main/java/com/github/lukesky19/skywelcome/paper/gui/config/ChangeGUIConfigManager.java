package com.github.lukesky19.skywelcome.paper.gui.config;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skylib.paper.api.gui.GUIType;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skywelcome.paper.enums.ButtonType;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the GUI configuration for the selecting a server-change message.
 */
public class ChangeGUIConfigManager extends SimpleConfigManager<GUIConfig> {
    /**
     * Constructor
     * @param plugin An {@link ISkyPlugin} instance.
     */
    public ChangeGUIConfigManager(@NonNull ISkyPlugin plugin) {
        super(plugin, Path.of(plugin.getDirectoryFile() + File.separator + "guis" + File.separator + "change.yml"), GUIConfig.class);
    }

    @Override
    public void saveDefaultConfiguration() {
        saveConfiguration(new GUIConfig(
                3,
                new GUIConfig.GuiData(
                        GUIType.CHEST_54,
                        "Choose a Server Change Message",
                        28,
                        new GUIConfig.PlaceholderButtons(
                                new ItemStackConfig(
                                        ItemType.GREEN_WOOL,
                                        1,
                                        null,
                                        null,
                                        List.of("<green>This is your current server change message.</green>"),
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
                                        new ItemStackConfig.OptionsConfig(null, null, null, null, null)),
                                new ItemStackConfig(
                                        ItemType.YELLOW_WOOL,
                                        1,
                                        null,
                                        null,
                                        List.of("<yellow>Click to change to this server change message.</yellow>"),
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
                                        new ItemStackConfig.OptionsConfig(null, null, null, null, null)),
                                new ItemStackConfig(
                                        ItemType.RED_WOOL,
                                        1,
                                        null,
                                        null,
                                        List.of("<red>You do not have permission to use this server change message.</red>"),
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
                        List.of(
                                new GUIConfig.ButtonConfig(
                                        ButtonType.FILLER,
                                        null,
                                        null,
                                        new ItemStackConfig(
                                                ItemType.GRAY_STAINED_GLASS_PANE,
                                                null,
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
                                new GUIConfig.ButtonConfig(
                                        ButtonType.PREV_PAGE,
                                        47,
                                        "516",
                                        new ItemStackConfig(
                                                ItemType.ARROW,
                                                1,
                                                null,
                                                "<white>Click to navigate to the previous page.</white>",
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
                                new GUIConfig.ButtonConfig(
                                        ButtonType.RETURN,
                                        49,
                                        null,
                                        new ItemStackConfig(
                                                ItemType.BARRIER,
                                                1,
                                                null,
                                                "<white>Click to exit this menu.</white>",
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
                                new GUIConfig.ButtonConfig(
                                        ButtonType.NEXT_PAGE,
                                        51,
                                        "513",
                                        new ItemStackConfig(
                                                ItemType.ARROW,
                                                1,
                                                null,
                                                "<white>Click to navigate to the next page.</white>",
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
                                                new ItemStackConfig.OptionsConfig(null, null, null, null, null)))),
                        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43)
                )
        ));
    }

    @Override
    public void loadConfiguration() {
        configuration = null;
        if(configurationPath == null) return;

        saveDefaultConfiguration();

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            ConfigurationNode root = loader.load();
            migrateVersion(root);
            loader.save(root);

            GUIConfig joinConfig = root.get(GUIConfig.class);
            if(joinConfig == null) {
                logger.warn(AdventureUtility.deserialize("Failed to load change.yml."));
                return;
            }

            GUIConfig migratedConfiguration = migrateConfiguration(joinConfig);

            // Check if the configuration is invalid
            if(!validateConfiguration(migratedConfiguration)) {
                logger.warn(AdventureUtility.deserialize("Server Change GUI configuration validation failed."));
                return;
            }

            this.configuration = migratedConfiguration;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.deserialize("Failed to load the server change GUI configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public GUIConfig migrateConfiguration(@NonNull GUIConfig guiConfig) {
        switch(guiConfig.version()) {
            case 3 -> {
                // Latest version
                return guiConfig;
            }

            case 2 -> {
                GUIConfig config = new GUIConfig(
                        3,
                        guiConfig.gui());

                saveConfiguration(config);

                return config;
            }

            case 1 -> {
                logger.warn(AdventureUtility.deserialize("Version 1 of the gui configuration cannot be automatically migrated."));
                return null;
            }

            default -> {
                logger.warn(AdventureUtility.deserialize("Unable to migrate the gui configuration due to an unrecognized version: " + guiConfig.version() + "."));
                return null;
            }
        }
    }

    @Override
    public boolean validateConfiguration(@Nullable GUIConfig guiConfig) {
        return guiConfig != null;
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

                default -> logger.warn(AdventureUtility.deserialize("Failed to convert String-based version to numeric version due to an unrecognized version."));
            }
        } catch (SerializationException e) {
            logger.warn(AdventureUtility.deserialize("Failed to convert String-based version to numeric version. Error: " + e.getMessage()));
        }
    }
}