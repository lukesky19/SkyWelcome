package com.github.lukesky19.skywelcome.common.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jspecify.annotations.Nullable;

/**
 * This record contains the configuration for an individual join/leave message.
 * @param permission The join/leave message's permission.
 * @param message The actual join/leave message.
 */
@ConfigSerializable
public record MessageConfig(@Nullable String permission, @Nullable String message) {}