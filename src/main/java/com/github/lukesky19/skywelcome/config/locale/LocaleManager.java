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
package com.github.lukesky19.skywelcome.config.locale;

import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;

public class LocaleManager {
    private final SkyWelcome skyWelcome;
    private final SettingsManager settingsManager;
    private final PlayerManager playerManager;
    private Locale locale;

    public LocaleManager(
            SkyWelcome skyWelcome,
            SettingsManager settingsManager,
            PlayerManager playerManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public void reload() {
        locale = null;

        ComponentLogger logger = skyWelcome.getComponentLogger();
        if (skyWelcome.isPluginDisabled()) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Locale settings cannot be loaded due to a previous plugin error.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        Path path = Path.of(
                skyWelcome.getDataFolder()
                        + File.separator
                        + "locale"
                        + File.separator
                        + settingsManager.getSettings().options().locale()
                        + ".yml");
        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException ignored) {
        }

        migrateLocale();
        validateLocale();
    }

    public void sendJoinMessage(Player joiningPlayer) {
        PlayerSettings playerSettings = playerManager.getPlayerSettings(joiningPlayer);

        if (playerSettings.joinMessage()) {
            for (Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if (player.isOnline()) {
                    player.sendMessage(FormatUtil.format(joiningPlayer, playerSettings.selectedJoinMessage()));
                }
            }
        }
    }

    public void sendQuitMessage(Player leavingPlayer) {
        PlayerSettings playerSettings = playerManager.getPlayerSettings(leavingPlayer);

        if (playerSettings.leaveMessage()) {
            for (Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if (player.isOnline()) {
                    player.sendMessage(FormatUtil.format(leavingPlayer, playerSettings.selectedLeaveMessage()));
                }
            }
        }
    }

    public void sendMotd(Player player) {
        if (playerManager.getPlayerSettings(player).motd()) {
            Settings.Motd motd = settingsManager.getSettings().motd();
            for (String message : motd.contents()) {
                player.sendMessage(FormatUtil.format(player, message));
            }
        }
    }

    private void validateLocale() {
        ComponentLogger logger = skyWelcome.getComponentLogger();
        String localeString = settingsManager.getSettings().options().locale();
        if (locale == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load <yellow>" + localeString + ".yml</yellow>.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.configVersion() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>config-version</yellow> setting in <yellow>" + localeString + ".yml</yellow> does exist.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>This means your config did not migrate properly or you modified the config-version setting.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.prefix() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>prefix</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.help() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>help</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        for (String msg : locale.help()) {
            if (msg == null) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>One of the Strings in the help setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
                skyWelcome.setPluginState(false);
                return;
            }
        }

        if (locale.playerOnly() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>player-only</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.reload() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>reload</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.noPermission() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>no-permission</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.unknownCommand() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>unknown-command</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.joinEnabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>join-enabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.joinDisabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>join-disabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.quitEnabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>quit-enabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.quitDisabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>quit-disabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.motdEnabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>motd-enabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }

        if (locale.motdDisabled() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>motd-disabled</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
            return;
        }


        if (locale.welcomeBroadcast() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The <yellow>welcome-broadcast</yellow> setting in <yellow>" + localeString + ".yml</yellow> is invalid.</red>"));
            skyWelcome.setPluginState(false);
        }
    }

    private void migrateLocale() {
        switch (locale.configVersion()) {
            case "1.2.0" -> {
                // Latest version, do nothing.
            }

            case "1.1.0", "1.0.0" -> {
                Locale newLocale = new Locale(
                        "1.2.0",
                        locale.prefix(),
                        locale.help(),
                        locale.playerOnly(),
                        locale.reload(),
                        locale.noPermission(),
                        locale.unknownCommand(),
                        locale.joinEnabled(),
                        locale.joinDisabled(),
                        locale.quitEnabled(),
                        locale.quitDisabled(),
                        locale.motdEnabled(),
                        locale.motdDisabled(),
                        "<aqua><white><welcome_player></white> welcomed <white><new_player></white> to the server!</aqua>");

                Path path = Path.of(
                        skyWelcome.getDataFolder()
                                + File.separator
                                + "locale"
                                + File.separator
                                + settingsManager.getSettings().options().locale()
                                + ".yml");
                YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);

                CommentedConfigurationNode node = loader.createNode();
                try {
                    node.set(newLocale);
                    loader.save(node);
                    locale = newLocale;
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + locale.configVersion());
        }
    }
}
