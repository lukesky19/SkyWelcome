package com.github.lukesky19.skywelcome.paper.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

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