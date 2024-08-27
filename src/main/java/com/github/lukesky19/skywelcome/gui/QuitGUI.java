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

import com.github.lukesky19.skywelcome.config.gui.GUIManager;
import com.github.lukesky19.skywelcome.config.gui.GUISettings;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.FormatUtil;
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

public class QuitGUI {
    final SettingsManager settingsManager;
    final PlayerManager playerManager;
    final GUIManager GUIManager;

    public QuitGUI(
            SettingsManager settingsManager,
            PlayerManager playerManager,
            GUIManager GUIManager) {
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
        this.GUIManager = GUIManager;
    }

    ChestGui quitGUI;
    StaticPane background;
    PaginatedPane pages;

    public void createGUI(Player player) {
        GUISettings quitConfig = GUIManager.getQuitGUIConfig();

        quitGUI = new ChestGui(quitConfig.gui().size() / 9, ComponentHolder.of(FormatUtil.format(player, quitConfig.gui().name())));

        background = new StaticPane(0, 0, 9, 6);
        pages = new PaginatedPane(
                quitConfig.gui().pagedSettings().xOffset(),
                quitConfig.gui().pagedSettings().yOffset(),
                quitConfig.gui().pagedSettings().length(),
                quitConfig.gui().pagedSettings().height());

        for(Map.Entry<Integer, LinkedHashMap<Integer, GUISettings.Item>> rowsEntry : quitConfig.gui().background().entrySet()) {
            int rowNum = rowsEntry.getKey();

            for(Map.Entry<Integer, GUISettings.Item> itemEntry : rowsEntry.getValue().entrySet()) {
                Integer slotNum = itemEntry.getKey();
                GUISettings.Item item = itemEntry.getValue();
                switch(item.type()) {

                    case "FILLER" -> {
                        ItemStack itemStack;
                        if(item.hdbId() != null) {
                            itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
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

                    case "PREV_PAGE" -> {
                        ItemStack itemStack;
                        if(item.hdbId() != null) {
                            itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
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

                                    quitGUI.update();
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

                                    quitGUI.update();
                                }
                            });
                            background.addItem(guiItem, slotNum, rowNum);
                        }
                    }

                    case "NEXT_PAGE" -> {
                        ItemStack itemStack;
                        if(item.hdbId() != null) {
                            itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
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

                                    quitGUI.update();
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

                                    quitGUI.update();
                                }
                            });
                            background.addItem(guiItem, slotNum, rowNum);
                        }
                    }

                    case "RETURN" -> {
                        ItemStack itemStack;
                        if(item.hdbId() != null) {
                            itemStack = HeadDatabaseUtil.getSkullItem(item.hdbId());
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

        quitGUI.addPane(background);
        quitGUI.addPane(pages);
    }

    public void updateGUI(Player player) {
        populatePages(player);
        quitGUI.getPanes().clear();
        quitGUI.addPane(background);
        quitGUI.addPane(pages);
        quitGUI.update();
    }

    public void openGUI(Player player) {
        quitGUI.show(player);
    }

    private void populatePages(Player player) {
        pages.clear();
        pages.populateWithGuiItems(getGuiItemsList(player));
    }

    private List<GuiItem> getGuiItemsList(Player player) {
        GUISettings quitConfig = GUIManager.getQuitGUIConfig();

        Settings settings = settingsManager.getSettings();
        PlayerSettings playerSettings = playerManager.getPlayerSettings(player);

        List<GuiItem> guiItems = new ArrayList<>();
        ItemStack itemStack;

        for(Map.Entry<String, Settings.Quit> entry : settings.quit().entrySet()) {
            Settings.Quit quit = entry.getValue();
            if(player.hasPermission(quit.permission())) {
                if(Objects.equals(playerSettings.selectedLeaveMessage(), quit.message())) {
                    itemStack = new ItemStack(Material.valueOf(quitConfig.placeholders().selected().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, quit.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : quitConfig.placeholders().selected().lore()) {
                        loreList.add(FormatUtil.format(player, loreStr));
                    }
                    itemMeta.lore(loreList);
                    itemStack.setItemMeta(itemMeta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                    guiItems.add(guiItem);
                } else {
                    itemStack = new ItemStack(Material.valueOf(quitConfig.placeholders().available().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, quit.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : quitConfig.placeholders().available().lore()) {
                        loreList.add(FormatUtil.format(player, loreStr));
                    }
                    itemMeta.lore(loreList);
                    itemStack.setItemMeta(itemMeta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> {
                        event.setCancelled(true);
                        playerManager.changeSelectedLeaveMessage(player, quit.message());
                        updateGUI(player);
                    });
                    guiItems.add(guiItem);
                }
            } else {
                itemStack = new ItemStack(Material.valueOf(quitConfig.placeholders().noPermission().material()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(FormatUtil.format(player, quit.message()));
                List<Component> loreList = new ArrayList<>();
                for(String loreStr : quitConfig.placeholders().noPermission().lore()) {
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