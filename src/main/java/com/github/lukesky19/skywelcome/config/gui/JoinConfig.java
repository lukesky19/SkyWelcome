package com.github.lukesky19.skywelcome.config.gui;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.LinkedHashMap;
import java.util.List;

@ConfigSerializable
public record JoinConfig(Placeholders placeholders, Gui gui) {

    @ConfigSerializable
    public record Placeholders(
            PlaceholderItem selected,
            PlaceholderItem available,
            PlaceholderItem noPermission) { }

    @ConfigSerializable
    public record Gui(
            Integer size,
            String name,
            PagedSettings pagedSettings,
            LinkedHashMap<Integer, LinkedHashMap<Integer, Item>> background) { }

    @ConfigSerializable
    public record PagedSettings(
            Integer xOffset,
            Integer yOffset,
            Integer length,
            Integer height) { }

    @ConfigSerializable
    public record PlaceholderItem(
            String material,
            List<String> lore) { }

    @ConfigSerializable
    public record Item(
            String type,
            String hdbId,
            String material,
            String name,
            List<String> lore) { }
}
