package com.github.lukesky19.skywelcome.gui;

import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.FormatUtil;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuitMessageGUI {
    SettingsManager settingsManager;
    PlayerManager playerManager;

    public QuitMessageGUI(
            SettingsManager settingsManager,
            PlayerManager playerManager) {
        this.settingsManager = settingsManager;
        this.playerManager = playerManager;
    }

    ChestGui gui;

    // TODO Configurable GUI
    // TODO Add lore saying selected, change to, or no permission
    public void createGUI(Player player) {
        Settings settings = settingsManager.getSettings();
        PlayerSettings playerSettings = playerManager.getPlayerSettings(player);

        gui = new ChestGui(6, "Select a Quit Message");

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        List<GuiItem> items = new ArrayList<>();
        for(Map.Entry<String, Settings.Quit> entry : settings.quit().entrySet()) {
            Settings.Quit quit = entry.getValue();
            if(player.hasPermission(quit.permission())) {
                if(Objects.equals(playerSettings.selectedLeaveMessage(), quit.message())) {
                    ItemStack itemStack = new ItemStack(Material.GREEN_CONCRETE_POWDER);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.displayName(FormatUtil.format(player, quit.message()));
                    itemStack.setItemMeta(meta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> {
                        event.setCancelled(true);
                        // Join Message is already selected
                    });
                    items.add(guiItem);
                } else {
                    ItemStack itemStack = new ItemStack(Material.YELLOW_CONCRETE_POWDER);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.displayName(FormatUtil.format(player, quit.message()));
                    itemStack.setItemMeta(meta);
                    GuiItem guiItem = new GuiItem(itemStack, event -> {
                        event.setCancelled(true);
                        playerManager.changeSelectedLeaveMessage(player, quit.message());
                        createGUI(player);
                        openGUI(player);
                    });
                    items.add(guiItem);
                }
            } else {
                ItemStack itemStack = new ItemStack(Material.RED_CONCRETE_POWDER);
                ItemMeta meta = itemStack.getItemMeta();
                meta.displayName(FormatUtil.format(player, quit.message()));
                itemStack.setItemMeta(meta);
                GuiItem guiItem = new GuiItem(itemStack, event -> {
                    event.setCancelled(true);
                    // No Permission
                });
                items.add(guiItem);
            }
        }

        pages.populateWithGuiItems(items);

        gui.addPane(pages);

        OutlinePane background = new OutlinePane(0, 0, 9, 6);
        background.addItem(new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);

        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(new ItemStack(Material.RED_WOOL), event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            } else {
                event.setCancelled(true);
            }
        }), 0, 0);

        navigation.addItem(new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            } else {
                event.setCancelled(true);
            }
        }), 8, 0);

        navigation.addItem(new GuiItem(new ItemStack(Material.BARRIER), event ->
                event.getWhoClicked().closeInventory()), 4, 0);

        gui.addPane(navigation);
    }

    public void openGUI(Player player) {
        if(player.getOpenInventory().equals(gui.getInventory())) {
            player.closeInventory();
        }

        gui.show(player);
    }
}
