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
package com.github.lukesky19.skywelcome.paper.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import com.github.lukesky19.skywelcome.common.settings.MessageBrokerConfig;
import com.github.lukesky19.skywelcome.common.settings.MessageConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * This record contains the plugin's settings.
 * @param version The config version.
 * @param messageBrokerConfig The {@link MessageBrokerConfig}.
 * @param enableProxy Enable the proxy functionality.
 * @param locale The plugin's locale to use.
 * @param globalJoinToggle Should join messages be enabled globally?
 * @param globalQuitToggle Should leave messages be enabled globally?
 * @param globalMotdToggle Should the server's motd be enabled globally?
 * @param globalServerChangeToggle Should server change messages be enabled globally?
 * @param joinMessages The {@link List} of {@link MessageConfig}s for the available join messages.
 * @param motd The {@link List} of {@link String}s for the motd.
 * @param quitMessages The {@link List} of {@link MessageConfig}s for the available leave messages.
 * @param serverChangeMessages The {@link List} of {@link MessageConfig}s for the available server change messages.
 * @param welcomeRewards The {@link WelcomeRewards} config.
 */
@ConfigSerializable
public record Settings(
        int version,
        @NonNull MessageBrokerConfig messageBrokerConfig,
        boolean enableProxy,
        @Nullable String locale,
        boolean globalJoinToggle,
        boolean globalQuitToggle,
        boolean globalMotdToggle,
        boolean globalServerChangeToggle,
        @NonNull List<MessageConfig> joinMessages,
        @NonNull List<String> motd,
        @NonNull List<MessageConfig> quitMessages,
        @NonNull List<MessageConfig> serverChangeMessages,
        @NonNull WelcomeRewards welcomeRewards) {}