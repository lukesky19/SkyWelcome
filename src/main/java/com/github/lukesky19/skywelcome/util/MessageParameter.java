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
package com.github.lukesky19.skywelcome.util;

import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * Takes a String for storage in a database.
 */
public class MessageParameter implements Parameter<String> {
    private final @NotNull String message;
    /**
     * Stores a join or leave message as a {@link String}.
     * @param message The join or leave message as a {@link String}.
     */
    public MessageParameter(@NotNull String message) {
        this.message = message;
    }

    /**
     * Returns the {@link String} representing the join or leave message to use replace the parameter with.
     * @return The {@link String} representing the join or leave message.
     */
    @Override
    public @NotNull String getValue() {
        return message;
    }
}
