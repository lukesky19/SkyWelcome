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
package com.github.lukesky19.skywelcome.config.settings.legacy;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This record contains the legacy plugin settings before it was versioned.
 * @param join The join message configuration.
 * @param motd The motd configuration.
 * @param quit The leave message configuration.
 */
@ConfigSerializable
public record LegacySettings(@NotNull Join join, @NotNull Motd motd, @NotNull Quit quit) {
    /**
     * Contains the join message.
     * @param content The join message.
     */
    @ConfigSerializable
    public record Join(@Nullable String content) { }

    /**
     * Contains the motd messages.
     * @param contents The {@link List} of {@link String}s for the motd messages.
     */
    @ConfigSerializable
    public record Motd(@NotNull List<String> contents) { }

    /**
     * Contains the leave message.
     * @param content The leave message.
     */
    @ConfigSerializable
    public record Quit(@NotNull String content) { }
}