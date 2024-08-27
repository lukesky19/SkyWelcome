package com.github.lukesky19.skywelcome.util;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseUtil implements Listener {
    static HeadDatabaseAPI hdbApi;

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        hdbApi = new HeadDatabaseAPI();
    }

    public static ItemStack getSkullItem(String id) {
        return hdbApi.getItemHead(id);
    }
}
