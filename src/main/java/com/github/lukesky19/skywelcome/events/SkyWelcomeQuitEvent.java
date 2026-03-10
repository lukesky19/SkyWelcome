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
import org.jspecify.annotations.NonNull;

/**
 * This class event is fired when a player quits and a custom quit message is sent.
 */
public class SkyWelcomeQuitEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Component componentQuitMessage;
    private final String plainQuitMessage;

    /**
     * Constructor
     * @param player The {@link Player}.
     * @param componentQuitMessage The quit message as a {@link Component}.
     * @param plainQuitMessage The plain quit message. This doesn't include any formatting tags.
     */
    public SkyWelcomeQuitEvent(@NonNull Player player, @NonNull Component componentQuitMessage, @NonNull String plainQuitMessage) {
        this.player = player;
        this.componentQuitMessage = componentQuitMessage;
        this.plainQuitMessage = plainQuitMessage;
    }

    /**
     * The {@link Player}.
     * @return A {@link Player}.
     */
    public @NonNull Player getPlayer() {
        return player;
    }

    /**
     * Get the quit message as a {@link Component}.
     * @return A {@link Component} containing the quit message.
     */
    public @NonNull Component getComponentQuitMessage() {
        return componentQuitMessage;
    }

    /**
     * Get the quit message as a {@link String} without any formatting.
     * @return A {@link String} containing the quit message.
     */
    public @NonNull String getPlainQuitMessage() {
        return plainQuitMessage;
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