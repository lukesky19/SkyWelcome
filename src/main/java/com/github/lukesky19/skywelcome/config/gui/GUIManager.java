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
package com.github.lukesky19.skywelcome.config.gui;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.util.ActionType;
import com.github.lukesky19.skywelcome.util.HeadDatabaseUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class GUIManager {
    final SkyWelcome skyWelcome;
    final ConfigurationUtility configurationUtility;
    GUISettings joinConfig;
    GUISettings quitConfig;

    public GUIManager(SkyWelcome skyWelcome, ConfigurationUtility configurationUtility) {
        this.skyWelcome = skyWelcome;
        this.configurationUtility = configurationUtility;
    }

    public GUISettings getJoinGUIConfig() {
        return joinConfig;
    }

    public GUISettings getQuitGUIConfig() {
        return quitConfig;
    }

    public void reload() {
        joinConfig = null;
        quitConfig = null;
        Path joinPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "join.yml");
        Path quitPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "quit.yml");
        YamlConfigurationLoader loader;

        if(!joinPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "join.yml", false);
        }
        if(!quitPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "quit.yml", false);
        }

        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(skyWelcome.isPluginDisabled()) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>GUI settings cannot be loaded due to a previous plugin error.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        loader = configurationUtility.getYamlConfigurationLoader(joinPath);
        try {
            joinConfig = loader.load().get(GUISettings.class);
        } catch (ConfigurateException ignored) { }

        loader = configurationUtility.getYamlConfigurationLoader(quitPath);
        try {
            quitConfig = loader.load().get(GUISettings.class);
        } catch (ConfigurateException ignored) { }

        validateGUI(joinConfig, "join");
        validateGUI(quitConfig, "quit");
    }

    public void validateGUI(GUISettings guiSettings, String fileName) {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(guiSettings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load <yellow>" + fileName + ".yml</yellow>.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(guiSettings.configVersion() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>config-version</yellow> setting in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the config-version setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        GUISettings.Placeholders placeholderItemConfig = guiSettings.placeholders();
        if(placeholderItemConfig == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>placeholders</yellow> settings in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(placeholderItemConfig.selected() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>selected</yellow> item configuration under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        try {
            Material.valueOf(placeholderItemConfig.selected().material()); 
        } catch (IllegalArgumentException e) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>selected</yellow> item configuration's <yellow>material</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(placeholderItemConfig.selected().lore() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>selected</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(String msg : placeholderItemConfig.selected().lore()) {
            if(msg == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>One of the Strings in the <yellow>selected</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        // Available Placeholder Item
        if(placeholderItemConfig.available() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>available</yellow> item configuration under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        try {
            Material.valueOf(placeholderItemConfig.available().material());
        } catch (IllegalArgumentException e) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>available</yellow> item configuration's <yellow>material</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(placeholderItemConfig.available().lore() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>available</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(String msg : placeholderItemConfig.available().lore()) {
            if(msg == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>One of the Strings in the <yellow>available</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        // No Permission Placeholder Item
        if(placeholderItemConfig.noPermission() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>no-permission</yellow> item configuration under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        try {
            Material.valueOf(placeholderItemConfig.noPermission().material());
        } catch (IllegalArgumentException e) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>no-permission</yellow> item configuration's <yellow>material</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(placeholderItemConfig.noPermission().lore() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>no-permission</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(String msg : placeholderItemConfig.noPermission().lore()) {
            if(msg == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>One of the Strings in the <yellow>no-permission</yellow> item configuration's <yellow>lore</yellow> under <yellow>placeholders</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        GUISettings.Gui gui = guiSettings.gui();
        if(gui == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>gui</yellow> settings in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(gui.size() == null || gui.size() % 9 != 0) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>size</yellow> setting under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(gui.name() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>name</yellow> setting under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        GUISettings.PagedSettings pagedSettings = gui.pagedSettings();
        if(pagedSettings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>paged-settings</yellow> settings under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(pagedSettings.xOffset() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>x-offset</yellow> setting under <yellow>paged-settings</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(pagedSettings.yOffset() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>y-offset</yellow> setting under <yellow>paged-settings</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(pagedSettings.length() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>length</yellow> setting under <yellow>paged-settings</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(pagedSettings.height() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>height</yellow> setting under <yellow>paged-settings</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for(Map.Entry<Integer, LinkedHashMap<Integer, GUISettings.Item>> pagesEntry : gui.background().entrySet()) {
            int pageNum = pagesEntry.getKey();
            for(Map.Entry<Integer, GUISettings.Item> itemEntry : pagesEntry.getValue().entrySet()) {
                int itemNum = itemEntry.getKey();
                GUISettings.Item item = itemEntry.getValue();

                if(item.type() == null) {
                    logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>type</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
                    skyWelcome.setPluginState(false);
                    return;
                }
                
                try {
                    ActionType.valueOf(item.type());
                } catch (IllegalArgumentException e) {
                    logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>type</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                    skyWelcome.setPluginState(false);
                    return;
                }

                if(item.material() == null && item.hdbId() == null) {
                    logger.error(MiniMessage.miniMessage().deserialize("<red>There is no <yellow>material</yellow> or <yellow>hdb-id</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow>.</red>"));
                    skyWelcome.setPluginState(false);
                    return;
                }

                if(item.material() != null && item.hdbId() != null) {
                    logger.warn(MiniMessage.miniMessage().deserialize("<red>Both <yellow>material</yellow> and <yellow>hdb-id</yellow> settings for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> are configured.</red>"));
                }

                if(item.hdbId() != null) {
                    if(HeadDatabaseUtil.getSkullItem(item.hdbId()) == null) {
                        logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>hdb-id</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                        skyWelcome.setPluginState(false);
                        return;
                    }
                }

                if(item.material() != null) {
                    if(Material.getMaterial(item.material()) == null) {
                        logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>material</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                        skyWelcome.setPluginState(false);
                        return;
                    }
                }

                if(item.name() == null) {
                    logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>name</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
                    skyWelcome.setPluginState(false);
                    return;
                }

                if(item.lore() == null) {
                    logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>lore</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> does not exist.</red>"));
                    skyWelcome.setPluginState(false);
                    return;
                }
                
                for(String msg : item.lore()) {
                    if(msg == null) {
                        logger.error(MiniMessage.miniMessage().deserialize("<red>One of the Strings in the <yellow>lore</yellow> setting for page <yellow>" + pageNum + "</yellow> and item <yellow>" + itemNum + "</yellow> under <yellow>background</yellow> under <yellow>gui</yellow> in <yellow>" + fileName + ".yml</yellow> is invalid.</red>"));
                        skyWelcome.setPluginState(false);
                        return;
                    }
                }
            }
        }
    }
}
