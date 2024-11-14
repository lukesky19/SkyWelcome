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
package com.github.lukesky19.skywelcome.gui;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skywelcome.config.gui.GUIManager;
import com.github.lukesky19.skywelcome.config.gui.GUISettings;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.enums.ActionType;
import com.github.lukesky19.skywelcome.util.HeadDatabaseUtil;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class JoinGUI {
    final SettingsManager settingsManager;
    final PlayerManager playerManager;
    final GUIManager GUIManager;

    public JoinGUI(
            SettingsManager settingsManager,
            PlayerManager playerManager,
            GUIManager GUIManager) {
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
        this.GUIManager = GUIManager;
    }

    ChestGui joinGUI;
    StaticPane background;
    PaginatedPane pages;

    public void createGUI(Player player) {
        GUISettings GUISettings = GUIManager.getJoinGUIConfig();

        joinGUI = new ChestGui(GUISettings.gui().size() / 9, ComponentHolder.of(FormatUtil.format(player, GUISettings.gui().name())));

        // TODO Background Pane doesn't match GUI size
        background = new StaticPane(0, 0, 9, 6);
        pages = new PaginatedPane(
                GUISettings.gui().pagedSettings().xOffset(),
                GUISettings.gui().pagedSettings().yOffset(),
                GUISettings.gui().pagedSettings().length(),
                GUISettings.gui().pagedSettings().height());

        for(Map.Entry<Integer, LinkedHashMap<Integer, GUISettings.Item>> rowsEntry : GUISettings.gui().background().entrySet()) {
            int rowNum = rowsEntry.getKey();

            for(Map.Entry<Integer, GUISettings.Item> itemEntry : rowsEntry.getValue().entrySet()) {
                Integer slotNum = itemEntry.getKey();
                GUISettings.Item item = itemEntry.getValue();
                switch(ActionType.valueOf(item.type())) {
                    case FILLER -> {
                        ItemStack itemStack;
                        if(item.hdbId() != null) {
                            itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
                            if(itemStack == null) return;
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.displayName(FormatUtil.format(player, item.name()));
                            List<Component> loreList = new ArrayList<>();
                            for(String loreStr : item.lore()) {
                                loreList.add(FormatUtil.format(player, loreStr));
                            }
                            itemMeta.lore(loreList);
                            itemStack.setItemMeta(itemMeta);
                            GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                            background.addItem(guiItem, slotNum, rowNum);
                        }

                        if(Material.getMaterial(item.material()) != null) {
                            itemStack = new ItemStack(Material.valueOf(item.material()));
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.displayName(FormatUtil.format(player, item.name()));
                            List<Component> loreList = new ArrayList<>();
                            for(String loreStr : item.lore()) {
                                loreList.add(FormatUtil.format(player, loreStr));
                            }
                            itemMeta.lore(loreList);
                            itemStack.setItemMeta(itemMeta);
                            GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                            background.addItem(guiItem, slotNum, rowNum);
                        }
                    }

                    case PREV_PAGE -> {
                         ItemStack itemStack;
                         if(item.hdbId() != null) {
                             itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
                             if(itemStack == null) return;
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for(String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> {
                                 event.setCancelled(true);
                                 if (pages.getPage() > 0) {
                                     pages.setPage(pages.getPage() - 1);

                                     joinGUI.update();
                                 }
                             });
                             background.addItem(guiItem, slotNum, rowNum);
                         }

                         if(Material.getMaterial(item.material()) != null) {
                             itemStack = new ItemStack(Material.valueOf(item.material()));
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for(String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> {
                                 event.setCancelled(true);
                                 if (pages.getPage() > 0) {
                                     pages.setPage(pages.getPage() - 1);

                                     joinGUI.update();
                                 }
                             });
                             background.addItem(guiItem, slotNum, rowNum);
                         }
                    }

                    case NEXT_PAGE -> {
                         ItemStack itemStack;
                         if(item.hdbId() != null) {
                             itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
                             if(itemStack == null) return;
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for(String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> {
                                 event.setCancelled(true);
                                 if (pages.getPage() < pages.getPages() - 1) {
                                     pages.setPage(pages.getPage() + 1);

                                     joinGUI.update();
                                 }
                             });
                             background.addItem(guiItem, slotNum, rowNum);
                         }

                         if(Material.getMaterial(item.material()) != null) {
                             itemStack = new ItemStack(Material.valueOf(item.material()));
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for(String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> {
                                 event.setCancelled(true);
                                 if (pages.getPage() < pages.getPages() - 1) {
                                     pages.setPage(pages.getPage() + 1);

                                     joinGUI.update();
                                 }
                             });
                             background.addItem(guiItem, slotNum, rowNum);
                         }
                    }

                    case RETURN -> {
                         ItemStack itemStack;
                         if(item.hdbId() != null) {
                             itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
                             if(itemStack == null) return;
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for (String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> event.getWhoClicked().closeInventory());
                             background.addItem(guiItem, slotNum, rowNum);
                         }

                         if(Material.getMaterial(item.material()) != null) {
                             itemStack = new ItemStack(Material.valueOf(item.material()));
                             ItemMeta itemMeta = itemStack.getItemMeta();
                             itemMeta.displayName(FormatUtil.format(player, item.name()));
                             List<Component> loreList = new ArrayList<>();
                             for (String loreStr : item.lore()) {
                                 loreList.add(FormatUtil.format(player, loreStr));
                             }
                             itemMeta.lore(loreList);
                             itemStack.setItemMeta(itemMeta);
                             GuiItem guiItem = new GuiItem(itemStack, event -> event.getWhoClicked().closeInventory());

                             background.addItem(guiItem, slotNum, rowNum);
                         }
                    }

                }
            }
        }

        populatePages(player);

        joinGUI.addPane(background);
        joinGUI.addPane(pages);
    }

    public void updateGUI(Player player) {
        populatePages(player);
        joinGUI.getPanes().clear();
        joinGUI.addPane(background);
        joinGUI.addPane(pages);
        joinGUI.update();
    }

    public void openGUI(Player player) {
        joinGUI.show(player);
    }

    private void populatePages(Player player) {
        pages.clear();
        pages.populateWithGuiItems(getGuiItemsList(player));
    }

    private List<GuiItem> getGuiItemsList(Player player) {
        GUISettings GUISettings = GUIManager.getJoinGUIConfig();

        Settings settings = settingsManager.getSettings();
        PlayerSettings playerSettings = playerManager.getPlayerSettings(player);

        List<GuiItem> guiItems = new ArrayList<>();
        ItemStack itemStack;

        for(Map.Entry<String, Settings.Join> entry : settings.join().entrySet()) {
            Settings.Join join = entry.getValue();
            if(player.hasPermission(join.permission())) {
                if(Objects.equals(playerSettings.selectedJoinMessage(), join.message())) {
                    itemStack = new ItemStack(Material.valueOf(GUISettings.placeholders().selected().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, join.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : GUISettings.placeholders().selected().lore()) {
                        loreList.add(FormatUtil.format(player, loreStr));
                    }
                    itemMeta.lore(loreList);
                    itemStack.setItemMeta(itemMeta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                    guiItems.add(guiItem);
                } else {
                    itemStack = new ItemStack(Material.valueOf(GUISettings.placeholders().available().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, join.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : GUISettings.placeholders().available().lore()) {
                        loreList.add(FormatUtil.format(player, loreStr));
                    }
                    itemMeta.lore(loreList);
                    itemStack.setItemMeta(itemMeta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> {
                        event.setCancelled(true);
                        playerManager.changeSelectedJoinMessage(player, join.message());
                        updateGUI(player);
                    });
                    guiItems.add(guiItem);
                }
            } else {
                itemStack = new ItemStack(Material.valueOf(GUISettings.placeholders().noPermission().material()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(FormatUtil.format(player, join.message()));
                List<Component> loreList = new ArrayList<>();
                for(String loreStr : GUISettings.placeholders().noPermission().lore()) {
                    loreList.add(FormatUtil.format(player, loreStr));
                }
                itemMeta.lore(loreList);
                itemStack.setItemMeta(itemMeta);
                GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                guiItems.add(guiItem);
            }
        }

        return guiItems;
    }
}
