package com.github.lukesky19.skywelcome.config.player;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class PlayerManager {
    final SkyWelcome skyWelcome;
    final ConfigurationUtility configurationUtility;
    final SettingsManager settingsManager;

    public PlayerManager(SkyWelcome skyWelcome, ConfigurationUtility configurationUtility, SettingsManager settingsManager) {
        this.skyWelcome = skyWelcome;
        this.configurationUtility = configurationUtility;
        this.settingsManager = settingsManager;
    }

    /**
     * Gets a player's settings.
     * @param player A bukkit player.
     * @return A player's settings.
     */
    public PlayerSettings getPlayerSettings(org.bukkit.entity.Player player) {
        PlayerSettings playerSettings;
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            playerSettings = loader.load().get(PlayerSettings.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>Unable to load " + player.getName() + "'s settings.</red>"));
            throw new RuntimeException(e);
        }

        return playerSettings;
    }

    /**
     * Creates a player's settings file if it doesn't exist.
     * @param player A bukkit player
     */
    public void createPlayerSettings(org.bukkit.entity.Player player) {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");
        Settings settings = settingsManager.getSettings();

        String joinMessage = settings.join().firstEntry().getValue().message();
        String leaveMessage = settings.quit().firstEntry().getValue().message();

        if(!path.toFile().exists()) {
            savePlayerSettings(player, new PlayerSettings(
                    true,
                    true,
                    true,
                    joinMessage,
                    leaveMessage));
        }
    }

    public void savePlayerSettings(org.bukkit.entity.Player player, PlayerSettings playerSettings) {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");
        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);

        CommentedConfigurationNode playerNode = loader.createNode();
        try {
            playerNode.set(playerSettings);
        } catch (SerializationException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>Unable to change " + player.getName() + "'s settings.</red>"));
            throw new RuntimeException(e);
        }

        try {
            loader.save(playerNode);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>Unable to save " + player.getName() + "'s settings.</red>"));
            throw new RuntimeException(e);
        }
    }

    public void toggleJoin(org.bukkit.entity.Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        savePlayerSettings(player, new PlayerSettings(
                !playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void toggleLeave(org.bukkit.entity.Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        savePlayerSettings(player, new PlayerSettings(
                playerSettings.joinMessage(),
                !playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void toggleMotd(org.bukkit.entity.Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        savePlayerSettings(player, new PlayerSettings(
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                !playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void changeSelectedJoinMessage(org.bukkit.entity.Player player, String message) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        savePlayerSettings(player, new PlayerSettings(
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                message,
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void changeSelectedLeaveMessage(org.bukkit.entity.Player player, String message) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        savePlayerSettings(player, new PlayerSettings(
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                message
        ));
    }
}
