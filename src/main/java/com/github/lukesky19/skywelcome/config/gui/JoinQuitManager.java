package com.github.lukesky19.skywelcome.config.gui;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class JoinQuitManager {
    final SkyWelcome skyWelcome;
    final ConfigurationUtility configurationUtility;
    JoinConfig joinConfig;
    QuitConfig quitConfig;

    public JoinQuitManager(SkyWelcome skyWelcome, ConfigurationUtility configurationUtility) {
        this.skyWelcome = skyWelcome;
        this.configurationUtility = configurationUtility;
    }

    public JoinConfig getJoinGUIConfig() {
        return joinConfig;
    }

    public QuitConfig getQuitGUIConfig() {
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
            joinConfig = loader.load().get(JoinConfig.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The join GUI configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }

        loader = configurationUtility.getYamlConfigurationLoader(quitPath);
        try {
            quitConfig = loader.load().get(QuitConfig.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The quit GUI configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }
    }
}
