package com.github.lukesky19.skywelcome.gui;

import com.github.lukesky19.skywelcome.config.gui.JoinConfig;
import com.github.lukesky19.skywelcome.config.gui.JoinQuitManager;
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

public class JoinGUI {
    final SettingsManager settingsManager;
    final PlayerManager playerManager;
    final JoinQuitManager joinQuitManager;

    public JoinGUI(
            SettingsManager settingsManager,
            PlayerManager playerManager,
            JoinQuitManager joinQuitManager) {
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
        this.joinQuitManager = joinQuitManager;
    }

    ChestGui joinGUI;
    StaticPane background;
    PaginatedPane pages;

    public void createGUI(Player player) {
        JoinConfig joinConfig = joinQuitManager.getJoinGUIConfig();

        joinGUI = new ChestGui(joinConfig.gui().size() / 9, ComponentHolder.of(FormatUtil.format(player, joinConfig.gui().name())));

        background = new StaticPane(0, 0, 9, 6);
        pages = new PaginatedPane(
                joinConfig.gui().pagedSettings().xOffset(),
                joinConfig.gui().pagedSettings().yOffset(),
                joinConfig.gui().pagedSettings().length(),
                joinConfig.gui().pagedSettings().height());

        for(Map.Entry<Integer, LinkedHashMap<Integer, JoinConfig.Item>> rowsEntry : joinConfig.gui().background().entrySet()) {
            int rowNum = rowsEntry.getKey();

            for(Map.Entry<Integer, JoinConfig.Item> itemEntry : rowsEntry.getValue().entrySet()) {
                Integer slotNum = itemEntry.getKey();
                JoinConfig.Item item = itemEntry.getValue();
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
        JoinConfig joinConfig = joinQuitManager.getJoinGUIConfig();

        Settings settings = settingsManager.getSettings();
        PlayerSettings playerSettings = playerManager.getPlayerSettings(player);

        List<GuiItem> guiItems = new ArrayList<>();
        ItemStack itemStack;

        for(Map.Entry<String, Settings.Join> entry : settings.join().entrySet()) {
            Settings.Join join = entry.getValue();
            if(player.hasPermission(join.permission())) {
                if(Objects.equals(playerSettings.selectedJoinMessage(), join.message())) {
                    itemStack = new ItemStack(Material.valueOf(joinConfig.placeholders().selected().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, join.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : joinConfig.placeholders().selected().lore()) {
                        loreList.add(FormatUtil.format(player, loreStr));
                    }
                    itemMeta.lore(loreList);
                    itemStack.setItemMeta(itemMeta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> event.setCancelled(true));
                    guiItems.add(guiItem);
                } else {
                    itemStack = new ItemStack(Material.valueOf(joinConfig.placeholders().available().material()));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(FormatUtil.format(player, join.message()));
                    List<Component> loreList = new ArrayList<>();
                    for(String loreStr : joinConfig.placeholders().available().lore()) {
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
                itemStack = new ItemStack(Material.valueOf(joinConfig.placeholders().noPermission().material()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(FormatUtil.format(player, join.message()));
                List<Component> loreList = new ArrayList<>();
                for(String loreStr : joinConfig.placeholders().noPermission().lore()) {
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
