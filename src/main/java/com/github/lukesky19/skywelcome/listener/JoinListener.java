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
package com.github.lukesky19.skywelcome.listener;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final SkyWelcome skyWelcome;
    private final PlayerManager playerManager;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;
    private Settings settings;

    public JoinListener(
            SkyWelcome skyWelcome,
            PlayerManager playerManager,
            SettingsManager settingsManager,
            LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.playerManager = playerManager;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    public void reload() {
        settings = settingsManager.getSettings();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(skyWelcome.isPluginDisabled()) {
            skyWelcome.getComponentLogger().warn(FormatUtil.format("<red>Unable to send a join message for <yellow>" + MiniMessage.miniMessage().serialize(event.getPlayer().displayName()) + "</yellow> since the plugin is soft-disabled due to a configuration error.</red>"));
            return;
        }

        playerManager.createPlayerSettings(player);

        if(settings.options().motd()) {
            if(playerManager.getPlayerSettings(player).motd()) {
                localeManager.sendMotd(player);
            }
        }

        if(settings.options().joins()) {
            if(playerManager.getPlayerSettings(player).joinMessage()) {
                localeManager.sendJoinMessage(player);
            }
        }
    }
}
