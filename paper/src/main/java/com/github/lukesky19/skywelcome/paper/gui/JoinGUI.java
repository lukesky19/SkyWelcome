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
package com.github.lukesky19.skywelcome.paper.gui;

import com.github.lukesky19.skylib.paper.api.gui.impl.UUIDGUIManager;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.gui.config.JoinGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.gui.menu.MessageSelectorGUI;
import com.github.lukesky19.skywelcome.paper.integration.HookManager;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * This class creates a gui to allow the selection of a custom join message.
 */
public class JoinGUI extends MessageSelectorGUI {
    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param guiManager A {@link UUIDGUIManager} instance.
     * @param player The {@link Player} this GUI is for.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param joinGUIConfigManager A {@link JoinGUIConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param playerData The {@link PlayerData}.
     * @param joinMessages The {@link List} of {@link MessageConfig}.
     */
    public JoinGUI(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull UUIDGUIManager guiManager,
            @NonNull Player player,
            @NonNull SettingsManager settingsManager,
            @NonNull JoinGUIConfigManager joinGUIConfigManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull MessageBroker messageBroker,
            @NonNull HookManager hookManager,
            @NonNull PlayerData playerData,
            @NonNull List<MessageConfig> joinMessages) {
        super(skyWelcome, guiManager, player, settingsManager, playerDataManager, messageBroker, hookManager, joinGUIConfigManager.getConfiguration(), playerData, joinMessages);
    }

    @Override
    public @NonNull String getPlayerMessage() {
        return playerData.getJoinMessage();
    }

    @Override
    protected void updatePlayerMessage(@NonNull String message) {
        playerData.setJoinMessage(message);

        if(settingsManager.isProxyEnabled()) {
            messageBroker.sendPlayerData(uuid, playerData);
        } else {
            playerDataManager.savePlayerData(uuid, playerData);
        }
    }
}