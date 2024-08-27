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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

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
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyWelcome.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            settings = loader.load().get(Settings.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The settings configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }
    }
}
