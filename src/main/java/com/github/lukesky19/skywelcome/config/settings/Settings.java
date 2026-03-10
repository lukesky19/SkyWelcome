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
package com.github.lukesky19.skywelcome.config.settings;

import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * This record contains the plugin's settings.
 * @param version The config version.
 * @param locale The plugin's locale to use.
 * @param globalJoinToggle Should join messages be enabled globally?
 * @param globalQuitToggle Should leave messages be enabled globally?
 * @param globalMotdToggle Should the server's motd be enabled globally?
 * @param joinMessages The {@link List} of {@link JoinMessageConfig}s for the available join messages.
 * @param motd The {@link List} of {@link String}s for the motd.
 * @param quitMessages The {@link List} of {@link QuitMessageConfig}s for the available leave messages.
 * @param welcomeRewards The {@link WelcomeRewards} config.
 */
@ConfigSerializable
public record Settings(
        int version,
        @Nullable String locale,
        @Nullable Boolean globalJoinToggle,
        @Nullable Boolean globalQuitToggle,
        @Nullable Boolean globalMotdToggle,
        @NonNull List<JoinMessageConfig> joinMessages,
        @NonNull List<String> motd,
        @NonNull List<QuitMessageConfig> quitMessages,
        @NonNull WelcomeRewards welcomeRewards) {
    /**
     * This record contains the configuration for an individual join message.
     * @param permission The join message's permission.
     * @param message The actual join message.
     */
    @ConfigSerializable
    public record JoinMessageConfig(@Nullable String permission, @Nullable String message) {}
    /**
     * This record contains the configuration for an individual leave message.
     * @param permission The leave message's permission.
     * @param message The actual leave message.
     */
    @ConfigSerializable
    public record QuitMessageConfig(@Nullable String permission, @Nullable String message) {}
    /**
     * The settings for welcome rewards.
     * @param enabled Are welcome rewards enabled?
     * @param rewardOfflineJoins Should offline new players give rewards?
     * @param cash The money to distribute to new players.
     * @param items The {@link List} of {@link ItemStackConfig}s to give as rewards.
     * @param commands The {@link List} of {@link String}s for the commands to execute in console.
     * @param messages The {@link List} of {@link String}s for the messages to send when a welcome reward is given.
     */
    @ConfigSerializable
    public record WelcomeRewards(
            @Nullable Boolean enabled,
            @Nullable Boolean rewardOfflineJoins,
            @Nullable Double cash,
            @NonNull List<ItemStackConfig> items,
            @NonNull List<String> commands,
            @NonNull List<String> messages) {}
}