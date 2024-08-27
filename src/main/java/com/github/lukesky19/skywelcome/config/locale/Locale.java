package com.github.lukesky19.skywelcome.config.locale;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public record Locale(
        String prefix,
        List<String> help,
        String playerOnly,
        String reload,
        String noPermission,
        String unknownCommand,
        String joinEnabled,
        String joinDisabled,
        String quitEnabled,
        String quitDisabled,
        String motdEnabled,
        String motdDisabled) { }
