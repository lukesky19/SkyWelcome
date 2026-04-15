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
package com.github.lukesky19.skywelcome.paper.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * This class event is fired when a player changes servers and a custom server change message is sent.
 */
public class SkyWelcomeServerChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Component componentServerChangeMessage;
    private final String plainServerChangeMessage;
    private final @NonNull String previousServerName;
    private final @NonNull String currentServerName;

    /**
     * Constructor
     * @param player The {@link Player}.
     * @param componentServerChangeMessage The server change message as a {@link Component}.
     * @param plainServerChangeMessage The plain server change message. This doesn't include any formatting tags.
     * @param previousServerName The previous server's name.
     * @param currentServerName The current server's name.
     */
    public SkyWelcomeServerChangeEvent(
            @NonNull Player player,
            @NonNull Component componentServerChangeMessage,
            @NonNull String plainServerChangeMessage,
            @NonNull String previousServerName,
            @NonNull String currentServerName) {
        this.player = player;
        this.componentServerChangeMessage = componentServerChangeMessage;
        this.plainServerChangeMessage = plainServerChangeMessage;
        this.previousServerName = previousServerName;
        this.currentServerName = currentServerName;
    }

    /**
     * The {@link Player}.
     * @return A {@link Player}.
     */
    public @NonNull Player getPlayer() {
        return player;
    }

    /**
     * Get the server change message as a {@link Component}.
     * @return A {@link Component} containing the server change message.
     */
    public @NonNull Component getComponentServerChangeMessage() {
        return componentServerChangeMessage;
    }

    /**
     * Get the join message as a {@link String} without any formatting.
     * @return A {@link String} containing the join message.
     */
    public @NonNull String getPlainServerChangeMessage() {
        return plainServerChangeMessage;
    }

    /**
     * Get the previous server's name.
     * @return A {@link String} containing the previous server's name.
     */
    public @NonNull String getPreviousServerName() {
        return previousServerName;
    }

    /**
     * Get the current server's name.
     * @return A {@link String} containing the current server's name.
     */
    public @NonNull String getCurrentServerName() {
        return currentServerName;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    public static @NonNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }
}