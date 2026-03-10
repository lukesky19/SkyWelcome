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
package com.github.lukesky19.skywelcome.gui;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.gui.GUIButton;
import com.github.lukesky19.skylib.api.gui.GUIType;
import com.github.lukesky19.skylib.api.gui.impl.UUIDGUIManager;
import com.github.lukesky19.skylib.api.gui.templates.ChestGUI;
import com.github.lukesky19.skylib.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.gui.GUIConfig;
import com.github.lukesky19.skywelcome.config.gui.GUIConfigManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.data.player.PlayerData;
import com.github.lukesky19.skywelcome.manager.HeadDatabaseManager;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * This class creates a gui to allow the selection of a custom join message.
 */
public class JoinGUI extends ChestGUI<UUID> {
    // Plugin Classes
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull HeadDatabaseManager headDatabaseManager;

    // GUI Config
    private final @Nullable GUIConfig guiConfig;

    // GUI data
    private int pageNum = 0;
    private int currentMessageKey = 0;
    private int numOfMessagesAdded = 0;
    private int numOfMessagesErrored = 0;
    private final @NonNull Map<Integer, Integer> messagesAddedPerPage = new HashMap<>();
    private final @NonNull Map<Integer, Integer> messagesErroredPerPage = new HashMap<>();

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param guiManager A {@link UUIDGUIManager} instance.
     * @param player The {@link Player} this GUI is for.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param guiConfigManager A {@link GUIConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param headDatabaseManager A  {@link HeadDatabaseManager} instance.
     */
    public JoinGUI(
            @NonNull SkyWelcome skyWelcome,
            @NonNull UUIDGUIManager guiManager,
            @NonNull Player player,
            @NonNull SettingsManager settingsManager,
            @NonNull GUIConfigManager guiConfigManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull HeadDatabaseManager headDatabaseManager) {
        super(skyWelcome, guiManager, player.getUniqueId(), player);

        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
        this.headDatabaseManager = headDatabaseManager;

        this.guiConfig = guiConfigManager.getJoinGUIConfig();
    }

    /**
     * Create the {@link InventoryView} for this GUI.
     * @return true if created successfully, otherwise false.
     */
    public boolean create() {
        if(guiConfig == null) {
            logger.warn(AdventureUtil.deserialize("Unable to create the InventoryView for the join message GUI due to invalid gui configuration."));
            return false;
        }

        GUIType guiType = guiConfig.gui().guiType();
        if(guiType == null) {
            logger.warn(AdventureUtil.deserialize("Unable to create the InventoryView for the join message GUI due to an invalid GUIType."));
            return false;
        }

        String guiName = Objects.requireNonNullElse(guiConfig.gui().guiName(), "");

        return create(guiType, guiName, List.of());
    }

    /**
     * A method to create all the buttons in the inventory GUI.
     * @return true is successful, otherwise false.
     */
    @Override
    public boolean update() {
        if(guiConfig == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add buttons to the GUI as the gui configuration is invalid."));
            return false;
        }

        // If the InventoryView was not created, log a warning and return false.
        if(inventoryView == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add buttons to the GUI as the InventoryView was not created."));
            return false;
        }

        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add buttons to the GUI as the plugin's settings are invalid."));
            return false;
        }

        // If the items per page was not configured log a warning and return false.
        if(guiConfig.gui().itemsPerPage() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add buttons to the GUI as the items per page is not configured."));
            return false;
        }
        int itemsPerPage = guiConfig.gui().itemsPerPage();

        // Get the GUI size
        int guiSize = inventoryView.getTopInventory().getSize();

        // Clear the GUI of buttons
        clearButtons();

        for(GUIConfig.ButtonConfig buttonConfig : guiConfig.gui().buttons()) {
            switch(buttonConfig.buttonType()) {
                case FILLER -> createFillerButtons(buttonConfig, guiSize);

                case DUMMY -> createDummyButton(buttonConfig);

                case RETURN -> createExitButton(buttonConfig);

                case null -> logger.warn(AdventureUtil.deserialize("Unable to add a button due to an invalid button type."));

                default -> {}
            }
        }

        createMessageButtons(itemsPerPage);

        messagesAddedPerPage.put(pageNum, numOfMessagesAdded);
        messagesErroredPerPage.put(pageNum, numOfMessagesErrored);

        for(GUIConfig.ButtonConfig buttonConfig : guiConfig.gui().buttons()) {
            switch(buttonConfig.buttonType()) {
                case NEXT_PAGE -> {
                    if(numOfMessagesAdded >= itemsPerPage && currentMessageKey <= (settings.joinMessages().size() - 1)) {
                        createNextPageButton(buttonConfig);
                    }
                }

                case PREV_PAGE -> {
                    if(pageNum > 0) {
                        createPreviousPageButton(buttonConfig);
                    }
                }

                case null -> logger.warn(AdventureUtil.deserialize("Unable to add a button due to an invalid button type."));

                default -> {}
            }
        }

        return super.update();
    }

