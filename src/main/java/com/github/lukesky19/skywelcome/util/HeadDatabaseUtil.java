/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024  lukeskywlker19

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

import com.github.lukesky19.skywelcome.SkyWelcome;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.CheckForNull;

public class HeadDatabaseUtil implements Listener {
    final SkyWelcome skyWelcome;
    static HeadDatabaseAPI hdbApi;

    public HeadDatabaseUtil(SkyWelcome skyWelcome) {
        this.skyWelcome = skyWelcome;
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        hdbApi = new HeadDatabaseAPI();
        skyWelcome.postHeadDatabaseAPI();
    }

    @CheckForNull
    public static ItemStack getSkullItem(String id) {
        return hdbApi.getItemHead(id);
    }
}
