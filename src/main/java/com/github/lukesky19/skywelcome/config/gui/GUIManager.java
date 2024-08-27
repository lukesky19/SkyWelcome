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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

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

        loader = configurationUtility.getYamlConfigurationLoader(joinPath);
        try {
            joinConfig = loader.load().get(GUISettings.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The join GUI configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }

        loader = configurationUtility.getYamlConfigurationLoader(quitPath);
        try {
            quitConfig = loader.load().get(GUISettings.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The quit GUI configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }
    }
}
