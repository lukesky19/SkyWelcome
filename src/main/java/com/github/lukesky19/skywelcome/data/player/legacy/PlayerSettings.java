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
package com.github.lukesky19.skywelcome.data.player.legacy;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.Nullable;

/**
 * This contains the legacy player settings from when player data was loaded from files rather than a database.
 * @param configVersion The config version of the file.
 * @param joinMessage Should the player's join message be sent?
 * @param leaveMessage Should the player's leave message be sent?
 * @param motd Should the MOTD be sent to the player?
 * @param selectedJoinMessage The player's join message.
 * @param selectedLeaveMessage The player's leave message.
 */
@ConfigSerializable
public record PlayerSettings(
        @Nullable String configVersion,
        @Nullable Boolean joinMessage,
        @Nullable Boolean leaveMessage,
        @Nullable Boolean motd,
        @Nullable String selectedJoinMessage,
        @Nullable String selectedLeaveMessage) {}
