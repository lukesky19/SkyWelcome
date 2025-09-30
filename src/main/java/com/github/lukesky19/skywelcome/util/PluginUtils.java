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

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains a method to check if the player is vanished.
 */
public class PluginUtils {
    /**
     * Default Constructor.
     * Use of the default constructor is not allowed. This class only contains static methods.
     * @deprecated Use of the default constructor is not allowed. This class only contains static methods.
     */
    @Deprecated
    public PluginUtils() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Is the provided {@link Player} vanished?
     * @param player The {@link Player} to check if they are vanished.
     * @return true if vanished, false if not.
     */
    @SuppressWarnings("deprecation")
    public static boolean isPlayerVanished(@NotNull Player player) {
        for(MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }
}