    /**
     * Refreshes the current buttons displayed.
     * @return @return true is successful, otherwise false.
     */
    @Override
    public boolean refresh() {
        int previewsErroredCurrentPage = messagesErroredPerPage.get(pageNum);
        int previewsAddedCurrentPage = messagesAddedPerPage.get(pageNum);

        messagesErroredPerPage.remove(pageNum);
        messagesAddedPerPage.remove(pageNum);

        currentMessageKey = currentMessageKey - ((previewsErroredCurrentPage + previewsAddedCurrentPage));

        numOfMessagesAdded = 0;
        numOfMessagesErrored = 0;

        return super.refresh();
    }

    /**
     * Handles when the GUI is closed by the player.
     * @param inventoryCloseEvent An {@link InventoryCloseEvent}
     */
    @Override
    public void handleClose(@NonNull InventoryCloseEvent inventoryCloseEvent) {
        if(inventoryCloseEvent.getReason().equals(InventoryCloseEvent.Reason.UNLOADED) || inventoryCloseEvent.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;

        guiManager.removeOpenGUI(uuid);
    }

    /**
     * This method does nothing.
     * @param inventoryDragEvent An {@link InventoryDragEvent}
     */
    @Override
    public void handleBottomDrag(@NonNull InventoryDragEvent inventoryDragEvent) {}

    /**
     * This method does nothing.
     * @param inventoryDragEvent An {@link InventoryDragEvent}
     */
    @Override
    public void handleGlobalDrag(@NonNull InventoryDragEvent inventoryDragEvent) {}

    /**
     * This method does nothing.
     * @param inventoryClickEvent An {@link InventoryClickEvent}
     */
    @Override
    public void handleBottomClick(@NonNull InventoryClickEvent inventoryClickEvent) {}

    /**
     * This method does nothing.
     * @param inventoryClickEvent An {@link InventoryClickEvent}
     */
    @Override
    public void handleGlobalClick(@NonNull InventoryClickEvent inventoryClickEvent) {}

    /**
     * Create and add the filler buttons.
     * @param buttonConfig The {@link GUIConfig.ButtonConfig} to use.
     * @param guiSize The size of the Inventory/GUI.
     */
    private void createFillerButtons(GUIConfig.ButtonConfig buttonConfig, int guiSize) {
        // Create the ItemStackBuilder and pass the ItemStackConfig.
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);
        itemStackBuilder.fromItemStackConfig(buttonConfig.item(), player, List.of());

        // If an ItemStack was created, create the GUIButton and add it to the GUI.
        Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
        optionalItemStack.ifPresent(itemStack -> {
            GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
            guiButtonBuilder.setItemStack(itemStack);

            GUIButton fillerButton = guiButtonBuilder.build();

            for (int i = 0; i <= (guiSize - 1); i++) {
                setButton(i, fillerButton);
            }
        });
    }

    /**
     * Create and add the dummy button's ItemStack. This is similar to filler buttons.
     * @param buttonConfig The {@link GUIConfig.ButtonConfig} to use.
     */
    private void createDummyButton(GUIConfig.ButtonConfig buttonConfig) {
        // Check if the slot is not configured and send a warning.
        if(buttonConfig.slot() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add a dummy button due to a slot not being configured."));
            return;
        }

        // Create the ItemStackBuilder and pass the ItemStackConfig.
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);

