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
package com.github.lukesky19.skywelcome.manager;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * This class manages access to the HeadDatabaseAPI.
 */
public class HeadDatabaseManager implements Listener {
    private @Nullable HeadDatabaseAPI hdbApi;

    /**
     * Constructor
     */
    public HeadDatabaseManager() {}

    /**
     * Listen to when the head database api is loaded.
     * @param databaseLoadEvent A {@link DatabaseLoadEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDatabaseLoad(DatabaseLoadEvent databaseLoadEvent) {
        hdbApi = new HeadDatabaseAPI();
    }

    /**
     * Get the {@link ItemStack} for the head database id provided.
     * @param id The id for the skull in the head database.
     * @return The {@link ItemStack} for the skull id provided or null if the head database api is not loaded or the id is null.
     */
    public @Nullable ItemStack getSkullItem(@Nullable String id) {
        if(hdbApi == null) return null;
        if(id == null) return null;

        return hdbApi.getItemHead(id);
    }
}
