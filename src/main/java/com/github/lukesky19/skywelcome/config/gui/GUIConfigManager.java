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
package com.github.lukesky19.skywelcome.config.gui;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the gui configuration.
 */
public class GUIConfigManager {
    private final @NotNull SkyWelcome skyWelcome;
    private @Nullable GUIConfig joinConfig;
    private @Nullable GUIConfig quitConfig;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     */
    public GUIConfigManager(@NotNull SkyWelcome skyWelcome) {
        this.skyWelcome = skyWelcome;
    }

    /**
     * Get the {@link GUIConfig} for the join gui config.
     * @return The {@link GUIConfig} or null.
     */
    public @Nullable GUIConfig getJoinGUIConfig() {
        return joinConfig;
    }

    /**
     * Get the {@link GUIConfig} for the quit gui config.
     * @return The {@link GUIConfig} or null.
     */
    public @Nullable GUIConfig getQuitGUIConfig() {
        return quitConfig;
    }

    /**
     * Reload the plugin's gui configs.
     */
    public void reload() {
        ComponentLogger logger = skyWelcome.getComponentLogger();

        joinConfig = null;
        quitConfig = null;
        Path joinPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "join.yml");
        Path quitPath = Path.of(skyWelcome.getDataFolder() + File.separator + "guis" + File.separator + "quit.yml");

        if(!joinPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "join.yml", false);
        }
        if(!quitPath.toFile().exists()) {
            skyWelcome.saveResource("guis" + File.separator + "quit.yml", false);
        }

        YamlConfigurationLoader joinLoader = ConfigurationUtility.getYamlConfigurationLoader(joinPath);
        try {
            joinConfig = joinLoader.load().get(GUIConfig.class);

            validateGUIConfig(joinConfig, "join");
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.serialize("Failed to load the join gui config. Error: " + e.getMessage()));
        }

        YamlConfigurationLoader quitLoader = ConfigurationUtility.getYamlConfigurationLoader(quitPath);
        try {
            quitConfig = quitLoader.load().get(GUIConfig.class);

            validateGUIConfig(quitConfig, "quit");
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.serialize("Failed to load the quit gui config. Error: " + e.getMessage()));
        }
    }

    /**
     * Validate the {@link GUIConfig} provided.
     * @param guiConfig The {@link GUIConfig} to validate.
     * @param fileName The file name for the gui config being validated.
     */
    private void validateGUIConfig(@Nullable GUIConfig guiConfig, @NotNull String fileName) {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(guiConfig == null) return;

        if(guiConfig.configVersion() == null) {
            logger.error(AdventureUtil.serialize("The gui config version for " + fileName + ".yml is invalid."));
            return;
        }

        if(!guiConfig.configVersion().equals("1.5.0.0")) {
            logger.error(AdventureUtil.serialize("The config version for " + fileName + ".yml is outdated and the config needs to be regenerated or updated."));
        }
    }
}
