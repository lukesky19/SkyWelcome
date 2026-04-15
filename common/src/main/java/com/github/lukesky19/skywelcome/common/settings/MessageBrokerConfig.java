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
package com.github.lukesky19.skywelcome.common.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This record holds the configuration to set up the RabbitMQ message broker.
 * @param address The address to use.
 * @param port The port to use.
 * @param username The optional username.
 * @param password The optional password.
 */
@ConfigSerializable
public record MessageBrokerConfig(
        @Nullable String address,
        @Nullable Integer port,
        @Nullable String username,
        @Nullable String password) {
    /**
     * Get the configured address or {@code localhost} if not set.
     * @return The configured address or {@code localhost} if not set.
     */
    public @NonNull String getAddress() {
        if(address == null || address.isEmpty()) {
            return "localhost";
        } else {
            return address;
        }
    }
}