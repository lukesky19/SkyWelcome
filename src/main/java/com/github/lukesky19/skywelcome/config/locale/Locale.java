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
package com.github.lukesky19.skywelcome.config.locale;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 * This record contains the plugin's locale messages.
 * @param configVersion The config version of the file.
 * @param prefix The plugin's prefix.
 * @param help The {@link List} of {@link String}s for the help message.
 * @param reload The message sent when the plugin is reloaded.
 * @param guiOpenError The message sent to the player when a gui fails to open due to a configuration error.
 * @param joinEnabled The message sent when the sending of the player's join message is enabled.
 * @param joinDisabled The message sent when the sending of the player's join message is disabled.
 * @param quitEnabled The message sent when the sending of the player's leave message is enabled.
 * @param quitDisabled The message sent when the sending of the player's leave message is disabled.
 * @param motdEnabled The message sent when the sending of the server's motd messages is enabled.
 * @param motdDisabled The message sent when the sending of the server's motd messages is disabled.
 * @param welcomeBroadcast The message sent when a player welcomes a new player.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        List<String> help,
        String reload,
        String guiOpenError,
        String joinEnabled,
        String joinDisabled,
        String quitEnabled,
        String quitDisabled,
        String motdEnabled,
        String motdDisabled,
        String welcomeBroadcast) {}
