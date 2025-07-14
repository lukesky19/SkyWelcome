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
package com.github.lukesky19.skywelcome.config.settings.legacy;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This record contains the settings configuration for version 1.1.0 through 1.3.0.
 * @param configVersion The config version of the settings.
 * @param options The {@link Options} config.
 * @param join The map of join messages.
 * @param motd The {@link Motd} config.
 * @param quit The map of leave messages.
 * @param welcomeRewards The {@link WelcomeRewards} config.
 */
@ConfigSerializable
public record SettingsV110ToV130(
        @Nullable String configVersion,
        @NotNull Options options,
        @NotNull LinkedHashMap<String, Join> join,
        @NotNull Motd motd,
        @NotNull LinkedHashMap<String, Quit> quit,
        @NotNull WelcomeRewards welcomeRewards) {
    /**
     * This record contains the configuration for an individual join message.
     * @param permission The join message's permission.
     * @param message The actual join message.
     */
    @ConfigSerializable
    public record Join(@Nullable String permission, @Nullable String message) { }
    /**
     * This record contains the {@link List} of {@link String}s to send for the server's motd.
     * @param contents The {@link List} of {@link String}s to send for the server's motd.
     */
    @ConfigSerializable
    public record Motd(@NotNull List<String> contents) { }
    /**
     * This record contains the configuration for an individual leave message.
     * @param permission The leave message's permission.
     * @param message The actual leave message.
     */
    @ConfigSerializable
    public record Quit(@Nullable String permission, @Nullable String message) { }
    /**
     * This record contains general plugin options.
     * @param locale The plugin's locale to use.
     * @param joins Should join messages be enabled globally?
     * @param quits Should leave messages be enabled globally?
     * @param motd Should the server's motd be enabled globally?
     */
    @ConfigSerializable
    public record Options(String locale, Boolean joins, Boolean quits, Boolean motd) {}
    /**
     * The settings for welcome rewards.
     * @param enabled Are welcome rewards enabled?
     * @param rewardOfflineJoins Should offline new players give rewards?
     * @param type The reward type.
     * @param cash The money to distribute to new players.
     * @param item The {@link List} of {@link Item}s to give as rewards.
     * @param commands The {@link List} of {@link String}s for the commands to execute in console.
     * @param messages The {@link List} of {@link String}s for the messages to send when a welcome reward is given.
     */
    @ConfigSerializable
    public record WelcomeRewards(Boolean enabled, Boolean rewardOfflineJoins, String type, Double cash, Item item, List<String> commands, List<String> messages) {}
    /**
     * The configuration to create an {@link ItemStack}.
     * @param material The {@link Material}'s name.
     * @param amount The amount of items.
     */
    @ConfigSerializable
    public record Item(String material, Integer amount) {}
}