        if(buttonConfig.hdbId() != null) {
            ItemStack baseItemStack = headDatabaseManager.getSkullItem(buttonConfig.hdbId());
            if(baseItemStack != null) {
                itemStackBuilder.setBaseItemStack(baseItemStack);
            }
        }

        itemStackBuilder.fromItemStackConfig(buttonConfig.item(), player, List.of());

        // If an ItemStack was created, create the GUIButton and add it to the GUI.
        Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
        optionalItemStack.ifPresent(itemStack -> {
            GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
            guiButtonBuilder.setItemStack(itemStack);

            setButton(buttonConfig.slot(), guiButtonBuilder.build());
        });
    }

    /**
     * Create the items displayed for the join messages.
     * @param itemsPerPage The number of items to display per page.
     */
    private void createMessageButtons(int itemsPerPage) {
        assert guiConfig != null;
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) return;
        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) return;
        List<Integer> slots = new ArrayList<>(guiConfig.gui().slots());

        while(numOfMessagesAdded < itemsPerPage) {
            if(currentMessageKey >= settings.joinMessages().size() || slots.isEmpty()) return;

            Settings.JoinMessageConfig joinMessageConfig = settings.joinMessages().get(currentMessageKey);
            if(joinMessageConfig.permission() == null) {
                logger.warn(AdventureUtil.deserialize("Unable to add join message to the gui due to an invalid permission."));
                handleMessageError();
                continue;
            }

            if(joinMessageConfig.message() == null) {
                logger.warn(AdventureUtil.deserialize("Unable to add join message to the gui due to an invalid join message."));
                handleMessageError();
                continue;
            }

            if(player.hasPermission(joinMessageConfig.permission())) {
                ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);
                if(playerData.getJoinMessage().equals(joinMessageConfig.message())) {
                    itemStackBuilder.fromItemStackConfig(guiConfig.gui().placeholders().selected(), player, List.of());
                    itemStackBuilder.setName(AdventureUtil.deserialize(player, joinMessageConfig.message()));

                    Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
                    optionalItemStack.ifPresentOrElse(itemStack -> {
                        GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
                        guiButtonBuilder.setItemStack(itemStack);

                        int slot = slots.removeFirst();
                        setButton(slot, guiButtonBuilder.build());

                        currentMessageKey++;
                        numOfMessagesAdded++;
                    }, this::handleMessageError);
                } else {
                    itemStackBuilder.fromItemStackConfig(guiConfig.gui().placeholders().available(), player, List.of());
                    itemStackBuilder.setName(AdventureUtil.deserialize(player, joinMessageConfig.message()));

                    Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
                    optionalItemStack.ifPresentOrElse(itemStack -> {
                        GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
                        guiButtonBuilder.setItemStack(itemStack);
                        guiButtonBuilder.setAction(inventoryClickEvent -> {
                            playerData.setJoinMessage(joinMessageConfig.message());

                            playerDataManager.savePlayerData(uuid, playerData);

                            refresh();
                        });

                        int slot = slots.removeFirst();
                        setButton(slot, guiButtonBuilder.build());

                        currentMessageKey++;
                        numOfMessagesAdded++;
                    }, this::handleMessageError);
                }
            } else {
                ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);
                itemStackBuilder.fromItemStackConfig(guiConfig.gui().placeholders().noPermission(), player, List.of());
                itemStackBuilder.setName(AdventureUtil.deserialize(player, joinMessageConfig.message()));

                Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
                optionalItemStack.ifPresentOrElse(itemStack -> {
                    GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
                    guiButtonBuilder.setItemStack(itemStack);

                    int slot = slots.removeFirst();
                    setButton(slot, guiButtonBuilder.build());

                    currentMessageKey++;
                    numOfMessagesAdded++;
                }, this::handleMessageError);
            }
        }
    }

    /**
     * Create the button to go to the previous page.
     */
    private void createPreviousPageButton(GUIConfig.ButtonConfig buttonConfig) {
        // Check if the slot is not configured and send a warning.
        if(buttonConfig.slot() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add a previous page button due to a slot not being configured."));
            return;
        }

        // Create the ItemStackBuilder and pass the ItemStackConfig.
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);

        if(buttonConfig.hdbId() != null) {
            ItemStack baseItemStack = headDatabaseManager.getSkullItem(buttonConfig.hdbId());
            if(baseItemStack != null) {
                itemStackBuilder.setBaseItemStack(baseItemStack);
            }
        }

        itemStackBuilder.fromItemStackConfig(buttonConfig.item(), player,List.of());

        // If an ItemStack was created, create the GUIButton and add it to the GUI.
        Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
        optionalItemStack.ifPresent(itemStack -> {
            GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
            guiButtonBuilder.setItemStack(itemStack);
            guiButtonBuilder.setAction(event -> {
                int messagesErroredCurrentPage = messagesErroredPerPage.get(pageNum);
                int messagesAddedCurrentPage = messagesAddedPerPage.get(pageNum);
                int messagesErroredPrevPage = messagesErroredPerPage.get(pageNum - 1);
                int messagesAddedPrevPage = messagesAddedPerPage.get(pageNum - 1);

                currentMessageKey = currentMessageKey - ((messagesErroredCurrentPage + messagesAddedCurrentPage) + (messagesErroredPrevPage + messagesAddedPrevPage));

                numOfMessagesAdded = 0;
                numOfMessagesErrored = 0;
                pageNum--;

                this.update();
            });

            setButton(buttonConfig.slot(), guiButtonBuilder.build());
        });
    }

    /**
     * Create the button to go to the next page.
     */
    private void createNextPageButton(GUIConfig.ButtonConfig buttonConfig) {
        // Check if the slot is not configured and send a warning.
        if(buttonConfig.slot() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add a previous page button due to a slot not being configured."));
            return;
        }

        // Create the ItemStackBuilder and pass the ItemStackConfig.
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);

        if(buttonConfig.hdbId() != null) {
            ItemStack baseItemStack = headDatabaseManager.getSkullItem(buttonConfig.hdbId());
            if(baseItemStack != null) {
                itemStackBuilder.setBaseItemStack(baseItemStack);
            }
        }

        itemStackBuilder.fromItemStackConfig(buttonConfig.item(), player, List.of());

        // If an ItemStack was created, create the GUIButton and add it to the GUI.
        Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
        optionalItemStack.ifPresent(itemStack -> {
            GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
            guiButtonBuilder.setItemStack(itemStack);
            guiButtonBuilder.setAction(event -> {
                numOfMessagesAdded = 0;
                numOfMessagesErrored = 0;
                pageNum++;

                update();
            });

            setButton(buttonConfig.slot(), guiButtonBuilder.build());
        });
    }

    /**
     * Create the button to exit the GUI.
     */
    private void createExitButton(GUIConfig.ButtonConfig buttonConfig) {
        // Check if the slot is not configured and send a warning.
        if(buttonConfig.slot() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to add a exit button due to a slot not being configured."));
            return;
        }

        // Create the ItemStackBuilder and pass the ItemStackConfig.
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(logger);

        if(buttonConfig.hdbId() != null) {
            ItemStack baseItemStack = headDatabaseManager.getSkullItem(buttonConfig.hdbId());
            if(baseItemStack != null) {
                itemStackBuilder.setBaseItemStack(baseItemStack);
            }
        }

        itemStackBuilder.fromItemStackConfig(buttonConfig.item(), player, List.of());

        // If an ItemStack was created, create the GUIButton and add it to the GUI.
        Optional<ItemStack> optionalItemStack = itemStackBuilder.buildItemStack();
        optionalItemStack.ifPresent(itemStack -> {
            GUIButton.Builder guiButtonBuilder = new GUIButton.Builder();
            guiButtonBuilder.setItemStack(itemStack);
            guiButtonBuilder.setAction(event -> close());

            setButton(buttonConfig.slot(), guiButtonBuilder.build());
        });
    }

    /**
     * Handle when a join message button cannot be shown due to an error.
     */
    private void handleMessageError() {
        currentMessageKey++;
        numOfMessagesErrored++;
    }
}