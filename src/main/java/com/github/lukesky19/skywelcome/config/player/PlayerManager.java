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
package com.github.lukesky19.skywelcome.config.player;

import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerManager {
    private final SkyWelcome skyWelcome;
    private final SettingsManager settingsManager;

    public PlayerManager(SkyWelcome skyWelcome, SettingsManager settingsManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
    }

    /**
     * Gets a player's settings.
     * @param player A bukkit player.
     * @return A player's settings.
     */
    public PlayerSettings getPlayerSettings(Player player) {
        PlayerSettings playerSettings = null;
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            playerSettings = loader.load().get(PlayerSettings.class);
        } catch (ConfigurateException ignored) { }

        playerSettings = migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);
        return playerSettings;
    }

    /**
     * Creates a player's settings file if it doesn't exist.
     * @param player A bukkit player
     */
    public void createPlayerSettings(Player player) {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(skyWelcome.isPluginDisabled()) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Player settings cannot be created due to a previous plugin error.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");
        Settings settings = settingsManager.getSettings();

        Settings.Join defaultJoin = settings.join().firstEntry().getValue();
        Settings.Quit defaultQuit = settings.quit().firstEntry().getValue();
        String joinMessage = "";
        String quitMessage = "";

        if(player.hasPermission(defaultJoin.permission())) {
            joinMessage = defaultJoin.message();
        }

        if(player.hasPermission(defaultQuit.permission())) {
            quitMessage = defaultQuit.message();
        }

        if(!path.toFile().exists()) {
            savePlayerSettings(player, new PlayerSettings(
                    "1.1.0",
                    true,
                    true,
                    true,
                    joinMessage,
                    quitMessage));
        }
    }

    public void savePlayerSettings(Player player, PlayerSettings playerSettings) {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".yml");
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

        CommentedConfigurationNode playerNode = loader.createNode();
        try {
            playerNode.set(playerSettings);
            loader.save(playerNode);
        } catch(ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>Unable to save " + player.getName() + "'s settings.</red>"));
            throw new RuntimeException(e);
        }
    }

    public void toggleJoin(Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);

        migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);

        savePlayerSettings(player, new PlayerSettings(
                playerSettings.configVersion(),
                !playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void toggleQuit(Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);

        migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);

        savePlayerSettings(player, new PlayerSettings(
                playerSettings.configVersion(),
                playerSettings.joinMessage(),
                !playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void toggleMotd(Player player) {
        PlayerSettings playerSettings = getPlayerSettings(player);

        migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);

        savePlayerSettings(player, new PlayerSettings(
                playerSettings.configVersion(),
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                !playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void changeSelectedJoinMessage(Player player, String message) {
        PlayerSettings playerSettings = getPlayerSettings(player);

        migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);

        savePlayerSettings(player, new PlayerSettings(
                playerSettings.configVersion(),
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                message,
                playerSettings.selectedLeaveMessage()
        ));
    }

    public void changeSelectedQuitMessage(Player player, String message) {
        PlayerSettings playerSettings = getPlayerSettings(player);

        migratePlayerSettings(player, playerSettings);
        validatePlayerSettings(player.getUniqueId(), playerSettings);

        savePlayerSettings(player, new PlayerSettings(
                playerSettings.configVersion(),
                playerSettings.joinMessage(),
                playerSettings.leaveMessage(),
                playerSettings.motd(),
                playerSettings.selectedJoinMessage(),
                message
        ));
    }

    private void validatePlayerSettings(UUID uuid, PlayerSettings playerSettings) {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(skyWelcome.isPluginDisabled()) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Player settings cannot be validated due to a previous plugin error.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        if(playerSettings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load <yellow>" + uuid + ".yml</yellow>.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.configVersion() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>config-version</yellow> setting in <yellow>" + uuid + ".yml</yellow> does not exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the <yellow>config-version</yellow> setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.joinMessage() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>join-message</yellow> setting in <yellow>" + uuid + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This should not happen unless you modified a player's file manually.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.leaveMessage() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>leave-message</yellow> setting in <yellow>" + uuid + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This should not happen unless you modified a player's file manually.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.motd() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>motd</yellow> setting in <yellow>" + uuid + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This should not happen unless you modified a player's file manually.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.selectedJoinMessage() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>selected-join-message</yellow> setting in <yellow>" + uuid + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the <yellow>selected-join-message</yellow> setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if(playerSettings.selectedLeaveMessage() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>selected-leave-message</yellow> setting in <yellow>" + uuid + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the <yellow>selected-leave-message</yellow> setting.</red>"));
            skyWelcome.setPluginState(false);
        }
    }

    private PlayerSettings migratePlayerSettings(Player player, PlayerSettings playerSettings) {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        if(playerSettings == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load <yellow>" + player.getUniqueId() + ".yml</yellow>.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Invalid legacy configuration cannot be migrated. </red>"));
            skyWelcome.setPluginState(false);
            return null;
        }

        if(playerSettings.configVersion() == null) {
            if (skyWelcome.isPluginDisabled()) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>Player settings cannot be migrated due to a previous plugin error.</red>"));
                logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
                return null;
            }

            PlayerSettings newPlayerSettings = new PlayerSettings(
                    "1.1.0",
                    playerSettings.joinMessage(),
                    playerSettings.leaveMessage(),
                    playerSettings.motd(),
                    settingsManager.getSettings().join().firstEntry().getValue().message(),
                    settingsManager.getSettings().quit().firstEntry().getValue().message());

            savePlayerSettings(player, newPlayerSettings);
            return newPlayerSettings;
        }

        return playerSettings;
    }
}
