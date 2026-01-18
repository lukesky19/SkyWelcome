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
package com.github.lukesky19.skywelcome.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This class event is fired when a player joins and a custom join message is sent.
 */
public class SkyWelcomeJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Component componentJoinMessage;
    private final String plainJoinMessage;

    /**
     * Constructor
     * @param player The {@link Player}.
     * @param componentJoinMessage The join message as a {@link Component}.
     * @param plainJoinMessage The plain join message. This doesn't include any formatting tags.
     */
    public SkyWelcomeJoinEvent(@NotNull Player player, @NotNull Component componentJoinMessage, @NotNull String plainJoinMessage) {
        this.player = player;
        this.componentJoinMessage = componentJoinMessage;
        this.plainJoinMessage = plainJoinMessage;
    }

    /**
     * The {@link Player}.
     * @return A {@link Player}.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Get the join message as a {@link Component}.
     * @return A {@link Component} containing the join message.
     */
    public @NotNull Component getComponentJoinMessage() {
        return componentJoinMessage;
    }

    /**
     * Get the join message as a {@link String} without any formatting.
     * @return A {@link String} containing the join message.
     */
    public @NotNull String getPlainJoinMessage() {
        return plainJoinMessage;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}