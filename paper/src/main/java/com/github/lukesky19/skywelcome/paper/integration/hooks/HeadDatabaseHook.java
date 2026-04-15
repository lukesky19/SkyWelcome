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
package com.github.lukesky19.skywelcome.paper.integration.hooks;

import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages interfacing with HeadDatabase.
 */
public class HeadDatabaseHook implements Listener, Hook {
    private final @NotNull SkyWelcomePaper skyWelcome;
    private @Nullable HeadDatabaseAPI hdbApi;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     */
    public HeadDatabaseHook(@NotNull SkyWelcomePaper skyWelcome) {
        this.skyWelcome = skyWelcome;
    }

    /**
     * Register the listener for the loading of the head database API.
     */
    @Override
    public void initialize() {
        PluginManager pluginManager = skyWelcome.getServer().getPluginManager();
        boolean enabled = pluginManager.isPluginEnabled("HeadDatabase");

        if(enabled) {
            pluginManager.registerEvents(this, skyWelcome);
        }
    }

    /**
     * Is the head database api non-null/loaded?
     * @return true if hooked, false if not.
     */
    @Override
    public boolean isHooked() {
        return hdbApi != null;
    }

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