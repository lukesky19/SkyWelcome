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
package com.github.lukesky19.skywelcome.config.gui;

import com.github.lukesky19.skylib.api.gui.GUIType;
import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import com.github.lukesky19.skywelcome.enums.ButtonType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This record contains the configuration for SkyWelcome's GUIs.
 * @param configVersion The config version of the file.
 * @param gui The {@link GuiData}.
 */
@ConfigSerializable
public record GUIConfig(
        @Nullable String configVersion,
        @NotNull GuiData gui) {
    /**
     * This record contains the gui data for the gui.
     * @param guiType The {@link GUIType}.
     * @param guiName The name to display in the gui.
     * @param itemsPerPage The number of items to display per page for join or quit messages.
     * @param placeholders The {@link PlaceholderButtons}.
     * @param buttons The {@link List} of {@link ButtonConfig}s.
     * @param slots The {@link List} of {@link Integer}s where to display join or quit message ItemStacks at.
     */
    @ConfigSerializable
    public record GuiData(
            @Nullable GUIType guiType,
            @Nullable String guiName,
            @Nullable Integer itemsPerPage,
            @NotNull PlaceholderButtons placeholders,
            @NotNull List<ButtonConfig> buttons,
            @NotNull List<Integer> slots) {}
    /**
     * This record contains the item configuration used to display selectable join or quit messages.
     * @param selected The {@link ItemStackConfig} used to create the {@link ItemStack} that displays the message the player has selected.
     * @param available The {@link ItemStackConfig} used to create the {@link ItemStack} that displays the messages that are available to the player.
     * @param noPermission The {@link ItemStackConfig} used to create the {@link ItemStack} that displays the messages that are not available to the player.
     */
    @ConfigSerializable
    public record PlaceholderButtons(
            @NotNull ItemStackConfig selected,
            @NotNull ItemStackConfig available,
            @NotNull ItemStackConfig noPermission) {}
    /**
     * This record contains the configuration for a single button.
     * @param buttonType The {@link ButtonType}.
     * @param slot The slot for the button.
     * @param hdbId The head database id to use.
     * @param item The {@link ItemStackConfig} for the button.
     */
    @ConfigSerializable
    public record ButtonConfig(
            @Nullable ButtonType buttonType,
            @Nullable Integer slot,
            @Nullable String hdbId,
            @NotNull ItemStackConfig item) { }
}
