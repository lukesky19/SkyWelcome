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

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.FormatUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class LocaleManager {
    final SkyWelcome skyWelcome;
    final ConfigurationUtility configurationUtility;
    final SettingsManager settingsManager;
    final PlayerManager playerManager;
    Locale locale;

    public LocaleManager(
            SkyWelcome skyWelcome,
            ConfigurationUtility configurationUtility,
            SettingsManager settingsManager,
            PlayerManager playerManager) {
        this.skyWelcome = skyWelcome;
        this.configurationUtility = configurationUtility;
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public void reload() {
        locale = null;

        copyDefaultLocales();

        Path path = Path.of(
                skyWelcome.getDataFolder()
                        + File.separator
                        + "locale"
                        + File.separator
                        + settingsManager.getSettings().options().locale()
                        + ".yml");
        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);

        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException e) {
            skyWelcome.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>The locale configuration failed to load.</red>"));
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     */
    public void copyDefaultLocales() {
        Path path = Path.of(skyWelcome.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyWelcome.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    public void sendJoinMessage(Player joiningPlayer) {
        PlayerSettings playerSettings = playerManager.getPlayerSettings(joiningPlayer);

        if(playerSettings.joinMessage()) {
            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(FormatUtil.format(joiningPlayer, playerSettings.selectedJoinMessage()));
                }
            }
        }
    }

    public void sendLeaveMessage(Player leavingPlayer) {
        PlayerSettings playerSettings = playerManager.getPlayerSettings(leavingPlayer);

        if(playerSettings.leaveMessage()) {
            for(Player player : skyWelcome.getServer().getOnlinePlayers()) {
                if(player.isOnline()) {
                    player.sendMessage(FormatUtil.format(leavingPlayer, playerSettings.selectedLeaveMessage()));
                }
            }
        }
    }

    public void sendMotd(Player player) {
        if(playerManager.getPlayerSettings(player).motd()) {
            Settings.Motd motd = settingsManager.getSettings().motd();
            for(String message : motd.contents()) {
                player.sendMessage(FormatUtil.format(player, message));
            }
        }
    }
}
