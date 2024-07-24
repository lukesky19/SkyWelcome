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
