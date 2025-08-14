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
package com.github.lukesky19.skywelcome.listener;

import com.github.lukesky19.skylib.api.gui.interfaces.BaseGUI;
import com.github.lukesky19.skywelcome.manager.GUIManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * This class listens for when a plugin GUI is clicked or closed.
 */
public class InventoryListener implements Listener {
    private final @NotNull GUIManager guiManager;

    /**
     * Constructor
     * @param guiManager A {@link GUIManager} instance.
     */
    public InventoryListener(@NotNull GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * When an inventory is clicked, check if the Inventory is a GUI created by the plugin.
     * If so, call the handleClick method for the specific GUI.
     * @param inventoryClickEvent InventoryClickEvent
     */
    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        UUID uuid = inventoryClickEvent.getWhoClicked().getUniqueId();
        Inventory inventory = inventoryClickEvent.getClickedInventory();

        @NotNull Optional<@NotNull BaseGUI> optionalBaseGUI = guiManager.getOpenGUI(uuid);
        if (optionalBaseGUI.isEmpty()) return;
        BaseGUI baseGUI = optionalBaseGUI.get();

        baseGUI.handleGlobalClick(inventoryClickEvent);

        if (inventory instanceof PlayerInventory) {
            baseGUI.handleBottomClick(inventoryClickEvent);
        } else {
            baseGUI.handleTopClick(inventoryClickEvent);
        }
    }

    /**
     * When an inventory is dragged, check if the Inventory is a GUI created by the plugin.
     * If so, call the handleDrag method for the specific GUI.
     * @param inventoryDragEvent InventoryClickEvent
     */
    @EventHandler
    public void onDrag(InventoryDragEvent inventoryDragEvent) {
        UUID uuid = inventoryDragEvent.getWhoClicked().getUniqueId();
        Inventory inventory = inventoryDragEvent.getInventory();

        @NotNull Optional<@NotNull BaseGUI> optionalBaseGUI = guiManager.getOpenGUI(uuid);
        if (optionalBaseGUI.isEmpty()) return;
        BaseGUI baseGUI = optionalBaseGUI.get();

        baseGUI.handleGlobalDrag(inventoryDragEvent);

        if (inventory instanceof PlayerInventory) {
            baseGUI.handleBottomDrag(inventoryDragEvent);
        } else {
            baseGUI.handleTopDrag(inventoryDragEvent);
        }
    }

    /**
     * When an inventory is closed, check if the inventory is a GUI created by the plugin.
     * If so, call the handleClose method for the specific GUI.
     * @param inventoryCloseEvent InventoryCloseEvent
     */
    @EventHandler
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        UUID uuid = inventoryCloseEvent.getPlayer().getUniqueId();

        @NotNull Optional<@NotNull BaseGUI> optionalBaseGUI = guiManager.getOpenGUI(uuid);
        if (optionalBaseGUI.isEmpty()) return;
        BaseGUI baseGUI = optionalBaseGUI.get();

        baseGUI.handleClose(inventoryCloseEvent);
    }
}
